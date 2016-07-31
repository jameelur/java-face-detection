package com.cs.comp7502.classifier;

import com.cs.comp7502.data.Feature;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class CascadedClassifierTest {
    @Test
    public void train() throws Exception {
        // prepare
        List<Feature> featureList = Feature.generateAllFeatures();

        // retrieve list of all face and non face files for training
        File faceFolder = new File("res/trainingSet/faces");
        File nonfaceFolder = new File("res/trainingSet/nonFaces");


        List<File> faces = new ArrayList<>(Arrays.asList(faceFolder.listFiles()));
        List<File> nonFaces = new ArrayList<>(Arrays.asList(nonfaceFolder.listFiles()));

        // evaluate
        long time = System.currentTimeMillis();
        CascadedClassifier train = CascadedClassifier.train(featureList, 0.95, 0.95, 0.20, faces, nonFaces);

        // verify
        System.out.println("Time taken to generate a cascade classifier of stages #:" + train.getStages().size() + " is " + ((System.currentTimeMillis() - time) / 1000) + "s");
    }

}