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
import java.util.ArrayList;
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
import sun.security.util.Cache;

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
        System.out.println(this.euclidianHardDistanceClassify());
    }

    public String euclidianHardDistanceClassify() {
        EuclideanDistance euclideanDistance = new EuclideanDistance();
        Map<String, Double> noNudeBustDistanceList = new HashMap<>();
        Map<String, Double> nudeBustDistanceList = new HashMap<>();

        for (String imagePath : fileList) {
            try {
                System.out.println("Extracting classify features from: ".concat(imagePath));
                this.image = ImageIO.read(new File(imagePath));

            } catch (IOException ex) {
                Logger.getLogger(ImageNormalizerService.class.getName()).log(Level.SEVERE, null, ex);
            }

            double[] dataRedChannel = imageDescriptorService.magnitude(this.image, ImageDescriptor.CHANNEL_RED, this.kernelSize);
            double[] dataGreenChannel = imageDescriptorService.magnitude(this.image, ImageDescriptor.CHANNEL_GREEN, this.kernelSize);
            double[] dataBlueChannel = imageDescriptorService.magnitude(this.image, ImageDescriptor.CHANNEL_BLUE, this.kernelSize);
            double[] dataGrayScaleChannel = imageDescriptorService.magnitude(this.image, ImageDescriptor.CHANNEL_GRAYSCALE, this.kernelSize);

            imageDescriptorService.openDatabase(new File(this.databaseName));
            List<ImageDescriptor> nudeBustList = imageDescriptorService.findAllWomanNudeBust();

            for (ImageDescriptor descritor : nudeBustList) {
                nudeBustDistanceList.put("Red", euclideanDistance.compute(dataRedChannel, descritor.getRedAvgChannel()));
                nudeBustDistanceList.put("Green", euclideanDistance.compute(dataGreenChannel, descritor.getGreenAvgChannel()));
                nudeBustDistanceList.put("Blue", euclideanDistance.compute(dataBlueChannel, descritor.getBlueAvgChannel()));
                nudeBustDistanceList.put("GrayScale", euclideanDistance.compute(dataGrayScaleChannel, descritor.getGrayScaleAvgChannel()));
            }

            List<ImageDescriptor> noNudeBustList = imageDescriptorService.findAllWomanNonNudeBust();

            for (ImageDescriptor descritor : noNudeBustList) {
                noNudeBustDistanceList.put("Red", euclideanDistance.compute(dataRedChannel, descritor.getRedAvgChannel()));
                noNudeBustDistanceList.put("Green", euclideanDistance.compute(dataGreenChannel, descritor.getGreenAvgChannel()));
                noNudeBustDistanceList.put("Blue", euclideanDistance.compute(dataBlueChannel, descritor.getBlueAvgChannel()));
                noNudeBustDistanceList.put("GrayScale", euclideanDistance.compute(dataGrayScaleChannel, descritor.getGrayScaleAvgChannel()));
            }
        }

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

        return (yes >= no ? "Yes" : "No");
    }
}
