/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.prof.salesfilho.oci.service;

import br.prof.salesfilho.oci.domain.BodyPartDescriptor;
import br.prof.salesfilho.oci.util.OCIUtils;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author salesfilho
 */
@Component
@NoArgsConstructor
public class BodyWomanNudeClassifier {

    @Autowired
    private BodyWomanDescriptorService bodyWomanDescriptorService;

    @Autowired
    private ImageProcessorService imageProcessorService;

    @Getter
    @Setter
    private String databaseName;

    @Getter
    @Setter
    private String inputDir;

    @Getter
    @Setter
    private double kernelSize;

    @Getter
    @Setter
    private List<String> fileList;

    public void start() {
        tClassify();
    }

    public void tClassify() {

        long startTime;
        long endTime;
        double totalTime = 0;

        EuclidianClassifier chestWorker;
        EuclidianClassifier buttockWorker;
        EuclidianClassifier genitalWorker;

        bodyWomanDescriptorService.openDatabase(new File(this.databaseName));

        BodyPartDescriptor nudeChestDescriptor = bodyWomanDescriptorService.findNudeBodyPartDescriptorByName("Chest");
        BodyPartDescriptor notNudeChestDescriptor = bodyWomanDescriptorService.findNotNudeBodyPartDescriptorByName("Chest");

        BodyPartDescriptor nudeButtockDescriptor = bodyWomanDescriptorService.findNudeBodyPartDescriptorByName("Buttock");
        BodyPartDescriptor notNudeButtockDescriptor = bodyWomanDescriptorService.findNotNudeBodyPartDescriptorByName("Buttock");

        BodyPartDescriptor nudeGenitalDescriptor = bodyWomanDescriptorService.findNudeBodyPartDescriptorByName("Genital");
        BodyPartDescriptor notNudeGenitalDescriptor = bodyWomanDescriptorService.findNotNudeBodyPartDescriptorByName("Genital");

        fileList = OCIUtils.getImageFiles(this.inputDir);
        ExecutorService executor;

        List<EuclidianClassifier> classifiers = new ArrayList();

        for (String imagePath : fileList) {
            startTime = System.currentTimeMillis();

            try {
                System.out.println("Classifying image: ".concat(imagePath));
                BufferedImage img = ImageIO.read(new File(imagePath));
                imageProcessorService.setImage(img);
                List<BufferedImage> partImageList = imageProcessorService.getSubImages(128);

                //Create new thread pool to each image file
                executor = Executors.newFixedThreadPool(5);
                for (BufferedImage subImg : partImageList) {

                    chestWorker = new EuclidianClassifier(nudeChestDescriptor, notNudeChestDescriptor, subImg, this.kernelSize);
                    chestWorker.setClassificationLevel(1);
                    executor.execute(chestWorker);
                    classifiers.add(chestWorker);

                    buttockWorker = new EuclidianClassifier(nudeButtockDescriptor, notNudeButtockDescriptor, subImg, this.kernelSize);
                    buttockWorker.setClassificationLevel(1);
                    executor.execute(buttockWorker);
                    classifiers.add(buttockWorker);

                    genitalWorker = new EuclidianClassifier(nudeGenitalDescriptor, notNudeGenitalDescriptor, subImg, this.kernelSize);
                    genitalWorker.setClassificationLevel(1);
                    executor.execute(genitalWorker);
                    classifiers.add(genitalWorker);
                }
                //Wait finish
                executor.shutdown();
                while (!executor.isTerminated()) {
                }

                printResult(classifiers);

                classifiers.clear();

                endTime = System.currentTimeMillis();
                totalTime += (endTime - startTime);

                System.out.println("--------------------------------------------------------");
                System.out.println("Process time.: " + (double) ((endTime - startTime) / 1000) + " secunds.");
                System.out.println("--------------------------------------------------------");

            } catch (IOException ex) {
                Logger.getLogger(BodyWomanNudeClassifier.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        System.out.println("--------------------------------------------------------");
        System.out.println("Total time.: " + ((totalTime) / 1000) + " secunds.");
        System.out.println("--------------------------------------------------------");

    }

    public void printResult(List<EuclidianClassifier> classifiers) {
        double chestCount = 0;
        double buttockCount = 0;
        double genitalCount = 0;

        //Comput classificarion for eath part
        for (EuclidianClassifier classification : classifiers) {
            if (classification.getNudeBodyPartDescriptor().getName().equalsIgnoreCase("chest")) {
                chestCount += (classification.isNude() ? 1 : 0);
            } else if (classification.getNudeBodyPartDescriptor().getName().equalsIgnoreCase("buttock")) {
                buttockCount += (classification.isNude() ? 1 : 0);

            } else if (classification.getNudeBodyPartDescriptor().getName().equalsIgnoreCase("genital")) {
                genitalCount += (classification.isNude() ? 1 : 0);
            }
        }

        double nudeAvg = ((chestCount * 1) + (buttockCount * 2) + (genitalCount * 3)) / 6;
        double notNudeAvg = (((4 - chestCount) * 1) + ((4 - buttockCount) * 2) + ((4 - genitalCount) * 3)) / 6;

        String result;

        if (nudeAvg > notNudeAvg) {
            result = "YES";
        } else if (nudeAvg < notNudeAvg) {
            result = "NO";
        } else {
            result = "MAYBE";
        }
        // Result
        System.out.println("------------------- Matchs results ---------------------");
        System.out.println("Chest..................: " + chestCount);
        System.out.println("Buttock................: " + buttockCount);
        System.out.println("Genital................: " + genitalCount);
        System.out.println("Nude score.............: " + nudeAvg);
        System.out.println("Not nude score.........: " + notNudeAvg);
        System.out.println("--------------------------------------------------------");
        System.out.println("It's woman nude image..: " + result);
        System.out.println("--------------------------------------------------------");

    }
}
