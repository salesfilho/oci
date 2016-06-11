/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.prof.salesfilho.oci.service;

import br.prof.salesfilho.oci.image.ImageProcessor;
import br.prof.salesfilho.oci.util.OCIUtils;
import br.prof.salesfilho.oci.image.GraphicBuilder;
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
import lombok.Setter;
import org.jfree.ui.RefineryUtilities;
import org.springframework.stereotype.Component;

/**
 *
 * @author salesfilho
 */
@Component
public class ImageNormalizerService {

    @Getter
    @Setter
    private String inputDir;

    @Getter
    @Setter
    private String outputDir;

    @Getter
    @Setter
    private int resizedImageWidth;

    @Getter
    @Setter
    private int resizedImageHeigth;

    @Getter
    @Setter
    private List<String> fileList;

    private static final int DEFAULT_RESIZED_IMAGE_SIZE = 32;
    private static final double KERNEL_SIZE = 0.15;

    public void start() {
        System.out.println("ImageNormalizer started.");
        fileList = OCIUtils.getImageFiles(this.inputDir);

        //this.normalyze();
        //this.combinedPlot(computeCorrentropy(), "Gráfico de Correntropia de todas as imagens", "Correntropia");
        //this.combinedPlot(computeRGBCorrentropy(getFirstImageFromList()), "Correntropia dos canais RGB", "Correntropia");
        //this.combinedPlot(computeFftOfCorrentropy(getFirstImageFromList()), "FFT da correntropia dos canais RGB", "FFT");
        //this.combinedPlot(computeMagnitudeOfCorrentropy(getFirstImageFromList()), "Magnitude dos canais RGB", "Magnitude");
        this.singlePlot(computeMagnitudeOfCorrentropy(getFirstImageFromList()), "Magnitude dos canais RGB");
    }

    public BufferedImage getFirstImageFromList() {
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File(fileList.get(1)));
        } catch (IOException ex) {
            Logger.getLogger(ImageNormalizerService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return img;
    }

    public void normalyze() {
        String DESTINATION_FILE_PATH = "/Users/salesfilho/Downloads/database/busto_vestido/32x32/";
        int i = 0;
        for (String fileName : fileList) {
            try {
                System.out.println("Processing: " + fileName);
                ImageProcessor imageRgbProcessor;
                BufferedImage img = ImageIO.read(new File(fileName));
                imageRgbProcessor = new ImageProcessor(img);
                img = imageRgbProcessor.resize(DEFAULT_RESIZED_IMAGE_SIZE, DEFAULT_RESIZED_IMAGE_SIZE);
                ImageIO.write(img, "jpg", new File(DESTINATION_FILE_PATH + i + ".jpg"));
                i++;

            } catch (IOException ex) {
                Logger.getLogger(ImageNormalizerService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public Map<String, double[]> computeCorrentropy() {
        Map<String, double[]> resultMap = new HashMap<>();
        int i = 1;
        for (String fileName : fileList) {
            try {
                BufferedImage originalImage = ImageIO.read(new File(fileName));
                ImageProcessor imageRgbProcessor = new ImageProcessor(originalImage);
                double[][] colorMatrix = OCIUtils.convertIntMatrixToDoubleMatrix(imageRgbProcessor.getRedMatrix());
                double[] vetorImage = OCIUtils.vetorizeEntropySequence(colorMatrix);
                double[] correntropy = OCIUtils.computeAutoCorrentropy(vetorImage, KERNEL_SIZE);
                resultMap.put("Imagem-" + i, correntropy);
                i++;
            } catch (IOException ex) {
                Logger.getLogger(ImageNormalizerService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return resultMap;
    }

    public Map<String, double[]> computeRGBCorrentropy(BufferedImage originalImage) {
        Map<String, double[]> resultMap = new HashMap<>();
        List<double[]> listCorrentropy = new ArrayList<>();

        ImageProcessor imageRgbProcessor = new ImageProcessor(originalImage);
        double[][] colorMatrix = OCIUtils.convertIntMatrixToDoubleMatrix(imageRgbProcessor.getRedMatrix());
        double[] vetorImage = OCIUtils.vetorizeEntropySequence(colorMatrix);
        double[] normalyzed = OCIUtils.computEnergyVector(vetorImage);
        double[] correntropy = OCIUtils.computeAutoCorrentropy(normalyzed, KERNEL_SIZE);
        resultMap.put("Red", correntropy);
        listCorrentropy.add(correntropy);

        colorMatrix = OCIUtils.convertIntMatrixToDoubleMatrix(imageRgbProcessor.getGreenMatrix());
        vetorImage = OCIUtils.vetorizeEntropySequence(colorMatrix);
        normalyzed = OCIUtils.normalyzedMaxArray(vetorImage);
        correntropy = OCIUtils.computeAutoCorrentropy(normalyzed, KERNEL_SIZE);
        resultMap.put("Green", correntropy);
        listCorrentropy.add(correntropy);

        colorMatrix = OCIUtils.convertIntMatrixToDoubleMatrix(imageRgbProcessor.getBlueMatrix());
        vetorImage = OCIUtils.vetorizeEntropySequence(colorMatrix);
        correntropy = OCIUtils.computeAutoCorrentropy(vetorImage, KERNEL_SIZE);
        resultMap.put("Blue", correntropy);
        listCorrentropy.add(correntropy);

        colorMatrix = OCIUtils.convertIntMatrixToDoubleMatrix(imageRgbProcessor.getGrayScaleMatrix());
        vetorImage = OCIUtils.vetorizeEntropySequence(colorMatrix);
        correntropy = OCIUtils.computeAutoCorrentropy(vetorImage, KERNEL_SIZE);
        resultMap.put("Gray Scale", correntropy);

        double[] vetorAvg = OCIUtils.avarage(listCorrentropy);

        resultMap.put("Avg (R+G+B)/3", vetorAvg);

        return resultMap;
    }

    public Map<String, double[]> computeFftOfCorrentropy(BufferedImage originalImage) {
        Map<String, double[]> resultMap = new HashMap<>();
        List<double[]> listCorrentropy = new ArrayList<>();

        ImageProcessor imageRgbProcessor = new ImageProcessor(originalImage);
        double[][] colorMatrix = OCIUtils.convertIntMatrixToDoubleMatrix(imageRgbProcessor.getRedMatrix());
        double[] vetorImage = OCIUtils.vetorizeEntropySequence(colorMatrix);
        double[] correntropy = OCIUtils.computeAutoCorrentropy(vetorImage, KERNEL_SIZE);
        double[] fft = OCIUtils.fft_forward_double(correntropy);
        resultMap.put("Red", fft);
        listCorrentropy.add(fft);

        colorMatrix = OCIUtils.convertIntMatrixToDoubleMatrix(imageRgbProcessor.getGreenMatrix());
        vetorImage = OCIUtils.vetorizeEntropySequence(colorMatrix);
        correntropy = OCIUtils.computeAutoCorrentropy(vetorImage, KERNEL_SIZE);
        fft = OCIUtils.fft_forward_double(correntropy);
        resultMap.put("Green", fft);
        listCorrentropy.add(fft);

        colorMatrix = OCIUtils.convertIntMatrixToDoubleMatrix(imageRgbProcessor.getBlueMatrix());
        vetorImage = OCIUtils.vetorizeEntropySequence(colorMatrix);
        correntropy = OCIUtils.computeAutoCorrentropy(vetorImage, KERNEL_SIZE);
        fft = OCIUtils.fft_forward_double(correntropy);
        resultMap.put("Blue", fft);
        listCorrentropy.add(fft);

        colorMatrix = OCIUtils.convertIntMatrixToDoubleMatrix(imageRgbProcessor.getGrayScaleMatrix());
        vetorImage = OCIUtils.vetorizeEntropySequence(colorMatrix);
        correntropy = OCIUtils.computeAutoCorrentropy(vetorImage, KERNEL_SIZE);
        fft = OCIUtils.fft_forward_double(correntropy);
        resultMap.put("Gray Scale", fft);

        double[] vetorAvg = OCIUtils.avarage(listCorrentropy);

        resultMap.put("Avg (R+G+B)/3", vetorAvg);

        return resultMap;
    }

    public Map<String, double[]> computeMagnitudeOfCorrentropy(BufferedImage originalImage) {
        Map<String, double[]> resultMap = new HashMap<>();
        List<double[]> listCorrentropy = new ArrayList<>();

        ImageProcessor imageRgbProcessor = new ImageProcessor(originalImage);
        double[][] colorMatrix = OCIUtils.convertIntMatrixToDoubleMatrix(imageRgbProcessor.getRedMatrix());
        double[] vetorImage = OCIUtils.vetorizeEntropySequence(colorMatrix);
        double[] correntropy = OCIUtils.computeAutoCorrentropy(vetorImage, KERNEL_SIZE);
        double[] magnitude_fft = OCIUtils.fft_magnitude(correntropy);
        resultMap.put("Red", magnitude_fft);
//        listCorrentropy.add(magnitude_fft);
//
//        colorMatrix = OCIUtils.convertIntMatrixToDoubleMatrix(imageRgbProcessor.getGreenMatrix());
//        vetorImage = OCIUtils.vetorizeEntropySequence(colorMatrix);
//        correntropy = OCIUtils.computeAutoCorrentropy(vetorImage, KERNEL_SIZE);
//        magnitude_fft = OCIUtils.fft_magnitude(correntropy);
//        resultMap.put("Green", magnitude_fft);
//        listCorrentropy.add(magnitude_fft);
//
//        colorMatrix = OCIUtils.convertIntMatrixToDoubleMatrix(imageRgbProcessor.getBlueMatrix());
//        vetorImage = OCIUtils.vetorizeEntropySequence(colorMatrix);
//        correntropy = OCIUtils.computeAutoCorrentropy(vetorImage, KERNEL_SIZE);
//        magnitude_fft = OCIUtils.fft_magnitude(correntropy);
//        resultMap.put("Blue", magnitude_fft);
//        listCorrentropy.add(magnitude_fft);
//
//        colorMatrix = OCIUtils.convertIntMatrixToDoubleMatrix(imageRgbProcessor.getGrayScaleMatrix());
//        vetorImage = OCIUtils.vetorizeEntropySequence(colorMatrix);
//        correntropy = OCIUtils.computeAutoCorrentropy(vetorImage, KERNEL_SIZE);
//        magnitude_fft = OCIUtils.fft_magnitude(correntropy);
//        resultMap.put("Gray Scale", magnitude_fft);
//
//        double[] vetorAvg = OCIUtils.avarage(listCorrentropy);
//        resultMap.put("Avg (R+G+B)/3", vetorAvg);
       return resultMap;
    }

    public void combinedPlot(Map<String, double[]> dataMap, String title, String legend) {
        final GraphicBuilder g1 = new GraphicBuilder(title);
        g1.createCombinedChart(dataMap, legend);
        g1.pack();
        RefineryUtilities.centerFrameOnScreen(g1);
        g1.setVisible(true);

    }

    public void singlePlot(Map<String, double[]> dataMap, String title) {

        final GraphicBuilder g1 = new GraphicBuilder(title);
        for (Map.Entry<String, double[]> entrySet : dataMap.entrySet()) {
            String key = entrySet.getKey();
            double[] value = entrySet.getValue();
            g1.addSeire(value, key);
        }
        g1.createChart();
        g1.pack();
        RefineryUtilities.centerFrameOnScreen(g1);
        g1.setVisible(true);

    }

    public void normalyze198() {
        int x = 1;
        ImageProcessor imageRgbProcessor;
        for (String fileName : OCIUtils.getImageFiles(this.inputDir)) {
            System.out.println("File:" + fileName);

            if (x == 1) {

                try {
                    x = 2;

                    BufferedImage originalImage = ImageIO.read(new File(this.inputDir + "/" + fileName));
                    imageRgbProcessor = new ImageProcessor(originalImage);
                    originalImage = imageRgbProcessor.resize(DEFAULT_RESIZED_IMAGE_SIZE, DEFAULT_RESIZED_IMAGE_SIZE);

                    /*
                     BufferedImage workImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), BufferedImage.TYPE_INT_RGB);
                     workImage.setRGB(0, 0, originalImage.getWidth(), originalImage.getHeight(), imageRgbProcessor.getGrayScale(), 0, originalImage.getWidth());

                     if (this.resizedImageWidth > 0 && this.resizedImageHeigth > 0) {
                     originalImage = imageRgbProcessor.resize(resizedImageWidth, resizedImageHeigth);
                     } else {
                     originalImage = imageRgbProcessor.resize(this.DEFAULT_RESIZED_IMAGE_SIZE, this.DEFAULT_RESIZED_IMAGE_SIZE);
                     }
                     ImagePlus img_work = new ImagePlus("Imagem", originalImage);
                     img_work.show();

                     ContentImage ci = new ContentImage(originalImage);

                     double[] vt = ci.getIntVetorizedImage();
                     double[] vtEnergia = ci.getEnergyVector();
                     double[] autoCorrentropyVetorizedImage = ci.getAutoCorrentropyFromVetorizedImage();
                     double[] autoCorrentropyEnergyVetorizedImage = ci.getAutoCorrentropyFromEnergyVetorizedImage();
                     //double[] fft = ci.getFft();
                     //Complex[] fft_inverse = ci.getFftForwardComplex();
                     //double[] fft_correntropyVetorizedImage = ci.getFftFromAutoCorrentropyOfVetorizedImage();
                     double[] fft_correntropyEnergyVetorizedImage = ci.getAutoCorrentropyFromEnergyVetorizedImage();
                     double[] magnitude = OCIUtils.fft_magnitude(fft_correntropyEnergyVetorizedImage);

                     GraphicBuilder g1 = new GraphicBuilder("Imagem vetorizada");
                     g1.addSeire(vt, "Vetorização");
                     g1.setXLabel("Amostras");
                     g1.setYLabel("Valor");
                     g1.pack();
                     RefineryUtilities.centerFrameOnScreen(g1);
                     g1.setVisible(true);

                     GraphicBuilder gEnergia = new GraphicBuilder("Energia da Imagem vetorizada");
                     gEnergia.addSeire(vtEnergia, "Energia");
                     gEnergia.setXLabel("Amostras");
                     gEnergia.setYLabel("Valor");
                     gEnergia.pack();
                     RefineryUtilities.centerFrameOnScreen(gEnergia);
                     gEnergia.setVisible(true);

                     GraphicBuilder g2 = new GraphicBuilder("Autocorrentropia da imagem vetorizada");
                     g2.addSeire(autoCorrentropyVetorizedImage, "Autocorrentropia da imagem");
                     g2.setXLabel("Amostras");
                     g2.setYLabel("Valor");
                     g2.pack();
                     RefineryUtilities.centerFrameOnScreen(g2);
                     g2.setVisible(true);

                     GraphicBuilder g_vetorEnergia = new GraphicBuilder("Autocorrentropia da energia da imagem vetorizada");
                     g_vetorEnergia.addSeire(autoCorrentropyEnergyVetorizedImage, "Autocorrentropia da energia");
                     g_vetorEnergia.setXLabel("Amostras");
                     g_vetorEnergia.setYLabel("Valor");
                     g_vetorEnergia.pack();
                     RefineryUtilities.centerFrameOnScreen(g_vetorEnergia);
                     g_vetorEnergia.setVisible(true);

                     GraphicBuilder g3 = new GraphicBuilder("Transformada de Fourrier da Autocorrentropia de imagem");
                     g3.addSeire(fft_correntropyEnergyVetorizedImage, "FFT");
                     g3.pack();
                     RefineryUtilities.centerFrameOnScreen(g3);
                     g3.setVisible(true);

                     GraphicBuilder g4 = new GraphicBuilder("Magnitude da Transformada de Fourrier da Autocorrentropia");
                     g4.addSeire(magnitude, "Magnitude");
                     g4.pack();
                     RefineryUtilities.centerFrameOnScreen(g4);
                     g4.setVisible(true);
                    
                     double[] v1 = new double[]{2.0, 4.0, 6.0};
                     double[] v2 = new double[]{2.0, 4.0, 6.0};
                     double[] v3 = new double[]{6.0, 6.0, 6.0};
                     double[] v4 = new double[]{2.0, 4.0, 1.0};
                    
                     List<double[]> listDouble = new ArrayList();
                     listDouble.add(v1);
                     listDouble.add(v2);
                     listDouble.add(v3);
                     listDouble.add(v4);
                    
                     double[] media = OCIUtils.avarage(listDouble);
                     OCIUtils.printArray(media);
                     */
//                    ImagePlus f1 = new ImagePlus("Imagem", g1.getGraphAsBufferedImage());
//                    f1.show();
//                    ImagePlus f2 = new ImagePlus("Autocorrentropy", g2.getGraphAsBufferedImage());
//                    f2.show();
//                    ImagePlus f3 = new ImagePlus("FFT", g3.getGraphAsBufferedImage());
//                    f3.show();
//                    ImagePlus f4 = new ImagePlus("Magnitude", g4.getGraphAsBufferedImage());
//                    f4.show();
//                    ImagePlus f5 = new ImagePlus("fft_correntropy", g5.getGraphAsBufferedImage());
//                    f5.show();
                } catch (IOException ex) {
                    Logger.getLogger(ImageNormalizerService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

}
