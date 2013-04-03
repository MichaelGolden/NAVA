/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.structurevis.data;

import java.awt.Color;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import nava.utils.Utils;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class NHistogram {
    
    public static void main(String[] args) {
        Random random = new Random();
        ArrayList<Double> values1 = new ArrayList<>();
        ArrayList<Double> values2 = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            values1.add(random.nextDouble() * 0.5);
            values2.add(random.nextDouble() * 1);
        }
        
        NHistogram nhist = new NHistogram(0, 1, 20, null);
        nhist.addClass("class 1", Color.lightGray, values1);
        nhist.addClass("class 2", Color.darkGray, values2);
        nhist.calculate();
        
        nhist.print();
    }
    public double min;
    public double max;
    public double range;
    public int nbins;
    public int maxBinCount = 0;
    public int[] finalbins;
    public double maxBinPerc = 0;
    ArrayList<NHistogramClass> classes = new ArrayList<>();
    public DataTransform transform;
    public String title;
    
    public NHistogram(double min, double max, int nbins, DataTransform transform) {
        this.min = min;
        this.max = max;
        this.range = max - min;
        this.nbins = nbins;
        this.transform = transform;
    }
    
    public void add(NHistogramClass nHistogramClass)
    {
        classes.add(nHistogramClass);
    }
    
    public void addClass(String name, Color color, ArrayList<Double> values) {
        classes.add(new NHistogramClass(name, color, values));
    }
    
    public void calculate() {
        /*
        int minSize = Integer.MAX_VALUE;
        for (HistClass hist : classes) {
            minSize = Math.max(minSize, hist.values.size());
        }
        
        nbins = Math.min(maxBins, Math.max(minBins, (int) Math.sqrt(minSize)));
        finalbins = new int[nbins];
        */
        finalbins = new int[nbins];
        
        for (NHistogramClass hist : classes) {
            hist.calculate(min, max, nbins, transform);
            for (int i = 0; i < hist.bins.length; i++) {
                finalbins[i] += hist.bins[i];
                maxBinCount = Math.max(maxBinCount, hist.bins[i]);
                maxBinPerc = Math.max(maxBinPerc, hist.percs[i]);
            }
        }
        
    }
    
    public void print() {
        DecimalFormat df = new DecimalFormat("0.000");
        for (int i = 0; i < finalbins.length; i++) {
            double x = (double) i / (double) finalbins.length;
           // System.out.println(df.format(x) + "\t" + Utils.nChars('X', finalbins[i]));
             System.out.println(df.format(x));
        }
    }
}

