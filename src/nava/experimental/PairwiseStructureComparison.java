/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.experimental;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import nava.data.io.FileImport;
import nava.data.io.FileImport.ParserException;
import nava.data.types.DataType;
import nava.data.types.SecondaryStructureData;
import nava.ranking.RankingAnalyses;
import nava.ranking.StatUtils;
import nava.structure.StructureAlign;
import nava.utils.Pair;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class PairwiseStructureComparison {

    public void runComparison() {
        try {
            int permutations = 2000;
            int windowSize = 75;
            
            /*File outFile = new File("C:/dev/thesis/dengue/dengue_permutations2.txt");
            String[] categories = {"dengue1", "dengue2", "dengue3", "dengue4"};
            System.out.println(combinations(categories));
            ArrayList<SecondaryStructureData> structureData = FileImport.loadStructures(new File("C:/dev/thesis/dengue-alignment.dbn"), DataType.FileFormat.VIENNA_DOT_BRACKET);

            ArrayList<Combination> combinations = combinations(categories);
            for (int i = 0; i < combinations.size(); i++) {
                Combination combination = combinations.get(i);
                if (combination.combination.size() <= 2 || combination.combination.size() == categories.length) {
                } else {
                    combinations.remove(i);
                    i--;
                }
            }*/
            
           /* File outFile = new File("C:/dev/thesis/hiv_full/hiv_full_permutations_75.txt");
            String[] categories = {"hiv1b", "hiv1c", "hiv1d", "hiv1g", "hiv1o", "hiv2", "siv"};
            String[] categories_hiv1 = {"hiv1b", "hiv1c", "hiv1d", "hiv1g", "hiv1o"};
            String[] categories_hiv1_hiv2 = {"hiv1b", "hiv1c", "hiv1d", "hiv1g", "hiv1o", "hiv2"};
            System.out.println(combinations(categories));
            ArrayList<SecondaryStructureData> structureData = FileImport.loadStructures(new File("C:/dev/thesis/hiv_full/hiv_full_aligned.dbn"), DataType.FileFormat.VIENNA_DOT_BRACKET);

            ArrayList<Combination> combinations = combinations(categories);
            for (int i = 0; i < combinations.size(); i++) {
                Combination combination = combinations.get(i);
                if (combination.combination.size() <= 2 || combination.combination.size() == categories.length) {
                } else {
                    combinations.remove(i);
                    i--;
                }
            }
            combinations.add(new Combination(categories_hiv1));
            combinations.add(new Combination(categories_hiv1_hiv2));*/
            
            File outFile = new File("C:/dev/thesis/hcv/hcv_75_2.txt");
            String[] categories = {"hcv1a", "hcv1b", "hcv2a", "hcv2b", "hcv3", "hcv4", "hcv6"};
            //String[] categories_hiv1 = {"hiv1b", "hiv1c", "hiv1d", "hiv1g", "hiv1o"};
           // String[] categories_hiv1_hiv2 = {"hiv1b", "hiv1c", "hiv1d", "hiv1g", "hiv1o", "hiv2"};
            System.out.println(combinations(categories));
            ArrayList<SecondaryStructureData> structureData = FileImport.loadStructures(new File("C:/dev/thesis/hcv/hcv_genotypes2_aligned.dbn"), DataType.FileFormat.VIENNA_DOT_BRACKET);

            ArrayList<Combination> combinations = combinations(categories);
            for (int i = 0; i < combinations.size(); i++) {
                Combination combination = combinations.get(i);
                if (combination.combination.size() <= 2 || combination.combination.size() == categories.length) {
                } else {
                    combinations.remove(i);
                    i--;
                }
            }

            
            ArrayList<String> pairMustContain = new ArrayList<>();
            //pairMustContain.add("siv");
            pairMustContain.add("hcv3");
            
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
                if (catPerc[cati] > catFrac || catPerc[catj] > catFrac) { // balance for the fact that we have an uneven number of sequences from each category
                    continue;
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
                double[] pairwiseSimilarity = sal.parallelizedPermutationTestSliding(s1.pairedSites, s2.pairedSites, windowSize, true, permutations);
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

            try {
                BufferedReader buffer = new BufferedReader(new FileReader(inFile));
                String textline = null;
                ArrayList<ArrayList<Double>> data = new ArrayList<>();
                while ((textline = buffer.readLine()) != null) {
                    String[] split = textline.split("(\\s)+");
                    ArrayList<Double> values = new ArrayList<>();

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
                            values.add(Double.parseDouble(split[i]));
                        }
                        if (data.size() == 0 || data.get(0).size() == values.size()) // if last line too short because of file I/O do not add
                        {
                            data.add(values);
                        }
                    }
                }
                buffer.close();
                System.out.println(combination + "\t" + data.size());

                if (data.size() > 0) {
                    String mustHave = "";
                    if (pairMustContain.size() > 0) {
                        mustHave = "_pairmusthave";
                        for (int i = 0; i < pairMustContain.size(); i++) {
                            mustHave += "_" + pairMustContain.get(i);
                        }
                    }
                    BufferedWriter writer = new BufferedWriter(new FileWriter(outFile.getAbsolutePath() + "_" + combination + mustHave));
                    for (int i = 0; i < data.get(0).size(); i++) {
                        ArrayList<Double> valuesAtPos = new ArrayList<>();
                        for (int j = 0; j < data.size(); j++) {
                            valuesAtPos.add(data.get(j).get(i));
                        }
                        double medianPval = RankingAnalyses.getMedian(valuesAtPos);
                        double percentile25 = StatUtils.getInvCDF(RankingAnalyses.getPercentile(valuesAtPos, 0.25) / 2, true);
                        double percentile75 = StatUtils.getInvCDF(RankingAnalyses.getPercentile(valuesAtPos, 0.75) / 2, true);
                        double percentile90 = StatUtils.getInvCDF(RankingAnalyses.getPercentile(valuesAtPos, 0.90) / 2, true);
                        double zscore = StatUtils.getInvCDF(medianPval / 2, true);
                        writer.write(i + "\t" + gaps[i] + "\t" + medianPval + "\t" + zscore + "\t" + (gaps[i] >= 0.2 ? "1\t0\t0\t0\t0" : medianPval + "\t" + zscore + "\t" + percentile25 + "\t" + percentile75 + "\t" + percentile90));
                        //writer.write(i + "\t" + gaps[i] + "\t" + medianPval + "\t" + zscore + "\t" + (gaps[i] >= 0.2 ? "1\t0" : medianPval + "\t" + zscore));

                        writer.newLine();

                        //System.out.println(i + "\t" + RankingAnalyses.getMedian(valuesAtPos));
                    }
                    writer.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
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
