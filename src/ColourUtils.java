/**
 * Created by rmohamed on 7/24/2016.
 */
public class ColourUtils {

    public enum Grayscale {
        INTENSITY,
        LUMINANCE;
    }

    public static int convertToG(int r, int g, int b, Grayscale type) {
        if (type == Grayscale.LUMINANCE) {
            return (int) Math.round((30*r +59*g +11*b)/100.0);
        } else {
            return (int) Math.round((r+g+b)/3.0);
        }
    }
}
