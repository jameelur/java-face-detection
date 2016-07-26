package com.cs.comp7502;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by rmohamed on 7/26/2016.
 */
public class ImageUtilsTest {
    @Test
    public void buildIntegralImage() throws Exception {
        // prepare
        int[][] testI = new int[][] {
                new int[] { 2,  2,  2,  2},
                new int[] { 2,  2,  2,  2},
                new int[] { 2,  2,  2,  2},
                new int[] { 2,  2,  2,  2}
        };

        int[][] expectedI = new int[][] {
                new int[] { 2,  4,  6,  8},
                new int[] { 4,  8, 12,  16},
                new int[] { 6,  12,  18,  24},
                new int[] { 8,  16,  24,  32},
        };

        int[][] actual = new int[4][4];

        // execute
        ImageUtils.buildIntegralImage(testI, actual, 4, 4);

        // verify
        assertArrayEquals(expectedI, actual);
    }
}