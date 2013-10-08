/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.experimental;

import java.io.File;
import java.util.ArrayList;
import nava.data.io.IO;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class AppendAlignments {
    public static void main(String [] args)
    {
        ArrayList<File> fastaFiles = new ArrayList<File>();
        fastaFiles.add(new File("C:/dev/thesis/hiv_full/darren/300/darren_hiv_gag_300_nooverlap_aligned.fas"));
        fastaFiles.add(new File("C:/dev/thesis/hiv_full/darren/300/darren_hiv_pol_300_nooverlap_aligned.fas"));
        fastaFiles.add(new File("C:/dev/thesis/hiv_full/darren/300/darren_hiv_vif_300_nooverlap_aligned.fas"));
        fastaFiles.add(new File("C:/dev/thesis/hiv_full/darren/300/darren_hiv_vpr_300_nooverlap_aligned.fas"));
        fastaFiles.add(new File("C:/dev/thesis/hiv_full/darren/300/darren_hiv_vpu_300_nooverlap_aligned.fas"));
        fastaFiles.add(new File("C:/dev/thesis/hiv_full/darren/300/darren_hiv_env_300_nooverlap_aligned.fas"));
        fastaFiles.add(new File("C:/dev/thesis/hiv_full/darren/300/darren_hiv_nef_300_nooverlap_aligned.fas"));
        
        
        
        ArrayList<String> sequencesFinal = new ArrayList<>();
        ArrayList<String> sequencesNamesFinal = new ArrayList<>();
        for(File fasta : fastaFiles)
        {
            ArrayList<String> sequences = new ArrayList<>();
            ArrayList<String> sequencesNames = new ArrayList<>();
            IO.loadFastaSequences(fasta, sequences, sequencesNames);
            for(int i = 0 ; i < sequences.size() ; i++)
            {
                if(i >= sequencesFinal.size())
                {
                    sequencesFinal.add(sequences.get(i));
                    sequencesNamesFinal.add(sequencesNames.get(i));
                }
                else
                {
                    sequencesFinal.set(i, sequencesFinal.get(i)+"---"+sequences.get(i));
                }
            }
        }
        
        IO.saveToFASTAfile(sequencesFinal, sequencesNamesFinal, new File("C:/dev/thesis/hiv_full/darren/300/appended.fas"));
    }
}
