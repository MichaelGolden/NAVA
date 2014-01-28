/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.vienna;

import java.io.*;
import java.util.ArrayList;
import java.util.Random;
import nava.data.io.IO;
import nava.utils.RNAFoldingTools;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class Utils {
    
    public static int init = (int)(System.currentTimeMillis() % 1000000);
    public static long id = 0;
    
    static Random random = new Random(7482684246432636275L);
    public static String getTemporaryName(String prefix)
    {
        return id+"_"+Math.abs(random.nextInt());
    }
    
    public static File createTemporaryDirectory(String prefix)
    {
        File tempDir = null;   
        while(true)
        {
            tempDir = new File(System.getProperty("java.io.tmpdir") + "/" + prefix+"_"+init+"_"+id + "/");        
            id++;
            if(!tempDir.exists())
            {
                break;
            }
        }
        tempDir.mkdirs();
        return tempDir;
    }
    
    public static void saveDotBracketStructure(int [] pairedSites, File outFile) throws IOException
    {
       BufferedWriter writer = new BufferedWriter(new FileWriter(outFile));
       writer.write(RNAFoldingTools.getDotBracketStringFromPairedSites(pairedSites));
       writer.newLine();
       writer.close();
    }
    
    public static void saveDotBracketStructure(String sequence, String sequenceName, int [] pairedSites, File outFile) throws IOException
    {
       BufferedWriter writer = new BufferedWriter(new FileWriter(outFile));
       writer.write(">"+sequenceName);
       writer.newLine();
       writer.write(sequence);
       writer.newLine();
       writer.write(RNAFoldingTools.getDotBracketStringFromPairedSites(pairedSites));
       writer.newLine();
       writer.close();
    }
    
    public static void saveSequenceAsFASTA(String sequence, String sequenceName, File outFile)
    {
        ArrayList<String> sequences = new ArrayList<>();
        sequences.add(sequence);
        ArrayList<String> sequenceNames = new ArrayList<>();
        sequenceNames.add(sequenceName);
        IO.saveToFASTAfile(sequences, sequenceNames, outFile);
    }
    
   /* public static void nullOutput(final InputStream inputStream) {
        new Thread() {

            @Override
            public void run() {
                try {
                    InputStreamReader input = new InputStreamReader(inputStream);
                    
                    while (input.read() != -1) {
                    }
                    input.close();
                } catch (IOException ex) {
                }
            }
        }.start();
    }*/
    
    public static double calculatedUnpairedGC(Structure s)
    {
        double count = 0;
        double total = 0;
        for(int i = 0 ; i < s.pairedSites.length ; i++)
        {
            if(s.pairedSites[i] == 0)
            {
                if(s.sequence.charAt(i) == 'G' || s.sequence.charAt(i) == 'C')
                {
                    count++;
                }
                total++;
            }
        }
        
        if(total == 0)
        {
            return 0.5;
        }
        else
        {
            return count / total;
        }
    }
    
    public static double calculatedBasePairedGC(Structure s)
    {
        double count = 0;
        double total = 0;
        for(int i = 0 ; i < s.pairedSites.length ; i++)
        {
            if(s.pairedSites[i] != 0)
            {
                if(s.sequence.charAt(i) == 'G' || s.sequence.charAt(i) == 'C')
                {
                    count++;
                }
                total++;
            }
        }
        
        if(total == 0)
        {
            return 0.5;
        }
        else
        {
            return count / total;
        }
    }
    
    public static double calculateGC(String sequence)
    {
        double count = 0;
        for(int i = 0 ; i < sequence.length() ; i++)
        {
            if(sequence.charAt(i) == 'G' || sequence.charAt(i) == 'C')
            {
                count++;
            }
        }
        return count / ((double)sequence.length());
    }
    
    public static void nullOutput(final InputStream inputStream) {
        new Thread() {

            @Override
            public void run() {
                try {

                    BufferedReader buffer = new BufferedReader(new InputStreamReader(inputStream));
                    String textline = null;
                    while ((textline = buffer.readLine()) != null) {
                       // System.err.println(textline);
                    }
                    buffer.close();
                } catch (IOException ex) {
                }
            }
        }.start();
    }
    
    public static String nChars(char c, int n) {
        String ret = "";
        for (int i = 0; i < n; i++) {
            ret += c;
        }
        return ret;
    }
}
