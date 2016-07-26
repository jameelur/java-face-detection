/**
 * Created by Jinyi Wu on 26/07/2016.
 */
public class SimilarityComputation {


    public SimilarityComputation() {
        super();
    }

    public boolean cosSimilarity(int[] vector1, int[] vector2, double threshold) {
        if (vector1.length != vector2.length) {
            System.out.println("Length of two vectors are different");
            return false;
        }

        int innerDot = 0;
        int normSquare1 = 0, normSquare2 = 0;

        for (int i = 0; i < vector1.length; ++i) {
            innerDot += vector1[i] * vector2[i];
            normSquare1 += vector1[i] * vector1[i];
            normSquare2 += vector2[i] * vector2[i];
        }

        double similarity = (double) innerDot / (Math.sqrt(normSquare1) * Math.sqrt(normSquare2));

        return similarity >= threshold;

    }

    public boolean correlation(int[] vector1, int[] vector2, double threshold) {
        if (vector1.length != vector2.length) {
            System.out.println("Length of two vectors are different");
            return false;
        }

        int innerDot = 0;
        int sum1 = 0, sum2 = 0;
        int squareSum1 = 0, squareSum2 = 0;

        for (int i = 0; i < vector1.length; ++i) {
            innerDot += vector1[i] * vector2[i];
            sum1 += vector1[i];
            sum2 += vector2[i];
            squareSum1 += vector1[i] * vector1[i];
            squareSum2 += vector2[i] * vector2[i];
        }

        double rho = (double) (vector1.length * innerDot - sum1 * sum2) / (Math.sqrt(vector1.length * squareSum1 - sum1 * sum1) * Math.sqrt(vector1.length * squareSum2 - sum2 * sum2));

        return rho >= threshold;

    }
}
