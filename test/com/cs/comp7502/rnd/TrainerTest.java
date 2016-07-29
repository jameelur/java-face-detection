package com.cs.comp7502.rnd;

import com.cs.comp7502.ImageUtils;
import com.cs.comp7502.SimilarityComputation;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by rmohamed on 7/26/2016.
 */
public class TrainerTest {

    @Test
    public void trainFaces() throws Exception {
        // prepare
        long time = System.currentTimeMillis();
        // execture
        Map<String, List<WHaarClassifier>> trainedClassifiers = Trainer.trainFaces();

//        File folder = new File("res/faces/24by24faces");
        File faceFolder = new File("../f1");
        File nonfaceFolder = new File("../nf1");

        File[] faceFiles = faceFolder.listFiles();
        File[] nonfaceFiles = nonfaceFolder.listFiles();

        // for face files
        compareFeatures("results_face", faceFiles, trainedClassifiers);
        compareFeatures("results_nonface", nonfaceFiles, trainedClassifiers);
        // verify

        System.out.println("Time Taken to train: " + ((System.currentTimeMillis() - time) / 1000.0) + "s");
    }

    private void compareFeatures(String fileprefix, File[] files, Map<String, List<WHaarClassifier>> trainedClassifiers) {
        ArrayList<int[][]> imageArray = new ArrayList<>();

        for (File file: files) {
            BufferedImage bImage = null;
            try {
                bImage = ImageIO.read(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            imageArray.add(ImageUtils.buildImageArray(bImage, true));
        }


        // for each image
        for (int[][] image : imageArray) {
            List<WHaarClassifier> computedFeatures = Trainer.train(image);

            Map<String, List<WHaarClassifier>> computedMap = Trainer.convertClassifierListToMap(computedFeatures);

            for (String key: computedMap.keySet()){
                List<WHaarClassifier> classifiers = computedMap.get(key);


                PrintWriter writer = null;
                try {
                    writer = new PrintWriter(fileprefix + key + ".txt", "UTF-8");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                for (WHaarClassifier feature : classifiers){
                    double similarity = SimilarityComputation.avgFeatureSimilarity(null, feature, trainedClassifiers.get(feature.getKey()));
                }

                writer.close();

            }


        }

    }

    @Test
    public void convertClassifierListToMap() throws Exception {
        // prepare
        List<WHaarClassifier> inputClassifierList = new ArrayList<>();

        WHaarClassifier classifier1 = new WHaarClassifier(1, 1, 5, 5);
        List<Integer> featureVector1 = new ArrayList<>();
        featureVector1.add(1);
        featureVector1.add(2);
        featureVector1.add(3);
        classifier1.setFeatureVector(featureVector1);

        WHaarClassifier classifier2 = new WHaarClassifier(2, 1, 5, 5);
        List<Integer> featureVector2 = new ArrayList<>();
        featureVector2.add(3);
        featureVector2.add(4);
        featureVector2.add(5);
        classifier2.setFeatureVector(featureVector2);

        WHaarClassifier classifier3 = new WHaarClassifier(3, 1, 5, 5);
        List<Integer> featureVector3 = new ArrayList<>();
        featureVector3.add(5);
        featureVector3.add(6);
        featureVector3.add(7);
        featureVector3.add(8);
        classifier3.setFeatureVector(featureVector3);

        WHaarClassifier classifier4 = new WHaarClassifier(2, 1, 5, 5);
        List<Integer> featureVector4 = new ArrayList<>();
        featureVector4.add(5);
        featureVector4.add(6);
        featureVector4.add(7);
        featureVector4.add(8);
        classifier4.setFeatureVector(featureVector4);

        inputClassifierList.add(classifier1);
        inputClassifierList.add(classifier2);
        inputClassifierList.add(classifier3);
        inputClassifierList.add(classifier4);

        // execute
        Map<String, List<WHaarClassifier>> classifierMap = Trainer.convertClassifierListToMap(inputClassifierList);

        // verify
        assertEquals(3, classifierMap.size());

        assertEquals(2, classifierMap.get("2_1").size());
    }

    @Test
    public void train_whenFeature1() throws Exception {
        // prepare
        int[][] testI = new int[24][24];
        for (int i = 0; i < 24; i++){
            int[] col = new int[24];
            Arrays.fill(col, 2);
            testI[i] = col;
        }

        // execute
        List<WHaarClassifier> train = Trainer.train(testI, 1);

        // verify
        int totalFeatureSum = 0;
        for (WHaarClassifier classifier : train){
            totalFeatureSum += classifier.getFeatureVector().size();
        }

        assertEquals(43200, totalFeatureSum);
    }

    @Test
    public void train_whenFeature2() throws Exception {
        // prepare
        int[][] testI = new int[24][24];
        for (int i = 0; i < 24; i++){
            int[] col = new int[24];
            Arrays.fill(col, 2);
            testI[i] = col;
        }

        // execute
        List<WHaarClassifier> train = Trainer.train(testI, 2);

        // verify
        int totalFeatureSum = 0;
        for (WHaarClassifier classifier : train){
            totalFeatureSum += classifier.getFeatureVector().size();
        }

        assertEquals(27600, totalFeatureSum);
    }

    @Test
    public void train_whenFeature3() throws Exception {
        // prepare
        int[][] testI = new int[24][24];
        for (int i = 0; i < 24; i++){
            int[] col = new int[24];
            Arrays.fill(col, 2);
            testI[i] = col;
        }

        // execute
        List<WHaarClassifier> train = Trainer.train(testI, 3);

        // verify
        int totalFeatureSum = 0;
        for (WHaarClassifier classifier : train){
            totalFeatureSum += classifier.getFeatureVector().size();
        }

        assertEquals(43200, totalFeatureSum);
    }

    @Test
    public void train_whenFeature4() throws Exception {
        // prepare
        int[][] testI = new int[24][24];
        for (int i = 0; i < 24; i++){
            int[] col = new int[24];
            Arrays.fill(col, 2);
            testI[i] = col;
        }

        // execute
        List<WHaarClassifier> train = Trainer.train(testI, 4);

        // verify
        int totalFeatureSum = 0;
        for (WHaarClassifier classifier : train){
            totalFeatureSum += classifier.getFeatureVector().size();
        }

        assertEquals(27600, totalFeatureSum);
    }

    @Test
    public void train_whenFeature5() throws Exception {
        // prepare
        int[][] testI = new int[24][24];
        for (int i = 0; i < 24; i++){
            int[] col = new int[24];
            Arrays.fill(col, 2);
            testI[i] = col;
        }

        // execute
        List<WHaarClassifier> train = Trainer.train(testI, 5);

        // verify
        int totalFeatureSum = 0;
        for (WHaarClassifier classifier : train){
            totalFeatureSum += classifier.getFeatureVector().size();
        }

        assertEquals(20736, totalFeatureSum);
    }

}