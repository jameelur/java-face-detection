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

import static org.junit.Assert.*;

/**
 * Created by rmohamed on 7/26/2016.
 */
public class TrainerTest {
    @Test
    public void trainFaces() throws Exception {
        // prepare

        // execture
        List<WeakHaarClassifier> trainedClassifiers = Trainer.trainFaces();

        ArrayList<WeakHaarClassifier> weakHaarClassifiers = new ArrayList<>();
        ArrayList<int[][]> imageArray = new ArrayList<>();

        File folder = new File("res/faces/24by24faces");
//        File folder = new File("res/faces/test");
        File[] files = folder.listFiles();

        for (File file: files) {
            BufferedImage bImage = null;
            try {
                bImage = ImageIO.read(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            imageArray.add(ImageUtils.buildGrayscaleImageArray(bImage));
        }

        long time = System.currentTimeMillis();
        PrintWriter writer = null;
        try {
            writer = new PrintWriter("results_faces.txt", "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        for (int[][] image: imageArray){
            List<HaarFeature> computedFeatures = Trainer.train(image, 1);
            SimilarityComputation.voting(writer,new WeakHaarClassifier(computedFeatures), trainedClassifiers, 0.6);
        }

        writer.close();
        // verify
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
        List<HaarFeature> train = Trainer.train(testI, 1);

        // verify
        HaarFeature haarFeature1 = train.get(0);
        HaarFeature haarFeature2 = train.get(1);

        assertEquals(6, haarFeature1.getWidth());
        assertEquals(6, haarFeature2.getWidth());
        assertEquals(3, haarFeature1.getHeight());
        assertEquals(6, haarFeature2.getHeight());

        assertEquals(21, haarFeature1.getFeatureVector().size());
        assertEquals(18, haarFeature2.getFeatureVector().size());

        for (int value: haarFeature1.getFeatureVector()){
            assertEquals(0, value);
        }
        for (int value: haarFeature2.getFeatureVector()){
            assertEquals(0, value);
        }
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
        List<HaarFeature> train = Trainer.train(testI, 2);

        // verify
        HaarFeature haarFeature1 = train.get(0);
        HaarFeature haarFeature2 = train.get(1);

        assertEquals(4, haarFeature1.getWidth());
        assertEquals(4, haarFeature2.getWidth());
        assertEquals(3, haarFeature1.getHeight());
        assertEquals(6, haarFeature2.getHeight());

        assertEquals(21, haarFeature1.getFeatureVector().size());
        assertEquals(18, haarFeature2.getFeatureVector().size());

        for (int value: haarFeature1.getFeatureVector()){
            assertEquals(24, value);
        }
        for (int value: haarFeature2.getFeatureVector()){
            assertEquals(48, value);
        }
    }

}