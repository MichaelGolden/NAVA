/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import nava.data.io.IO;

/**
 *
 * @author Michael Golden
 */
public class AlignmentUtils {

    public static void impute(ArrayList<String> sequences, int end) {
        double[][] distanceMatrix = getDistanceMatrix(sequences);

        for (int i = 0; (i < end || end == -1) && i < sequences.size(); i++) {
            for (int pos = 0; pos < sequences.get(i).length(); pos++) {
                double minDistance = -1;
                int closestSeq = -1;
                if (sequences.get(i).charAt(pos) == '-') {
                    for (int j = 0; j < sequences.size(); j++) {
                        if (pos < sequences.get(j).length() && sequences.get(j).charAt(pos) != '-') {
                            if (closestSeq == -1 || distanceMatrix[i][j] < minDistance) {
                                minDistance = distanceMatrix[i][j];
                                closestSeq = j;
                            }
                        }
                    }
                    StringBuffer buf = new StringBuffer(sequences.get(i));
                    if (closestSeq != -1) {
                        buf.setCharAt(pos, sequences.get(closestSeq).charAt(pos));
                        sequences.set(i, buf.toString());
                    }
                }
            }
        }
    }

    public static double[] getWeights(ArrayList<String> sequences) {
        double[][] distanceMatrix = getDistanceMatrix(sequences);
        double[] weights = new double[sequences.size()];
        for (int i = 0; i < weights.length; i++) {
            for (int j = 0; j < weights.length; j++) {
                weights[i] += distanceMatrix[i][j];
            }
            weights[i] /= weights.length;

        }
        return weights;
    }

    public static double[][] getNucleotideComposition(ArrayList<String> sequences, double[] weights) {
        int seqLength = sequences.get(0).length();
        double[][] nucleotideComposition = new double[seqLength][5];
        for (int i = 0; i < seqLength; i++) {
            for (int j = 0; j < sequences.size(); j++) {
                char c = sequences.get(j).charAt(i);
                switch (c) {
                    case 'A':
                        nucleotideComposition[i][0] += weights[j];
                        break;
                    case 'C':
                        nucleotideComposition[i][1] += weights[j];
                        break;
                    case 'G':
                        nucleotideComposition[i][2] += weights[j];
                        break;
                    case 'T':
                        nucleotideComposition[i][3] += weights[j];
                        break;
                    default:
                        nucleotideComposition[i][4] += weights[j];
                        break;
                }
            }
        }

        for (int i = 0; i < seqLength; i++) {
            double gapsum = 0;
            double sum = 0;
            for (int j = 0; j < 5; j++) {
                gapsum += nucleotideComposition[i][j];
                if (j != 4) {
                    sum += nucleotideComposition[i][j];
                }
            }

            String s = i + "\t";
            for (int j = 0; j < 5; j++) {
                s += (nucleotideComposition[i][j] / gapsum) + "\t";
            }
            for (int j = 0; j < 5; j++) {
                s += (nucleotideComposition[i][j] / sum) + "\t";
            }
        }

        return nucleotideComposition;
    }

    public static double[] getSequenceLogoAtI(ArrayList<String> sequences, int i, double[] weights) {
        int n = 0;
        for (int j = 0; j < sequences.size(); j++) {
            if (sequences.get(j).charAt(i) != '-') {
                n++;
            }
        }
        double fa[] = getFrequenciesAtI(sequences, i, weights);

        return getSequenceLogo(fa, n);
    }

    public static double[] getFrequenciesAtI(ArrayList<String> sequences, int i, double[] weights) {
        double[] fa = new double[4];

        for (int j = 0; j < sequences.size(); j++) {
            char c = sequences.get(j).charAt(i);
            switch (c) {
                case 'A':
                    fa[0] += weights[j];
                    break;
                case 'C':
                    fa[1] += weights[j];
                    break;
                case 'G':
                    fa[2] += weights[j];
                    break;
                case 'T':
                    fa[3] += weights[j];
                    break;
            }
        }

        double t = fa[0] + fa[1] + fa[2] + fa[3];
        for (int k = 0; k < 4; k++) {
            fa[k] = fa[k] / t;
        }

        return fa;
    }

    public static double[] getSequenceLogo(double[] fa, int n) {
        double[] ha = new double[4];

        double Hi = 0;
        double en = 3.0 / (2 * Math.log(2) * n);
        en = 0;
        for (int a = 0; a < 4; a++) {
            double log2fa = Math.log(fa[a]) / Math.log(2);

            if (fa[a] == 0) {
                Hi += 0;
            } else {
                Hi += -(log2fa * fa[a]);
            }
        }

        double Ri = 2 - (Hi + en);
        for (int a = 0; a < 4; a++) {
            ha[a] = fa[a] * Ri;
        }

        return ha;
    }

    public static double[][] getDistanceMatrix(ArrayList<String> sequences) {
        int len = sequences.size();
        double[][] distanceMatrix = new double[len][len];
        for (int i = 0; i < len; i++) {
            for (int j = 0; j < len; j++) {
                distanceMatrix[i][j] = distanceIgnoringGaps(sequences.get(i), sequences.get(j));
            }
        }

        return distanceMatrix;
    }

    public static int distanceIgnoringGaps(String seq1, String seq2) {
        int dist = 0;
        int length = Math.min(seq1.length(), seq2.length());
        for (int i = 0; i < length; i++) {
            if (seq1.charAt(i) != '-' && seq2.charAt(i) != '-' && seq1.charAt(i) != seq2.charAt(i)) {
                dist += 1;
            }
        }

        return dist;
    }

    /*
     * A Simple Method for Estimating Average Number of Nucleotide Substitutions
     * Within and Between Populations From Restriction Data Masatoshi Nei and
     * Joyce C. Miller
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

        double[] nucleotideDiversity = new double[nucFrequency.length];
        for (int i = 0; i < nucleotideDiversity.length; i++) {
            nucleotideDiversity[i] = 1;
            for (int j = 0; j < 5; j++) {
                nucleotideDiversity[i] -= nucFrequency[i][j] * nucFrequency[i][j];
            }
            nucleotideDiversity[i] *= (n / (n - 1));
        }

        return nucleotideDiversity;
    }
    public static final String nucleotides = "ACGTU";
    //public static final char [] nucleotides = {'A','C','G','T','U'};
    public static final String ambiguousNucleotides = "YRWSKMDVHBNX";
    //char [] ambiguousNucleotides = {'Y','R','W','S','K','M','D','V','H','B','N','X'};
    public static final String aminoAcids = "ARNDCQEGHILKMFPSTWYVX";
    //char [] aminoAcids = {'A','R','N','D','C','Q','E',
    public static final String validAminoAcids = "ARNDCQEGHILKMFPSTWYV";
    public static final String stopCodon = "*";

    public static AlignmentType guessAlignmentType(ArrayList<String> sequences) {
        Random random = new Random(5129880201392134141L);
        double standardNucCount = 0;
        double ambiguousNucCount = 0;
        double nucCount = 0;
        double aminoAcidCount = 0;
        double aminoAcidExcludingNucCount = 0;
        double total = 0;
        for (int j = 0 ; j < 50 ; j++) {
            String sequence = sequences.get(random.nextInt(sequences.size()));
            int length = sequence.length();
            int randomStartPos = random.nextInt(length);
            for (int i = 0; i < 1000 ; i++) {
                int pos = (randomStartPos + i) % length;
                String s = sequence.charAt(pos) + "";

                if (s.equals("-")) {
                    continue;
                }

                boolean isNuc = true;
                if (nucleotides.contains(s)) {
                    standardNucCount++;
                    nucCount++;
                } else if (ambiguousNucleotides.contains(s)) {
                    ambiguousNucCount++;
                    nucCount++;
                } else {
                    isNuc = false;
                }

                if (aminoAcids.contains(s)) {
                    aminoAcidCount++;
                    if (!isNuc) {
                        aminoAcidExcludingNucCount++;
                    }
                } else if (!isNuc) {
                    //System.out.println(s);
                }

                total++;
            }
        }
        
        if((nucCount / total) >= (aminoAcidCount / total)*0.98)
        {
            
        }
        else
        {
            return AlignmentType.PROTEIN_ALIGNMENT;
        }


        double validCodons = 0;
        double invalidCodons = 0;
        double stopCodons = 0;
        double totalTriplets = 0;
         for (int j = 0 ; j < 20 ; j++) {
            String proteinTranslation = GeneticCode.translateNucleotideSequence(sequences.get(random.nextInt(sequences.size())));
            int length = proteinTranslation.length();
            int randomStartPos = random.nextInt(length);
            for (int i = 0; i < 1000 ; i++) {
                int pos = (randomStartPos + i) % length;
                String s = proteinTranslation.charAt(pos) + "";
                if (validAminoAcids.contains(s)) {
                    validCodons++;
                    totalTriplets++;
                } else if ("X".equals(s)) {
                    invalidCodons++;
                    totalTriplets++;
                } else if (stopCodon.contains(s)) {
                    stopCodons++;
                    totalTriplets++;
                }

            }
        }
         /*
        System.out.println((standardNucCount / total));
        System.out.println((ambiguousNucCount / total));
        System.out.println((nucCount / total));
        System.out.println((aminoAcidCount / total));
        System.out.println((aminoAcidExcludingNucCount / total));
        System.out.println((validCodons / totalTriplets));
        System.out.println((invalidCodons / totalTriplets));
        System.out.println((stopCodons / totalTriplets));
        System.out.println(total+"\t"+totalTriplets);
*/
        if(validCodons / totalTriplets >= 0.90)
        {
            return AlignmentType.CODON_ALIGNMENT;
        }
        else
        {
            return AlignmentType.NUCLEOTIDE_ALIGNMENT;
        }
    }

    public static void main(String[] args) {
        ArrayList<String> sequences = new ArrayList<>();
        ArrayList<String> sequenceNames = new ArrayList<>();
        IO.loadFastaSequences(new File("examples/alignments/hcv_coding.fas"), sequences, sequenceNames);
        System.out.println(AlignmentUtils.guessAlignmentType(sequences));
        System.out.println();
        sequences = new ArrayList<>();
        sequenceNames = new ArrayList<>();
        IO.loadFastaSequences(new File("examples/alignments/hiv500.fas"), sequences, sequenceNames);
        System.out.println(AlignmentUtils.guessAlignmentType(sequences));
        System.out.println();
        sequences = new ArrayList<>();
        sequenceNames = new ArrayList<>();
        IO.loadFastaSequences(new File("examples/alignments/random_protein.fas"), sequences, sequenceNames);
        System.out.println(AlignmentUtils.guessAlignmentType(sequences));
    }
}
