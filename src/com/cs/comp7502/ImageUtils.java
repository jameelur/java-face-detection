package com.cs.comp7502;

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
            }
        }

    }
}
