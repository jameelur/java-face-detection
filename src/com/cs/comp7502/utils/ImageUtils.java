package com.cs.comp7502.utils;

import com.cs.comp7502.utils.ColourUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.util.*;
import java.util.List;

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

    public static List<Rectangle> mergeWindows(List<Rectangle> rectangles, double overlap, int threshold) {
        List<Rectangle> output = new ArrayList<>();
        Stack<Stack<Rectangle>> partitions = new Stack<>();

        Stack<Rectangle> rects = new Stack<>();
        rects.addAll(rectangles);

        for (Rectangle r1 : rectangles){
            int r1Area = r1.width * r1.height;
            rects.pop();
            
            if (partitions.isEmpty()) {
                Stack<Rectangle> stack = new Stack<>();
                stack.push(r1);
                partitions.push(stack);
            } else {
                boolean merge = false;
                for (int i = 0; i < partitions.size(); i++) {
                    for (int j = 0; j < partitions.get(i).size(); j++) {
                        Rectangle r2 = partitions.get(i).get(j);
                        int r2Area = r2.width * r2.height;
                        Rectangle intersect = r1.intersection(r2);
                        int intersectArea = intersect.width * intersect.height;

                        if ((r1Area == r2Area) && (intersectArea >= overlap * r1Area))
                            merge = true;
                        else if ((r1Area < r2Area) && (intersectArea >= overlap * r1Area))
                            merge = true;
                        else if ((r2Area < r1Area) && (intersectArea >= overlap * r2Area))
                            merge = true;

                        if (merge) break;
                    }
                    if (merge) {
                        partitions.get(i).push(r1);
                        break;
                    }
                }
                if (!merge) {
                    Stack<Rectangle> stack = new Stack<>();
                    stack.push(r1);
                    partitions.push(stack);
                }


            }
        }

        for (int i = 0; i < partitions.size(); i++) {
            if (partitions.get(i).size() <= threshold)
                continue;

            Rectangle merged = partitions.get(i).get(0);
            for (int j = 1; j < partitions.get(i).size(); j++) {
                merged.add(partitions.get(i).get(j));
            }

            output.add(merged);

        }

        return output;
    }
}
