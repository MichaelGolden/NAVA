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
       // new AutomatedFolding().performingFolding(new File("C:/dev/thesis/jev/300/all_300_aligned.fas"), new File("C:/dev/thesis/jev/jev_all.dbn"), 4);
         //new AutomatedFolding().performingFolding(new File("C:/dev/thesis/gb/300/all_300_aligned.fas"), new File("C:/dev/thesis/gb/gb_all.dbn"), 4);
        // new AutomatedFolding().performingFolding(new File("C:/dev/thesis/csfv/300/all_300_aligned.fas"), new File("C:/dev/thesis/csfv/csfv_all.dbn"), 4);
         //new AutomatedFolding().performingFolding(new File("C:/dev/thesis/bvdv/300/all_300_aligned.fas"), new File("C:/dev/thesis/bvdv/bvdv_all.dbn"), 4);
        //new AutomatedFolding().performingFolding(new File("C:/dev/thesis/tbv/all_tbv.fas"), new File("C:/dev/thesis/tbv/all_tbv.dbn"), 8);
        new AutomatedFolding().performingFolding(new File("C:/dev/thesis/jev_tbv_westnile/jev_westnile.fas"), new File("C:/dev/thesis/jev_tbv_westnile/jev_westnile.dbn"), 8);
        
    }
    
    public static class Fold
    {
        String sequence;
        int [] pairedSites;
        double freeEnergy;

        @Override
        public String toString() {
            return sequence+"$"+ RNAFoldingTools.getDotBracketStringFromPairedSites(pairedSites)+"$"+freeEnergy;
        }
        
        public static Fold getFoldFromString(String s)
        {
            Fold fold = new Fold();
            String [] split = s.split("\\$");
            fold.sequence = split[0];
            fold.pairedSites = RNAFoldingTools.getPairedSitesFromDotBracketString(split[1]);
            fold.freeEnergy = Double.parseDouble(split[2]);
            return fold;
        }
        
        
    }
    
    public static Fold fold(String sequence, int threads)
    {
            ArrayList<String> seq = new ArrayList<>();
            ArrayList<String> seqName = new ArrayList<>();
            seq.add(sequence);
            seqName.add("sequence");
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
              //System.out.println(code);
                if (code == 0) {
                try {
                    ArrayList<SecondaryStructureData> structure = FileImport.readConnectFile(tempCtFile);
                    SecondaryStructureData data = structure.get(0);
                    Fold fold = new Fold();
                    fold.sequence = data.sequence;
                    fold.pairedSites = data.pairedSites;
                    fold.freeEnergy = Double.parseDouble(data.title.split("=")[1].trim().split("\\s")[0]);
                    return fold;
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
            
            return null;
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
