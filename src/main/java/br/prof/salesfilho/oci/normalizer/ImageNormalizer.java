/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.prof.salesfilho.oci.normalizer;

import br.prof.salesfilho.oci.image.ImageRgbProcessor;
import br.prof.salesfilho.oci.util.OCIUtils;
import br.prof.salesfilho.oci.view.graph.Graph;
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
import lombok.Setter;
import org.jfree.ui.RefineryUtilities;
import org.springframework.stereotype.Component;

/**
 *
 * @author salesfilho
 */
@Component
public class ImageNormalizer {

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

    private static final int DEFAULT_RESIZED_IMAGE_SIZE = 256;
    private static final double KERNEL_SIZE = 0.15;

    public void start() {
        System.out.println("ImageNormalizer started.");
        fileList = OCIUtils.getImageFiles(this.inputDir);

        //this.normalyze();
        this.plot(computeCorrentropy());
    }

    public void normalyze() {

        for (String fileName : fileList) {
            try {
                System.out.println("Processing: " + fileName);
                ImageRgbProcessor imageRgbProcessor;
                BufferedImage originalImage = ImageIO.read(new File(fileName));
                imageRgbProcessor = new ImageRgbProcessor(originalImage);
                originalImage = imageRgbProcessor.resize(DEFAULT_RESIZED_IMAGE_SIZE, DEFAULT_RESIZED_IMAGE_SIZE);
                ImageIO.write(originalImage, "jpg", new File(fileName));

            } catch (IOException ex) {
                Logger.getLogger(ImageNormalizer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public Map<String, double[]> computeCorrentropy() {
        Map<String, double[]> resultMap = new HashMap<>();
        for (String fileName : fileList) {
            try {
                BufferedImage originalImage = ImageIO.read(new File(fileName));
                ImageRgbProcessor imageRgbProcessor = new ImageRgbProcessor(originalImage);
                double[][] colorMatrix = OCIUtils.convertIntMatrixToDoubleMatrix(imageRgbProcessor.getRedMatrix());
                double[] vetorImage = OCIUtils.vetorizeEntropySequence(colorMatrix);
                double[] correntropy = OCIUtils.computeAutoCorrentropy(vetorImage, KERNEL_SIZE);
                resultMap.put(fileName, correntropy);
            } catch (IOException ex) {
                Logger.getLogger(ImageNormalizer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return resultMap;
    }

    public void plot(Map<String, double[]> dataMap) {

        Graph g1 = new Graph("Gráfico das correntropias");

        for (Map.Entry<String, double[]> entrySet : dataMap.entrySet()) {
            String key = entrySet.getKey();
            double[] value = entrySet.getValue();
            g1.addSeire(value, key);
        }
        g1.pack();
        RefineryUtilities.centerFrameOnScreen(g1);
        g1.setVisible(true);

    }

    public void normalyze198() {
        int x = 1;
        ImageRgbProcessor imageRgbProcessor;
        for (String fileName : OCIUtils.getImageFiles(this.inputDir)) {
            System.out.println("File:" + fileName);

            if (x == 1) {

                try {
                    x = 2;

                    BufferedImage originalImage = ImageIO.read(new File(this.inputDir + "/" + fileName));
                    imageRgbProcessor = new ImageRgbProcessor(originalImage);
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

                     Graph g1 = new Graph("Imagem vetorizada");
                     g1.addSeire(vt, "Vetorização");
                     g1.setXLabel("Amostras");
                     g1.setYLabel("Valor");
                     g1.pack();
                     RefineryUtilities.centerFrameOnScreen(g1);
                     g1.setVisible(true);

                     Graph gEnergia = new Graph("Energia da Imagem vetorizada");
                     gEnergia.addSeire(vtEnergia, "Energia");
                     gEnergia.setXLabel("Amostras");
                     gEnergia.setYLabel("Valor");
                     gEnergia.pack();
                     RefineryUtilities.centerFrameOnScreen(gEnergia);
                     gEnergia.setVisible(true);

                     Graph g2 = new Graph("Autocorrentropia da imagem vetorizada");
                     g2.addSeire(autoCorrentropyVetorizedImage, "Autocorrentropia da imagem");
                     g2.setXLabel("Amostras");
                     g2.setYLabel("Valor");
                     g2.pack();
                     RefineryUtilities.centerFrameOnScreen(g2);
                     g2.setVisible(true);

                     Graph g_vetorEnergia = new Graph("Autocorrentropia da energia da imagem vetorizada");
                     g_vetorEnergia.addSeire(autoCorrentropyEnergyVetorizedImage, "Autocorrentropia da energia");
                     g_vetorEnergia.setXLabel("Amostras");
                     g_vetorEnergia.setYLabel("Valor");
                     g_vetorEnergia.pack();
                     RefineryUtilities.centerFrameOnScreen(g_vetorEnergia);
                     g_vetorEnergia.setVisible(true);

                     Graph g3 = new Graph("Transformada de Fourrier da Autocorrentropia de imagem");
                     g3.addSeire(fft_correntropyEnergyVetorizedImage, "FFT");
                     g3.pack();
                     RefineryUtilities.centerFrameOnScreen(g3);
                     g3.setVisible(true);

                     Graph g4 = new Graph("Magnitude da Transformada de Fourrier da Autocorrentropia");
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
                    Logger.getLogger(ImageNormalizer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

}
