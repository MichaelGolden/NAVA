/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.experimental;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import nava.data.io.CsvReader;
import nava.data.io.IO;
import nava.experimental.AutomatedFolding.Fold;
import nava.experimental.CorrelatedSitesTest.PairedSitesPermutationTestResult;
import nava.ranking.MyMannWhitney;
import nava.ranking.RankingAnalyses;
import nava.ranking.StatUtils;
import nava.structure.MountainMetrics;
import nava.structure.StructureAlign;
import nava.utils.Mapping;
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
        ArrayList<String> recombinantNames = new ArrayList<>();
        ArrayList<String> minorParentNames = new ArrayList<>();
        ArrayList<String> majorParentNames = new ArrayList<>();
        String recombinantSequence;
        String minorParentSequence;
        String majorParentSequence;
        double rdpPval;
        boolean breakpointUndetermined = false;
        boolean startBreakpointUndetermined = false;
        boolean endBreakpointUndetermined = false;
        boolean bothParentsKnown = false;
        boolean majorParentKnown = false;
        boolean minorParentKnown = false;
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
            clone.recombinantNames = (ArrayList<String>) recombinantNames.clone();
            clone.majorParentNames = (ArrayList<String>) majorParentNames.clone();
            clone.minorParentNames = (ArrayList<String>) minorParentNames.clone();
            clone.recombinantSequence = recombinantSequence;
            clone.minorParentSequence = minorParentSequence;
            clone.majorParentSequence = majorParentSequence;
            clone.rdpPval = rdpPval;
            clone.breakpointUndetermined = breakpointUndetermined;
            clone.circulationCount = circulationCount;
            clone.breakpointUndetermined = breakpointUndetermined;
            clone.startBreakpointUndetermined = startBreakpointUndetermined;
            clone.endBreakpointUndetermined = endBreakpointUndetermined;
            clone.bothParentsKnown = bothParentsKnown;
            clone.majorParentKnown = majorParentKnown;
            clone.minorParentKnown = minorParentKnown;

            return clone;
        }
    }

    public class InformativeSiteCalculation {

        int[] informativeSitesRunningSum;

        public InformativeSiteCalculation(String seq1, String seq2) {
            int seq1Length = seq1.length();
            informativeSitesRunningSum = new int[seq1Length * 2];

            System.out.println(informativeSitesRunningSum);
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
                event.recombinantNames.add(split[8].trim());
                event.minorParentNames.add(split[9].trim());
                event.majorParentNames.add(split[10].trim());
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


                if (split[2].contains("*")) {
                    event.breakpointUndetermined = true;
                    event.startBreakpointUndetermined = true;
                }

                if (split[3].contains("*")) {
                    event.breakpointUndetermined = true;
                    event.endBreakpointUndetermined = true;
                }

                event.minorParentKnown = !event.minorParentName.startsWith("Unknown");
                event.majorParentKnown = !event.majorParentName.startsWith("Unknown");
                event.bothParentsKnown = event.minorParentKnown && event.majorParentKnown;


                if (onlyWhereBothParentsAreKnown && event.bothParentsKnown) {
                    events.add(event);
                } else if (!onlyWhereBothParentsAreKnown) {
                    events.add(event);
                }
            } else if (split.length > 8 && split[8].length() > 0) {
                String recombinantName = split[8].trim();
                String minorParentName = "";
                String majorParentName = "";
                if (split.length > 9) {
                    minorParentName = split[9].trim();
                    if (split.length > 10) {
                        majorParentName = split[10].trim();
                    }
                }
                if (recombinantName.length() > 0) {
                    event.recombinantNames.add(recombinantName);
                }
                if (minorParentName.length() > 0) {
                    event.minorParentNames.add(minorParentName);
                }
                if (majorParentName.length() > 0) {
                    event.majorParentNames.add(majorParentName);
                }

                event.circulationCount++;
            }
        }
        return events;
    }

    public RecombinationEvent generatePermutedRecombinationEvent(Random random, RecombinationEvent event) {
        return generatePermutedRecombinationEvent(random, event, false);
    }

    public RecombinationEvent generatePermutedRecombinationEvent3(Random random, int newRandomStart, RecombinationEvent event, boolean simplePermutation, ArrayList<String> sequenceNames, int[][] nullPositions) {
        int newStart = newRandomStart;
        RecombinationEvent newEvent = event.clone();
        newEvent.start = newStart;
        if (simplePermutation || event.minorParentSequence == null || event.majorParentSequence == null) {
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

        int length = nullPositions[0].length;
        int recombinantPos = sequenceNames.indexOf(newEvent.recombinantName);
        int majorPos = sequenceNames.indexOf(newEvent.majorParentName);
        int minorPos = sequenceNames.indexOf(newEvent.minorParentName);
        boolean cont = false;

        if (recombinantPos < 0 || nullPositions[recombinantPos][newEvent.start % length] == nullPositions[recombinantPos][(newEvent.start + newEvent.length) % length]) {
            if (majorPos < 0 || nullPositions[majorPos][newEvent.start % length] == nullPositions[majorPos][(newEvent.start + newEvent.length) % length]) {
                if (minorPos < 0 || nullPositions[minorPos][newEvent.start % length] == nullPositions[minorPos][(newEvent.start + newEvent.length) % length]) {
                    for (int m = 0; m < newEvent.recombinantNames.size(); m++) {
                        int recombinantPos2 = sequenceNames.indexOf(newEvent.recombinantNames.get(m));
                        if (recombinantPos2 >= 0) {
                            for (int k = 0; k < newEvent.length; k++) {
                                nullPositions[recombinantPos2][(newEvent.start + k) % length] = 1;
                            }
                        }
                    }
                    cont = true;
                }
            }
        }

        if (!cont) {
            newRandomStart = random.nextInt(length);
            return generatePermutedRecombinationEvent3(random, newRandomStart, event, simplePermutation, sequenceNames, nullPositions);
        } else {
            System.out.println("?><? proceeding");
            return newEvent;
        }
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

    public void saveFoldCache(File cacheFile) throws IOException {
        BufferedWriter buffer = new BufferedWriter(new FileWriter(cacheFile));
        Iterator<String> keyIterator = foldCache.keySet().iterator();
        while (keyIterator.hasNext()) {
            String key = keyIterator.next();
            Fold fold = foldCache.get(key);
            buffer.write(fold + "\n");
        }
        numberSaved = foldCache.size();
        buffer.close();
    }

    public void saveFoldCache(int atLeastNWaiting, File cacheFile) throws IOException {
        if (numberSaved == -1) {
            numberSaved = foldCache.size();
        }
        if (numberSaved == -1 || foldCache.size() - numberSaved >= atLeastNWaiting) {
            saveFoldCache(cacheFile);
        }
    }

    public void loadFoldCache(File cacheFile) {
        try {
            BufferedReader buffer = new BufferedReader(new FileReader(cacheFile));
            String textline = null;
            while ((textline = buffer.readLine()) != null) {
                Fold fold = Fold.getFoldFromString(textline);
                foldCache.put(fold.sequence, fold);
            }
            buffer.close();
        } catch (IOException ex) {
        }
    }

    public void performTest3() {

        double highConfidencePercent = 0.5;
            boolean useHighConfidencePairings = false;
        boolean useSHAPEHCSS = false;
        if(useSHAPEHCSS)
        {
            highConfidencePercent = 1.0;
        }
        
        Random random = new Random(7920171293137101310L);
        Random random2 = new Random(301201013337101310L);
   // File alignmentFile = new File("C:/dev/thesis/hiv_full/test/darren_hiv.fas");
    // File rdpCSVFile = new File("C:/dev/thesis/hiv_full/test/darren_hiv.csv");
      // File alignmentFile = new File("C:/dev/thesis/hiv_full/test/darren_hiv.fas");
     // File rdpCSVFile = new File("C:/dev/thesis/hiv_full/test/darren_hiv_auto.csv");
              // File alignmentFile = new File("C:/dev/thesis/hiv_full/test/darren_hiv.fas");
     // File rdpCSVFile = new File("C:/dev/thesis/hiv_full/test/darren_hiv_auto.csv");
        //File alignmentFile = new File("C:/dev/thesis/hiv_full/test/darren_hiv_full_aligned.fas");
       // File rdpCSVFile = new File("C:/dev/thesis/hiv_full/test/darren_hiv_full_aligned2_methods2.csv");
         
     File alignmentFile = new File("C:/dev/thesis/hiv_full/test/darren_hiv_full_aligned_muscle.fas");
     //File rdpCSVFile = new File("C:/dev/thesis/hiv_full/test/darren_hiv_muscle_aligned.csv");
     File rdpCSVFile = new File("C:/dev/thesis/hiv_full/test/darren_hiv_muscle_aligned_6methods.csv");
          
       Mapping mapping = Mapping.createMapping(alignmentFile, new File("C:/dev/NL4-3-shape-alignment.fas"), 1000, false);
       String shapeDBN = "(((.(((((((((((.(((((...((((......)))))))))))))))))))))))((((((((.(((((((.............)))).)))..)))))))).(((((((((..........(((((((..((((((((.((.(((((.......)))..)).))...))))))))......................................)))))))....((((((..(((....((((.(((((((.........)))))))...))))..)))((((((....))).)))............(((((....)))))...)))))))))))))))...................(((((..............((((.............(((((((((....................................................................)))))))))................((((((...((.....))..)))))).......)))).........((((...........))))................((..((((((((.(((((((((........))))).....))))..(((...)))........))))))))...))....................((((.((.....)).((((((...((.........))..))))))...............))))....))))).(((((...((((.((..(((((.....................(((((((((.................((((...)))).........))))))))).....((..............((.((((((..........................)))))))).(((...((((((((....)))..........((........))..)))))...)))........(((((...))))).))..)))))..)).))))...............(((((((...........................)))))..)).......(((.(((((........)))))))).(((((...(((...((((((.....))))))..)))))))).........................)))))....(((((.(((((.....((((((.((...)).......(((((....((((((.....))))))...))))).((((.......))))..............................))))))..))))).)))))............................((...((((((.(((((..((((...........(((((...)))))...)))).((((.(((.((......))...((((((((((.........)))))))...........))).(((((((((.(((.....(((.((((............)))).)))....))).))).))))))........(((.....)))..........))))))).........((((((((((.....(((....((((((..((....((((((((...(((.....)))...))))))))..))....((((((((((((....)))))))))))).....))))))........)))...))))))))))............))))).)).((((((..(((.......)))..))))))..((((...((.........))..)))).))))...))(((((((......(((.((((((....(((((((((........))).))))))............(((((..........))))).............................))))))).)).(((...))).........))))))).(((((((....................................(((((((((...............((((((((.((((((((((........(((((((..(((......))).))))))).(((.....)))....................)))))))))).)))))))).............(((....)))((((((...............)))))).............................(((((((........................)))))))......((((........)))).......((((((((((.(................))))))....))))).......))))))))).(((..(((.....)))..)))...((((((((...((((......)))).((((((((.....(((.........................)))))))))))..((((((..........((((.................))))......)))))).((........))...............))))))))..................))))))).(((((((((..(((.............)))...................(((((..((((............((((..((((((((((((((......))))))))))...)))).))))(((.(((.......))))))...............))))..))))).((((....)))).((((((......)).))))........................)))))))))..(((((((........)))))))........((.((((((........))))))))..........(((((((((((...................(((((...........(((((...........((((.............(((...........(((((..((....))..................(((((..................((((....))))........(((...)))..........))))).(((.((........))))).........................)))))((..(((...)))..)).....................)))..........(((....))).......))))...........))))).(((.......)))..............(((.............)))...................((((......))))............)))))...........(.((((((....(((((................)))))....)))))).).(((((...((....))..)))))...........))))).))))))...........((......)).((((..(((((((((((..((((((((.((....))..................(((.....)))........................))))(((((.....................((((((...((.(((..........)))..............................((((.....))))............((.((((((................)))))).))...))...))))))......................................((((((.((((.(((((((..........................................))))))).....................................)))).................(((((((((.............((((((...........((((........))))............)))))))))))))))..)))))).....)))))...))))))))))).))))...)))).((((.........((((.....)))).........................(((..(((.....)))..))).((((....................))))...........((........))...((((((((.......)).))))))...((((.......................))))......((((((................((((((......))))))..................((...((((.((((((((....................................................................((((............)))).((((........)))).........................)))))))).))))....)).(((((((..............................................................................((((.....(((((..........)))....)).....)))).((...)))))))))....)))))).))))....................(((.........((((((((((............((((......)))).(((((((((....((((((((.....((..........................(((.((((.................)))).)))...................(((.((((.......................)))).))).....................((((((.....))))))..........................(((((((...(((..........)))......(((((((((..((((((.....))))))..........................))))))))).))))))).))....))))))))...........)))))))))...((((((.......(((((....((((((.........)))))))))))........))))))..........((((.....))))....)))))))))).................................((((((.......((((....)))))))))).............((((...........)))))))...((..(((((((....((((........(((((...................)))))......)))).((.(((((..((((((((..........(((..(((((.............))))).))).((((((((((...))))........)))))).....((((((((((((((.........))))))..))))..)))).....((.(..(((....)))..).))......)))))))).....))))))).......(((((......))))).....(((........))).(((.....)))......................(((((.....................(((.((((.....)))).)))..........(((((.(((...((.(((.((.............))))).))..))))))))..................((...((((((.(.((((..........))))).))))))...)).)))))................))))))))).................................................(((.................((((((((((........((((...((((((.................(((((((...(((((........((((((...))))))...............((((((....)))))).....((((((((((((........((....)).((((((..((......)).(((.........((((((((.((((....)))).))))......))))........))).))))))....))))).)))))))..)))))..)))))))..........(((.....................(((((.........))))).....(((.((((...(((......(((.....)))........)))...)).)).))).......))).)))))).)))).((.......)).........................((............)).................................))))))))))..........................((((((.........)))))).....................))).............(((.((((((................................)))))).....(((((((.....((...(((((.............((((.((((((((...((((((..................((((....)))).......(((((.............)))))..........))))))))))))))..............((((((.(((((((((((((....................)))).)))))).))).)))).))...................((((.((....)).))))........................................................))))....))))).((...(((((((((.............................)))))))))...))...)).....))).))))...............)))........................................((((...((..(((((.........(((((.((((((.....))))))......(((((((((..((((((((((.........((((...........(((((.........))))).......(((....))).........(((..((.((((.........))))))....)))....................))))......))))))))))..........))))))).).).(((((((.......))))))).........)))))(((((((.........))))))).............(((((...........))))).))))).)).....))))........................................................(.(((((....((((((((........((((((((((((((.....(((((((.......((((((((((.(((((((((((((....(((((((((.((((((.(..(((((((((...)))).)).)))..)....((((((.....))))))...))))))..(((((........))))).........(((..(((((((...)))))))..)))...(((((((((........))))))))).....))))).))))...)))).)))).))))).))).))))))).((...))....)))))))......))).)))))))))))....))))))))..))))).).((((........))))..........((......)).((((..(((((((((..................................))))))............((((((.((..((..............(((......)))...............))..)))))))).))))))).............((((((((((((...........................((((.....))))..............(((..(((...............)))..))).((((..(((((((.............(((((((((...((((..(((..((((.((((((((..(((............((((..................(((((.(((.......))).))))).....))))............))).))).))))))).(((((.(((.......))).))))).....(((...........)))......))..)))..))))....)))))))))..............)))))))...)))).........(((((((................))).......)))).)))))))))))).......(((((.................................)))))......(((((..(((((...(((.............)))((..............))..........)))))..))))).........(((((........(.(((((((..((................))..).)))))))....(((((((........((((....(((............(((...(((((((((((................)))))))))))....)))...........))).....)))).((.((((((((((...((((.(((.........)))............(((..............)))................)))).)))))))))).))...)))))))......(((((((.....)))).))).(((.....)))))))).(((((((.((((..((((((.................(((((((((((.......))))))))))).((((((((.....)))))))).....(((......))).......))))))...((((((....(((((........((((....((((...((((..((.....)).......((((((((((.((((..(.....)..)))))))))))))).....(((((......))))).)))).)).............(((.......)))..))....)))).......)))))...)))))).))))...))))))).(((((....((((.((.....))))))....(((((......))))).((.....))......(((((.(((((((((((.(((((...((((......)))))))))))))))))))))))))))))).(((((((((...........))))).))))";
       String shapeAligned = RNAFoldingTools.getDotBracketStringFromPairedSites(StructureAlign.getMappedSites(mapping.alignedB0, mapping.alignedA0, RNAFoldingTools.getPairedSitesFromDotBracketString(shapeDBN)));
        // String shapeAligned = StructureAlign.mapStringToAlignedSequence(shapeDBN, mapping.alignedB0, "-");
      // System.out.println(mapping.alignedB0);
       //System.out.println(mapping.alignedA0);
       //System.out.println(shapeAligned);
       
         
        
        
        //File alignmentFile = new File("C:/dev/thesis/porcine/300/porcine_all_300_aligned.fas");
       // File rdpCSVFile = new File("C:/dev/thesis/porcine/300/porcine.csv");
       // File alignmentFile = new File("C:/dev/thesis/hiv_full/hiv1/200/hiv1_all_200_aligned.fas");
        //File rdpCSVFile = new File("C:/dev/thesis/hiv_full/hiv1/200/recombination.csv");
        //File alignmentFile = new File("C:/dev/thesis/hiv_full/hiv1/200/hiv1_all_200_aligned.fas");
       // File alignmentFile = new File("C:/Users/Michael/Dropbox/Thesis/Picornavirus RDP/Picornavirus RDP files/hmn_entero_b.fas");
        //File rdpCSVFile = new File("C:/Users/Michael/Dropbox/Thesis/Picornavirus RDP/Picornavirus RDP files/hmn_entero_b.csv");
        //File alignmentFile = new File("C:/Users/Michael/Dropbox/Thesis/Picornavirus RDP/Picornavirus RDP files/parechovirus.fas");
        //File rdpCSVFile = new File("C:/Users/Michael/Dropbox/Thesis/Picornavirus RDP/Picornavirus RDP files/parechovirus.csv");
       
         //File alignmentFile = new File("C:/dev/thesis/norovirus/200/norovirus.fas");
        //File rdpCSVFile = new File("C:/dev/thesis/norovirus/200/norovirus.csv");
        //File alignmentFile = new File("C:/dev/thesis/hepe/400/hepe_all_400_aligned_upper.fas");
        //  File rdpCSVFile = new File("C:/dev/thesis/hepe/400/hepe_all_400_aligned_upper.csv");
     //  File alignmentFile = new File("C:/dev/thesis/enteroa/400/enteroa_all_400_aligned_upper.fas");
       //File rdpCSVFile = new File("C:/dev/thesis/enteroa/400/enteroa_all_400_aligned_upper.csv");
       //File alignmentFile = new File("C:/dev/thesis/hcv/1/300/hcv1_all_300_aligned.fas");
       //File rdpCSVFile = new File("C:/dev/thesis/hcv/1/300/hcv.csv");
       // File alignmentFile = new File("C:/dev/thesis/enterob/250/enterob_all_250_aligned_upper.fas");
        //File rdpCSVFile = new File("C:/dev/thesis/enterob/250/enterob_all_250_aligned_upper.csv");
       // File alignmentFile = new File("C:/dev/thesis/dengue/400/all_400_aligned_curated.fas");
       //File rdpCSVFile = new File("C:/dev/thesis/dengue/400/all_400_aligned_curated.csv");
    //   File rdpCSVFile = new File("C:/dev/thesis/dengue/400/all_400_aligned_curated_manual.csv");
        //File alignmentFile = new File("C:/dev/thesis/hepe/400/hepe_all_400_aligned_upper.fas");
        // File rdpCSVFile = new File("C:/dev/thesis/hepe/400/hepe_all_400_aligned_upper.csv");
        //  File alignmentFile = new File("C:/dev/thesis/jev/300/jev_all_300_aligned_upper.fas");
        // File rdpCSVFile = new File("C:/dev/thesis/jev/300/jev_all_300_aligned_upper.csv");
        ArrayList<String> sequences = new ArrayList<>();
        ArrayList<String> sequenceNames = new ArrayList<>();

        int save_interval = 50;
        //File cacheFile = new File("fold.cache");
        File cacheFile = new File(alignmentFile.getName()+"_fold.cache");
    
        IO.loadFastaSequences(alignmentFile, sequences, sequenceNames);
        loadFoldCache(cacheFile);
        double[] count = new double[sequences.get(0).length()];
        double[] nongaps = new double[sequences.get(0).length()];
        double t = 0;
        double[] pairingProbability = new double[count.length];
        SparseBasePairMatrix pairMatrix = new SparseBasePairMatrix();
        if(useSHAPEHCSS)
        {
            pairMatrix.increment(RNAFoldingTools.getPairedSitesFromDotBracketString(shapeAligned));
        }
        
        try
        {
        BufferedWriter writer = new BufferedWriter(new FileWriter("myhahahaoutput.dbn"));
        for (int i = 0; i < sequences.size(); i++) {
            System.out.println((i + 1) + " / " + sequences.size());
            //int seqno = random2.nextInt(sequences.size());
            int seqno = i;
            // System.out.println("no=" + seqno);
            String seq = sequences.get(seqno);
            Fold f = fold(seq, 8);
            if (!f.cached) {
                try {
                    saveFoldCache(save_interval, cacheFile);
                } catch (IOException ex) {
                    Logger.getLogger(RecombinationFoldDisruption.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            String dbs = RNAFoldingTools.getDotBracketStringFromPairedSites(f.pairedSites);
            String dbs_aligned = StructureAlign.mapStringToAlignedSequence(dbs, seq, "-");
            writer.write(">"+sequenceNames.get(i)+"_seq"+"\n");
            writer.write(seq+"\n");
            writer.write(dbs_aligned+"\n");
            int[] alignedPairedSites = RNAFoldingTools.getPairedSitesFromDotBracketString(dbs_aligned);
            for (int j = 0; j < alignedPairedSites.length; j++) {
                count[j] += alignedPairedSites[j] != 0 ? 1 : 0;
                nongaps[j] += sequences.get(i).charAt(j) == '-' ? 0 : 1;
            }
            
            if(!useSHAPEHCSS)
            {
                pairMatrix.increment(alignedPairedSites);
            }
            t++;
        }
        writer.close();
        
            for (int i = 0; i < sequences.size(); i++) {
                int seqno = i;
                String seq = sequences.get(seqno);
                Fold f = fold(seq, 8);
                if (!f.cached) {
                    try {
                        saveFoldCache(save_interval, cacheFile);
                    } catch (IOException ex) {
                        Logger.getLogger(RecombinationFoldDisruption.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                String dbs = RNAFoldingTools.getDotBracketStringFromPairedSites(f.pairedSites);
                String dbs_aligned = StructureAlign.mapStringToAlignedSequence(dbs, seq, "-");
                int[] alignedPairedSites = RNAFoldingTools.getPairedSitesFromDotBracketString(dbs_aligned);
               // System.out.println("?"+i+"\t"+RNAFoldingTools.getDotBracketStringFromPairedSites(pairMatrix.getHighConfidencePairedPositions(0.25, alignedPairedSites)));
                //System.out.println("?"+i+"\t"+pairMatrix.getHighConfidencePairedPositionCount(0.1, alignedPairedSites));
                //System.out.println("?"+i+"\t"+pairMatrix.getHighConfidencePairedPositionCount(0.15, alignedPairedSites));
                //System.out.println("?"+i+"\t"+pairMatrix.getHighConfidencePairedPositionCount(0.25, alignedPairedSites));
                System.out.println("?"+i+"\t"+pairMatrix.getHighConfidencePairedPositionCount(0.0, alignedPairedSites)+"\t"+pairMatrix.getHighConfidencePairedPositionCount(highConfidencePercent, alignedPairedSites));
                t++;
            }
        }
        
        
        
        
        catch(IOException ex)
        {
            ex.printStackTrace();
        }

        for (int j = 0; j < count.length; j++) {
            pairingProbability[j] = count[j] / nongaps[j];
        }

        boolean shape = false;
        
        MappedData mappedData  = null;
        if(shape)
        {
        
            ArrayList<String> values = null;
            try {
                values = CsvReader.getColumn(new File("C:/dev/hiv-1-shape-reactivities.csv"), 2);
            } catch (IOException ex) {
                Logger.getLogger(RecombinationFoldDisruption.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println(values);
            values.remove(0);

            
            mappedData = MappedData.getMappedData(alignmentFile, new File("C:/dev/NL4-3-shape-alignment.fas"), values, false, "", 1000, false);
            pairingProbability = mappedData.values;
        }        
        double[] pairingProbabilityWindow = new double[pairingProbability.length];
        //boolean used =
        ///double [] pairingProbabilityWindow = pairingProbability;
        int window = 35;

        boolean fast = false;
        double overallCount = 0;
        double overallTotal = 0;
        for (int i = 0; i < pairingProbabilityWindow.length; i++) {
            ArrayList<Double> shapeScores = new ArrayList<>();
            double c = 0;
            for (int j = Math.max(0, i - window); j < Math.min(pairingProbabilityWindow.length, i + window); j++) {


                pairingProbabilityWindow[i] += pairingProbability[j];
                if(shape)
                {
                    // SHAPE reactivities
                    if (mappedData.used[j]) {
                        pairingProbabilityWindow[i] += mappedData.values[j];
                        shapeScores.add(mappedData.values[j]);
                        c++;
                    }
                }
                else
                {
                    c++;
                }
            }
                
         /*   pairingProbabilityWindow[i] = RankingAnalyses.getMedian(shapeScores);
            if(Double.isNaN(pairingProbabilityWindow[i]))
            {
                pairingProbabilityWindow[i] =pairingProbabilityWindow[i-1];
            }*/
           
            pairingProbabilityWindow[i] /= c;
            if(c < 10)
            { 
                /*
                if(i - 1 >= 0 && pairingProbabilityWindow[i-1] != 0)
                {
                    pairingProbabilityWindow[i]  = pairingProbabilityWindow[i-1];
                }
                else
                {
                    pairingProbabilityWindow[i] = 0.407; 
                }*/
                 pairingProbabilityWindow[i] = Double.NaN; 
            }
            //
            System.out.println("XXshape\t" + i + "\t" + c + "\t" + pairingProbabilityWindow[i]);
        }
        
        try {
            loadFoldCache(cacheFile);
            ArrayList<RecombinationEvent> recombinationEvents = loadRecombinationEvents(alignmentFile, rdpCSVFile, false);
            ArrayList<RecombinationEvent> selectedRecombinationEvents = new ArrayList<>();
            for (RecombinationEvent event : recombinationEvents) {
                // if (!event.breakpointUndetermined && event.circulationCount >= 0 && event.recombinantName.matches("SN[0-9]+_.+"))

              /*if(event.recombinantName.matches("SN[0-9]+_.+"))
                {
                    selectedRecombinationEvents.add(event);
                }*/
                selectedRecombinationEvents.add(event);
                
                /*
                 * if (!event.breakpointUndetermined && event.circulationCount
                 * >= 0) // if (!event.breakpointUndetermined &&
                 * event.circulationCount >= 1) //if
                 * (!event.breakpointUndetermined && event.circulationCount >= 0
                 * && !event.recombinantName.matches("SN[0-9]+_.+")) // if
                 * (!event.breakpointUndetermined && event.circulationCount == 0
                 * && !event.recombinantName.matches("SN[0-9]+_.+")) { //if
                 * (event.length >= 75) {
                 * selectedRecombinationEvents.add(event);
                 *
                 * System.out.println(selectedRecombinationEvents.size() + "_" +
                 * event); }
                 *
                 * }
                 */
                /*
                 * if (!event.breakpointUndetermined && event.circulationCount
                 * >= 0 && event.recombinantName.matches("SN[0-9]+_.+")) {
                 * selectedRecombinationEvents.add(event);
                 *
                 * System.out.println(selectedRecombinationEvents.size() + "_" +
                 * event); }
                 */
                 System.out.println(selectedRecombinationEvents.size() + "_" + event);
                if (selectedRecombinationEvents.size() >= 3000) {
                    break;
                }
            }

            int[] recombinationCount = new int[selectedRecombinationEvents.get(0).recombinantSequence.length()];
            for (RecombinationEvent r : selectedRecombinationEvents) {
                System.out.println(r.start + "\t" + (r.start + r.length) + "\t" + recombinationCount.length);
                recombinationCount[r.start]++;
                recombinationCount[(r.start + r.length) % recombinationCount.length]++;
            }
            try {
                int[] recombinantPairs = new int[recombinationCount.length];
                for (RecombinationEvent r : selectedRecombinationEvents) {
                    recombinantPairs[r.start] = r.start + r.length + 1;
                }
                CorrelatedSitesTest.PairedSitesPermutationTestResult result = new CorrelatedSitesTest().pairedSitesCorrelationPermutationTest(new MappedData(pairingProbability), recombinantPairs, 8);
                System.out.println("correlation " + result.r + "\t" + result.pval + "\t" + result.permutation);
            } catch (InterruptedException ex) {
                Logger.getLogger(RecombinationFoldDisruption.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ExecutionException ex) {
                Logger.getLogger(RecombinationFoldDisruption.class.getName()).log(Level.SEVERE, null, ex);
            }

            boolean doshift = false;
            int start = 0;
            int end = 0;
            int iterations = 100000;
            if (doshift) {
                start = -4000;
                end = 4000;
                iterations = 5;
                fast = true;
            }

            for (int j = start; j <= end; j++) {

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
                ArrayList<Double> realMinorSimilarityValues = new ArrayList<>();
                ArrayList<Double> permutedMinorSimilarityValues = new ArrayList<>();
                ArrayList<Double> realMajorSimilarityValues = new ArrayList<>();
                ArrayList<Double> permutedMajorSimilarityValues = new ArrayList<>();
                ArrayList<Double> realDisruptionANDValues = new ArrayList<>();
                ArrayList<Double> permutedDisruptionANDValues = new ArrayList<>();
                ArrayList<Double> realDisruptionORValues = new ArrayList<>();
                ArrayList<Double> permutedDisruptionORValues = new ArrayList<>();
                 ArrayList<Double> realDisruptionMinorValues = new ArrayList<>();
                ArrayList<Double> permutedDisruptionMinorValues = new ArrayList<>();
                ArrayList<Double> realDisruptionMajorValues = new ArrayList<>();
                ArrayList<Double> permutedDisruptionMajorValues = new ArrayList<>();
                ArrayList<Double> realDisruptionSimpleValues = new ArrayList<>();
                ArrayList<Double> permutedDisruptionSimpleValues = new ArrayList<>();
                /*ArrayList<Double> realDistanceScoresMajor = new ArrayList<>();
                ArrayList<Double> permutedDistanceScoresMajor = new ArrayList<>();
                ArrayList<Double> realDistanceScoresMinor = new ArrayList<>();
                ArrayList<Double> permutedDistanceScoresMinor = new ArrayList<>();
                ArrayList<Double> realDistanceScoresRecombinant = new ArrayList<>();
                ArrayList<Double> permutedDistanceScoresRecombinant = new ArrayList<>();*/
                ArrayList<Double> realPercentPairedMinor = new ArrayList<>();
                ArrayList<Double> permutedPercentPairedMinor = new ArrayList<>();
                ArrayList<Double> realPercentPairedMajor = new ArrayList<>();
                ArrayList<Double> permutedPercentPairedMajor= new ArrayList<>();
                long[][] observedCountsOR = new long[2][2];
                long[][] observedCountsAND = new long[2][2];
                long[][] observedCountsMinorParent = new long[2][2];
                long[][] observedCountsMajorParent = new long[2][2];
                int UNPAIRED = 0, PAIRED = 1;
                int PERMUTED = 0, REAL = 1;

                int totaliter = 0;
                double realSum = 0;
                int breakpointCount = 0;
                for (int i = 0; i < iterations; i++) {
                    double permSum = 0;
                    int[][] nullpositions = new int[sequences.size()][sequences.get(0).length()];
                    for (int h = 0; h < selectedRecombinationEvents.size(); h++) {
                        totaliter++;
                        int randomOffset = random.nextInt(pairingProbability.length);
                        RecombinationEvent realEvent = selectedRecombinationEvents.get(h).clone();
                        realEvent.start = (realEvent.start + j + pairingProbabilityWindow.length) % pairingProbabilityWindow.length;
                        // RecombinationEvent permutedEvent2 = generatePermutedRecombinationEvent(random, realEvent2, false);

                        RecombinationEvent permutedEvent = generatePermutedRecombinationEvent3(random, (realEvent.start + randomOffset) % pairingProbability.length, realEvent, false, sequenceNames, nullpositions);
                       if(!realEvent.recombinantName.matches("SN[0-9]+_.+"))
                        {
                           // continue;
                        }
                       
                        if (realEvent.breakpointUndetermined) {
                            continue;
                        }
                        breakpointCount+= 2;
                        //breakpointCount+= 2;
                       /* if((realEvent.breakpointStart >= 2248 && realEvent.breakpointStart < 2371)) // full
                        {
                            breakpointCount+= 1;
                        }
                        if((realEvent.breakpointEnd >= 2248 && realEvent.breakpointEnd < 2371)) // full
                        {
                            breakpointCount+= 1;
                        }*/
                        if((realEvent.breakpointStart >= 8872 && realEvent.breakpointStart < 9184))
                        {
                            //breakpointCount+= 1;
                        }
                        if((realEvent.breakpointEnd >= 8872 && realEvent.breakpointEnd < 9184))
                        {
                            //breakpointCount+= 1;
                        }
                        /*if((i >= 2248 && i < 2371) || (i >= 8872 && i < 9184)) // full
                        {
                            breakpointCount+= 2;
                        }*/
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
                            if (realEvent.bothParentsKnown) {
                                minorParentFold = fold(realEvent.minorParentSequence, 8);
                                majorParentFold = fold(realEvent.majorParentSequence, 8);
                                realRecombinantFold = fold(realEvent.getRecombinant(), 8);
                                permutedRecombinantFold = fold(permutedEvent.getRecombinant(), 8);

                                minorAligned = StructureAlign.mapStringToAlignedSequence(RNAFoldingTools.getDotBracketStringFromPairedSites(minorParentFold.pairedSites), realEvent.minorParentSequence, "-");
                                majorAligned = StructureAlign.mapStringToAlignedSequence(RNAFoldingTools.getDotBracketStringFromPairedSites(majorParentFold.pairedSites), realEvent.majorParentSequence, "-");
                                realAligned = StructureAlign.mapStringToAlignedSequence(RNAFoldingTools.getDotBracketStringFromPairedSites(realRecombinantFold.pairedSites), realEvent.getRecombinant(), "-");
                                permutedAligned = StructureAlign.mapStringToAlignedSequence(RNAFoldingTools.getDotBracketStringFromPairedSites(permutedRecombinantFold.pairedSites), permutedEvent.getRecombinant(), "-");

                                int gdist1 = geneticDistance(realEvent.getRecombinant(), realEvent.minorParentSequence);
                                int gdist2 = geneticDistance(permutedEvent.getRecombinant(), realEvent.minorParentSequence);
                                if(realEvent.majorParentSequence != null)
                                {
                                    int gdist3 = geneticDistance(realEvent.getRecombinant(), realEvent.majorParentSequence);
                                    int gdist4 = geneticDistance(permutedEvent.getRecombinant(), realEvent.majorParentSequence);
                                    System.out.println("GDIST="+gdist1+"\t"+gdist2+"\t"+gdist3+"\t"+gdist4);
                                }
                                else
                                {
                                    System.out.println("GDIST="+gdist1+"\t"+gdist2);
                                }
                                
                                if(useHighConfidencePairings || useSHAPEHCSS)
                                {
                                    minorAligned = RNAFoldingTools.getDotBracketStringFromPairedSites(pairMatrix.getHighConfidencePairedPositions(highConfidencePercent, RNAFoldingTools.getPairedSitesFromDotBracketString(minorAligned)));
                                    majorAligned = RNAFoldingTools.getDotBracketStringFromPairedSites(pairMatrix.getHighConfidencePairedPositions(highConfidencePercent, RNAFoldingTools.getPairedSitesFromDotBracketString(majorAligned)));
                                    realAligned = RNAFoldingTools.getDotBracketStringFromPairedSites(pairMatrix.getHighConfidencePairedPositions(highConfidencePercent, RNAFoldingTools.getPairedSitesFromDotBracketString(realAligned)));
                                    permutedAligned = RNAFoldingTools.getDotBracketStringFromPairedSites(pairMatrix.getHighConfidencePairedPositions(highConfidencePercent, RNAFoldingTools.getPairedSitesFromDotBracketString(permutedAligned)));
                                }
                                
                                minorPairedSites = RNAFoldingTools.getPairedSitesFromDotBracketString(minorAligned);
                                majorPairedSites = RNAFoldingTools.getPairedSitesFromDotBracketString(majorAligned);
                            }
                            saveFoldCache(save_interval, cacheFile);
                        }
                        // real += pairingProbabilityWindow[realEvent2.start];
                        // perm += pairingProbabilityWindow[permutedEvent2.start];

                        // real += pairingProbabilityWindow[(realEvent2.start + realEvent2.length) % pairingProbability.length];
                        //perm += pairingProbabilityWindow[(permutedEvent2.start + permutedEvent2.length) % pairingProbability.length];

                        if (i == 0) {
                            realSum += pairingProbabilityWindow[realEvent.start];
                            realSum += pairingProbabilityWindow[(realEvent.start + realEvent.length) % pairingProbability.length];
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
                                if (realEvent.bothParentsKnown) {
                                    
                                    if(minorAligned.charAt(realEvent.start) != '-' && majorAligned.charAt(realEvent.start) != '-')
                                    {
                                         observedCountsOR[REAL][isPaired(minorPairedSites, majorPairedSites, realEvent.start, false) ? 1 : 0]++;
                                         observedCountsAND[REAL][isPaired(minorPairedSites, majorPairedSites, realEvent.start, true) ? 1 : 0]++;
                                    }
                                    if(minorAligned.charAt((realEvent.start + realEvent.length) % pairingProbability.length) != '-' && majorAligned.charAt((realEvent.start + realEvent.length) % pairingProbability.length) != '-')
                                    {
                                        observedCountsOR[REAL][isPaired(minorPairedSites, majorPairedSites, (realEvent.start + realEvent.length) % pairingProbability.length, false) ? 1 : 0]++;
                                         observedCountsAND[REAL][isPaired(minorPairedSites, majorPairedSites, (realEvent.start + realEvent.length) % pairingProbability.length, true) ? 1 : 0]++;
                                    }
                                    /*
                                    observedCountsOR[REAL][isPaired(minorPairedSites, majorPairedSites, realEvent.start, false) ? 1 : 0]++;
                                    observedCountsOR[REAL][isPaired(minorPairedSites, majorPairedSites, (realEvent.start + realEvent.length) % pairingProbability.length, false) ? 1 : 0]++;
                                    observedCountsAND[REAL][isPaired(minorPairedSites, majorPairedSites, realEvent.start, true) ? 1 : 0]++;
                                    observedCountsAND[REAL][isPaired(minorPairedSites, majorPairedSites, (realEvent.start + realEvent.length) % pairingProbability.length, true) ? 1 : 0]++;
                                    */
                                    realEnergyValues.add(realRecombinantFold.freeEnergy);
                                    realSimilarityValues.add(averageSimilarityToParents(minorAligned, majorAligned, realAligned));
                                    realMinorSimilarityValues.add(similarity(minorAligned, realAligned));
                                    realMajorSimilarityValues.add(similarity(majorAligned, realAligned));
                                    realDisruptionANDValues.add((double) getDisruptionScore(minorAligned, majorAligned, realAligned, true));
                                    realDisruptionORValues.add((double) getDisruptionScore(minorAligned, majorAligned, realAligned, false));
                                    realDisruptionSimpleValues.add((double) getDisruptionScoreSimple(minorAligned, majorAligned, realAligned));
                                    realDisruptionMinorValues.add((double) getDisruptionScoreForParents(minorAligned, realAligned));
                                    realDisruptionMajorValues.add((double) getDisruptionScoreForParents(majorAligned, realAligned));
                                   
                                    
                                    /// realDistanceScoresRecombinant.add(Dijkstra.getDistance(RNAFoldingTools.getPairedSitesFromDotBracketString(minorAligned), realEvent.start, (realEvent.start + realEvent.length) % pairingProbability.length));
                                    // permutedDistanceScoresRecombinant.add(Dijkstra.getDistance(RNAFoldingTools.getPairedSitesFromDotBracketString(minorAligned), permutedEvent.start, (permutedEvent.start + permutedEvent.length) % pairingProbability.length));
                                    //  realDistanceScoresRecombinant.add(Dijkstra.getDistance(RNAFoldingTools.getPairedSitesFromDotBracketString(majorAligned), realEvent.start, (realEvent.start + realEvent.length) % pairingProbability.length));
                                    // permutedDistanceScoresRecombinant.add(Dijkstra.getDistance(RNAFoldingTools.getPairedSitesFromDotBracketString(majorAligned), permutedEvent.start, (permutedEvent.start + permutedEvent.length) % pairingProbability.length));

                                    /*realDistanceScoresMinor.add(Dijkstra.getDistance(RNAFoldingTools.getPairedSitesFromDotBracketString(minorAligned), minorAligned, realEvent.start, (realEvent.start + realEvent.length) % pairingProbability.length));
                                    realDistanceScoresMajor.add(Dijkstra.getDistance(RNAFoldingTools.getPairedSitesFromDotBracketString(majorAligned), majorAligned, realEvent.start, (realEvent.start + realEvent.length) % pairingProbability.length));
                                    realDistanceScoresRecombinant.add(Dijkstra.getDistance(RNAFoldingTools.getPairedSitesFromDotBracketString(realAligned), realAligned, realEvent.start, (realEvent.start + realEvent.length) % pairingProbability.length));
                                      */                                  
                                    realPercentPairedMinor.add(getPercentPaired(minorPairedSites, minorAligned, realEvent.start, window));
                                    realPercentPairedMinor.add(getPercentPaired(minorPairedSites, minorAligned, realEvent.start + realEvent.length, window));
                                    realPercentPairedMajor.add(getPercentPaired(majorPairedSites, majorAligned, realEvent.start, window));
                                    realPercentPairedMajor.add(getPercentPaired(majorPairedSites, majorAligned, realEvent.start + realEvent.length, window));
                                }
                            }


                        }
                    
                        permSum += pairingProbabilityWindow[permutedEvent.start];
                        permSum += pairingProbabilityWindow[(permutedEvent.start + permutedEvent.length) % pairingProbability.length];
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
                           // System.out.println(realEvent.bothParentsKnown + "\t" + h + "\t" + realEvent);
                            if (realEvent.bothParentsKnown) {
                                if(minorAligned.charAt(permutedEvent.start) != '-' && majorAligned.charAt(permutedEvent.start) != '-')
                                {
                                        observedCountsOR[PERMUTED][isPaired(minorPairedSites, majorPairedSites, permutedEvent.start, false) ? 1 : 0]++;
                                        observedCountsAND[PERMUTED][isPaired(minorPairedSites, majorPairedSites, permutedEvent.start, true) ? 1 : 0]++;
                                }
                                if(minorAligned.charAt((permutedEvent.start + permutedEvent.length) % pairingProbability.length) != '-' && majorAligned.charAt((permutedEvent.start + permutedEvent.length) % pairingProbability.length) != '-')
                                {
                                    observedCountsOR[PERMUTED][isPaired(minorPairedSites, majorPairedSites, (permutedEvent.start + permutedEvent.length) % pairingProbability.length, false) ? 1 : 0]++;
                                    observedCountsAND[PERMUTED][isPaired(minorPairedSites, majorPairedSites, (permutedEvent.start + permutedEvent.length) % pairingProbability.length, true) ? 1 : 0]++;
                                }
                                
                                 /*
                                observedCountsOR[PERMUTED][isPaired(minorPairedSites, majorPairedSites, permutedEvent.start, false) ? 1 : 0]++;
                                observedCountsOR[PERMUTED][isPaired(minorPairedSites, majorPairedSites, (permutedEvent.start + permutedEvent.length) % pairingProbability.length, false) ? 1 : 0]++;
                                observedCountsAND[PERMUTED][isPaired(minorPairedSites, majorPairedSites, permutedEvent.start, true) ? 1 : 0]++;
                                observedCountsAND[PERMUTED][isPaired(minorPairedSites, majorPairedSites, (permutedEvent.start + permutedEvent.length) % pairingProbability.length, true) ? 1 : 0]++;
                                */

                                permutedEnergyValues.add(permutedRecombinantFold.freeEnergy);
                                permutedSimilarityValues.add(averageSimilarityToParents(minorAligned, majorAligned, permutedAligned));
                                permutedMinorSimilarityValues.add(similarity(minorAligned, permutedAligned));
                                permutedMajorSimilarityValues.add(similarity(majorAligned, permutedAligned));
                                permutedDisruptionANDValues.add((double) getDisruptionScore(minorAligned, majorAligned, permutedAligned, true));
                                permutedDisruptionORValues.add((double) getDisruptionScore(minorAligned, majorAligned, permutedAligned, false));
                                permutedDisruptionSimpleValues.add((double) getDisruptionScoreSimple(minorAligned, majorAligned, permutedAligned));
                                permutedDisruptionMinorValues.add((double) getDisruptionScoreForParents(minorAligned, permutedAligned));
                                permutedDisruptionMajorValues.add((double) getDisruptionScoreForParents(majorAligned, permutedAligned));
                                   
                                // realDistanceScoresRecombinant.add(Dijkstra.getDistance(RNAFoldingTools.getPairedSitesFromDotBracketString(majorAligned), realEvent.start, (realEvent.start + realEvent.length) % pairingProbability.length));
                                /*permutedDistanceScoresMinor.add(Dijkstra.getDistance(RNAFoldingTools.getPairedSitesFromDotBracketString(minorAligned), minorAligned, permutedEvent.start, (permutedEvent.start + permutedEvent.length) % pairingProbability.length));
                                permutedDistanceScoresMajor.add(Dijkstra.getDistance(RNAFoldingTools.getPairedSitesFromDotBracketString(majorAligned), majorAligned, permutedEvent.start, (permutedEvent.start + permutedEvent.length) % pairingProbability.length));
                                permutedDistanceScoresRecombinant.add(Dijkstra.getDistance(RNAFoldingTools.getPairedSitesFromDotBracketString(permutedAligned), permutedAligned, permutedEvent.start, (permutedEvent.start + permutedEvent.length) % pairingProbability.length));*/

                                permutedPercentPairedMinor.add(getPercentPaired(minorPairedSites, minorAligned, permutedEvent.start, window));
                                permutedPercentPairedMinor.add(getPercentPaired(minorPairedSites, minorAligned, permutedEvent.start + permutedEvent.length, window));
                                permutedPercentPairedMajor.add(getPercentPaired(majorPairedSites, majorAligned, permutedEvent.start, window));
                                permutedPercentPairedMajor.add(getPercentPaired(majorPairedSites, majorAligned, permutedEvent.start + permutedEvent.length, window));
                            }


                        }

                        System.out.println("Iterations = "+i+"\t"+totaliter+"\tbreakpoints="+breakpointCount);
                        if(totaliter % 25 == 0)
                        {
                        if (!doshift) {
                                MyMannWhitney mwpairing = new MyMannWhitney(realPairingValues, permutedPairingValues);
                                MannWhitneyUTest mwpairing2 = new MannWhitneyUTest(NaNStrategy.REMOVED, TiesStrategy.RANDOM);
                                MyMannWhitney mw5primepairing = new MyMannWhitney(real5PrimePairingValues, permuted5PrimePairingValues);
                                MyMannWhitney mw3primepairing = new MyMannWhitney(real3PrimePairingValues, permuted3PrimePairingValues);
                                MyMannWhitney mw5prime3prime = new MyMannWhitney(real5PrimePairingValues, real3PrimePairingValues);
                                MyMannWhitney mw5prime3primepermuted = new MyMannWhitney(permuted5PrimePairingValues, permuted3PrimePairingValues);
                                MyMannWhitney mwenergy = new MyMannWhitney(realEnergyValues, permutedEnergyValues);
                                MyMannWhitney mwsimilarity = new MyMannWhitney(realSimilarityValues, permutedSimilarityValues);
                                MyMannWhitney mwminorsimilarity = new MyMannWhitney(realMinorSimilarityValues, permutedMinorSimilarityValues);
                                MyMannWhitney mwmajorsimilarity = new MyMannWhitney(realMajorSimilarityValues, permutedMajorSimilarityValues);
                                MyMannWhitney mwdisruptionAND = new MyMannWhitney(realDisruptionANDValues, permutedDisruptionANDValues);
                                MyMannWhitney mwdisruptionOR = new MyMannWhitney(realDisruptionORValues, permutedDisruptionORValues);
                                MyMannWhitney mwdisruptionSimple = new MyMannWhitney(realDisruptionSimpleValues, permutedDisruptionSimpleValues);
                                MyMannWhitney mwdisruptionMinorParent = new MyMannWhitney(realDisruptionMinorValues, permutedDisruptionMinorValues);
                                MyMannWhitney mwdisruptionMajorParent = new MyMannWhitney(realDisruptionMajorValues, permutedDisruptionMajorValues);
                                /*MyMannWhitney mwdistanceminor = new MyMannWhitney(realDistanceScoresMinor, permutedDistanceScoresMinor);
                                MyMannWhitney mwdistancemajor = new MyMannWhitney(realDistanceScoresMajor, permutedDistanceScoresMajor);
                                MyMannWhitney mwdistancerecombinant = new MyMannWhitney(realDistanceScoresRecombinant, permutedDistanceScoresRecombinant);*/
                                MyMannWhitney mwpercentPairedMinor = new MyMannWhitney(realPercentPairedMinor, permutedPercentPairedMinor);
                                MyMannWhitney mwpercentPairedMajor = new MyMannWhitney(realPercentPairedMajor, permutedPercentPairedMajor);
                                System.out.println("pairing\t" + j + "\t" + RankingAnalyses.getMedian(realPairingValues) + "\t" + RankingAnalyses.getMedian(permutedPairingValues) + "\t" + mwpairing.getZ() + "\t" + realPairingValues.size() + "\t" + permutedPairingValues.size());
    //                            double pairing2pval = mwpairing2.mannWhitneyUTest(RankingAnalyses.getArray(realPairingValues), RankingAnalyses.getArray(permutedPairingValues));
                                //System.out.println("pairing2\t"+pairing2pval+"\t"+StatUtils.getInvCDF(pairing2pval, true)+"\t"+StatUtils.getInvCDF(pairing2pval/2, true));
                            System.out.println("pairing 5'\t" + j + "\t" + RankingAnalyses.getMedian(real5PrimePairingValues) + "\t" + RankingAnalyses.getMedian(permuted5PrimePairingValues) + "\t" + mw5primepairing.getZ() + "\t" + real5PrimePairingValues.size() + "\t" + permuted5PrimePairingValues.size());
                                System.out.println("pairing 3'\t" + j + "\t" + RankingAnalyses.getMedian(real3PrimePairingValues) + "\t" + RankingAnalyses.getMedian(permuted3PrimePairingValues) + "\t" + mw3primepairing.getZ() + "\t" + real3PrimePairingValues.size() + "\t" + permuted3PrimePairingValues.size());
                                System.out.println("5' vs. 3' (real)\t" + j + "\t" + RankingAnalyses.getMedian(real5PrimePairingValues) + "\t" + RankingAnalyses.getMedian(real3PrimePairingValues) + "\t" + mw5prime3prime.getZ());
                                System.out.println("5' vs. 3' (permuted)\t" + j + "\t" + RankingAnalyses.getMedian(permuted5PrimePairingValues) + "\t" + RankingAnalyses.getMedian(permuted3PrimePairingValues) + "\t" + mw5prime3primepermuted.getZ());
                                if (!fast) {
                                    System.out.println("energy\t" + j + "\t" + RankingAnalyses.getMedian(realEnergyValues) + "\t" + RankingAnalyses.getMedian(permutedEnergyValues) + "\t" + mwenergy.getZ() + "\t" + realEnergyValues.size() + "\t" + permutedEnergyValues.size());
                                    System.out.println("similarity\t" + j + "\t" + RankingAnalyses.getMedian(realSimilarityValues) + "\t" + RankingAnalyses.getMedian(permutedSimilarityValues) + "\t" + mwsimilarity.getZ() + "\t" + realSimilarityValues.size() + "\t" + permutedSimilarityValues.size());
                                    System.out.println("similarity (minor)\t" + j + "\t" + RankingAnalyses.getMedian(realMinorSimilarityValues) + "\t" + RankingAnalyses.getMedian(permutedMinorSimilarityValues) + "\t" + mwminorsimilarity.getZ());
                                    System.out.println("similarity (major)\t" + j + "\t" + RankingAnalyses.getMedian(realMajorSimilarityValues) + "\t" + RankingAnalyses.getMedian(permutedMajorSimilarityValues) + "\t" + mwmajorsimilarity.getZ());
                                    System.out.println("disruption (AND)\t" + j + "\t" + RankingAnalyses.getMedian(realDisruptionANDValues) + "\t" + RankingAnalyses.getMedian(permutedDisruptionANDValues) + "\t" + mwdisruptionAND.getZ() + "\t" + realDisruptionANDValues.size() + "\t" + permutedDisruptionANDValues.size());
                                    System.out.println("disruption (OR)\t" + j + "\t" + RankingAnalyses.getMedian(realDisruptionORValues) + "\t" + RankingAnalyses.getMedian(permutedDisruptionORValues) + "\t" + mwdisruptionOR.getZ() + "\t" + realDisruptionORValues.size() + "\t" + permutedDisruptionORValues.size());
                                    System.out.println("disruption (simple)\t" + j + "\t" + RankingAnalyses.getMedian(realDisruptionSimpleValues) + "\t" + RankingAnalyses.getMedian(permutedDisruptionSimpleValues) + "\t" + mwdisruptionSimple.getZ());
                                    System.out.println("disruption (minor)\t" + j + "\t" + RankingAnalyses.getMedian(realDisruptionMinorValues) + "\t" + RankingAnalyses.getMedian(permutedDisruptionMinorValues) + "\t" + mwdisruptionMinorParent.getZ() + "\t" + realDisruptionMinorValues.size() + "\t" + permutedDisruptionMinorValues.size());
                                    System.out.println("disruption (major)\t" + j + "\t" + RankingAnalyses.getMedian(realDisruptionMajorValues) + "\t" + RankingAnalyses.getMedian(permutedDisruptionMajorValues) + "\t" + mwdisruptionMajorParent.getZ());
                                    /*System.out.println("distance (minor)\t" + j + "\t" + RankingAnalyses.getMedian(realDistanceScoresMinor) + "\t" + RankingAnalyses.getMedian(permutedDistanceScoresMinor) + "\t" + mwdistanceminor.getZ());
                                    System.out.println("distance (major)\t" + j + "\t" + RankingAnalyses.getMedian(realDistanceScoresMajor) + "\t" + RankingAnalyses.getMedian(permutedDistanceScoresMajor) + "\t" + mwdistancemajor.getZ());
                                    System.out.println("distance (recombinant)\t" + j + "\t" + RankingAnalyses.getMedian(realDistanceScoresRecombinant) + "\t" + RankingAnalyses.getMedian(permutedDistanceScoresRecombinant) + "\t" + mwdistancerecombinant.getZ());*/
                                     System.out.println("paired (minor)\t" + j + "\t" + RankingAnalyses.getMedian(realPercentPairedMinor) + "\t" + RankingAnalyses.getMedian(permutedPercentPairedMinor) + "\t" + mwpercentPairedMinor.getZ());
                                    System.out.println("paired (major)\t" + j + "\t" + RankingAnalyses.getMedian(realPercentPairedMajor) + "\t" + RankingAnalyses.getMedian(permutedPercentPairedMajor) + "\t" + mwpercentPairedMajor.getZ());
                                    ChiSquareTest chiPairingTest = new ChiSquareTest();
                                    for (int recombinant = 0; recombinant < observedCountsOR.length; recombinant++) {
                                        for (int ispaired = 0; ispaired < observedCountsOR[0].length; ispaired++) {
                                            System.out.print(observedCountsOR[recombinant][ispaired] + "\t");
                                        }
                                        System.out.println();
                                    }                                    
                                    double [][] expectedOR = SubstructureCoevolution.getExpectedValues(observedCountsOR);
                                    String [] colLabels = {"Permuted", "Real"};
                                    String [] rowLabels = {"Unpaired", "Paired"};
                                    SubstructureCoevolution.printTable(observedCountsOR, colLabels, rowLabels);
                                    SubstructureCoevolution.printTable(expectedOR, colLabels, rowLabels);
                                    
                                    System.out.println("p-value (OR) = " + chiPairingTest.chiSquareTest(observedCountsOR));
                                    for (int recombinant = 0; recombinant < observedCountsAND.length; recombinant++) {
                                        for (int ispaired = 0; ispaired < observedCountsAND[0].length; ispaired++) {
                                            System.out.print(observedCountsAND[recombinant][ispaired] + "\t");
                                        }
                                        System.out.println();
                                    }
                                    System.out.println("p-value (AND) = " + chiPairingTest.chiSquareTest(observedCountsAND));
                                    double [][] expectedAND= SubstructureCoevolution.getExpectedValues(observedCountsAND);                       
                                    SubstructureCoevolution.printTable(observedCountsAND, colLabels, rowLabels);
                                    SubstructureCoevolution.printTable(expectedAND, colLabels, rowLabels);
                                }
                                System.out.println();
                        }

                    }
                    }

                    if (realSum > permSum) {
                        overallCount++;
                    }
                    overallTotal++;
                    
                    //System.out.println(realSum+"\t"+permSum+" --> "+(overallCount) + "\t" + overallTotal + "\t" + (overallCount / overallTotal));
                          
                }

                // print offset info here
                double medianPairingReal = RankingAnalyses.getMedian(realPairingValues);
                double medianPairingPerm = RankingAnalyses.getMedian(permutedPairingValues);
                double zscorePairing = new MyMannWhitney(realPairingValues, permutedPairingValues).getZ();
                double medianPairing5PrimeReal = RankingAnalyses.getMedian(real5PrimePairingValues);
                double medianPairing5PrimePerm = RankingAnalyses.getMedian(permuted5PrimePairingValues);
                double zscorePairing5Prime = new MyMannWhitney(real5PrimePairingValues, permuted5PrimePairingValues).getZ();
                double medianPairing3PrimeReal = RankingAnalyses.getMedian(real3PrimePairingValues);
                double medianPairing3PrimePerm = RankingAnalyses.getMedian(permuted3PrimePairingValues);
                double zscorePairing3Prime = new MyMannWhitney(real3PrimePairingValues, permuted3PrimePairingValues).getZ();
                double zscore5Prime3PrimeReal = new MyMannWhitney(real5PrimePairingValues, real3PrimePairingValues).getZ();
                double zscore5Prime3PrimePerm = new MyMannWhitney(permuted5PrimePairingValues, permuted3PrimePairingValues).getZ();
                System.out.println(j + "\t" + medianPairingReal + "\t" + medianPairingPerm + "\t" + zscorePairing + "\t" + medianPairing5PrimeReal + "\t" + medianPairing5PrimePerm + "\t" + zscorePairing5Prime + "\t" + medianPairing3PrimeReal + "\t" + medianPairing3PrimePerm + "\t" + zscorePairing3Prime + "\t" + zscore5Prime3PrimeReal + "\t" + zscore5Prime3PrimePerm);


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

    public void performTest2() {



        Random random = new Random(7920171293137101310L);
        Random random2 = new Random(301201013337101310L);
        File alignmentFile = new File("C:/dev/thesis/hiv_full/test/darren_hiv.fas");
        File rdpCSVFile = new File("C:/dev/thesis/hiv_full/test/darren_hiv.csv");
         File cacheFile = new File(alignmentFile.getName()+"_fold.cache");
        //File alignmentFile = new File("C:/dev/thesis/porcine/300/porcine_all_300_aligned.fas");
        // File rdpCSVFile = new File("C:/dev/thesis/porcine/300/porcine.csv");
        //  File alignmentFile = new File("C:/dev/thesis/hiv_full/hiv1/200/hiv1_all_200_aligned.fas");
        //  File rdpCSVFile = new File("C:/dev/thesis/hiv_full/hiv1/200/recombination.csv");
        // File alignmentFile = new File("C:/dev/thesis/norovirus/200/norovirus.fas");
        //File rdpCSVFile = new File("C:/dev/thesis/norovirus/200/norovirus.csv");
        //File alignmentFile = new File("C:/dev/thesis/hepe/400/hepe_all_400_aligned_upper.fas");
        //  File rdpCSVFile = new File("C:/dev/thesis/hepe/400/hepe_all_400_aligned_upper.csv");
        //File alignmentFile = new File("C:/dev/thesis/enteroa/400/enteroa_all_400_aligned_upper.fas");
        // File rdpCSVFile = new File("C:/dev/thesis/enteroa/400/enteroa_all_400_aligned_upper.csv");
        //File alignmentFile = new File("C:/dev/thesis/hcv/1/300/hcv1_all_300_aligned.fas");
        //File rdpCSVFile = new File("C:/dev/thesis/hcv/1/300/hcv.csv");
        //ile alignmentFile = new File("C:/dev/thesis/enterob/250/enterob_all_250_aligned_upper.fas");
        // File rdpCSVFile = new File("C:/dev/thesis/enterob/250/enterob_all_250_aligned_upper.csv");
        //   File alignmentFile = new File("C:/dev/thesis/dengue/400/all_400_aligned_curated.fas");
        // File rdpCSVFile = new File("C:/dev/thesis/dengue/400/all_400_aligned_curated.csv");
//     File rdpCSVFile = new File("C:/dev/thesis/dengue/400/all_400_aligned_curated_manual.csv");
        //   File alignmentFile = new File("C:/dev/thesis/hepe/400/hepe_all_400_aligned_upper.fas");
        // File rdpCSVFile = new File("C:/dev/thesis/hepe/400/all_400_aligned_curated.csv");
        //  File alignmentFile = new File("C:/dev/thesis/jev/300/jev_all_300_aligned_upper.fas");
        // File rdpCSVFile = new File("C:/dev/thesis/jev/300/jev_all_300_aligned_upper.csv");
        ArrayList<String> sequences = new ArrayList<>();
        ArrayList<String> sequenceNames = new ArrayList<>();

        IO.loadFastaSequences(alignmentFile, sequences, sequenceNames);
        loadFoldCache(cacheFile);
        double[] count = new double[sequences.get(0).length()];
        double[] nongaps = new double[sequences.get(0).length()];
        double t = 0;
        double[] pairingProbability = new double[count.length];
        for (int i = 0; i < sequences.size(); i++) {
            System.out.println((i + 1) + " / " + sequences.size());
            //int seqno = random2.nextInt(sequences.size());
            int seqno = i;
            // System.out.println("no=" + seqno);
            String seq = sequences.get(seqno);
            Fold f = fold(seq, 8);
            if (!f.cached) {
                try {
                    saveFoldCache(10, cacheFile);
                } catch (IOException ex) {
                    Logger.getLogger(RecombinationFoldDisruption.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            String dbs = RNAFoldingTools.getDotBracketStringFromPairedSites(f.pairedSites);
            String dbs_aligned = StructureAlign.mapStringToAlignedSequence(dbs, seq, "-");
            int[] pairedSites = RNAFoldingTools.getPairedSitesFromDotBracketString(dbs_aligned);
            for (int j = 0; j < pairedSites.length; j++) {
                count[j] += pairedSites[j] != 0 ? 1 : 0;
                nongaps[j] += sequences.get(i).charAt(j) == '-' ? 0 : 1;
            }
            t++;
        }

        for (int j = 0; j < count.length; j++) {
            pairingProbability[j] = count[j] / nongaps[j];
        }

        ArrayList<String> values = null;
        try {
            values = CsvReader.getColumn(new File("C:/dev/hiv-1-shape-reactivities.csv"), 2);
        } catch (IOException ex) {
            Logger.getLogger(RecombinationFoldDisruption.class.getName()).log(Level.SEVERE, null, ex);
        }
        values.remove(0);

        MappedData mappedData = MappedData.getMappedData(alignmentFile, new File("C:/dev/NL4-3-shape-alignment.fas"), values, false, "", 1000, false);
        pairingProbability = mappedData.values;
        double[] pairingProbabilityWindow = new double[pairingProbability.length];
        //boolean used =
        //double [] pairingProbabilityWindow = pairingProbability;
        int window = 37;

        boolean fast = false;
        for (int i = 0; i < pairingProbabilityWindow.length; i++) {
            ArrayList<Double> shapeScores = new ArrayList<>();
            double c = 0;
            for (int j = Math.max(0, i - window); j < Math.min(pairingProbabilityWindow.length, i + window); j++) {


                pairingProbabilityWindow[i] += pairingProbability[j];
                c++;
                // SHAPE reactivities
                if (mappedData.used[j]) {
                    pairingProbabilityWindow[i] += mappedData.values[j];
                    shapeScores.add(mappedData.values[j]);
                    c++;
                }
            }
            pairingProbabilityWindow[i] = RankingAnalyses.getMedian(shapeScores);
            /*
             *
             * if(c < 3) { pairingProbabilityWindow[i] = 0.407; }
             */
            //pairingProbabilityWindow[i] /= c;
            System.out.println("shape\t" + i + "\t" + c + "\t" + pairingProbabilityWindow[i]);
        }
        try {
            loadFoldCache(cacheFile);
            ArrayList<RecombinationEvent> recombinationEvents = loadRecombinationEvents(alignmentFile, rdpCSVFile, false);
            ArrayList<RecombinationEvent> selectedRecombinationEvents = new ArrayList<>();
            for (RecombinationEvent event : recombinationEvents) {
                // if (!event.breakpointUndetermined && event.circulationCount >= 0 && event.recombinantName.matches("SN[0-9]+_.+"))
                if (!event.breakpointUndetermined && event.circulationCount >= 0) // if (!event.breakpointUndetermined && event.circulationCount >= 1) //if (!event.breakpointUndetermined && event.circulationCount >= 0 && !event.recombinantName.matches("SN[0-9]+_.+"))
                // if (!event.breakpointUndetermined && event.circulationCount == 0 && !event.recombinantName.matches("SN[0-9]+_.+"))
                {
                    //if (event.length >= 75) 
                    {
                        selectedRecombinationEvents.add(event);

                        System.out.println(selectedRecombinationEvents.size() + "_" + event);
                    }

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
            try {
                int[] recombinantPairs = new int[recombinationCount.length];
                for (RecombinationEvent r : selectedRecombinationEvents) {
                    recombinantPairs[r.start] = r.start + r.length + 1;
                }
                CorrelatedSitesTest.PairedSitesPermutationTestResult result = new CorrelatedSitesTest().pairedSitesCorrelationPermutationTest(new MappedData(pairingProbability), recombinantPairs, 8);
                System.out.println("correlation " + result.r + "\t" + result.pval + "\t" + result.permutation);
            } catch (InterruptedException ex) {
                Logger.getLogger(RecombinationFoldDisruption.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ExecutionException ex) {
                Logger.getLogger(RecombinationFoldDisruption.class.getName()).log(Level.SEVERE, null, ex);
            }

            boolean doshift = false;
            int start = 0;
            int end = 0;
            int iterations = 100000;
            if (doshift) {
                start = -4000;
                end = 4000;
                iterations = 5;
                fast = true;
            }

            for (int j = start; j <= end; j++) {

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
                ArrayList<Double> realMinorSimilarityValues = new ArrayList<>();
                ArrayList<Double> permutedMinorSimilarityValues = new ArrayList<>();
                ArrayList<Double> realMajorSimilarityValues = new ArrayList<>();
                ArrayList<Double> permutedMajorSimilarityValues = new ArrayList<>();
                ArrayList<Double> realDisruptionANDValues = new ArrayList<>();
                ArrayList<Double> permutedDisruptionANDValues = new ArrayList<>();
                ArrayList<Double> realDisruptionORValues = new ArrayList<>();
                ArrayList<Double> permutedDisruptionORValues = new ArrayList<>();
                ArrayList<Double> realDisruptionSimpleValues = new ArrayList<>();
                ArrayList<Double> permutedDisruptionSimpleValues = new ArrayList<>();
                ArrayList<Double> realDistanceScoresMajor = new ArrayList<>();
                ArrayList<Double> permutedDistanceScoresMajor = new ArrayList<>();
                ArrayList<Double> realDistanceScoresMinor = new ArrayList<>();
                ArrayList<Double> permutedDistanceScoresMinor = new ArrayList<>();
                ArrayList<Double> realDistanceScoresRecombinant = new ArrayList<>();
                ArrayList<Double> permutedDistanceScoresRecombinant = new ArrayList<>();
                // long[][] observedCountsOR = new long[2][2];
                //long[][] observedCountsAND = new long[2][2];
                // int UNPAIRED = 0, PAIRED = 1;
                // int PERMUTED = 0, REAL = 1;

                int breakpointCount = 0;
                for (int i = 0; i < iterations; i++) {
                    for (int h = 0; h < selectedRecombinationEvents.size(); h++) {
                        int randomOffset = random.nextInt(pairingProbability.length);
                        RecombinationEvent realEvent = selectedRecombinationEvents.get(h).clone();
                        realEvent.start = (realEvent.start + j + pairingProbabilityWindow.length) % pairingProbabilityWindow.length;
                        // RecombinationEvent permutedEvent2 = generatePermutedRecombinationEvent(random, realEvent2, false);

                        RecombinationEvent permutedEvent = generatePermutedRecombinationEvent(random, (realEvent.start + randomOffset) % pairingProbability.length, realEvent, true);

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
                            if (realEvent.bothParentsKnown) {
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
                            }
                            saveFoldCache(10, cacheFile);
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
                                if (realEvent.bothParentsKnown) {
                                    //observedCountsOR[REAL][isPaired(minorPairedSites, majorPairedSites, realEvent.start, false) ? 1 : 0]++;
                                    //observedCountsOR[REAL][isPaired(minorPairedSites, majorPairedSites, (realEvent.start + realEvent.length) % pairingProbability.length, false) ? 1 : 0]++;
                                    //observedCountsAND[REAL][isPaired(minorPairedSites, majorPairedSites, realEvent.start, true) ? 1 : 0]++;
                                    //observedCountsAND[REAL][isPaired(minorPairedSites, majorPairedSites, (realEvent.start + realEvent.length) % pairingProbability.length, true) ? 1 : 0]++;

                                    realEnergyValues.add(realRecombinantFold.freeEnergy);
                                    realSimilarityValues.add(averageSimilarityToParents(minorAligned, majorAligned, realAligned));
                                    realMinorSimilarityValues.add(similarity(minorAligned, realAligned));
                                    realMajorSimilarityValues.add(similarity(majorAligned, realAligned));
                                    realDisruptionANDValues.add((double) getDisruptionScore(minorAligned, majorAligned, realAligned, true));
                                    realDisruptionORValues.add((double) getDisruptionScore(minorAligned, majorAligned, realAligned, false));
                                    realDisruptionSimpleValues.add((double) getDisruptionScoreSimple(minorAligned, majorAligned, realAligned));

                                    /// realDistanceScoresRecombinant.add(Dijkstra.getDistance(RNAFoldingTools.getPairedSitesFromDotBracketString(minorAligned), realEvent.start, (realEvent.start + realEvent.length) % pairingProbability.length));
                                    // permutedDistanceScoresRecombinant.add(Dijkstra.getDistance(RNAFoldingTools.getPairedSitesFromDotBracketString(minorAligned), permutedEvent.start, (permutedEvent.start + permutedEvent.length) % pairingProbability.length));
                                    //  realDistanceScoresRecombinant.add(Dijkstra.getDistance(RNAFoldingTools.getPairedSitesFromDotBracketString(majorAligned), realEvent.start, (realEvent.start + realEvent.length) % pairingProbability.length));
                                    // permutedDistanceScoresRecombinant.add(Dijkstra.getDistance(RNAFoldingTools.getPairedSitesFromDotBracketString(majorAligned), permutedEvent.start, (permutedEvent.start + permutedEvent.length) % pairingProbability.length));

                                    realDistanceScoresMinor.add(Dijkstra.getDistance(RNAFoldingTools.getPairedSitesFromDotBracketString(minorAligned), realEvent.start, (realEvent.start + realEvent.length) % pairingProbability.length));
                                    realDistanceScoresMajor.add(Dijkstra.getDistance(RNAFoldingTools.getPairedSitesFromDotBracketString(majorAligned), realEvent.start, (realEvent.start + realEvent.length) % pairingProbability.length));
                                    realDistanceScoresRecombinant.add(Dijkstra.getDistance(RNAFoldingTools.getPairedSitesFromDotBracketString(realAligned), realEvent.start, (realEvent.start + realEvent.length) % pairingProbability.length));

                                }
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
                            System.out.println(realEvent.bothParentsKnown + "\t" + h + "\t" + realEvent);
                            if (realEvent.bothParentsKnown) {
                                //observedCountsOR[PERMUTED][isPaired(minorPairedSites, majorPairedSites, permutedEvent.start, false) ? 1 : 0]++;
                                //observedCountsOR[PERMUTED][isPaired(minorPairedSites, majorPairedSites, (permutedEvent.start + permutedEvent.length) % pairingProbability.length, false) ? 1 : 0]++;
                                //observedCountsAND[PERMUTED][isPaired(minorPairedSites, majorPairedSites, permutedEvent.start, true) ? 1 : 0]++;
                                //observedCountsAND[PERMUTED][isPaired(minorPairedSites, majorPairedSites, (permutedEvent.start + permutedEvent.length) % pairingProbability.length, true) ? 1 : 0]++;


                                permutedEnergyValues.add(permutedRecombinantFold.freeEnergy);
                                permutedSimilarityValues.add(averageSimilarityToParents(minorAligned, majorAligned, permutedAligned));
                                permutedMinorSimilarityValues.add(similarity(minorAligned, permutedAligned));
                                permutedMajorSimilarityValues.add(similarity(majorAligned, permutedAligned));
                                permutedDisruptionANDValues.add((double) getDisruptionScore(minorAligned, majorAligned, permutedAligned, true));
                                permutedDisruptionORValues.add((double) getDisruptionScore(minorAligned, majorAligned, permutedAligned, false));
                                permutedDisruptionSimpleValues.add((double) getDisruptionScoreSimple(minorAligned, majorAligned, permutedAligned));

                                // realDistanceScoresRecombinant.add(Dijkstra.getDistance(RNAFoldingTools.getPairedSitesFromDotBracketString(majorAligned), realEvent.start, (realEvent.start + realEvent.length) % pairingProbability.length));
                                permutedDistanceScoresMinor.add(Dijkstra.getDistance(RNAFoldingTools.getPairedSitesFromDotBracketString(minorAligned), permutedEvent.start, (permutedEvent.start + permutedEvent.length) % pairingProbability.length));
                                permutedDistanceScoresMajor.add(Dijkstra.getDistance(RNAFoldingTools.getPairedSitesFromDotBracketString(majorAligned), permutedEvent.start, (permutedEvent.start + permutedEvent.length) % pairingProbability.length));
                                permutedDistanceScoresRecombinant.add(Dijkstra.getDistance(RNAFoldingTools.getPairedSitesFromDotBracketString(realAligned), permutedEvent.start, (permutedEvent.start + permutedEvent.length) % pairingProbability.length));

                            }


                        }

                        if (!doshift) {
                            MyMannWhitney mwpairing = new MyMannWhitney(realPairingValues, permutedPairingValues);
                            MannWhitneyUTest mwpairing2 = new MannWhitneyUTest(NaNStrategy.REMOVED, TiesStrategy.RANDOM);
                            MyMannWhitney mw5primepairing = new MyMannWhitney(real5PrimePairingValues, permuted5PrimePairingValues);
                            MyMannWhitney mw3primepairing = new MyMannWhitney(real3PrimePairingValues, permuted3PrimePairingValues);
                            MyMannWhitney mw5prime3prime = new MyMannWhitney(real5PrimePairingValues, real3PrimePairingValues);
                            MyMannWhitney mw5prime3primepermuted = new MyMannWhitney(permuted5PrimePairingValues, permuted3PrimePairingValues);
                            MyMannWhitney mwenergy = new MyMannWhitney(realEnergyValues, permutedEnergyValues);
                            MyMannWhitney mwsimilarity = new MyMannWhitney(realSimilarityValues, permutedSimilarityValues);
                            MyMannWhitney mwminorsimilarity = new MyMannWhitney(realMinorSimilarityValues, permutedMinorSimilarityValues);
                            MyMannWhitney mwmajorsimilarity = new MyMannWhitney(realMajorSimilarityValues, permutedMajorSimilarityValues);
                            MyMannWhitney mwdisruptionAND = new MyMannWhitney(realDisruptionANDValues, permutedDisruptionANDValues);
                            MyMannWhitney mwdisruptionOR = new MyMannWhitney(realDisruptionORValues, permutedDisruptionORValues);
                            MyMannWhitney mwdisruptionSimple = new MyMannWhitney(realDisruptionSimpleValues, permutedDisruptionSimpleValues);
                            MyMannWhitney mwdistanceminor = new MyMannWhitney(realDistanceScoresMinor, permutedDistanceScoresMinor);
                            MyMannWhitney mwdistancemajor = new MyMannWhitney(realDistanceScoresMajor, permutedDistanceScoresMajor);
                            MyMannWhitney mwdistancerecombinant = new MyMannWhitney(realDistanceScoresRecombinant, permutedDistanceScoresRecombinant);
                            System.out.println("pairing\t" + j + "\t" + RankingAnalyses.getMedian(realPairingValues) + "\t" + RankingAnalyses.getMedian(permutedPairingValues) + "\t" + mwpairing.getZ() + "\t" + realPairingValues.size() + "\t" + permutedPairingValues.size());
//                            double pairing2pval = mwpairing2.mannWhitneyUTest(RankingAnalyses.getArray(realPairingValues), RankingAnalyses.getArray(permutedPairingValues));
                            //System.out.println("pairing2\t"+pairing2pval+"\t"+StatUtils.getInvCDF(pairing2pval, true)+"\t"+StatUtils.getInvCDF(pairing2pval/2, true));
                            System.out.println("pairing 5'\t" + j + "\t" + RankingAnalyses.getMedian(real5PrimePairingValues) + "\t" + RankingAnalyses.getMedian(permuted5PrimePairingValues) + "\t" + mw5primepairing.getZ() + "\t" + real5PrimePairingValues.size() + "\t" + permuted5PrimePairingValues.size());
                            System.out.println("pairing 3'\t" + j + "\t" + RankingAnalyses.getMedian(real3PrimePairingValues) + "\t" + RankingAnalyses.getMedian(permuted3PrimePairingValues) + "\t" + mw3primepairing.getZ() + "\t" + real3PrimePairingValues.size() + "\t" + permuted3PrimePairingValues.size());
                            System.out.println("5' vs. 3' (real)\t" + j + "\t" + RankingAnalyses.getMedian(real5PrimePairingValues) + "\t" + RankingAnalyses.getMedian(real3PrimePairingValues) + "\t" + mw5prime3prime.getZ());
                            System.out.println("5' vs. 3' (permuted)\t" + j + "\t" + RankingAnalyses.getMedian(permuted5PrimePairingValues) + "\t" + RankingAnalyses.getMedian(permuted3PrimePairingValues) + "\t" + mw5prime3primepermuted.getZ());
                            if (!fast) {
                                System.out.println("energy\t" + j + "\t" + RankingAnalyses.getMedian(realEnergyValues) + "\t" + RankingAnalyses.getMedian(permutedEnergyValues) + "\t" + mwenergy.getZ() + "\t" + realEnergyValues.size() + "\t" + permutedEnergyValues.size());
                                System.out.println("similarity\t" + j + "\t" + RankingAnalyses.getMedian(realSimilarityValues) + "\t" + RankingAnalyses.getMedian(permutedSimilarityValues) + "\t" + mwsimilarity.getZ() + "\t" + realSimilarityValues.size() + "\t" + permutedSimilarityValues.size());
                                System.out.println("similarity (minor)\t" + j + "\t" + RankingAnalyses.getMedian(realMinorSimilarityValues) + "\t" + RankingAnalyses.getMedian(permutedMinorSimilarityValues) + "\t" + mwminorsimilarity.getZ());
                                System.out.println("similarity (major)\t" + j + "\t" + RankingAnalyses.getMedian(realMajorSimilarityValues) + "\t" + RankingAnalyses.getMedian(permutedMajorSimilarityValues) + "\t" + mwmajorsimilarity.getZ());
                                System.out.println("disruption (AND)\t" + j + "\t" + RankingAnalyses.getMedian(realDisruptionANDValues) + "\t" + RankingAnalyses.getMedian(permutedDisruptionANDValues) + "\t" + mwdisruptionAND.getZ() + "\t" + realDisruptionANDValues.size() + "\t" + permutedDisruptionANDValues.size());
                                System.out.println("disruption (OR)\t" + j + "\t" + RankingAnalyses.getMedian(realDisruptionORValues) + "\t" + RankingAnalyses.getMedian(permutedDisruptionORValues) + "\t" + mwdisruptionOR.getZ() + "\t" + realDisruptionORValues.size() + "\t" + permutedDisruptionORValues.size());
                                System.out.println("disruption (simple)\t" + j + "\t" + RankingAnalyses.getMedian(realDisruptionSimpleValues) + "\t" + RankingAnalyses.getMedian(permutedDisruptionSimpleValues) + "\t" + mwdisruptionSimple.getZ());
                                System.out.println("distance (minor)\t" + j + "\t" + RankingAnalyses.getMedian(realDistanceScoresMinor) + "\t" + RankingAnalyses.getMedian(permutedDistanceScoresMinor) + "\t" + mwdistanceminor.getZ());
                                System.out.println("distance (major)\t" + j + "\t" + RankingAnalyses.getMedian(realDistanceScoresMajor) + "\t" + RankingAnalyses.getMedian(permutedDistanceScoresMajor) + "\t" + mwdistancemajor.getZ());
                                System.out.println("distance (recombinant)\t" + j + "\t" + RankingAnalyses.getMedian(realDistanceScoresRecombinant) + "\t" + RankingAnalyses.getMedian(permutedDistanceScoresRecombinant) + "\t" + mwdistancerecombinant.getZ());
                                /*
                                 * ChiSquareTest chiPairingTest = new
                                 * ChiSquareTest(); for (int recombinant = 0;
                                 * recombinant < observedCountsOR.length;
                                 * recombinant++) { for (int ispaired = 0;
                                 * ispaired < observedCountsOR[0].length;
                                 * ispaired++) {
                                 * System.out.print(observedCountsOR[recombinant][ispaired]
                                 * + "\t"); } System.out.println(); }
                                 * System.out.println("p-value (OR) = " +
                                 * chiPairingTest.chiSquareTest(observedCountsOR));
                                 * for (int recombinant = 0; recombinant <
                                 * observedCountsAND.length; recombinant++) {
                                 * for (int ispaired = 0; ispaired <
                                 * observedCountsAND[0].length; ispaired++) {
                                 * System.out.print(observedCountsAND[recombinant][ispaired]
                                 * + "\t"); } System.out.println(); }
                                 * System.out.println("p-value (AND) = " +
                                 * chiPairingTest.chiSquareTest(observedCountsAND));
                            }
                                 */
                                System.out.println();
                            }

                        }
                    }

                    // print offset info here
                    double medianPairingReal = RankingAnalyses.getAverage(realPairingValues);
                    double medianPairingPerm = RankingAnalyses.getAverage(permutedPairingValues);
                    double zscorePairing = new MyMannWhitney(realPairingValues, permutedPairingValues).getZ();
                    double medianPairing5PrimeReal = RankingAnalyses.getMedian(real5PrimePairingValues);
                    double medianPairing5PrimePerm = RankingAnalyses.getMedian(permuted5PrimePairingValues);
                    double zscorePairing5Prime = new MyMannWhitney(real5PrimePairingValues, permuted5PrimePairingValues).getZ();
                    double medianPairing3PrimeReal = RankingAnalyses.getMedian(real3PrimePairingValues);
                    double medianPairing3PrimePerm = RankingAnalyses.getMedian(permuted3PrimePairingValues);
                    double zscorePairing3Prime = new MyMannWhitney(real3PrimePairingValues, permuted3PrimePairingValues).getZ();
                    double zscore5Prime3PrimeReal = new MyMannWhitney(real5PrimePairingValues, real3PrimePairingValues).getZ();
                    double zscore5Prime3PrimePerm = new MyMannWhitney(permuted5PrimePairingValues, permuted3PrimePairingValues).getZ();
                    System.out.println(j + "\t" + medianPairingReal + "\t" + medianPairingPerm + "\t" + zscorePairing + "\t" + medianPairing5PrimeReal + "\t" + medianPairing5PrimePerm + "\t" + zscorePairing5Prime + "\t" + medianPairing3PrimeReal + "\t" + medianPairing3PrimePerm + "\t" + zscorePairing3Prime + "\t" + zscore5Prime3PrimeReal + "\t" + zscore5Prime3PrimePerm);


                }



                /*
                 * BufferedWriter writer = new BufferedWriter(new FileWriter(new
                 * File("rfd.out")));
                 *
                 * double countDisruption = 0; double sumDisruptionReal = 0;
                 * double sumDisruptionPermuted = 0; double total = 0; double
                 * countEnergy = 0; double countParentalSimilarity = 0; double
                 * countBasePairsReal = 0; double countBasePairsPermuted = 0;
                 * for (int i = 0; i < 100000000; i++) { int a =
                 * random.nextInt(selectedRecombinationEvents.size());
                 * RecombinationEvent realEvent =
                 * selectedRecombinationEvents.get(a); RecombinationEvent
                 * permutedEvent = generatePermutedRecombinationEvent(random,
                 * realEvent);
                 *
                 * Fold minorParentFold = fold(realEvent.minorParentSequence,
                 * 8); Fold majorParentFold =
                 * fold(realEvent.majorParentSequence, 8); Fold
                 * realRecombinantFold = fold(realEvent.getRecombinant(), 8);
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
                 * writer.write(minorAligned + "\n"); writer.write(majorAligned
                 * + "\n"); writer.write(realAligned + "\n");
                 * writer.write(permutedAligned + "\n"); writer.flush();
                 *
                 * //System.out.println(realAligned);
                 * //System.out.println(permutedAligned); int realDisruption =
                 * getDisruptionScore(minorAligned, majorAligned, realAligned,
                 * true); int permutedDisruption =
                 * getDisruptionScore(minorAligned, majorAligned,
                 * permutedAligned, true); sumDisruptionReal += realDisruption;
                 * sumDisruptionPermuted += permutedDisruption; double
                 * realSimilarityToParents =
                 * averageSimilarityToParents(minorAligned, majorAligned,
                 * realAligned); double permutedSimilarityToParents =
                 * averageSimilarityToParents(minorAligned, majorAligned,
                 * permutedAligned); //double basePairCountReal =
                 * getBasePairCount(realAligned,realEvent); // double
                 * basePairCountPermuted =
                 * getBasePairCount(realAligned,permutedEvent); for (int m = 0;
                 * m < 1000; m++) { RecombinationEvent permutedEvent2 =
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
                 * //double basePairCount = System.out.println(realEvent.id + ",
                 * " + realEvent.start + ", " + permutedEvent.start + ": " +
                 * realRecombinantFold.freeEnergy + "\t" +
                 * permutedRecombinantFold.freeEnergy + "\t" + energyPval + "\t"
                 * + realDisruption + "\t" + permutedDisruption + "\t" +
                 * disruptionPval + "\t" + realSimilarityToParents + "\t" +
                 * permutedSimilarityToParents + "\t" + parentalSimilarityPval +
                 * "\t" + (sumDisruptionReal / (sumDisruptionReal +
                 * sumDisruptionPermuted)) + "\t" + countBasePairsReal + "/" +
                 * (countBasePairsReal + countBasePairsPermuted) + "\t" +
                 * (countBasePairsReal / (countBasePairsReal +
                 * countBasePairsPermuted))); if (i % 20 == 0) {
                 * saveFoldCache(); } } writer.close();
                 */
            }
            }  catch (IOException ex) {

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
    
    public double getPercentPaired(int [] pairedSites, String sequence, int position, int windowSize) 
    {
        double count = 0;
        double total = 0;
        double total2 = 0;
        for(int i = Math.max(0, position - windowSize) ; i < Math.min(position + windowSize, pairedSites.length) ; i++)
        {
            if(sequence.charAt(i) != '-')
            {
                if(pairedSites[i] != 0)
                {
                    count++;
                }
                total++;
            }
            total2++;
        }
        if(total/total2 < 0.8)
        {
            return Double.NaN;
        }
        return count/total;        
    }

    public boolean isPaired(int[] minorPairedSites, int[] majorPairedSites, int position, boolean AND) {
        /*boolean minorPaired = minorPairedSites[position] != 0;
        boolean majorPaired = majorPairedSites[position] != 0;
        if (AND && minorPaired && majorPaired) {
            return true;
        }
        if (!AND && (minorPaired || majorPaired)) {
            return true;
        }

        return false;*/
        return minorPairedSites[position] != 0;
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
            loadFoldCache(cacheFile);
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
                    saveFoldCache(cacheFile);
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
            //if((i >= 2248 && i < 2371) || (i >= 8872 && i < 9184)) // full
            //if((i >= 1311 && i < 1485) || (i >= 7540 && i < 7851)) // coding
              //if((i >= 1311 && i < 1485)) // coding
            //if((i >= 7540 && i < 7851)) // coding
              // if((i >= 8872 && i < 9184))
            //if((i >= 2248 && i < 2371) )
            {
            if(1==1)
            {

                if (useAND) {
                    if ((minorSites[i] != 0 || majorSites[i] != 0) && !(recombinantSites[i] == minorSites[i] && recombinantSites[i] == majorSites[i])) {
                        disruption++;
                    }
                } else {
                    if ((minorSites[i] != 0 || majorSites[i] != 0) && !(recombinantSites[i] == minorSites[i] || recombinantSites[i] == majorSites[i])) {
                        disruption++;
                    }
                }
                /*if (useAND) {
                    if ((minorSites[i] != 0) && !(recombinantSites[i] == minorSites[i])) {
                        disruption++;
                    }
                } else {
                    if ((majorSites[i] != 0) && !(recombinantSites[i] == majorSites[i])) {
                        disruption++;
                    }
                }*/
          
           }
            }
        
            
            
        /*
        int[] minorSites = RNAFoldingTools.getPairedSitesFromDotBracketString(minorParentDotBracket);
        //int[] majorSites = RNAFoldingTools.getPairedSitesFromDotBracketString(majorParentDotBracket);
        int[] recombinantSites = RNAFoldingTools.getPairedSitesFromDotBracketString(recombinantDotBracket);

        int disruption = 0;
        for (int i = 0; i < minorSites.length; i++) {
            if(minorSites[i] != 0 && recombinantSites[i] != minorSites[i])
            {
                disruption++;
            }
        }*/
        }
        return disruption;
  
    }
        
        public int getDisruptionScoreForParents(String parentDotBracket, String recombinantDotBracket) {
        
        int[] parentSites = RNAFoldingTools.getPairedSitesFromDotBracketString(parentDotBracket);
        int[] recombinantSites = RNAFoldingTools.getPairedSitesFromDotBracketString(recombinantDotBracket);

        int disruption = 0;
        for (int i = 0; i < parentSites.length; i++) {
            // if(/*(i >= 2248 && i < 2371) ||*/ (i >= 8872 && i < 9184))
             //if((i >= 1311 && i < 1485)) // coding
            //if((i >= 7540 && i < 7851)) // coding
               //if((i >= 8872 && i < 9184))
            //if((i >= 2248 && i < 2371) )
            //if((i >= 2248 && i < 2371) || (i >= 8872 && i < 9184)) // full
                 //if((i >= 1311 && i < 1485) || (i >= 7540 && i < 7851)) // coding
            {
            if ((parentSites[i] != 0) && !(recombinantSites[i] == parentSites[i])) {
                disruption++;
            }
            }
        }
        return disruption;
    }

    public int getDisruptionScoreSimple(String minorParentDotBracket, String majorParentDotBracket, String recombinantDotBracket) {
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
           //if((i >= 8872 && i < 9184))
            //if((i >= 2248 && i < 2371) )
            //if((i >= 2248 && i < 2371) || (i >= 8872 && i < 9184)) // full
              //if((i >= 1311 && i < 1485)) // coding
           // if((i >= 7540 && i < 7851)) // coding
            //if((i >= 1311 && i < 1485) || (i >= 7540 && i < 7851)) // coding
            //if(1==1)
            {
                if (minorSites[i] != recombinantSites[i]) {
                    disruption++;
                }

                if (majorSites[i] != recombinantSites[i]) {
                    disruption++;
                }
            }
        }
        return disruption;
    }

    public double similarity(String parentDotBracket, String recombinantDotBracket) {
        return 1 - MountainMetrics.calculateNormalizedWeightedMountainDistance(RNAFoldingTools.getPairedSitesFromDotBracketString(parentDotBracket), RNAFoldingTools.getPairedSitesFromDotBracketString(recombinantDotBracket));
    }

    public double averageSimilarityToParents(String minorParentDotBracket, String majorParentDotBracket, String recombinantDotBracket) {
        //int[] minorSites = RNAFoldingTools.getPairedSitesFromDotBracketString(minorParentDotBracket);
        //int[] majorSites = RNAFoldingTools.getPairedSitesFromDotBracketString(majorParentDotBracket);
        //int[] recombinantSites = RNAFoldingTools.getPairedSitesFromDotBracketString(recombinantDotBracket);
         
        
        int[] minorSites = StructureAlign.getSubstructureRelaxed(RNAFoldingTools.getPairedSitesFromDotBracketString(minorParentDotBracket), 8872, 9184-8872);
        int[] majorSites = StructureAlign.getSubstructureRelaxed(RNAFoldingTools.getPairedSitesFromDotBracketString(majorParentDotBracket), 8872, 9184-8872);
        int[] recombinantSites = StructureAlign.getSubstructureRelaxed(RNAFoldingTools.getPairedSitesFromDotBracketString(recombinantDotBracket), 8872, 9184-8872);

        
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
        new RecombinationFoldDisruption().performTest3();
        //new RecombinationFoldDisruption().example();

    }
    
    public static int geneticDistance(String a, String b)
    {
        int n = 0;
        for(int i = 0 ; i < a.length() ; i++)
        {
            if(a.charAt(i) != b.charAt(i))
            {
                n++;
            }
        }
        return n;
    }
   
}
