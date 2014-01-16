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

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class RNAinverse {
    private ViennaRuntime viennaRuntime;
    
    public RNAinverse(ViennaRuntime viennaRuntime)
    {
        this.viennaRuntime = viennaRuntime;
    }
    
    /*public void inverse(int [] pairedSites, String startSequence, double tempCelsius, int repeats, boolean useMFE, boolean usePartitionFunction) throws IOException, InterruptedException
    {
       File tempDir = Utils.createTemporaryDirectory("vienna.rnafold");


       File outFile = new File(tempDir.getAbsolutePath()+File.separatorChar+"structure.fas");
       BufferedWriter writer = new BufferedWriter(new FileWriter(outFile));
       writer.write(RNAFoldingTools.getDotBracketStringFromPairedSites(pairedSites));
       writer.newLine();
       if(startSequence != null)
       {
            writer.write(startSequence);
            writer.newLine();
       }
       writer.close();

       String folding = "-";
     
       if(useMFE && usePartitionFunction)
       {
           folding = "-Fmp";
       }
       else
       if(useMFE)
       {
           folding = "-Fm";
       }
       else
       if(usePartitionFunction)
       {
           folding = "-Fp";
       }
       
        
       String cmd = "cmd /c "+viennaRuntime.getExecutablePath("RNAinverse")+" -R "+repeats+" -T "+tempCelsius+ " "+folding;
        //String cmd = "cmd /c "+viennaRuntime.getExecutablePath("RNAinverse")+" -R "+repeats+" -T "+tempCelsius+ " "+folding+" < "+outFile.getAbsolutePath();

        Process process = Runtime.getRuntime().exec(cmd);
        Utils.nullOutput(process.getErrorStream());
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String textline = null;
        for(int i = 0 ; (textline = reader.readLine()) != null ; i++)
        {
            Structure s = new Structure();
            s.tempCelsius = tempCelsius;
            
            String [] split = textline.split("(\\s)+",3);
            
            s.sequence = split[0];
            s.pairedSites = pairedSites;
            if(split.length > 2)
            {
                if(split[2].startsWith("d="))
                {
                    s.structureDistance = Double.parseDouble(split[2].replaceAll("[a-z=\\(\\)\\s]", ""));
                }
                
                if(!split[2].startsWith("d="))
                {
                    s.ensembleFrequency = Double.parseDouble(split[2].replaceAll("[\\(\\)\\s]", ""));
                }
            }
            try {
                //System.out.println(s);
                s.calculateEnsembleFrequency(viennaRuntime);
                //System.out.println(s.calculateEnsembleFrequency(viennaRuntime));
            } catch (Exception ex) {
                Logger.getLogger(RNAinverse.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        reader.close();
        int exitCode = process.waitFor();

        if (exitCode == 0) {
            
        }
        else
        {
            System.err.println("RNAinverse exited with error");
        }
        
    }*/
    
    public ArrayList<Structure> inverse(int [] pairedSites, String startSequence, double tempCelsius, int repeats, boolean useMFE, boolean usePartitionFunction) throws IOException, InterruptedException
    {
       String folding = "";     
       if(useMFE && usePartitionFunction)
       {
           folding = "--function=mp";
       }
       else
       if(useMFE)
       {
           folding = "--function=m";
       }
       else
       if(usePartitionFunction)
       {
           folding = "--function=p";
       }
       
        
        String cmd = viennaRuntime.getExecutablePath("RNAinverse")+" --repeat="+repeats+" --temp="+tempCelsius+ " "+folding;
        //String cmd = "cmd /c "+viennaRuntime.getExecutablePath("RNAinverse")+" -R "+repeats+" -T "+tempCelsius+ " "+folding+" < "+outFile.getAbsolutePath();

        Process process = Runtime.getRuntime().exec(cmd);
        
        BufferedOutputStream stdin = new BufferedOutputStream(process.getOutputStream());
        stdin.write((RNAFoldingTools.getDotBracketStringFromPairedSites(pairedSites)+"\n").getBytes());
        if(startSequence != null)
        {
            stdin.write((startSequence+"\n").getBytes());
        }
        else
        {
            stdin.write((Utils.nChars('N', pairedSites.length)+"\n").getBytes());
        }
        stdin.write("@\n".getBytes());
        stdin.close();
        
        Utils.nullOutput(process.getErrorStream());
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String textline = null;
        ArrayList<Structure> sample = new ArrayList<>();
        while((textline = reader.readLine()) != null)
        {
            Structure s = new Structure();
            s.tempCelsius = tempCelsius;
            
            String [] split = textline.split("(\\s)+",3);
            
            s.sequence = split[0];
            s.pairedSites = pairedSites;
            if(split.length > 2)
            {
                if(split[2].startsWith("d="))
                {
                    s.structureDistance = Double.parseDouble(split[2].replaceAll("[a-z=\\(\\)\\s]", ""));
                }
                
                if(!split[2].startsWith("d="))
                {
                    s.ensembleFrequency = Double.parseDouble(split[2].replaceAll("[\\(\\)\\s]", ""));
                }
            }
            
            sample.add(s);
            try {
                //System.out.println(s);
               // s.calculateEnsembleFrequency(viennaRuntime);
                //System.out.println(s.calculateEnsembleFrequency(viennaRuntime));
            } catch (Exception ex) {
                Logger.getLogger(RNAinverse.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        reader.close();
        
        /*
         * When RNAinverse crashes it can hang here indefinitely in Windows, because it brings up GUI error dialog.
         * We will have to set this registry value to 1 instead: HKEY_CURRENT_USER\Software\ Microsoft\Windows\Windows Error Reporting\DontShowUI
         */
       int exitCode = process.waitFor();

        if (exitCode == 0) {
            return sample;
        }
        else
        {
            //System.err.println("RNAinverse exited with an error, code: "+exitCode);
            return null;
        }
    }
}
