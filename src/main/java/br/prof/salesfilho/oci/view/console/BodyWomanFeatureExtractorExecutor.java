/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.prof.salesfilho.oci.view.console;

import br.prof.salesfilho.oci.domain.BodyPartDescriptor;
import br.prof.salesfilho.oci.domain.BodyWomanDescriptor;
import br.prof.salesfilho.oci.service.BodyWomanDescriptorService;
import br.prof.salesfilho.oci.service.ImageProcessorService;
import br.prof.salesfilho.oci.util.OCIUtils;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author salesfilho
 */
public class BodyWomanFeatureExtractorExecutor implements Runnable {

    private final BodyWomanDescriptorService bodyWomanDescriptorService;

    private final ImageProcessorService imageProcessorService;

    @Getter
    @Setter
    private List<String> fileList;

    @Getter
    @Setter
    private String inputDir;

    @Getter
    @Setter
    private String outputDir;

    @Getter
    @Setter
    private String databaseName;

    @Getter
    @Setter
    private double kernelSize;

    @Getter
    @Setter
    private boolean nude;

    private static final double DEFAULT_KERNEL_SIZE = 0.15;
    private static final String DEFAULT_FEATURE_IMAGE_SIZE = "128x128";

    public BodyWomanFeatureExtractorExecutor(BodyWomanDescriptorService bodyWomanDescriptorService, ImageProcessorService imageProcessorService, boolean nude) {
        this.bodyWomanDescriptorService = bodyWomanDescriptorService;
        this.imageProcessorService = imageProcessorService;
        this.nude = nude;
    }

    private void start() {

        fileList = OCIUtils.getImageFiles(this.inputDir);
        if (databaseName == null || databaseName.isEmpty()) {
            this.databaseName = this.outputDir + "/" + new SimpleDateFormat("yyyy-MM-dd-HH-mm").format(new Date()) + ".xml";
        } else {
            this.databaseName = this.outputDir + "/" + this.databaseName;
        }
        if (kernelSize <= 0 || kernelSize > 10) {
            this.kernelSize = DEFAULT_KERNEL_SIZE;
        }
        if (isNude()) {
            this.extractNudeFeatures();
        } else {
            this.extractNotNudeFeatures();
        }
    }

    private BodyWomanDescriptor extract(String descriptorName, boolean isNude) {
        String facePath;
        String buttockPath;
        String chestPath;
        String genitalPath;
        String umbilicusPath;

        long startPartTime;
        long endPartTime;

        if (isNude) {
            facePath = this.inputDir.concat("/nude/face/".concat(DEFAULT_FEATURE_IMAGE_SIZE));
            buttockPath = this.inputDir.concat("/nude/buttock/".concat(DEFAULT_FEATURE_IMAGE_SIZE));
            chestPath = this.inputDir.concat("/nude/chest/".concat(DEFAULT_FEATURE_IMAGE_SIZE));
            genitalPath = this.inputDir.concat("/nude/genital/".concat(DEFAULT_FEATURE_IMAGE_SIZE));
            umbilicusPath = this.inputDir.concat("/nude/umbilicus/".concat(DEFAULT_FEATURE_IMAGE_SIZE));

        } else {
            facePath = this.inputDir.concat("/not_nude/face/".concat(DEFAULT_FEATURE_IMAGE_SIZE));
            buttockPath = this.inputDir.concat("/not_nude/buttock/".concat(DEFAULT_FEATURE_IMAGE_SIZE));
            chestPath = this.inputDir.concat("/not_nude/chest/".concat(DEFAULT_FEATURE_IMAGE_SIZE));
            genitalPath = this.inputDir.concat("/not_nude/genital/".concat(DEFAULT_FEATURE_IMAGE_SIZE));
            umbilicusPath = this.inputDir.concat("/not_nude/umbilicus/".concat(DEFAULT_FEATURE_IMAGE_SIZE));

        }

        System.out.println("------------------------------------------------------------------------------------");
        System.out.println("Extracting feaure to " + (isNude == true ? "Nude" : "Not nude") + " images.");
        System.out.println("------------------------------------------------------------------------------------");

        BodyWomanDescriptor womanDescriptor = new BodyWomanDescriptor(descriptorName, isNude);

        /*Add face features*/
        startPartTime = System.currentTimeMillis();

        BodyPartDescriptor partDescriptor = new BodyPartDescriptor("Face", isNude);
        partDescriptor = populate(partDescriptor, facePath);
        womanDescriptor.addBodyPart(partDescriptor);

        endPartTime = System.currentTimeMillis();

        System.out.println("------------------------------------------------------------------------------------");
        System.out.println("Time to " + partDescriptor.getName() + " :" + (endPartTime - startPartTime) + " ms");
        System.out.println("------------------------------------------------------------------------------------");

        /*Add Buttock features*/
        startPartTime = System.currentTimeMillis();

        partDescriptor = new BodyPartDescriptor("Buttock", isNude);
        partDescriptor = populate(partDescriptor, buttockPath);
        womanDescriptor.addBodyPart(partDescriptor);

        endPartTime = System.currentTimeMillis();

        System.out.println("------------------------------------------------------------------------------------");
        System.out.println("Time to " + partDescriptor.getName() + " :" + (endPartTime - startPartTime) + " ms");
        System.out.println("------------------------------------------------------------------------------------");


        /*Add chest features*/
        startPartTime = System.currentTimeMillis();

        partDescriptor = new BodyPartDescriptor("Chest", isNude);
        partDescriptor = populate(partDescriptor, chestPath);
        womanDescriptor.addBodyPart(partDescriptor);

        endPartTime = System.currentTimeMillis();

        System.out.println("------------------------------------------------------------------------------------");
        System.out.println("Time to " + partDescriptor.getName() + " :" + (endPartTime - startPartTime) + " ms");
        System.out.println("------------------------------------------------------------------------------------");


        /*Add genital features*/
        startPartTime = System.currentTimeMillis();

        partDescriptor = new BodyPartDescriptor("Genital", isNude);
        partDescriptor = populate(partDescriptor, genitalPath);
        womanDescriptor.addBodyPart(partDescriptor);

        endPartTime = System.currentTimeMillis();

        System.out.println("------------------------------------------------------------------------------------");
        System.out.println("Time to " + partDescriptor.getName() + " :" + (endPartTime - startPartTime) + " ms");
        System.out.println("------------------------------------------------------------------------------------");

        /*Add umbilicus features*/
        startPartTime = System.currentTimeMillis();

        partDescriptor = new BodyPartDescriptor("Umbilicus", isNude);
        partDescriptor = populate(partDescriptor, umbilicusPath);
        womanDescriptor.addBodyPart(partDescriptor);

        endPartTime = System.currentTimeMillis();

        System.out.println("------------------------------------------------------------------------------------");
        System.out.println("Time to " + partDescriptor.getName() + " :" + (endPartTime - startPartTime) + " ms");
        System.out.println("------------------------------------------------------------------------------------");

        return womanDescriptor;

    }

    public void extractNudeFeatures() {

        long startTime = System.currentTimeMillis();

        File f = new File( this.databaseName + "_nude.xml");
        bodyWomanDescriptorService.openDatabase(f);
        bodyWomanDescriptorService.add(extract("Nude body descriptor", true));
        bodyWomanDescriptorService.save(f, true);

        long endTime = System.currentTimeMillis();

        System.out.println("*************************************************************************************");
        System.out.println("Total process time: " + (endTime - startTime) + " ms");
        System.out.println("*************************************************************************************");
    }

    public void extractNotNudeFeatures() {

        long startTime = System.currentTimeMillis();
        File f = new File( this.databaseName + "_not_nude.xml");

        bodyWomanDescriptorService.openDatabase(f);
        bodyWomanDescriptorService.add(extract("Not Nude body descriptor", false));
        bodyWomanDescriptorService.save(f, false);

        long endTime = System.currentTimeMillis();

        System.out.println("*************************************************************************************");
        System.out.println("Total process time: " + (endTime - startTime) + " ms");
        System.out.println("*************************************************************************************");
    }

    private BodyPartDescriptor populate(BodyPartDescriptor part, String path) {

        List<double[]> listRedChannelFeatures = new ArrayList<>();
        List<double[]> listGreenChannelFeatures = new ArrayList<>();
        List<double[]> listBlueChannelFeatures = new ArrayList<>();
        List<double[]> listGrayScaleChannelFeatures = new ArrayList<>();
        List<double[]> listRgbAvgFeatures = new ArrayList<>();

        Map<String, double[]> imageFeaturesMap;

        fileList = OCIUtils.getImageFiles(path);

        for (String imagePath : fileList) {
            try {
                System.out.println("Extracting features from file: " + imagePath + " --> to part: " + part.getName());
                BufferedImage img = ImageIO.read(new File(imagePath));
                imageProcessorService.setImage(img);

                imageFeaturesMap = imageProcessorService.getImageFeaturesMap(img, kernelSize);

                listRedChannelFeatures.add(imageFeaturesMap.get("red"));
                listGreenChannelFeatures.add(imageFeaturesMap.get("green"));
                listBlueChannelFeatures.add(imageFeaturesMap.get("blue"));
                listGrayScaleChannelFeatures.add(imageFeaturesMap.get("gray"));
                listRgbAvgFeatures.add(imageFeaturesMap.get("avg"));

            } catch (IOException ex) {
                Logger.getLogger(BodyWomanFeatureExtractorExecutor.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        /* Populate Descriptor Object */
        part.setRedChannel(imageProcessorService.getAvarege(listRedChannelFeatures));
        part.setGreenChannel(imageProcessorService.getAvarege(listGreenChannelFeatures));
        part.setBlueChannel(imageProcessorService.getAvarege(listBlueChannelFeatures));
        part.setGrayScaleChannel(imageProcessorService.getAvarege(listGrayScaleChannelFeatures));
        part.setAvgChannel(imageProcessorService.getAvarege(listRgbAvgFeatures));

        return part;
    }

    @Override
    public void run() {
        this.start();
    }

}
