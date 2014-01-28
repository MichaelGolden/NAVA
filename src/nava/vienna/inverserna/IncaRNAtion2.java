/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.vienna.inverserna;

import nava.vienna.*;
import java.io.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.midi.Sequence;
import nava.utils.RNAFoldingTools;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class IncaRNAtion2 
{
    private static File PYTHON_PATH = new File("C:/Python27/python.exe");
    private static File SCRIPT_PATH = new File("bin/IncaRNAtion.py");
    
    public static void main(String [] args) throws IOException, InterruptedException
    {
        String dbn = "(((.((((....((((((((((............(((.......)))))))))))))....)))))))((.......................................................))(((((....((((((((.........))))))))...))))).......(((.....)))......................";
        IncaRNAtion2 inverse = new IncaRNAtion2();
        inverse.generateSample(RNAFoldingTools.getPairedSitesFromDotBracketString(dbn), 37, 0.5, 20, 100);
    }
    
    public static boolean isRNASequence(String seq)
    {
        return seq.matches("^[ACGU]+$");
    }
    
    public ArrayList<String> generateSample(int [] pairedSites, double tempCelsius, double gcContent, double maxInvalidBasePairPenalty, int samples) throws IOException, InterruptedException
    {        
        File tempDir = Utils.createTemporaryDirectory("vienna.rnafold");
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
        ArrayList<String> sample = new ArrayList<>();
        while((textline = reader.readLine()) != null)
        {
            String [] split = textline.split("(\\s)+", 2);
            if(split[0].length() == pairedSites.length)
            {
                if(isRNASequence(split[0]))
                {                    
                    sample.add(split[0]);
                }
            }
        }
        
        reader.close();
        
  
        int exitCode = process.waitFor();

        if (exitCode == 0) {
            return sample;
        }
        else
        {
            return null;
        }
    }
}
