package com.cs.comp7502;

import com.cs.comp7502.data.Feature;
import com.cs.comp7502.data.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.PackedColorModel;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class Adaboost {

    // Viola-Jones Cascade Classifier
    //
    // 1. set following params
    //      maxFPR, the maximum acceptable false positive rate per layer (stage)
    //      minDR, the minimum acceptable detection rate per layer (stage)
    //      targetFPR, the target false positive rate for cascade classifier
    //      posSet, set of positive samples
    //      negSet, set of negative samples

    // 2. initialise following params
    //      FPR = 0.0, the false positive rate we get for current cascade classifier (part of the final one)
    //      DR = 0.0, the detection rate we get for current cascade classifier (part of the final one)
    //      i = 0, the layer (stage) index

    // 3. train the cascade classifier
    //      while (FRP > targetFPR) {
    //          i++
    //          n = 0, the size of feature set
    //          newFPR = FPR
    //          newDR = DR
    //
    //          while (newFPR > maxFPR * FPR) {
    //              n++
    //              train a adaboost classifier with posSet, negSet and a feature set having n feature
    //              evaluate current cascade classifier on validation set to get newFPR and newDR
    //
    //              while (newDR < minDR * DR) {
    //                  decrease the stage threshold for this adaboost classifier
    //              }
    //          }
    //
    //          FPR = newFPR
    //          DR = newDR
    //          clear negSet
    //
    //          if (FRP > targetFPR) {
    //              give a set of negative samples
    //              for any negative sample which can be detected as face
    //              put it into negSet
    //          }
    //
    //      }

    static int FACE = 1;
    static int NON_FACE = -1;

    public static Stage learn(List<Feature> features, File[] faces, File[] nonFaces){
        Stage stage = new Stage();
        int maxTrainingRounds = features.size();
        List<TrainedImage> images = new ArrayList<>();
        double posWeight = 1.0 / (2.0 * faces.length);
        double negWeight = 1.0 / (2.0 * nonFaces.length);
        double sumOfPosWeight = 0.5;
        double sumOfNegWeight = 0.5;
        ArrayList<Feature> decisionStumps = new ArrayList<>();
        double stageThreshold = 0.0;

        for (File face : faces)
            images.add(new TrainedImage(FACE, face, posWeight));

        for (File nonFace: nonFaces)
            images.add(new TrainedImage(NON_FACE, nonFace, negWeight));

        // using list of faces and non faces make a list of TrainedImages with labels and weights set to 1/ total pos and neg respectively

        for (int round = 0; round < maxTrainingRounds;  round++) {
            // 0. normalize the weights
            double sumOfWeight = sumOfPosWeight + sumOfNegWeight;
            sumOfPosWeight /= sumOfWeight;
            sumOfNegWeight /= sumOfWeight;
            for (TrainedImage image : images) {
                double oldWeight = image.getWeight();
                image.setWeight(oldWeight / sumOfWeight);
            }

            // 1. find best stump, and the return values from the best stump should contain
            // error, threshold, polarity, and (optional) classified correct or not
            BestStump bestStump = findBestStump(features.get(round), images, sumOfPosWeight, sumOfNegWeight);

            // 2. calculate the beta = error / (1 - error)
            // 3. calculate the alpha = log(1 / beta), but I am not sure the base of the log
            double error = bestStump.getFeature().getError();
            if (error <= 0) error = 0.001;
            double beta = error / (1 - error);
            double alpha = Math.log10(1.0 / beta);
            bestStump.getFeature().setWeight(alpha);
            stageThreshold += alpha;

            // 4. update the weights of training samples
            // if the sample is classified correct, newWeight = oldWeight * beta
            // if the sample is classified wrong, don't change the weight
            //
            // 5. (optional) update total sum of positive / negative weight based on pre-assigned label
            double threshold = bestStump.getFeature().getThreshold();
            int polarity = bestStump.getFeature().getPolarity();

            for (TrainedImage image : images) {

                boolean isFace = ((double) polarity * image.featureValue) < (polarity * threshold);
                if (isFace && image.getLabel() == FACE || !isFace && image.getLabel() == NON_FACE){
                    double oldWeight = image.weight;
                    double newWeight = image.weight * beta;
                    image.weight = newWeight;

                    if (image.label == FACE)
                        sumOfPosWeight += -oldWeight + newWeight;
                    else
                        sumOfNegWeight += -oldWeight + newWeight;
                }
            }

            // 6. add the best stump with alpha (the weight of this stump) into this stage
            decisionStumps.add(bestStump.getFeature());
        }

        stage.setStageThreshold(stageThreshold / 2);
        stage.setClassifierList(decisionStumps);
        return stage;
    }

    // find best stump
    // input: one feature, a set of training samples
    public static BestStump findBestStump(Feature feature, List<TrainedImage> data, double sumOfPosWeight, double sumOfNegWeight) {
        // 1. calculate feature values for all training samples based on given feature
        for (TrainedImage datum : data){
            try {
                datum.setFeatureValue(feature.getValue(datum.getFile()));
            } catch (IOException e) {
                throw new RuntimeException("Unable to open training data set", e);
            }
        }

        // 2. sort the feature value list
        Collections.sort(data);

        double eMin = 2; // min error cannot be larger than 1
        int ePolarity = 0;
        double eThreshold = 0;
        double sumPosBelowT = 0;
        double sumNegBelowT = 0;
        // 3. pass over the feature value list to find best threshold
        for (int i = 0; i < data.size() - 1; i++) {
            // for each feature value we calculate the error for this threshold
            // error = min(Sp + (Tn - Sn), Sn + (Tp - Sp))
            // where Sp / Sn is the sum of positive / negative weight below this threshold
            // Tn / Tp is the total sum of positive / negative weight based on pre-assigned label
            if (data.get(i).getLabel() == FACE) {
                sumPosBelowT += data.get(i).getWeight();
            } else {
                sumNegBelowT += data.get(i).getWeight();
            }


            double sumNegAboveT = (sumOfNegWeight - sumNegBelowT);
            if (sumNegAboveT < 0) sumNegAboveT = 0.001;
            double sumPosAboveT = (sumOfPosWeight - sumPosBelowT);
            if (sumPosAboveT < 0) sumPosAboveT = 0.001;
            double ePos = sumPosBelowT + sumNegAboveT;
            double eNeg = sumNegBelowT + sumPosAboveT;
            double threshold = ((data.get(i).getFeatureValue() + data.get(i + 1).getFeatureValue()) / 2);

            int polarity;
            double e;
            if (ePos < eNeg) {
                e = ePos;
                polarity = 1;
            } else {
                e = eNeg;
                polarity = -1;
            }

            // 4. get the threshold and polarity with minimum error
            if (e < eMin) {
                eMin = e;
                ePolarity = polarity;
                eThreshold = threshold;
            }
        }



        feature.setThreshold(eThreshold);
        feature.setError(eMin);
        feature.setPolarity(ePolarity);

        // set threshold
        // set error
        // set polarity
        return new BestStump(feature, data);
    }

    static class BestStump {
        private Feature feature;
        private List<TrainedImage> trainedImages;

        public BestStump(Feature feature, List<TrainedImage> trainedImages) {
            this.feature = feature;
            this.trainedImages = trainedImages;
        }

        public Feature getFeature() {
            return feature;
        }

        public List<TrainedImage> getTrainedImages() {
            return trainedImages;
        }
    }


    static class TrainedImage implements Comparable<TrainedImage> {
        int label;
        File file;
        double weight;

        int featureValue;

        public TrainedImage(int label, File file, double weight) {
            this.label = label;
            this.file = file;
            this.weight = weight;
        }

        public int getLabel() {
            return label;
        }

        public void setLabel(int label) {
            this.label = label;
        }

        public File getFile() {
            return file;
        }

        public void setFile(File file) {
            this.file = file;
        }

        public double getWeight() {
            return weight;
        }

        public void setWeight(double weight) {
            this.weight = weight;
        }

        public int getFeatureValue() {
            return featureValue;
        }

        public void setFeatureValue(int featureValue) {
            this.featureValue = featureValue;
        }


        @Override
        public int compareTo(TrainedImage o) {
            return Integer.compare(this.getFeatureValue(), o.getFeatureValue());
        }
    }

}