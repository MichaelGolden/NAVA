/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.experimental;

import java.io.*;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class CoevolutionParser {
    public static void main(String [] args) throws Exception
    {
       // File inFile = new File("C:/dev/thesis/jev/200/all3/all.txt");
       // File outFile = new File("C:/dev/thesis/jev/200/all3/jev-formation.clm");
        
        File inFile = new File("C:/dev/thesis/hcv/coevolution/all.txt");
        File outFile = new File("C:/dev/thesis/hcv/coevolution/hcv-formation.clm");
       
     
        BufferedReader buffer = new BufferedReader(new FileReader(inFile));
        BufferedWriter writer = new BufferedWriter(new FileWriter(outFile));
        String textline = null;
        int count = 0;
        int maxi = 0;
        int [] numj = new int[13000];
        while((textline = buffer.readLine()) != null)
        {
            String [] split = textline.split("(\\s)+");
            int i = Integer.parseInt(split[0]);
            int j = Integer.parseInt(split[1]);
            numj[i]++;
            if(Math.abs(i - j) <= 100)
            {
                count++;
                maxi = Math.max(i, maxi);
            }
            double pval = Double.parseDouble(split[4]);
            double r = Double.parseDouble(split[5]);
            if(r >= 1 && Math.abs(i - j) <= 100)
            {
                writer.write(i+"\t"+j+"\t"+pval+"\n");
            }
        }
        System.out.println(count+"\t"+maxi);
        for(int i = 0 ; i < numj.length ; i++)
        {
            //System.out.println(i+"\t"+numj[i]);
        }
        writer.close();
        buffer.close();
    }
}
