/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.prof.salesfilho.oci.service;

import br.prof.salesfilho.oci.domain.ImageDescriptor;
import br.prof.salesfilho.oci.util.OCIUtils;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
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
public class ImageWomanBustClassifier {

    @Autowired
    private ImageDescriptorService imageDescriptorService;

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

    private BufferedImage image;

    public ImageWomanBustClassifier(BufferedImage image) {
        this.image = image;
    }

    public void start() {
        fileList = OCIUtils.getImageFiles(this.inputDir);
        System.out.println("******************** CLASSIFICATION SUMMARY ********************");
        for (Map.Entry<String, String> entrySet : this.euclidianDistanceHardClassify().entrySet()) {
            String key = entrySet.getKey();
            String value = entrySet.getValue();
            System.out.println("File: " + key);
            System.out.println("Is nude bust ...: " + value);
        }
        System.out.println("****************************************************************");
    }

    public Map<String, String> euclidianDistanceVeryHardClassify() {
        EuclideanDistance euclideanDistance = new EuclideanDistance();

        Map<String, Double> noNudeBustDistanceList = new HashMap<>();
        Map<String, Double> nudeBustDistanceList = new HashMap<>();

        Map<String, String> result = new HashMap<>();

        imageDescriptorService.openDatabase(new File(this.databaseName));
        List<ImageDescriptor> nudeBustList = imageDescriptorService.findAllWomanNudeBust();
        List<ImageDescriptor> noNudeBustList = imageDescriptorService.findAllWomanNonNudeBust();

        for (String imagePath : fileList) {
            try {
                this.image = ImageIO.read(new File(imagePath));

            } catch (IOException ex) {
                Logger.getLogger(ImageNormalizerService.class.getName()).log(Level.SEVERE, null, ex);
            }

            System.out.println("Extracting classify features from: ".concat(imagePath));

            double[] dataRedChannel = imageDescriptorService.magnitude(this.image, ImageDescriptor.CHANNEL_RED, this.kernelSize);
            double[] dataGreenChannel = imageDescriptorService.magnitude(this.image, ImageDescriptor.CHANNEL_GREEN, this.kernelSize);
            double[] dataBlueChannel = imageDescriptorService.magnitude(this.image, ImageDescriptor.CHANNEL_BLUE, this.kernelSize);
            double[] dataGrayScaleChannel = imageDescriptorService.magnitude(this.image, ImageDescriptor.CHANNEL_GRAYSCALE, this.kernelSize);

            System.out.println("Computing Euclidian distance for nude bust images... ");

            for (ImageDescriptor descritor : nudeBustList) {
                nudeBustDistanceList.put("Red", euclideanDistance.compute(dataRedChannel, descritor.getRedChannel()));
                nudeBustDistanceList.put("Green", euclideanDistance.compute(dataGreenChannel, descritor.getGreenChannel()));
                nudeBustDistanceList.put("Blue", euclideanDistance.compute(dataBlueChannel, descritor.getBlueChannel()));
                nudeBustDistanceList.put("GrayScale", euclideanDistance.compute(dataGrayScaleChannel, descritor.getGrayScaleChannel()));
            }

            System.out.println("Computing Euclidian distance for NON nude bust images... ");
            for (ImageDescriptor descritor : noNudeBustList) {
                noNudeBustDistanceList.put("Red", euclideanDistance.compute(dataRedChannel, descritor.getRedChannel()));
                noNudeBustDistanceList.put("Green", euclideanDistance.compute(dataGreenChannel, descritor.getGreenChannel()));
                noNudeBustDistanceList.put("Blue", euclideanDistance.compute(dataBlueChannel, descritor.getBlueChannel()));
                noNudeBustDistanceList.put("GrayScale", euclideanDistance.compute(dataGrayScaleChannel, descritor.getGrayScaleChannel()));
            }

            /* Calculate and classify */
            double yes = 0;
            double no = 0;

            for (Map.Entry<String, Double> entrySet : nudeBustDistanceList.entrySet()) {
                String key = entrySet.getKey();
                Double value = entrySet.getValue();

                System.out.println("Nude bust test --> Euclidian distance for " + key + " channel: " + value);
                yes += value;
            }
            for (Map.Entry<String, Double> entrySet : noNudeBustDistanceList.entrySet()) {
                String key = entrySet.getKey();
                Double value = entrySet.getValue();

                System.out.println("No nude bust test --> Euclidian distance for " + key + " channel: " + value);
                no += value;
            }
            System.out.println("YES score..: " + yes);
            System.out.println("NO score...: " + no);

            result.put(imagePath, (yes >= no ? "Yes" : "No"));

            /* Clear lists for next step */
            noNudeBustDistanceList.clear();
            nudeBustDistanceList.clear();

        }

        return result;
    }

    public Map<String, String> euclidianDistanceHardClassify() {
        EuclideanDistance euclideanDistance = new EuclideanDistance();

        Map<String, Double> noNudeBustDistanceList = new HashMap<>();
        Map<String, Double> nudeBustDistanceList = new HashMap<>();

        Map<String, String> result = new HashMap<>();

        imageDescriptorService.openDatabase(new File(this.databaseName));
        List<ImageDescriptor> nudeBustList = imageDescriptorService.findAllWomanNudeBust();
        List<ImageDescriptor> noNudeBustList = imageDescriptorService.findAllWomanNonNudeBust();

        for (String imagePath : fileList) {
            try {
                this.image = ImageIO.read(new File(imagePath));

            } catch (IOException ex) {
                Logger.getLogger(ImageNormalizerService.class.getName()).log(Level.SEVERE, null, ex);
            }

            System.out.println("Extracting classify features from: ".concat(imagePath));

            double[] dataRedChannel = imageDescriptorService.magnitude(this.image, ImageDescriptor.CHANNEL_RED, this.kernelSize);
            double[] dataGreenChannel = imageDescriptorService.magnitude(this.image, ImageDescriptor.CHANNEL_GREEN, this.kernelSize);
            double[] dataBlueChannel = imageDescriptorService.magnitude(this.image, ImageDescriptor.CHANNEL_BLUE, this.kernelSize);

            System.out.println("Computing Euclidian distance for nude bust images... ");

            for (ImageDescriptor descritor : nudeBustList) {
                nudeBustDistanceList.put("Red", euclideanDistance.compute(dataRedChannel, descritor.getRedChannel()));
                nudeBustDistanceList.put("Green", euclideanDistance.compute(dataGreenChannel, descritor.getGreenChannel()));
                nudeBustDistanceList.put("Blue", euclideanDistance.compute(dataBlueChannel, descritor.getBlueChannel()));
            }

            System.out.println("Computing Euclidian distance for NON nude bust images... ");
            for (ImageDescriptor descritor : noNudeBustList) {
                noNudeBustDistanceList.put("Red", euclideanDistance.compute(dataRedChannel, descritor.getRedChannel()));
                noNudeBustDistanceList.put("Green", euclideanDistance.compute(dataGreenChannel, descritor.getGreenChannel()));
                noNudeBustDistanceList.put("Blue", euclideanDistance.compute(dataBlueChannel, descritor.getBlueChannel()));
            }

            /* Calculate and classify */
            double yes = 0;
            double no = 0;

            for (Map.Entry<String, Double> entrySet : nudeBustDistanceList.entrySet()) {
                String key = entrySet.getKey();
                Double value = entrySet.getValue();

                System.out.println("Nude bust test --> Euclidian distance for " + key + " channel: " + value);
                yes += value;
            }
            for (Map.Entry<String, Double> entrySet : noNudeBustDistanceList.entrySet()) {
                String key = entrySet.getKey();
                Double value = entrySet.getValue();

                System.out.println("No nude bust test --> Euclidian distance for " + key + " channel: " + value);
                no += value;
            }
            System.out.println("YES score..: " + yes);
            System.out.println("NO score...: " + no);

            result.put(imagePath, (yes <= no ? "Yes" : "No"));

            /* Clear lists for next step */
            noNudeBustDistanceList.clear();
            nudeBustDistanceList.clear();

        }

        return result;
    }
}
