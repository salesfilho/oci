/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.prof.salesfilho.oci.image;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

/**
 *
 * @author salesfilho
 */
@Component
@NoArgsConstructor
public class ImageProcessor {

    @Getter
    @Setter
    private BufferedImage image;

    public ImageProcessor(BufferedImage srcImage) {
        this.image = srcImage;
    }

    public double[] getRed() {
        int[] colors = new int[this.image.getWidth() * this.image.getHeight()];
        this.image.getRGB(0, 0, this.image.getWidth(), this.image.getHeight(), colors, 0, this.image.getWidth());

        double[] red = new double[colors.length];

        for (int i = 0; i < colors.length; i++) {
            Color color = new Color(colors[i]);
            int rgb = color.getRGB();
            int r = (rgb >> 16) & 0xFF;
            red[i] = (double) r;
        }
        return red;
    }

    public double[] getGreen() {
        int[] colors = new int[this.image.getWidth() * this.image.getHeight()];
        this.image.getRGB(0, 0, this.image.getWidth(), this.image.getHeight(), colors, 0, this.image.getWidth());

        double[] green = new double[colors.length];

        for (int i = 0; i < colors.length; i++) {
            Color color = new Color(colors[i]);
            int rgb = color.getRGB();
            int g = (rgb >> 8) & 0xFF;
            green[i] = (double) g;
        }
        return green;
    }

    public double[] getBlue() {
        int[] colors = new int[this.image.getWidth() * this.image.getHeight()];
        this.image.getRGB(0, 0, this.image.getWidth(), this.image.getHeight(), colors, 0, this.image.getWidth());

        double[] blue = new double[colors.length];

        for (int i = 0; i < colors.length; i++) {
            Color color = new Color(colors[i]);
            int rgb = color.getRGB();
            int b = (rgb & 0xFF);
            blue[i] = (double) b;
        }
        return blue;
    }

    public double[] getGrayScale() {
        int[] colors = new int[this.image.getWidth() * this.image.getHeight()];
        this.image.getRGB(0, 0, this.image.getWidth(), this.image.getHeight(), colors, 0, this.image.getWidth());

        double[] grayScale = new double[colors.length];

        for (int i = 0; i < colors.length; i++) {
            Color color = new Color(colors[i]);

            int rgb = color.getRGB();
            int r = (rgb >> 16) & 0xFF;
            int g = (rgb >> 8) & 0xFF;
            int b = (rgb & 0xFF);

            int grayLevel = (r + g + b) / 3;

            grayScale[i] = (double) grayLevel;
        }
        return grayScale;
    }

    public double[][] getRedMatrix() {
        double[][] resultMatrix = new double[this.image.getWidth()][this.image.getHeight()];
        double[] red = getRed();
        int idx = 0;
        for (int x = 0; x < this.image.getWidth(); x++) {
            for (int y = 0; y < this.image.getHeight(); y++) {
                resultMatrix[x][y] = red[idx];
                idx++;
            }
        }
        return resultMatrix;
    }

    public double[][] getGreenMatrix() {
        double[][] resultMatrix = new double[this.image.getWidth()][this.image.getHeight()];
        double[] green = getGreen();
        int idx = 0;
        for (int x = 0; x < this.image.getWidth(); x++) {
            for (int y = 0; y < this.image.getHeight(); y++) {
                resultMatrix[x][y] = green[idx];
                idx++;
            }
        }
        return resultMatrix;
    }

    public double[][] getBlueMatrix() {
        double[][] resultMatrix = new double[this.image.getWidth()][this.image.getHeight()];
        double[] blue = getBlue();
        int idx = 0;
        for (int x = 0; x < this.image.getWidth(); x++) {
            for (int y = 0; y < this.image.getHeight(); y++) {
                resultMatrix[x][y] = blue[idx];
                idx++;
            }
        }
        return resultMatrix;
    }

    public double[][] getGrayScaleMatrix() {
        double[][] resultMatrix = new double[this.image.getWidth()][this.image.getHeight()];
        double[] grayScale = getGrayScale();
        int idx = 0;
        for (int x = 0; x < this.image.getWidth(); x++) {
            for (int y = 0; y < this.image.getHeight(); y++) {
                resultMatrix[x][y] = grayScale[idx];
                idx++;
            }
        }
        return resultMatrix;
    }

    public BufferedImage resize(int newWidth, int newHeigth) {
        Image tmp = image.getScaledInstance(newWidth, newHeigth, Image.SCALE_SMOOTH);
        BufferedImage dimg = new BufferedImage(newWidth, newHeigth, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = dimg.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();
        this.image = dimg;
        return this.image;
    }

    public List<BufferedImage> getSubImages(int size) {

        if (this.image.getWidth() % size != 0 || this.image.getHeight() % size != 0 ) {
            throw new ArrayIndexOutOfBoundsException("Image size and input size cause array index out of bounds!");
        }

        List<BufferedImage> result = new ArrayList<>();
        BufferedImage subImage;
        for (int i = 0; i < this.image.getWidth(); i = i + size) {
            for (int j = 0; j < this.image.getHeight(); j = j + size) {
                subImage = image.getSubimage(i, j, size, size);
                result.add(subImage);
            }
        }
        return result;
    }

}
