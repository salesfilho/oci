/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.prof.salesfilho.oci.service;

import br.prof.salesfilho.oci.dao.DescriptorDAO;
import br.prof.salesfilho.oci.domain.ImageDescriptor;
import br.prof.salesfilho.oci.image.ImageProcessor;
import br.prof.salesfilho.oci.util.OCIUtils;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author salesfilho
 */
@Component
public class ImageDescriptorService {

    @Autowired
    private ImageProcessor imageProcessor;

    @Autowired
    private DescriptorDAO dao;

    public void openDatabase(String xmlDatabase) {
        dao.create(xmlDatabase);
    }

    public void openDatabase(File xmlDatabase) {
        dao.open(xmlDatabase);
    }

    public void save(File xmlDatabase) {
        dao.save(xmlDatabase);
    }

    public String toXML() {
        return dao.toXML();
    }

    public void add(ImageDescriptor descriptor, int descritorType) {
        dao.add(descriptor, descritorType);
    }

    public List<ImageDescriptor> findAll() {
        return dao.findAll();
    }

    public List<ImageDescriptor> findAllWomanNonNudeBust() {
        return dao.findAllWomanNonNudeBust();
    }

    public List<ImageDescriptor> findAllWomanNudeBust() {
        return dao.findAllWomanNudeBust();
    }

    public ImageDescriptor findAWomanNonNudeBustById(int id) {
        return dao.findAWomanNonNudeBustById(id);
    }

    public ImageDescriptor findAWomanNudeBustById(int id) {
        return dao.findAWomanNudeBustById(id);
    }

    /**
     * @param image input image (signal)
     * @param width
     * @param heigth
     * @return Resized image
     */
    public BufferedImage resize(BufferedImage image, int width, int heigth) {
        imageProcessor.setImage(image);
        BufferedImage result = imageProcessor.resize(width, heigth);
        return result;
    }

    /**
     * @param signal
     * @param kernel Kernel size
     * @return AutoCorrentropy array
     */
    public double[] autoCorrentropy(double[] signal, double kernel) {
        //Normalyze
        //signal = OCIUtils.maxElementArrayNormalyze(signal);

        double twokSizeSquare = 2 * Math.pow(kernel, 2d);
        int signal_length = signal.length;
        double[] autoCorrentropy = new double[signal_length];
        double b = 1 / kernel * Math.sqrt(2 * Math.PI);
        int N = signal_length;

        for (int m = 0; m < signal_length; m++) {
            for (int n = m + 1; n < signal_length; n++) {
                double pow = Math.pow((signal[n] - signal[n - m - 1]), 2);
                double exp = Math.exp(-pow / twokSizeSquare);
                double equation = (1d / (N - m + 1d)) * b * exp;
                autoCorrentropy[m] = autoCorrentropy[m] + equation;
            }
        }
        return autoCorrentropy;
    }

    /**
     * @param image input image (signal)
     * @param channel RGB and grayscale (1 = RED, 2 = GREEN, 3 = BLUE and 4 =
     * GRAYSCALE, diferent value GRAYSCALE is returned)
     * @param kernel Kernel size
     * @return AutoCorrentropy array
     */
    public double[] autoCorrentropy(BufferedImage image, int channel, double kernel) {

        //Vetorization and normalization
        double[] signal = OCIUtils.vetorizeWithSpatialEntropySequence(this.getColorMatrix(image, channel));
        //signal = OCIUtils.maxElementArrayNormalyze(signal);
        //signal = OCIUtils.computEnergyVector(signal);

        double twokSizeSquare = 2 * Math.pow(kernel, 2d);
        int signal_length = signal.length;
        double[] autoCorrentropy = new double[signal_length];
        double b = 1 / kernel * Math.sqrt(2 * Math.PI);
        int N = signal_length;

        for (int m = 0; m < signal_length; m++) {
            for (int n = m + 1; n < signal_length; n++) {
                double pow = Math.pow((signal[n] - signal[n - m - 1]), 2);
                double exp = Math.exp(-pow / twokSizeSquare);
                double equation = (1d / (N - m + 1d)) * b * exp;
                autoCorrentropy[m] = autoCorrentropy[m] + equation;
            }
        }
        return autoCorrentropy;
    }

    /**
     * @param signal
     * @param channel RGB and grayscale (1 = RED, 2 = GREEN, 3 = BLUE and 4 =
     * GRAYSCALE, diferent value GRAYSCALE is returned)
     * @return array of abs FFT
     */
    public double[] fft(double[] signal, int channel) {
        double[] result = new double[signal.length];

        FastFourierTransformer fft = new FastFourierTransformer(DftNormalization.STANDARD);
        Complex[] complexTransInput = fft.transform(signal, TransformType.FORWARD);
        for (int i = 0; i < complexTransInput.length; i++) {
            result[i] = complexTransInput[i].getReal() + complexTransInput[i].getImaginary();
        }
        return result;
    }
    /**
     * @param image input image (signal)
     * @param channel RGB and grayscale (1 = RED, 2 = GREEN, 3 = BLUE and 4 =
     * GRAYSCALE, diferent value GRAYSCALE is returned)
     * @return array of abs FFT
     */
    public double[] fft(BufferedImage image, int channel) {
        double[] result = OCIUtils.vetorizeWithSpatialEntropySequence(this.getColorMatrix(image, channel));

        FastFourierTransformer fft = new FastFourierTransformer(DftNormalization.STANDARD);
        Complex[] complexTransInput = fft.transform(result, TransformType.FORWARD);
        for (int i = 0; i < complexTransInput.length; i++) {
            result[i] = complexTransInput[i].getReal() + complexTransInput[i].getImaginary();
        }
        return result;
    }

    /**
     * @param image input image (signal)
     * @param channel RGB and grayscale (1 = RED, 2 = GREEN, 3 = BLUE and 4 =
     * GRAYSCALE, diferent value GRAYSCALE is returned)
     * @param kernel Kernel size
     * @return magnitude/modulo = fft(autocorrentropia)) as array
     */
    public double[] magnitude(BufferedImage image, int channel, double kernel) {
        double[] result = this.autoCorrentropy(image, channel, kernel);

        FastFourierTransformer fft = new FastFourierTransformer(DftNormalization.STANDARD);
        Complex[] complexTransInput = fft.transform(result, TransformType.FORWARD);
        for (int i = 0; i < complexTransInput.length; i++) {
            double real = (complexTransInput[i].getReal());
            double img = (complexTransInput[i].getImaginary());
            result[i] = Math.sqrt(Math.pow(real, 2) + Math.pow(img, 2));
        }
        return result;
    }
    /**
     * @param signal
     * @param kernel Kernel size
     * @return magnitude/modulo = fft(autocorrentropia)) as array
     */
    public double[] magnitude(double[] signal,  double kernel) {
        double[] result = this.autoCorrentropy(signal, kernel);

        FastFourierTransformer fft = new FastFourierTransformer(DftNormalization.STANDARD);
        Complex[] complexTransInput = fft.transform(result, TransformType.FORWARD);
        for (int i = 0; i < complexTransInput.length; i++) {
            double real = (complexTransInput[i].getReal());
            double img = (complexTransInput[i].getImaginary());
            result[i] = Math.sqrt(Math.pow(real, 2) + Math.pow(img, 2));
        }
        return result;
    }

    /*   Prvate Methodos */
    private double[][] getColorMatrix(BufferedImage image, int channel) {
        imageProcessor.setImage(image);
        double[][] colorMatrix;
        switch (channel) {
            case 1:
                colorMatrix = imageProcessor.getRedMatrix();
                break;
            case 2:
                colorMatrix = imageProcessor.getGreenMatrix();
                break;
            case 3:
                colorMatrix = imageProcessor.getBlueMatrix();
                break;
            case 4:
                colorMatrix = imageProcessor.getGrayScaleMatrix();
                break;
            default:
                colorMatrix = imageProcessor.getGrayScaleMatrix();
        }
        return colorMatrix;
    }

}
