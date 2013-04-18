/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.experimental;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import nava.data.io.FileImport;
import nava.data.io.FileImport.ParserException;
import nava.data.io.IO;
import nava.data.types.SecondaryStructureData;
import nava.tasks.applications.Application;
import nava.utils.GeneticCode;
import nava.utils.RNAFoldingTools;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class AutomatedFolding {

    public static final File workingDir = new File("bin/hybrid-ss-min-tiled-cpu-win");
    public static final String HYBRIDSSMIN_EXECUTABLE = "bin/hybrid-ss-min-tiled-cpu-win/hybrid-ss-min-SSE4.exe";

    public static void main(String[] args) {
       // new AutomatedFolding().performingFolding(new File("C:/dev/thesis/dengue_50x4.fas"), new File("C:/dev/thesis/dengue_50x4.dbn"));
        // new AutomatedFolding().performingFolding(new File("C:/dev/thesis/hiv_full/hiv_full.fas"), new File("C:/dev/thesis/hiv_full/hiv_full.dbn"), 4);
         //new AutomatedFolding().performingFolding(new File("C:/dev/thesis/westnile/200/westnile_all_200_aligned.fas"), new File("C:/dev/thesis/westnile/westnile_all_200_aligned.dbn"), 4);
        new AutomatedFolding().performingFolding(new File("C:/dev/thesis/jev/300/all_300_aligned.fas"), new File("C:/dev/thesis/jev/jev_all.dbn"), 4);
        
    }

    public void performingFolding(File inFastaFile, File outCtFile, int threads) {
        ArrayList<String> sequences = new ArrayList<>();
        ArrayList<String> sequenceNames = new ArrayList<>();
        IO.loadFastaSequences(inFastaFile, sequences, sequenceNames);
        outCtFile.delete();
        for (int i = 0; i < sequences.size(); i++) {
            ArrayList<String> seq = new ArrayList<>();
            ArrayList<String> seqName = new ArrayList<>();
            seq.add(sequences.get(i));
            seqName.add(sequenceNames.get(i));
            File foldFastaFile = new File("fold.fas");
            //File workingDirectory = new File("");
            IO.saveToFASTAfile(seq, seqName, foldFastaFile);
            try {

                File tempCtFile = new File(workingDir.getAbsolutePath() + File.separator + "fold.fas.ct");
                String cmd = new File(HYBRIDSSMIN_EXECUTABLE).getAbsolutePath() + " " + foldFastaFile.getAbsolutePath() + " --threads="+threads;

                Process p = Runtime.getRuntime().exec(cmd, null, workingDir);
                Application.nullOutput(p.getInputStream());
                Application.nullOutput(p.getErrorStream());;
                int code = p.waitFor();
                System.out.println(code);
                if (code == 0) {
                    try {
                        ArrayList<SecondaryStructureData> structure = FileImport.readConnectFile(tempCtFile);
                        BufferedWriter buffer = new BufferedWriter(new FileWriter(outCtFile, true));
                        buffer.write(">" + structure.get(0).title);
                        buffer.newLine();
                        buffer.write(structure.get(0).sequence);
                        buffer.newLine();
                        buffer.write(RNAFoldingTools.getDotBracketStringFromPairedSites(structure.get(0).pairedSites));
                        buffer.newLine();
                        buffer.close();
                    } catch (ParserException ex) {
                        Logger.getLogger(AutomatedFolding.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (Exception ex) {
                        Logger.getLogger(AutomatedFolding.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                tempCtFile.delete();

            } catch (InterruptedException ex) {
                Logger.getLogger(GeneFinder.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(GeneFinder.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
