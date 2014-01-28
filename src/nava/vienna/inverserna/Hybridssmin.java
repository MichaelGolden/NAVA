/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.vienna.inverserna;

import nava.experimental.GeneFinder;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import nava.data.io.FileImport;
import nava.data.io.IO;
import nava.data.types.SecondaryStructureData;
import nava.utils.RNAFoldingTools;
import nava.vienna.Utils;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class Hybridssmin {

    public static final File workingDir = new File("bin/hybrid-ss-min-tiled-cpu-win");
    public static final File workingDirOld = new File("bin/hybrid-ss-min-tiled-cpu-win/old/");
    public static final String HYBRIDSSMIN_EXECUTABLE = "bin/hybrid-ss-min-tiled-cpu-win/hybrid-ss-min-SSE4.exe";
    public static final String HYBRIDSSMIN_EXECUTABLE_OLD = "bin/hybrid-ss-min-tiled-cpu-win/old/hybrid-ss-min-old.exe";

    public static void main(String[] args) {
       // new AutomatedFolding().performingFolding(new File("C:/dev/thesis/dengue_50x4.fas"), new File("C:/dev/thesis/dengue_50x4.dbn"));
        // new AutomatedFolding().performingFolding(new File("C:/dev/thesis/hiv_full/hiv_full.fas"), new File("C:/dev/thesis/hiv_full/hiv_full.dbn"), 4);
         //new AutomatedFolding().performingFolding(new File("C:/dev/thesis/westnile/200/westnile_all_200_aligned.fas"), new File("C:/dev/thesis/westnile/westnile_all_200_aligned.dbn"), 4);
       // new AutomatedFolding().performingFolding(new File("C:/dev/thesis/jev/300/all_300_aligned.fas"), new File("C:/dev/thesis/jev/jev_all.dbn"), 4);
         //new AutomatedFolding().performingFolding(new File("C:/dev/thesis/gb/300/all_300_aligned.fas"), new File("C:/dev/thesis/gb/gb_all.dbn"), 4);
        // new AutomatedFolding().performingFolding(new File("C:/dev/thesis/csfv/300/all_300_aligned.fas"), new File("C:/dev/thesis/csfv/csfv_all.dbn"), 4);
         //new AutomatedFolding().performingFolding(new File("C:/dev/thesis/bvdv/300/all_300_aligned.fas"), new File("C:/dev/thesis/bvdv/bvdv_all.dbn"), 4);
        //new AutomatedFolding().performingFolding(new File("C:/dev/thesis/tbv/all_tbv.fas"), new File("C:/dev/thesis/tbv/all_tbv.dbn"), 8);
        //new AutomatedFolding().performingFolding(new File("C:/dev/thesis/jev_tbv_westnile/jev_westnile.fas"), new File("C:/dev/thesis/jev_tbv_westnile/jev_westnile.dbn"), 8);
        //new AutomatedFolding().performingFolding(new File("C:/brej/msv/MSV_10seq.fas"), new File("C:/brej/msv/MSV_10seq.dbn"), 1, true, 25, true);
       // new AutomatedFolding().performingFolding(new File("C:/brej/gemini_begomo/Gemini_Begomovirus.fas"), new File("C:/brej/gemini_begomo/Gemini_Begomovirus.dbn"), 1, true, 25, true);
         //new AutomatedFolding().performingFolding(new File("C:/brej/gemini_mastrevirus/Gemini_Mastrevirus.fas"), new File("C:/brej/gemini_mastrevirus/Gemini_Mastrevirus.dbn"), 1, true, 25, true);
       // new AutomatedFolding().performingFolding(new File("C:/brej/circovirus/Circovirus.fas"), new File("C:/brej/circovirus/Circovirus.dbn"), 1, true, 37, true);
        // new AutomatedFolding().performingFolding(new File("C:/brej/anellovirus/Anellovirus.fas"), new File("C:/brej/anellovirus/Anellovirus.dbn"), 1, true, 37, true);
         //new AutomatedFolding().performingFolding(new File("C:/brej/parvovirus/Parvovirus.fas"), new File("C:/brej/parvovirus/Parvovirus.dbn"), 8, false, 37, true);
        //new AutomatedFolding().performingFolding(new File("C:/dev/thesis/hiv_full/darren/darren_hiv_full_aligned_muscle.fas"), new File("C:/dev/thesis/hiv_full/darren/darren_hiv_full_aligned_muscle.dbn"), 8);
        //new AutomatedFolding().performingFolding(new File("C:/dev/thesis/dengue/100/dengue_all_100_aligned.fas"), new File("C:/dev/thesis/dengue/100/dengue_all_100_aligned.dbn"), 8);
        //new AutomatedFolding().performingFolding(new File("C:/dev/thesis/hiv_full/test/darren_hiv.fas"), new File("C:/dev/thesis/hiv_full/test/darren_hiv.dbn"), 4);
        
        
    }
    
    public static class Fold
    {
        String sequence;
        int [] pairedSites;
        double freeEnergy;
        boolean cached;

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
        return fold(sequence, threads,false,37, false);
    }
    
     public static ArrayList<Fold> fold(List<String> sequences, int threads, boolean circular, double temp, boolean dna)
    {
            //File tempDir = Utils.createTemporaryDirectory("unafold.ssmin");
        
            ArrayList<Fold> folds = new ArrayList<>(sequences.size());
        
            ArrayList<String> seq = new ArrayList<>(sequences.size());
            ArrayList<String> seqName = new ArrayList<>(sequences.size());
            for(int i = 0 ; i < sequences.size() ; i++)
            {
                seqName.add("seq"+i);
                seq.add(sequences.get(i));
            }
            File foldFastaFile = new File(Utils.getTemporaryName("unafold.ssmin")+"fold.fas");
            //File workingDirectory = new File("");
            IO.saveToFASTAfile(seq, seqName, foldFastaFile);
            try {

                File tempCtFile = new File((circular ? workingDirOld.getAbsolutePath() : workingDir.getAbsolutePath()) + File.separator +foldFastaFile.getName()+ ".ct");
                String cmd = new File(HYBRIDSSMIN_EXECUTABLE).getAbsolutePath() + " " + foldFastaFile.getAbsolutePath() + " --threads="+threads;
                 //String cmd = new File(HYBRIDSSMIN_EXECUTABLE).getAbsolutePath() + " --threads="+threads;
                if(circular)
                {
                    cmd += " --circular ";
                }
                if(temp != 37)
                {
                    cmd += "--tmin="+temp+" --tmax="+temp+" ";
                }
                if(dna)
                {
                    cmd += " --NA=DNA ";
                }
                
              // cmd += " --stream";
               // cmd += " \""+tempDir.getAbsolutePath()+File.separatorChar+"out\"";
                Process process = Runtime.getRuntime().exec(cmd, null, (circular ? workingDirOld : workingDir));
                
                 /*BufferedOutputStream stdin = new BufferedOutputStream(process.getOutputStream());
                   for(int i = 0 ; i < sequences.size() ; i++)
            {
                stdin.write((">seq"+i+"\n").getBytes());
                stdin.write((sequences.get(i) +"\n").getBytes());
            }
               
                stdin.close();*/
                
               /* BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String textline = null;
                while((textline = reader.readLine()) != null)
                {
                    System.out.println(textline);
                }*/
                Utils.nullOutput(process.getInputStream());
                Utils.nullOutput(process.getErrorStream());;
                int code = process.waitFor();
                
                if (code == 0) {
                try {
                    ArrayList<SecondaryStructureData> structure = FileImport.readConnectFile(tempCtFile);
                    for(SecondaryStructureData data : structure)
                    {
                        Fold fold = new Fold();
                        fold.sequence = data.sequence;
                        fold.pairedSites = data.pairedSites;                    
                        fold.freeEnergy = Double.parseDouble(data.title.split("=")[1].trim().split("\\s")[0]);
                        folds.add(fold);
                    }
                    return folds;
                } catch (FileImport.ParserException ex) {
                    Logger.getLogger(Hybridssmin.class.getName()).log(Level.SEVERE, null, ex);
                } catch (Exception ex) {
                    Logger.getLogger(Hybridssmin.class.getName()).log(Level.SEVERE, null, ex);
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
    
    public static Fold fold(String sequence, int threads, boolean circular, double temp, boolean dna)
    {
            ArrayList<String> seq = new ArrayList<>();
            ArrayList<String> seqName = new ArrayList<>();
            seq.add(sequence);
            seqName.add("sequence");
            File foldFastaFile = new File("fold.fas");
            //File workingDirectory = new File("");
            IO.saveToFASTAfile(seq, seqName, foldFastaFile);
            try {

                File tempCtFile = new File((circular ? workingDirOld.getAbsolutePath() : workingDir.getAbsolutePath()) + File.separator + "fold.fas.ct");
                String cmd = new File(HYBRIDSSMIN_EXECUTABLE).getAbsolutePath() + " " + foldFastaFile.getAbsolutePath() + " --threads="+threads;
                if(circular)
                {
                    cmd += " --circular ";
                }
                if(temp != 37)
                {
                    cmd += "--tmin="+temp+" --tmax="+temp+" ";
                }
                if(dna)
                {
                    cmd += " --NA=DNA ";
                }
                
                Process p = Runtime.getRuntime().exec(cmd, null, (circular ? workingDirOld : workingDir));
                Utils.nullOutput(p.getInputStream());
                Utils.nullOutput(p.getErrorStream());;
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
                } catch (FileImport.ParserException ex) {
                    Logger.getLogger(Hybridssmin.class.getName()).log(Level.SEVERE, null, ex);
                } catch (Exception ex) {
                    Logger.getLogger(Hybridssmin.class.getName()).log(Level.SEVERE, null, ex);
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

        performingFolding(inFastaFile, outCtFile, threads, false, 37, false);
    }
   
          
    
    public void performingFolding(File inFastaFile, File outCtFile, int threads, boolean circular, double temp, boolean dna) {
        ArrayList<String> sequences = new ArrayList<>();
        ArrayList<String> sequenceNames = new ArrayList<>();
        IO.loadFastaSequences(inFastaFile, sequences, sequenceNames);
        //outCtFile.delete();
        for (int i = 0; i < sequences.size(); i++) {
            ArrayList<String> seq = new ArrayList<>();
            ArrayList<String> seqName = new ArrayList<>();
            seq.add(sequences.get(i));
            seqName.add(sequenceNames.get(i));
            File foldFastaFile = new File(inFastaFile.getName()+".fold.fas");
            //File workingDirectory = new File("");
            IO.saveToFASTAfile(seq, seqName, foldFastaFile);
            try {

                File tempCtFile = new File((circular ? workingDirOld.getAbsolutePath() : workingDir.getAbsolutePath()) + File.separator + inFastaFile.getName()+".fold.fas.ct");                
                System.out.println(">"+tempCtFile.getAbsolutePath());
                String cmd = new File(circular ? HYBRIDSSMIN_EXECUTABLE_OLD : HYBRIDSSMIN_EXECUTABLE).getAbsolutePath() + " " + foldFastaFile.getAbsolutePath() + (circular ? "" : " --threads="+threads);
                if(circular)
                {
                    cmd += " --circular ";
                }
                if(temp != 37)
                {
                    cmd += "--tmin="+temp+" --tmax="+temp+" ";
                }
                if(dna)
                {
                    cmd += " --NA=DNA ";
                }
                Process p = Runtime.getRuntime().exec(cmd, null, (circular ? workingDirOld : workingDir));
                String textline = null;
                BufferedReader reader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
                while((textline = reader.readLine()) != null)
                {
                    System.err.println(textline);
                }
                
                /*Application.nullOutput(p.getInputStream());
                Application.nullOutput(p.getErrorStream());*/
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
                    } catch (FileImport.ParserException ex) {
                        Logger.getLogger(Hybridssmin.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (Exception ex) {
                        Logger.getLogger(Hybridssmin.class.getName()).log(Level.SEVERE, null, ex);
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
