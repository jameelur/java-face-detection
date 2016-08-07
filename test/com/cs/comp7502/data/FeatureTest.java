package com.cs.comp7502.data;

import com.cs.comp7502.utils.ImageUtils;
import com.cs.comp7502.classifier.Feature;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;

import static com.cs.comp7502.classifier.Feature.*;
import static org.junit.Assert.*;

/**
 * Created by jinyiwu on 30/7/2016.
 */
public class FeatureTest {
    @Test
    public void colCount() throws Exception {
        assertEquals(2, Feature.colCount(FEATURE_TYPE_1));
        assertEquals(3, Feature.colCount(FEATURE_TYPE_2));
        assertEquals(1, Feature.colCount(FEATURE_TYPE_3));
        assertEquals(1, Feature.colCount(FEATURE_TYPE_4));
        assertEquals(2, Feature.colCount(FEATURE_TYPE_5));
    }

    @Test
    public void rowCount() throws Exception {
        assertEquals(1, Feature.rowCount(FEATURE_TYPE_1));
        assertEquals(1, Feature.rowCount(FEATURE_TYPE_2));
        assertEquals(2, Feature.rowCount(FEATURE_TYPE_3));
        assertEquals(3, Feature.rowCount(FEATURE_TYPE_4));
        assertEquals(2, Feature.rowCount(FEATURE_TYPE_5));
    }

    @Test
    public void getValue_whenIntegralImage() throws Exception {
        // prepare
        int[][] testI = new int[24][24];
        for (int i = 0; i < 24; i++){
            int[] col = new int[24];
            Arrays.fill(col, 2);
            testI[i] = col;
        }

        int[][] integralI = new int[24][24];
        ImageUtils.buildIntegralImage(testI, integralI, 24, 24);

        Feature feature1 = new Feature(FEATURE_TYPE_1, 12, 6, 6, 2);
        Feature feature2 = new Feature(FEATURE_TYPE_2, 12, 6, 4, 2);
        Feature feature3 = new Feature(FEATURE_TYPE_3, 6, 6, 12, 6);
        Feature feature4 = new Feature(FEATURE_TYPE_4, 6, 6, 12, 4);
        Feature feature5 = new Feature(FEATURE_TYPE_5, 6, 6, 6, 6);

        // execute
        // verify
        assertEquals(0, feature1.getValue(integralI));
        assertEquals(16, feature2.getValue(integralI));
        assertEquals(0, feature3.getValue(integralI));
        assertEquals(24 * 4, feature4.getValue(integralI));
        assertEquals(0, feature5.getValue(integralI));
    }

    @Test
    public void getValue_whenFile() throws Exception {
        // prepare
        File test1 = new File("res/testImages/testImage1.png");
        File test2 = new File("res/testImages/testImage2.png");

        Feature feature1 = new Feature(FEATURE_TYPE_1, 11, 6, 6, 2);
        Feature feature3 = new Feature(FEATURE_TYPE_3, 6, 5, 12, 6);

        // execute
        // verify
        assertEquals(255 * 6 * 2 * -1, feature1.getValue(test1));
        assertEquals(0, feature1.getValue(test2));
        assertEquals(0, feature3.getValue(test1));
        assertEquals(255 * 12 * 6 * -1, feature3.getValue(test2));
    }

}