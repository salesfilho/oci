/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.prof.salesfilho.oci.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author salesfilho
 */
@NoArgsConstructor
@EqualsAndHashCode
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
    private double[] redAvgChannel;

    @Getter
    @Setter
    private double[] greenAvgChannel;

    @Getter
    @Setter
    private double[] blueAvgChannel;

    @Getter
    @Setter
    private double[] avgRgbChannel;

    @Getter
    @Setter
    private double[] grayScaleAvgChannel;
    

    public static final int CHANNEL_RED = 1;
    public static final int CHANNEL_GREEN = 2;
    public static final int CHANNEL_BLUE = 3;
    public static final int CHANNEL_GRAYSCALE = 3;
    
    public ImageDescriptor(int id, String label, double[] redChannel, double[] greenChannel, double[] blueChannel, double[] rgbChannel, double[] grayScaleChannel) {
        this.id = id;
        this.label = label;
        this.redAvgChannel = redChannel;
        this.greenAvgChannel = greenChannel;
        this.blueAvgChannel = blueChannel;
        this.avgRgbChannel = rgbChannel;
        this.grayScaleAvgChannel = grayScaleChannel;
    }
}
