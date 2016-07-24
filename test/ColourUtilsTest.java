import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by rmohamed on 7/24/2016.
 */
public class ColourUtilsTest {
    @Test
    public void convertToG_whenTypeIntensity() throws Exception {
        // prepare
        int r = 100;
        int g = 25;
        int b = 30;

        int expectedG = 52;

        // execute
        int actualG = ColourUtils.convertToG(r,g,b, ColourUtils.Grayscale.INTENSITY);

        // verify
        assertEquals(expectedG, actualG);
    }

    @Test
    public void convertToG_whenTypeLuminance() throws Exception {
        // prepare
        int r = 100;
        int g = 25;
        int b = 30;

        int expectedG = 48;

        // execute
        int actualG = ColourUtils.convertToG(r,g,b, ColourUtils.Grayscale.LUMINANCE);

        // verify
        assertEquals(expectedG, actualG);
    }

}