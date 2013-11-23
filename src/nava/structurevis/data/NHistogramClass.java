/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.structurevis.data;

import java.awt.Color;
import java.text.DecimalFormat;
import java.util.ArrayList;
import nava.ranking.RankingAnalyses;
import nava.utils.Utils;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class NHistogramClass {
    
    String name;
    Color color;
    Color transparentColor;
    ArrayList<Double> values;
    int nbins;
    double min;
    double max;
    double range;
    int[] bins;
    double[] percs;
    int maxBinCount = 0;
    double median = 0;
    
    public NHistogramClass(String name, Color color, ArrayList<Double> values) {
        this.name = name;
        this.color = color;
        this.transparentColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), 150);
        this.values = values;
    }
    
    public void calculate(double min, double max, int nbins, DataTransform transform) {
        
        if (this.nbins != nbins || this.min != min || this.max != max) {
            this.nbins = nbins;
            this.min = min;
            this.max = max;
            this.range = max - min;
            if(transform != null)
            {
                this.min = 0;
                this.max = 1;
                this.range = 1;
            }
            bins = new int[nbins];
            percs = new double[nbins];
            for (int i = 0; i < values.size(); i++) {
                double x = (values.get(i) - min) / range;
                if(transform != null)
                {
                    x = transform.transform(values.get(i));
                }
                //System.out.println(min+"\t"+max+"\t"+values.get(i));
                int bin = Math.max(Math.min((int) (x * ((double) nbins)), nbins - 1), 0);
                bins[bin]++;
            }
            
            for (int i = 0; i < bins.length; i++) {
                maxBinCount = Math.max(maxBinCount, bins[i]);
            }
            
            for (int i = 0; i < bins.length; i++) {
                percs[i] = (double) bins[i] / (double) values.size();
            }
        }
        
        median = RankingAnalyses.getMedian(values);
    }
    
    public void print() {
        DecimalFormat df = new DecimalFormat("0.000");
        for (int i = 0; i < bins.length; i++) {
            double x = (double) i / (double) bins.length;
           // System.out.println(df.format(x) + "\t" + Utils.nChars('X', bins[i]) + "\t" + percs[i]);
             System.out.println(df.format(x) + "\t" + percs[i]);
        }
    }
    
    public void setMedian(double median)
    {
        this.median = median;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
}
