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
    public static int FEATURE_TYPE_2 = 2;
    public static int FEATURE_TYPE_3 = 3;
    public static int FEATURE_TYPE_4 = 4;
    public static int FEATURE_TYPE_5 = 5;

    public static Map<String, List<WHaarClassifier>> trainFaces() {
        List<WHaarClassifier> trainedClassifierList = new ArrayList<>();
        List<int[][]> imageArray = new ArrayList<>();

        File folder = new File("../24by24faces");
//        File folder = new File("res/faces/test");
        File[] files = folder.listFiles();

        for (File file : files) {
            if (file.getName().startsWith(".")) continue;
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
            List<WHaarClassifier> train = train(image);
            trainedClassifierList.addAll(train);
        }

        Map<String, List<WHaarClassifier>> trainedClassifierMap = convertClassifierListToMap(trainedClassifierList);

        System.out.println("Time Taken to train " + imageArray.size() + " number of image is " + ((System.currentTimeMillis() - time) / 1000.0) + "s");

        return trainedClassifierMap;
    }

    public static Map<String, List<WHaarClassifier>> convertClassifierListToMap(List<WHaarClassifier> classifierList) {
        Map<String, List<WHaarClassifier>> classifierMap = new HashMap<>();
        for (WHaarClassifier classifier : classifierList) {
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
        for (int i = 1; i < 6; i++) {
            // collect the resultant classifiers in a list
            weakHaarClassifiers.addAll(train(image, i));
        }
        return weakHaarClassifiers;
    }

    public static List<WHaarClassifier> train(int[][] integralI, int type) {
        List<WHaarClassifier> featureList = new ArrayList<>();

        int imageH = integralI.length;
        int imageW = integralI[0].length;

//        int[][] integralI = new int[h][w];
//        ImageUtils.buildIntegralImage(inputI, integralI, w, h);

        int windowScale = imageH / ORIGINAL_WINDOW_SIZE;
        int maxH = imageH / windowScale;
        int maxW = imageW / windowScale;

        if (type == FEATURE_TYPE_1) {
            int count = 0;

            for (int featH = 1; featH <= maxH; featH++) {
                for (int featW = 1; featW <= (maxW / 2); featW++) {
                    WHaarClassifier result = new WHaarClassifier(1, count, featW, featH);
                    result.setFeatureVector(computeHaarFeature(integralI, imageH, imageW, featH * windowScale, featW * windowScale, windowScale, 1, 2));
                    featureList.add(result);
                    count++;
                }
            }
        } else if (type == FEATURE_TYPE_2) {
            int count = 0;
            for (int featH = 1; featH <= maxH; featH++) {
                for (int featW = 1; featW <= (maxW / 3); featW++) {
                    WHaarClassifier result = new WHaarClassifier(2, count, featW, featH);
                    result.setFeatureVector(computeHaarFeature(integralI, imageH, imageW, featH * windowScale, featW * windowScale, windowScale, 1, 3));
                    featureList.add(result);
                    count++;
                }
            }
        } else if (type == FEATURE_TYPE_3) {
            int count = 0;
            for (int featH = 1; featH <= (maxH / 2); featH++) {
                for (int featW = 1; featW <= maxW; featW++) {
                    WHaarClassifier result = new WHaarClassifier(3, count, featW, featH);
                    result.setFeatureVector(computeHaarFeature(integralI, imageH, imageW, featH * windowScale, featW * windowScale, windowScale, 2, 1));
                    featureList.add(result);
                    count++;
                }
            }
        } else if (type == FEATURE_TYPE_4) {
            int count = 0;
            for (int featH = 1; featH <= (maxH / 3); featH++) {
                for (int featW = 1; featW <= maxW; featW++) {
                    WHaarClassifier result = new WHaarClassifier(4, count, featW, featH);
                    result.setFeatureVector(computeHaarFeature(integralI, imageH, imageW, featH * windowScale, featW * windowScale, windowScale, 3, 1));
                    featureList.add(result);
                    count++;
                }
            }
        } else if (type == FEATURE_TYPE_5) {
            int count = 0;
            for (int featH = 1; featH <= (maxH / 2); featH++) {
                for (int featW = 1; featW <= (maxW / 2); featW++) {
                    WHaarClassifier result = new WHaarClassifier(5, count, featW, featH);
                    result.setFeatureVector(computeHaarFeature(integralI, imageH, imageW, featH * windowScale, featW * windowScale, windowScale, 2, 2));
                    featureList.add(result);
                    count++;
                }
            }
        }
        return featureList;
    }

    private static List<Integer> computeHaarFeature(int[][] integralI, int imageH, int imageW, int windowH, int windowW, int windowScale, int windowCountH, int windowCountW) {
        List<Integer> featureVector = new ArrayList<>();

        for (int x = 0; x < imageH - (windowH * windowCountH - 1); x += windowScale) {
            for (int y = 0; y < imageW - (windowW * windowCountW - 1); y += windowScale) {

                // type 1
                if (windowCountH == 1 && windowCountW == 2) {
                    int sum1 = ImageUtils.sumIntegralImage(integralI, x, y, windowW, windowH);
                    int sum2 = ImageUtils.sumIntegralImage(integralI, x, y + windowW, windowW, windowH);

                    featureVector.add(sum1 - sum2);
                }
                // type 2
                else if (windowCountH == 1 && windowCountW == 3) {
                    int sum1 = ImageUtils.sumIntegralImage(integralI, x, y, windowW, windowH);
                    int sum2 = ImageUtils.sumIntegralImage(integralI, x, y + windowW, windowW, windowH);
                    int sum3 = ImageUtils.sumIntegralImage(integralI, x, y + windowW * 2, windowW, windowH);

                    featureVector.add(sum1 - sum2 + sum3);
                }
                // type 3
                else if (windowCountH == 2 && windowCountW == 1) {
                    int sum1 = ImageUtils.sumIntegralImage(integralI, x, y, windowW, windowH);
                    int sum2 = ImageUtils.sumIntegralImage(integralI, x + windowH, y, windowW, windowH);

                    featureVector.add(sum1 - sum2);
                }
                // type 4
                else if (windowCountH == 3 && windowCountW == 1) {
                    int sum1 = ImageUtils.sumIntegralImage(integralI, x, y, windowW, windowH);
                    int sum2 = ImageUtils.sumIntegralImage(integralI, x + windowH, y, windowW, windowH);
                    int sum3 = ImageUtils.sumIntegralImage(integralI, x + windowH * 2, y, windowW, windowH);

                    featureVector.add(sum1 - sum2 + sum3);
                }
                // type 5
                else if (windowCountH == 2 && windowCountW == 2) {
                    int sum1 = ImageUtils.sumIntegralImage(integralI, x, y, windowW, windowH);
                    int sum2 = ImageUtils.sumIntegralImage(integralI, x + windowH, y, windowW, windowH);
                    int sum3 = ImageUtils.sumIntegralImage(integralI, x, y + windowW, windowW, windowH);
                    int sum4 = ImageUtils.sumIntegralImage(integralI, x + windowH, y + windowW, windowW, windowH);

                    featureVector.add(sum1 - sum2 - sum3 + sum4);
                }
            }
        }
        return featureVector;
    }
}
