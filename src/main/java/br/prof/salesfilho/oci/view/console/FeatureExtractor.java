/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.prof.salesfilho.oci.view.console;

import br.prof.salesfilho.oci.domain.ImageDescriptor;
import br.prof.salesfilho.oci.image.GraphicBuilder;
import br.prof.salesfilho.oci.service.ImageDescriptorService;
import br.prof.salesfilho.oci.service.ImageNormalizerService;
import br.prof.salesfilho.oci.util.OCIUtils;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import lombok.Getter;
import lombok.Setter;
import org.jfree.ui.RefineryUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author salesfilho
 */
@Component
public class FeatureExtractor {

    @Autowired
    private ImageDescriptorService imageDescriptorService;

    @Getter
    @Setter
    private List<String> fileList;

    @Getter
    @Setter
    private String inputDir;

    @Getter
    @Setter
    private String outputDir;

    @Getter
    @Setter
    private String database;

    private static final double KERNEL_SIZE = 0.15;
    private static final String DATABASE_NAME = "/WoamanBustFeatures.xml";
    private static final String AUDIO_FILE_PATH = "/Users/salesfilho/Downloads/patologica1.wav";

    void start() {
        fileList = OCIUtils.getImageFiles(this.inputDir);
        if (database == null || database.isEmpty()) {
            this.database = this.outputDir + this.DATABASE_NAME;
        }
        this.saveFeature();
        //this.plotAudioData(this.audioBytes(), "Magnitude do Sianl de audio (4096 amostras) - Kernel size 0.15");
    }

    private BufferedImage getFirstImageFromList() {
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File(fileList.get(1)));
        } catch (IOException ex) {
            Logger.getLogger(ImageNormalizerService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return img;
    }

    public void saveFeature() {
        System.out.println("Inicio da computação: ");
        long inicio = System.currentTimeMillis();
        long fim = 0;
        
        Map<String, double[]> dataMap = new HashMap<>();
        List<double[]> listMagnitude = new ArrayList<>();

        //double[] feature = imageDescriptorService.magnitude(getFirstImageFromList(), ImageDescriptor.CHANNEL_RED, KERNEL_SIZE);
        double[] data = imageDescriptorService.magnitude(getFirstImageFromList(), ImageDescriptor.CHANNEL_RED, KERNEL_SIZE);
       // listMagnitude.add(data);
        dataMap.put("Red", data);

//        ImageDescriptor descriptor = new ImageDescriptor();
//        descriptor.setId(1);
//        descriptor.setLabel("Label of Image");
//        descriptor.setDescription("Description of  Image");
//        descriptor.setRedChannel(data);
//
//        imageDescriptorService.add(descriptor, ImageDescriptorDatabase.DESCRIPTOR_TYPE_BUST_NON_NUDE);
//        imageDescriptorService.save(new File(this.database));
//
//        dataMap.put("Autocorrentropy", data);
        //data = imageDescriptorService.fft(data, ImageDescriptor.CHANNEL_RED);
        //data = imageDescriptorService.magnitude(getFirstImageFromList(), ImageDescriptor.CHANNEL_GREEN, KERNEL_SIZE);
        //listMagnitude.add(data);
        //dataMap.put("Green", data);

        //data = imageDescriptorService.magnitude(getFirstImageFromList(), ImageDescriptor.CHANNEL_BLUE, KERNEL_SIZE);
        //listMagnitude.add(data);
        //dataMap.put("Blue", data);

        //data = imageDescriptorService.magnitude(getFirstImageFromList(), ImageDescriptor.CHANNEL_GRAYSCALE, KERNEL_SIZE);
        //listMagnitude.add(data);
        //dataMap.put("GrayScale", data);

        fim = System.currentTimeMillis();
        System.out.println("Fim da computação: " + (fim - inicio) + " ms");

        //print graph
        plot(dataMap, "Magnitude do canal RED (Imagem 128x128) - Kernel size 0.15");

        //plot(dataMap, "Magnitude of RGB Channels", "Channels");

    }

    private void plotAudioData(double[] audioBytes, String title) {
        plot(imageDescriptorService.magnitude(audioBytes, KERNEL_SIZE), title, "Magnitude");
    }

    public double[] audioBytes() {

        File file = new File(AUDIO_FILE_PATH);
        AudioInputStream ais = null;
        byte[] data = null;
        try {
            ais = AudioSystem.getAudioInputStream(file);
            data = new byte[ais.available()];
            ais.read(data);
        } catch (UnsupportedAudioFileException | IOException ex) {
            Logger.getLogger(FeatureExtractor.class.getName()).log(Level.SEVERE, null, ex);
        }

        int limit = 4096;
        double[] result = new double[limit];
        for (int i = 0; i < limit; i++) {
            result[i] = (double) data[i];
        }
        return result;
    }

    public void plot(double[] data, String title, String serieName) {

        final GraphicBuilder g1 = new GraphicBuilder(title);
        g1.addSeire(data, serieName);
        g1.createChart();
        g1.pack();
        RefineryUtilities.centerFrameOnScreen(g1);
        g1.setVisible(true);
    }

    public void plot(Map<String, double[]> dataMap, String title, String legend) {
        final GraphicBuilder g1 = new GraphicBuilder(title);
        g1.createCombinedChart(dataMap, legend);
        g1.pack();
        RefineryUtilities.centerFrameOnScreen(g1);
        g1.setVisible(true);

    }

    public void plot(Map<String, double[]> dataMap, String title) {
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

    public byte[] readAudioFileData(final String filePath) {
        byte[] data = null;
        try {
            final ByteArrayOutputStream baout = new ByteArrayOutputStream();
            final File file = new File(filePath);
            final AudioInputStream audioInputStream = AudioSystem
                    .getAudioInputStream(file);

            byte[] buffer = new byte[4096];
            int c;
            while ((c = audioInputStream.read(buffer, 0, buffer.length)) != -1) {
                baout.write(buffer, 0, c);
            }
            audioInputStream.close();
            baout.close();
            data = baout.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    public byte[] readWAVAudioFileData(final String filePath) {
        byte[] data = null;
        try {
            final ByteArrayOutputStream baout = new ByteArrayOutputStream();
            final AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(filePath));

            AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, baout);
            audioInputStream.close();
            baout.close();
            data = baout.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

}
