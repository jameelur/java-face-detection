package com.cs.comp7502.data;

import com.cs.comp7502.ImageUtils;

import java.util.HashMap;
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

    private static Map<Integer, int[]> FEATURE_MAP = new HashMap<>();

    static {
        FEATURE_MAP.put(FEATURE_TYPE_1, new int[]{1,2});
        FEATURE_MAP.put(FEATURE_TYPE_2, new int[]{1,3});
        FEATURE_MAP.put(FEATURE_TYPE_3, new int[]{2,1});
        FEATURE_MAP.put(FEATURE_TYPE_4, new int[]{3,1});
        FEATURE_MAP.put(FEATURE_TYPE_5, new int[]{2,2});
    }

    public static int colCount(int type){
        return FEATURE_MAP.get(type)[0];
    }

    public static int rowCount(int type){
        return FEATURE_MAP.get(type)[1];
    }

    private int type;

    private int x;
    private int y;

    private int width;
    private int height;

    private double error;
    private double threshold;
    private int polarity;

    public Feature(int type, int x, int y, int width, int height) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
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
}
