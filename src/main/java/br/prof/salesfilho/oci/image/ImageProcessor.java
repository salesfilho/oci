/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.prof.salesfilho.oci.image;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;
import org.springframework.stereotype.Component;

/**
 *
 * @author salesfilho
 */
@Component
@NoArgsConstructor
public class ImageProcessor {

    @Getter
    @Setter
    private BufferedImage image;

    public static final int CHANNEL_RED = 1;
    public static final int CHANNEL_GREEN = 2;
    public static final int CHANNEL_BLUE = 3;
    public static final int CHANNEL_GRAYSCALE = 4;

    public ImageProcessor(BufferedImage srcImage) {
        this.image = srcImage;
    }

    public double[] getRed() {
        int[] colors = new int[this.image.getWidth() * this.image.getHeight()];
        this.image.getRGB(0, 0, this.image.getWidth(), this.image.getHeight(), colors, 0, this.image.getWidth());

        double[] red = new double[colors.length];

        for (int i = 0; i < colors.length; i++) {
            Color color = new Color(colors[i]);
            int rgb = color.getRGB();
            int r = (rgb >> 16) & 0xFF;
            red[i] = (double) r;
        }
        return red;
    }

    public double[] getGreen() {
        int[] colors = new int[this.image.getWidth() * this.image.getHeight()];
        this.image.getRGB(0, 0, this.image.getWidth(), this.image.getHeight(), colors, 0, this.image.getWidth());

        double[] green = new double[colors.length];

        for (int i = 0; i < colors.length; i++) {
            Color color = new Color(colors[i]);
            int rgb = color.getRGB();
            int g = (rgb >> 8) & 0xFF;
            green[i] = (double) g;
        }
        return green;
    }

    public double[] getBlue() {
        int[] colors = new int[this.image.getWidth() * this.image.getHeight()];
        this.image.getRGB(0, 0, this.image.getWidth(), this.image.getHeight(), colors, 0, this.image.getWidth());

        double[] blue = new double[colors.length];

        for (int i = 0; i < colors.length; i++) {
            Color color = new Color(colors[i]);
            int rgb = color.getRGB();
            int b = (rgb & 0xFF);
            blue[i] = (double) b;
        }
        return blue;
    }

    public double[] getGrayScale() {
        int[] colors = new int[this.image.getWidth() * this.image.getHeight()];
        this.image.getRGB(0, 0, this.image.getWidth(), this.image.getHeight(), colors, 0, this.image.getWidth());

        double[] grayScale = new double[colors.length];

        for (int i = 0; i < colors.length; i++) {
            Color color = new Color(colors[i]);

            int rgb = color.getRGB();
            int r = (rgb >> 16) & 0xFF;
            int g = (rgb >> 8) & 0xFF;
            int b = (rgb & 0xFF);

            //int grayLevel = (r + g + b) / 3;
            int grayLevel = (int) ((r * 0.2126d) + (g * 0.7152d) + (b * 0.0722)); //Recomended by OpenCV

            int gray = (grayLevel << 16) + (grayLevel << 8) + grayLevel;

            grayScale[i] = (double) gray;
        }
        return grayScale;
    }

    public double[][] getRedMatrix() {
        double[][] resultMatrix = new double[this.image.getWidth()][this.image.getHeight()];
        double[] red = getRed();
        int idx = 0;
        for (int x = 0; x < this.image.getWidth(); x++) {
            for (int y = 0; y < this.image.getHeight(); y++) {
                resultMatrix[x][y] = red[idx];
                idx++;
            }
        }
        return resultMatrix;
    }

    public double[][] getGreenMatrix() {
        double[][] resultMatrix = new double[this.image.getWidth()][this.image.getHeight()];
        double[] green = getGreen();
        int idx = 0;
        for (int x = 0; x < this.image.getWidth(); x++) {
            for (int y = 0; y < this.image.getHeight(); y++) {
                resultMatrix[x][y] = green[idx];
                idx++;
            }
        }
        return resultMatrix;
    }

    public double[][] getBlueMatrix() {
        double[][] resultMatrix = new double[this.image.getWidth()][this.image.getHeight()];
        double[] blue = getBlue();
        int idx = 0;
        for (int x = 0; x < this.image.getWidth(); x++) {
            for (int y = 0; y < this.image.getHeight(); y++) {
                resultMatrix[x][y] = blue[idx];
                idx++;
            }
        }
        return resultMatrix;
    }

    public double[][] getGrayScaleMatrix() {
        double[][] resultMatrix = new double[this.image.getWidth()][this.image.getHeight()];
        double[] grayScale = getGrayScale();
        int idx = 0;
        for (int x = 0; x < this.image.getWidth(); x++) {
            for (int y = 0; y < this.image.getHeight(); y++) {
                resultMatrix[x][y] = grayScale[idx];
                idx++;
            }
        }
        return resultMatrix;
    }

    public BufferedImage resize(int newWidth, int newHeigth) {
        Image tmp = image.getScaledInstance(newWidth, newHeigth, Image.SCALE_SMOOTH);
        BufferedImage dimg = new BufferedImage(newWidth, newHeigth, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = dimg.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();
        this.image = dimg;
        return this.image;
    }

    public List<BufferedImage> getSubImages(int size) {

        if (this.image.getWidth() % size != 0 || this.image.getHeight() % size != 0) {
            throw new ArrayIndexOutOfBoundsException("Image size and input size cause array index out of bounds!");
        }

        List<BufferedImage> result = new ArrayList<>();
        BufferedImage subImage;
        for (int i = 0; i < this.image.getWidth(); i = i + size) {
            for (int j = 0; j < this.image.getHeight(); j = j + size) {
                subImage = image.getSubimage(i, j, size, size);
                result.add(subImage);
            }
        }
        return result;
    }

    /**
     * @param channel RGB and grayscale (1 = RED, 2 = GREEN, 3 = BLUE and 4 =
     * GRAYSCALE, diferent value GRAYSCALE is returned)
     * @param kernel Kernel size to be used by correntropy calculation
     * @return getMagnitude/modulo = fft(autocorrentropia)) as array
     */
    public double[] getMagnitude(int channel, double kernel) {

        double[] result = this.getAutoCorrentropy(channel, kernel);

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
     * @param kernel Kernel size to be used by correntropy calculation
     * @return Avarage of Magnitude from Red, Green and Blue channels (
     * fft(autocorrentropia(avgRgbArray) )) as array
     */
    public double[] getMagnitudeAvarageFromRgbChannels(double kernel) {
        List<double[]> listAvg = new ArrayList();
        listAvg.add(this.getMagnitude(CHANNEL_RED, kernel));
        listAvg.add(this.getMagnitude(CHANNEL_GREEN, kernel));
        listAvg.add(this.getMagnitude(CHANNEL_BLUE, kernel));
        return this.getAvarage(listAvg);
    }

    /**
     * @param input List of equals length array
     * @precond input != null and equal size double array
     * @return avg array
     */
    public double[] getAvarage(List<double[]> input) {
        int vSize = input.get(0).length;
        double[] meanResult = new double[vSize];
        for (double[] vector : input) {
            //sum
            for (int i = 0; i < vector.length; i++) {
                meanResult[i] += vector[i];
            }
        }
        //avg
        int divisor = input.size();
        for (int i = 0; i < vSize; i++) {
            meanResult[i] = meanResult[i] / divisor;
        }
        return meanResult;
    }

    /*   Prvate Methodos */
    /**
     * @param channel RGB and grayscale (1 = RED, 2 = GREEN, 3 = BLUE and 4 =
     * GRAYSCALE, diferent value GRAYSCALE is returned)
     * @param kernel Kernel size
     * @return AutoCorrentropy array
     */
    private double[] getAutoCorrentropy(int channel, double kernel) {

        double[] signal = this.getZigZagVetorized(this.getColorMatrix(channel));

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

    private double[] getZigZagVetorized(double[][] source) {
        double[][][] splited = this.getSplitMatrix(source, 2);

        double[] outArray = new double[splited.length * 4];
        int offSet = 0;
        for (double[][] matrix : splited) {
            double[] v = this.getVetorized(matrix);
            System.arraycopy(v, 0, outArray, offSet, v.length);
            offSet = offSet + 4;
        }
        return outArray;
    }

    /**
     * @param larger the larger array to be split into sub arrays of size
     * chunksize
     * @param subArraySize the size of each sub array
     * @precond chunksize > 0 && larger != null
     * @throws ArrayIndexOutOfBoundsException, NullPointerException
     */
    private double[][][] getSplitMatrix(double[][] larger, int subArraySize) throws
            ArrayIndexOutOfBoundsException, NullPointerException {
        if (subArraySize <= 0) {
            throw new ArrayIndexOutOfBoundsException("Chunks must be atleast 1x1");
        }
        int size = larger.length / subArraySize * (larger[0].length / subArraySize);
        double[][][] subArrays = new double[size][][];

        for (int c = 0; c < size; c++) {
            double[][] sub = new double[subArraySize][subArraySize];
            int startx = (subArraySize * (c / subArraySize)) % larger.length;
            int starty = (subArraySize * c) % larger[0].length;

            if (starty + subArraySize > larger[0].length) {
                starty = 0;
            }

            for (int row = 0; row < subArraySize; row++) {
                for (int col = 0; col < subArraySize; col++) {
                    sub[row][col] = larger[startx + row][col + starty];
                }
            }
            subArrays[c] = sub;
        }

        return subArrays;
    }

    private double[] getVetorized(double[][] source) {
        int width = source.length;
        double[] output = new double[width * width];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < width; y++) {
                int loc = y + x * width;
                output[loc] = source[x][y];
            }
        }
        return output;
    }

    private double[][] getColorMatrix(int channel) {
        double[][] colorMatrix;
        switch (channel) {
            case 1:
                colorMatrix = this.getRedMatrix();
                break;
            case 2:
                colorMatrix = this.getGreenMatrix();
                break;
            case 3:
                colorMatrix = this.getBlueMatrix();
                break;
            case 4:
                colorMatrix = this.getGrayScaleMatrix();
                break;
            default:
                colorMatrix = this.getGrayScaleMatrix();
        }
        return colorMatrix;
    }
}
