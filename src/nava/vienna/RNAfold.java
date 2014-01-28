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
    
    public static double calculateEnsembleFrequency(ViennaRuntime viennaRuntime, int [] pairedSites, String sequence, int tempCelsius) throws Exception
    {
        RNAfold rnafold = new RNAfold(viennaRuntime);
        double foldingTemp = Double.isNaN(tempCelsius) ? ViennaRuntime.defaultTempCelsius : tempCelsius;
        RNAfoldResult result = rnafold.fold(sequence, foldingTemp, true);
        Structure mfe = result.getMFEstructure();
        RNAeval rnaeval = new RNAeval(viennaRuntime);
        double freeEnergy = rnaeval.calculateFreeEnergy(sequence, pairedSites, foldingTemp);
        double difference = mfe.freeEnergy - freeEnergy;
        double ensembleFrequencyOfStructure = Math.exp(difference)*result.mfeEnsembleFrequency;
        return ensembleFrequencyOfStructure;
    }
    
    public static void main(String [] args) throws IOException, InterruptedException
    {
        ViennaRuntime viennaRuntime = new ViennaRuntime(new File("C:/Program Files (x86)/ViennaRNA Package/"), OS.WINDOWS);
        
        /*RNAfold rnafold = new RNAfold(viennaRuntime);
        System.out.println(rnafold.fold("CCGGGUCCGCCUCCUUGGCGGGCAAAAAAAAAAAAAAACCCGGCGAUUAGCCGGGCCGG", 37, true));
        RNAfoldResult result = rnafold.fold("GGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCC", 37, true);
        Structure mfeStructure = result.getMFEstructure();
        
        RNAeval rnaeval = new RNAeval(viennaRuntime);
         System.out.println(mfeStructure.pairedSites.length);
        */
        RNAinverse rnainverse = new RNAinverse(viennaRuntime);
        
        //rnainverse.inverse(RNAFoldingTools.getPairedSitesFromDotBracketString("(((((((.....)))))))"), 37, 10);
        IncaRNAtion incarnation = new IncaRNAtion();
        
        int [] target = RNAFoldingTools.getPairedSitesFromDotBracketString(".((((((((((((....................)))))))))).))(((((((((((((..............)))))))))))))....");
        //int [] target = RNAFoldingTools.getPairedSitesFromDotBracketString("(((.((((....((((((((((............(((.......)))))))))))))....)))))))((.......................................................))(((((....((((((((.........))))))))...))))).......(((.....)))......................");
        System.out.println("target_length="+target.length);
        boolean useMFE = false;
        boolean usePartitionFunction = true;
        boolean applyConstraintMaskForStackingBases = true;
        double gcContent = 0.5;
        int count = 0;
        
        ArrayList<Structure> firstSample = incarnation.generateSample(target, 37, gcContent, 20, 1000);
        long startTime = System.currentTimeMillis();
        for(Structure s2 : firstSample)
        {    
           ArrayList<Structure> sample = rnainverse.inverse(target, s2.sequence, 37, 1, useMFE, usePartitionFunction, applyConstraintMaskForStackingBases);
            //ArrayList<Structure> sample = rnainverse.inverse(target, null, 37, 5, useMFE, usePartitionFunction, applyConstraintMaskForStackingBases);
            if(sample != null)
            {
                for(Structure s : sample)
                {
                    try {                        
                        count++;
                        long elapsedTime = System.currentTimeMillis()-startTime;
                        double rate = count / (elapsedTime/1000.0);
                        System.out.print(rate+"\t"+elapsedTime+"\t");
                        s.calculateEnsembleFrequency(viennaRuntime);
                    } catch (Exception ex) {
                        Logger.getLogger(RNAfold.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
           
           /*for(int i = 0 ;  ; i++)
           {    
               ArrayList<Structure> sample = rnainverse.inverse(RNAFoldingTools.getPairedSitesFromDotBracketString("(((.((((....((((((((((............(((.......)))))))))))))....)))))))((.......................................................))(((((....((((((((.........))))))))...))))).......(((.....)))......................"), null, 37, 2, false, true);
              // ArrayList<Structure> sample = rnainverse.inverse(RNAFoldingTools.getPairedSitesFromDotBracketString(".((((((((((((....................)))))))))).))(((((((((((((..............)))))))))))))...."), null, 37, 2, false, true);
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
           }*/
    }
    
    public double [][] getBasePairProb(File rnafoldBasePairProbFile, int length) throws IOException
    {
        double [][] probs = new double[length][length];
        BufferedReader buffer = new BufferedReader(new FileReader(rnafoldBasePairProbFile));
        String textline = null;
        while((textline = buffer.readLine()) != null)
        {
            if(textline.endsWith("ubox") && !textline.startsWith("%"))
            {
                String [] split = textline.split("(\\s)+");
                int x = Integer.parseInt(split[0])-1;
                int y = Integer.parseInt(split[1])-1;
                double prob = Math.pow(Double.parseDouble(split[2]), 2);
                if(x < probs.length && y < probs.length)
                {
                    probs[x][y] = prob;
                    probs[y][x] = prob;                
                }
            }
        }
        for(int i = 0 ; i < probs.length ; i++)
        {
            probs[i][i] = 0;
            for(int j = 0 ; j < probs.length ; j++)
            {
                probs[i][i] += probs[i][j];
            }
        }
        
        /*System.out.println("----------------------------------------------------------------");
         for(int i = 0 ; i < probs.length ; i++)
        {
            for(int j = 0 ; j < probs.length ; j++)
            {
                System.out.print(probs[i][j]+"\t");
            }
            System.out.println();
        }
           System.out.println("----------------------------------------------------------------");
        */
        buffer.close();
        return probs;
    }
    
    public RNAfoldResult fold(String sequence, double tempCelsius, boolean partitionFunction) throws IOException, InterruptedException
    {
        File tempDir = Utils.createTemporaryDirectory("vienna.rnafold");
        //String cmd = viennaRuntime.getExecutablePath("RNAfold")+(partitionFunction ? " -p " :"") +" --temp="+tempCelsius;
        ArrayList<String> cmds = new ArrayList<>();
        cmds.add(viennaRuntime.getExecutablePath("RNAfold"));
        cmds.add(partitionFunction ? "−−partfunc=1" :"");
        cmds.add("--temp="+tempCelsius);
        
        ProcessBuilder builder = new ProcessBuilder();
        builder.directory(tempDir);
        builder.command(cmds);
        Process process = builder.start();
        //Process process = Runtime.getRuntime().exec(cmd);
         
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
            if(partitionFunction)
            {
                rnafoldResult.basePairProb = getBasePairProb(new File(tempDir.getAbsolutePath()+File.separatorChar+"seq_dp.ps"), sequence.length());
            }
            return rnafoldResult;
        }
        else
        {
            System.err.println("RNAfold exited with error");
            return null;
        }
    }
}