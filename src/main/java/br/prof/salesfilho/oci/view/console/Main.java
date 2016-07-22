/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.prof.salesfilho.oci.view.console;

import br.prof.salesfilho.oci.service.BodyWomanDescriptorService;
import br.prof.salesfilho.oci.service.BodyWomanNudeClassifier;
import br.prof.salesfilho.oci.service.ImageNormalizerService;
import br.prof.salesfilho.oci.service.ImageProcessorService;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.SimpleCommandLinePropertySource;
import org.springframework.stereotype.Component;

/**
 *
 * @author salesfilho
 */
@Component
public class Main {

    @Autowired
    private BodyWomanDescriptorService bodyWomanDescriptorService;

    @Autowired
    private ImageProcessorService imageProcessorService;

    @Autowired
    private ImageNormalizerService imageNormalizer;

    @Autowired
    private BodyWomanNudeClassifier bodyWomanNudeClassifier;

    @Autowired
    private ApplicationArguments applicationArguments;
    @Getter
    private PropertySource propertySource;

    private static final double KERNEL_SIZE = 0.15;

    private boolean start = true;

    @PostConstruct
    public void init() throws FileNotFoundException, IOException {
        propertySource = new SimpleCommandLinePropertySource(applicationArguments.getSourceArgs());

        if (this.propertySource.containsProperty("classify")) {
            this.classify();
        }
        if (this.propertySource.containsProperty("normalyze")) {
            this.startNormalyze();
        }
        if (this.propertySource.containsProperty("extractFeatures")) {
            this.extractFeatures();
        }
        if (!(this.propertySource.containsProperty("classify")
                || this.propertySource.containsProperty("normalyze")
                || this.propertySource.containsProperty("extractFeatures"))) {
            this.usage();
        }
    }

    public void startNormalyze() {

        if (this.propertySource.containsProperty("inputDir") && this.propertySource.containsProperty("outputDir")) {
            imageNormalizer.setInputDir(this.propertySource.getProperty("inputDir").toString());
            imageNormalizer.setOutputDir(this.propertySource.getProperty("outputDir").toString());

        } else {
            this.start = false;
        }
        if (this.start) {
            imageNormalizer.start();
        } else {
            usage();
        }
    }

    public void extractFeatures() {

        if (this.propertySource.containsProperty("inputDir") && this.propertySource.containsProperty("outputDir")) {

            //Create new thread pool to each image file
            ExecutorService executor = Executors.newFixedThreadPool(2);

            BodyWomanFeatureExtractorExecutor e1 = new BodyWomanFeatureExtractorExecutor(bodyWomanDescriptorService, imageProcessorService, true);
            e1.setInputDir(this.propertySource.getProperty("inputDir").toString());
            e1.setOutputDir(this.propertySource.getProperty("outputDir").toString());
            e1.setKernelSize(Double.valueOf(this.propertySource.getProperty("kernelsize").toString()));
            e1.setDatabaseName(this.propertySource.getProperty("databaseName").toString());

            executor.execute(e1);

            BodyWomanFeatureExtractorExecutor e2 = new BodyWomanFeatureExtractorExecutor(bodyWomanDescriptorService, imageProcessorService, false);

            e2.setInputDir(this.propertySource.getProperty("inputDir").toString());
            e2.setOutputDir(this.propertySource.getProperty("outputDir").toString());
            e2.setKernelSize(Double.valueOf(this.propertySource.getProperty("kernelsize").toString()));
            e2.setDatabaseName(this.propertySource.getProperty("databaseName").toString());

            executor.execute(e2);

            //Wait finish
            executor.shutdown();
            while (!executor.isTerminated()) {
            }
            File f = new File(e1.getDatabaseName());
            bodyWomanDescriptorService.openDatabase(f);
            bodyWomanDescriptorService.add(e1.getBodyWomanDescriptor());
            bodyWomanDescriptorService.add(e2.getBodyWomanDescriptor());
            bodyWomanDescriptorService.save(f);

        } else {
            usage();
        }
    }

    public void classify() {

        if (this.propertySource.containsProperty("inputDir")) {
            bodyWomanNudeClassifier.setInputDir(this.propertySource.getProperty("inputDir").toString());
            bodyWomanNudeClassifier.setKernelSize(Double.valueOf(this.propertySource.getProperty("kernelsize").toString()));
            bodyWomanNudeClassifier.setDatabaseName(this.propertySource.getProperty("databaseName").toString());
        } else {
            this.start = false;
        }
        if (this.start) {
            bodyWomanNudeClassifier.start();
        } else {
            usage();
        }
    }

    private void usage() {
        System.out.println("------------------------------------------------------------------------------------");
        System.out.println("");
        System.out.println("Usage: java -jar oci<version>.jar options");
        System.out.println("Main Options: --normalyze && --extractFeatures");
        System.out.println("");
        System.out.println("Ex.: normalyze: java -jar oci.jar --normalyze --inputDir=/tmp/in --outputDir=/tmp/out");
        System.out.println("Ex.: extractFeatures: java -jar oci.jar --extractFeatures kernelsize=0.15 --type=1 --inputDir=/tmp/in --outputDir=/tmp/out");
        System.out.println("Ex.: normalyze and extractFeatures: java -jar oci.jar --normalyze --extractFeatures --inputDir=/tmp/in --outputDir=/tmp/out");
        System.out.println("");
        System.out.println("------------------------------------------------------------------------------------");

    }

}
