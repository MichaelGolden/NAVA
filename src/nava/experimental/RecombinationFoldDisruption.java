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
import nava.data.io.IO;
import nava.experimental.AutomatedFolding.Fold;
import nava.structure.MountainMetrics;
import nava.structure.StructureAlign;
import nava.utils.RNAFoldingTools;

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
            newEvent.length = newLength;
        }
        return newEvent;
    }
    HashMap<String, Fold> foldCache = new HashMap<>();

    public Fold fold(String sequence, int threads) {
        String key = sequence.replaceAll("-", "").toUpperCase();
        if (foldCache.containsKey(key)) {
            return foldCache.get(key);
        }

        Fold fold = AutomatedFolding.fold(key, threads);
        foldCache.put(key, fold);
        return fold;
    }

    public void saveFoldCache() throws IOException {
        BufferedWriter buffer = new BufferedWriter(new FileWriter("fold.cache"));
        Iterator<String> keyIterator = foldCache.keySet().iterator();
        while (keyIterator.hasNext()) {
            String key = keyIterator.next();
            Fold fold = foldCache.get(key);
            buffer.write(fold + "\n");
        }
        buffer.close();
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
                if (!event.breakpointUndetermined && event.circulationCount >= 0  && event.recombinantName.matches("SN[0-9]+_.+")) {
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
                if (selectedRecombinationEvents.size() >= 20) {
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
                    
                    countBasePairsReal += getBasePairCount(minorAligned, realEvent);
                    countBasePairsPermuted += getBasePairCount(minorAligned, permutedEvent2);

                    countBasePairsReal += getBasePairCount(majorAligned, realEvent);
                    countBasePairsPermuted += getBasePairCount(majorAligned, permutedEvent2);
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
                if (i % 3 == 0) {
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
        new RecombinationFoldDisruption().performTest();
        //new RecombinationFoldDisruption().example();

    }
}
