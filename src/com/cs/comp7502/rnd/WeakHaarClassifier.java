package com.cs.comp7502.rnd;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rmohamed on 7/26/2016.
 */
public class WeakHaarClassifier {

    public WeakHaarClassifier(List<HaarFeature> classifierList) {
        this.classifierList = classifierList;
    }

    private List<HaarFeature> classifierList = new ArrayList<>();

    public void setClassifierList(ArrayList<HaarFeature> classifierList) {
        this.classifierList = classifierList;
    }

    public List<HaarFeature> getClassifierList() {
        return this.classifierList;
    }
}
