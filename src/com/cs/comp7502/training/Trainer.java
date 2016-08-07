package com.cs.comp7502.training;

import com.cs.comp7502.utils.ImageUtils;
import com.cs.comp7502.classifier.Feature;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

import static com.cs.comp7502.classifier.Feature.*;

/**
 * Created by rmohamed on 7/26/2016.
 */
public class Trainer {

    public static final int ORIGINAL_WINDOW_SIZE = 24;


    public static Map<String, List<WHaarClassifier>> trainFaces() {
        List<WHaarClassifier> trainedClassifierList = new ArrayList<>();
        List<int[][]> imageArray = new ArrayList<>();

        File folder = new File("res/baseFeatureTrainingSet/faces");
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
            imageArray.add(ImageUtils.buildImageArray(bImage, true));
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

        int windowScale = imageH / ORIGINAL_WINDOW_SIZE;
        int maxH = imageH / windowScale;
        int maxW = imageW / windowScale;

        int count = 0;
        int windowCountH = rowCount(type);
        int windowCountW = colCount(type);

        for (int featH = 1; featH <= (maxH / rowCount(type)); featH++) {
            for (int featW = 1; featW <= (maxW / colCount(type)); featW++) {
                WHaarClassifier result = new WHaarClassifier(type, count, featW, featH);
                List<Integer> featureVector = new ArrayList<>();

                for (int x = 0; x < imageH - (featH * windowScale * windowCountH - 1); x += windowScale) {
                    for (int y = 0; y < imageW - (featW * windowScale * windowCountW - 1); y += windowScale) {

                        featureVector.add(new Feature(type, x, y, featW * windowScale, featH * windowScale).getValue(integralI));
                    }
                }
                result.setFeatureVector(featureVector);
                featureList.add(result);
                count++;
            }
        }
        return featureList;
    }


}
