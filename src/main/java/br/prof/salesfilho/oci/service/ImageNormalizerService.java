/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.prof.salesfilho.oci.service;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
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
    private static final double KERNEL_SIZE = 0.5;

    public void start() {
        System.out.println("ImageNormalizer started.");

    }
}
