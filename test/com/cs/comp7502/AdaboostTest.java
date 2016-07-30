package com.cs.comp7502;

import com.cs.comp7502.Adaboost;
import com.cs.comp7502.data.Feature;
import com.cs.comp7502.data.Stage;
import com.cs.comp7502.rnd.WHaarClassifier;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static com.cs.comp7502.Adaboost.*;
import static com.cs.comp7502.data.Feature.FEATURE_TYPE_1;
import static com.cs.comp7502.data.Feature.colCount;
import static com.cs.comp7502.data.Feature.rowCount;
import static org.junit.Assert.*;

/**
 * Created by jinyiwu on 30/7/2016.
 */
public class AdaboostTest {
    @Test
    public void findBestStump() throws Exception {
        // prepare
        Feature feature1 = new Feature(FEATURE_TYPE_1, 11, 6, 6, 2);


        File test1 = new File("res/testImages/testImage1.png");
        File test2 = new File("res/testImages/testImage2.png");

        List<TrainedImage> images = new ArrayList<>();
        TrainedImage image1 = new TrainedImage(1, test1, 0.5);
        TrainedImage image2 = new TrainedImage(-1, test2, 0.5);
        images.add(image1);
        images.add(image2);

        // execute
        BestStump bestStump = Adaboost.findBestStump(feature1, images, 0.5, 0.5);

        // verify
        Feature feature = bestStump.getFeature();
        assertEquals(0,feature.getError(), 0.01);
        assertEquals(-1,feature.getPolarity(), 0.01);
        assertEquals(-1530,feature.getThreshold(), 0.01);
    }

    @Test
    public void learn() throws Exception {
        // prepare
        // generate all features
        List<Feature> featureList = new ArrayList<>();
        for (int type = 1; type <= Feature.FEATURE_MAP.size(); type++) {
            int windowCountH = rowCount(type);
            int windowCountW = colCount(type);
            for (int height = 1; height <= (24 / windowCountH); height++) {
                for (int width = 1; width <= (24 / windowCountW); width++) {
                    for (int x = 0; x < 24 - (height * windowCountH - 1); x ++) {
                        for (int y = 0; y < 24 - (width * windowCountW - 1); y ++) {
                            featureList.add(new Feature(type, x, y, width, height));
                        }
                    }
                }
            }
        }

        assertEquals(162336, featureList.size());

        // retrieve list of all face and non face files for training
        File faceFolder = new File("res/trainingSet/faces");
        File nonfaceFolder = new File("res/trainingSet/nonFaces");


        File[] faceFiles = faceFolder.listFiles();
        File[] nonfaceFiles = nonfaceFolder.listFiles();


        List<Feature> trainingFeatures = featureList.subList(0, 99);
        // prepare
//        Feature feature1 = new Feature(FEATURE_TYPE_1, 11, 6, 6, 2);
//
//
//        File test1 = new File("res/testImages/testImage1.png");
//        File test2 = new File("res/testImages/testImage2.png");
//
//        List<Feature> trainingFeatures = new ArrayList<>();
//        trainingFeatures.add(feature1);



        // execute
        long time = System.currentTimeMillis();
//        Stage stage = Adaboost.learn(trainingFeatures, new File[]{test1}, new File[]{test2});
        Stage stage = Adaboost.learn(trainingFeatures, faceFiles, nonfaceFiles);
        System.out.println("Time taken to boost: " + ((System.currentTimeMillis() - time)/1000) + "s");

        for (double i = 1 ; i > 0.001; i-=0.01){
            System.out.println("Setting stage threshold to " + i);
            stage.setStageThreshold(i);

            // verify
            // retrieve list of all face and non face files for testing
            File trainingFaceFolder = new File("res/testingExamples/faces");
            File trainingNonfaceFolder = new File("res/testingExamples/nonFaces");

            File[] trainingFaces = trainingFaceFolder.listFiles();
            File[] trainingNonFaces = trainingNonfaceFolder.listFiles();

            // for each image
            int numPositiveFaces = 0;
            int numNegativeFaces = 0;
            int numPositiveNonFaces = 0;
            int numNegativeNonFaces = 0;
            for (File file: trainingFaces){
                boolean isFace = isFace(stage, file);


                if (isFace) {
                    numPositiveFaces++;
                }
                else {
                    numNegativeFaces++;
                }
            }

            for (File file: trainingNonFaces){
                // for each feature in the stage
                boolean isFace = isFace(stage, file);

                if (isFace) {
                    numPositiveNonFaces++;
                }
                else {
                    numNegativeNonFaces++;
                }
            }

            System.out.println("Face: " + numPositiveFaces + " / " + (numPositiveFaces + numNegativeFaces));
            System.out.println("Non-Face: " + numNegativeNonFaces + " / " + (numPositiveNonFaces + numNegativeNonFaces));
        }
    }

    private boolean isFace(Stage stage, File file) throws IOException {
        // for each feature in the stage
        double sumResult = 0;
        for (Feature feature : stage.getClassifierList()){
            // calculate the feature value
            int value = feature.getValue(file);
            // if p*feature value < p * threshold
            int result = 0;
            if (feature.getPolarity() * value < feature.getPolarity() * feature.getThreshold()){
                // result is 1 else 0
                result = 1;
            }
            sumResult = feature.getWeight() * result;
        }
        // sum up each result with the corresponding feature weight
        // if the sum of result is >= stage threshold the image contains a face
        // otherwise non face
        return sumResult >= stage.getStageThreshold() ? true : false;
    }
}