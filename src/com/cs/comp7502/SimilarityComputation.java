package com.cs.comp7502;

import com.cs.comp7502.rnd.WHaarClassifier;

import java.io.*;
import java.util.List;

/**
 * Created by Jinyi Wu on 26/07/2016.
 */
public class SimilarityComputation {


    public SimilarityComputation() {
        super();
    }


    public static double cosSimilarity(List<Integer> vector1, List<Integer> vector2) {
        if (vector1.size() != vector2.size()) {
            System.out.println("Two vectors have different length");
            return 0.0;
        }

        int innerDot = 0;
        int normSquare1 = 0, normSquare2 = 0;

        for (int i = 0; i < vector1.size(); ++i) {
            innerDot += vector1.get(i) * vector2.get(i);
            normSquare1 += vector1.get(i) * vector1.get(i);
            normSquare2 += vector2.get(i) * vector2.get(i);
        }

        if ((Math.sqrt(normSquare1) * Math.sqrt(normSquare2)) == 0.0) {
            return 0.0;
        } else {
            double similarity = Math.abs((double) innerDot / (Math.sqrt(normSquare1) * Math.sqrt(normSquare2)));
//        System.out.println("similarity: " + similarity);
            return similarity;
        }

    }

    public static double correlation(List<Integer> vector1, List<Integer> vector2, double threshold) {
        if (vector1.size() != vector2.size()) {
            System.out.println("Two vectors have different length");
            return 0.0;
        }

        int length = vector1.size();
        int innerDot = 0;
        int sum1 = 0, sum2 = 0;
        int squareSum1 = 0, squareSum2 = 0;

        for (int i = 0; i < length; ++i) {
            innerDot += vector1.get(i) * vector2.get(i);
            sum1 += vector1.get(i);
            sum2 += vector2.get(i);
            squareSum1 += vector1.get(i) * vector1.get(i);
            squareSum2 += vector2.get(i) * vector2.get(i);
        }

        if ((Math.sqrt(length * squareSum1 - sum1 * sum1) * Math.sqrt(length * squareSum2 - sum2 * sum2)) == 0) {
            return 0.0;
        } else {
            double rho = Math.abs((double) (length * innerDot - sum1 * sum2) / (Math.sqrt(length * squareSum1 - sum1 * sum1) * Math.sqrt(length * squareSum2 - sum2 * sum2)));
            return rho;
        }

    }

    public static double avgFeatureSimilarity(PrintWriter writer, WHaarClassifier queryClassifier, List<WHaarClassifier> referenceClassifiers) {
        int numOfRefFace = referenceClassifiers.size();
        double sumOfSimilarity = 0.0;

        List<Integer> queryFeatureVector = queryClassifier.getFeatureVector();

        for (WHaarClassifier wHaarClassifier : referenceClassifiers) {
            List<Integer> referenceFeatureVector = wHaarClassifier.getFeatureVector();

            double similarity = cosSimilarity(queryFeatureVector, referenceFeatureVector);

            writer.println(similarity);

            sumOfSimilarity += similarity;
        }

        double average = sumOfSimilarity / numOfRefFace;

        return average;
    }
}
