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
import java.util.logging.Level;
import java.util.logging.Logger;
import nava.utils.RNAFoldingTools;

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

    public static ArrayList<Region> getConservedStructures(ArrayList<Structure> alignedStructures, ArrayList<String> alignedSequences, ArrayList<String> structureNames, int windowSize, double cutoff, String prefix, boolean useMinMethod) {
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

    public static boolean[] getConservedPositions(double[] similarity, double cutoff, int windowSize) {
        boolean[] conserved = new boolean[similarity.length + windowSize];
        for (int i = 0; i < similarity.length; i++) {
            if (similarity[i] >= cutoff) {
                for (int j = 0; j < windowSize; j++) {
                    conserved[i + j] = true;
                }
            }
        }
        return conserved;
    }

    public static double[] slidingWeightedMountainSimilarity(int[] pairedSites1, int[] pairedSites2, int windowSize, boolean relaxed) {
        double[] sim = new double[pairedSites1.length - windowSize];
        for (int i = 0; i < pairedSites1.length - windowSize; i++) {
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

    public static Structure loadStructureFromCtFile(File ctFile) {
        int[] pairedSites = RNAFoldingTools.getPairedSitesFromCtFile(ctFile);
        String seq = RNAFoldingTools.getSequenceFromCtFile(ctFile);
        Structure s = new Structure(pairedSites.length);
        s.pairedSites = pairedSites;
        s.sequence = seq;
        return s;
    }

    public static String mapStringToAlignedSequence(String s, String alignedSequence, String gapInsert) {
        String ret = "";
        int spos = 0;
        for (int i = 0; i < alignedSequence.length(); i++) {
            if (alignedSequence.charAt(i) != '-' && i < s.length()) {
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
            /*if(i == 0)
            {
                String a = s;
                String b = sequences.get(i).replaceAll("-", "").trim().toUpperCase().replaceAll("U", "T");
                boolean same = true;
                for(int j = 0 ; j < Math.min(a.length(),b.length()) ; j++)
                {
                    if(a.charAt(j) != b.charAt(j))
                    {
                        same = false;
                    }
                    System.out.println(j+"\t"+a.charAt(j)+"\t"+b.charAt(j) + "\t" + same);
                }
            }*/
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

    public static class Region {

        public int startPos;
        public int length;

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
