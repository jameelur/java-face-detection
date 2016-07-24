package data;

import java.util.ArrayList;

public class Stage {

    private double stageThreshold;
    private ArrayList<WeakClassifier> classifierList = new ArrayList<>();

    public double getStageThreshold() {
        return stageThreshold;
    }

    public void setStageThreshold(double stageThreshold) {
        this.stageThreshold = stageThreshold;
    }

    public ArrayList<WeakClassifier> getClassifierList() {
        return classifierList;
    }

    public void setClassifierList(ArrayList<WeakClassifier> classifierList) {
        this.classifierList = classifierList;
    }
}
