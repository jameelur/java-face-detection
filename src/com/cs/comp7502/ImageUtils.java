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

    public static int[][] buildGrayscaleImageArray(BufferedImage bImage){
        Raster raster = bImage.getData();
        int w = raster.getWidth();
        int h = raster.getHeight();
        int image[][] = new int[w][h];
        for (int x = 0; x < h; x++) {
            for (int y = 0; y < w; y++) {
                image[x][y] = raster.getSample(y, x, 0);
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
