/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.experimental;

import nava.structure.MountainMetrics;
import nava.utils.RNAFoldingTools;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class MountainTest {
    public static void main(String [] args)
    {
        int [] p1 = RNAFoldingTools.getPairedSitesFromDotBracketString("((..((...))....))");
        int [] p2 = RNAFoldingTools.getPairedSitesFromDotBracketString("((....((..))...))");
        double d = MountainMetrics.calculateMountainDistance(p1, p2, 1);
        System.out.println("Distance n = "+ MountainMetrics.calculateNormalizedWeightedMountainDistance(p1, p2));
        System.out.println("Distance d = "+d);
    }    
}
