package com.cs.comp7502;

import com.cs.comp7502.rnd.HaarFeature;
import com.cs.comp7502.rnd.WeakHaarClassifier;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jinyi Wu on 26/07/2016.
 */
public class SimilarityComputation {


    public SimilarityComputation() {
        super();
    }

    public static boolean cosSimilarity(List<Integer> vector1, List<Integer> vector2, double threshold) {
        if (vector1.size() != vector2.size()) {
            System.out.println("Two vectors have different length");
            return false;
        }

        int innerDot = 0;
        int normSquare1 = 0, normSquare2 = 0;

        for (int i = 0; i < vector1.size(); ++i) {
            innerDot += vector1.get(i) * vector2.get(i);
            normSquare1 += vector1.get(i) * vector1.get(i);
            normSquare2 += vector2.get(i) * vector2.get(i);
        }

        double similarity = Math.abs((double) innerDot / (Math.sqrt(normSquare1) * Math.sqrt(normSquare2)));

//        System.out.println("similarity: " + similarity);

        return similarity >= threshold;

    }

    public static boolean correlation(List<Integer> vector1, List<Integer> vector2, double threshold) {
        if (vector1.size() != vector2.size()) {
            System.out.println("Two vectors have different length");
            return false;
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

        double rho = Math.abs((double) (length * innerDot - sum1 * sum2) / (Math.sqrt(length * squareSum1 - sum1 * sum1) * Math.sqrt(length * squareSum2 - sum2 * sum2)));

        return rho >= threshold;

    }

    public static double voting(PrintWriter writer, WeakHaarClassifier queryClassifier, List<WeakHaarClassifier> referenceClassifiers, double threshold) {
        int numOfRefFace = referenceClassifiers.size();
        int numOfPosVote = 0;


        List<HaarFeature> queryFeatuerList = queryClassifier.getClassifierList();
        int numOfFeatuerType = queryFeatuerList.size();

        for (int featureTypeIndex = 0; featureTypeIndex < numOfFeatuerType; ++featureTypeIndex) {
            List<Integer> queryFeatureVector = queryFeatuerList.get(featureTypeIndex).getFeatureVector();
            int newPosVote = 0;

            for (WeakHaarClassifier weakHaarClassifier : referenceClassifiers) {
                List<Integer> referenceFeatureVector = weakHaarClassifier.getClassifierList().get(featureTypeIndex).getFeatureVector();
                if (cosSimilarity(queryFeatureVector, referenceFeatureVector, threshold))
                    ++newPosVote;
//                if (correlation(queryFeatureVector, referenceFeatureVector, threshold))
//                    ++newPosVote;
            }

//            System.out.print("Positive Vote for feature " + featureTypeIndex + ": " + newPosVote + " / " + numOfRefFace);
//            System.out.print(featureTypeIndex + ": " + newPosVote + " / " + numOfRefFace);
            writer.print(featureTypeIndex + ": " + newPosVote + " / " + numOfRefFace + "_ ");
            numOfPosVote += newPosVote;
        }

        double rate = (double) numOfPosVote / (numOfRefFace * numOfFeatuerType);

//        System.out.println("\n The overall positive voting rate: " + rate);
        writer.println("The overall positive voting rate: " + rate);

        return rate;
    }
}
