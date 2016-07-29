package com.cs.comp7502;

import com.cs.comp7502.data.Feature;
import com.cs.comp7502.data.Stage;

import java.util.ArrayList;

public class Adaboost {


    public static Stage train(ArrayList<Feature> features, int maxTrainingRounds){
        Stage stage = new Stage();

        for (int round = 0; round < maxTrainingRounds;  round++){
            // 1. find best stump, and the return values from the best stump should contain
            // error, threshold, polarity, and (optional) classified correct or not

            // 2. calculate the beta = error / (1 - error)
            // 3. calculate the alpha = log(1 / beta), but I am not sure the base of the log

            // 4. update the weights of training samples
            // if the sample is classified correct, newWeight = oldWeight * beta
            // if the sample is classified wrong, don't change the weight

            // 5. (optional) update total sum of positive / negative weight based on pre-assigned label

            // 6. add the best stump with alpha (the weight of this stump) into this stage

        }

        return stage;
    }

    class TrainingImage {

    }

    // find best stump
    // input: one feature, a set of training samples
    // 1. calculate feature values for all training samples based on given feature

    // 2. sort the feature value list

    // 3. pass over the feature value list to find best threshold
    // for each feature value we calculate the error for this threshold
    // error = min(Sp + (Tn - Sn), Sn + (Tp - Sp))
    // where Sp / Sn is the sum of positive / negative weight below this threshold
    // Tn / Tp is the total sum of positive / negative weight based on pre-assigned label

    // 4. get the threshold and polarity with minimum error
}
