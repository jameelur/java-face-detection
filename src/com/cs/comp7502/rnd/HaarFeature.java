package com.cs.comp7502.rnd;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rmohamed on 7/26/2016.
 */

public class HaarFeature {
    private int width, height;
    private int type;
    private List<Integer> featureVector = new ArrayList<>();

    public HaarFeature(int type, int width, int height) {
        this.type = type;
        this.width = width;
        this.height = height;
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
}
