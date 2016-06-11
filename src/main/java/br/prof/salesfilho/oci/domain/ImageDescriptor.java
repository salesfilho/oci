/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.prof.salesfilho.oci.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author salesfilho
 */
@NoArgsConstructor
public class ImageDescriptor {

    @Getter
    @Setter
    private int id;

    @Getter
    @Setter
    private String label;

    @Getter
    @Setter
    private String description;

    @Getter
    @Setter
    private double[] redChannel;

    @Getter
    @Setter
    private double[] greenChannel;

    @Getter
    @Setter
    private double[] blueChannel;

    @Getter
    @Setter
    private double[] rgbChannel;

    @Getter
    @Setter
    private double[] grayScaleChannel;

    public ImageDescriptor(int id, String label, double[] redChannel, double[] greenChannel, double[] blueChannel, double[] rgbChannel, double[] grayScaleChannel) {
        this.id = id;
        this.label = label;
        this.redChannel = redChannel;
        this.greenChannel = greenChannel;
        this.blueChannel = blueChannel;
        this.rgbChannel = rgbChannel;
        this.grayScaleChannel = grayScaleChannel;
    }
}
