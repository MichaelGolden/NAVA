/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.vienna;

import java.io.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import nava.utils.RNAFoldingTools;
import nava.vienna.ViennaRuntime.OS;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class RNAfold 
{
    private ViennaRuntime viennaRuntime;
    
    public RNAfold(ViennaRuntime viennaRuntime)
    {
        this.viennaRuntime = viennaRuntime;
    }
    
    public static void main(String [] args) throws IOException, InterruptedException
    {
        ViennaRuntime viennaRuntime = new ViennaRuntime(new File("C:/Program Files (x86)/ViennaRNA Package/"), OS.WINDOWS);
        RNAfold rnafold = new RNAfold(viennaRuntime);
        System.out.println(rnafold.fold("CCGGGUCCGCCUCCUUGGCGGGCAAAAAAAAAAAAAAACCCGGCGAUUAGCCGGGCCGG", 37, true));
        RNAfoldResult result = rnafold.fold("GGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCC", 37, true);
        Structure mfeStructure = result.getMFEstructure();
        
        RNAeval rnaeval = new RNAeval(viennaRuntime);
        System.out.println(rnaeval.calculateFreeEnergy(mfeStructure.sequence, mfeStructure.pairedSites, 37));
        
           RNAinverse rnainverse = new RNAinverse(viennaRuntime);
           System.out.println(mfeStructure.pairedSites.length);
           //rnainverse.inverse(RNAFoldingTools.getPairedSitesFromDotBracketString("(((((((.....)))))))"), 37, 10);
           for(int i = 0 ;  ; i++)
           {       
               ArrayList<Structure> sample = rnainverse.inverse(RNAFoldingTools.getPairedSitesFromDotBracketString(".((((((((((((....................)))))))))).))(((((((((((((..............)))))))))))))...."), null, 37, 2, false, true);
             //  System.out.println(sample);
               if(sample != null)
               {
                   for(Structure s : sample)
                   {
                        try {
                            s.calculateEnsembleFrequency(viennaRuntime);
                        } catch (Exception ex) {
                            //Logger.getLogger(RNAfold.class.getName()).log(Level.SEVERE, null, ex);
                        }
                   }
               }
                //rnainverse.inverse(RNAFoldingTools.getPairedSitesFromDotBracketString("(((.((((....((((((((((............(((.......)))))))))))))....)))))))((.......................................................))(((((....((((((((.........))))))))...))))).......(((.....)))......................"), null, 37, 100, false, false);
           }
           
           //rnainverse.inverse(RNAFoldingTools.getPairedSitesFromDotBracketString("(((.((((....((((((((((............(((.......)))))))))))))....)))))))((.......................................................))(((((....((((((((.........))))))))...))))).......(((.....)))......................"), null, 37, 100, false, false);
    }
    
    public RNAfoldResult fold(String sequence, double tempCelsius, boolean partitionFunction) throws IOException, InterruptedException
    {
        String cmd = viennaRuntime.getExecutablePath("RNAfold")+(partitionFunction ? " -p " :"") +" --temp="+tempCelsius;
       
        Process process = Runtime.getRuntime().exec(cmd);
         
        BufferedOutputStream stdin = new BufferedOutputStream(process.getOutputStream());
        stdin.write((">seq\n").getBytes());
        stdin.write((sequence+"\n").getBytes());
        stdin.close();
        
        Utils.nullOutput(process.getErrorStream());
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String textline = null;
        RNAfoldResult rnafoldResult = new RNAfoldResult();
        Structure mfeStructure = new Structure();
        mfeStructure.tempCelsius = tempCelsius;
        for(int i = 0 ; (textline = reader.readLine()) != null ; i++)
        {
            switch(i)
            {
                case 1:
                    mfeStructure.sequence = textline;
                    break;
                case 2:
                    String [] split =textline.split("(\\s)+",2);
                    String dbn = split[0];
                    mfeStructure.pairedSites = RNAFoldingTools.getPairedSitesFromDotBracketString(dbn);
                    mfeStructure.freeEnergy = Double.parseDouble(split[1].replaceAll("[\\(\\)\\s]", ""));
                    break;
            }
            
            if(i > 2 && textline.trim().startsWith("frequency of mfe"))
            {
                String [] colonSplit = textline.split(";");
                String [] freqSplit = colonSplit[0].split("\\s");
                String [] divSplit = colonSplit[1].split("\\s");
                
                rnafoldResult.mfeEnsembleFrequency = Double.parseDouble(freqSplit[freqSplit.length-1]);                
                mfeStructure.ensembleFrequency = rnafoldResult.mfeEnsembleFrequency;
                rnafoldResult.ensembleDiversity = Double.parseDouble(divSplit[divSplit.length-1]);
        
            }
        }

        reader.close();
        int exitCode = process.waitFor();

        if (exitCode == 0) {
            rnafoldResult.mfeStructure = mfeStructure;
            return rnafoldResult;
        }
        else
        {
            System.err.println("RNAfold exited with error");
            return null;
        }
    }
}