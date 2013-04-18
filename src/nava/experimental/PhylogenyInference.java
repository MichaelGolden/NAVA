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

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class PhylogenyInference {
    public static void main(String [] args)
    {
        File fastaFile = new File("C:/dev/thesis/dengue/300/all_300_norm.fas");
        File inTree = new File("C:/dev/thesis/dengue/300/all_300_tree.nwk");
        File outFile = new File("C:/dev/thesis/dengue/300/all_300_tree_norm.nwk");
        try {
            PhylogenyInference.resaveTree(fastaFile, inTree, outFile);
        } catch (Exception ex) {
            Logger.getLogger(PhylogenyInference.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void resaveTree(File fastaFile, File inTree, File outFile) throws Exception {
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
            BufferedWriter fileWriter = new BufferedWriter(new FileWriter(outFile));
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
}
