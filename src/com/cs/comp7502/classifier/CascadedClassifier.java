package com.cs.comp7502.classifier;

import com.cs.comp7502.Adaboost;
import com.cs.comp7502.JSONRW;
import com.cs.comp7502.data.Feature;
import com.cs.comp7502.data.Stage;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class CascadedClassifier implements JSONRW {

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
    public static CascadedClassifier train(List<Feature> possibleFeatures, double maxFPR, double minDR, double targetFPR, List<File> faces, List<File> nonFaces) {
        // 2. initialise following params
        //      FPR = 0.0, the false positive rate we get for current cascade classifier (part of the final one)
        //      DR = 0.0, the detection rate we get for current cascade classifier (part of the final one)
        //      i = 0, the layer (stage) index

        double fPR = 1.0;
        double dR = 1.0;

        List<File> P = faces;
        List<File> N = nonFaces;

        // 3. train the cascade classifier
        //      while (FPR > targetFPR) {
        int layer = 1;
        CascadedClassifier cascadedClassifier = new CascadedClassifier();
        System.out.println("----Starting training----");
        while (fPR > targetFPR) {
            int maxClassifiers = Math.min(10*layer + 10, 200);

            int n = 0; // the size of feature set
            System.out.println("----Computing stage " +  layer + "----");
            long stageTime = System.currentTimeMillis();
            double newFPR = fPR;
            double newDR = dR;

            Stage stage = null;
            boolean retry = false;
//            Set<Integer> usedFeatures = new HashSet<>();
            List<Feature> featureSubset = new ArrayList<>();
            while (newFPR > maxFPR * fPR) {
                n++;
                if (featureSubset.size() >= maxClassifiers) {
                    retry = true;
                    break;
                    // retry with new subset of feature
                }
                // train a adaboost classifier with posSet, negSet and a feature set having n feature
                // evaluate current cascade classifier on validation set to get newFPR and newDR

//                while (true) {
                    int subIndex = ThreadLocalRandom.current().nextInt(0, possibleFeatures.size() - n);
//                    if (!usedFeatures.contains(subIndex)) {
//                        featureSubset.add(possibleFeatures.get(subIndex));
//                        usedFeatures.add(subIndex);
//                        break;
//                    }
//                }
                stage = Adaboost.learn(possibleFeatures.subList(subIndex, subIndex + n), P, N);

                double threshold = stage.getStageThreshold();
                double decrement = Math.abs(threshold) * 0.01;
                do {
                    //decrease the stage threshold for this adaboost classifier
                    stage.setStageThreshold(threshold);
                    threshold -= decrement;
                    // (evaluate the cascaded classifier on the training set)
                    double[] results = cascadedClassifier.evaluate(stage, faces, nonFaces);
                    newDR = results[0];
                    newFPR = results[1];

                } while (newDR < minDR * dR);
                System.out.println("----Computed stage " + layer + ", classifier " + n + " newFPR " + newFPR + " maxFPR * fPR " + maxFPR * fPR +" newDR " + newDR + " dR " + dR + "----");
            }
            if (retry) {
                System.out.println("Max number of classifiers per cascade reached, retrying...");
                continue;
            }

            if (stage == null) throw new RuntimeException("stage is null");
            cascadedClassifier.add(stage);
            System.out.println("----Finished computing stage " +  layer + " in" + ((System.currentTimeMillis() - stageTime)/1000) +"s ----");
            System.out.println("----Number of classifiers in stage " +  layer + " is " + stage.getClassifierList().size() + "----");

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
            layer++;
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

    @Override
    public JSONObject encode() {
        return null;
    }

    @Override
    public void decode(JSONObject json) {

    }
}
