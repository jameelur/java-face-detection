package com.cs.comp7502;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;

/**
 * Created by rmohamed on 7/26/2016.
 */
public class ImageUtils {
    public static void buildIntegralImage(int[][] image, int[][] integral, int w, int h) {
        for(int x = 0; x < h; x++){
            for (int y = 0; y < w; y++){
                int g = image[x][y];

                integral[x][y] = g;
                integral[x][y] += x > 0 ? integral[x - 1][y] : 0;
                integral[x][y] += y > 0 ? integral[x][y - 1] : 0;
                integral[x][y] -= x > 0 && y > 0 ? integral[x-1][y - 1] : 0;
            }
        }
    }

    public static int[][] buildImageArray(BufferedImage input, boolean isGrayscale){
        int w = input.getWidth();
        int h = input.getHeight();
        int image[][] = new int[h][w];
        if (isGrayscale) {
            Raster raster = input.getData();
            for (int x = 0; x < h; x++) {
                for (int y = 0; y < w; y++) {
                    image[x][y] = raster.getSample(y, x, 0);
                }
            }
        } else {
            for (int x = 0; x < h; x++) {
                for (int y = 0; y < w; y++) {
                    int rgb = input.getRGB(y, x);
                    image[x][y] = ColourUtils.convertToG((rgb & 0x00ff0000) >> 16, (rgb & 0x0000ff00) >> 8, (rgb & 0x000000ff), ColourUtils.Grayscale.LUMINANCE);
                }
            }
        }
        return image;
    }

    public static int sumIntegralImage(int[][] integralI, int x, int y, int w, int h) {

        int endX = x + h - 1;
        int endY = y + w - 1;

        int sum = integralI[endX][endY];
        sum -= x > 0 ? integralI[x - 1][endY] : 0;
        sum -= y > 0 ? integralI[endX][y - 1] : 0;
        sum += x > 0 && y > 0 ? integralI[x - 1][y - 1] : 0;

        return sum;
    }
}
