/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.structure;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import nava.data.io.FileImport;
import nava.data.io.FileImport.ParserException;
import nava.data.io.IO;
import nava.data.io.ReadseqTools;
import nava.data.types.DataType;
import nava.data.types.SecondaryStructureData;
import nava.structure.StructureAlign.Method;
import nava.structure.StructureAlign.Region;
import nava.tasks.applications.Application;
import nava.utils.RNAFoldingTools;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class PhylogeneticIdentification {

    public static String normalizeLanlName(String lanlName) {
        if (lanlName.contains("Ref")) {
            String[] split = lanlName.split("(\\s)+");
            String[] split2 = split[split.length - 1].split("\\.");
            String genotype = split2[1];
            String id = split2[split2.length - 1];
            return genotype + "_" + id;
        }

        return lanlName;
    }

    public static String getGenosubtypeFromeLanlName(String lanlName) {
        if (lanlName.contains("Ref")) {
            String[] split = lanlName.split("(\\s)+");
            String[] split2 = split[split.length - 1].split("\\.");
            String genotype = split2[1];
            String id = split2[split2.length - 1];
            return genotype;
        } else {
            String[] split = lanlName.split("_");
            if (split.length >= 2) {
                return split[1];
            }
        }

        return "";
    }

    public static void main(String[] args) {
        File structureFile = new File("C:/dev/full-alignment4.dbn");

        File treeFile = new File("conserved/temp.nwk");
        String treeString = "";
        try {
            BufferedReader buffer = new BufferedReader(new FileReader(treeFile));
            treeString = buffer.readLine();
            System.out.println(treeString);
            buffer.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        String ref = "conserved//out_1_79.fas";
        try {
            PhylogeneticIdentification.resaveTree(new File(ref), treeFile, "temp", "conserved//");
        } catch (Exception ex) {
            Logger.getLogger(PhylogeneticIdentification.class.getName()).log(Level.SEVERE, null, ex);
        }


        ArrayList<DataType.FileFormat> formats = FileImport.parsableStructureFormats(structureFile);
        try {
            ArrayList<SecondaryStructureData> structures = FileImport.loadStructures(structureFile, formats.get(0));


            ArrayList<Structure> alignedStructures = new ArrayList<>();
            ArrayList<String> alignedSequences = new ArrayList<>();
            ArrayList<String> names = new ArrayList<>();
            //int windowSize = 75;
            //double cutoff = 0.80;
            //boolean relaxed = true;
            //Method method = Method.MEAN;

            int windowSize = 75;
            double cutoff = 0.75;
            boolean relaxed = true;
            Method method = Method.MINIMUM;

            for (int i = 0; i < structures.size(); i++) {
                alignedStructures.add(new Structure(structures.get(i).pairedSites, structures.get(i).title));
                alignedSequences.add(structures.get(i).sequence);
                names.add(structures.get(i).title);
            }

            ArrayList<Structure> shapeAlignedStructures = new ArrayList<>();
            ArrayList<String> shapeAlignedSequences = new ArrayList<>();
            ArrayList<String> shapeNames = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                shapeAlignedStructures.add(new Structure(structures.get(i).pairedSites, structures.get(i).title));
                shapeAlignedSequences.add(structures.get(i).sequence);
                shapeNames.add(structures.get(i).title);
            }

            ArrayList<int[]> slist = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                slist.add(alignedStructures.get(i).pairedSites);
            }
            double [] slope = MountainMetrics.compareMountainSlopes(alignedStructures.get(0).pairedSites,alignedStructures.get(2).pairedSites, false);

            String dir = "conserved/";
            ArrayList<Region> conservedRegions = StructureAlign.getConservedStructures(shapeAlignedStructures, shapeAlignedSequences, shapeNames, windowSize, cutoff, relaxed, method);
            boolean slidingSim = true;
            int permutations = -1;

            //String dir = "conserved/weeks/";
            // ArrayList<Region> conservedRegions = new ArrayList<>();
            //conservedRegions.addAll(getH77Regions(shapeAlignedSequences, 0));
            //conservedRegions.addAll(getConRegions(shapeAlignedSequences, 1));
            //conservedRegions.addAll(getJFH1Regions(shapeAlignedSequences, 2));
            /*
             * String dir = "conserved/weeks/"; ArrayList<Region>
             * conservedRegions = new ArrayList<>();
             * conservedRegions.addAll(getH77Regions(shapeAlignedSequences, 0));
             * conservedRegions.addAll(getConRegions(shapeAlignedSequences, 1));
             * conservedRegions.addAll(getJFH1Regions(shapeAlignedSequences,
             * 2));
             */
            for (Region conservedRegion : conservedRegions) {
                if (!conservedRegions.get(conservedRegions.size() - 1).equals(conservedRegion)) {
                    //   continue;
                }
                int count = 0;

                String filename = (conservedRegion.startPos + 1) + "_" + (conservedRegion.startPos + 1 + conservedRegion.length);
                String filepath = dir + "out_" + filename + ".fas";
                String outTreeString = treeString.replaceAll("_\\*+", "");
                //HashMap<String, Double> genotypeSumTable = new HashMap<>();
                //HashMap<String, Double> genotypeCountTable = new HashMap<>();
                //HashMap<String, Double> genosubtypeSumTable = new HashMap<>();
                //HashMap<String, Double> genotsubtypeCountTable = new HashMap<>();
                SpreadsheetCalculation genotypeSimCalc = new SpreadsheetCalculation();
                SpreadsheetCalculation genotypePermCalc = new SpreadsheetCalculation();
                SpreadsheetCalculation genosubtypeSimCalc = new SpreadsheetCalculation();
                SpreadsheetCalculation genosubtypePermCalc = new SpreadsheetCalculation();
                double avgSimExcludingShape = 0;
                double countSimExcludingShape = 0;
                double avgPermExcludingShape = 0;
                double countPermExcludingShape = 0;
                BufferedWriter permutationBuffer = new BufferedWriter(new FileWriter(dir + filename + "_perm.txt", true));
                for (int j = 0; j < alignedStructures.size(); j++) {
                    Structure genotypeStructure = alignedStructures.get(j);
                    int[] genotypePairedSites = StructureAlign.getSubstructureRelaxed(genotypeStructure.pairedSites, conservedRegion.startPos, conservedRegion.length);
                    double avgSim = 0;
                    double avgPerm = 0;
                    double total = 0;
                    for (int i = 0; i < 3; i++) {
                        Structure shapeStructure = alignedStructures.get(i);
                        if (i != j) {
                            int[] shapePairedSites = StructureAlign.getSubstructureRelaxed(shapeStructure.pairedSites, conservedRegion.startPos, conservedRegion.length);
                            double sim = 0;
                            if (slidingSim) {
                                sim = StructureAlign.slidingWeightedMountainSimilarityAverage(genotypePairedSites, shapePairedSites, windowSize - 1, relaxed);
                            } else {
                                sim = 1 - MountainMetrics.calculateNormalizedWeightedMountainDistance(genotypePairedSites, shapePairedSites);
                            }

                            double pval = StructureAlign.permutationTest(conservedRegion, sim, genotypeStructure.pairedSites, shapeStructure.pairedSites, windowSize, relaxed, slidingSim, permutations);

                            try {
                                permutationBuffer.write(i + "_" + j + "\t" + sim + "\t" + pval);
                                permutationBuffer.newLine();
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }

                            avgSim += sim;
                            avgPerm += pval;

                            total += 1;
                        }
                    }
                    avgSim /= total;
                    avgPerm /= total;
                    if (avgSim >= cutoff) {
                        count++;
                    }

                    try {
                        if (j == 0) {
                            BufferedWriter buffer = new BufferedWriter(new FileWriter(new File(filepath)));
                            buffer.close();
                        }
                        BufferedWriter buffer = new BufferedWriter(new FileWriter(new File(filepath), true));
                        String append = "";
                        if (avgSim >= 0.9) {
                            append = "_***";
                        } else if (avgSim >= 0.8) {
                            append = "_**";
                        } else if (avgSim >= 0.7) {
                            append = "_*";
                        }
                        String lanlName = normalizeLanlName(names.get(j));
                        String genosubtype = PhylogeneticIdentification.getGenosubtypeFromeLanlName(names.get(j));
                        String genotype = genosubtype.length() > 0 ? genosubtype.substring(0, 1) : "";
                        if (!genotype.equals("S")) {
                            avgSimExcludingShape += avgSim;
                            countSimExcludingShape += 1;
                            avgPermExcludingShape += avgPerm;
                            countPermExcludingShape += 1;
                        }
                        genotypeSimCalc.add(genotype, avgSim);
                        genosubtypeSimCalc.add(genosubtype, avgSim);
                        genotypePermCalc.add(genotype, avgPerm);
                        genosubtypePermCalc.add(genosubtype, avgPerm);

                        if (avgSim >= 0.7) {
                            // System.out.println(lanlName + "\t" + append);
                        }
                        outTreeString = outTreeString.replaceAll(lanlName + "(_\\**)?", lanlName + append);
                        buffer.write(">" + lanlName + append);
                        buffer.newLine();
                        buffer.write(alignedSequences.get(j));
                        buffer.newLine();
                        buffer.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
                permutationBuffer.close();

                BufferedWriter treeBuffer = new BufferedWriter(new FileWriter(new File(dir + filename + ".nwk"), false));
                treeBuffer.write(outTreeString);
                treeBuffer.close();


                //String region = (conservedRegion.startPos + 1) + "-" + (conservedRegion.startPos + 1 + conservedRegion.length);
                String region = (conservedRegion.startPos) + "-" + (conservedRegion.startPos + conservedRegion.length);
                System.out.println(region + "\t" + count + "\t" + alignedStructures.size());


                ArrayList<String> rowNames = genotypeSimCalc.getAscendingRowNames();
                for (int i = 0; i < rowNames.size(); i++) {
                    String rowName = rowNames.get(i);
                    double avgSim = genotypeSimCalc.getAverage(rowName);
                    double permSim = genotypePermCalc.getAverage(rowName);
                    int cnt = genotypeSimCalc.getCount(rowName);
                    System.out.println(rowName + "\t" + avgSim + "\t" + permSim + "\t" + cnt);
                }
                System.out.println("avg\t" + (avgSimExcludingShape / countSimExcludingShape) + "\t" + (avgPermExcludingShape / countPermExcludingShape));

            }
        } catch (ParserException ex) {
            Logger.getLogger(PhylogeneticIdentification.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(PhylogeneticIdentification.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(PhylogeneticIdentification.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            //PhylogeneticIdentification.inferTree(new File(ref), "full", "conserved//");
        } catch (Exception ex) {
            Logger.getLogger(PhylogeneticIdentification.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void inferTree2(File fastaFile, String name, String dir) throws Exception {

        //Process p = Runtime.getRuntime().exec("PhyML_3.0_win32.exe -i " + outputFilePhylip.getAbsolutePath() + " -d nt -m HKY85 -s NNI -o tlr -b 0");
        Process p = Runtime.getRuntime().exec("bin/raxmlHPC.exe -m GTRCAT -n raxmlrun -s " + fastaFile.getAbsolutePath() + " -n " + name + " -w " + dir);
        //Process p = Runtime.getRuntime().exec("raxmlHPC-PTHREADS.exe -m GTRCAT -n raxmlrun -s " + outputFilePhylip.getAbsolutePath() + " -n "+ base +" -w temp\\ -T 2");

        Application.nullOutput(p.getErrorStream());
        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String textline;
        while ((textline = reader.readLine()) != null) {
            System.out.println(":" + textline);
        }
        reader.close();

        if (p.waitFor() == 0) {
            BufferedReader fileReader = new BufferedReader(new FileReader(dir + "RAxML_bestTree." + name));
            String tree = fileReader.readLine();
            fileReader.close();
            if (tree != null) {
                BufferedWriter fileWriter = new BufferedWriter(new FileWriter(dir + File.separatorChar + name + ".nwk"));
                fileWriter.write(tree.replaceAll("[0-9]\\.[0-9]+\\:", ":"));
                fileWriter.newLine();
                fileWriter.close();
            }
        }
    }

    public static void resaveTree(File fastaFile, File inTree, String outName, String dir) throws Exception {
        ArrayList<String> sequences = new ArrayList<>();
        ArrayList<String> sequenceNames = new ArrayList<>();
        IO.loadFastaSequences(fastaFile, sequences, sequenceNames);

        ArrayList<String> newSequenceNames = new ArrayList<>();
        for (int i = 0; i < sequenceNames.size(); i++) {
            newSequenceNames.add("seq" + i);
        }

        BufferedReader fileReader = new BufferedReader(new FileReader(inTree));
        String tree = fileReader.readLine();
        fileReader.close();
        if (tree != null) {
            System.out.println(sequenceNames);
            System.out.println(tree);
            BufferedWriter fileWriter = new BufferedWriter(new FileWriter(dir + File.separatorChar + outName + ".nwk"));
            for (int i = 0; i < sequenceNames.size(); i++) {
                tree = tree.replaceAll("seq" + i + ":", sequenceNames.get(i) + ":");
            }
            tree = tree.replaceAll("[0-9]\\.[0-9]+\\:", ":");
            System.out.println(tree);
            fileWriter.write(tree);
            fileWriter.newLine();
            fileWriter.close();
        }
    }

    public static void inferTree(File fastaFile, String name, String dir) throws Exception {

        ArrayList<String> sequences = new ArrayList<>();
        ArrayList<String> sequenceNames = new ArrayList<>();
        IO.loadFastaSequences(fastaFile, sequences, sequenceNames);

        ArrayList<String> newSequenceNames = new ArrayList<>();
        for (int i = 0; i < sequenceNames.size(); i++) {
            newSequenceNames.add("seq" + i);
        }
        File tempFastaFile = new File(dir + File.separatorChar + "temp_renamed.fasta");
        IO.saveToFASTAfile(sequences, newSequenceNames, tempFastaFile);

        File phyFile = new File(dir + File.separatorChar + "temp.phy");
        String outName = "temp";
        ReadseqTools.convertToFormat(12, tempFastaFile, phyFile);
        Process p = Runtime.getRuntime().exec("bin/PhyML_3.0_win32.exe -i " + phyFile.getAbsolutePath() + " -d nt -m GTR -s NNI -o tlr -b 0");

        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String textline;
        while ((textline = reader.readLine()) != null) {
            System.out.println(":" + textline);
        }
        reader.close();

        if (p.waitFor() == 0) {
            BufferedReader fileReader = new BufferedReader(new FileReader(dir + File.separatorChar + outName + ".phy_phyml_tree.txt"));
            String tree = fileReader.readLine();
            fileReader.close();
            if (tree != null) {
                BufferedWriter fileWriter = new BufferedWriter(new FileWriter(dir + File.separatorChar + outName + ".nwk"));
                for (int i = 0; i < sequenceNames.size(); i++) {
                    tree = tree.replaceAll("seq" + i, sequenceNames.get(i).replaceAll("_\\*+", ""));
                }
                tree = tree.replaceAll("[0-9]\\.[0-9]+\\:", ":");
                fileWriter.write(tree);
                fileWriter.newLine();
                fileWriter.close();
            }
        }
    }

    public static ArrayList<Region> getH77Regions(ArrayList<String> sequences, int seq) {
        ArrayList<Region> h77regions = new ArrayList<>();
        h77regions.add(Region.getRegion(getGappedPosition(sequences.get(seq), 908), getGappedPosition(sequences.get(seq), 993)));
        h77regions.add(Region.getRegion(getGappedPosition(sequences.get(seq), 6503), getGappedPosition(sequences.get(seq), 6645)));
        h77regions.add(Region.getRegion(getGappedPosition(sequences.get(seq), 6648), getGappedPosition(sequences.get(seq), 6680)));
        h77regions.add(Region.getRegion(getGappedPosition(sequences.get(seq), 6719), getGappedPosition(sequences.get(seq), 6746)));
        h77regions.add(Region.getRegion(getGappedPosition(sequences.get(seq), 6769), getGappedPosition(sequences.get(seq), 6877)));
        h77regions.add(Region.getRegion(getGappedPosition(sequences.get(seq), 6884), getGappedPosition(sequences.get(seq), 6959)));
        h77regions.add(Region.getRegion(getGappedPosition(sequences.get(seq), 7423), getGappedPosition(sequences.get(seq), 7602)));
        return h77regions;
    }

    public static ArrayList<Region> getConRegions(ArrayList<String> sequences, int seq) {
        ArrayList<Region> conregions = new ArrayList<Region>();
        conregions.add(Region.getRegion(getGappedPosition(sequences.get(seq), 927), getGappedPosition(sequences.get(seq), 1020)));
        conregions.add(Region.getRegion(getGappedPosition(sequences.get(seq), 6503), getGappedPosition(sequences.get(seq), 6645)));
        conregions.add(Region.getRegion(getGappedPosition(sequences.get(seq), 6648), getGappedPosition(sequences.get(seq), 6680)));
        conregions.add(Region.getRegion(getGappedPosition(sequences.get(seq), 6886), getGappedPosition(sequences.get(seq), 6966)));
        conregions.add(Region.getRegion(getGappedPosition(sequences.get(seq), 7444), getGappedPosition(sequences.get(seq), 7584)));
        return conregions;
    }

    public static ArrayList<Region> getJFH1Regions(ArrayList<String> sequences, int seq) {
        ArrayList<Region> jfhregions = new ArrayList<Region>();
        jfhregions.add(Region.getRegion(getGappedPosition(sequences.get(seq), 901), getGappedPosition(sequences.get(seq), 997)));
        jfhregions.add(Region.getRegion(getGappedPosition(sequences.get(seq), 6556), getGappedPosition(sequences.get(seq), 6637)));
        jfhregions.add(Region.getRegion(getGappedPosition(sequences.get(seq), 6639), getGappedPosition(sequences.get(seq), 6667)));
        jfhregions.add(Region.getRegion(getGappedPosition(sequences.get(seq), 6728), getGappedPosition(sequences.get(seq), 6754)));
        jfhregions.add(Region.getRegion(getGappedPosition(sequences.get(seq), 6763), getGappedPosition(sequences.get(seq), 6897)));
        jfhregions.add(Region.getRegion(getGappedPosition(sequences.get(seq), 6904), getGappedPosition(sequences.get(seq), 6984)));
        jfhregions.add(Region.getRegion(getGappedPosition(sequences.get(seq), 7501), getGappedPosition(sequences.get(seq), 7646)));
        return jfhregions;
    }

    public static int getGappedPosition(String gappedSeq, int ungappedPos) {
        int ungapped = 0;
        for (int i = 0; i < gappedSeq.length(); i++) {
            if (gappedSeq.charAt(i) != '-') {
                if (ungapped == ungappedPos) {
                    return i;
                }
                ungapped++;
            }
        }

        return -1;
    }

    public static class SpreadsheetCalculation {

        private HashMap<String, Double> sumTable = new HashMap<>();
        private HashMap<String, Double> countTable = new HashMap<>();

        public void add(String rowName, double value) {
            sumTable.put(rowName, sumTable.get(rowName) == null ? value : sumTable.get(rowName) + value);
            countTable.put(rowName, countTable.get(rowName) == null ? 1 : countTable.get(rowName) + 1);
        }

        public ArrayList<String> getAscendingRowNames() {
            Set<String> keySet = sumTable.keySet();
            Iterator<String> it = keySet.iterator();
            ArrayList<String> keyList = new ArrayList<>();
            while (it.hasNext()) {
                keyList.add(it.next());
            }
            Collections.sort(keyList);
            return keyList;
        }

        public double getSum(String rowName) {
            return sumTable.get(rowName);
        }

        public int getCount(String rowName) {
            return countTable.get(rowName).intValue();
        }

        public double getAverage(String rowName) {
            return (sumTable.get(rowName) / countTable.get(rowName));
        }

        public void printSummary() {
            ArrayList<String> keys = getAscendingRowNames();
            for (int i = 0; i < keys.size(); i++) {
                double avg = sumTable.get(keys.get(i)) / countTable.get(keys.get(i));
                System.out.println(keys.get(i) + "\t" + avg + "\t" + countTable.get(keys.get(i)));
            }
        }
    }
}
