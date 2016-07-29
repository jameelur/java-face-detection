package com.cs.comp7502.rnd;

import com.cs.comp7502.ImageUtils;
import com.cs.comp7502.data.WeakClassifier;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by rmohamed on 7/26/2016.
 */
public class Trainer {

    public static final int ORIGINAL_WINDOW_SIZE = 24;

    public static int FEATURE_TYPE_1 = 1;
    public static ArrayList<WHaarClassifier> FEATURE_1 = new ArrayList<>(
            Arrays.asList(new WHaarClassifier(1, 1, 6, 3),
                    new WHaarClassifier(1, 2, 6, 6))
    );
    public static int FEATURE_TYPE_2 = 2;
    public static ArrayList<WHaarClassifier> FEATURE_2 = new ArrayList<>(
            Arrays.asList(new WHaarClassifier(2, 1, 4, 3),
                    new WHaarClassifier(2, 2, 4, 6))
    );
    public static int FEATURE_TYPE_3 = 3;
    public static ArrayList<WHaarClassifier> FEATURE_3 = new ArrayList<>(
            Arrays.asList(new WHaarClassifier(3, 1, 6, 6),
                    new WHaarClassifier(3, 2, 12, 6))
    );
    public static int FEATURE_TYPE_4 = 4;
    public static ArrayList<WHaarClassifier> FEATURE_4 = new ArrayList<>(
            Arrays.asList(new WHaarClassifier(4, 1, 12, 4),
                    new WHaarClassifier(4, 2, 16, 5),
                    new WHaarClassifier(4, 3, 20, 6))
    );
    public static int FEATURE_TYPE_5 = 5;
    public static ArrayList<WHaarClassifier> FEATURE_5 = new ArrayList<>(
            Arrays.asList(new WHaarClassifier(5, 1, 6, 4),
                    new WHaarClassifier(5, 2, 8, 6),
                    new WHaarClassifier(5, 3, 10, 8))
    );

    public static Map<String, List<WHaarClassifier>> trainFaces() {
        List<WHaarClassifier> trainedClassifierList = new ArrayList<>();
        List<int[][]> imageArray = new ArrayList<>();

        File folder = new File("../24by24faces");
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
        // for each face image in the training data set
        for (int[][] image : imageArray) {
            trainedClassifierList.addAll(train(image));
        }

        Map<String, List<WHaarClassifier>> trainedClassifierMap = convertClassifierListToMap(trainedClassifierList);

        System.out.println("Time Taken to train " + imageArray.size() + " number of image is " + ((System.currentTimeMillis() - time) / 1000.0) + "s");

        return trainedClassifierMap;
    }

    public static Map<String, List<WHaarClassifier>> convertClassifierListToMap(List<WHaarClassifier> classifierList){
        Map<String, List<WHaarClassifier>> classifierMap = new HashMap<>();
        for (WHaarClassifier classifier: classifierList) {
            List<WHaarClassifier> tempList = classifierMap.get(classifier.getKey());
            if (tempList == null) tempList = new ArrayList<>();
            tempList.add(classifier);
            classifierMap.put(classifier.getKey(), tempList);
        }

        return classifierMap;
    }

    public static List<WHaarClassifier> train(int[][] image) {
        List<WHaarClassifier> weakHaarClassifiers = new ArrayList<>();
        // for each of the 5 features
        int h = image.length;
        int w = image[0].length;
        int[][] integralI = new int[h][w];
        ImageUtils.buildIntegralImage(image, integralI, w, h);
        for (int i = 1; i < 6; i++){
            // collect the resultant classifiers in a list
            weakHaarClassifiers.addAll(train(integralI, i));
        }
        return weakHaarClassifiers;
    }

    public static List<WHaarClassifier> train(int[][] integralI, int type) {
        List<WHaarClassifier> featureList = new ArrayList<>();

        int h = integralI.length;
//        int w = inputI[0].length;
//        int[][] integralI = new int[h][w];
//        ImageUtils.buildIntegralImage(inputI, integralI, w, h);

        int windowScale = h / ORIGINAL_WINDOW_SIZE;

        if (type == FEATURE_TYPE_1) {
            int i = 1;
            for (WHaarClassifier feature : FEATURE_1) {
                int featW = feature.getWidth() * windowScale;
                int featH = feature.getHeight() * windowScale;
                WHaarClassifier result = new WHaarClassifier(1, i, featW, featH);
                for (int x = 0; x < h - featH; x+=windowScale) {
                    // assume centered.
                    int y = (integralI[0].length - featW * 2) / 2;

                    int sum1 = ImageUtils.sumIntegralImage(integralI, x, y, featW, featH);
                    int sum2 = ImageUtils.sumIntegralImage(integralI, x, y + featW, featW, featH);

                    result.add(sum1 - sum2);
                }
                featureList.add(result);
                i++;
            }
        } else if (type == FEATURE_TYPE_2) {
            int i = 1;
            for (WHaarClassifier feature : FEATURE_2) {
                int featW = feature.getWidth() * windowScale;
                int featH = feature.getHeight() * windowScale;
                WHaarClassifier result = new WHaarClassifier(2, i, featW, featH);
                for (int x = 0; x < h - featH; x+=windowScale) {
                    // assume centered.
                    int y = (integralI[0].length - featW * 2) / 2;
                    // sum1 of pixels on S1
                    int sum1 = ImageUtils.sumIntegralImage(integralI, x, y, featW, featH);
                    int sum2 = ImageUtils.sumIntegralImage(integralI, x, y + featW, featW, featH);
                    int sum3 = ImageUtils.sumIntegralImage(integralI, x, y + featW * 2, featW, featH);

                    result.add(sum1 - sum2 + sum3);
                }
                featureList.add(result);
                i++;
            }
        } else if (type == FEATURE_TYPE_3) {
            int i = 1;
            for (WHaarClassifier feature : FEATURE_3) {
                int featW = feature.getWidth() * windowScale;
                int featH = feature.getHeight() * windowScale;
                WHaarClassifier result = new WHaarClassifier(3, i, featW, featH);
                for (int x = 0; x < h - (featH * 2 - 1); x+=windowScale) {
                    // assume centered.
                    int y = (integralI[0].length - featW * 2) / 2;
                    // sum1 of pixels on S1

                    int sum1 = ImageUtils.sumIntegralImage(integralI, x, y, featW, featH);
                    int sum2 = ImageUtils.sumIntegralImage(integralI, x + featH, y, featW, featH);

                    result.add(sum1 - sum2);
                }
                featureList.add(result);
                i++;
            }
        } else if (type == FEATURE_TYPE_4){
            int i = 1;
            for (WHaarClassifier feature : FEATURE_4) {
                int featW = feature.getWidth() * windowScale;
                int featH = feature.getHeight() * windowScale;
                WHaarClassifier result = new WHaarClassifier(4, i, featW, featH);
                for (int x = 0; x < h - (featH * 3 - 1); x+=windowScale) {
                    // assume centered.
                    int y = (integralI[0].length - featW * 2) / 2;

                    int sum1 = ImageUtils.sumIntegralImage(integralI, x, y, featW, featH);
                    int sum2 = ImageUtils.sumIntegralImage(integralI, x + featH, y, featW, featH);
                    int sum3 = ImageUtils.sumIntegralImage(integralI, x + featH + featH, y, featW, featH);

                    result.add(sum1 - sum2 + sum3);
                }
                featureList.add(result);
                i++;
            }
        } else if (type == FEATURE_TYPE_5) {
            int i = 1;
            for (WHaarClassifier feature : FEATURE_5) {
                int featW = feature.getWidth() * windowScale;
                int featH = feature.getHeight() * windowScale;
                WHaarClassifier result = new WHaarClassifier(5, i, featW, featH);
                for (int x = 0; x < h - (featH * 2 - 1); x+=windowScale) {
                    // assume centered.
                    int y = (integralI[0].length - featW * 2) / 2;

                    int sum1 = ImageUtils.sumIntegralImage(integralI, x, y, featW, featH);
                    int sum2 = ImageUtils.sumIntegralImage(integralI, x + featH, y, featW, featH);
                    int sum3 = ImageUtils.sumIntegralImage(integralI, x, y + featW, featW, featH);
                    int sum4 = ImageUtils.sumIntegralImage(integralI, x + featH, y + featW, featW, featH);

                    result.add(sum1 - sum2 - sum3 + sum4);
                }
                featureList.add(result);
                i++;
            }
        }
        return featureList;
    }
}
