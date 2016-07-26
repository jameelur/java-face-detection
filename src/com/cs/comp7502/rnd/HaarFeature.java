package com.cs.comp7502.rnd;

import java.util.List;

/**
 * Created by rmohamed on 7/26/2016.
 */
public class HaarFeature {
    int type;
    List<Integer> featureVector;

    public HaarFeature(int type, List<Integer> featureVector) {
        this.type = type;
        this.featureVector = featureVector;
    }
}
