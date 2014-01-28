/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.vienna;

import java.io.*;
import nava.utils.RNAFoldingTools;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class RNAeval {
    
    private ViennaRuntime viennaRuntime;
    
    public RNAeval(ViennaRuntime viennaRuntime)
    {
        this.viennaRuntime = viennaRuntime;
    }
    
    public double calculateFreeEnergy(String sequence, int [] pairedSites, double tempCelsius) throws IOException, InterruptedException
    {        
        String cmd = "cmd /c "+viennaRuntime.getExecutablePath("RNAeval")+" --temp="+tempCelsius;
       
        Process process = Runtime.getRuntime().exec(cmd);
        BufferedOutputStream stdin = new BufferedOutputStream(process.getOutputStream());
        stdin.write((">seq\n").getBytes());
        stdin.write((sequence+"\n").getBytes());
        stdin.write((RNAFoldingTools.getDotBracketStringFromPairedSites(pairedSites) +"\n").getBytes());
        stdin.close();
        
        Utils.nullOutput(process.getErrorStream());
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String textline = null;
        double freeEnergy = Double.NaN;
        for(int i = 0 ; (textline = reader.readLine()) != null ; i++)
        {
            if(i == 1)
            {
                String [] split =textline.split("\\s",2);
                freeEnergy = Double.parseDouble(split[1].replaceAll("[\\(\\)\\s]", ""));
            }
        }
        
        reader.close();
        int exitCode = process.waitFor();

        if (exitCode == 0) {
            return freeEnergy;
        }
        else
        {
            return Double.NaN;
        }
    }
}
