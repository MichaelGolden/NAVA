/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.utils;

import java.io.File;
import java.util.ArrayList;
import nava.data.io.IO;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class AlignmentTools {
    
    public static void main(String [] args)
    {
        ArrayList<String> sequences = new ArrayList<>();
        ArrayList<String> sequenceNames = new ArrayList<>();
        //IO.loadFastaSequences(new File("examples/alignments/Crab_rRNA.fas"), sequences, sequenceNames);
        IO.loadFastaSequences(new File("examples/alignments/hiv500.fas"), sequences, sequenceNames);
        double [] nucDiversity = calculateNucleotideDiversity(sequences);
        for(int i = 0 ; i  < nucDiversity.length ; i++)
        {
            System.out.println((i+1)+"\t"+nucDiversity[i]);
        }
    }

    /*
     * A Simple Method for Estimating Average Number of Nucleotide Substitutions Within and Between Populations From Restriction Data
     * Masatoshi Nei and Joyce C. Miller
     */
    public static double[] calculateNucleotideDiversity(ArrayList<String> sequences) {
        AmbiguityCodes ambiguityCodes = new AmbiguityCodes();
        double[][] nucFrequency = new double[sequences.get(0).length()][5]; // A, C, G, T, gap
        for (String sequence : sequences) {
            for (int i = 0; i < sequence.length(); i++) {
                double[] val = ambiguityCodes.getBaseScores(sequence.charAt(i) + "");
                nucFrequency[i][0] += val[0];
                nucFrequency[i][1] += val[1];
                nucFrequency[i][2] += val[2];
                nucFrequency[i][3] += val[3];
                nucFrequency[i][4] += val[4];
            }
        }

        double n = sequences.size();
        for (int i = 0; i < nucFrequency.length; i++) {
            nucFrequency[i][0] /= n;
            nucFrequency[i][1] /= n;
            nucFrequency[i][2] /= n;
            nucFrequency[i][3] /= n;
            nucFrequency[i][4] /= n;
        }
        
        double [] nucleotideDiversity = new double[nucFrequency.length];
        for(int i = 0 ; i < nucleotideDiversity.length ; i++)
        {
            nucleotideDiversity[i] = 1;
            for(int j = 0 ; j < 5 ; j++)
            {
                nucleotideDiversity[i] -= nucFrequency[i][j]*nucFrequency[i][j];
            }
            nucleotideDiversity[i] *= (n/(n-1));
        }

        return nucleotideDiversity;
    }
}
