/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.prof.salesfilho.oci.view.console;

import br.prof.salesfilho.oci.domain.BodyPartDescriptor;
import br.prof.salesfilho.oci.domain.BodyWomanDescriptor;
import br.prof.salesfilho.oci.image.ImageProcessor;
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

    @Getter
    @Setter
    private BodyWomanDescriptor bodyWomanDescriptor;

    private static final double DEFAULT_KERNEL_SIZE = 0.15;
    private static final String DEFAULT_FEATURE_IMAGE_SIZE = "128x128";

    public BodyWomanFeatureExtractorExecutor(boolean nudeFeature) {
        this.nude = nudeFeature;
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

    public void extractNudeFeatures() {

        long startTime = System.currentTimeMillis();
        System.out.println("*************************************************************************************");
        System.out.println("Extracting not nude image features");
        System.out.println("*************************************************************************************");

        bodyWomanDescriptor = extract("Nude body descriptor", true);

        long endTime = System.currentTimeMillis();

        System.out.println("-------------------------------------------------------------------------------------");
        System.out.println("Total process time: " + (endTime - startTime) + " ms");
        System.out.println("-------------------------------------------------------------------------------------");
    }

    public void extractNotNudeFeatures() {

        long startTime = System.currentTimeMillis();
        System.out.println("*************************************************************************************");
        System.out.println("Extracting nude image features");
        System.out.println("*************************************************************************************");

        bodyWomanDescriptor = extract("Not nude body descriptor", false);

        long endTime = System.currentTimeMillis();

        System.out.println("-------------------------------------------------------------------------------------");
        System.out.println("Total process time: " + (endTime - startTime) + " ms");
        System.out.println("-------------------------------------------------------------------------------------");
    }

    private BodyWomanDescriptor extract(String descriptorName, boolean isNude) {
        String buttockPath;
        String chestPath;
        String genitalPath;

        if (isNude) {
            buttockPath = this.inputDir.concat("/nude/buttock/".concat(DEFAULT_FEATURE_IMAGE_SIZE));
            chestPath = this.inputDir.concat("/nude/chest/".concat(DEFAULT_FEATURE_IMAGE_SIZE));
            genitalPath = this.inputDir.concat("/nude/genital/".concat(DEFAULT_FEATURE_IMAGE_SIZE));

        } else {
            buttockPath = this.inputDir.concat("/not_nude/buttock/".concat(DEFAULT_FEATURE_IMAGE_SIZE));
            chestPath = this.inputDir.concat("/not_nude/chest/".concat(DEFAULT_FEATURE_IMAGE_SIZE));
            genitalPath = this.inputDir.concat("/not_nude/genital/".concat(DEFAULT_FEATURE_IMAGE_SIZE));
        }

        BodyWomanDescriptor womanDescriptor = new BodyWomanDescriptor(descriptorName, isNude);

        /*Add Buttock features*/
        BodyPartDescriptor partDescriptor = new BodyPartDescriptor("Buttock", isNude);
        partDescriptor = populate(partDescriptor, buttockPath);
        womanDescriptor.addBodyPart(partDescriptor);

        /*Add chest features*/
        partDescriptor = new BodyPartDescriptor("Chest", isNude);
        partDescriptor = populate(partDescriptor, chestPath);
        womanDescriptor.addBodyPart(partDescriptor);

        /*Add genital features*/
        partDescriptor = new BodyPartDescriptor("Genital", isNude);
        partDescriptor = populate(partDescriptor, genitalPath);
        womanDescriptor.addBodyPart(partDescriptor);

        return womanDescriptor;
    }

    private BodyPartDescriptor populate(BodyPartDescriptor part, String path) {

        List<double[]> listRedChannelFeatures = new ArrayList<>();
        List<double[]> listGreenChannelFeatures = new ArrayList<>();
        List<double[]> listBlueChannelFeatures = new ArrayList<>();
        List<double[]> listGrayChannelFeatures = new ArrayList<>();
        List<double[]> listRgbAvgFeatures = new ArrayList<>();

        Map<String, double[]> imageFeaturesMap;
        fileList = OCIUtils.getImageFiles(path);

        for (String imagePath : fileList) {
            System.out.println("Processing file: " + imagePath);

            try {
                BufferedImage img = ImageIO.read(new File(imagePath));
                
                ImageProcessorService imageService = new ImageProcessorService();
                ImageProcessor ip = new ImageProcessor(img);
                imageService.setProcessor(ip);

                imageFeaturesMap = imageService.getImageFeaturesMap(kernelSize);

                listRedChannelFeatures.add(imageFeaturesMap.get("red"));
                listGreenChannelFeatures.add(imageFeaturesMap.get("green"));
                listBlueChannelFeatures.add(imageFeaturesMap.get("blue"));
                listGrayChannelFeatures.add(imageFeaturesMap.get("gray"));
                listRgbAvgFeatures.add(imageFeaturesMap.get("avg"));

            } catch (IOException ex) {
                Logger.getLogger(BodyWomanFeatureExtractorExecutor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        /* Populate Descriptor Object */
        part.setRedChannel(ImageProcessorService.computeAgv(listRedChannelFeatures));
        part.setGreenChannel(ImageProcessorService.computeAgv(listGreenChannelFeatures));
        part.setBlueChannel(ImageProcessorService.computeAgv(listBlueChannelFeatures));
        part.setGrayScaleChannel(ImageProcessorService.computeAgv(listGrayChannelFeatures));
        part.setAvgChannel(ImageProcessorService.computeAgv(listRgbAvgFeatures));

        return part;
    }

    @Override
    public void run() {
        this.start();
    }
}
