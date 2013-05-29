/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.experimental;

import java.io.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import nava.data.io.IO;
import nava.tasks.applications.Application;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class PhylogenyInference {

    public static String FAST_TREE_EXECUTABLE = "bin/FastTree.exe";

    public static void main(String[] args) {
        ArrayList<File> fastaFiles = new ArrayList<>();
        fastaFiles.add(new File("C:/dev/thesis/hcv/1/300/hcv1_all_300_aligned.fas"));
        fastaFiles.add(new File("C:/dev/thesis/hcv/1a/300/hcv1a_all_300_aligned.fas"));
        fastaFiles.add(new File("C:/dev/thesis/hcv/1b/300/hcv1b_all_300_aligned.fas"));
        fastaFiles.add(new File("C:/dev/thesis/hcv/2/300/hcv2_all_300_aligned.fas"));
        fastaFiles.add(new File("C:/dev/thesis/hcv/6/300/hcv6_all_300_aligned.fas"));
        fastaFiles.add(new File("C:/dev/thesis/dengue/300/dengue_all_300_aligned.fas"));
        fastaFiles.add(new File("C:/dev/thesis/dengue1/300/dengue1_all_300_aligned.fas"));
        fastaFiles.add(new File("C:/dev/thesis/dengue2/300/dengue2_all_300_aligned.fas"));
        fastaFiles.add(new File("C:/dev/thesis/dengue3/300/dengue3_all_300_aligned.fas"));
        fastaFiles.add(new File("C:/dev/thesis/dengue4/300/dengue4_all_300_aligned.fas"));
        fastaFiles.add(new File("C:/dev/thesis/jev/300/jev_all_300_aligned.fas"));
        fastaFiles.add(new File("C:/dev/thesis/westnile/300/westnile_all_300_aligned.fas"));

        // File fastaFile = new File("C:/dev/thesis/hcv/hcv_genotypes2_aligned.fas");
        //File inTree = new File("C:/dev/thesis/hcv/conservation/tree.nwk");
        //  File outFastaFile = new File("C:/dev/thesis/hcv/conservation/hcv_genotypes2_aligned.fas");
        // File outTreeFile = new File("C:/dev/thesis/hcv/conservation/tree_norm.nwk");

        //File fastaFile = new File("C:/dev/thesis/hcv/1/300/hcv1_all_300_aligned_test.fas");
        for (File fastaFile : fastaFiles) {
            File fastaNormFile = new File(fastaFile.getAbsolutePath() + "_norm");
            File newickFile = new File(fastaFile.getAbsolutePath() + ".nwk");
            try {
                ArrayList<String> sequences = new ArrayList<>();
                ArrayList<String> sequenceNames = new ArrayList<>();
                IO.loadFastaSequences(fastaFile, sequences, sequenceNames);
                for (int i = 0; i < sequenceNames.size(); i++) {
                    sequenceNames.set(i, "seq" + i);
                }
                IO.saveToFASTAfile(sequences, sequenceNames, fastaNormFile);
                String cmd = "cmd /c " + new File(FAST_TREE_EXECUTABLE).getAbsolutePath() + " -nt -gtr " + fastaNormFile.getAbsolutePath() + " > " + newickFile.getAbsolutePath();
                Process process = Runtime.getRuntime().exec(cmd);
                Application.nullOutput(process.getInputStream());
                Application.nullOutput(process.getErrorStream());
                int exitCode = process.waitFor();
                System.out.println(exitCode);
                resaveTree(newickFile, newickFile);
            } catch (Exception ex) {
                Logger.getLogger(PhylogenyInference.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void resaveTree(File fastaFile, File inTree, File outTreeFile, File outFastaFile) throws Exception {
        ArrayList<String> sequences = new ArrayList<>();
        ArrayList<String> sequenceNames = new ArrayList<>();
        IO.loadFastaSequences(fastaFile, sequences, sequenceNames);

        ArrayList<String> newSequenceNames = new ArrayList<>();
        for (int i = 0; i < sequenceNames.size(); i++) {
            newSequenceNames.add("seq" + i);
        }
        IO.saveToFASTAfile(sequences, newSequenceNames, outFastaFile);

        BufferedReader fileReader = new BufferedReader(new FileReader(inTree));
        String tree = fileReader.readLine();
        fileReader.close();
        if (tree != null) {
            System.out.println(sequenceNames);
            System.out.println(tree);
            BufferedWriter fileWriter = new BufferedWriter(new FileWriter(outTreeFile));
            for (int i = 0; i < sequenceNames.size(); i++) {
                // tree = tree.replaceAll("seq" + i + ":", sequenceNames.get(i) + ":");
            }
            tree = tree.replaceAll("[0-9]\\.[0-9]+\\:", ":");
            System.out.println(tree);
            fileWriter.write(tree);
            fileWriter.newLine();
            fileWriter.close();
        }
    }

    public static void resaveTree(File inTree, File outTreeFile) throws Exception {

        BufferedReader fileReader = new BufferedReader(new FileReader(inTree));
        String tree = fileReader.readLine();
        fileReader.close();
        if (tree != null) {
            BufferedWriter fileWriter = new BufferedWriter(new FileWriter(outTreeFile));
            tree = tree.replaceAll("[0-9]\\.[0-9]+\\:", ":");
            fileWriter.write(tree);
            fileWriter.newLine();
            fileWriter.close();
        }
    }
}
