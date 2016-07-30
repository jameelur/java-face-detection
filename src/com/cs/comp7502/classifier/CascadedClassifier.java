package com.cs.comp7502.classifier;

import com.cs.comp7502.Adaboost;
import com.cs.comp7502.data.Feature;
import com.cs.comp7502.data.Stage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class CascadedClassifier {

    private ArrayList<Stage> stages = new ArrayList<Stage>();

    public ArrayList<Stage> getStages() {
        return stages;
    }

    // Viola-Jones Cascade Classifier
    // 1. set following params
    //      maxFPR, the maximum acceptable false positive rate per layer (stage)
    //      minDR, the minimum acceptable detection rate per layer (stage)
    //      targetFPR, the target false positive rate for cascade classifier
    //      posSet, set of positive samples
    //      negSet, set of negative samples
    public static CascadedClassifier train(List<Feature> posssibleFeatures, double maxFPR, double minDR, double targetFPR, List<File> faces, List<File> nonFaces) {
        // 2. initialise following params
        //      FPR = 0.0, the false positive rate we get for current cascade classifier (part of the final one)
        //      DR = 0.0, the detection rate we get for current cascade classifier (part of the final one)
        //      i = 0, the layer (stage) index

        double fPR = 0.0;
        double dR = 0.0;

        List<File> P = faces;
        List<File> N = nonFaces;

        // 3. train the cascade classifier
        //      while (FPR > targetFPR) {
        CascadedClassifier cascadedClassifier = new CascadedClassifier();
        while (fPR > targetFPR) {
            int n = 0; // the size of feature set
            double newFPR = fPR;
            double newDR = dR;

            Stage stage = null;
            while (newFPR > maxFPR * fPR) {
                n++;
                // train a adaboost classifier with posSet, negSet and a feature set having n feature
                // evaluate current cascade classifier on validation set to get newFPR and newDR
                int subsetIndex = ThreadLocalRandom.current().nextInt(0, posssibleFeatures.size() - n);
                stage = Adaboost.learn(posssibleFeatures.subList(subsetIndex, subsetIndex + n), P, N);

                double threshold = stage.getStageThreshold();
                while (newDR < minDR * dR) {
                    //decrease the stage threshold for this adaboost classifier
                    stage.setStageThreshold(threshold--);
                    // (evaluate the cascaded classifier on the training set)
                    double[] results = cascadedClassifier.evaluate(stage, faces, nonFaces);
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
                    boolean isFace = cascadedClassifier.isFace(nonFace);
                    if (isFace) N.add(nonFace);
                }
            }
        }
        return cascadedClassifier;
    }

    private void add(Stage stage) {
        stages.add(stage);
    }

    public boolean isFace(File image) {
        for (Stage stage: stages) {
            if (!stage.isFace(image)) return false;
        }
        return true;
    }

    public double[] evaluate(Stage stage, List<File> faces, List<File> nonfaces) {
        int faceNum = faces.size();
        int nonFaceNum = nonfaces.size();

        int posFaceNum = 0;
        int negNonFaceNum = 0;

        for (File face : faces) {
            boolean isFace = this.isFace(face);
            if (isFace) {
                isFace = stage.isFace(face);
                if (isFace)
                    posFaceNum++;
            }
        }

        for (File nonFace : nonfaces) {
            boolean isFace = this.isFace(nonFace);
            if (isFace) {
                isFace = stage.isFace(nonFace);
                if (isFace)
                    negNonFaceNum++;
            }
        }

        return new double[]{(double) posFaceNum / faceNum, (double) negNonFaceNum / nonFaceNum};
    }
}
