/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.experimental;

import java.awt.Color;
import java.awt.geom.Line2D;
import java.io.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import nava.data.io.FileImport;
import nava.data.io.FileImport.ParserException;
import nava.data.types.DataType;
import nava.data.types.SecondaryStructureData;
import nava.ranking.RankingAnalyses;
import nava.ranking.StatUtils;
import nava.structure.MountainMetrics;
import nava.structure.StructureAlign;
import nava.structurevis.data.DataTransform;
import nava.structurevis.data.DataTransform.TransformType;
import nava.structurevis.data.Feature;
import nava.utils.ColorGradient;
import nava.utils.GraphicsUtils;
import nava.utils.Pair;
import nava.utils.RNAFoldingTools;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class PairwiseStructureComparison {

    public void runComparison() {
        try {
            int permutations = 3000;
            int windowSize = 75;
            boolean balance = true;

            //File outFile = new File("C:/dev/thesis/dengue/dengue_permutations2.txt");
            // String[] categories = {"dengue1", "dengue2", "dengue3", "dengue4"};

            //File outFile = new File("C:/dev/thesis/westnile/westnile_permutations.txt");
            //  String[] categories = {"westnile"};
            //File outFile = new File("C:/dev/thesis/westnile_dengue_permutations_gap.txt");
            //String[] categories = {"westnile", "dengue1", "dengue2", "dengue3", "dengue4"};
            //File structureAlignment = new File("C:/dev/thesis/structure_dengue_westnile_align_400.dbn");

            //File outFile = new File("C:/dev/thesis/jev_westnile_permutations3.txt");
            //File outFile = new File("C:/dev/thesis/jev_westnile_permutations3.txt");
            // String[] categories = {"westnile", "jev"};
            // File structureAlignment = new File("C:/dev/thesis/structure_jev_westnile_aligned.dbn");

            //File outFile = new File("C:/dev/thesis/hcv_westnile_permutations2.txt");
            //String[] categories = {"westnile", "hcv1a", "hcv1b", "hcv2a", "hcv2b","hcv3","hcv4", "hcv6"};

            File outFile = new File("C:/dev/thesis/hcv/hcv_permutations2.txt");
            String[] categories = {"hcv1a", "hcv1b", "hcv2a", "hcv2b", "hcv3", "hcv4", "hcv6"};
            File structureAlignment = new File("C:/dev/thesis/hcv/hcv_genotypes2_aligned.dbn");

            //File outFile = new File("C:/dev/thesis/hiv_full/hiv_not_siv_permutations2.txt");
            // String[] categories = {"hiv1b", "hiv1c", "hiv1d", "hiv1g", "hiv1o", "hiv2"};
            //File structureAlignment = new File("C:/dev/thesis/hiv_full/hiv_not_siv_full_aligned.dbn");

            System.out.println(combinations(categories));
            ArrayList<SecondaryStructureData> structureData = FileImport.loadStructures(structureAlignment, DataType.FileFormat.VIENNA_DOT_BRACKET);


            /*
             * ArrayList<SecondaryStructureData> structuralAlignment = new
             * ArrayList<SecondaryStructureData>();
             * structuralAlignment.addAll(structureData.subList(0, 25));
             * double[] mountainSumPlot = mountainSumPlot(structuralAlignment);
             * for (int i = 0; i < mountainSumPlot.length; i++) {
             * System.out.println(i + "\t" + mountainSumPlot[i]); }
             * System.exit(0);
             */

            ArrayList<Combination> combinations = combinations(categories);
            for (int i = 0; i < combinations.size(); i++) {
                Combination combination = combinations.get(i);
                // combination.combination.size() == 1 ||
                if (combination.combination.size() == 1 || combination.combination.size() == categories.length) {
                } else {
                    combinations.remove(i);
                    i--;
                }
            }

            /*
             * File outFile = new
             * File("C:/dev/thesis/hiv_full/hiv_full_permutations_75.txt");
             * String[] categories = {"hiv1b", "hiv1c", "hiv1d", "hiv1g",
             * "hiv1o", "hiv2", "siv"}; String[] categories_hiv1 = {"hiv1b",
             * "hiv1c", "hiv1d", "hiv1g", "hiv1o"}; String[]
             * categories_hiv1_hiv2 = {"hiv1b", "hiv1c", "hiv1d", "hiv1g",
             * "hiv1o", "hiv2"}; System.out.println(combinations(categories));
             * ArrayList<SecondaryStructureData> structureData =
             * FileImport.loadStructures(new
             * File("C:/dev/thesis/hiv_full/hiv_full_aligned.dbn"),
             * DataType.FileFormat.VIENNA_DOT_BRACKET);
             *
             * ArrayList<Combination> combinations = combinations(categories);
             * for (int i = 0; i < combinations.size(); i++) { Combination
             * combination = combinations.get(i); if
             * (combination.combination.size() <= 2 ||
             * combination.combination.size() == categories.length) { } else {
             * combinations.remove(i); i--; } } combinations.add(new
             * Combination(categories_hiv1)); combinations.add(new
             * Combination(categories_hiv1_hiv2));
             */

            /*
             * File outFile = new File("C:/dev/thesis/hcv/hcv_75_2.txt");
             * String[] categories = {"hcv1a", "hcv1b", "hcv2a", "hcv2b",
             * "hcv3", "hcv4", "hcv6"}; //String[] categories_hiv1 = {"hiv1b",
             * "hiv1c", "hiv1d", "hiv1g", "hiv1o"}; // String[]
             * categories_hiv1_hiv2 = {"hiv1b", "hiv1c", "hiv1d", "hiv1g",
             * "hiv1o", "hiv2"}; System.out.println(combinations(categories));
             * ArrayList<SecondaryStructureData> structureData =
             * FileImport.loadStructures(new
             * File("C:/dev/thesis/hcv/hcv_genotypes2_aligned.dbn"),
             * DataType.FileFormat.VIENNA_DOT_BRACKET);
             *
             * ArrayList<Combination> combinations = combinations(categories);
             * for (int i = 0; i < combinations.size(); i++) { Combination
             * combination = combinations.get(i); if
             * (combination.combination.size() <= 2 ||
             * combination.combination.size() == categories.length) { } else {
             * combinations.remove(i); i--; } }
             */



            ArrayList<String> pairMustContain = new ArrayList<>();
            //pairMustContain.add("");
            //pairMustContain.add("dengue4");
            ////pairMustContain.add("siv");
            //pairMustContain.add("hcv6");
            // pairMustContain.add("westnile");

            for (int i = 0; i < categories.length; i++) {
                pairMustContain = new ArrayList<>();
                pairMustContain.add(categories[i]);
                postAnalysis(outFile, outFile, categories, combinations, structureData, windowSize, pairMustContain);
            }
            pairMustContain = new ArrayList<>();
            postAnalysis(outFile, outFile, categories, combinations, structureData, windowSize, pairMustContain);
            System.exit(0);
            //ArrayList<SecondaryStructureData> structureData = FileImport.loadStructures(new File("C:/dev/thesis/dengue2/50/dengue2_all_50_aligned_partial_structurealign.fas.dbn"), DataType.FileFormat.VIENNA_DOT_BRACKET);
            //ArrayList<SecondaryStructureData> structureData = FileImport.loadStructures(new File("C:/dev/thesis/full-alignment.dbn"), DataType.FileFormat.VIENNA_DOT_BRACKET);

            ArrayList<StructureItem> structures = new ArrayList<>();
            for (SecondaryStructureData d : structureData) {
                StructureItem item = new StructureItem();
                item.pairedSites = d.pairedSites;
                item.sequence = d.sequence;
                item.title = d.title;
                String[] split = d.title.split("_");
                item.organism = split[split.length - 1];
                structures.add(item);
            }

            Random random = new Random(4809130618718489104L);

            HashSet<Pair<StructureItem, StructureItem>> pairwiseComparisons = new HashSet<>();
            BufferedWriter buffer = new BufferedWriter(new FileWriter(outFile));

            ArrayList<String> catList = new ArrayList<>();
            for (int i = 0; i < categories.length; i++) {
                catList.add(categories[i]);
            }
            double[] catCount = new double[categories.length];
            double[] catPerc = new double[categories.length];
            double catFrac = 1 / (double) catCount.length;
            double t = 0;

            StructureAlign sal = new StructureAlign();
            for (int a = 0; a < 10000000;) {
                int i = random.nextInt(structures.size());
                int j = random.nextInt(structures.size());

                StructureItem s1 = structures.get(i);
                StructureItem s2 = structures.get(j);
                if (i == j || pairwiseComparisons.contains(new Pair<>(s1, s2))) {
                    continue;
                }

                // System.out.println();
                //System.out.println(catList+"\t"+s1.organism);
                int cati = catList.indexOf(s1.organism);
                int catj = catList.indexOf(s2.organism);
                if (balance) {
                    if (catPerc[cati] > catFrac || catPerc[catj] > catFrac) { // balance for the fact that we have an uneven number of sequences from each category
                        continue;
                    }
                }

                catCount[cati]++;
                catCount[catj]++;
                t += 2;
                for (int c = 0; c < catPerc.length; c++) {
                    catPerc[c] = catCount[c] / t;
                }
                if (a % 10 == 0) {
                    for (int c = 0; c < catPerc.length; c++) {
                        System.out.println(catList.get(c) + "\t" + catPerc[c]);
                    }
                    System.out.println();
                }
                a++;

                //double[] pairwiseSimilarity = StructureAlign.slidingWeightedMountainSimilarity(s1.pairedSites, s2.pairedSites, windowSize, true);

                // double[] pairwiseSimilarity = StructureAlign.permutationTestSliding(s1.pairedSites, s2.pairedSites, windowSize, true, -1);
                double[] pairwiseSimilarity = sal.parallelizedPermutationTestSliding(s1.pairedSites, s2.pairedSites, s1.sequence, windowSize, true, permutations);
                String[] split1 = s1.title.split("_");
                String[] split2 = s2.title.split("_");
                String o1 = split1[split1.length - 1];
                String o2 = split2[split2.length - 1];
                buffer.write(i + "_" + j + "\t" + o1 + "_" + o2 + "\t");
                for (int p = 0; p < pairwiseSimilarity.length; p++) {
                    buffer.write(pairwiseSimilarity[p] + "\t");
                }
                buffer.newLine();
                pairwiseComparisons.add(new Pair<>(s1, s2));

            }
            buffer.close();

            /*
             * double[][] data = new double[structures.get(0).pairedSites.length
             * - windowSize][pairwiseComparisons.size()]; int c = 0; for (int i
             * = 0; i < structures.size(); i++) { StructureItem s1 =
             * structures.get(i); for (int j = i + 1; j < structures.size();
             * j++) { StructureItem s2 = structures.get(j); Pair<StructureItem,
             * StructureItem> key = new Pair<>(s1, s2); double[]
             * pairwiseSimilarity = pairwiseComparisons.get(key); for (int k =
             * 0; k < data.length; k++) {
             * //System.out.println(k+"\t"+c+"\t"+data.length+"\t"+pairwiseSimilarity.length);
             * data[k][c] = pairwiseSimilarity[k]; } c++; } }
             * System.out.println("pairs = " + c);
             *
             * for (int i = 0; i < data.length; i++) { Arrays.sort(data[i]);
             * System.out.println(i + "\t" + data[i][data[i].length / 4] + "\t"
             * + data[i][data[i].length / 2] + "\t" + data[i][data[i].length / 4
             * * 3]); }
             */

        } catch (ParserException ex) {
            Logger.getLogger(PairwiseStructureComparison.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(PairwiseStructureComparison.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(PairwiseStructureComparison.class.getName()).log(Level.SEVERE, null, ex);
        }


    }

    public double[] getPercentGaps(ArrayList<SecondaryStructureData> structureData, int windowSize) {
        double[] array = new double[structureData.get(0).pairedSites.length];
        double[] sliding = new double[array.length - windowSize];
        double count = 0;
        for (SecondaryStructureData s : structureData) {
            for (int i = 0; i < s.pairedSites.length; i++) {
                if (s.sequence.charAt(i) == '-') {
                    array[i]++;
                }
            }
            count++;
        }
        for (int i = 0; i < array.length; i++) {
            array[i] /= count;
        }
        for (int i = 0; i < sliding.length; i++) {
            for (int j = i; j < i + windowSize; j++) {
                sliding[i] += array[j];
            }
            sliding[i] /= (double) windowSize;
        }
        return sliding;
    }

    public static class Combination {

        public ArrayList<String> combination;

        public Combination(String[] combination) {
            this.combination = new ArrayList<>();
            for (int i = 0; i < combination.length; i++) {
                this.combination.add(combination[i]);
            }
        }

        public boolean contains(String s1) {
            return combination.contains(s1);
        }

        public boolean contains(String s1, String s2) {
            return combination.contains(s1) && combination.contains(s2);
        }

        @Override
        public String toString() {
            String ret = "";
            for (int i = 0; i < combination.size(); i++) {
                ret += combination.get(i);
                if (i != combination.size() - 1) {
                    ret += "_";
                }
            }
            return ret;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Combination other = (Combination) obj;
            if (!Objects.equals(this.combination, other.combination)) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 29 * hash + Objects.hashCode(this.combination);
            return hash;
        }
    }

    public static String leftPad(String s, char padChar, int length) {
        String ret = s;
        for (int i = 0; i < length - s.length(); i++) {
            ret = padChar + ret;
        }
        return ret;
    }

    public static ArrayList<Combination> combinations(String[] s) {
        /*
         * String [] s = new String[t.length*2]; for(int i = 0 ; i < t.length ;
         * i++) { s[i] = t[i]; }
         *
         * for(int i = t.length ; i < s.length ; i++) { s[i] = t[i-t.length]; }
         */

        ArrayList<Combination> possibilities = new ArrayList<>();

        for (int i = 1; i < Math.pow(2, s.length); i++) {
            String p = leftPad(Integer.toBinaryString(i), '0', s.length);
            int c = 0;
            for (int j = 0; j < p.length(); j++) {
                if (p.charAt(j) == '1') {
                    c++;
                }
            }
            String[] q = new String[c];
            c = 0;
            for (int j = 0; j < p.length(); j++) {
                if (p.charAt(j) == '1') {
                    q[c] = s[j];
                    c++;
                }
            }
            Arrays.sort(q);
            if (!possibilities.contains(new Combination(q))) {
                possibilities.add(new Combination(q));
            }
        }
        return possibilities;
    }

    public void postAnalysis(File inFile, File outFile, String[] categories, ArrayList<Combination> combinations, ArrayList<SecondaryStructureData> structureData, int windowSize, ArrayList<String> pairMustContain) {
        double[] gaps = getPercentGaps(structureData, windowSize);
        double gapPerc = 0.3;

        String mustHave = "";

        if (pairMustContain.size() > 0) {
            mustHave = "_pairmusthave";
            for (int i = 0; i < pairMustContain.size(); i++) {
                mustHave += "_" + pairMustContain.get(i);
            }
        }

        for (Combination combination : combinations) {
            boolean mayContinue = true;
            for (int i = 0; i < pairMustContain.size(); i++) {
                if (!combination.contains(pairMustContain.get(i))) {
                    mayContinue = false;
                }
            }
            if (!mayContinue) {
                continue;
            }

            ArrayList<Integer> structures = new ArrayList<>();
            try {
                BufferedReader buffer = new BufferedReader(new FileReader(inFile));
                BufferedWriter writer2 = new BufferedWriter(new FileWriter(outFile.getAbsolutePath() + "_" + combination + mustHave + "_structures.txt"));
                double totalPairs = 0;
                String textline = null;
                ArrayList<double[]> data = new ArrayList<>();
                int dataLength = -1;
                long startTime = System.currentTimeMillis();
                while ((textline = buffer.readLine()) != null) {
                    String[] split = textline.split("(\\s)+");
                    //ArrayList<Double> values = new ArrayList<>();
                    dataLength = dataLength == -1 ? split.length - 2 : dataLength;
                    double[] values = new double[dataLength];

                    int x = Integer.parseInt(split[0].split("_")[0]);
                    int y = Integer.parseInt(split[0].split("_")[1]);

                    String o1 = split[1].split("_")[0];
                    String o2 = split[1].split("_")[1];

                    mayContinue = true;
                    for (int i = 0; i < pairMustContain.size(); i++) {
                        // exactly one of the pair must contain value i
                        if (!(pairMustContain.get(i).equals(o1) ^ pairMustContain.get(i).equals(o2))) {
                            mayContinue = false;
                            break;
                        }
                    }

                    if (!mayContinue) {
                        continue;
                    }

                    if (combination.contains(o1, o2)) {

                        for (int i = 2; i < split.length; i++) {
                            values[i - 2] = Double.parseDouble(split[i]);
                        }
                        if (dataLength == split.length - 2) // if last line too short because of file I/O do not add
                        {
                            data.add(values);
                        }

                        writer2.write(RNAFoldingTools.getDotBracketStringFromPairedSites(structureData.get(x).pairedSites));
                        writer2.newLine();
                        writer2.write(RNAFoldingTools.getDotBracketStringFromPairedSites(structureData.get(y).pairedSites));
                        writer2.newLine();

                        // System.out.println(combination);
                        // System.out.println(o1 + "\t" + x);
                        // System.out.println(o2 + "\t" + y);
                        structures.add(x);
                        structures.add(y);

                        if (data.size() >= 10000) {
                            break;
                        }
                    }
                }
                long endTime = System.currentTimeMillis();
                double elapsedTime = (double) (endTime - startTime) / 1000.0;
                System.out.println("Elapsed time = " + data.size() + "\t" + elapsedTime);
                buffer.close();
                System.out.println(combination + "\t" + data.size());

                ArrayList<Double> medianPvals = new ArrayList<>();


                if (data.size() > 0) {
                    BufferedWriter writer = new BufferedWriter(new FileWriter(outFile.getAbsolutePath() + "_" + combination + mustHave));
                    for (int i = 0; i < data.get(0).length; i++) {
                        ArrayList<Double> valuesAtPos = new ArrayList<>();
                        for (int j = 0; j < data.size(); j++) {
                            valuesAtPos.add(data.get(j)[i]);
                        }

                        double medianPval = RankingAnalyses.getMedian(valuesAtPos);
                        double percentile25 = StatUtils.getInvCDF(RankingAnalyses.getPercentile(valuesAtPos, 0.25) / 2, true);
                        double percentile75 = StatUtils.getInvCDF(RankingAnalyses.getPercentile(valuesAtPos, 0.75) / 2, true);
                        double percentile90 = StatUtils.getInvCDF(RankingAnalyses.getPercentile(valuesAtPos, 0.90) / 2, true);
                        double zscore = StatUtils.getInvCDF(medianPval / 2, true);
                        medianPvals.add(medianPval);
                        writer.write(i + "\t" + gaps[i] + "\t" + medianPval + "\t" + zscore + "\t" + (gaps[i] >= gapPerc ? "1\t0\t0\t0\t0" : medianPval + "\t" + zscore + "\t" + percentile25 + "\t" + percentile75 + "\t" + percentile90));
                        //writer.write(i + "\t" + gaps[i] + "\t" + medianPval + "\t" + zscore + "\t" + (gaps[i] >= 0.2 ? "1\t0" : medianPval + "\t" + zscore));

                        writer.newLine();

                        //System.out.println(i + "\t" + RankingAnalyses.getMedian(valuesAtPos));
                    }
                    writer.close();
                }

                boolean[] inWindow = new boolean[structureData.get(0).pairedSites.length];
                for (int i = 0; i < medianPvals.size(); i++) {
                    if (gaps[i] < gapPerc && medianPvals.get(i) <= 0.05) {
                        for (int j = i; j < i + windowSize; j++) {
                            inWindow[j] = true;
                        }
                    }
                }
                for (int i = 0; i < medianPvals.size(); i++) {
                    if (gaps[i] >= gapPerc) {
                        writer2.write('-');
                    } else if (medianPvals.get(i) <= 0.05) {
                        writer2.write('*');
                    } else {
                        writer2.write(' ');
                    }
                }
                writer2.newLine();

                for (int i = 0; i < inWindow.length; i++) {
                    if (inWindow[i]) {
                        writer2.write('*');
                    } else {
                        writer2.write(' ');
                    }
                }
                writer2.newLine();

                int start = -1;
                int end = -1;
                ArrayList<Substructure> substructures = new ArrayList<>();
                for (int i = 0; i < inWindow.length; i++) {
                    if (inWindow[i] && (i == 0 || !inWindow[i - 1])) {
                        start = i;
                    }

                    if (inWindow[i] && (i == inWindow.length - 1 || !inWindow[i + 1])) {
                        end = i;
                        int length = end - start + 1;
                        substructures.add(new Substructure(start, length));
                        //System.out.println(start+"-"+end);
                    }
                }


                BufferedWriter substructureWriter = new BufferedWriter(new FileWriter(outFile.getAbsolutePath() + "_" + combination + mustHave + ".substructures"));
                for (Substructure substructure : substructures) {
                    ArrayList<Double> pvalues = new ArrayList<>();
                    for (int i = substructure.start; i < substructure.start + substructure.length - windowSize + 1; i++) {
                        pvalues.add(medianPvals.get(i));
                    }
                    int l = Math.max(0, (1000 - substructure.length) / 2);
                    System.out.println("l=" + l + "\t" + substructure.start + "\t" + substructure.length);
                    substructure.startMatrix = Math.max(0, substructure.start - l);
                    int endPos = Math.min(structureData.get(0).pairedSites.length, substructure.startMatrix + substructure.length + 2 * l);
                    substructure.matrixLength = endPos - substructure.startMatrix;

                    substructure.medianPvalue = RankingAnalyses.getMedian(pvalues);

                    substructure.matrix = new double[substructure.matrixLength][substructure.matrixLength];
                    for (int a = 0; a < structures.size(); a++) {
                        SecondaryStructureData structure = structureData.get(structures.get(a));
                        for (int x = 0; x < structure.pairedSites.length; x++) {
                            int y = structure.pairedSites[x] - 1;
                            if (x - substructure.startMatrix >= 0 && x - substructure.startMatrix < substructure.matrixLength) {
                                if (y - substructure.startMatrix >= 0 && y - substructure.startMatrix < substructure.matrixLength) {

                                    substructure.matrix[x - substructure.startMatrix][y - substructure.startMatrix]++;
                                    //System.out.println(x - substructure.start+"\t"+(y - substructure.start)+"\t"+substructure.matrix[x - substructure.start][y - substructure.start]);
                                }
                            }
                        }
                    }
                    DecimalFormat df = new DecimalFormat("0.000");
                    double t = structures.size();
                    for (int x = 0; x < substructure.matrix.length; x++) {
                        for (int y = 0; y < substructure.matrix[0].length; y++) {
                            substructure.matrix[x][y] /= t;
                        }
                    }
                    substructure.pairedSites = RNAFoldingTools.getPosteriorDecodingConsensusStructure(substructure.matrix);
                    substructure.pairedSites = StructureAlign.getSubstructure(substructure.pairedSites, substructure.start - substructure.startMatrix, substructure.length);
                    System.out.println(substructure.toString());
                    substructureWriter.write(substructure.toString() + "\n");
                }
                substructureWriter.close();

                BufferedWriter svgWriter = new BufferedWriter(new FileWriter(outFile.getAbsolutePath() + "_" + combination + mustHave + ".svg"));
                svgWriter.write(getSVGRepresentationSubstructure(substructures, structureData.get(0).pairedSites.length, combination.toString() + mustHave));
                svgWriter.close();


                BufferedWriter matrixWriter = new BufferedWriter(new FileWriter(outFile.getAbsolutePath() + "_" + combination + mustHave + ".matrix"));
                String structure = "";
                double[] pairingProbability = new double[structureData.get(0).pairedSites.length];
                for (int i = 0; i < structureData.get(0).pairedSites.length; i++) {
                    for (Substructure substructure : substructures) {
                        if (substructure.start == i) {
                            structure += RNAFoldingTools.getDotBracketStringFromPairedSites(substructure.pairedSites);

                            for (int j = i; j < i + substructure.length; j++) {
                                pairingProbability[j] = substructure.pairingProbability[j - i];
                            }
                            i += substructure.length;

                            for (int x = substructure.start - substructure.startMatrix; x < substructure.start - substructure.startMatrix + substructure.length; x++) {
                                for (int y = substructure.start - substructure.startMatrix; y < substructure.start - substructure.startMatrix + substructure.length; y++) {
                                    //pairingProbability[x - (substructure.start - substructure.startMatrix)] += substructure.matrix[x][y];
                                    if (substructure.matrix[x][y] != 0) {
                                        matrixWriter.write((x + substructure.startMatrix) + "," + (y + substructure.startMatrix) + "," + substructure.matrix[x][y]);
                                        matrixWriter.newLine();
                                    }
                                }
                            }


                            /*
                             * for (int x = 0; x < substructure.length; x++) {
                             * for (int y = 0; y < substructure.length; y++) {
                             * if (substructure.matrix[x][y] != 0) {
                             * matrixWriter.write((substructure.start + x) + ","
                             * + (substructure.start + y) + "," +
                             * substructure.matrix[x][y]);
                             * matrixWriter.newLine(); } } }
                             */
                        }

                    }
                    structure += "#";
                }

                matrixWriter.close();


                System.out.println(structure);
                writer2.write(structure);
                writer2.newLine();
                writer2.close();

                BufferedWriter csvWriter = new BufferedWriter(new FileWriter(outFile.getAbsolutePath() + "_" + combination + mustHave + ".csv"));
                csvWriter.write("Position,Probability\n");
                for (int i = 0; i < pairingProbability.length; i++) {
                    csvWriter.write((i + 1) + "," + pairingProbability[i] + "\n");
                }
                csvWriter.close();

                BufferedWriter allWriter = new BufferedWriter(new FileWriter(outFile.getAbsolutePath() + "_summary.txt", true));
                allWriter.write(">" + combination + mustHave);
                allWriter.newLine();
                allWriter.write(structure);
                allWriter.newLine();
                allWriter.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static class Substructure {

        int start;
        int startMatrix;
        int length;
        int matrixLength;
        double[][] matrix;
        int[] pairedSites;
        double medianPvalue;
        double[] pairingProbability;
        DecimalFormat df = new DecimalFormat("0.000");

        public Substructure(int start, int length) {
            this.start = start;
            this.length = length;
        }

        public void calculatePairingProbability(double[][] matrix) {
            pairingProbability = new double[length];

            /*
             * for (int i = start - startMatrix; i < start - startMatrix +
             * length; i++) { for (int j = start - startMatrix; j < start -
             * startMatrix + length; j++) { pairingProbability[i - (start -
             * startMatrix)] += matrix[i][j]; } }
             */

            for (int i = 0; i < matrixLength; i++) {
                for (int j = 0; j < matrixLength; j++) {
                    if (i - (start - startMatrix) >= 0 && i - (start - startMatrix) < length) {
                        pairingProbability[i - (start - startMatrix)] += matrix[i][j];
                    }
                }
            }
        }

        @Override
        public String toString() {
            String ret = "";
            ret = start + " - " + (start + length) + " (" + medianPvalue + ")" + " : " + RNAFoldingTools.getDotBracketStringFromPairedSites(pairedSites);
            ret += "\n";
            calculatePairingProbability(matrix);
            String dbn = RNAFoldingTools.getDotBracketStringFromPairedSites(pairedSites);
            for (int i = 0; i < pairingProbability.length; i++) {
                int y = pairedSites[i];
                double p = 0;
                if (y != 0) {

                    p = matrix[i + (start - startMatrix)][y - 1 + (start - startMatrix)];
                }
                System.out.println(i + "\t" + dbn.charAt(i) + "\t" + df.format(p) + "\t" + df.format(pairingProbability[i]));
            }
            return ret;
        }
    }

    public static String getSVGRepresentationSubstructure(ArrayList<Substructure> substructures, int length, String label) {
        int panelWidth = 1000;
        int panelHeight = 12;


        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        pw.println("<?xml version=\"1.0\" standalone=\"no\"?>");
        pw.println("<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\" \n\"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">");
        pw.println("<svg xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\" width=\"" + panelWidth + "\" height=\"" + panelHeight + "\" style=\"fill:none;stroke-width:16\">");


        int fontSize = 9;

        pw.println("<text x=\"" + (-5) + "\" y=\"" + ((panelHeight / 2) + (fontSize / 2)) + "\" style=\"font-size:" + fontSize + "px;stroke:none;fill:black\" text-anchor=\"" + "end" + "\">");
        pw.println("<tspan>" + label + "</tspan>");
        pw.println("</text>");
        pw.println("<g>");
        pw.println("<rect x=\"" + (0) + "\" y=\"" + 0 + "\" width=\"" + (panelWidth) + "\" height=\"" + (panelHeight) + "\"  style=\"fill:#" + GraphicsUtils.getHexString(Color.white) + ";\"/>");
        for (Substructure substructure : substructures) {
            double x = ((double) substructure.start / (double) length) * panelWidth;
            double width = ((double) substructure.length / (double) length) * panelWidth;

            DataTransform transform = new DataTransform(0.0001, 0.5, TransformType.NORMSINV1);
            ColorGradient gradient = new ColorGradient(Color.darkGray, Color.white);

            System.out.println(">>>" + substructure.medianPvalue + "\t" + gradient.getColor(transform.transform((float) substructure.medianPvalue)) + "\t" + transform.transform((float) substructure.medianPvalue));

            pw.println("<rect x=\"" + (x) + "\" y=\"" + 0 + "\" width=\"" + (width) + "\" height=\"" + (panelHeight) + "\"  style=\"fill:#" + GraphicsUtils.getHexString(gradient.getColor(transform.transform((float) substructure.medianPvalue))) + ";\"/>");
        }
        pw.println("</g>");
        /*
         * pw.println("<text x=\"" + (x + xoffset + regionWidth / 2) + "\" y=\""
         * + (rulerHeight + feature.row * blockHeight + blockHeight / 2 +
         * (fontSize / 2)) + "\" style=\"font-size:" + fontSize +
         * "px;stroke:none;fill:black\" text-anchor=\"" + "middle" + "\" >");
         * pw.println("<tspan>" + feature.name + "</tspan>");
         * pw.println("</text>");
         */

        pw.println("</svg>");
        pw.close();
        //System.out.println(sw.toString());
        return sw.toString();
    }

    public static double[] mountainSumPlot(ArrayList<SecondaryStructureData> structuralAlignment) {
        double[] sum = new double[structuralAlignment.get(0).pairedSites.length];

        for (SecondaryStructureData structure : structuralAlignment) {
            double[] mountain = MountainMetrics.getMountainVector(structure.pairedSites, false);

            for (int i = 0; i < mountain.length; i++) {
                sum[i] += mountain[i];
                //System.out.print(sum[i] + "\t");
            }
            // System.out.println();
        }

        for (int i = 0; i < sum.length; i++) {
            sum[i] /= (double) structuralAlignment.size();
        }

        return sum;
    }

    public static void main(String[] args) {
        new PairwiseStructureComparison().runComparison();
    }

    public class StructureItem {

        String organism = "";
        String dotBracketStructure;
        int[] pairedSites;
        String sequence;
        String title;

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final StructureItem other = (StructureItem) obj;
            if (!Objects.equals(this.organism, other.organism)) {
                return false;
            }
            if (!Objects.equals(this.dotBracketStructure, other.dotBracketStructure)) {
                return false;
            }
            if (!Arrays.equals(this.pairedSites, other.pairedSites)) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 53 * hash + Objects.hashCode(this.organism);
            hash = 53 * hash + Objects.hashCode(this.dotBracketStructure);
            hash = 53 * hash + Arrays.hashCode(this.pairedSites);
            return hash;
        }
    }
}
