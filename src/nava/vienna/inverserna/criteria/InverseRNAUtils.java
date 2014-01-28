/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.vienna.inverserna.criteria;

import nava.vienna.Structure;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class InverseRNAUtils {

    public static double calculateGC(String sequence) {
        double count = 0;
        for (int i = 0; i < sequence.length(); i++) {
            if (sequence.charAt(i) == 'G' || sequence.charAt(i) == 'C') {
                count++;
            }
        }
        return count / ((double) sequence.length());
    }

    public static double calculatePairedSitesGCContent(int[] pairedSites, String sequence) {
        double count = 0;
        double total = 0;
        for (int i = 0; i < pairedSites.length; i++) {
            if (pairedSites[i] != 0) {
                if (sequence.charAt(i) == 'G' || sequence.charAt(i) == 'C') {
                    count++;
                }
                total++;
            }
        }

        if (total == 0) {
            return 0.5;
        } else {
            return count / total;
        }
    }
    
    public static double calculatedUnpairedGCContent(int [] pairedSites, String sequence)
    {
        double count = 0;
        double total = 0;
        for(int i = 0 ; i < pairedSites.length ; i++)
        {
            if(pairedSites[i] == 0)
            {
                if(sequence.charAt(i) == 'G' || sequence.charAt(i) == 'C')
                {
                    count++;
                }
                total++;
            }
        }
        
        if(total == 0)
        {
            return 0.5;
        }
        else
        {
            return count / total;
        }
    }
}
