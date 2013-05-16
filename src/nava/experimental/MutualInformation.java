/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.experimental;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import nava.data.io.IO;
import nava.utils.AmbiguityCodes;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class MutualInformation {

    public static double calculateMutualInformation(List<String> sequences, int col1, int col2) {
        AmbiguityCodes ambiguityCodes = new AmbiguityCodes();

        char[] letters = {'A', 'C', 'G', 'T'};
        int[][] pairs = {{0, 0, 0, 1},
            {0, 0, 1, 0},
            {0, 1, 0, 0},
            {1, 0, 0, 0}};

        double[][] expected = new double[4][4]; // ACGT, ACGT
        double[][] observed = new double[4][4]; // ACGT, ACGT

        double[] col1observed = new double[4];
        double[] col2observed = new double[4];
        double t = 0;
        for (String seq : sequences) {
            double[] pos1 = ambiguityCodes.getBaseScores(seq.charAt(col1) + "");
            double[] pos2 = ambiguityCodes.getBaseScores(seq.charAt(col2) + "");
            for (int i = 0; i < col1observed.length; i++) {
                col1observed[i] += pos1[i];
                col2observed[i] += pos2[i];
            }

            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    observed[i][j] += pos1[i] * pos2[j];
                }
            }

            t++;
        }
        for (int i = 0; i < col1observed.length; i++) {
            col1observed[i] /= t;
            col2observed[i] /= t;
            //System.out.println(i + "\t" + col1observed[i] + "\t" + col2observed[i]);
        }

        for (int i = 0; i < observed.length; i++) {
            for (int j = 0; j < observed[0].length; j++) {
                observed[i][j] /= t;
            }
        }

        for (int i = 0; i < col1observed.length; i++) {
            for (int j = 0; j < col2observed.length; j++) {
                expected[i][j] = col1observed[i] * col2observed[j];
                //System.out.println(letters[i] + "" + letters[j] + "\t" + expected[i][j] + "\t" + observed[i][j]);
            }
        }

        double chi2 = 0;
        for (int i = 0; i < pairs.length; i++) {
            for (int j = 0; j < pairs.length; j++) {
                if (pairs[i][j] == 1 && expected[i][j] > 0) {
                    chi2 += Math.pow(observed[i][j] - expected[i][j], 2) / expected[i][j];
                }
            }
        }

       // System.out.println(t * chi2);

        return chi2*t;
    }

    public static void main(String[] args) {
        ArrayList<String> sequences = new ArrayList<>();
        ArrayList<String> sequenceNames = new ArrayList<>();
        IO.loadFastaSequences(new File("C:/dev/thesis/jev/200/all_200_aligned.fas"), sequences, sequenceNames);
        for (int i = 0; i < 100; i++) {
            for (int j = i+1; j < i + 101 ; j++) {
                System.out.println(i + "\t" + j + "\t" + calculateMutualInformation(sequences, i, j));
            }
        }
    }
}
