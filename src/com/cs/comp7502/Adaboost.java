package com.cs.comp7502;

import com.cs.comp7502.data.Feature;
import com.cs.comp7502.data.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Adaboost {


    public static Stage learn(File[] faces, File[] nonFaces, int maxTrainingRounds){
        Stage stage = new Stage();

        // using list of faces and non faces make a list of TrainedImages with labels and weights set to 1/ total pos and neg respectively

        for (int round = 0; round < maxTrainingRounds;  round++) {
            // normalize

            // 1. find best stump, and the return values from the best stump should contain
            // error, threshold, polarity, and (optional) classified correct or not

            // 2. calculate the beta = error / (1 - error)
            // 3. calculate the alpha = log(1 / beta), but I am not sure the base of the log

            // 4. update the weights of training samples
            // if the sample is classified correct, newWeight = oldWeight * beta
            // if the sample is classified wrong, don't change the weight

            // 5. (optional) update total sum of positive / negative weight based on pre-assigned label

            // 6. add the best stump with alpha (the weight of this stump) into this stage
        }
        return stage;
    }


    public Feature findBestStump(Feature feature, List<TrainedImage> images) {
        // find best stump
        // input: one feature, a set of training samples
        // 1. calculate feature values for all training samples based on given feature

        // 2. sort the feature value list

        // 3. pass over the feature value list to find best threshold
        // for each feature value we calculate the error for this threshold
        // error = min(Sp + (Tn - Sn), Sn + (Tp - Sp))
        // where Sp / Sn is the sum of positive / negative weight below this threshold
        // Tn / Tp is the total sum of positive / negative weight based on pre-assigned label

        // 4. get the threshold and polarity with minimum error

        // Need a for each feature, you need a list of feature values and their corresponding images
        // for each feature also need a threshold polarity and error for that t and p


        // set threshold
        // set error
        // set polarity
        return feature;
    }



    class TrainedImage {
        int label;
        File file;
        double weight;

        public TrainedImage(int label, File file) {
            this.label = label;
            this.file = file;
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
    }
}