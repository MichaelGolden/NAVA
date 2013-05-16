/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.experimental;

import java.io.File;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class Main {
    public static void main(String [] args)
    {
        GeneFinder.codonMAFFTalignment(new File("C:/dev/thesis/jev_tbv_westnile/visualisation/jev_westnile_tbv_polyprotein.fas"), new File("C:/dev/thesis/jev_tbv_westnile/visualisation/jev_westnile_tbv_polyprotein_aligned.fas"));
    }
}
