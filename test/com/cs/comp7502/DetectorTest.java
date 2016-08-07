package com.cs.comp7502;

import com.cs.comp7502.utils.ColourUtils;
import org.junit.Test;

import static org.junit.Assert.*;

public class DetectorTest {

    @Test
    public void setIntensity() throws Exception {
        // prepare
        int[][] testInput = new int[][] {
                new int[] { 6,  6,  6,  6},
                new int[] { 6,  6,  6,  6},
                new int[] { 6,  6,  6,  6},
                new int[] { 6,  6,  6,  6}
        };

        int[][] expectedI = new int[][] {
                new int[] { 2,  4,  6,  8},
                new int[] { 4,  10, 18,  28},
                new int[] { 6,  18,  38,  68},
                new int[] { 8,  28,  68,  138},
        };

        int[][] expectedI2 = new int[][] {
                new int[] { 4,  8,  12,  16},
                new int[] { 8,  20,  36,  56},
                new int[] { 12,  36,  76,  136},
                new int[] { 16,  56, 136,  276},
        };

        int[][] image = new int[4][4];
        int[][] image2 = new int[4][4];

        Detector detector = new Detector();

        // execute
        detector.setIntensity(testInput, image, image2, ColourUtils.Grayscale.INTENSITY);

        // verify
        assertArrayEquals(expectedI, image);
        assertArrayEquals(expectedI2, image2);
    }

}