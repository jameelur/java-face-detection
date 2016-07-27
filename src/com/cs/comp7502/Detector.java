package com.cs.comp7502;

import com.cs.comp7502.classifier.CascadingClassifier;
import com.cs.comp7502.rnd.Trainer;
import com.cs.comp7502.rnd.WHaarClassifier;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Detector {

    CascadingClassifier cClassifier;

    /**
     * for testing
     */
    public Detector() {
    }

    public Detector(CascadingClassifier cClassifier) {
        this.cClassifier = cClassifier;
    }

    public java.util.List<Rectangle> detectFaces(int[][] input, ColourUtils.Grayscale type, Map<String, List<WHaarClassifier>> trainedClassifiers, double finalThreshold, double similarityThreshold) {
        ArrayList<Rectangle> rectangles = new ArrayList<>();

        int width = input.length;
        int height = input[0].length;

        int[][] image = new int[height][width];
        int[][] image2 = new int[height][width];

        // calculate intensity integral image
        // calculate intensity squared integral image
        setIntensity(input, image, image2, type);

        // find max scale
        // for each possible window
        // run through cascading com.cs.comp7502.classifier and gt true or false
        // if true add the location to the image and the size of the window
        for (int winSize = 24; (winSize <= width) && (winSize <= height); winSize *= 2) { // enlarge the size of sliding window twice each loop
            for (int x = 0; x <= height - winSize; x++) {
                for (int y = 0; y <= width - winSize; y++) {
                    int[][] slidingWindow = new int[winSize][winSize];
                    for (int i = 0; i < winSize; i++) {
                        slidingWindow[i] = Arrays.copyOfRange(image[x + i], y, y + winSize);
                    }
                    doesFaceExist(image, trainedClassifiers, finalThreshold, similarityThreshold);
                }
            }
        }

        return rectangles;
    }

    private boolean doesFaceExist(int[][] image, Map<String, List<WHaarClassifier>> trainedClassifiers, double finalThreshold, double similarityThreshold) {
        List<WHaarClassifier> computedFeatures = Trainer.train(image);
        int positiveCount = 0;
        int negativeCount = 0;
        for (WHaarClassifier feature : computedFeatures){
            double similarity = SimilarityComputation.avgFeatureSimilarity(null, feature, trainedClassifiers.get(feature.getKey()));
            if (similarity > similarityThreshold) positiveCount++;
            else negativeCount++;
        }

        System.out.print("# of Positive Count: " + positiveCount);
        System.out.println(" # of Negative Count: " + negativeCount);

        if (positiveCount / (positiveCount + negativeCount) > finalThreshold) return true;
        return false;
    }

    /**
     * Sets the integral image and integral image squared from a colour image based on a specific grayscale conversion algorithm
     *
     * @param input  image that com.cs.comp7502.data is to be extracted from
     * @param image  integral image
     * @param image2 integral image with squared values
     * @param type   greyscale algorithm type
     */
    public void setIntensity(int[][] input, int[][] image, int[][] image2, ColourUtils.Grayscale type) {
        for (int x = 0; x < input.length; x++) {
            for (int y = 0; y < input[0].length; y++) {
                // basing on this http://journals.plos.org/plosone/article/asset?id=10.1371/journal.pone.0029740.PDF
                int rgb = input[x][y];
                int g = ColourUtils.convertToG((rgb & 0x00ff0000) >> 16, (rgb & 0x0000ff00) >> 8, (rgb & 0x000000ff), type);

                if (x > 0 && y > 0) {
                    image[x][y] = image[x - 1][y] + image[x][y - 1] + g;
                    image2[x][y] = image2[x - 1][y] + image2[x][y - 1] + g * g;
                } else if (x > 0) {
                    image[x][y] = image[x - 1][y] + g;
                    image2[x][y] = image2[x - 1][y] + g * g;
                } else if (y > 0) {
                    image[x][y] = image[x][y - 1] + g;
                    image2[x][y] = image2[x][y - 1] + g * g;
                } else {
                    image[x][y] = g;
                    image2[x][y] = g * g;
                }
            }
        }
    }
}
