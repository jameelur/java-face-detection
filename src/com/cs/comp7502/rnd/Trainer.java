package com.cs.comp7502.rnd;

import com.cs.comp7502.ImageUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rmohamed on 7/26/2016.
 */
public class Trainer {

    public static int FEATURE_TYPE_1 = 1;
    public static int FEATURE_TYPE_2 = 2;
    public static int FEATURE_TYPE_3 = 3;
    public static int FEATURE_TYPE_4 = 4;
    public static int FEATURE_TYPE_5 = 5;

    public HaarFeature train(int[][] inputImage, int type){
        List<Integer> featureVector = new ArrayList<>();

        int h = inputImage.length;
        int w = inputImage[0].length;
        int[][] integralI = new int[h][w];
        ImageUtils.buildIntegralImage(inputImage, integralI, w, h);

        if (type == FEATURE_TYPE_1){
            for (int x = 0; x < inputImage.length; x++){
                for (int y = 0; y < inputImage[0].length; y++){

                }
            }
        }



        return new HaarFeature(type, featureVector);
    }
}
