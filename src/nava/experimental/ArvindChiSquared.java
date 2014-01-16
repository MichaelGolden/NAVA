/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.experimental;

import org.apache.commons.math3.stat.inference.ChiSquareTest;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class ArvindChiSquared {
    public static void main(String [] args)
    {
        /*long [] [] counts = {{45, 75},
                               {39, 73}
                               };*/
        
        long [] [] counts = {{71, 72},
                               {59, 87}
                               };
        
        double [][] expected = SubstructureCoevolution.getExpectedValues(counts);
        
        String [] colLabels = {"No negative sel BFDV", "Negative sel BFDV"};
        String [] rowLabels = {"No negative sel PICV", "Negative sel PICV"};
        
        System.out.println("Observed:");
        SubstructureCoevolution.printTable(counts, colLabels, rowLabels);
        System.out.println();
        System.out.println("Expected:");
        SubstructureCoevolution.printTable(expected, colLabels, rowLabels);
        System.out.println();
        ChiSquareTest chiPairingTest = new ChiSquareTest();
        System.out.println("chi2 = "+chiPairingTest.chiSquare(counts));
        System.out.println("p-value = "+chiPairingTest.chiSquareTest(counts));
        
    }    
}
