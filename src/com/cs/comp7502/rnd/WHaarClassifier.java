package com.cs.comp7502.rnd;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rmohamed on 7/26/2016.
 */

public class WHaarClassifier {
    private int width, height;
    private int type;
    private int subtype;
    private List<Integer> featureVector = new ArrayList<>();

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
}
