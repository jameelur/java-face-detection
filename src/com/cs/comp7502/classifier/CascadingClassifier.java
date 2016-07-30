package com.cs.comp7502.classifier;

import com.cs.comp7502.Adaboost;
import com.cs.comp7502.data.Feature;
import com.cs.comp7502.data.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class CascadingClassifier {

    private int height;
    private int width;

    private ArrayList<Stage> stages = new ArrayList<Stage>();

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public ArrayList<Stage> getStages() {
        return stages;
    }

    public void setStages(ArrayList<Stage> stages) {
        this.stages = stages;
    }

    // Viola-Jones Cascade Classifier
//
    // 1. set following params
    //      maxFPR, the maximum acceptable false positive rate per layer (stage)
    //      minDR, the minimum acceptable detection rate per layer (stage)
    //      targetFPR, the target false positive rate for cascade classifier
    //      posSet, set of positive samples
    //      negSet, set of negative samples

    public static List<Stage> train(List<Feature> posssibleFeatures, double maxFPR, double minDR, double targetFPR, List<File> faces, List<File> nonFaces) {
        // 2. initialise following params
        //      FPR = 0.0, the false positive rate we get for current cascade classifier (part of the final one)
        //      DR = 0.0, the detection rate we get for current cascade classifier (part of the final one)
        //      i = 0, the layer (stage) index

        double fPR = 0.0;
        double dR = 0.0;

        List<File> P = faces;
        List<File> N = nonFaces;

        int layer = 0;

        // 3. train the cascade classifier
        //      while (FPR > targetFPR) {
        List<Stage> cascadedClassifier = new ArrayList<>();
        while (fPR > targetFPR) {
            layer++;
            int n = 0; // the size of feature set
            double newFPR = fPR;
            double newDR = dR;

            Stage stage = null;
            while (newFPR > maxFPR * fPR) {
                n++;
                // train a adaboost classifier with posSet, negSet and a feature set having n feature
                // evaluate current cascade classifier on validation set to get newFPR and newDR
                int subsetIndex = ThreadLocalRandom.current().nextInt(0, posssibleFeatures.size() - n);
                stage = Adaboost.learn(posssibleFeatures.subList(subsetIndex, subsetIndex + n), faces, nonFaces);

                double threshold = stage.getStageThreshold();
                while (newDR < minDR * dR) {
                    //decrease the stage threshold for this adaboost classifier
                    stage.setStageThreshold(threshold--);
                    // (evaluate the cascaded classifier on the training set)
                    double[] results = evaluate(cascadedClassifier, stage, P, N);
                    newDR = results[0];
                    newFPR = results[1];
                }
            }
            if (stage == null) throw new RuntimeException("stage is null");
            cascadedClassifier.add(stage);

            fPR = newFPR;
            dR = newDR;
            //  clear negSet
            N = new ArrayList<>();

            if (fPR > targetFPR) {
                // give a set of negative sample
                for (File nonFace : nonFaces){
                    // for any negative sample which can be detected as face
                    // put it into negSet
                    boolean isFace = isFace(cascadedClassifier, nonFace);
                    if (isFace) N.add(nonFace);
                }
            }
        }
        return cascadedClassifier;
    }

    public static double[] evaluate(CascadingClassifier cascadedClassifier, Stage stage, List<File> faces, List<File> nonfaces) {
        int faceNum = faces.size();
        int nonFaceNum = nonfaces.size();

        int posFaceNum = 0;
        int negNonFaceNum = 0;

        for (File face : faces) {
            boolean isFace = isFace(cascadedClassifier, face);
            if (isFace) {
                isFace = isFace(stage, face);
                if (isFace)
                    posFaceNum++;
            }
        }

        for (File nonFace : nonfaces) {
            boolean isFace = isFace(cascadedClassifier, nonFace);
            if (isFace) {
                isFace = isFace(stage, nonFace);
                if (isFace)
                    negNonFaceNum++;
            }
        }

        return new double[]{(double) negNonFaceNum / nonFaceNum, (double) posFaceNum / posFaceNum};
    }
}
