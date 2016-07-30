package com.cs.comp7502.data;

import com.cs.comp7502.ImageUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Stage {

    private double stageThreshold;
    private ArrayList<Feature> classifierList = new ArrayList<>();

    public double getStageThreshold() {
        return stageThreshold;
    }

    public void setStageThreshold(double stageThreshold) {
        this.stageThreshold = stageThreshold;
    }

    public ArrayList<Feature> getClassifierList() {
        return classifierList;
    }

    public void setClassifierList(ArrayList<Feature> classifierList) {
        this.classifierList = classifierList;
    }


    public boolean isFace(File file) {
        BufferedImage bImage = null;
        try {
            bImage = ImageIO.read(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        int[][] image = ImageUtils.buildImageArray(bImage, true);
        int w = image[0].length;
        int h = image.length;

        return this.isFace(image);
    }

    public boolean isFace(int[][] inputImage) {
        // for each feature in the stage

        int h = inputImage.length;
        int w = inputImage[0].length;
        int[][] image = new int[h][w];
        ImageUtils.buildIntegralImage(inputImage, image, w, h);

        double sumResult = 0;
        for (Feature feature : classifierList){
            // calculate the feature value
            int value = feature.getValue(image);
            // if p*feature value < p * threshold
            int result = 0;
            if (feature.getPolarity() * value < feature.getPolarity() * feature.getThreshold()){
                // result is 1 else 0
                result = 1;
            }
            sumResult += feature.getWeight() * result;
        }
        // sum up each result with the corresponding feature weight
        // if the sum of result is >= stage threshold the image contains a face
        // otherwise non face
        return sumResult >= stageThreshold ? true : false;
    }
}
