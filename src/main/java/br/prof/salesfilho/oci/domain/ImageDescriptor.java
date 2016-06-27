/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.prof.salesfilho.oci.domain;

import java.io.Serializable;
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
@Getter
@Setter
public class ImageDescriptor implements Serializable {

    private double[] redChannel;

    private double[] greenChannel;

    private double[] blueChannel;

    private double[] avgChannel;

    private double[] grayScaleChannel;

    public static final int CHANNEL_RED = 1;
    public static final int CHANNEL_GREEN = 2;
    public static final int CHANNEL_BLUE = 3;
    public static final int CHANNEL_GRAYSCALE = 4;

    public ImageDescriptor(double[] redChannel, double[] greenChannel, double[] blueChannel, double[] rgbChannel, double[] grayScaleChannel) {
        this.redChannel = redChannel;
        this.greenChannel = greenChannel;
        this.blueChannel = blueChannel;
        this.avgChannel = rgbChannel;
        this.grayScaleChannel = grayScaleChannel;
    }
}
