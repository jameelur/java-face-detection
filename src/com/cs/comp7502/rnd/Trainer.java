package com.cs.comp7502.rnd;

import com.cs.comp7502.ImageUtils;
import com.sun.org.apache.xalan.internal.xsltc.util.IntegerArray;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by rmohamed on 7/26/2016.
 */
public class Trainer {

    public static int FEATURE_TYPE_1 = 1;
    public static ArrayList<HaarFeature> FEATURE_1 = new ArrayList<>(
            Arrays.asList(new HaarFeature(1,6,3),
                    new HaarFeature(1,6,6))
    );
    public static int FEATURE_TYPE_2 = 2;
    public static int FEATURE_TYPE_3 = 3;
    public static int FEATURE_TYPE_4 = 4;
    public static int FEATURE_TYPE_5 = 5;

    public static List<WeakHaarClassifier> trainFaces(){
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
        for (int[][] image: imageArray){
            weakHaarClassifiers.add(new WeakHaarClassifier(train(image, 1)));
        }

        System.out.println("Time Taken to train " + imageArray.size() + " number of image is " + ((System.currentTimeMillis() - time)/1000.0) + "s");

        return weakHaarClassifiers;
    }

    public static List<HaarFeature> train(int[][] inputI, int type){
        List<HaarFeature> featureList = new ArrayList<>();

        int h = inputI.length;
        int w = inputI[0].length;
        int[][] integralI = new int[h][w];
        ImageUtils.buildIntegralImage(inputI, integralI, w, h);

        if (type == FEATURE_TYPE_1){
            for (HaarFeature feature : FEATURE_1) {
                int featW = feature.getWidth();
                int featH = feature.getHeight();
                HaarFeature result = new HaarFeature(1, featW, featH);
                for (int x = 0; x < h - featH; x++) {
                    // assume centered.
                    int y = (integralI[0].length - featW*2)/2;
                    // sum1 of pixels on S1
                    int sum1 = (integralI[x+featH-1][y+featW-1] + integralI[x][y] - (integralI[x][y+featW-1] + integralI[x+featH-1][y]));
                    int sum2 = (integralI[x+featH-1][y+featW*2-1] + integralI[x][y+featW] - (integralI[x][y+featW*2-1] + integralI[x+featH-1][y+featW]));

                    result.add(sum1-sum2);
                }
                featureList.add(result);
            }
        }
        return featureList;
    }
}
