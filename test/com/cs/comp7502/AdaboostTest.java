package com.cs.comp7502;

import com.cs.comp7502.data.Feature;
import com.cs.comp7502.data.Stage;
import com.cs.comp7502.rnd.WHaarClassifier;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.cs.comp7502.Adaboost.*;
import static com.cs.comp7502.data.Feature.colCount;
import static com.cs.comp7502.data.Feature.rowCount;
import static org.junit.Assert.*;

/**
 * Created by jinyiwu on 30/7/2016.
 */
public class AdaboostTest {
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

        List<Feature> trainingFeatures = featureList.subList(0, 999);

        // execute
        Stage stage = Adaboost.learn(trainingFeatures, faceFiles, nonfaceFiles);

        // verify
        // retrieve list of all face and non face files for testing
        File trainingFaceFolder = new File("res/trainingExamples/faces");
        File trainingNonfaceFolder = new File("res/trainingExamples/nonFaces");

        File[] trainingFaces = trainingFaceFolder.listFiles();
        File[] trainingNonFace = trainingNonfaceFolder.listFiles();

        // for each image
        int numPositiveFaces = 0;
        int numNegativeFaces = 0;
        int numPositiveNonFaces = 0;
        int numNegativeNonFaces = 0;
        for (File file: trainingFaces){
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
            boolean isFace = sumResult >= stage.getStageThreshold() ? true : false;
        }





    }

    @Test
    public void findBestStump() throws Exception {
        // prepare
        Feature feature = new Feature(1, 0, 0, 5, 5);

        List<Adaboost.TrainedImage> list = new ArrayList<>();
        TrainedImage image = new TrainedImage(1, new File("res/test/face0001.png"), 10);
        TrainedImage image2 = new TrainedImage(1, new File("res/test/face0002.png"), 10);
        list.add(image);
        list.add(image2);

        // execute
        Adaboost.findBestStump(feature, list, 100, 90);

        // verify
    }

}