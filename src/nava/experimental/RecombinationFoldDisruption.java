/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.experimental;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import nava.data.io.CsvReader;
import nava.data.io.IO;
import nava.experimental.AutomatedFolding.Fold;
import nava.ranking.MyMannWhitney;
import nava.ranking.RankingAnalyses;
import nava.ranking.StatUtils;
import nava.structure.MountainMetrics;
import nava.structure.StructureAlign;
import nava.utils.RNAFoldingTools;
import org.apache.commons.math3.stat.inference.ChiSquareTest;
import org.apache.commons.math3.stat.inference.MannWhitneyUTest;
import org.apache.commons.math3.stat.ranking.NaNStrategy;
import org.apache.commons.math3.stat.ranking.TiesStrategy;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class RecombinationFoldDisruption {

    public class RecombinationEvent {

        int id;
        int breakpointStart;
        int breakpointEnd;
        int start;
        int length;
        String recombinantName;
        String minorParentName;
        String majorParentName;
        String recombinantSequence;
        String minorParentSequence;
        String majorParentSequence;
        double rdpPval;
        boolean breakpointUndetermined = false;
        int circulationCount = 0;

        @Override
        public String toString() {
            return "RecombinationEvent{" + "id=" + id + ", breakpointStart=" + breakpointStart + ", breakpointEnd=" + breakpointEnd + ", start=" + start + ", length=" + length + ", recombinantName=" + recombinantName + ", minorParentName=" + minorParentName + ", majorParentName=" + majorParentName + ", rdpPval=" + rdpPval + ", breakpointUndetermined=" + breakpointUndetermined + ", circulationCount=" + circulationCount + '}';
        }

        public String getRecombinant() {
            StringBuffer sb = new StringBuffer(majorParentSequence);
            int seqLength = majorParentSequence.length();
            for (int i = 0; i < length; i++) {
                int pos = (i + start) % seqLength;
                sb.setCharAt(pos, minorParentSequence.charAt((pos)));
            }
            return sb.toString();
        }

        @Override
        public RecombinationEvent clone() {
            RecombinationEvent clone = new RecombinationEvent();
            clone.id = id;
            clone.breakpointStart = breakpointStart;
            clone.breakpointEnd = breakpointEnd;
            clone.start = start;
            clone.length = length;
            clone.recombinantName = recombinantName;
            clone.minorParentName = minorParentName;
            clone.majorParentName = majorParentName;
            clone.recombinantSequence = recombinantSequence;
            clone.minorParentSequence = minorParentSequence;
            clone.majorParentSequence = majorParentSequence;
            clone.rdpPval = rdpPval;
            clone.breakpointUndetermined = breakpointUndetermined;
            clone.circulationCount = circulationCount;
            return clone;
        }
    }

    public class InformativeSiteCalculation {

        int[] informativeSitesRunningSum;

        public InformativeSiteCalculation(String seq1, String seq2) {
            int seq1Length = seq1.length();
            informativeSitesRunningSum = new int[seq1Length * 2];

            informativeSitesRunningSum[0] = seq1.charAt(0) != seq2.charAt(0) ? 1 : 0;
            for (int i = 1; i < informativeSitesRunningSum.length; i++) {
                informativeSitesRunningSum[i] = informativeSitesRunningSum[i - 1];
                int pos = i % seq1Length;
                /*
                 * if (seq1.charAt(pos) != seq2.charAt(pos) && seq1.charAt(pos)
                 * != '-' && seq2.charAt(pos) != '-') {
                 * informativeSitesRunningSum[i]++; }
                 */
                if (seq1.charAt(pos) != seq2.charAt(pos)) {
                    informativeSitesRunningSum[i]++;
                }
                //System.out.println(informativeSitesRunningSum[i]);
            }
        }

        public int getNumberOfInformativeSites(int start, int length) {
            int lower = 0;
            if (start > 0) {
                lower = informativeSitesRunningSum[start - 1];
            }
            return informativeSitesRunningSum[start + length - 1] - lower;
        }
    }

    /*
     * public static int getNumberOfInformativeSites(String seq1, String seq2,
     * int start, int length) { int count = 0; for (int i = 0; i < length; i++)
     * { int pos = (start + i) % seq1.length(); if (seq1.charAt(pos) !=
     * seq2.charAt(pos)) { count++; } } return count; }
     */
    public String getSequence(ArrayList<String> sequences, ArrayList<String> sequenceNames, String sequenceName) {
        int index = sequenceNames.indexOf(sequenceName);
        if (index >= 0) {
            return sequences.get(index);
        }
        return null;
    }

    public ArrayList<RecombinationEvent> loadRecombinationEvents(File alignmentFile, File rdpCSVFile, boolean onlyWhereBothParentsAreKnown) throws IOException {
        ArrayList<String> sequences = new ArrayList<>();
        ArrayList<String> sequenceNames = new ArrayList<>();

        IO.loadFastaSequences(alignmentFile, sequences, sequenceNames);

        ArrayList<RecombinationEvent> events = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(rdpCSVFile));
        String textline = null;
        reader.readLine();
        reader.readLine();
        reader.readLine();
        RecombinationEvent event = new RecombinationEvent();
        while ((textline = reader.readLine()) != null) {
            String[] split = textline.split(",");
            if (split.length > 1 && split[1].length() > 0) {
                event = new RecombinationEvent();
                event.id = Integer.parseInt(split[0].trim());
                event.breakpointStart = Integer.parseInt(split[2].trim().replaceAll("\\*", ""));
                event.breakpointEnd = Integer.parseInt(split[3].trim().replaceAll("\\*", ""));
                event.recombinantName = split[8].trim();
                event.minorParentName = split[9].trim();
                event.majorParentName = split[10].trim();
                event.rdpPval = split[11].trim().equalsIgnoreCase("NS") ? 1 : Double.parseDouble(split[11].trim());
                event.recombinantSequence = getSequence(sequences, sequenceNames, event.recombinantName);
                event.minorParentSequence = getSequence(sequences, sequenceNames, event.minorParentName);
                event.majorParentSequence = getSequence(sequences, sequenceNames, event.majorParentName);
                event.start = event.breakpointStart - 1;
                if (event.breakpointEnd > event.breakpointStart) {
                    event.length = event.breakpointEnd - event.breakpointStart + 1;
                } else {
                    event.length = (event.recombinantSequence.length() - event.breakpointStart + 1) + event.breakpointEnd;
                }


                if (split[2].contains("*") || split[3].contains("*")) {
                    event.breakpointUndetermined = true;
                }

                if (onlyWhereBothParentsAreKnown && !event.minorParentName.startsWith("Unknown") && !event.majorParentName.startsWith("Unknown")) {
                    events.add(event);
                } else if (!onlyWhereBothParentsAreKnown) {
                    events.add(event);
                }
            } else if (split.length > 8 && split[8].length() > 0) {
                event.circulationCount++;
            }
        }
        return events;
    }

    public RecombinationEvent generatePermutedRecombinationEvent(Random random, RecombinationEvent event) {
        return generatePermutedRecombinationEvent(random, event, false);
    }

    public RecombinationEvent generatePermutedRecombinationEvent(Random random, int newRandomStart, RecombinationEvent event, boolean simplePermutation) {
        int newStart = newRandomStart;
        RecombinationEvent newEvent = event.clone();
        newEvent.start = newStart;
        if (simplePermutation) {
        } else {
            InformativeSiteCalculation infCalc = new InformativeSiteCalculation(event.minorParentSequence, event.majorParentSequence);
            int informativeSitesReal = infCalc.getNumberOfInformativeSites(event.start, event.length);
            int newLength = informativeSitesReal;

            int informativeSitesPermuted = infCalc.getNumberOfInformativeSites(newStart, newLength);
            while (informativeSitesReal != informativeSitesPermuted) {
                newLength++;
                informativeSitesPermuted = infCalc.getNumberOfInformativeSites(newStart, newLength);
            }

            int newLength2 = newLength;

            int informativeSitesPermuted2 = infCalc.getNumberOfInformativeSites(newStart, newLength2);
            while (informativeSitesReal + 1 != informativeSitesPermuted2) {
                newLength2++;
                informativeSitesPermuted2 = infCalc.getNumberOfInformativeSites(newStart, newLength2);
            }

            int diff = newLength2 - newLength;
            //System.out.println("Diff: "+diff);
            newEvent.length = newLength + random.nextInt(diff);
        }
        return newEvent;
    }

    public RecombinationEvent generatePermutedRecombinationEvent(Random random, RecombinationEvent event, boolean simplePermutation) {
        int newStart = random.nextInt(event.recombinantSequence.length());
        while (newStart == event.start) {
            newStart = random.nextInt(event.recombinantSequence.length());
        }
        RecombinationEvent newEvent = event.clone();
        newEvent.start = newStart;
        if (simplePermutation) {
        } else {
            InformativeSiteCalculation infCalc = new InformativeSiteCalculation(event.minorParentSequence, event.majorParentSequence);
            int informativeSitesReal = infCalc.getNumberOfInformativeSites(event.start, event.length);
            int newLength = informativeSitesReal;

            int informativeSitesPermuted = infCalc.getNumberOfInformativeSites(newStart, newLength);
            while (informativeSitesReal != informativeSitesPermuted) {
                newLength++;
                informativeSitesPermuted = infCalc.getNumberOfInformativeSites(newStart, newLength);
            }

            int newLength2 = newLength;

            int informativeSitesPermuted2 = infCalc.getNumberOfInformativeSites(newStart, newLength2);
            while (informativeSitesReal + 1 != informativeSitesPermuted2) {
                newLength2++;
                informativeSitesPermuted2 = infCalc.getNumberOfInformativeSites(newStart, newLength2);
            }

            int diff = newLength2 - newLength;
            //System.out.println("Diff: "+diff);
            newEvent.length = newLength + random.nextInt(diff);
        }
        return newEvent;
    }
    HashMap<String, Fold> foldCache = new HashMap<>();

    public Fold fold(String sequence, int threads) {
        String key = sequence.replaceAll("-", "").toUpperCase();
        if (foldCache.containsKey(key)) {
            Fold fold = foldCache.get(key);
            fold.cached = true;
            return fold;
        }

        Fold fold = AutomatedFolding.fold(key, threads);
        foldCache.put(key, fold);
        return fold;
    }
    int numberSaved = -1;

    public void saveFoldCache() throws IOException {
        BufferedWriter buffer = new BufferedWriter(new FileWriter("fold.cache"));
        Iterator<String> keyIterator = foldCache.keySet().iterator();
        while (keyIterator.hasNext()) {
            String key = keyIterator.next();
            Fold fold = foldCache.get(key);
            buffer.write(fold + "\n");
        }
        numberSaved = foldCache.size();
        buffer.close();
    }

    public void saveFoldCache(int atLeastNWaiting) throws IOException {
        if (numberSaved == -1) {
            numberSaved = foldCache.size();
        }
        if (numberSaved == -1 || foldCache.size() - numberSaved >= atLeastNWaiting) {
            saveFoldCache();
        }
    }

    public void loadFoldCache() {
        try {
            BufferedReader buffer = new BufferedReader(new FileReader("fold.cache"));
            String textline = null;
            while ((textline = buffer.readLine()) != null) {
                Fold fold = Fold.getFoldFromString(textline);
                foldCache.put(fold.sequence, fold);
            }
            buffer.close();
        } catch (IOException ex) {
        }
    }

    public void performTest2() {



        Random random = new Random(7920171293137101310L);
        Random random2 = new Random(3811018041148014014L);
        File alignmentFile = new File("C:/dev/thesis/hiv_full/test/darren_hiv.fas");
         File rdpCSVFile = new File("C:/dev/thesis/hiv_full/test/darren_hiv.csv");
        //  File alignmentFile = new File("C:/dev/thesis/porcine/300/porcine_all_300_aligned.fas");
        // File rdpCSVFile = new File("C:/dev/thesis/porcine/300/porcine.csv");
        //  File alignmentFile = new File("C:/dev/thesis/hiv_full/hiv1/200/hiv1_all_200_aligned.fas");
        // File rdpCSVFile = new File("C:/dev/thesis/hiv_full/hiv1/200/recombination.csv");
        //File alignmentFile = new File("C:/dev/thesis/norovirus/200/norovirus.fas");
        // File rdpCSVFile = new File("C:/dev/thesis/norovirus/200/norovirus.csv");
        //File alignmentFile = new File("C:/dev/thesis/hcv/1/300/hcv1_all_300_aligned.fas");
        //File rdpCSVFile = new File("C:/dev/thesis/hcv/1/300/hcv.csv");
       // File alignmentFile = new File("C:/dev/thesis/dengue/400/all_400_aligned_curated.fas");
        //File rdpCSVFile = new File("C:/dev/thesis/dengue/400/all_400_aligned_curated.csv");
        ArrayList<String> sequences = new ArrayList<>();
        ArrayList<String> sequenceNames = new ArrayList<>();

        IO.loadFastaSequences(alignmentFile, sequences, sequenceNames);
        loadFoldCache();
        double[] count = new double[sequences.get(0).length()];
        double t = 0;
        double[] pairingProbability = new double[count.length];
        for (int i = 0; i < sequences.size(); i++) {
            System.out.println((i + 1) + " / " + sequences.size());
            int seqno = random.nextInt(sequences.size());
            String seq = sequences.get(seqno);
            Fold f = fold(seq, 8);
            if (!f.cached) {
                try {
                    saveFoldCache(10);
                } catch (IOException ex) {
                    Logger.getLogger(RecombinationFoldDisruption.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            String dbs = RNAFoldingTools.getDotBracketStringFromPairedSites(f.pairedSites);
            String dbs_aligned = StructureAlign.mapStringToAlignedSequence(dbs, seq, "-");
            int[] pairedSites = RNAFoldingTools.getPairedSitesFromDotBracketString(dbs_aligned);
            for (int j = 0; j < pairedSites.length; j++) {
                count[j] += pairedSites[j] != 0 ? 1 : 0;
            }
            t++;
            for (int j = 0; j < count.length; j++) {
                pairingProbability[j] = count[j] / t;
            }
        }

        ArrayList<String> values = null;
        try {
            values = CsvReader.getColumn(new File("C:/dev/hiv-1-shape-reactivities.csv"), 2);
        } catch (IOException ex) {
            Logger.getLogger(RecombinationFoldDisruption.class.getName()).log(Level.SEVERE, null, ex);
        }
        values.remove(0);

        MappedData mappedData = MappedData.getMappedData(alignmentFile, new File("C:/dev/NL4-3-shape-alignment.fas"), values, false, "", 1000, false);
        //pairingProbability= mappedData.values;
        double[] pairingProbabilityWindow = new double[pairingProbability.length];
        int window = 20;

        boolean fast = true;
        for (int i = 0; i < pairingProbabilityWindow.length; i++) {
            double c = 0;
            for (int j = Math.max(0, i - window); j < Math.min(pairingProbabilityWindow.length, i + window); j++) {
                pairingProbabilityWindow[i] += pairingProbability[j];
                c++;
            }
            pairingProbabilityWindow[i] /= c;
        }
        try {
            loadFoldCache();
            ArrayList<RecombinationEvent> recombinationEvents = loadRecombinationEvents(alignmentFile, rdpCSVFile, true);
            ArrayList<RecombinationEvent> selectedRecombinationEvents = new ArrayList<>();
            for (RecombinationEvent event : recombinationEvents) {
                // if (!event.breakpointUndetermined && event.circulationCount >= 0 && event.recombinantName.matches("SN[0-9]+_.+")) {
               if (!event.breakpointUndetermined && event.circulationCount >= 0) {
                    selectedRecombinationEvents.add(event);

                    System.out.println(selectedRecombinationEvents.size() + "_" + event);
                }
                /*
                 * if (!event.breakpointUndetermined && event.circulationCount
                 * >= 0 && event.recombinantName.matches("SN[0-9]+_.+")) {
                 * selectedRecombinationEvents.add(event);
                 *
                 * System.out.println(selectedRecombinationEvents.size() + "_" +
                 * event); }
                 */
                // System.out.println(selectedRecombinationEvents.size() + "_" + event);
                if (selectedRecombinationEvents.size() >= 3000) {
                    break;
                }
            }

            int[] recombinationCount = new int[selectedRecombinationEvents.get(0).majorParentSequence.length()];
            for (RecombinationEvent r : selectedRecombinationEvents) {
                System.out.println(r.start + "\t" + (r.start + r.length) + "\t" + recombinationCount.length);
                recombinationCount[r.start]++;
                recombinationCount[(r.start + r.length) % recombinationCount.length]++;
            }

            for (int j = 0; j <= 0; j++) {

                ArrayList<Double> realPairingValues = new ArrayList<>();
                ArrayList<Double> permutedPairingValues = new ArrayList<>();
                ArrayList<Double> real5PrimePairingValues = new ArrayList<>();
                ArrayList<Double> permuted5PrimePairingValues = new ArrayList<>();
                ArrayList<Double> real3PrimePairingValues = new ArrayList<>();
                ArrayList<Double> permuted3PrimePairingValues = new ArrayList<>();

                ArrayList<Double> realEnergyValues = new ArrayList<>();
                ArrayList<Double> permutedEnergyValues = new ArrayList<>();
                ArrayList<Double> realSimilarityValues = new ArrayList<>();
                ArrayList<Double> permutedSimilarityValues = new ArrayList<>();
                ArrayList<Double> realDisruptionValues = new ArrayList<>();
                ArrayList<Double> permutedDisruptionValues = new ArrayList<>();
                long[][] observedCountsOR = new long[2][2];
                long[][] observedCountsAND = new long[2][2];
                int UNPAIRED = 0, PAIRED = 1;
                int PERMUTED = 0, REAL = 1;

                for (int i = 0; i < 10000; i++) {
                    int randomOffset = random.nextInt(pairingProbability.length);
                    for (int h = 0; h < selectedRecombinationEvents.size(); h++) {
                        RecombinationEvent realEvent = selectedRecombinationEvents.get(h).clone();
                        realEvent.start = (realEvent.start + j + pairingProbabilityWindow.length) % pairingProbabilityWindow.length;
                        // RecombinationEvent permutedEvent2 = generatePermutedRecombinationEvent(random, realEvent2, false);

                        RecombinationEvent permutedEvent = generatePermutedRecombinationEvent(random, (realEvent.start + randomOffset) % pairingProbability.length, realEvent, false);

                        Fold minorParentFold = null;
                        Fold majorParentFold = null;
                        Fold realRecombinantFold = null;
                        Fold permutedRecombinantFold = null;
                        String minorAligned = null;
                        String majorAligned = null;
                        String realAligned = null;
                        String permutedAligned = null;
                        int[] minorPairedSites = null;
                        int[] majorPairedSites = null;

                        if (!fast) {
                            minorParentFold = fold(realEvent.minorParentSequence, 8);
                            majorParentFold = fold(realEvent.majorParentSequence, 8);
                            realRecombinantFold = fold(realEvent.getRecombinant(), 8);
                            permutedRecombinantFold = fold(permutedEvent.getRecombinant(), 8);

                            minorAligned = StructureAlign.mapStringToAlignedSequence(RNAFoldingTools.getDotBracketStringFromPairedSites(minorParentFold.pairedSites), realEvent.minorParentSequence, "-");
                            majorAligned = StructureAlign.mapStringToAlignedSequence(RNAFoldingTools.getDotBracketStringFromPairedSites(majorParentFold.pairedSites), realEvent.majorParentSequence, "-");
                            realAligned = StructureAlign.mapStringToAlignedSequence(RNAFoldingTools.getDotBracketStringFromPairedSites(realRecombinantFold.pairedSites), realEvent.getRecombinant(), "-");
                            permutedAligned = StructureAlign.mapStringToAlignedSequence(RNAFoldingTools.getDotBracketStringFromPairedSites(permutedRecombinantFold.pairedSites), permutedEvent.getRecombinant(), "-");

                            minorPairedSites = RNAFoldingTools.getPairedSitesFromDotBracketString(minorAligned);
                            majorPairedSites = RNAFoldingTools.getPairedSitesFromDotBracketString(majorAligned);
                            saveFoldCache(10);
                        }
                        // real += pairingProbabilityWindow[realEvent2.start];
                        // perm += pairingProbabilityWindow[permutedEvent2.start];

                        // real += pairingProbabilityWindow[(realEvent2.start + realEvent2.length) % pairingProbability.length];
                        //perm += pairingProbabilityWindow[(permutedEvent2.start + permutedEvent2.length) % pairingProbability.length];

                        if (i == 0) {
                            realPairingValues.add(pairingProbabilityWindow[realEvent.start]);
                            realPairingValues.add(pairingProbabilityWindow[(realEvent.start + realEvent.length) % pairingProbability.length]);
                            if (realEvent.start < (realEvent.start + realEvent.length) % pairingProbability.length) {
                                real5PrimePairingValues.add(pairingProbabilityWindow[realEvent.start]);
                                real3PrimePairingValues.add(pairingProbabilityWindow[(realEvent.start + realEvent.length) % pairingProbability.length]);
                            } else {
                                real3PrimePairingValues.add(pairingProbabilityWindow[realEvent.start]);
                                real5PrimePairingValues.add(pairingProbabilityWindow[(realEvent.start + realEvent.length) % pairingProbability.length]);
                            }

                            if (!fast) {
                                observedCountsOR[REAL][isPaired(minorPairedSites, majorPairedSites, realEvent.start, false) ? 1 : 0]++;
                                observedCountsOR[REAL][isPaired(minorPairedSites, majorPairedSites, (realEvent.start + realEvent.length) % pairingProbability.length, false) ? 1 : 0]++;
                                observedCountsAND[REAL][isPaired(minorPairedSites, majorPairedSites, realEvent.start, true) ? 1 : 0]++;
                                observedCountsAND[REAL][isPaired(minorPairedSites, majorPairedSites, (realEvent.start + realEvent.length) % pairingProbability.length, true) ? 1 : 0]++;

                                realEnergyValues.add(realRecombinantFold.freeEnergy);
                                realSimilarityValues.add(averageSimilarityToParents(minorAligned, majorAligned, realAligned));
                                realDisruptionValues.add((double) getDisruptionScore(minorAligned, majorAligned, realAligned, true));
                            }


                        }

                        permutedPairingValues.add(pairingProbabilityWindow[permutedEvent.start]);
                        permutedPairingValues.add(pairingProbabilityWindow[(permutedEvent.start + permutedEvent.length) % pairingProbability.length]);
                        if (permutedEvent.start < (permutedEvent.start + permutedEvent.length) % pairingProbability.length) {
                            permuted5PrimePairingValues.add(pairingProbabilityWindow[permutedEvent.start]);
                            permuted3PrimePairingValues.add(pairingProbabilityWindow[(permutedEvent.start + permutedEvent.length) % pairingProbability.length]);
                        } else {
                            permuted3PrimePairingValues.add(pairingProbabilityWindow[permutedEvent.start]);
                            permuted5PrimePairingValues.add(pairingProbabilityWindow[(permutedEvent.start + permutedEvent.length) % pairingProbability.length]);
                        }

                        if (!fast) {
                            observedCountsOR[PERMUTED][isPaired(minorPairedSites, majorPairedSites, permutedEvent.start, false) ? 1 : 0]++;
                            observedCountsOR[PERMUTED][isPaired(minorPairedSites, majorPairedSites, (permutedEvent.start + permutedEvent.length) % pairingProbability.length, false) ? 1 : 0]++;
                            observedCountsAND[PERMUTED][isPaired(minorPairedSites, majorPairedSites, permutedEvent.start, true) ? 1 : 0]++;
                            observedCountsAND[PERMUTED][isPaired(minorPairedSites, majorPairedSites, (permutedEvent.start + permutedEvent.length) % pairingProbability.length, true) ? 1 : 0]++;


                            permutedEnergyValues.add(permutedRecombinantFold.freeEnergy);
                            permutedSimilarityValues.add(averageSimilarityToParents(minorAligned, majorAligned, permutedAligned));
                            permutedDisruptionValues.add((double) getDisruptionScore(minorAligned, majorAligned, permutedAligned, true));

                        }

                        MyMannWhitney mwpairing = new MyMannWhitney(realPairingValues, permutedPairingValues);
                        MannWhitneyUTest mwpairing2 = new MannWhitneyUTest(NaNStrategy.REMOVED, TiesStrategy.RANDOM);
                        MyMannWhitney mw5primepairing = new MyMannWhitney(real5PrimePairingValues, permuted5PrimePairingValues);
                        MyMannWhitney mw3primepairing = new MyMannWhitney(real3PrimePairingValues, permuted3PrimePairingValues);
                        MyMannWhitney mw5prime3prime = new MyMannWhitney(real5PrimePairingValues, real3PrimePairingValues);
                        MyMannWhitney mwenergy = new MyMannWhitney(realEnergyValues, permutedEnergyValues);
                        MyMannWhitney mwsimilarity = new MyMannWhitney(realSimilarityValues, permutedSimilarityValues);
                        MyMannWhitney mwdisruption = new MyMannWhitney(realDisruptionValues, permutedDisruptionValues);
                        System.out.println("pairing\t" + j + "\t" + RankingAnalyses.getMedian(realPairingValues) + "\t" + RankingAnalyses.getMedian(permutedPairingValues) + "\t" + mwpairing.getZ() + "\t" + realPairingValues.size() + "\t" + permutedPairingValues.size());
                        double pairing2pval = mwpairing2.mannWhitneyUTest(RankingAnalyses.getArray(realPairingValues), RankingAnalyses.getArray(permutedPairingValues));
                        //System.out.println("pairing2\t"+pairing2pval+"\t"+StatUtils.getInvCDF(pairing2pval, true)+"\t"+StatUtils.getInvCDF(pairing2pval/2, true));
                        System.out.println("pairing 5'\t" + j + "\t" + RankingAnalyses.getMedian(real5PrimePairingValues) + "\t" + RankingAnalyses.getMedian(permuted5PrimePairingValues) + "\t" + mw5primepairing.getZ() + "\t" + real5PrimePairingValues.size() + "\t" + permuted5PrimePairingValues.size());
                        System.out.println("pairing 3'\t" + j + "\t" + RankingAnalyses.getMedian(real3PrimePairingValues) + "\t" + RankingAnalyses.getMedian(permuted3PrimePairingValues) + "\t" + mw3primepairing.getZ() + "\t" + real3PrimePairingValues.size() + "\t" + permuted3PrimePairingValues.size());
                        System.out.println("5' vs. 3'\t" + j + "\t" + RankingAnalyses.getMedian(real5PrimePairingValues) + "\t" + RankingAnalyses.getMedian(real3PrimePairingValues) + "\t" + mw5prime3prime.getZ());
                        System.out.println("energy\t" + j + "\t" + RankingAnalyses.getMedian(realEnergyValues) + "\t" + RankingAnalyses.getMedian(permutedEnergyValues) + "\t" + mwenergy.getZ() + "\t" + realEnergyValues.size() + "\t" + permutedEnergyValues.size());
                        System.out.println("similarity\t" + j + "\t" + RankingAnalyses.getMedian(realSimilarityValues) + "\t" + RankingAnalyses.getMedian(permutedSimilarityValues) + "\t" + mwsimilarity.getZ() + "\t" + realSimilarityValues.size() + "\t" + permutedSimilarityValues.size());
                        System.out.println("disruption\t" + j + "\t" + RankingAnalyses.getMedian(realDisruptionValues) + "\t" + RankingAnalyses.getMedian(permutedDisruptionValues) + "\t" + mwdisruption.getZ() + "\t" + realDisruptionValues.size() + "\t" + permutedDisruptionValues.size());
                        ChiSquareTest chiPairingTest = new ChiSquareTest();
                        for (int recombinant = 0; recombinant < observedCountsOR.length; recombinant++) {
                            for (int ispaired = 0; ispaired < observedCountsOR[0].length; ispaired++) {
                                System.out.print(observedCountsOR[recombinant][ispaired] + "\t");
                            }
                            System.out.println();
                        }
                        System.out.println("p-value (OR) = " + chiPairingTest.chiSquareTest(observedCountsOR));
                        for (int recombinant = 0; recombinant < observedCountsAND.length; recombinant++) {
                            for (int ispaired = 0; ispaired < observedCountsAND[0].length; ispaired++) {
                                System.out.print(observedCountsAND[recombinant][ispaired] + "\t");
                            }
                            System.out.println();
                        }
                        System.out.println("p-value (AND) = " + chiPairingTest.chiSquareTest(observedCountsAND));
                        System.out.println();

                    }
                }

                MyMannWhitney mwpairing = new MyMannWhitney(realPairingValues, permutedPairingValues);
                MyMannWhitney mwenergy = new MyMannWhitney(realEnergyValues, permutedEnergyValues);
                System.out.println(j + "\t" + RankingAnalyses.getMedian(realPairingValues) + "\t" + RankingAnalyses.getMedian(permutedPairingValues) + "\t" + mwpairing.getZ());
                System.out.println(j + "\t" + RankingAnalyses.getMedian(realEnergyValues) + "\t" + RankingAnalyses.getMedian(permutedEnergyValues) + "\t" + mwenergy.getZ());
                //System.out.println();
                // System.out.println();
            }



            /*
             * BufferedWriter writer = new BufferedWriter(new FileWriter(new
             * File("rfd.out")));
             *
             * double countDisruption = 0; double sumDisruptionReal = 0; double
             * sumDisruptionPermuted = 0; double total = 0; double countEnergy =
             * 0; double countParentalSimilarity = 0; double countBasePairsReal
             * = 0; double countBasePairsPermuted = 0; for (int i = 0; i <
             * 100000000; i++) { int a =
             * random.nextInt(selectedRecombinationEvents.size());
             * RecombinationEvent realEvent =
             * selectedRecombinationEvents.get(a); RecombinationEvent
             * permutedEvent = generatePermutedRecombinationEvent(random,
             * realEvent);
             *
             * Fold minorParentFold = fold(realEvent.minorParentSequence, 8);
             * Fold majorParentFold = fold(realEvent.majorParentSequence, 8);
             * Fold realRecombinantFold = fold(realEvent.getRecombinant(), 8);
             * Fold permutedRecombinantFold =
             * fold(permutedEvent.getRecombinant(), 8);
             *
             * String minorAligned =
             * StructureAlign.mapStringToAlignedSequence(RNAFoldingTools.getDotBracketStringFromPairedSites(minorParentFold.pairedSites),
             * realEvent.minorParentSequence, "-"); String majorAligned =
             * StructureAlign.mapStringToAlignedSequence(RNAFoldingTools.getDotBracketStringFromPairedSites(majorParentFold.pairedSites),
             * realEvent.majorParentSequence, "-"); String realAligned =
             * StructureAlign.mapStringToAlignedSequence(RNAFoldingTools.getDotBracketStringFromPairedSites(realRecombinantFold.pairedSites),
             * realEvent.getRecombinant(), "-"); String permutedAligned =
             * StructureAlign.mapStringToAlignedSequence(RNAFoldingTools.getDotBracketStringFromPairedSites(permutedRecombinantFold.pairedSites),
             * permutedEvent.getRecombinant(), "-");
             *
             * writer.write(realEvent.toString() + "\n");
             * writer.write(permutedEvent.toString() + "\n");
             * writer.write(realEvent.minorParentSequence + "\n");
             * writer.write(realEvent.majorParentSequence + "\n");
             * writer.write(realEvent.getRecombinant() + "\n");
             * writer.write(permutedEvent.getRecombinant() + "\n");
             * writer.write(minorAligned + "\n"); writer.write(majorAligned +
             * "\n"); writer.write(realAligned + "\n");
             * writer.write(permutedAligned + "\n"); writer.flush();
             *
             * //System.out.println(realAligned);
             * //System.out.println(permutedAligned); int realDisruption =
             * getDisruptionScore(minorAligned, majorAligned, realAligned,
             * true); int permutedDisruption = getDisruptionScore(minorAligned,
             * majorAligned, permutedAligned, true); sumDisruptionReal +=
             * realDisruption; sumDisruptionPermuted += permutedDisruption;
             * double realSimilarityToParents =
             * averageSimilarityToParents(minorAligned, majorAligned,
             * realAligned); double permutedSimilarityToParents =
             * averageSimilarityToParents(minorAligned, majorAligned,
             * permutedAligned); //double basePairCountReal =
             * getBasePairCount(realAligned,realEvent); // double
             * basePairCountPermuted =
             * getBasePairCount(realAligned,permutedEvent); for (int m = 0; m <
             * 1000; m++) { RecombinationEvent permutedEvent2 =
             * generatePermutedRecombinationEvent(random2, realEvent);
             *
             * //System.out.println(m+"> "+getBasePairCount(minorAligned,
             * realEvent)); //System.out.println(m+">
             * "+getBasePairCount(minorAligned, permutedEvent2));
             * //System.out.println(m+"> "+getBasePairCount(majorAligned,
             * realEvent)); //System.out.println(m+">
             * "+getBasePairCount(majorAligned, permutedEvent2));
             *
             * countBasePairsReal += getBasePairProportion(realAligned,
             * realEvent,50); countBasePairsPermuted +=
             * getBasePairProportion(realAligned, permutedEvent2,50);
             *
             * //countBasePairsReal += getBasePairProportion(majorAligned,
             * realEvent,50); //countBasePairsPermuted +=
             * getBasePairProportion(majorAligned, permutedEvent2,50);
             *
             * //countBasePairsReal += getBasePairProportion(realAligned,
             * realEvent,50); //countBasePairsPermuted +=
             * getBasePairProportion(realAligned, permutedEvent2,50); }
             *
             * if (realDisruption >= permutedDisruption) { //if
             * (realRecombinantFold.freeEnergy >=
             * permutedRecombinantFold.freeEnergy) { countDisruption++; } if
             * (realRecombinantFold.freeEnergy >=
             * permutedRecombinantFold.freeEnergy) { countEnergy++; } if
             * (realSimilarityToParents >= permutedSimilarityToParents) {
             * countParentalSimilarity++; }
             *
             * total++; double energyPval = countEnergy / total; double
             * disruptionPval = countDisruption / total; double
             * parentalSimilarityPval = countParentalSimilarity / total;
             * //double basePairCount = System.out.println(realEvent.id + ", " +
             * realEvent.start + ", " + permutedEvent.start + ": " +
             * realRecombinantFold.freeEnergy + "\t" +
             * permutedRecombinantFold.freeEnergy + "\t" + energyPval + "\t" +
             * realDisruption + "\t" + permutedDisruption + "\t" +
             * disruptionPval + "\t" + realSimilarityToParents + "\t" +
             * permutedSimilarityToParents + "\t" + parentalSimilarityPval +
             * "\t" + (sumDisruptionReal / (sumDisruptionReal +
             * sumDisruptionPermuted)) + "\t" + countBasePairsReal + "/" +
             * (countBasePairsReal + countBasePairsPermuted) + "\t" +
             * (countBasePairsReal / (countBasePairsReal +
             * countBasePairsPermuted))); if (i % 20 == 0) { saveFoldCache(); }
             * } writer.close();
             */
        } catch (IOException ex) {

            ex.printStackTrace();
        }
    }

    public int[] shift(int[] array, int shift) {
        int[] out = new int[array.length];
        for (int i = 0; i < out.length; i++) {
            out[(i + shift) % array.length] = array[i];
        }

        return out;
    }

    public double calculateRecombinantSum(int[] s, int[] pairedSites) {
        double sum = 0;
        for (int i = 0; i < pairedSites.length; i++) {
            if (pairedSites[i] != 0) {
                sum += s[i];
            }
        }

        return sum;
    }

    public boolean isPaired(int[] minorPairedSites, int[] majorPairedSites, int position, boolean AND) {
        boolean minorPaired = minorPairedSites[position] != 0;
        boolean majorPaired = majorPairedSites[position] != 0;
        if (AND && minorPaired && majorPaired) {
            return true;
        }
        if (!AND && (minorPaired || majorPaired)) {
            return true;
        }

        return false;
    }

    public double dot(int[] s, double[] values) {
        double sum = 0;
        for (int i = 0; i < s.length; i++) {
            sum += s[i] * values[i];
        }

        return sum;
    }

    public void performTest() {
        File alignmentFile = new File("C:/dev/thesis/hiv_full/test/darren_hiv.fas");
        File rdpCSVFile = new File("C:/dev/thesis/hiv_full/test/darren_hiv.csv");
        //File alignmentFile = new File("C:/dev/thesis/porcine/300/porcine_all_300_aligned.fas");
        // File rdpCSVFile = new File("C:/dev/thesis/porcine/300/porcine.csv");
        //File alignmentFile = new File("C:/dev/thesis/hiv_full/hiv1/200/hiv1_all_200_aligned.fas");
        //File rdpCSVFile = new File("C:/dev/thesis/hiv_full/hiv1/200/recombination.csv");
        // File alignmentFile = new File("C:/dev/thesis/norovirus/200/norovirus_all_200_aligned.fas");
        // File rdpCSVFile = new File("C:/dev/thesis/norovirus/200/norovirus.csv");
        //File alignmentFile = new File("C:/dev/thesis/hcv/1/300/hcv1_all_300_aligned.fas");
        //File rdpCSVFile = new File("C:/dev/thesis/hcv/1/300/hcv.csv");
        Random random = new Random(7920171293137101310L);
        Random random2 = new Random(3811018041148014014L);
        try {
            loadFoldCache();
            ArrayList<RecombinationEvent> recombinationEvents = loadRecombinationEvents(alignmentFile, rdpCSVFile, true);
            ArrayList<RecombinationEvent> selectedRecombinationEvents = new ArrayList<>();
            for (RecombinationEvent event : recombinationEvents) {
                //if (!event.breakpointUndetermined && event.circulationCount >= 0 && event.recombinantName.matches("SN[0-9]+_.+")) {
                if (!event.breakpointUndetermined) {
                    selectedRecombinationEvents.add(event);

                    System.out.println(selectedRecombinationEvents.size() + "_" + event);
                }
                /*
                 * if (!event.breakpointUndetermined && event.circulationCount
                 * >= 0 && event.recombinantName.matches("SN[0-9]+_.+")) {
                 * selectedRecombinationEvents.add(event);
                 *
                 * System.out.println(selectedRecombinationEvents.size() + "_" +
                 * event); }
                 */
                // System.out.println(selectedRecombinationEvents.size() + "_" + event);
                if (selectedRecombinationEvents.size() >= 1000) {
                    break;
                }

            }

            BufferedWriter writer = new BufferedWriter(new FileWriter(new File("rfd.out")));

            double countDisruption = 0;
            double sumDisruptionReal = 0;
            double sumDisruptionPermuted = 0;
            double total = 0;
            double countEnergy = 0;
            double countParentalSimilarity = 0;
            double countBasePairsReal = 0;
            double countBasePairsPermuted = 0;
            for (int i = 0; i < 100000000; i++) {
                int a = random.nextInt(selectedRecombinationEvents.size());
                RecombinationEvent realEvent = selectedRecombinationEvents.get(a);
                RecombinationEvent permutedEvent = generatePermutedRecombinationEvent(random, realEvent);

                Fold minorParentFold = fold(realEvent.minorParentSequence, 8);
                Fold majorParentFold = fold(realEvent.majorParentSequence, 8);
                Fold realRecombinantFold = fold(realEvent.getRecombinant(), 8);
                Fold permutedRecombinantFold = fold(permutedEvent.getRecombinant(), 8);

                String minorAligned = StructureAlign.mapStringToAlignedSequence(RNAFoldingTools.getDotBracketStringFromPairedSites(minorParentFold.pairedSites), realEvent.minorParentSequence, "-");
                String majorAligned = StructureAlign.mapStringToAlignedSequence(RNAFoldingTools.getDotBracketStringFromPairedSites(majorParentFold.pairedSites), realEvent.majorParentSequence, "-");
                String realAligned = StructureAlign.mapStringToAlignedSequence(RNAFoldingTools.getDotBracketStringFromPairedSites(realRecombinantFold.pairedSites), realEvent.getRecombinant(), "-");
                String permutedAligned = StructureAlign.mapStringToAlignedSequence(RNAFoldingTools.getDotBracketStringFromPairedSites(permutedRecombinantFold.pairedSites), permutedEvent.getRecombinant(), "-");

                writer.write(realEvent.toString() + "\n");
                writer.write(permutedEvent.toString() + "\n");
                writer.write(realEvent.minorParentSequence + "\n");
                writer.write(realEvent.majorParentSequence + "\n");
                writer.write(realEvent.getRecombinant() + "\n");
                writer.write(permutedEvent.getRecombinant() + "\n");
                writer.write(minorAligned + "\n");
                writer.write(majorAligned + "\n");
                writer.write(realAligned + "\n");
                writer.write(permutedAligned + "\n");
                writer.flush();

                //System.out.println(realAligned);
                //System.out.println(permutedAligned);
                int realDisruption = getDisruptionScore(minorAligned, majorAligned, realAligned, true);
                int permutedDisruption = getDisruptionScore(minorAligned, majorAligned, permutedAligned, true);
                sumDisruptionReal += realDisruption;
                sumDisruptionPermuted += permutedDisruption;
                double realSimilarityToParents = averageSimilarityToParents(minorAligned, majorAligned, realAligned);
                double permutedSimilarityToParents = averageSimilarityToParents(minorAligned, majorAligned, permutedAligned);
                //double basePairCountReal = getBasePairCount(realAligned,realEvent);
                // double basePairCountPermuted = getBasePairCount(realAligned,permutedEvent);
                for (int m = 0; m < 1000; m++) {
                    RecombinationEvent permutedEvent2 = generatePermutedRecombinationEvent(random2, realEvent);

                    //System.out.println(m+"> "+getBasePairCount(minorAligned, realEvent));
                    //System.out.println(m+"> "+getBasePairCount(minorAligned, permutedEvent2));
                    //System.out.println(m+"> "+getBasePairCount(majorAligned, realEvent));
                    //System.out.println(m+"> "+getBasePairCount(majorAligned, permutedEvent2));

                    countBasePairsReal += getBasePairProportion(realAligned, realEvent, 50);
                    countBasePairsPermuted += getBasePairProportion(realAligned, permutedEvent2, 50);

                    //countBasePairsReal += getBasePairProportion(majorAligned, realEvent,50);
                    //countBasePairsPermuted += getBasePairProportion(majorAligned, permutedEvent2,50);

                    //countBasePairsReal += getBasePairProportion(realAligned, realEvent,50);
                    //countBasePairsPermuted += getBasePairProportion(realAligned, permutedEvent2,50);
                }

                if (realDisruption >= permutedDisruption) {
                    //if (realRecombinantFold.freeEnergy >= permutedRecombinantFold.freeEnergy) {
                    countDisruption++;
                }
                if (realRecombinantFold.freeEnergy >= permutedRecombinantFold.freeEnergy) {
                    countEnergy++;
                }
                if (realSimilarityToParents >= permutedSimilarityToParents) {
                    countParentalSimilarity++;
                }

                total++;
                double energyPval = countEnergy / total;
                double disruptionPval = countDisruption / total;
                double parentalSimilarityPval = countParentalSimilarity / total;
                //double basePairCount = 
                System.out.println(realEvent.id + ", " + realEvent.start + ", " + permutedEvent.start + ": " + realRecombinantFold.freeEnergy + "\t" + permutedRecombinantFold.freeEnergy + "\t" + energyPval + "\t" + realDisruption + "\t" + permutedDisruption + "\t" + disruptionPval + "\t" + realSimilarityToParents + "\t" + permutedSimilarityToParents + "\t" + parentalSimilarityPval + "\t" + (sumDisruptionReal / (sumDisruptionReal + sumDisruptionPermuted)) + "\t" + countBasePairsReal + "/" + (countBasePairsReal + countBasePairsPermuted) + "\t" + (countBasePairsReal / (countBasePairsReal + countBasePairsPermuted)));
                if (i % 20 == 0) {
                    saveFoldCache();
                }
            }
            writer.close();
        } catch (IOException ex) {

            ex.printStackTrace();
        }
    }

    public int getBasePairCount(String recombinantDotBracket, RecombinationEvent event) {
        int count = 0;
        int[] recombinantSites = RNAFoldingTools.getPairedSitesFromDotBracketString(recombinantDotBracket);

        if (recombinantSites[event.start] != 0) {
            count++;
        }
        if (recombinantSites[(event.start + event.length) % recombinantSites.length] != 0) {
            count++;
        }
        return count;
    }

    public double getSlidingWindowAverage(double[] pairingProbability, RecombinationEvent event, int windowSize) {
        double count = 0;
        double total = 0;

        for (int i = pairingProbability.length + event.start - (windowSize / 2); i < pairingProbability.length + event.start + (windowSize / 2); i++) {
            count += pairingProbability[i % pairingProbability.length];
            total++;
        }
        for (int i = pairingProbability.length + event.start + event.length - (windowSize / 2); i < pairingProbability.length + event.start + event.length + (windowSize / 2); i++) {
            count += pairingProbability[i % pairingProbability.length];
            total++;
        }
        return count / total;
    }

    public double getBasePairProportion(String recombinantDotBracket, RecombinationEvent event, int windowSize) {
        double count = 0;
        double total = 0;
        int[] recombinantSites = RNAFoldingTools.getPairedSitesFromDotBracketString(recombinantDotBracket);
        /*
         * if (recombinantSites[event.start] != 0) { count++; } if
         * (recombinantSites[(event.start + event.length) %
         * recombinantSites.length] != 0) { count++; }
         */

        for (int i = recombinantSites.length + event.start - (windowSize / 2); i < recombinantSites.length + event.start + (windowSize / 2); i++) {
            if (recombinantSites[i % recombinantSites.length] != 0) {
                count++;
            }
            //System.out.println(event.id+" 1= "+(i%recombinantSites.length)+"\t"+recombinantSites[i%recombinantSites.length]);
            total++;
        }
        for (int i = recombinantSites.length + event.start + event.length - (windowSize / 2); i < recombinantSites.length + event.start + event.length + (windowSize / 2); i++) {
            if (recombinantSites[i % recombinantSites.length] != 0) {
                count++;
            }
            //System.out.println(event.id+" 1= "+(i%recombinantSites.length)+"\t"+recombinantSites[i%recombinantSites.length]);
            total++;
        }
        return count / total;
    }

    public int getDisruptionScore(String minorParentDotBracket, String majorParentDotBracket, String recombinantDotBracket, boolean useAND) {
        int[] minorSites = RNAFoldingTools.getPairedSitesFromDotBracketString(minorParentDotBracket);
        int[] majorSites = RNAFoldingTools.getPairedSitesFromDotBracketString(majorParentDotBracket);
        int[] recombinantSites = RNAFoldingTools.getPairedSitesFromDotBracketString(recombinantDotBracket);

        int disruption = 0;
        for (int i = 0; i < minorSites.length; i++) {
            /*
             * if(minorSites[i] != 0 && recombinantSites[i] != minorSites[i]) {
             * disruption++; } if(majorSites[i] != 0 && recombinantSites[i] !=
             * majorSites[i]) { disruption++; }
             *
             */

            if (useAND) {
                if ((minorSites[i] != 0 || majorSites[i] != 0) && !(recombinantSites[i] == minorSites[i] && recombinantSites[i] == majorSites[i])) {
                    disruption++;
                }
            } else {
                if ((minorSites[i] != 0 || majorSites[i] != 0) && !(recombinantSites[i] == minorSites[i] || recombinantSites[i] == majorSites[i])) {
                    disruption++;
                }
            }
        }
        return disruption;
    }

    public double averageSimilarityToParents(String minorParentDotBracket, String majorParentDotBracket, String recombinantDotBracket) {
        int[] minorSites = RNAFoldingTools.getPairedSitesFromDotBracketString(minorParentDotBracket);
        int[] majorSites = RNAFoldingTools.getPairedSitesFromDotBracketString(majorParentDotBracket);
        int[] recombinantSites = RNAFoldingTools.getPairedSitesFromDotBracketString(recombinantDotBracket);

        double sim1 = 1 - MountainMetrics.calculateNormalizedWeightedMountainDistance(minorSites, recombinantSites);
        double sim2 = 1 - MountainMetrics.calculateNormalizedWeightedMountainDistance(majorSites, recombinantSites);

        return (sim1 + sim2) / 2;
    }

    public void example() {
        RecombinationEvent e = new RecombinationEvent();
        Random random = new Random(3018170320280222L);
        e.start = 10;
        e.length = 10;
        e.minorParentSequence = "GGGGGGGGGGGAGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGAAAAAAAAAAGGGGGGGGGGGGGGGGGGGGGGGGG";
        e.majorParentSequence = "GGGGGGGGGGGTGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG";
        e.recombinantSequence = e.getRecombinant();
        System.out.println(e.getRecombinant());
        RecombinationEvent r = generatePermutedRecombinationEvent(random, e);
        System.out.println(r.getRecombinant());
    }

    public static void main(String[] args) {
        new RecombinationFoldDisruption().performTest2();
        //new RecombinationFoldDisruption().example();

    }
}
