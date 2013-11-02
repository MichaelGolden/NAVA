/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.structure;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import nava.structurevis.data.NucleotideComposition;
import nava.utils.AmbiguityCodes;
import nava.utils.RNAFoldingTools;
import nava.utils.Utils;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class StructureAlign {

    public static void saveConservedSubstructuresProgressiveScan(ArrayList<Structure> alignedStructures, ArrayList<String> alignedSequences, ArrayList<String> structureNames, int startWindowSize, double cutoff, String prefix) {
        boolean relaxed = true;

        ArrayList<String> nameCombinations = new ArrayList<>();
        for (int i = 0; i < alignedStructures.size(); i++) {
            for (int j = i + 1; j < alignedStructures.size(); j++) {
                nameCombinations.add(alignedStructures.get(i).name + " and " + alignedStructures.get(j).name);
            }
        }


        ArrayList<Region> conservedRegions = new ArrayList<>();
        int structureLength = alignedStructures.get(0).pairedSites.length;
        for (int a = 0; a < structureLength;) {

            int startPos = a;
            int length = startWindowSize;
            boolean addRegion = false;

            while (startPos + length <= structureLength) {
                double simmin = Double.MAX_VALUE;
                for (int i = 0; i < alignedStructures.size(); i++) {
                    for (int j = i + 1; j < alignedStructures.size(); j++) {
                        int[] pairedSitesSub1;
                        int[] pairedSitesSub2;
                        if (relaxed) {
                            pairedSitesSub1 = getSubstructureRelaxed(alignedStructures.get(i).pairedSites, startPos, length);
                            pairedSitesSub2 = getSubstructureRelaxed(alignedStructures.get(j).pairedSites, startPos, length);
                        } else {
                            pairedSitesSub1 = getSubstructure(alignedStructures.get(i).pairedSites, startPos, length);
                            pairedSitesSub2 = getSubstructure(alignedStructures.get(j).pairedSites, startPos, length);
                        }
                        double sim = 1 - MountainMetrics.calculateNormalizedWeightedMountainDistance(pairedSitesSub1, pairedSitesSub2);
                        simmin = Math.min(simmin, sim);
                    }
                }

                if (simmin >= cutoff) {
                    length++;
                    addRegion = true;
                } else {
                    break;
                }
            }

            //System.out.println(a);
            if (addRegion) {
                a = startPos + length - 1;
                conservedRegions.add(new Region(startPos, length - 1));
            } else {
                a = startPos + 1;
            }

            if (a + length > structureLength) {
                break;
            }
        }


        String name = "";
        for (int i = 0; i < alignedStructures.size(); i++) {
            name += alignedStructures.get(i).name + "_";
        }
        try {
            BufferedWriter buffer = new BufferedWriter(new FileWriter(new File(prefix + "conserved_" + name + startWindowSize + "_" + cutoff + ".txt")));
            int coverage = 0;
            for (int i = 0; i < conservedRegions.size(); i++) {
                Region r = conservedRegions.get(i);
                ArrayList<int[]> substructuresStrict = new ArrayList<>();
                ArrayList<int[]> substructuresRelaxed = new ArrayList<>();
                for (int j = 0; j < alignedStructures.size(); j++) {
                    Structure s = alignedStructures.get(j);
                    substructuresStrict.add(StructureAlign.getSubstructure(s.pairedSites, r.startPos, r.length));
                    substructuresRelaxed.add(StructureAlign.getSubstructureRelaxed(s.pairedSites, r.startPos, r.length));
                }

                buffer.write(">" + r.startPos + " - " + (r.startPos + r.length) + "(" + r.length + ") : ");
                for (int j = 0; j < alignedStructures.size(); j++) {
                    buffer.write(alignedStructures.get(j).name + "; ");
                }
                buffer.newLine();
                for (int j = 0; j < alignedSequences.size(); j++) {
                    buffer.write(structureNames.get(j) + " A\t" + alignedSequences.get(j).substring(r.startPos, r.startPos + r.length) + "\n");
                }
                for (int j = 0; j < alignedStructures.size(); j++) {
                    buffer.write(alignedStructures.get(j).name + " S\t" + RNAFoldingTools.getDotBracketStringFromPairedSites(substructuresStrict.get(j)) + "\n");
                }
                for (int j = 0; j < alignedStructures.size(); j++) {
                    buffer.write(alignedStructures.get(j).name + " R\t" + RNAFoldingTools.getDotBracketStringFromPairedSites(substructuresRelaxed.get(j)) + "\n");
                }

                int m = 0;
                for (int x = 0; x < alignedStructures.size(); x++) {
                    for (int y = x + 1; y < alignedStructures.size(); y++) {
                        double sim = 1 - MountainMetrics.calculateNormalizedWeightedMountainDistance(substructuresStrict.get(x), substructuresStrict.get(y));
                        buffer.write("S " + nameCombinations.get(m) + " mSim = " + sim + "\n");
                        m++;
                    }
                }
                m = 0;
                for (int x = 0; x < alignedStructures.size(); x++) {
                    for (int y = x + 1; y < alignedStructures.size(); y++) {
                        double sim = 1 - MountainMetrics.calculateNormalizedWeightedMountainDistance(substructuresRelaxed.get(x), substructuresRelaxed.get(y));
                        buffer.write("R " + nameCombinations.get(m) + " mSim = " + sim + "\n");
                        m++;
                    }
                }

                buffer.newLine();
                coverage += r.length;
            }
            buffer.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public enum Method {

        AND, MINIMUM, MEAN;

        @Override
        public String toString() {
            switch (this) {
                case AND:
                    return "AND";
                case MINIMUM:
                    return "Minimum";
                case MEAN:
                    return "Mean";
                default:
                    return "";
            }
        }
    }

    public static ArrayList<Region> getConservedStructures(ArrayList<Structure> alignedStructures, ArrayList<String> alignedSequences, ArrayList<String> structureNames, int windowSize, double cutoff, boolean relaxed, Method method) {

        ArrayList<double[]> pairwiseMountainSimiliarity = new ArrayList<>();
        ArrayList<String> nameCombinations = new ArrayList<>();
        for (int i = 0; i < alignedStructures.size(); i++) {
            for (int j = i + 1; j < alignedStructures.size(); j++) {
                pairwiseMountainSimiliarity.add(slidingWeightedMountainSimilarity(alignedStructures.get(i).pairedSites, alignedStructures.get(j).pairedSites, windowSize, relaxed));
                nameCombinations.add(alignedStructures.get(i).name + " and " + alignedStructures.get(j).name);
            }
        }



        boolean[] conservedPositions = null;

        switch (method) {
            case MINIMUM:
                conservedPositions = getConservedPositions(getMinimumVector(pairwiseMountainSimiliarity), cutoff, windowSize);
                break;
            case AND:
                ArrayList<boolean[]> conservedList = new ArrayList<>();
                for (int m = 0; m < pairwiseMountainSimiliarity.size(); m++) {
                    conservedList.add(getConservedPositions(pairwiseMountainSimiliarity.get(m), cutoff, windowSize));
                }
                conservedPositions = ANDvectors(conservedList);
                break;
            case MEAN:
                conservedPositions = getConservedPositions(getMeanVector(pairwiseMountainSimiliarity), cutoff, windowSize);
                break;
        }

        ArrayList<Region> conservedRegions = getConservedRegions(conservedPositions);
        double[] avgSim = getMeanVector(pairwiseMountainSimiliarity);
        for (Region conservedRegion : conservedRegions) {
            conservedRegion.score = 0;
            int end = Math.min(avgSim.length, conservedRegion.startPos + conservedRegion.length - windowSize + 1);
            for (int i = conservedRegion.startPos; i < end; i++) {
                conservedRegion.score += avgSim[i];
            }
            conservedRegion.score /= ((double) Math.max(1, end - conservedRegion.startPos));
        }

        return conservedRegions;
    }

    public static void saveConservedSubstructures(ArrayList<Structure> alignedStructures, ArrayList<String> alignedSequences, ArrayList<String> structureNames, int windowSize, double cutoff, String prefix, boolean useMinMethod) {
        boolean relaxed = true;

        ArrayList<double[]> pairwiseMountainSimiliarity = new ArrayList<>();
        ArrayList<String> nameCombinations = new ArrayList<>();
        for (int i = 0; i < alignedStructures.size(); i++) {
            for (int j = i + 1; j < alignedStructures.size(); j++) {
                pairwiseMountainSimiliarity.add(slidingWeightedMountainSimilarity(alignedStructures.get(i).pairedSites, alignedStructures.get(j).pairedSites, windowSize, relaxed));
                nameCombinations.add(alignedStructures.get(i).name + " and " + alignedStructures.get(j).name);
            }
        }


        boolean[] conservedPositions = null;

        if (useMinMethod) {
            conservedPositions = getConservedPositions(getMinimumVector(pairwiseMountainSimiliarity), cutoff, windowSize);

        } else {
            ArrayList<boolean[]> conservedList = new ArrayList<>();
            for (int m = 0; m < pairwiseMountainSimiliarity.size(); m++) {
                conservedList.add(getConservedPositions(pairwiseMountainSimiliarity.get(m), cutoff, windowSize));
            }
            conservedPositions = ANDvectors(conservedList);
        }

        ArrayList<Region> conservedRegions = getConservedRegions(conservedPositions);


        String name = "";
        for (int i = 0; i < alignedStructures.size(); i++) {
            name += alignedStructures.get(i).name + "_";
        }
        try {
            BufferedWriter buffer = new BufferedWriter(new FileWriter(new File(prefix + "conserved_" + name + windowSize + "_" + cutoff + ".txt")));
            int coverage = 0;
            for (int i = 0; i < conservedRegions.size(); i++) {
                Region r = conservedRegions.get(i);
                ArrayList<int[]> substructuresStrict = new ArrayList<>();
                ArrayList<int[]> substructuresRelaxed = new ArrayList<>();
                for (int j = 0; j < alignedStructures.size(); j++) {
                    Structure s = alignedStructures.get(j);
                    substructuresStrict.add(StructureAlign.getSubstructure(s.pairedSites, r.startPos, r.length));
                    substructuresRelaxed.add(StructureAlign.getSubstructureRelaxed(s.pairedSites, r.startPos, r.length));
                }

                buffer.write(">" + r.startPos + " - " + (r.startPos + r.length) + "(" + r.length + ") : ");
                for (int j = 0; j < alignedStructures.size(); j++) {
                    buffer.write(alignedStructures.get(j).name + "; ");
                }
                buffer.newLine();
                for (int j = 0; j < alignedSequences.size(); j++) {
                    buffer.write(structureNames.get(j) + " A\t" + alignedSequences.get(j).substring(r.startPos, r.startPos + r.length) + "\n");
                }
                for (int j = 0; j < alignedStructures.size(); j++) {
                    buffer.write(alignedStructures.get(j).name + " S\t" + RNAFoldingTools.getDotBracketStringFromPairedSites(substructuresStrict.get(j)) + "\n");
                }
                for (int j = 0; j < alignedStructures.size(); j++) {
                    buffer.write(alignedStructures.get(j).name + " R\t" + RNAFoldingTools.getDotBracketStringFromPairedSites(substructuresRelaxed.get(j)) + "\n");
                }

                int m = 0;
                for (int x = 0; x < alignedStructures.size(); x++) {
                    for (int y = x + 1; y < alignedStructures.size(); y++) {
                        double sim = 1 - MountainMetrics.calculateNormalizedWeightedMountainDistance(substructuresStrict.get(x), substructuresStrict.get(y));
                        buffer.write("S " + nameCombinations.get(m) + " mSim = " + sim + "\n");
                        m++;
                    }
                }
                m = 0;
                for (int x = 0; x < alignedStructures.size(); x++) {
                    for (int y = x + 1; y < alignedStructures.size(); y++) {
                        double sim = 1 - MountainMetrics.calculateNormalizedWeightedMountainDistance(substructuresRelaxed.get(x), substructuresRelaxed.get(y));
                        buffer.write("R " + nameCombinations.get(m) + " mSim = " + sim + "\n");
                        m++;
                    }
                }

                buffer.newLine();
                coverage += r.length;
            }
            System.out.println(windowSize + "\t" + cutoff + "\t" + coverage);
            buffer.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static ArrayList<Region> getConservedRegions(boolean[] conservedPositions) {
        ArrayList<Region> conservedRegions = new ArrayList<>();
        for (int i = 0; i < conservedPositions.length;) {
            int startPos = i;
            int length = 0;
            while (conservedPositions[i] && i < conservedPositions.length) {
                i++;
                length++;
            }

            if (length > 0) {
                conservedRegions.add(new Region(startPos, length));
            }

            i++;
        }
        return conservedRegions;
    }

    public static void saveConservationTable(ArrayList<Structure> alignedStructures, int windowSize, String prefix) {
        boolean relaxed = true;

        ArrayList<double[]> pairwiseMountainSimiliarity = new ArrayList<>();
        ArrayList<String> nameCombinations = new ArrayList<>();
        for (int i = 0; i < alignedStructures.size(); i++) {
            for (int j = i + 1; j < alignedStructures.size(); j++) {
                pairwiseMountainSimiliarity.add(slidingWeightedMountainSimilarity(alignedStructures.get(i).pairedSites, alignedStructures.get(j).pairedSites, windowSize, relaxed));
                nameCombinations.add(alignedStructures.get(i).name + " and " + alignedStructures.get(j).name);
            }
        }

        double[] cutoffs = {0.5, 0.55, 0.6, 0.65, 0.7, 0.75, 0.8, 0.85, 0.9, 0.95};
        ArrayList<boolean[]> andVectors = new ArrayList<>();
        ArrayList<String[]> bitStringVectors = new ArrayList<>();
        for (double cutoff : cutoffs) {
            ArrayList<boolean[]> conservedList = new ArrayList<>();
            for (int m = 0; m < pairwiseMountainSimiliarity.size(); m++) {
                conservedList.add(getConservedPositions(pairwiseMountainSimiliarity.get(m), cutoff, windowSize));
            }
            andVectors.add(ANDvectors(conservedList));
            bitStringVectors.add(booleanStrings(conservedList));
        }


        try {
            BufferedWriter buffer = new BufferedWriter(new FileWriter(new File(prefix + "conservation_table_" + windowSize + ".txt")));
            int n = alignedStructures.size();
            int length = (n * n + n) / 2 - n;
            buffer.write("A = conserved in all\n");
            for (int m = 0; m < nameCombinations.size(); m++) {
                String s = "";
                for (int k = 0; k < nameCombinations.size(); k++) {
                    if (k == m) {
                        s += "1";
                    } else {
                        s += ".";
                    }
                }
                s += " = conserved in " + nameCombinations.get(m);
                buffer.write(s + "\n");
            }
            buffer.write("Window size = " + windowSize);
            for (int i = 0; i < nameCombinations.size(); i++) {
                buffer.write("\t");
            }
            buffer.write("\tSimilarity cut-off\n");
            buffer.write("Position\t");
            for (int m = 0; m < nameCombinations.size(); m++) {
                buffer.write(nameCombinations.get(m) + "\t");
            }
            for (int i = 0; i < cutoffs.length; i++) {
                buffer.write(cutoffs[i] + "\t");
            }
            buffer.newLine();
            for (int k = 0; k < andVectors.get(0).length; k++) {
                buffer.write(k + "\t");

                for (int m = 0; m < pairwiseMountainSimiliarity.size(); m++) {
                    if (k < andVectors.get(0).length - windowSize) {
                        buffer.write(pairwiseMountainSimiliarity.get(m)[k] + "\t");
                    } else {
                        buffer.write("-\t");
                    }
                }

                for (int i = 0; i < andVectors.size(); i++) {
                    String bitString = bitStringVectors.get(i)[k];
                    if (andVectors.get(i)[k]) {
                        buffer.write("A\t");
                    } else if (bitString.matches("^0+")) {
                        buffer.write(".\t");
                    } else {
                        buffer.write(bitStringVectors.get(i)[k].replace("0", ".") + "\t");
                    }
                }
                buffer.newLine();
            }
            buffer.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static String getString(double[] vector) {
        String ret = "[";
        for (int i = 0; i < vector.length; i++) {
            ret += vector[i] + ",";
        }
        ret += "]";
        return ret;
    }

    public static void saveConservationSummary(ArrayList<Structure> alignedStructures, int windowSize, double cutoff, boolean relaxed, File outFile) {
        try {
            BufferedWriter buffer = new BufferedWriter(new FileWriter(outFile));

            buffer.write(getConservationSummary(alignedStructures, windowSize, cutoff, relaxed));
            /*
             * buffer.write("#\tH77andCon \tConandJFH1\tJFH1andH77\n"); for (int
             * i = 0; i < h77ConSim50.length; i++) { buffer.write(i + "\t" +
             * h77ConSim50[i] + "\t" + conJFH1Sim50[i] + "\t" + jfh1ConSim50[i]
             * + "\n"); }
             */
            buffer.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static String getConservationSummary(ArrayList<Structure> alignedStructures, int windowSize, double cutoff, boolean relaxed) {
        String ret = "";
        ArrayList<double[]> pairwiseMountainSimiliarity = new ArrayList<>();
        for (int i = 0; i < alignedStructures.size(); i++) {
            for (int j = i + 1; j < alignedStructures.size(); j++) {
                pairwiseMountainSimiliarity.add(slidingWeightedMountainSimilarity(alignedStructures.get(i).pairedSites, alignedStructures.get(j).pairedSites, windowSize, relaxed));
            }
        }

        ret += "Position\t";
        for (int i = 0; i < alignedStructures.size(); i++) {
            for (int j = i + 1; j < alignedStructures.size(); j++) {
                ret += "dM(" + windowSize + ") " + alignedStructures.get(i).name + ":" + alignedStructures.get(j).name + "\t";
            }
        }
        for (int i = 0; i < alignedStructures.size(); i++) {
            for (int j = i + 1; j < alignedStructures.size(); j++) {
                ret += ">" + cutoff + "? " + alignedStructures.get(i).name + ":" + alignedStructures.get(j).name + "\t";
            }
        }
        ret += "All\t";
        for (int i = 0; i < alignedStructures.size(); i++) {
            for (int j = i + 1; j < alignedStructures.size(); j++) {
                ret += "" + alignedStructures.get(i).name + ":" + alignedStructures.get(j).name + "\t";
            }
        }
        ret += "All\t";
        ret += "\n";

        /*
         * ret += "Min\t"; ret += "Max\t"; ret += "Average\n";
         */
        int n = alignedStructures.size();
        int length = pairwiseMountainSimiliarity.get(0).length + windowSize;
        boolean[][][] conserved = new boolean[n][n][length];
        boolean[][][] conserved2 = new boolean[n][n][length];
        for (int k = 0; k < length; k++) {
            ret += k + "\t";
            int m = 0;
            for (int i = 0; i < alignedStructures.size(); i++) {
                for (int j = i + 1; j < alignedStructures.size(); j++) {
                    if (k < length - windowSize) {
                        ret += pairwiseMountainSimiliarity.get(i)[k] + "\t";
                        if (pairwiseMountainSimiliarity.get(m)[k] >= cutoff) {
                            conserved[i][j][k] = true;
                            for (int l = 0; l < windowSize; l++) {
                                conserved2[i][j][k + l] = true;
                            }
                        }
                    } else {
                        ret += "\t";
                    }
                    m++;
                }
            }

            boolean conservedInAll = true;
            for (int i = 0; i < alignedStructures.size(); i++) {
                for (int j = i + 1; j < alignedStructures.size(); j++) {
                    ret += (conserved[i][j][k] ? "X" : ".") + "\t";
                    if (!conserved[i][j][k]) {
                        conservedInAll = false;
                    }
                }
            }
            ret += (conservedInAll ? "A" : ".") + "\t";

            boolean conservedInAll2 = true;
            for (int i = 0; i < alignedStructures.size(); i++) {
                for (int j = i + 1; j < alignedStructures.size(); j++) {
                    ret += (conserved2[i][j][k] ? "X" : ".") + "\t";
                    if (!conserved2[i][j][k]) {
                        conservedInAll2 = false;
                    }
                }
            }
            ret += (conservedInAll2 ? "A" : ".") + "\t";

            ret += "\n";
        }

        return ret;
    }

    public static String[] booleanStrings(ArrayList<boolean[]> arrays) {
        String[] boolStrings = new String[arrays.get(0).length];
        for (int i = 0; i < boolStrings.length; i++) {
            boolStrings[i] = "";
            for (int j = 0; j < arrays.size(); j++) {
                if (arrays.get(j)[i]) {
                    boolStrings[i] += "1";
                } else {
                    boolStrings[i] += "0";
                }
            }
        }
        return boolStrings;
    }

    public static boolean[] ANDvectors(ArrayList<boolean[]> arrays) {
        boolean[] and = new boolean[arrays.get(0).length];
        for (int i = 0; i < and.length; i++) {
            and[i] = true;
            for (int j = 0; j < arrays.size(); j++) {
                if (!arrays.get(j)[i]) {
                    and[i] = false;
                    break;
                }
            }
        }
        return and;
    }

    public static double[] getMinimumVector(ArrayList<double[]> vectors) {
        double[] minimum = new double[vectors.get(0).length];
        for (int i = 0; i < minimum.length; i++) {
            minimum[i] = Double.MAX_VALUE;
            for (int j = 0; j < vectors.size(); j++) {
                minimum[i] = Math.min(minimum[i], vectors.get(j)[i]);
            }
        }
        return minimum;
    }

    public static double[] getMeanVector(ArrayList<double[]> vectors) {
        double[] mean = new double[vectors.get(0).length];
        for (int i = 0; i < mean.length; i++) {
            for (int j = 0; j < vectors.size(); j++) {
                mean[i] = mean[i] + vectors.get(j)[i];
            }
            mean[i] /= ((double) vectors.size());
        }
        return mean;
    }

    public static boolean[] getConservedPositions(double[] similarity, double cutoff, int windowSize) {
        boolean[] conserved = new boolean[similarity.length + windowSize];
        for (int i = 0; i < similarity.length; i++) {
            if (similarity[i] >= cutoff) {
                System.out.println(i);
                for (int j = 0; j < windowSize; j++) {
                    conserved[i + j] = true;
                }
            }
        }
        return conserved;
    }

    public static double[] slidingWeightedMountainSimilarity(int[] pairedSites1, int[] pairedSites2, int windowSize, boolean relaxed) {
        //System.out.println(pairedSites1.length+"\t"+windowSize);
        double[] sim = new double[pairedSites1.length - windowSize];
        int end = Math.min(pairedSites1.length - windowSize, pairedSites2.length - windowSize);
        for (int i = 0; i < end; i++) {
            int[] pairedSitesSub1;
            int[] pairedSitesSub2;
            if (relaxed) {
                pairedSitesSub1 = getSubstructureRelaxed(pairedSites1, i, windowSize);
                pairedSitesSub2 = getSubstructureRelaxed(pairedSites2, i, windowSize);
            } else {
                pairedSitesSub1 = getSubstructure(pairedSites1, i, windowSize);
                pairedSitesSub2 = getSubstructure(pairedSites2, i, windowSize);
            }
            sim[i] = 1 - MountainMetrics.calculateNormalizedWeightedMountainDistance(pairedSitesSub1, pairedSitesSub2);
        }
        return sim;
    }

    public static double permutationTest(int startPos, int length, double sim, int[] pairedSites1, int [] gapCountSum, int[] pairedSites2, int windowSize, boolean relaxed, boolean slidingSim, int permutations) {
        double permCount = 0;
        double permTotal = 0;


        Random random = new Random(-1380401484108201L);
        boolean[] indices = new boolean[pairedSites1.length - length];
        indices[startPos] = true;
        Utils.randomBooleanArray(random, permutations < 0 ? indices.length : permutations, indices);

        //Arrays.fill(indices, true);
        int[] subPairedSites2 = null;
        if (relaxed) {
            subPairedSites2 = StructureAlign.getSubstructureRelaxed(pairedSites2, startPos, length);
        } else {
            subPairedSites2 = StructureAlign.getSubstructure(pairedSites2, startPos, length);
        }
        //System.out.println(RNAFoldingTools.getDotBracketStringFromPairedSites(subPairedSites2));
        for (int k = 0; k < pairedSites1.length - length; k++) {
            if (indices[k]) {
                if(k != startPos)
                {
                    int startGap = 0;
                    if(k > 0)
                    {
                        startGap = gapCountSum[k-1];
                    }
                    double gaps = gapCountSum[k+length] - startGap;
                    double gapPerc = gaps / (double)length;
                    if(gapPerc > 0.2)
                    {
                        continue; // if two many gaps in permuted substructure: skip
                    }
                }
                //int[] permutedPairedSites1 = StructureAlign.getSubstructureRelaxed(pairedSites1, k, region.length);
                int[] permutedPairedSites1 = null;
                if (relaxed) {
                    permutedPairedSites1 = StructureAlign.getSubstructureRelaxed(pairedSites1, k, length);
                } else {
                    permutedPairedSites1 = StructureAlign.getSubstructure(pairedSites1, k, length);
                }
                double permSim = 0;
                if (slidingSim) {
                    permSim = StructureAlign.slidingWeightedMountainSimilarityAverage(permutedPairedSites1, subPairedSites2, windowSize - 1, relaxed);
                } else {
                    permSim = 1 - MountainMetrics.calculateNormalizedWeightedMountainDistance(permutedPairedSites1, subPairedSites2);
                    //System.out.println(RNAFoldingTools.getDotBracketStringFromPairedSites(permutedPairedSites1));

                    //System.out.println();
                }
                if (sim > permSim) {
                    permCount++;
                }
                permTotal++;
                //System.out.println(permTotal);
            }
        }
        //System.out.println(">"+permCount+"\t"+permTotal);
        return 1 - (permCount / permTotal);
    }
    
    public static double permutationTestCircular(int startPos, int length, double sim, int[] pairedSites1, int [] gapCountSum, int[] pairedSites2, int windowSize, boolean relaxed, boolean slidingSim, int permutations) {
        double permCount = 0;
        double permTotal = 0;


        Random random = new Random(-1380401484108201L);
        boolean[] indices = new boolean[pairedSites1.length];
        indices[startPos] = true;
        Utils.randomBooleanArray(random, permutations < 0 ? indices.length : permutations, indices);

        //Arrays.fill(indices, true);
        int[] subPairedSites2 = null;
        if (relaxed) {
            subPairedSites2 = StructureAlign.getSubstructureRelaxedCircular(pairedSites2, startPos, length);
        } else {
            subPairedSites2 = StructureAlign.getSubstructureCircular(pairedSites2, startPos, length);
        }
        //System.out.println(RNAFoldingTools.getDotBracketStringFromPairedSites(subPairedSites2));
        //for (int k = 0; k < pairedSites1.length - length; k++) {
        for (int k = 0; k < pairedSites1.length ; k++) {
            if (indices[k]) {
                if(k != startPos)
                {
                    int startGap = 0;
                    if(k > 0)
                    {
                        startGap = gapCountSum[k-1];
                    }
                    double gaps = gapCountSum[(k+length)%gapCountSum.length] - startGap;
                    double gapPerc = gaps / (double)length;
                    if(gapPerc > 0.2)
                    {
                        continue; // if two many gaps in permuted substructure: skip
                    }
                }
                //int[] permutedPairedSites1 = StructureAlign.getSubstructureRelaxed(pairedSites1, k, region.length);
                int[] permutedPairedSites1 = null;
                if (relaxed) {
                    permutedPairedSites1 = StructureAlign.getSubstructureRelaxedCircular(pairedSites1, k, length);
                } else {
                    permutedPairedSites1 = StructureAlign.getSubstructureCircular(pairedSites1, k, length);
                }
                double permSim = 0;
                if (slidingSim) {
                    permSim = StructureAlign.slidingWeightedMountainSimilarityAverage(permutedPairedSites1, subPairedSites2, windowSize - 1, relaxed);
                } else {
                    permSim = 1 - MountainMetrics.calculateNormalizedWeightedMountainDistance(permutedPairedSites1, subPairedSites2);
                    //System.out.println(RNAFoldingTools.getDotBracketStringFromPairedSites(permutedPairedSites1));

                    //System.out.println();
                }
                if (sim > permSim) {
                    permCount++;
                }
                permTotal++;
                //System.out.println(permTotal);
            }
        }
        if(permTotal == 0)
        {
            return 1;
        }
        //System.out.println(">"+permCount+"\t"+permTotal);
        return 1 - (permCount / permTotal);
    }

    public static double[] permutationTestSliding(int[] pairedSites1, int[] pairedSites2, int windowSize, boolean relaxed, int permutations) {
        double[] pvals = new double[pairedSites1.length - windowSize];
        Integer countLock = new Integer(0);
        for (int i = 0; i < pvals.length; i++) {
            int[] subPairedSites1;
            int[] subPairedSites2;
            if (relaxed) {
                subPairedSites1 = StructureAlign.getSubstructureRelaxed(pairedSites1, i, windowSize);
                subPairedSites2 = StructureAlign.getSubstructureRelaxed(pairedSites2, i, windowSize);
            } else {
                subPairedSites1 = StructureAlign.getSubstructure(pairedSites1, i, windowSize);
                subPairedSites2 = StructureAlign.getSubstructure(pairedSites2, i, windowSize);
            }

            double sim = 1 - MountainMetrics.calculateNormalizedWeightedMountainDistance(subPairedSites1, subPairedSites2);
            int [] gapCountSum = new int[pairedSites1.length];
            pvals[i] = permutationTest(i, windowSize, sim, pairedSites1, gapCountSum, pairedSites2, windowSize, relaxed, false, permutations);

        }

        return pvals;
    }

    public double[] parallelizedPermutationTestSliding(int[] pairedSites1, int[] pairedSites2, String seq1, int windowSize, boolean relaxed, int permutations) {

        int [] gapCountSum = new int[seq1.length()];
        gapCountSum[0] = seq1.charAt(0) == '-' ? 1 : 0;
        for (int i = 1; i < gapCountSum.length; i++) {
            gapCountSum[i] = (seq1.charAt(i) == '-' ? 1 : 0) + gapCountSum[i-1];
        }

        double[] pvals = new double[pairedSites1.length - windowSize];
        ArrayList<Input> inputs = new ArrayList<>();
        for (int i = 0; i < pvals.length; i++) {
            int[] subPairedSites1;
            int[] subPairedSites2;
            if (relaxed) {
                subPairedSites1 = StructureAlign.getSubstructureRelaxed(pairedSites1, i, windowSize);
                subPairedSites2 = StructureAlign.getSubstructureRelaxed(pairedSites2, i, windowSize);
            } else {
                subPairedSites1 = StructureAlign.getSubstructure(pairedSites1, i, windowSize);
                subPairedSites2 = StructureAlign.getSubstructure(pairedSites2, i, windowSize);
            }

            double sim = 1 - MountainMetrics.calculateNormalizedWeightedMountainDistance(subPairedSites1, subPairedSites2);
            //pvals[i] = permutationTest(i, windowSize, sim, pairedSites1, pairedSites2, windowSize, relaxed, false, permutations);
            inputs.add(new Input(i, windowSize, sim, pairedSites1, gapCountSum, pairedSites2, windowSize, relaxed, false, permutations));
        }
        System.out.println("here");
        try {
            List<Output> outputs = processInputs(inputs);
            for (Output out : outputs) {
                pvals[out.i] = out.pval;
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(StructureAlign.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ExecutionException ex) {
            Logger.getLogger(StructureAlign.class.getName()).log(Level.SEVERE, null, ex);
        }

        return pvals;
    }
    
    public double[] parallelizedPermutationTestSlidingCircular(int[] pairedSites1, int[] pairedSites2, String seq1, int windowSize, boolean relaxed, int permutations) {

        int [] gapCountSum = new int[seq1.length()];
        gapCountSum[0] = seq1.charAt(0) == '-' ? 1 : 0;
        for (int i = 1; i < gapCountSum.length; i++) {
            gapCountSum[i] = (seq1.charAt(i) == '-' ? 1 : 0) + gapCountSum[i-1];
        }

        double[] pvals = new double[pairedSites1.length];
        ArrayList<Input> inputs = new ArrayList<>();
        for (int i = 0; i < pvals.length; i++) {
            int[] subPairedSites1;
            int[] subPairedSites2;
            if (relaxed) {
                subPairedSites1 = StructureAlign.getSubstructureRelaxedCircular(pairedSites1, i, windowSize);
                subPairedSites2 = StructureAlign.getSubstructureRelaxedCircular(pairedSites2, i, windowSize);
            } else {
                subPairedSites1 = StructureAlign.getSubstructureCircular(pairedSites1, i, windowSize);
                subPairedSites2 = StructureAlign.getSubstructureCircular(pairedSites2, i, windowSize);
            }

            double sim = 1 - MountainMetrics.calculateNormalizedWeightedMountainDistance(subPairedSites1, subPairedSites2);
            //pvals[i] = permutationTest(i, windowSize, sim, pairedSites1, pairedSites2, windowSize, relaxed, false, permutations);
            inputs.add(new Input(i, windowSize, sim, pairedSites1, gapCountSum, pairedSites2, windowSize, relaxed, false, permutations));
        }
        try {
            List<Output> outputs = processInputs(inputs);
            for (Output out : outputs) {
                pvals[out.i] = out.pval;
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(StructureAlign.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ExecutionException ex) {
            Logger.getLogger(StructureAlign.class.getName()).log(Level.SEVERE, null, ex);
        }

        return pvals;
    }

    public class Input {

        public int startPos;
        public int length;
        public double sim;
        public int[] pairedSites1;        
        public int [] gapCountSum;
        public int[] pairedSites2;
        public int windowSize;
        public boolean relaxed;
        public boolean slidingSim;
        public int permutations;

        public Input(int startPos, int length, double sim, int[] pairedSites1, int [] gapCountSum, int[] pairedSites2, int windowSize, boolean relaxed, boolean slidingSim, int permutations) {
            this.startPos = startPos;
            this.length = length;
            this.sim = sim;
            this.pairedSites1 = pairedSites1;
            this.gapCountSum = gapCountSum;
            this.pairedSites2 = pairedSites2;
            this.windowSize = windowSize;
            this.relaxed = relaxed;
            this.slidingSim = slidingSim;
            this.permutations = permutations;
        }
    }

    public class Output {

        int i;
        double pval;
    }

    public List<Output> processInputs(List<Input> inputs)
            throws InterruptedException, ExecutionException {

        int threads = Runtime.getRuntime().availableProcessors();
        ExecutorService service = Executors.newFixedThreadPool(threads);

        List<Future<Output>> futures = new ArrayList<Future<Output>>();
        for (final Input input : inputs) {
            Callable<Output> callable = new Callable<Output>() {

                public Output call() throws Exception {
                    Output output = new Output();
                    output.i = input.startPos;
                    output.pval = permutationTestCircular(input.startPos, input.length, input.sim, input.pairedSites1, input.gapCountSum, input.pairedSites2, input.windowSize, input.relaxed, input.slidingSim, input.permutations);
                    return output;
                }
            };
            futures.add(service.submit(callable));
        }

        service.shutdown();

        List<Output> outputs = new ArrayList<Output>();
        for (Future<Output> future : futures) {
            outputs.add(future.get());
        }
        return outputs;
    }

    public static double permutationTest(Region region, double sim, int[] pairedSites1, int[] pairedSites2, int windowSize, boolean relaxed, boolean slidingSim, int permutations) {
        double permCount = 0;
        double permTotal = 0;


        Random random = new Random(-1380401484108201L);
        boolean[] indices = new boolean[pairedSites1.length - region.length];
        indices[region.startPos] = true;
        Utils.randomBooleanArray(random, permutations < 0 ? indices.length : permutations, indices);

        //Arrays.fill(indices, true);
        int[] subPairedSites2 = null;
        if (relaxed) {
            subPairedSites2 = StructureAlign.getSubstructureRelaxed(pairedSites2, region.startPos, region.length);
        } else {
            subPairedSites2 = StructureAlign.getSubstructure(pairedSites2, region.startPos, region.length);
        }
        for (int k = 0; k < pairedSites1.length - region.length; k++) {
            if (indices[k]) {
                //int[] permutedPairedSites1 = StructureAlign.getSubstructureRelaxed(pairedSites1, k, region.length);
                int[] permutedPairedSites1 = null;
                if (relaxed) {
                    permutedPairedSites1 = StructureAlign.getSubstructureRelaxed(pairedSites1, k, region.length);
                } else {
                    permutedPairedSites1 = StructureAlign.getSubstructure(pairedSites1, k, region.length);
                }
                double permSim = 0;
                if (slidingSim) {
                    permSim = StructureAlign.slidingWeightedMountainSimilarityAverage(permutedPairedSites1, subPairedSites2, windowSize - 1, relaxed);
                } else {
                    permSim = 1 - MountainMetrics.calculateNormalizedWeightedMountainDistance(permutedPairedSites1, subPairedSites2);

                }
                if (sim > permSim) {
                    permCount++;
                }
                permTotal++;
                //System.out.println(permTotal);
            }
        }

        return 1 - (permCount / permTotal);
    }

    public static double permutationTest2(Region region, int[] pairedSites1, int[] pairedSites2, int windowSize, boolean relaxed) {
        double permCount = 0;
        double permTotal = 0;

        int[] sub1 = StructureAlign.getSubstructureRelaxed(pairedSites1, region.startPos, region.length);
        int[] sub2 = StructureAlign.getSubstructureRelaxed(pairedSites2, region.startPos, region.length);
        double sim = StructureAlign.slidingWeightedMountainSimilarityAverage(sub1, sub2, windowSize - 1, relaxed);
        double[] simVector = slidingWeightedMountainSimilarity(pairedSites1, pairedSites2, windowSize, relaxed);
        for (int k = 0; k < pairedSites1.length - region.length; k++) {
            if (k != region.startPos) {
                //int[] permSites = StructureAlign.getSubstructureRelaxed(pairedSites1, k, region.length);
                //double permSim = StructureAlign.slidingWeightedMountainSimilarityAverage(permSites, pairedSites2, windowSize - 1, relaxed);
                double permSim = average(simVector, k, k + region.length - windowSize + 1);
                if (sim > permSim) {
                    permCount++;
                }
                permTotal++;
            }
        }

        return 1 - (permCount / permTotal);
    }

    public static double average(double[] vector, int startPos, int endPos) {
        double sum = 0;
        for (int i = startPos; i < endPos; i++) {
            sum += vector[i];
        }
        return sum / ((double) (endPos - startPos));
    }

    public static double slidingWeightedMountainSimilarityAverage(int[] pairedSites1, int[] pairedSites2, int windowSize, boolean relaxed) {
        double[] sim = slidingWeightedMountainSimilarity(pairedSites1, pairedSites2, Math.min(pairedSites1.length - 1, windowSize), relaxed);
        double sum = 0;
        for (int i = 0; i < sim.length; i++) {
            sum += sim[i];
        }
        return sum / ((double) sim.length);
    }

    public static double[] slidingWeightedSequenceSimilarity(ArrayList<String> sequences, int windowSize) {
        AmbiguityCodes ambiguityCodes = new AmbiguityCodes();
        double[][] nucFreq = new double[sequences.get(0).length()][5];
        for (int i = 0; i < sequences.size(); i++) {
            String sequence = sequences.get(i);
            for (int j = 0; j < nucFreq.length; j++) {
                double[] baseScores = ambiguityCodes.getBaseScores(sequence.charAt(j) + "");
                for (int k = 0; k < baseScores.length; k++) {
                    nucFreq[j][k] += baseScores[k];
                }
            }
        }

        double[] nucleotideConservation = new double[nucFreq.length];
        for (int i = 0; i < nucleotideConservation.length; i++) {
            double total = 0;
            for (int j = 0; j < 4; j++) {
                total += nucFreq[i][j];
            }
            for (int j = 0; j < 4; j++) {
                nucFreq[i][j] /= total;
            }


            double[] shannonEntropy = NucleotideComposition.getShannonEntropy(nucFreq[i], sequences.size());
            for (int j = 0; j < shannonEntropy.length; j++) {
                nucleotideConservation[i] += shannonEntropy[j];
            }
            nucleotideConservation[i] /= 8.0;
        }

        double[] sim = new double[sequences.get(0).length() - windowSize];
        for (int i = 0; i < sim.length; i++) {
            for (int j = 0; j < windowSize; j++) {
                sim[i] += nucleotideConservation[i + j];
            }
            sim[i] /= ((double) windowSize);
        }

        return sim;
    }

    public static double[] slidingWeightedSequenceSimilarity(String seq1, String seq2, int windowSize) {
        AmbiguityCodes ambiguityCodes = new AmbiguityCodes();
        double[] sim = new double[seq1.length()];
        for (int i = 0; i < seq1.length() && i < seq2.length(); i++) {
            double[] b1 = ambiguityCodes.getBaseScores(seq1.charAt(i) + "");
            double[] b2 = ambiguityCodes.getBaseScores(seq2.charAt(i) + "");
            for (int j = 0; j < 5; j++) {
                sim[i] += Math.abs(b1[j] - b2[j]);
            }
            sim[i] = 1 - (sim[i] / 2);
            //System.out.println(i+"\t*"+sim[i]);
        }

        double[] simWindow = new double[sim.length + windowSize];
        Arrays.fill(simWindow, Double.MIN_VALUE);
        for (int i = 0; i < sim.length - windowSize; i++) {
            for (int j = 0; j < windowSize; j++) {
                simWindow[i + (windowSize / 2)] += sim[i + j];
            }
            simWindow[i + (windowSize / 2)] /= ((double) windowSize);
        }

        return simWindow;
    }

    public static Structure loadStructureFromCtFile(File ctFile) {
        int[] pairedSites = RNAFoldingTools.getPairedSitesFromCtFile(ctFile);
        String seq = RNAFoldingTools.getSequenceFromCtFile(ctFile);
        Structure s = new Structure(pairedSites.length);
        s.pairedSites = pairedSites;
        s.sequence = seq;
        return s;
    }
    
    public static int [] getMappedSites(String alignedSequence, String mappingString, int [] pairedSites)
    {
        String s = RNAFoldingTools.getDotBracketStringFromPairedSites(pairedSites);
         String ret = "";
         char gapInsert = '-';
        int spos = 0;
        for (int i = 0; i < alignedSequence.length(); i++) {
            if (alignedSequence.charAt(i) != '-' && spos < s.length()) {
                ret += s.charAt(spos);
                spos++;
            } else {
                ret += gapInsert;
            }
        }
        int [] newsites = RNAFoldingTools.getPairedSitesFromDotBracketString(ret);
        
        for(int i = 0 ; i < newsites.length ; i++)
        {
            if(mappingString.charAt(i) == '-')
            {
                int temp = newsites[i];
                if(temp != 0)
                {
                    newsites[i] = 0;
                    newsites[temp-1] = 0;
                }
            }
        }
        String str = RNAFoldingTools.getDotBracketStringFromPairedSites(newsites);
        String ret2 = "";
        for(int i = 0  ; i < mappingString.length() ; i++)
        {
            if(mappingString.charAt(i) != '-')
            {
                ret2 += str.charAt(i);
            }
        }
        
        return RNAFoldingTools.getPairedSitesFromDotBracketString(ret2);
    }

    public static String mapStringToAlignedSequence(String s, String alignedSequence, String gapInsert) {
        String ret = "";
        int spos = 0;
        for (int i = 0; i < alignedSequence.length(); i++) {
            if (alignedSequence.charAt(i) != '-' && spos < s.length()) {
                ret += s.charAt(spos);
                spos++;
            } else {
                ret += gapInsert;
            }
        }
        return ret;
    }

    public static String findAlignedSequence(ArrayList<String> sequences, String sequence) {
        String s = sequence.replaceAll("-", "").trim().toUpperCase().replaceAll("U", "T");
        for (int i = 0; i < sequences.size(); i++) {
            /*
             * if(i == 0) { String a = s; String b =
             * sequences.get(i).replaceAll("-",
             * "").trim().toUpperCase().replaceAll("U", "T"); boolean same =
             * true; for(int j = 0 ; j < Math.min(a.length(),b.length()) ; j++)
             * { if(a.charAt(j) != b.charAt(j)) { same = false; }
             * System.out.println(j+"\t"+a.charAt(j)+"\t"+b.charAt(j) + "\t" +
             * same); } }
             */
            if (sequences.get(i).replaceAll("-", "").trim().toUpperCase().replaceAll("U", "T").startsWith(s)) {
                return sequences.get(i);
            }
        }
        return null;
    }

    public static int[] getMountainRepresentation(int[] pairedSites) {
        int[] mountain = new int[pairedSites.length];
        for (int i = 0; i < mountain.length; i++) {
            for (int j = 0; j < i; j++) {
                if (pairedSites[j] != 0 && pairedSites[j] - 1 > i) {
                    mountain[i]++;
                }
            }
        }
        return mountain;
    }

    public static int[] getSubstructure(int[] pairedSites, int i, int length) {
        int[] substructure = new int[length];
        int a = 0;
        for (int j = i; j < i + length; j++) {
            substructure[a] = Math.max(0, pairedSites[j] - i);
            if (substructure[a] > length) {
                substructure[a] = 0;
            }
            a++;
        }
        return substructure;
    }
    
    public static int[] getSubstructureCircular(int[] pairedSites, int i, int length) {
        int[] substructure = new int[length];
        int a = 0;
        for (int j = i; j < i + length; j++) {
            substructure[a] = Math.max(0, pairedSites[j%pairedSites.length] - i);
            /*if(Math.abs(pairedSites[j%pairedSites.length]-1-i) >= pairedSites.length/2)
            {
                substructure[a] += pairedSites.length;
            }*/        
            if (substructure[a] > length) {
                substructure[a] = 0;
            }
            a++;
        }
        /*for(int j = 0 ; j < substructure.length ; j++)
        {
            if(substructure[j] != 0)
            {
                substructure[substructure[j]-1] = j+1;
            }
        }*/
        return substructure;
    }

    public static int[] getSubstructureRelaxed(int[] pairedSites, int i, int length) {
        int[] substructure = new int[length];
        int a = 0;
        for (int j = i; j < i + length; j++) {
            if (pairedSites[j] != 0) {
                substructure[a] = pairedSites[j] - i;
            }
            a++;
        }
        return substructure;
    }

    public static int[] getSubstructureRelaxedCircular(int[] pairedSites, int i, int length) {
        int[] substructure = new int[length];
        int a = 0;
        for (int j = i; j < i + length; j++) {
            if (pairedSites[j%pairedSites.length] != 0) {
                substructure[a] = pairedSites[j%pairedSites.length] - i;
           
                
                //if(j >= pairedSites.length)
                //{
                
                if(Math.abs(pairedSites[j%pairedSites.length]-1-i) >= pairedSites.length/2)
                {
                    substructure[a] += pairedSites.length;
                }
                
                     
                if(substructure[a] <= 0)
                {
                    substructure[a] = -pairedSites[j%pairedSites.length];
                }
            }
            a++;
        }
        
       /* for(int j = 0 ; j < substructure.length ; j++)
        {
            if(substructure[j] != 0)
            {
                substructure[substructure[j]-1] = j+1;
            }
        }*/
        return substructure;
    }
    
    public static void main(String [] args)
    {
        //                                                             0123456789012
        int [] p = RNAFoldingTools.getPairedSitesFromDotBracketString("(.....(....))");
         int [] p2 = RNAFoldingTools.getPairedSitesFromDotBracketString("(.....(....))");
        int [] s = getSubstructureRelaxedCircular(p, 11, 2);
         int [] s2 = getSubstructureRelaxedCircular(p, 11, 2);
        //int [] s = getSubstructureRelaxedCircular(p, 0, 4);        
        for(int i = 0;  i < s.length ; i++)
        {
            System.out.println((i+1)+"\t"+s[i]);
        }
        
        System.out.println(RNAFoldingTools.getDotBracketStringFromPairedSites(s));
        StructureAlign sal = new StructureAlign();
       double [] d = sal.parallelizedPermutationTestSlidingCircular(s, s2, "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", 75, true, 1000);
       for(int i = 0 ; i < d.length ; i++)
       {
           System.out.println(i+"\t"+d[i]);
       }
       
    }
    
    
    public static class Region {

        public int id;
        public int startPos;
        public int length;
        public double score;

        public Region(int startPos, int length) {
            this.startPos = startPos;
            this.length = length;
        }

        @Override
        public String toString() {
            return "[" + startPos + "-" + (startPos + length) + "]" + length;
        }

        public static Region getRegion(int startPos, int endPos) {
            return new Region(startPos, endPos + 1 - startPos);
        }
    }
}
