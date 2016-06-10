/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.prof.salesfilho.oci.domain;

import br.prof.salesfilho.oci.image.ImageRgbProcessor;
import br.prof.salesfilho.oci.util.OCIUtils;
import java.awt.image.BufferedImage;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author salesfilho
 */
@Getter
@Setter
public class ContentImage {
    double[] fft;
    double[] autoCorrentropy;
    double kernel;
    ImageRgbProcessor imageRgbProcessor;

    public ContentImage(BufferedImage bufferedImage) {
        this.imageRgbProcessor = new ImageRgbProcessor(bufferedImage);
    }

    public double[][] getRedPixels() {
        int[][] colorMatrix = imageRgbProcessor.getRedMatrix();
        double[][] result = OCIUtils.convertIntMatrixToDoubleMatrix(colorMatrix);
        return result;
    }

    public double[][] getGreenPixels() {
        int[][] colorMatrix = imageRgbProcessor.getGreenMatrix();
        double[][] result = OCIUtils.convertIntMatrixToDoubleMatrix(colorMatrix);
        return result;
    }

    public double[][] getBluePixels() {
        int[][] colorMatrix = imageRgbProcessor.getBlueMatrix();
        double[][] result = OCIUtils.convertIntMatrixToDoubleMatrix(colorMatrix);
        return result;
    }

    public double[][] getGrayScalePixels() {
        int[][] colorMatrix = imageRgbProcessor.getGrayScaleMatrix();
        double[][] result = OCIUtils.convertIntMatrixToDoubleMatrix(colorMatrix);
        return result;
    }

}
