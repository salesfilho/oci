/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.prof.salesfilho.oci.service;

import br.prof.salesfilho.oci.domain.BodyPartDescriptor;
import br.prof.salesfilho.oci.image.ImageProcessor;
import java.awt.image.BufferedImage;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.math3.ml.distance.EuclideanDistance;

/**
 *
 * @author salesfilho
 */
@Getter
@Setter
public class EuclidianClassifier implements Runnable {

    private ImageProcessor imageProcessor;
    private BodyPartDescriptor nudeBodyPartDescriptor;
    private BodyPartDescriptor notNudeBodyPartDescriptor;
    private BufferedImage image;
    private double kernelSize;
    private boolean nude;
    private boolean finish;
    private int classificationLevel = 1;

    public EuclidianClassifier(BodyPartDescriptor nudeBodyPartDescriptor, BodyPartDescriptor notNudeBodyPartDescriptor, BufferedImage image, double kernelSize) {
        this.imageProcessor = new ImageProcessor(image);
        this.nudeBodyPartDescriptor = nudeBodyPartDescriptor;
        this.notNudeBodyPartDescriptor = notNudeBodyPartDescriptor;
        this.image = image;
        this.kernelSize = kernelSize;
        this.finish = false;
    }

    @Override
    public void run() {
        System.out.println("Part classify: " + this.nudeBodyPartDescriptor.getName());
        this.classify(this.classificationLevel);
        this.finish = true;
        System.out.println(this.nudeBodyPartDescriptor.getName() + " is nude: " + this.isNude());
    }

    private void classify(int level) {

        double yesNudeDistance = 0;
        double noNudeDistance = 0;

        EuclideanDistance euclideanDistance = new EuclideanDistance();

        double[] redChanellDescriptor;
        double[] greenChanellDescriptor;
        double[] blueChanellDescriptor;
        double[] grayChanellDescriptor;
        double[] avgChanellDescriptor;

        long startTime, endTime;

        switch (level) {
            case 1:
                //Gray
                grayChanellDescriptor = imageProcessor.getMagnitude(ImageProcessor.CHANNEL_GRAYSCALE, kernelSize);

                startTime = System.currentTimeMillis();
                yesNudeDistance += euclideanDistance.compute(grayChanellDescriptor, nudeBodyPartDescriptor.getGrayScaleChannel());
                endTime = System.currentTimeMillis();
                System.out.println("Time in secunds to comput nude enclidian distance: " + (double)(endTime - startTime) / 1000);

                startTime = System.currentTimeMillis();
                noNudeDistance += euclideanDistance.compute(grayChanellDescriptor, notNudeBodyPartDescriptor.getGrayScaleChannel());
                endTime = System.currentTimeMillis();
                System.out.println("Time in secunds to comput NOT nude enclidian distance: " + (double)(endTime - startTime) / 1000);
                break;
            case 2:
                redChanellDescriptor = imageProcessor.getMagnitude(ImageProcessor.CHANNEL_RED, kernelSize);
                greenChanellDescriptor = imageProcessor.getMagnitude(ImageProcessor.CHANNEL_GREEN, kernelSize);
                blueChanellDescriptor = imageProcessor.getMagnitude(ImageProcessor.CHANNEL_BLUE, kernelSize);

                //Red
                yesNudeDistance += euclideanDistance.compute(redChanellDescriptor, nudeBodyPartDescriptor.getRedChannel());
                noNudeDistance += euclideanDistance.compute(redChanellDescriptor, notNudeBodyPartDescriptor.getRedChannel());

                //Green
                yesNudeDistance += euclideanDistance.compute(greenChanellDescriptor, nudeBodyPartDescriptor.getGreenChannel());
                noNudeDistance += euclideanDistance.compute(greenChanellDescriptor, notNudeBodyPartDescriptor.getGreenChannel());

                //Blue
                yesNudeDistance += euclideanDistance.compute(blueChanellDescriptor, nudeBodyPartDescriptor.getBlueChannel());
                noNudeDistance += euclideanDistance.compute(blueChanellDescriptor, notNudeBodyPartDescriptor.getBlueChannel());

                break;
            case 3:
                redChanellDescriptor = imageProcessor.getMagnitude(ImageProcessor.CHANNEL_RED, kernelSize);
                greenChanellDescriptor = imageProcessor.getMagnitude(ImageProcessor.CHANNEL_GREEN, kernelSize);
                blueChanellDescriptor = imageProcessor.getMagnitude(ImageProcessor.CHANNEL_BLUE, kernelSize);
                grayChanellDescriptor = imageProcessor.getMagnitude(ImageProcessor.CHANNEL_GRAYSCALE, kernelSize);

                //Red
                yesNudeDistance += euclideanDistance.compute(redChanellDescriptor, nudeBodyPartDescriptor.getRedChannel());
                noNudeDistance += euclideanDistance.compute(redChanellDescriptor, notNudeBodyPartDescriptor.getRedChannel());

                //Green
                yesNudeDistance += euclideanDistance.compute(greenChanellDescriptor, nudeBodyPartDescriptor.getGreenChannel());
                noNudeDistance += euclideanDistance.compute(greenChanellDescriptor, notNudeBodyPartDescriptor.getGreenChannel());

                //Blue
                yesNudeDistance += euclideanDistance.compute(blueChanellDescriptor, nudeBodyPartDescriptor.getBlueChannel());
                noNudeDistance += euclideanDistance.compute(blueChanellDescriptor, notNudeBodyPartDescriptor.getBlueChannel());

                //Gray
                yesNudeDistance += euclideanDistance.compute(grayChanellDescriptor, nudeBodyPartDescriptor.getGrayScaleChannel());
                noNudeDistance += euclideanDistance.compute(grayChanellDescriptor, notNudeBodyPartDescriptor.getGrayScaleChannel());
                break;
            case 4:
                //RGB AVG
                avgChanellDescriptor = imageProcessor.getMagnitudeAvarageFromRgbChannels(kernelSize);
                yesNudeDistance += euclideanDistance.compute(avgChanellDescriptor, nudeBodyPartDescriptor.getAvgChannel());
                noNudeDistance += euclideanDistance.compute(avgChanellDescriptor, notNudeBodyPartDescriptor.getAvgChannel());
            default:
                //Gray
                grayChanellDescriptor = imageProcessor.getMagnitude(ImageProcessor.CHANNEL_GRAYSCALE, kernelSize);
                yesNudeDistance += euclideanDistance.compute(grayChanellDescriptor, nudeBodyPartDescriptor.getGrayScaleChannel());
                noNudeDistance += euclideanDistance.compute(grayChanellDescriptor, notNudeBodyPartDescriptor.getGrayScaleChannel());
        }
        this.nude = (yesNudeDistance <= noNudeDistance);
    }
}
