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
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author salesfilho
 */
@Getter
@Setter
public class ImageRgbProcessor {

    private BufferedImage image;

    public ImageRgbProcessor(BufferedImage srcImage) {
        this.image = srcImage;
    }

    public int[] getRed() {
        int[] colors = new int[this.image.getWidth() * this.image.getHeight()];
        this.image.getRGB(0, 0, this.image.getWidth(), this.image.getHeight(), colors, 0, this.image.getWidth());

        int[] red = new int[colors.length];

        for (int i = 0; i < colors.length; i++) {
            Color color = new Color(colors[i]);
            int rgb = color.getRGB();
            int r = (rgb >> 16) & 0xFF;
            red[i] = r;
        }
        return red;
    }

    public int[] getGreen() {
        int[] colors = new int[this.image.getWidth() * this.image.getHeight()];
        this.image.getRGB(0, 0, this.image.getWidth(), this.image.getHeight(), colors, 0, this.image.getWidth());

        int[] green = new int[colors.length];

        for (int i = 0; i < colors.length; i++) {
            Color color = new Color(colors[i]);
            int rgb = color.getRGB();
            int g = (rgb >> 8) & 0xFF;
            green[i] = g;
        }
        return green;
    }

    public int[] getBlue() {
        int[] colors = new int[this.image.getWidth() * this.image.getHeight()];
        this.image.getRGB(0, 0, this.image.getWidth(), this.image.getHeight(), colors, 0, this.image.getWidth());

        int[] blue = new int[colors.length];

        for (int i = 0; i < colors.length; i++) {
            Color color = new Color(colors[i]);
            int rgb = color.getRGB();
            int b = (rgb & 0xFF);
            blue[i] = b;
        }
        return blue;
    }

    public int[] getGrayScale() {
        int[] colors = new int[this.image.getWidth() * this.image.getHeight()];
        this.image.getRGB(0, 0, this.image.getWidth(), this.image.getHeight(), colors, 0, this.image.getWidth());

        int[] grayScale = new int[colors.length];

        for (int i = 0; i < colors.length; i++) {
            Color color = new Color(colors[i]);

            int rgb = color.getRGB();
            int r = (rgb >> 16) & 0xFF;
            int g = (rgb >> 8) & 0xFF;
            int b = (rgb & 0xFF);

            int grayLevel = (r + g + b) / 3;
            int gray = (grayLevel << 16) + (grayLevel << 8) + grayLevel;

            //grayScale[i] = gray;
            grayScale[i] = grayLevel;
        }
        return grayScale;
    }

    public int[][] getRedMatrix() {
        int[][] resultMatrix = new int[this.image.getWidth()][this.image.getHeight()];
        int[] red = getRed();
        int idx = 0;
        for (int x = 0; x < this.image.getWidth(); x++) {
            for (int y = 0; y < this.image.getHeight(); y++) {
                resultMatrix[x][y] = red[idx];
                idx++;
            }
        }
        return resultMatrix;
    }

    public int[][] getGreenMatrix() {
        int[][] resultMatrix = new int[this.image.getWidth()][this.image.getHeight()];
        int[] green = getGreen();
        int idx = 0;
        for (int x = 0; x < this.image.getWidth(); x++) {
            for (int y = 0; y < this.image.getHeight(); y++) {
                resultMatrix[x][y] = green[idx];
                idx++;
            }
        }
        return resultMatrix;
    }

    public int[][] getBlueMatrix() {
        int[][] resultMatrix = new int[this.image.getWidth()][this.image.getHeight()];
        int[] blue = getBlue();
        int idx = 0;
        for (int x = 0; x < this.image.getWidth(); x++) {
            for (int y = 0; y < this.image.getHeight(); y++) {
                resultMatrix[x][y] = blue[idx];
                idx++;
            }
        }
        return resultMatrix;
    }

    public int[][] getGrayScaleMatrix() {
        int[][] resultMatrix = new int[this.image.getWidth()][this.image.getHeight()];
        int[] grayScale = getGrayScale();
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

}
