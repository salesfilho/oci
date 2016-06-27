/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.prof.salesfilho.oci.service;

import br.prof.salesfilho.oci.domain.BodyPartDescriptor;
import br.prof.salesfilho.oci.domain.BodyWomanDescriptor;
import br.prof.salesfilho.oci.domain.ImageDescriptor;
import br.prof.salesfilho.oci.util.OCIUtils;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.math3.ml.distance.EuclideanDistance;
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
        bodyWomanDescriptorService.openDatabase(new File(this.databaseName));

        fileList = OCIUtils.getImageFiles(this.inputDir);

        System.out.println("******************** CLASSIFICATION SUMMARY ********************");
        for (String imagePath : fileList) {
            try {
                System.out.println("Classifying: ".concat(imagePath));
                BufferedImage img = ImageIO.read(new File(imagePath));
                classify(img, 64, kernelSize);
            } catch (IOException ex) {
                Logger.getLogger(BodyWomanNudeClassifier.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        System.out.println("****************************************************************");
    }

    public boolean classify(BufferedImage image, int subImageSize, double kernelSize) {
        double faceMatch = 0;
        double chestMatch = 0;
        double umbilicusMatch = 0;
        double buttockMatch = 0;
        double genitalMatch = 0;
        double totalMatch = 0;

        Map<String, double[]> imgInputFeaures;

        imageProcessorService.setImage(image);
        List<BufferedImage> partImageList = imageProcessorService.getSubImages(subImageSize);
        for (BufferedImage img : partImageList) {
            imgInputFeaures = imageProcessorService.getImageFeaturesMap(img, kernelSize);

            if (hasMatch("face", imgInputFeaures)) {
                faceMatch++;
            }
            if (hasMatch("chest", imgInputFeaures)) {
                chestMatch++;
            }
            if (hasMatch("umbilicus", imgInputFeaures)) {
                umbilicusMatch++;
            }
            if (hasMatch("buttock", imgInputFeaures)) {
                buttockMatch++;
            }
            if (hasMatch("genital", imgInputFeaures)) {
                genitalMatch++;
            }

        }
        totalMatch = chestMatch + umbilicusMatch + (buttockMatch*2) + (genitalMatch*3);
        
        System.out.println("------------------- Matchs results ---------------------");
        System.out.println("Faces....: " + faceMatch);
        System.out.println("Chest....: " + chestMatch);
        System.out.println("Umbilicus: " + umbilicusMatch);
        System.out.println("Buttock..: " + buttockMatch);
        System.out.println("Genital..: " + genitalMatch);
        System.out.println("Total..: " + totalMatch);
        System.out.println("-----------------------------------");

        return totalMatch >= 3;
    }

    private boolean hasMatch(String name, Map<String, double[]> imgInputFeaures) {

        EuclideanDistance euclideanDistance = new EuclideanDistance();
        //double[] redChanellDescriptor = imgInputFeaures.get("red");
        //double[] greenChanellDescriptor = imgInputFeaures.get("green");
        //double[] blueChanellDescriptor = imgInputFeaures.get("blue");
        //double[] grayChanellDescriptor = imgInputFeaures.get("gray");
        double[] avgChanellDescriptor = imgInputFeaures.get("avg");

        BodyPartDescriptor nudeBodyPartDescriptor = bodyWomanDescriptorService.findNudeBodyPartDescriptorByName(name);
        BodyPartDescriptor notNudeBodyPartDescriptor = bodyWomanDescriptorService.findNotNudeBodyPartDescriptorByName(name);

        System.out.println("-----------------------------------");
        System.out.println(name.concat(" analyze..."));
        System.out.println("-----------------------------------");

        double nudeDistance = euclideanDistance.compute(avgChanellDescriptor, nudeBodyPartDescriptor.getAvgChannel());
        double notNudeDistance = euclideanDistance.compute(avgChanellDescriptor, notNudeBodyPartDescriptor.getAvgChannel());

        return (nudeDistance <= notNudeDistance);

    }

}
