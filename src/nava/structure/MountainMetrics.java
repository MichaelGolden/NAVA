/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.structure;

import java.util.Arrays;
import nava.utils.RNAFoldingTools;

/**
 * Based on Moulton et al.
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class MountainMetrics {

    public static void main(String[] args) {
        /*
         * int[] s3 =
         * RNAFoldingTools.getPairedSitesFromDotBracketString("(......)"); int[]
         * s4 = RNAFoldingTools.getPairedSitesFromDotBracketString("..(..)..");
         * System.out.println(MountainMetrics.calculateMountainDistance(s3,
         * MountainMetrics.getStructureZero(8), 1));
         * System.out.println(MountainMetrics.calculateMountainDistance(s4,
         * MountainMetrics.getStructureZero(8), 1));
         */

        int[] s1 = RNAFoldingTools.getPairedSitesFromDotBracketString("...(..).......");
        int[] sz = RNAFoldingTools.getPairedSitesFromDotBracketString("..............");
        System.out.println(calculateWeightedMountainDistance(s1, sz));
    }

    public static double calculateNormalizedWeightedMountainDistance(int[] pairedSites1, int[] pairedSites2) {        
        return calculateWeightedMountainDistance(pairedSites1, pairedSites2) / calculateWeightedMountainDiameter(pairedSites1.length);
    }

    public static double calculateWeightedMountainDistance(int[] pairedSites1, int[] pairedSites2) {
        double[] f1 = getMountainVector(pairedSites1, true);
        double[] f2 = getMountainVector(pairedSites2, true);
        double d = 0;
        for (int i = 0; i < f1.length; i++) {
            d += Math.abs(f1[i] - f2[i]);
        }
        return d;
    }

    public static double calculateWeightedMountainDiameter(int length) {
        return calculateWeightedMountainDistance(getStructureStar(length), getStructureZero(length));
    }

    public static double calculateMountainDistance(int[] pairedSites1, int[] pairedSites2, int p) {
        double[] f1 = getMountainVector(pairedSites1, false);
        double[] f2 = getMountainVector(pairedSites2, false);
        double d = 0;
        for (int i = 0; i < f1.length; i++) {
            d += Math.pow(Math.abs(f1[i] - f2[i]), p);
        }
        return Math.pow(d, 1 / p);
    }

    public static double[] getMountainVector(int[] pairedSites, boolean weighted) {
        String s1 = RNAFoldingTools.getDotBracketStringFromPairedSites(pairedSites);
        double[] f1 = new double[pairedSites.length];

        double w = 1;
        if (weighted) {
            w = 1 / (double) Math.abs(pairedSites[0] - 1 - 0);
        }
        if (s1.charAt(0) == '(') {
            f1[0] += w;
        } else if (s1.charAt(0) == ')') {
            f1[0] -= w;
        }

        for (int i = 1; i < pairedSites.length; i++) {
            if (weighted) {
                w = 1 / (double) Math.abs(pairedSites[i] - 1 - i);
            }
            
            f1[i] = f1[i - 1];
            if (s1.charAt(i) == '(') {
                f1[i] += w;
            } else if (s1.charAt(i) == ')') {
                f1[i] -= w;
            }
        }
        return f1;
    }

    public static int[] getStructureStar(int length) {
        String dotBracket;
        if (length % 2 == 0) // even
        {
            dotBracket = nChars('(', (length - 2) / 2) + ".." + nChars(')', (length - 2) / 2);
        } else // odd
        {
            dotBracket = nChars('(', (length - 1) / 2) + "." + nChars(')', (length - 1) / 2);
        }
        return RNAFoldingTools.getPairedSitesFromDotBracketString(dotBracket);
    }

    public static int[] getStructureZero(int length) {
        int[] structureZero = new int[length];
        Arrays.fill(structureZero, 0); // probably not necessary
        return structureZero;
    }

    public static String nChars(char c, int length) {
        String ret = "";
        for (int i = 0; i < length; i++) {
            ret += c;
        }
        return ret;
    }
}
