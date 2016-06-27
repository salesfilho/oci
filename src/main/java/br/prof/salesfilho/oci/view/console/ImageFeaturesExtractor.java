/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.prof.salesfilho.oci.view.console;

import br.prof.salesfilho.oci.domain.ImageDescriptor;
import br.prof.salesfilho.oci.service.ImageDescriptorService;
import br.prof.salesfilho.oci.service.ImageNormalizerService;
import br.prof.salesfilho.oci.util.OCIUtils;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author salesfilho
 */
@Component
public class ImageFeaturesExtractor {

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
    private String databaseName;

    @Getter
    @Setter
    private double kernelSize;

    @Getter
    @Setter
    private int descriptorType;

    private static final double DEFAULT_KERNEL_SIZE = 0.15;
    private static final int DEFAULT_DESCRITOR_TYPE = 1; //Nude bust

    void start() {
        fileList = OCIUtils.getImageFiles(this.inputDir);
        if (databaseName == null || databaseName.isEmpty()) {
            this.databaseName = this.outputDir + "/" + new SimpleDateFormat("yyyy-MM-dd-HH-mm").format(new Date()) + ".xml";
        } else {
            this.databaseName = this.outputDir + "/" + this.databaseName;
        }
        if (kernelSize <= 0 || kernelSize > 10) {
            this.kernelSize = DEFAULT_KERNEL_SIZE;
        }
        if (descriptorType == 0 || descriptorType > 2) {
            this.kernelSize = DEFAULT_DESCRITOR_TYPE;
        }
        this.extractFeatures();
    }

    public void extractFeatures() {
        long startTime = System.currentTimeMillis();
        long endTime;
        String label = (descriptorType == 1 ? "Woman nude bust" : "Woman no-nude bust");
        List<double[]> listRedChannelFeatures = new ArrayList<>();
        List<double[]> listGreenChannelFeatures = new ArrayList<>();
        List<double[]> listBlueChannelFeatures = new ArrayList<>();
        List<double[]> listGrayScaleChannelFeatures = new ArrayList<>();
        List<double[]> listRgbAvgFeatures = new ArrayList<>();

        List<double[]> listRgbAvgTmpFeatures = new ArrayList<>();

        imageDescriptorService.openDatabase(new File(this.databaseName));
        for (String imagePath : fileList) {
            try {
                System.out.println("Extracting features from: ".concat(imagePath));
                BufferedImage img = ImageIO.read(new File(imagePath));

                double[] dataRedChannel = imageDescriptorService.magnitude(img, ImageDescriptor.CHANNEL_RED, this.kernelSize);
                double[] dataGreenChannel = imageDescriptorService.magnitude(img, ImageDescriptor.CHANNEL_GREEN, this.kernelSize);
                double[] dataBlueChannel = imageDescriptorService.magnitude(img, ImageDescriptor.CHANNEL_BLUE, this.kernelSize);
                double[] dataGrayScaleChannel = imageDescriptorService.magnitude(img, ImageDescriptor.CHANNEL_GRAYSCALE, this.kernelSize);

                listRedChannelFeatures.add(dataRedChannel);
                listGreenChannelFeatures.add(dataGreenChannel);
                listBlueChannelFeatures.add(dataBlueChannel);

                listGrayScaleChannelFeatures.add(dataGrayScaleChannel);

                listRgbAvgTmpFeatures.add(dataRedChannel);
                listRgbAvgTmpFeatures.add(dataGreenChannel);
                listRgbAvgTmpFeatures.add(dataBlueChannel);

                System.out.println("Compute AVG for RGB Channel");
                
                double[] dataAvgRgbChannels = OCIUtils.avarage(listRgbAvgTmpFeatures);

                listRgbAvgFeatures.add(dataAvgRgbChannels);

                listRgbAvgTmpFeatures.clear();

            } catch (IOException ex) {
                Logger.getLogger(ImageNormalizerService.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

        /* Populate ImageDescriptor Object */
        ImageDescriptor descriptor = new ImageDescriptor();
        
        descriptor.setRedChannel(OCIUtils.avarage(listRedChannelFeatures));
        descriptor.setGreenChannel(OCIUtils.avarage(listGreenChannelFeatures));
        descriptor.setBlueChannel(OCIUtils.avarage(listBlueChannelFeatures));
        descriptor.setGrayScaleChannel(OCIUtils.avarage(listGrayScaleChannelFeatures));
        descriptor.setAvgChannel(OCIUtils.avarage(listRgbAvgFeatures));

        //Save to XML Database 
        imageDescriptorService.add(descriptor, descriptorType);
        imageDescriptorService.save(new File(this.databaseName));

        endTime = System.currentTimeMillis();
        System.out.println("Total process time: " + (endTime - startTime) + " ms" + ", AVG: " + ((endTime - startTime) / fileList.size()) + " ms");
        System.out.println("Save time: " + (endTime - startTime) + " ms");
    }

}
