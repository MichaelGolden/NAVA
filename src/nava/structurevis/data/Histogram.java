/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.structurevis.data;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import nava.utils.Utils;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class Histogram {

    public double min;
    public double max;
    public double range;
    public int[] bins;
    public int nbins;
    public int maxBinCount = 0;

    public static Histogram getHistogram(double min, double max, List<Double> values, int minBins, int maxBins) {
        Histogram hist = new Histogram();
        hist.nbins = Math.min(maxBins, Math.max(minBins, (int) Math.sqrt(values.size())));
        hist.bins = new int[hist.nbins];
        hist.min = min;
        hist.max = max;
        hist.range = max - min;

        for (int i = 0; i < values.size(); i++) {
            double x = (values.get(i) - hist.min) / hist.range;
            int bin = Math.max(Math.min((int) (x * ((double) hist.nbins)), hist.nbins - 1), 0);
            hist.bins[bin]++;
        }

        for (int i = 0; i < hist.bins.length; i++) {
            hist.maxBinCount = Math.max(hist.maxBinCount, hist.bins[i]);
        }

        return hist;
    }

    public double getProportionOfMax(int bin) {
        return bins[bin] / ((double) maxBinCount);
    }

    public void print() {
        DecimalFormat df = new DecimalFormat("0.000");
        for (int i = 0; i < bins.length; i++) {
            double x = (double) i / (double) bins.length;
            System.out.println(df.format(x) + "\t" + Utils.nChars('X', bins[i]));
        }
    }

    public static Histogram example() {
        Random random = new Random();
        ArrayList<Double> values = new ArrayList(1000);
        for (int i = 0; i < 1000; i++) {
            values.add(Math.asin(random.nextDouble() * 2 - 1));
        }
        return getHistogram(-Math.PI / 2, Math.PI / 2, values, 2, 20);
    }

    public static ArrayList<Double> getTransformedValues(double min, double max, boolean excludeOutOfRange, DataTransform transform, ArrayList<Double> values) 
    {
        ArrayList<Double> transformedValues = new ArrayList<>(values.size());
        for (int i = 0; i < values.size(); i++) {
            if(!excludeOutOfRange || (values.get(i) >= min && values.get(i) <= max))
            {
                transformedValues.add(transform.transform(values.get(i)));
            }
        }
        return transformedValues;
    }
    
    public static void main(String[] args) {

        Histogram hist = Histogram.example();
        System.out.println(hist.nbins);
        hist.print();
    }
}
