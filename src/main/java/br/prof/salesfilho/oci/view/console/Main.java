/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.prof.salesfilho.oci.view.console;

import br.prof.salesfilho.oci.service.ImageNormalizerService;
import java.io.FileNotFoundException;
import java.io.IOException;
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
    private ImageNormalizerService imageNormalizer;

    @Autowired
    private ApplicationArguments applicationArguments;
    @Getter
    private PropertySource propertySource;

    private boolean start = true;

    @PostConstruct
    public void init() throws FileNotFoundException, IOException {
        propertySource = new SimpleCommandLinePropertySource(applicationArguments.getSourceArgs());

        if (this.propertySource.containsProperty("normalyze")) {
            this.startNormalyze();
        }
        if (this.propertySource.containsProperty("extractFeatures")) {
            this.extractFeatures();
        }
        if (!(this.propertySource.containsProperty("normalyze") || this.propertySource.containsProperty("extractFeatures"))) {
            this.usage();
        }
    }

    public void startNormalyze() {

        if (this.propertySource.containsProperty("inputDir") && this.propertySource.containsProperty("workDir")) {
            imageNormalizer.setInputDir(this.propertySource.getProperty("inputDir").toString());
            imageNormalizer.setOutputDir(this.propertySource.getProperty("workDir").toString());

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

        if (this.propertySource.containsProperty("workDir")) {
            //imageFeatureExtractor.setWorkDir(this.propertySource.getProperty("workDir").toString());

        } else {
            this.start = false;
        }
        if (this.start) {
            //imageFeatureExtractor.start();
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
        System.out.println("Ex.: normalyze: java -jar oci.jar --normalyze --inputDir=/tmp/in --workDir=/tmp/out");
        System.out.println("Ex.: extractFeatures: java -jar oci.jar --extractFeatures --workDir=/tmp/out");
        System.out.println("Ex.: normalyze and extractFeatures: java -jar oci.jar --normalyze --extractFeatures --inputDir=/tmp/in --workDir=/tmp/out");
        System.out.println("");

        System.out.println("------------------------------------------------------------------------------------");

    }

}
