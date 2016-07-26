package com.cs.comp7502.rnd;

import java.util.ArrayList;

/**
 * Created by rmohamed on 7/26/2016.
 */
public class WeakHaarClassifier {

    private ArrayList<HaarFeature> classifierList = new ArrayList<>();

    public void addHaarFeature(HaarFeature feature) {
        classifierList.add(feature);
    }
}
