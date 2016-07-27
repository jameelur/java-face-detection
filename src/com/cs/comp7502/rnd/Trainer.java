package com.cs.comp7502.rnd;

import com.cs.comp7502.ImageUtils;
import com.sun.org.apache.xalan.internal.xsltc.util.IntegerArray;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by rmohamed on 7/26/2016.
 */
public class Trainer {

    public static int FEATURE_TYPE_1 = 1;
    public static ArrayList<HaarFeature> FEATURE_1 = new ArrayList<>(
            Arrays.asList(new HaarFeature(1, 6, 3),
                    new HaarFeature(1, 6, 6))
    );
    public static int FEATURE_TYPE_2 = 2;
    public static ArrayList<HaarFeature> FEATURE_2 = new ArrayList<>(
            Arrays.asList(new HaarFeature(2, 4, 3),
                    new HaarFeature(2, 4, 6))
    );
    public static int FEATURE_TYPE_3 = 3;
    public static ArrayList<HaarFeature> FEATURE_3 = new ArrayList<>(
            Arrays.asList(new HaarFeature(3, 6, 6),
                    new HaarFeature(3, 12, 6))
    );
    public static int FEATURE_TYPE_4 = 4;
    public static ArrayList<HaarFeature> FEATURE_4 = new ArrayList<>(
            Arrays.asList(new HaarFeature(4, 12, 4),
                    new HaarFeature(4, 16, 5),
                    new HaarFeature(4, 20, 6))
    );
    public static int FEATURE_TYPE_5 = 5;
    public static ArrayList<HaarFeature> FEATURE_5 = new ArrayList<>(
            Arrays.asList(new HaarFeature(5, 6, 4),
                    new HaarFeature(5, 8, 6),
                    new HaarFeature(5, 10, 8))
    );

    public static List<WeakHaarClassifier> trainFaces() {
        ArrayList<WeakHaarClassifier> weakHaarClassifiers = new ArrayList<>();
        ArrayList<int[][]> imageArray = new ArrayList<>();

        File folder = new File("res/faces/24by24faces");
//        File folder = new File("res/faces/test");
        File[] files = folder.listFiles();

        for (File file : files) {
            BufferedImage bImage = null;
            try {
                bImage = ImageIO.read(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            imageArray.add(ImageUtils.buildGrayscaleImageArray(bImage));
        }

        long time = System.currentTimeMillis();
        for (int[][] image : imageArray) {
            weakHaarClassifiers.add(new WeakHaarClassifier(train(image, 1)));
        }

        System.out.println("Time Taken to train " + imageArray.size() + " number of image is " + ((System.currentTimeMillis() - time) / 1000.0) + "s");

        return weakHaarClassifiers;
    }

    public static List<HaarFeature> train(int[][] inputI, int type) {
        List<HaarFeature> featureList = new ArrayList<>();

        int h = inputI.length;
        int w = inputI[0].length;
        int[][] integralI = new int[h][w];
        ImageUtils.buildIntegralImage(inputI, integralI, w, h);

        if (type == FEATURE_TYPE_1) {
            for (HaarFeature feature : FEATURE_1) {
                int featW = feature.getWidth();
                int featH = feature.getHeight();
                HaarFeature result = new HaarFeature(1, featW, featH);
                for (int x = 0; x < h - featH; x++) {
                    // assume centered.
                    int y = (integralI[0].length - featW * 2) / 2;
                    // sum1 of pixels on S1

                    int sum1 = ImageUtils.sumIntegralImage(integralI, x, y, featW, featH);
                    int sum2 = ImageUtils.sumIntegralImage(integralI, x, y + featW, featW, featH);

                    result.add(sum1 - sum2);
                }
                featureList.add(result);
            }
        } else if (type == FEATURE_TYPE_2) {
            for (HaarFeature feature : FEATURE_2) {
                int featW = feature.getWidth();
                int featH = feature.getHeight();
                HaarFeature result = new HaarFeature(1, featW, featH);
                for (int x = 0; x < h - featH; x++) {
                    // assume centered.
                    int y = (integralI[0].length - featW * 2) / 2;
                    // sum1 of pixels on S1
                    int sum1 = ImageUtils.sumIntegralImage(integralI, x, y, featW, featH);
                    int sum2 = ImageUtils.sumIntegralImage(integralI, x, y + featW, featW, featH);
                    int sum3 = ImageUtils.sumIntegralImage(integralI, x, y + featW * 2, featW, featH);

                    result.add(sum1 - sum2 + sum3);
                }
                featureList.add(result);
            }
        } else if (type == FEATURE_TYPE_3) {
            for (HaarFeature feature : FEATURE_3) {
                int featW = feature.getWidth();
                int featH = feature.getHeight();
                HaarFeature result = new HaarFeature(3, featW, featH);
                for (int x = 0; x < h - (featH * 2 - 1); x++) {
                    // assume centered.
                    int y = (integralI[0].length - featW * 2) / 2;
                    // sum1 of pixels on S1

                    int sum1 = ImageUtils.sumIntegralImage(integralI, x, y, featW, featH);
                    int sum2 = ImageUtils.sumIntegralImage(integralI, x + featH, y, featW, featH);

                    result.add(sum1 - sum2);
                }
                featureList.add(result);
            }
        } else if (type == FEATURE_TYPE_4){
            for (HaarFeature feature : FEATURE_4) {
                int featW = feature.getWidth();
                int featH = feature.getHeight();
                HaarFeature result = new HaarFeature(1, featW, featH);
                for (int x = 0; x < h - (featH * 3 - 1); x++) {
                    // assume centered.
                    int y = (integralI[0].length - featW * 2) / 2;

                    int sum1 = ImageUtils.sumIntegralImage(integralI, x, y, featW, featH);
                    int sum2 = ImageUtils.sumIntegralImage(integralI, x + featH, y, featW, featH);
                    int sum3 = ImageUtils.sumIntegralImage(integralI, x + featH + featH, y, featW, featH);

                    result.add(sum1 - sum2 + sum3);
                }
                featureList.add(result);
            }
        } else if (type == FEATURE_TYPE_5) {
            for (HaarFeature feature : FEATURE_5) {
                int featW = feature.getWidth();
                int featH = feature.getHeight();
                HaarFeature result = new HaarFeature(1, featW, featH);
                for (int x = 0; x < h - (featH * 2 - 1); x++) {
                    // assume centered.
                    int y = (integralI[0].length - featW * 2) / 2;

                    int sum1 = ImageUtils.sumIntegralImage(integralI, x, y, featW, featH);
                    int sum2 = ImageUtils.sumIntegralImage(integralI, x + featH, y, featW, featH);
                    int sum3 = ImageUtils.sumIntegralImage(integralI, x, y + featW, featW, featH);
                    int sum4 = ImageUtils.sumIntegralImage(integralI, x + featH, y + featW, featW, featH);

                    result.add(sum1 - sum2 - sum3 + sum4);
                }
                featureList.add(result);
            }
        }
        return featureList;
    }
}
