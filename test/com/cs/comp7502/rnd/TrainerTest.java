package com.cs.comp7502.rnd;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by rmohamed on 7/26/2016.
 */
public class TrainerTest {
    @Test
    public void trainFaces() throws Exception {
        // prepare

        // execture
        Trainer.trainFaces();

        // verify
    }

    @Test
    public void train() throws Exception {
        // prepare
        int[][] testI = new int[24][24];
        for (int i = 0; i < 24; i++){
            int[] col = new int[24];
            Arrays.fill(col, 2);
            testI[i] = col;
        }

        // execute
        List<HaarFeature> train = Trainer.train(testI, 1);

        // verify
        HaarFeature haarFeature1 = train.get(0);
        HaarFeature haarFeature2 = train.get(1);

        assertEquals(6, haarFeature1.getWidth());
        assertEquals(6, haarFeature2.getWidth());
        assertEquals(3, haarFeature1.getHeight());
        assertEquals(6, haarFeature2.getHeight());

        assertEquals(21, haarFeature1.getFeatureVector().size());
        assertEquals(21, haarFeature1.getFeatureVector().size());
    }

}