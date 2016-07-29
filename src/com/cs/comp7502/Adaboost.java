package com.cs.comp7502;

import com.cs.comp7502.data.Feature;
import com.cs.comp7502.data.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

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

    // find best stump
    // input: one feature, a set of training samples
    public BestStump findBestStump(Feature feature, List<TrainedImage> data, double sumPos, double sumNeg) {
        // 1. calculate feature values for all training samples based on given feature

        for (TrainedImage datum: data){
            datum.setFeatureValue(0);
            int[][] image = new int[0][];
            try {
                BufferedImage img = ImageIO.read(datum.getFile());
                int[][] tempImg = ImageUtils.buildImageArray(img, true);
                ImageUtils.buildIntegralImage(image, tempImg, tempImg[0].length, tempImg.length);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            datum.setFeatureValue(feature.getValue(image));
        }

        // 2. sort the feature value list
        Collections.sort(data);

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
        return new BestStump(feature, data);
    }

    class BestStump {
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

    class TrainedImage implements Comparable<TrainedImage>{
        int label;
        File file;
        double weight;

        int featureValue;

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

        public int getFeatureValue() {
            return featureValue;
        }

        public void setFeatureValue(int featureValue) {
            this.featureValue = featureValue;
        }

        @Override
        public int compareTo(TrainedImage o) {
            return Integer.compare(this.getFeatureValue(), o.featureValue);
        }
    }
}