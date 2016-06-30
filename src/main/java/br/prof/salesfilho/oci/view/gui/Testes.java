/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.prof.salesfilho.oci.view.gui;

/**
 *
 * @author salesfilho
 */
public class Testes {

   // private ImageProcessor imageProcessor;

    public static void main(String[] args) {
        System.out.println("Tested!");
    }
//        Testes t = new Testes();
//        BufferedImage image;
//        try {
//            String path = "/Users/salesfilho/Downloads/database/busto_nu/256x256/";
//            image = ImageIO.read(new File("/Users/salesfilho/Downloads/database/busto_nu/256x256/06.jpg"));
//            t.imageProcessor = new ImageProcessor(image);
//
//            int idx = 0;
//            for (BufferedImage subImage : t.imageProcessor.getSubImages(64)) {
//                ImageIO.write(subImage, "jpg", new File(path.concat("part-" + idx + ".jpg")));
//                idx++;
//            }

//            BufferedImage subImage = image.getSubimage(0, 0, 128, 128);
//            ImageIO.write(subImage, "jpg", new File(path.concat("part-0" + ".jpg")));
//            
//            subImage = image.getSubimage(0, 128, 128, 128);
//            ImageIO.write(subImage, "jpg", new File(path.concat("part-1" + ".jpg")));
//            
//            subImage = image.getSubimage(128, 0, 128, 128);
//            ImageIO.write(subImage, "jpg", new File(path.concat("part-2" + ".jpg")));
//            
//            subImage = image.getSubimage(128, 128, 128, 128);
//            ImageIO.write(subImage, "jpg", new File(path.concat("part-3" + ".jpg")));
//            subImage = image.getSubimage(0, 128, 64, 64);
//            ImageIO.write(subImage, "jpg", new File(path.concat("part-2" + ".jpg")));
//            
//            subImage = image.getSubimage(0, 192, 64, 64);
//            ImageIO.write(subImage, "jpg", new File(path.concat("part-3" + ".jpg")));
//
//            subImage = image.getSubimage(64, 0, 64, 64);
//            ImageIO.write(subImage, "jpg", new File(path.concat("part-4" + ".jpg")));
//
//            subImage = image.getSubimage(64, 64, 64, 64);
//            ImageIO.write(subImage, "jpg", new File(path.concat("part-5" + ".jpg")));
//
//            subImage = image.getSubimage(64, 128, 64, 64);
//            ImageIO.write(subImage, "jpg", new File(path.concat("part-6" + ".jpg")));
//
//            subImage = image.getSubimage(64, 192, 64, 64);
//            ImageIO.write(subImage, "jpg", new File(path.concat("part-7" + ".jpg")));
//
//            t.imageProcessor = new ImageProcessor((BufferedImage) image);
//
//            imageChannel = t.imageProcessor.getRedMatrix();
//
//            partsOfImage = OCIUtils.splitMatrix(imageChannel, 64);
//            int idx = 0;
//            for (double[][] partImage : partsOfImage) {
//                bytesImage = OCIUtils.convertDoubleArray2IntArray(OCIUtils.vetorize(partImage));
//                ImageIO.write((BufferedImage) getImageFromArray(bytesImage, partImage.length, partImage.length), "jpg", new File(path.concat("part-" + idx + ".jpg")));
////                idx++;
////            }
//        } catch (IOException ex) {
//            Logger.getLogger(ImageNormalizerService.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
//        //t.imageProcessor = new ImageProcessor()
//    }
//
//    public static Image getImageFromArray(int[] pixels, int width, int height) {
//        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
//        //WritableRaster raster = (WritableRaster) image.getData();
//        //raster.setPixels(0, 0, width, height, pixels);
//        image.setRGB(0, 0, width, height, pixels, 0, width);
//        return image;
//    }
}
