package com.cs.comp7502.rnd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by rmohamed on 7/26/2016.
 */

public class WHaarClassifier {
    private int width, height;
    private int type;
    private int subtype;
    private List<Integer> featureVector = new ArrayList<>();

    private static Map<Integer, Double> thresholdMap = new HashMap<>();

    static {
        thresholdMap.put(1, 0.38);
        thresholdMap.put(2, 0.47);
        thresholdMap.put(3, 0.5);
        thresholdMap.put(4, 0.5);
        thresholdMap.put(5, 0.42);
    }

    public WHaarClassifier(int type, int subtype, int width, int height) {
        this.type = type;
        this.subtype = subtype;
        this.width = width;
        this.height = height;
    }

    public void setFeatureVector(List<Integer> featureVector) {
        this.featureVector = featureVector;
    }

    public int getWidth(){
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void add(int i) {
        featureVector.add(i);
    }

    public List<Integer> getFeatureVector() {
        return featureVector;
    }

    public String getKey(){
        return type + "_" + subtype;
    }

    public static double getThreshold(int type){
        return thresholdMap.get(type);
    }

    public int getType() {
        return type;
    }
}

