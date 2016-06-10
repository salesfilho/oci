/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.prof.salesfilho.oci.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;

/**
 *
 * @author salesfilho
 */
public class OCIUtils {

    public static List<String> getImageFiles(String directory) {
        List<String> results = new ArrayList();

        File[] files = new File(directory).listFiles((File dir, String name) -> name.toLowerCase().endsWith(".jpg")
                || name.toLowerCase().endsWith(".jpeg") || name.toLowerCase().endsWith(".png") || name.toLowerCase().endsWith(".tiff")
                || name.toLowerCase().endsWith(".tif") || name.toLowerCase().endsWith(".bmp") || name.toLowerCase().endsWith(".gif"));

        for (File file : files) {
            if (file.isFile()) {
                results.add(file.getAbsolutePath());
            }
        }
        return results;
    }

    public static Complex[] fft(double[] input, TransformType type) {
        FastFourierTransformer fft = new FastFourierTransformer(DftNormalization.STANDARD);
        Complex[] resultComplex = fft.transform(input, type);
        return resultComplex;
    }

    public static Complex[] fft(Complex[] input, TransformType type) {
        FastFourierTransformer fft = new FastFourierTransformer(DftNormalization.STANDARD);
        Complex[] resultComplex = fft.transform(input, type);
        return resultComplex;
    }

    public static double[] fft_forward_double(double[] input) {
        FastFourierTransformer fft = new FastFourierTransformer(DftNormalization.STANDARD);
        Complex[] complexTransInput = fft.transform(input, TransformType.FORWARD);
        double[] result = new double[complexTransInput.length];
        for (int i = 0; i < complexTransInput.length; i++) {
            result[i] = (complexTransInput[i].getReal());
        }
        return result;
    }

    public static double[] fft_magnitude(double[] input) {
        FastFourierTransformer fft = new FastFourierTransformer(DftNormalization.STANDARD);
        Complex[] complexTransInput = fft.transform(input, TransformType.FORWARD);
        for (int i = 0; i < complexTransInput.length; i++) {
            double real = (complexTransInput[i].getReal());
            double img = (complexTransInput[i].getImaginary());
            input[i] = Math.sqrt((Math.pow(real, 2) + Math.pow(img, 2)));
        }
        return input;
    }

    /**
     * @param signal input signal
     * @precond signal != null
     * @return max input signal energy
     */
    public static double computMaxEnergy(double[] signal) {
        double signalEnergy = 0;
        for (double element : signal) {
            signalEnergy = signalEnergy + Math.pow(element, 2);
        }
        return signalEnergy;
    }

    /**
     * @param signal input signal
     * @precond signal != null
     * @return Normalyzed signal array energy
     */
    public static double[] computEnergyVector(double[] signal) {
        double[] energyNormalydedSignal = new double[signal.length];
        double signalEnergy = computMaxEnergy(signal);
        for (int i = 0; i < signal.length; i++) {
            energyNormalydedSignal[i] = signal[i] / Math.sqrt(signalEnergy);
        }
        return energyNormalydedSignal;
    }

    /**
     * @param signal input signal
     * @param kernel Kernel size
     * @precond input != null and Kernel size great then 0 and less then 10
     * @return AutoCorrentropy array
     */
    public static double[] computeAutoCorrentropy(double[] signal, double kernel) {
        double twokSizeSquare = 2 * Math.pow(kernel, 2d);
        int signal_length = signal.length;
        double[] Y = new double[signal_length];
        double b = 1 / kernel * Math.sqrt(2 * Math.PI);
        int N = signal_length;

        for (int m = 0; m < signal_length; m++) {
            for (int n = m + 1; n < signal_length; n++) {
                double pow = Math.pow((signal[n] - signal[n - m - 1]), 2);
                double exp = Math.exp(-pow / twokSizeSquare);
                double equation = (1d / (N - m + 1d)) * b * exp;
                Y[m] = Y[m] + equation;
            }
        }
        return Y;
    }

    /**
     * @param input List of equals length array
     * @precond input != null and equal size double array
     * @return avg array
     */
    public static double[] avarage(List<double[]> input) {
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

    public static int nextPowerOfTwo(int sizeOf) {
        int result;
        if (sizeOf < 2) {
            return 2;
        }

        if ((sizeOf & (sizeOf - 1)) == 0) {
            return sizeOf;
        } else {
            result = nextPowerOfTwo(sizeOf + 1);
        }
        return result;
    }

    public static int previusPowerOfTwo(int sizeOf) {
        int result;
        if (sizeOf < 2) {
            return 2;
        }

        if ((sizeOf & (sizeOf - 1)) == 0) {
            return sizeOf;
        } else {
            result = previusPowerOfTwo(sizeOf - 1);
        }
        return result;
    }

    public static double[] vetorizeEntropySequence(double[][] source) {

        List<double[][]> splited = splitMatrix(source, 2);

        double[] outArray = new double[splited.size() * 4];
        int offSet = 0;
        for (double[][] matrix : splited) {
            double[] v = vetorize(matrix);
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
    private static List<double[][]> splitMatrix(double[][] larger, int subArraySize) throws
            ArrayIndexOutOfBoundsException, NullPointerException {
        if (subArraySize <= 0) {
            throw new ArrayIndexOutOfBoundsException("Chunks must be atleast 1x1");
        }
        int size = larger.length / subArraySize * (larger[0].length / subArraySize);
        List<double[][]> subArrays = new ArrayList<>();

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
            subArrays.add(sub);
        }

        return subArrays;
    }

    public static double[] vetorize(double[][] source) {
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

    public static void printArray(double[][] array) {
        int srcRows = array.length;
        int srcCols = array[0].length;

        for (int y = 0; y < srcRows; y++) {
            for (int x = 0; x < srcCols; x++) {
                System.out.println("Element:[" + y + "," + x + "]" + array[y][x]);
            }
        }
    }

    public static boolean compareArray(double[] array1, double[] array2) {
        int srcRows = array1.length;
        boolean equals = true;
        for (int y = 0; y < srcRows; y++) {
            if (array1[y] != array2[y]) {
                equals = false;
                System.out.println("Diferente na posição:[" + y + "]");
            }
        }
        return equals;
    }

    public static void printArray(double[] array) {
        int srcRows = array.length;

        for (int y = 0; y < srcRows; y++) {
            System.out.println("Element:[" + y + "]" + array[y]);
        }
    }

    public static void printArray(float[][] array) {
        int srcRows = array.length;
        int srcCols = array[0].length;

        for (int y = 0; y < srcRows; y++) {
            for (int x = 0; x < srcCols; x++) {
                System.out.println("Element:[" + y + "," + x + "]" + array[y][x]);
            }
        }
    }

    public static void printArray(int[][] array) {
        int srcRows = array.length;
        int srcCols = array[0].length;

        for (int y = 0; y < srcRows; y++) {
            for (int x = 0; x < srcCols; x++) {
                System.out.println("Element:[" + y + "," + x + "]" + array[y][x]);
            }
        }
    }

    public static void printArray(Complex[] array) {
        int srcRows = array.length;

        for (int y = 0; y < srcRows; y++) {
            System.out.println("Element:[" + y + "]" + array[y]);
        }
    }

    public static void printArray(int[] array) {
        int srcRows = array.length;

        for (int y = 0; y < srcRows; y++) {
            System.out.println("Element:[" + y + "]" + array[y]);
        }
    }

    public static double[][] convertFloatMatrixToDoubleMatrix(float[][] input) {
        double[][] result = new double[input.length][input[0].length];
        for (int i = 0; i < input.length; i++) {
            for (int j = 0; j < input[0].length; j++) {
                result[i][j] = (double) input[i][j];
            }
        }
        return result;
    }

    public static double[][] convertIntMatrixToDoubleMatrix(int[][] input) {
        double[][] result = new double[input.length][input[0].length];
        for (int i = 0; i < input.length; i++) {
            for (int j = 0; j < input[0].length; j++) {
                result[i][j] = (double) input[i][j];
            }
        }
        return result;
    }

    public static double[] convertByteArray2DoubleArray(byte[] input) {
        double[] result = new double[input.length];
        for (int i = 0; i < input.length; i++) {
            result[i] = (double) input[i];
        }
        return result;
    }

    public static byte[] convertDoubleArray2ByteArray(double[] input) {
        byte[] result = new byte[input.length];
        for (int i = 0; i < input.length; i++) {
            result[i] = (byte) input[i];
        }
        return result;
    }
}
