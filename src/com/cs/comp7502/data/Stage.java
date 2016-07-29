package com.cs.comp7502.data;

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
}
