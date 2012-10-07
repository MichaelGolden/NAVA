/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.structurevis.data;

import java.text.DecimalFormat;
import java.util.Random;
import nava.utils.Utils;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class Histogram {
        
    double min;
    double max;
    double range;
    
    int [] bins;
    int nbins;
    
    public static Histogram getHistogram(double min, double max, double [] values, int minBins, int maxBins)
    {
       Histogram hist = new Histogram();
       hist.nbins = Math.min(maxBins, Math.max(minBins, (int)Math.sqrt(values.length)));  
       hist.bins = new int[hist.nbins];
       hist.min = min;
       hist.max = max;
       hist.range = max - min;
       
       for(int i = 0 ; i < values.length ; i++)
       {
           double x = (values[i] - hist.min)/hist.range;
           int bin = Math.min((int) (x*((double)hist.nbins)), hist.nbins-1);
           hist.bins[bin]++;
       }
       return hist;
    }
    
    public void print()
    {        
        DecimalFormat df = new DecimalFormat("0.000");
        for(int i = 0 ; i < bins.length ; i++)
        {
            double x = (double)i / (double) bins.length;
            System.out.println(df.format(x) +"\t"+Utils.nChars('X', bins[i]));
        }
    }
    
    public static void main(String [] args)
    {
        Random random = new Random();
        double [] values = new double[1000];
        for(int i = 0 ; i < values.length ; i++)
        {
            values[i] = Math.asin(random.nextDouble()*2 - 1);
        }
        Histogram hist = getHistogram(-Math.PI/2,Math.PI/2,values,2,20);
        hist.print();
    }
}
