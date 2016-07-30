package com.cs.comp7502.data;

import com.cs.comp7502.ImageUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by rmohamed on 7/24/2016.
 */
public class Feature {

    public static int FEATURE_TYPE_1 = 1;
    public static int FEATURE_TYPE_2 = 2;
    public static int FEATURE_TYPE_3 = 3;
    public static int FEATURE_TYPE_4 = 4;
    public static int FEATURE_TYPE_5 = 5;

    public static Map<Integer, int[]> FEATURE_MAP = new HashMap<>();

    static {
        FEATURE_MAP.put(FEATURE_TYPE_1, new int[]{1,2});
        FEATURE_MAP.put(FEATURE_TYPE_2, new int[]{1,3});
        FEATURE_MAP.put(FEATURE_TYPE_3, new int[]{2,1});
        FEATURE_MAP.put(FEATURE_TYPE_4, new int[]{3,1});
        FEATURE_MAP.put(FEATURE_TYPE_5, new int[]{2,2});
    }

    public static int colCount(int type){
        return FEATURE_MAP.get(type)[1];
    }

    public static int rowCount(int type){
        return FEATURE_MAP.get(type)[0];
    }

    private int type;

    private int x;
    private int y;

    private int width;
    private int height;

    private double error;
    private double threshold;
    private int polarity;

    private double weight;

    public Feature(int type, int x, int y, int width, int height) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public double getError() {
        return error;
    }

    public void setError(double error) {
        this.error = error;
    }

    public double getThreshold() {
        return threshold;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }

    public int getPolarity() {
        return polarity;
    }

    public void setPolarity(int polarity) {
        this.polarity = polarity;
    }


    public int getValue(int[][] image) {
        int result;

        int windowCountH = rowCount(type);
        int windowCountW = colCount(type);
        // type 1
        if (windowCountH == 1 && windowCountW == 2) {
            int sum1 = ImageUtils.sumIntegralImage(image, x, y, width, height);
            int sum2 = ImageUtils.sumIntegralImage(image, x, y + width, width, height);

            result = sum1 - sum2;
        }
        // type 2
        else if (windowCountH == 1 && windowCountW == 3) {
            int sum1 = ImageUtils.sumIntegralImage(image, x, y, width, height);
            int sum2 = ImageUtils.sumIntegralImage(image, x, y + width, width, height);
            int sum3 = ImageUtils.sumIntegralImage(image, x, y + width * 2, width, height);

            result = sum1 - sum2 + sum3;
        }
        // type 3
        else if (windowCountH == 2 && windowCountW == 1) {
            int sum1 = ImageUtils.sumIntegralImage(image, x, y, width, height);
            int sum2 = ImageUtils.sumIntegralImage(image, x + height, y, width, height);

            result = sum1 - sum2;
        }
        // type 4
        else if (windowCountH == 3 && windowCountW == 1) {
            int sum1 = ImageUtils.sumIntegralImage(image, x, y, width, height);
            int sum2 = ImageUtils.sumIntegralImage(image, x + height, y, width, height);
            int sum3 = ImageUtils.sumIntegralImage(image, x + height * 2, y, width, height);

            result = sum1 - sum2 + sum3;
        }
        // type 5
        else {
            int sum1 = ImageUtils.sumIntegralImage(image, x, y, width, height);
            int sum2 = ImageUtils.sumIntegralImage(image, x + height, y, width, height);
            int sum3 = ImageUtils.sumIntegralImage(image, x, y + width, width, height);
            int sum4 = ImageUtils.sumIntegralImage(image, x + height, y + width, width, height);

            result = sum1 - sum2 - sum3 + sum4;
        }

        return result;
    }

    public int getValue(File file) throws IOException {
        BufferedImage bImage = ImageIO.read(file);
        int[][] image = ImageUtils.buildImageArray(bImage, true);
        int w = image[0].length;
        int h = image.length;
        int[][] integralI = new int[h][w];
        ImageUtils.buildIntegralImage(image, integralI, w, h);

        return this.getValue(integralI);
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public static List<Feature> generateAllFeatures() {
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
        return featureList;
    }
}
