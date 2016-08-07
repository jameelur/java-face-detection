package com.cs.comp7502.classifier;

import com.cs.comp7502.utils.ImageUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Stage implements JSONRW {

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
        BufferedImage bImage;
        try {
            bImage = ImageIO.read(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        int[][] image = ImageUtils.buildImageArray(bImage, true);

        return this.isFace(image);
    }

    public boolean isFace(int[][] inputImage) {
        // for each feature in the stage

        int h = inputImage.length;
        int w = inputImage[0].length;
        int[][] image = new int[h][w];
        ImageUtils.buildIntegralImage(inputImage, image, w, h);

        double sumResult = 0;
        for (Feature feature : classifierList) {
            // calculate the feature value
            int value = feature.getValue(image);
            // if p*feature value < p * threshold
            int result = 0;
            if (feature.getPolarity() * value < feature.getPolarity() * feature.getThreshold()) {
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

    @Override
    public JSONObject encode() {
        JSONObject stage = new JSONObject();
        JSONArray features = new JSONArray();
        try {
            ArrayList<Feature> classifierList = getClassifierList();
            for (Feature feature : classifierList) {
                JSONObject featureJSON = feature.encode();
                features.put(featureJSON);
            }
            stage.put("stageThreshold", getStageThreshold());
            stage.put("features", features);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return stage;
    }

    @Override
    public void decode(JSONObject json) {
        try {
            stageThreshold = json.getDouble("stageThreshold");
            JSONArray features = json.getJSONArray("features");
            for (int i = 0; i < features.length(); i++) {
                JSONObject jsonObject = features.getJSONObject(i);
                Feature feature = new Feature();
                feature.decode(jsonObject);
                classifierList.add(feature);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
