/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.prof.salesfilho.oci.service;

import br.prof.salesfilho.oci.image.ImageProcessor;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author salesfilho
 */
@Component
@NoArgsConstructor
public class ImageProcessorService {

    @Autowired
    private ImageProcessor processor;

    public ImageProcessorService(BufferedImage image) {
        processor.setImage(image);
    }

    public void setImage(BufferedImage image) {
        processor.setImage(image);
    }

    public double[] getMagnitude(int colorChannel, double kernelSize) {
        return processor.getMagnitude(colorChannel, kernelSize);
    }

    public double[] getMagnitudeAvarageFromRgbChannels(double kernelSize) {
        return processor.getMagnitudeAvarageFromRgbChannels(kernelSize);
    }

    public double[] getAvarege(List<double[]> list) {
        return processor.getAvarage(list);
    }
     public List<BufferedImage> getSubImages(int size) {
        return processor.getSubImages(size);
    }

    public Map<String, double[]> getImageFeaturesMap(BufferedImage img, double kernelSize) {

        Map<String, double[]> result = new HashMap<>();

        this.setImage(img);

        double[] redChannelFeatures = this.getMagnitude(ImageProcessor.CHANNEL_RED, kernelSize);
        double[] greenChannelFeatures = this.getMagnitude(ImageProcessor.CHANNEL_GREEN, kernelSize);
        double[] blueChannelFeatures = this.getMagnitude(ImageProcessor.CHANNEL_BLUE, kernelSize);
        double[] rgbAvgFeatures = processor.getMagnitudeAvarageFromRgbChannels(kernelSize);

        result.put("red", redChannelFeatures);
        result.put("green", greenChannelFeatures);
        result.put("blue", blueChannelFeatures);
        result.put("avg", rgbAvgFeatures);

        return result;
    }

}
