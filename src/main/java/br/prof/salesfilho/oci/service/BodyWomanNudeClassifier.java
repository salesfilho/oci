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

    @Getter
    @Setter
    private int classificationLevel = 1;

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
                executor = Executors.newFixedThreadPool(3);
                for (BufferedImage subImg : partImageList) {

                    chestWorker = new EuclidianClassifier(nudeChestDescriptor, notNudeChestDescriptor, subImg, this.kernelSize);
                    chestWorker.setClassificationLevel(this.classificationLevel);
                    executor.execute(chestWorker);
                    classifiers.add(chestWorker);

                    buttockWorker = new EuclidianClassifier(nudeButtockDescriptor, notNudeButtockDescriptor, subImg, this.kernelSize);
                    buttockWorker.setClassificationLevel(this.classificationLevel);
                    executor.execute(buttockWorker);
                    classifiers.add(buttockWorker);

                    genitalWorker = new EuclidianClassifier(nudeGenitalDescriptor, notNudeGenitalDescriptor, subImg, this.kernelSize);
                    genitalWorker.setClassificationLevel(this.classificationLevel);
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
        double nudeChestCount = 0;
        double nudeButtockCount = 0;
        double nudeGenitalCount = 0;

        double notNudeChestCount = 0;
        double notNudeButtockCount = 0;
        double notNudeGenitalCount = 0;

        double maybeNudeChestCount = 0;
        double maybeNudeButtockCount = 0;
        double maybeNudeGenitalCount = 0;

        //Comput classificarion for eath part
        for (EuclidianClassifier classification : classifiers) {
            if (classification.getNudeBodyPartDescriptor().getName().equalsIgnoreCase("chest")) {
                nudeChestCount += (classification.getClassification().equalsIgnoreCase("YES") ? 1 : 0);
                notNudeChestCount += (classification.getClassification().equalsIgnoreCase("NO") ? 1 : 0);
                maybeNudeChestCount += (classification.getClassification().equalsIgnoreCase("MAYBE") ? 1 : 0);

            } else if (classification.getNudeBodyPartDescriptor().getName().equalsIgnoreCase("buttock")) {
                nudeButtockCount += (classification.getClassification().equalsIgnoreCase("YES") ? 1 : 0);
                notNudeButtockCount += (classification.getClassification().equalsIgnoreCase("NO") ? 1 : 0);
                maybeNudeButtockCount += (classification.getClassification().equalsIgnoreCase("MAYBE") ? 1 : 0);

            } else if (classification.getNudeBodyPartDescriptor().getName().equalsIgnoreCase("genital")) {
                nudeGenitalCount += (classification.getClassification().equalsIgnoreCase("YES") ? 1 : 0);
                notNudeGenitalCount += (classification.getClassification().equalsIgnoreCase("NO") ? 1 : 0);
                maybeNudeGenitalCount += (classification.getClassification().equalsIgnoreCase("MAYBE") ? 1 : 0);
            }
        }

        double nudeAvg = ((nudeChestCount * 1) + (nudeButtockCount * 2) + (nudeGenitalCount * 3)) / 6;
        double notNudeAvg = ((notNudeChestCount * 1) + (notNudeButtockCount * 2) + (notNudeGenitalCount * 3)) / 6;
        double maybeAvg = ((maybeNudeChestCount * 1) + (maybeNudeButtockCount * 2) + (maybeNudeGenitalCount * 3)) / 6;

        String result;

        if ((nudeAvg > notNudeAvg) && (nudeAvg > maybeAvg) ) {
            result = "Higth probability";
        } else if ( (notNudeAvg > nudeAvg) && (notNudeAvg > maybeAvg)) {
            result = "Very low probability";
        } else if ( (maybeAvg > nudeAvg) && (maybeAvg > notNudeAvg)) {
            result = "Medium probability";
        } else {
            result = "Low probability";
        }
        // Result
        System.out.println("------------------- Parameters -------------------------");
        System.out.println("Database...............: " + getDatabaseName());
        System.out.println("Kernel size............: " + getKernelSize());
        System.out.println("Classification Level...: " + getClassificationLevel());
        System.out.println(" ");
        System.out.println("------------------- Matchs results ---------------------");
        
        System.out.println("***** CLASS NUDE ***** ");
        System.out.println("Chest..................: " + nudeChestCount);
        System.out.println("Buttock................: " + nudeButtockCount);
        System.out.println("Genital................: " + nudeGenitalCount);
        
        System.out.println("*** CLASS MAYBE NUDE *** ");
        System.out.println("Chest..................: " + maybeNudeChestCount);
        System.out.println("Buttock................: " + maybeNudeButtockCount);
        System.out.println("Genital................: " + maybeNudeGenitalCount);
        
        System.out.println("*** CLASS NOT NUDE *** ");
        System.out.println("Chest..................: " + notNudeChestCount);
        System.out.println("Buttock................: " + notNudeButtockCount);
        System.out.println("Genital................: " + notNudeGenitalCount);
        
        System.out.println("*** SCORE BOARD *** ");
        System.out.println("Yes score..............: " + nudeAvg);
        System.out.println("Maybe score............: " + maybeAvg);
        System.out.println("No score...............: " + notNudeAvg);
        System.out.println("--------------------------------------------------------");
        System.out.println("Nude classification....: " + result);
        System.out.println("--------------------------------------------------------");

    }
}
