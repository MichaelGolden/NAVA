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
public class IncaRNAtion 
{
    private static File PYTHON_PATH = new File("C:/Python27/python.exe");
    private static File SCRIPT_PATH = new File("bin/IncaRNAtion.py");
    
    public static void main(String [] args) throws IOException, InterruptedException
    {
        String dbn = "(((.((((....((((((((((............(((.......)))))))))))))....)))))))((.......................................................))(((((....((((((((.........))))))))...))))).......(((.....)))......................";
        IncaRNAtion inverse = new IncaRNAtion();
        inverse.generateSample(RNAFoldingTools.getPairedSitesFromDotBracketString(dbn), 37, 0.5, 20, 100);
    }
    
    public static boolean isRNASequence(String seq)
    {
        return seq.matches("^[ACGU]+$");
    }
    
    public ArrayList<Structure> generateSample(int [] pairedSites, double tempCelsius, double gcContent, double maxInvalidBasePairPenalty, int samples) throws IOException, InterruptedException
    {        
        File tempDir = Utils.createTemporaryDirectory("incarnation");
        File outFile = new File(tempDir.getAbsolutePath()+File.separatorChar+"structure.dbn");
        BufferedWriter writer = new BufferedWriter(new FileWriter(outFile));
        writer.write(RNAFoldingTools.getDotBracketStringFromPairedSites(pairedSites)+"\n");
        writer.close();
       // python IncaRNAtion -d data.txt -a 0.5 -m 20 -b 5 -no_profile
          // String cmd = "\""+PYTHON_PATH+"\" \""+SCRIPT_PATH+"\"";
        String cmd = "\""+PYTHON_PATH+"\" \""+SCRIPT_PATH+"\" -d \""+outFile.getAbsolutePath()+"\" "+(gcContent >= 0 && gcContent <= 1 ? "-s_gc "+gcContent+" " : "-b ")+samples +" -a 0.5 -no_profile -m " + maxInvalidBasePairPenalty +" -t "+(tempCelsius+273.5);

        Process process = Runtime.getRuntime().exec(cmd);
        Utils.nullOutput(process.getErrorStream());
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String textline = null;
        ArrayList<Structure> sample = new ArrayList<>();
        while((textline = reader.readLine()) != null)
        {
            String [] split = textline.split("(\\s)+", 2);
            if(split[0].length() == pairedSites.length)
            {
                if(isRNASequence(split[0]))
                {
                    Structure s = new Structure();
                    s.pairedSites = pairedSites;
                    s.sequence = split[0];
                    s.tempCelsius = tempCelsius;
                    
                    sample.add(s);
                    //System.out.println(split[1]+"\t"+Utils.calculateGC(split[0])+"\t"+Utils.calculatedBasePairedGC(s));
                }
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
