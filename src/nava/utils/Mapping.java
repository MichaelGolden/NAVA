package nava.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import nava.data.io.IO;
import nava.tasks.ProcessReference;

/**
 *
 * @author Michael Golden
 */
public class Mapping implements Serializable {

    public static String MUSCLE_EXECUTABLE = "muscle3.8.31_i86win32.exe";
    public static String MAFFT_EXECUTABLE = "bin/mafft-6.952-win64/mafft-win/mafft.bat";
    public static String MAFFT_PROFILE_EXECUTABLE = "bin/mafft-6.952-win64/mafft-win/ms/lib/mafft/mafft-profile.exe";
    private static final long serialVersionUID = 1L;

    public static void setMuscleExecutable(String muscleExecutable) {
        Mapping.MUSCLE_EXECUTABLE = muscleExecutable;
    }
    private static final char GAP_CHARACTER = 'N';
    public static boolean impute = false;
    public static boolean imputeRandomN = true;
    public static double gapOpen = -500;
    public static double gapExtend = -1;
    public static int select = 1;
    public String alignedA0;
    public String alignedB0;
    public File mappingFile;
    public boolean bReversedComplemented = false;
    int[] aToRefList = {};
    int[] bToRefList = {};
    int[] refToAList = {};
    int[] refToBList = {};
    int refLen;

    public String mapString() {
        String ret = "";
        for (int i = 0; i < aToRefList.length; i++) {
            ret += i + "\t" + aToRefList[i] + "\t" + bToRefList[i] + "\t" + refToAList[i] + "\t" + refToBList[i] + "\n";
        }
        return ret;
    }

    public static void setAlignmentParameters(double gapOpen, double gapExtend, int s) {
        Mapping.gapOpen = gapOpen;
        Mapping.gapExtend = gapExtend;
        Mapping.select = s;
    }

    private Mapping() {
    }

    public int aToRef(int i) {
        if (i >= 0 && i < aToRefList.length) {
            return aToRefList[i];
        }
        return -1;
    }

    public int bToRef(int i) {
        if (i >= 0 && i < bToRefList.length) {
            return bToRefList[i];
        }
        return -1;
    }

    public int refToA(int i) {
        if (i >= 0 && i < refToAList.length) {
            return refToAList[i];
        }
        return -1;
    }

    public int refToB(int i) {
        if (i >= 0 && i < refToBList.length) {
            return refToBList[i];
        }
        return -1;
    }

    public int aToB(int i) {
        return refToB(aToRef(i));
    }

    public int aToBNearest(int i) {
        int l = 0;
        while (i - l >= 0 && aToB(i - l) == -1) {
            l++;
        }

        int u = 0;
        while (i + u < getALength() && aToB(i + u) == -1) {
            u++;
        }

        if (l < u) {
            return aToB(i - l);
        } else {
            return aToB(i + u);
        }
    }

    public int aToBNearestLowerBound(int i) {
        int l = 0;
        while (i - l >= 0 && aToB(i - l) == -1) {
            l++;
        }

        return aToB(i - l);
    }

    public int aToBNearestUpperBound(int i) {
        int u = 0;
        while (i + u < getALength() && aToB(i + u) == -1) {
            u++;
        }

        return aToB(i + u);
    }

    public int bToA(int i) {
        return refToA(bToRef(i));
    }

    public int bToANearest(int i) {
        int l = 0;
        while (i - l >= 0 && bToA(i - l) == -1) {
            l++;
        }

        int u = 0;
        while (i + u < getBLength() && bToA(i + u) == -1) {
            u++;
        }

        int pos = Math.max(bToA(i - l), bToA(i + u));
        if (l < u && bToA(i - l) != -1) {
            return bToA(i - l);
        } else if (l > u && bToA(i + u) != -1) {
            return bToA(i + u);
        } else {
            return pos;
        }
    }

    public int getALength() {
        return getUngappedPosition(alignedA0, alignedA0.length());
    }

    public int getBLength() {
        return getUngappedPosition(alignedB0, alignedB0.length());
    }

    public int getRefLength() {
        return refLen;
    }

    public String toString() {
        String ret = "";
        ret += ">ReverseComplement=" + Boolean.toString(bReversedComplemented) + "\n";
        ret += alignedA0 + "\n";
        ret += alignedB0 + "\n";
        return ret;
    }

    public static int getUngappedPosition(String sequence, int gappedPos) {
        int ungappedPos = -1;
        int i = 0;
        int end = Math.min(sequence.length() - 1, gappedPos);
        for (i = 0; i <= end; i++) {
            if (sequence.charAt(i) != '-') {
                ungappedPos++;
            }
        }
        if (i == gappedPos) {
            return (ungappedPos + 1);
        }
        return ungappedPos;
    }

    public static String reverseComplement(String s) {
        StringBuilder sb = new StringBuilder(s);
        String rev = sb.reverse().toString();
        rev = rev.replaceAll("A", "1").replaceAll("C", "2").replaceAll("G", "3").replaceAll("T", "4");
        rev = rev.replaceAll("1", "T").replaceAll("2", "G").replaceAll("3", "C").replaceAll("4", "A");
        return rev;
    }

    public static Mapping createMappingWithRestrictionsAutoDirection(File alignmentA, File alignmentB, boolean useMUSCLE, int select, int aStart, int aEnd, int bStart, int bEnd) {
        ArrayList<String> sequencesA = new ArrayList<>();
        ArrayList<String> sequencesNamesA = new ArrayList<>();
        ArrayList<String> sequencesB = new ArrayList<>();
        ArrayList<String> sequencesNamesB = new ArrayList<>();
        IO.loadFastaSequences(alignmentA, sequencesA, sequencesNamesA);
        IO.loadFastaSequences(alignmentB, sequencesB, sequencesNamesB);

        ArrayList<String> sequencesAmod = new ArrayList<>();
        ArrayList<String> sequencesBmod = new ArrayList<>();

        for (int i = 0; i < sequencesA.size(); i++) {
            String seqi = sequencesA.get(i);
            int s = Math.min(seqi.length() - 1, Math.max(aStart, 0));
            int e = Math.min(Math.max(s, (aEnd == -1 ? seqi.length() : aEnd)), seqi.length());
            sequencesAmod.add(seqi.substring(s, e));
        }
        for (int i = 0; i < sequencesB.size(); i++) {
            String seqi = sequencesB.get(i);
            int s = Math.min(seqi.length() - 1, Math.max(bStart, 0));
            int e = Math.min(Math.max(s, (bEnd == -1 ? seqi.length() : bEnd)), seqi.length());
            sequencesBmod.add(seqi.substring(s, e));
        }
        File restrictedA = new File("a.fas");
        File restrictedB = new File("b.fas");
        IO.saveToFASTAfile(sequencesAmod, sequencesNamesA, restrictedA);
        IO.saveToFASTAfile(sequencesBmod, sequencesNamesB, restrictedB);

        Mapping mapping = Mapping.createMapping(restrictedA, restrictedB, select, useMUSCLE);

        int s = Math.min(mapping.alignedA0.length() - 1, Math.max(aStart, 0));
        int e = Math.max(s, (aEnd == -1 ? mapping.alignedA0.length() : aEnd));
        mapping.alignedA0 = nChar('-', s) + mapping.alignedA0 + nChar('-', e - s);

        s = Math.min(mapping.alignedB0.length() - 1, Math.max(bStart, 0));
        e = Math.max(s, (bEnd == -1 ? mapping.alignedB0.length() : bEnd));
        mapping.alignedB0 = nChar('-', s) + mapping.alignedB0 + nChar('-', e - s);

        return mapping;
    }

    public static String nChar(char c, int n) {
        String s = "";
        for (int i = 0; i < n; i++) {
            s += c;
        }
        return s;
    }

    public static Mapping createMappingWithRestrictions(File alignmentA, File alignmentB, int select, boolean reverseComplementB, String outputfileName, boolean useMUSCLE, int aStart, int aEnd, int bStart, int bEnd) {
        ArrayList<String> sequencesA = new ArrayList<>();
        ArrayList<String> sequencesNamesA = new ArrayList<>();
        ArrayList<String> sequencesB = new ArrayList<>();
        ArrayList<String> sequencesNamesB = new ArrayList<>();
        IO.loadFastaSequences(alignmentA, sequencesA, sequencesNamesA);
        IO.loadFastaSequences(alignmentB, sequencesB, sequencesNamesB);

        for (int i = 0; i < sequencesA.size(); i++) {
            String seqi = sequencesA.get(i);
            int s = Math.min(seqi.length() - 1, aStart);
            int e = Math.max(s, aEnd);
            sequencesA.set(i, seqi.substring(s, e));
        }
        for (int i = 0; i < sequencesB.size(); i++) {
            String seqi = sequencesB.get(i);
            int s = Math.min(seqi.length() - 1, bStart);
            int e = Math.max(s, bEnd);
            sequencesB.set(i, seqi.substring(s, e));
        }
        File restrictedA = new File("a.fas");
        File restrictedB = new File("b.fas");
        IO.saveToFASTAfile(sequencesA, sequencesNamesA, restrictedA);
        IO.saveToFASTAfile(sequencesB, sequencesNamesB, restrictedB);

        return Mapping.createMapping(restrictedA, restrictedB, select, reverseComplementB, outputfileName, useMUSCLE);
    }

    public static Mapping createMapping(File alignmentA, File alignmentB, int select, boolean reverseComplementB, String outputfileName, boolean useMUSCLE) {

        ArrayList<String> sequencesA = new ArrayList<>();
        ArrayList<String> sequencesNamesA = new ArrayList<>();
        ArrayList<String> sequencesB = new ArrayList<>();
        ArrayList<String> sequencesNamesB = new ArrayList<>();

        int maxSequencesToLoad = Math.max(Mapping.select, 100); // to ensure fast loading limit the number of sequences to be load
        IO.loadFastaSequences(alignmentA, sequencesA, sequencesNamesA, maxSequencesToLoad);
        IO.loadFastaSequences(alignmentB, sequencesB, sequencesNamesB, maxSequencesToLoad);
        return createMapping(sequencesA, sequencesNamesA, sequencesB, sequencesNamesB, select, reverseComplementB, outputfileName, useMUSCLE, new ProcessReference());
    }

    public static Mapping createMapping(ArrayList<String> sequencesA, ArrayList<String> sequencesNamesA, ArrayList<String> sequencesB, ArrayList<String> sequencesNamesB, int select, boolean reverseComplementB, String outputfileName, boolean useMUSCLE, ProcessReference processReference) {
        Mapping mapping = null;

        File tempDir = Utils.createTempDirectory();
        File inputFile1 = Utils.getFile(tempDir, "alignment1.fasta");
        File inputFile2 = Utils.getFile(tempDir, "alignment2.fasta");
        impute = false;
        imputeRandomN = false;
        File outputFile = Utils.getFile(tempDir, outputfileName);
        try {
            BufferedWriter writer1 = new BufferedWriter(new FileWriter(inputFile1));
            BufferedWriter writer2 = new BufferedWriter(new FileWriter(inputFile2));
            if (impute) {
                AlignmentUtils.impute(sequencesA, Mapping.select);
                AlignmentUtils.impute(sequencesB, Mapping.select);
            } else if (imputeRandomN) {
                Random random = new Random(-8813712038722450337L);
                int N = 50;

                // choose the first s sequences and make a random selection from the rest
                while (sequencesA.size() > N && sequencesA.size() > Mapping.select) {
                    int removeIndex = Mapping.select + random.nextInt(sequencesA.size() - Mapping.select);
                    sequencesA.remove(removeIndex);
                    sequencesNamesA.remove(removeIndex);
                }
                // choose the first s sequences and make a random selection from the rest
                while (sequencesB.size() > N && sequencesB.size() > Mapping.select) {
                    int removeIndex = Mapping.select + random.nextInt(sequencesB.size() - Mapping.select);
                    sequencesB.remove(removeIndex);
                    sequencesNamesB.remove(removeIndex);
                }

                AlignmentUtils.impute(sequencesA, Mapping.select);
                AlignmentUtils.impute(sequencesB, Mapping.select);
            }
            
            int alignmentAlength = 0;
            int alignmentBlength = 0;
            for(String seq : sequencesA)
            {
                alignmentAlength = Math.max(seq.length(), alignmentAlength);
            }
             for(String seq : sequencesB)
            {
                alignmentBlength = Math.max(seq.length(), alignmentBlength);
            }

            for (int i = 0; i < select && i < sequencesA.size(); i++) {
                writer1.write(">a" + i);
                writer1.newLine();
                writer1.write(Utils.padStringLeft(sequencesA.get(i), alignmentAlength, '-').replaceAll("-", GAP_CHARACTER + ""));
                writer1.newLine();
                //aLen = sequencesA.get(i).length();
            }
            writer1.close();
            for (int i = 0; i < select && i < sequencesB.size(); i++) {
                writer2.write(">b" + i);
                writer2.newLine();
                if (reverseComplementB) {
                    writer2.write(reverseComplement(Utils.padStringLeft(sequencesB.get(i), alignmentBlength, '-')).replaceAll("-", GAP_CHARACTER + ""));
                } else {
                    writer2.write(Utils.padStringLeft(sequencesB.get(i), alignmentBlength, '-').replaceAll("-", GAP_CHARACTER + ""));
                }
                writer2.newLine();
                //bLen = sequencesB.get(i).length();
            }
            writer2.close();
            if (Mapping.select >= 1) {
                //String cmd = MUSCLE_EXECUTABLE + " -in " + inputFile.getAbsolutePath() + " -out " + outputFile.getAbsolutePath() + " -gapopen " + gapOpen + " -gapextend " + gapExtend;


                String cmd = "cmd /c " + new File(MAFFT_PROFILE_EXECUTABLE).getAbsolutePath() + " \"" + inputFile1.getAbsolutePath() + "\" \"" + inputFile2.getAbsolutePath() + "\" > \"" + outputFile.getAbsolutePath() + "\"";
                //String cmd = new File(MAFFT_EXECUTABLE).getAbsolutePath() + " --maxiterate 1000 --seed \"" + inputFile1.getAbsolutePath()+"\" --seed \""+inputFile2.getAbsolutePath() + "\" NUL > \"" + outputFile.getAbsolutePath()+"\"";                
                //mafft --maxiterate 1000 --seed group1 --seed group2 /dev/null [> output]
                //String cmd = new File(MAFFT_PROFILE_EXECUTABLE).getAbsolutePath() + " " + inputFile1.getAbsolutePath()+" "+inputFile2.getAbsolutePath() + " > \"" + outputFile.getAbsolutePath()+"\"";                
                if (useMUSCLE) {
                    //cmd = MUSCLE_EXECUTABLE + " -profile -in1 \"" + inputFile1.getAbsolutePath() + "\" -in2 \""+inputFile2.getAbsolutePath()+"\" -out \"" + outputFile.getAbsolutePath() + "\" -gapopen " + gapOpen + " -gapextend " + gapExtend;
                    cmd = MUSCLE_EXECUTABLE + " -profile -in1 \"" + inputFile1.getAbsolutePath() + "\" -in2 \"" + inputFile2.getAbsolutePath() + "\" -out \"" + outputFile.getAbsolutePath() + "\" -gapopen " + -100.0 + " -gapextend " + -1.0;
                    //cmd = MUSCLE_EXECUTABLE + " -in " + inputFile.getAbsolutePath() + " -out " + outputFile.getAbsolutePath() + " -gapopen " + gapOpen + " -gapextend " + gapExtend;
                }
                System.out.println(cmd);


                Process p = Runtime.getRuntime().exec(cmd);
                processReference.addProcess(p);
                if (!processReference.cancelled) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
                    String textline = null;
                    while ((textline = reader.readLine()) != null) {
                        System.err.println(textline);
                    }
                    reader.close();
                    int exitCode = p.waitFor();

                    if (exitCode == 0) {
                        ArrayList<String> alignedSequences = new ArrayList<String>();
                        ArrayList<String> alignedSequenceNames = new ArrayList<String>();
                        IO.loadFastaSequences(outputFile, alignedSequences, alignedSequenceNames);


                        String alignedA0 = null;
                        String alignedB0 = null;
                        for (int i = 0; i < alignedSequences.size(); i++) {
                            if (alignedSequenceNames.get(i).startsWith("a")) {
                                alignedA0 = alignedSequences.get(i);
                            }

                            if (alignedSequenceNames.get(i).startsWith("b")) {
                                alignedB0 = alignedSequences.get(i);
                            }
                        }

                        mapping = getMappingFromAlignedStrings(alignedA0, alignedB0, reverseComplementB);
                    } else if (!useMUSCLE) {
                        return createMapping(sequencesA, sequencesNamesA, sequencesB, sequencesNamesB, select, reverseComplementB, outputfileName, !useMUSCLE, processReference);
                    }
                } else {
                    p.destroy();
                }
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return mapping;
    }

    public static Mapping createMapping2(ArrayList<String> sequencesA, ArrayList<String> sequencesNamesA, ArrayList<String> sequencesB, ArrayList<String> sequencesNamesB, int select, boolean reverseComplementB, String outputfileName, boolean useMUSCLE, ProcessReference processReference) {
        Mapping mapping = null;

        File tempDir = Utils.createTempDirectory();
        File inputFile = Utils.getFile(tempDir, "inputfile.fasta");
        File outputFile = Utils.getFile(tempDir, outputfileName);
        try {
            BufferedWriter buffer = new BufferedWriter(new FileWriter(inputFile));
            if (impute) {
                AlignmentUtils.impute(sequencesA, Mapping.select);
                AlignmentUtils.impute(sequencesB, Mapping.select);
            } else if (imputeRandomN) {
                Random random = new Random(-8813712038722450337L);
                int N = 50;

                // choose the first s sequences and make a random selection from the rest
                while (sequencesA.size() > N && sequencesA.size() > Mapping.select) {
                    int removeIndex = Mapping.select + random.nextInt(sequencesA.size() - Mapping.select);
                    sequencesA.remove(removeIndex);
                    sequencesNamesA.remove(removeIndex);
                }
                // choose the first s sequences and make a random selection from the rest
                while (sequencesB.size() > N && sequencesB.size() > Mapping.select) {
                    int removeIndex = Mapping.select + random.nextInt(sequencesB.size() - Mapping.select);
                    sequencesB.remove(removeIndex);
                    sequencesNamesB.remove(removeIndex);
                }

                AlignmentUtils.impute(sequencesA, Mapping.select);
                AlignmentUtils.impute(sequencesB, Mapping.select);
            }

            for (int i = 0; i < select && i < sequencesA.size(); i++) {
                buffer.write(">a" + i);
                buffer.newLine();
                buffer.write(sequencesA.get(i).replaceAll("-", GAP_CHARACTER + ""));
                buffer.newLine();
                //aLen = sequencesA.get(i).length();
            }
            for (int i = 0; i < select && i < sequencesB.size(); i++) {
                buffer.write(">b" + i);
                buffer.newLine();
                if (reverseComplementB) {
                    buffer.write(reverseComplement(sequencesB.get(i)).replaceAll("-", GAP_CHARACTER + ""));
                } else {
                    buffer.write(sequencesB.get(i).replaceAll("-", GAP_CHARACTER + ""));
                }
                buffer.newLine();
                //bLen = sequencesB.get(i).length();
            }
            buffer.close();
            if (Mapping.select >= 1) {
                //String cmd = MUSCLE_EXECUTABLE + " -in " + inputFile.getAbsolutePath() + " -out " + outputFile.getAbsolutePath() + " -gapopen " + gapOpen + " -gapextend " + gapExtend;


                String cmd = new File(MAFFT_EXECUTABLE).getAbsolutePath() + " --retree 2 --maxiterate 1000 " + inputFile.getAbsolutePath() + " > " + outputFile.getAbsolutePath();
                if (useMUSCLE) {
                    cmd = MUSCLE_EXECUTABLE + " -in " + inputFile.getAbsolutePath() + " -out " + outputFile.getAbsolutePath() + " -gapopen " + gapOpen + " -gapextend " + gapExtend;
                }


                Process p = Runtime.getRuntime().exec(cmd);
                processReference.addProcess(p);
                if (!processReference.cancelled) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
                    String textline = null;
                    while ((textline = reader.readLine()) != null) {
                        // System.err.println(textline);
                    }
                    reader.close();
                    int exitCode = p.waitFor();

                    if (exitCode == 0) {
                        ArrayList<String> alignedSequences = new ArrayList<String>();
                        ArrayList<String> alignedSequenceNames = new ArrayList<String>();
                        IO.loadFastaSequences(outputFile, alignedSequences, alignedSequenceNames);


                        String alignedA0 = null;
                        String alignedB0 = null;
                        for (int i = 0; i < alignedSequences.size(); i++) {
                            if (alignedSequenceNames.get(i).startsWith("a")) {
                                alignedA0 = alignedSequences.get(i);
                            }

                            if (alignedSequenceNames.get(i).startsWith("b")) {
                                alignedB0 = alignedSequences.get(i);
                            }
                        }

                        mapping = getMappingFromAlignedStrings(alignedA0, alignedB0, reverseComplementB);
                    }
                } else {
                    p.destroy();
                }
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return mapping;
    }

    public static Mapping createMapping(File alignmentA, File alignmentB, int select, boolean useMUSCLE) {
        if (alignmentA.length() == alignmentB.length() && IO.contentEquals(alignmentA, alignmentB)) { // if files are identical
            ArrayList<String> sequencesA = new ArrayList<String>();
            ArrayList<String> sequencesNamesA = new ArrayList<String>();
            IO.loadFastaSequences(alignmentA, sequencesA, sequencesNamesA);
            return Mapping.getMappingFromAlignedStrings(sequencesA.get(0), sequencesA.get(0), false);
        }

        ArrayList<String> sequencesA = new ArrayList<>();
        ArrayList<String> sequencesNamesA = new ArrayList<>();
        ArrayList<String> sequencesB = new ArrayList<>();
        ArrayList<String> sequencesNamesB = new ArrayList<>();

        int maxSequencesToLoad = Math.max(Mapping.select, 100); // to ensure fast loading limit the number of sequences to be loaded
        IO.loadFastaSequences(alignmentA, sequencesA, sequencesNamesA, maxSequencesToLoad);
        IO.loadFastaSequences(alignmentB, sequencesB, sequencesNamesB, maxSequencesToLoad);
        return Mapping.createMapping(sequencesA, sequencesNamesA, sequencesB, sequencesNamesB, select, useMUSCLE, new ProcessReference());
    }

    public static Mapping createMapping(ArrayList<String> sequencesA, ArrayList<String> sequencesNamesA, ArrayList<String> sequencesB, ArrayList<String> sequencesNamesB, int select, boolean useMUSCLE, ProcessReference processReference) {
        // identity mapping
        if (sequencesA.equals(sequencesB)) { // if alignments are identical
            return Mapping.getMappingFromAlignedStrings(sequencesA.get(0).replace('-', Mapping.GAP_CHARACTER), sequencesA.get(0).replace('-', Mapping.GAP_CHARACTER), false);
        }

        Mapping mapping = createMapping(sequencesA, sequencesNamesA, sequencesB, sequencesNamesB, select, false, "mappingforward.fas", useMUSCLE, processReference);
        Mapping reverseMapping = createMapping(sequencesA, sequencesNamesA, sequencesB, sequencesNamesB, select, true, "mappingreverse.fas", useMUSCLE, processReference);

        if (mapping == null || reverseMapping == null) {
            return null;
        }

        double m1 = 0;
        double t1 = 0;
        for (int i = 0; i < mapping.alignedA0.length(); i++) {
            if (mapping.alignedA0.charAt(i) == mapping.alignedB0.charAt(i) || mapping.alignedA0.charAt(i) == '-' || mapping.alignedB0.charAt(i) == '-') {
                m1++;
            }
            t1++;
        }

        double m2 = 0;
        double t2 = 0;
        for (int i = 0; i < reverseMapping.alignedA0.length(); i++) {
            if (reverseMapping.alignedA0.charAt(i) == reverseMapping.alignedB0.charAt(i) || reverseMapping.alignedA0.charAt(i) == '-' || reverseMapping.alignedB0.charAt(i) == '-') {
                m2++;
            }
            t2++;
        }

        if ((m1 / t1) >= (m2 / t2)) {
            return mapping;
        } else {
            return reverseMapping;
        }
    }

    public static Mapping getMappingFromAlignedStrings(String alignedA0, String alignedB0, boolean bReversedComplemented) {
        if (alignedA0 == null || alignedB0 == null) {
            return null;
        }

        if (alignedA0.length() != alignedB0.length()) {
            System.out.println(alignedA0 + "\t" + alignedB0);
            throw new Error("Aligned sequences are not of equal length.");
        }

        Mapping mapping = new Mapping();
        mapping.refLen = getUngappedPosition(alignedA0, alignedA0.length());
        mapping.alignedA0 = alignedA0;
        mapping.alignedB0 = alignedB0;
        mapping.bReversedComplemented = bReversedComplemented;

        mapping.refToAList = new int[alignedA0.length()];
        mapping.refToBList = new int[alignedA0.length()];
        mapping.aToRefList = new int[alignedA0.length()];
        mapping.bToRefList = new int[alignedA0.length()];
        Arrays.fill(mapping.refToAList, -1);
        Arrays.fill(mapping.refToBList, -1);
        Arrays.fill(mapping.aToRefList, -1);
        Arrays.fill(mapping.bToRefList, -1);

        for (int i = 0; i < alignedA0.length(); i++) {
            int ref = i;
            int refToA = getUngappedPosition(alignedA0, ref);
            if (alignedA0.charAt(ref) != '-') {
                //mapping.referenceToA.put(ref, refToA);
                mapping.refToAList[ref] = refToA;
                /*
                 * if (!mapping.aToReference.containsKey(refToA)) {
                 * mapping.aToReference.put(refToA, ref); }
                 */
            }
        }

        int bLength = getUngappedPosition(alignedB0, alignedB0.length());
        if (bReversedComplemented) {
            for (int i = 0; i < alignedB0.length(); i++) {
                int ref = i;
                int refToB = bLength - getUngappedPosition(alignedB0, ref) - 1;
                if (alignedB0.charAt(ref) != '-') {
                    //mapping.referenceToB.put(ref, refToB);
                    mapping.refToBList[ref] = refToB;
                    /*
                     * if (!mapping.bToReference.containsKey(refToB)) {
                     * mapping.bToReference.put(refToB, ref); }
                     */
                }
            }
        } else {
            for (int i = 0; i < alignedB0.length(); i++) {
                int ref = i;
                int refToB = getUngappedPosition(alignedB0, ref);
                if (alignedB0.charAt(ref) != '-') {
                    //mapping.referenceToB.put(ref, refToB);
                    mapping.refToBList[ref] = refToB;
                    /*
                     * if (!mapping.bToReference.containsKey(refToB)) {
                     * mapping.bToReference.put(refToB, ref); }
                     */
                }
            }
        }

        for (int i = 0; i < mapping.refToAList.length; i++) {
            if (mapping.refToAList[i] > -1) {
                mapping.aToRefList[mapping.refToAList[i]] = i;
            }
        }

        for (int i = 0; i < mapping.refToBList.length; i++) {
            if (mapping.refToBList[i] > -1) {
                mapping.bToRefList[mapping.refToBList[i]] = i;
            }
        }

        return mapping;
    }

    public void printMapping(Hashtable<Integer, Integer> mapping) {
        Enumeration<Integer> en = mapping.keys();
        while (en.hasMoreElements()) {
            Integer key = en.nextElement();
            Integer val = mapping.get(key);
            System.out.println(key + "\t" + val + "\t" + aToRef(key.intValue()) + "\t" + aToB(key.intValue()));
        }
    }

    public void saveMapping(File outFile) {
        try {
            BufferedWriter buffer = new BufferedWriter(new FileWriter(outFile));
            buffer.write(">ReverseComplement=" + Boolean.toString(bReversedComplemented) + "\n");
            buffer.write(alignedA0 + "\n");
            buffer.write(alignedB0 + "\n");
            buffer.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static Mapping loadMapping(File inFile) {
        try {
            BufferedReader buffer = new BufferedReader(new FileReader(inFile));
            boolean bReversedComplemented = Boolean.parseBoolean(buffer.readLine().substring(19));
            String alignedA0 = buffer.readLine();
            String alignedB0 = buffer.readLine();
            buffer.close();
            return getMappingFromAlignedStrings(alignedA0, alignedB0, bReversedComplemented);
        } catch (IOException ex) {
            Logger.getLogger(Mapping.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
