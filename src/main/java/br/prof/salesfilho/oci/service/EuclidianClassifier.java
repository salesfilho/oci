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
    private String classification; // YES, NO, MAYBE
    private int classificationLevel = 1; // 1 - 3

    public EuclidianClassifier(BodyPartDescriptor nudeBodyPartDescriptor, BodyPartDescriptor notNudeBodyPartDescriptor, BufferedImage image, double kernelSize) {
        this.imageProcessor = new ImageProcessor(image);
        this.nudeBodyPartDescriptor = nudeBodyPartDescriptor;
        this.notNudeBodyPartDescriptor = notNudeBodyPartDescriptor;
        this.image = image;
        this.kernelSize = kernelSize;
    }

    @Override
    public void run() {
        System.out.println("Classification level: " + this.classificationLevel);
        System.out.println("BodyPart classification: " + this.nudeBodyPartDescriptor.getName());
        this.classify();
        System.out.println("Classification result: " + this.getClassification());
    }

    private void classify() {

        double yesNudeDistance = 0;
        double noNudeDistance = 0;

        EuclideanDistance euclideanDistance = new EuclideanDistance();

        double[] redChanellDescriptor;
        double[] greenChanellDescriptor;
        double[] blueChanellDescriptor;
        double[] avgChanellDescriptor;

        switch (this.classificationLevel) {
            case 1:
                //RGB AVG
                avgChanellDescriptor = imageProcessor.getMagnitudeAvarageFromRgbChannels(kernelSize);
                yesNudeDistance += euclideanDistance.compute(avgChanellDescriptor, nudeBodyPartDescriptor.getAvgChannel());
                noNudeDistance += euclideanDistance.compute(avgChanellDescriptor, notNudeBodyPartDescriptor.getAvgChannel());
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
                avgChanellDescriptor = imageProcessor.getMagnitudeAvarageFromRgbChannels(kernelSize);

                //Red
                yesNudeDistance += euclideanDistance.compute(redChanellDescriptor, nudeBodyPartDescriptor.getRedChannel());
                noNudeDistance += euclideanDistance.compute(redChanellDescriptor, notNudeBodyPartDescriptor.getRedChannel());

                //Green
                yesNudeDistance += euclideanDistance.compute(greenChanellDescriptor, nudeBodyPartDescriptor.getGreenChannel());
                noNudeDistance += euclideanDistance.compute(greenChanellDescriptor, notNudeBodyPartDescriptor.getGreenChannel());

                //Blue
                yesNudeDistance += euclideanDistance.compute(blueChanellDescriptor, nudeBodyPartDescriptor.getBlueChannel());
                noNudeDistance += euclideanDistance.compute(blueChanellDescriptor, notNudeBodyPartDescriptor.getBlueChannel());

                //RGB AVG
                yesNudeDistance += euclideanDistance.compute(avgChanellDescriptor, nudeBodyPartDescriptor.getAvgChannel());
                noNudeDistance += euclideanDistance.compute(avgChanellDescriptor, notNudeBodyPartDescriptor.getAvgChannel());

                break;
            default:
                //RGB AVG
                avgChanellDescriptor = imageProcessor.getMagnitudeAvarageFromRgbChannels(kernelSize);
                yesNudeDistance += euclideanDistance.compute(avgChanellDescriptor, nudeBodyPartDescriptor.getAvgChannel());
                noNudeDistance += euclideanDistance.compute(avgChanellDescriptor, notNudeBodyPartDescriptor.getAvgChannel());
        }

        System.out.println("Distance YES.:  " + yesNudeDistance);
        System.out.println("Distance NO..:  " + noNudeDistance);

        if (yesNudeDistance < noNudeDistance) {
            classification = "YES";
        } else if (yesNudeDistance > noNudeDistance) {
            classification = "NO";
        } else {
            classification = "MAYBE";
        }
    }
}
