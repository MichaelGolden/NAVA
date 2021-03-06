/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.ranking;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Random;
import org.apache.commons.math3.stat.inference.MannWhitneyUTest;
import org.apache.commons.math3.stat.ranking.NaNStrategy;
import org.apache.commons.math3.stat.ranking.TiesStrategy;

/**
 *
 * @author Michael
 */
public class MyMannWhitney {

    
    public static void main(String [] args)
    {
        System.out.println(StatUtils.getInvCDF(0.05, true));
    }
            
    double ux = 0;
    double uy = 0;
    double nx;
    double ny;
    double N;
    double tieCorrectionFactor = 0;
    Hashtable<Double, Double> groups = new Hashtable<Double, Double>();

    public double getGroup(double value) {
        Double val = groups.get(value);
        if (val != null) {
            return val.doubleValue();
        }

        return 0;
    }
    
    public double getGroup(int value) {
        Double val = groups.get(value);
        if (val != null) {
            return val.doubleValue();
        }

        return 0;
    }

    /*
     * public MyMannWhitney(double[] x, double[] y) { this.nx = x.length;
     * this.ny = y.length; this.N = this.nx + this.ny;
     *
     * ArrayList<Pair> data = new ArrayList<Pair>(); for (int i = 0; i <
     * x.length; i++) { data.add(new Pair(x[i], "x")); }
     *
     * for (int i = 0; i < y.length; i++) { data.add(new Pair(y[i], "y")); }
     *
     * Collections.sort(data); double[] counts = new double[data.size()]; for
     * (int i = 0; i < counts.length; i++) { Pair pairi = data.get(i);
     * groups.put(pairi.val, getGroup(pairi.val) + 1); for (int j = 0; j < i;
     * j++) { Pair pairj = data.get(j);
     *
     * if (!pairi.label.equals(pairj.label) && pairi.val == pairj.val) { // deal
     * with ties counts[i] += 0.5; counts[j] += 0.5; } else if
     * (!pairi.label.equals(pairj.label) && pairi.val > pairj.val) {
     * counts[i]++; } } }
     *
     * for (int i = 0; i < counts.length; i++) { if
     * (data.get(i).label.equals("x")) { ux += counts[i]; } else { uy +=
     * counts[i]; } }
     *
     *
     * tieCorrectionFactor = 0; Enumeration<Double> keys = groups.keys(); while
     * (keys.hasMoreElements()) { double c = groups.get(keys.nextElement());
     * double q = (Math.pow(c, 3) - c) / 12; tieCorrectionFactor += q; } }
     * 
     * 
     */
   /* public MyMannWhitney(double[] x, double[] y) {
        this.nx = x.length;
        this.ny = y.length;
        this.N = this.nx + this.ny;

        ArrayList<Pair> data = new ArrayList<Pair>();
        for (int i = 0; i < x.length; i++) {
            data.add(new Pair(x[i], "x"));
        }

        for (int i = 0; i < y.length; i++) {
            data.add(new Pair(y[i], "y"));
        }

        Collections.sort(data);
        double[] counts = new double[data.size()];
        for (int i = 0; i < counts.length; i++) {
            Pair pairi = data.get(i);
            groups.put(pairi.val, getGroup(pairi.val) + 1);
            for (int j = 0; j < i; j++) {
                Pair pairj = data.get(j);

                if (!pairi.label.equals(pairj.label) && pairi.val == pairj.val) {
                    // deal with ties
                    counts[i] += 0.5;
                    counts[j] += 0.5;
                } else if (!pairi.label.equals(pairj.label) && pairi.val > pairj.val) {
                    counts[i]++;
                }
            }
        }

        for (int i = 0; i < counts.length; i++) {
            if (data.get(i).label.equals("x")) {
                ux += counts[i];
            } else {
                uy += counts[i];
            }
        }


        tieCorrectionFactor = 0;
        Enumeration<Double> keys = groups.keys();
        while (keys.hasMoreElements()) {
            double c = groups.get(keys.nextElement());
            double q = (Math.pow(c, 3) - c) / 12;
            tieCorrectionFactor += q;
        }
    }*/
    
    public double approximatePvalue(double [] x, double [] y)
    {
        Random random = new Random(6912481482045395204L);
        double d = 0;
        double t = 0;
        for(int i = 0 ; i < 1000000 ; i++)
        {
            double x1 = x[random.nextInt(x.length)];
            double y1 = y[random.nextInt(y.length)];
           
            if(x1 > y1)
            {
                d++;
            }
            t++;
        }
        
        return d/t;
    }
    
    public static int direction(double [] x, double [] y)
    {
        Random random = new Random(6912481482045395204L);
        int d = 0;
        for(int i = 0 ; i < 100000 ; i++)
        {
            double x1 = x[random.nextInt(x.length)];
            double y1 = y[random.nextInt(y.length)];
            if(x1 < y1)
            {
                d--;
            }
            else
            if(x1 > y1)
            {
                d++;
            }
        }
        
        if(d < 0)
        {
            return -1;
        }
        else
        {
            return 1;
        }
    }
    
   
    class MannWhitneyValue implements Comparable<MannWhitneyValue>
    {
        double value;
        double randomize;
        
        public MannWhitneyValue(double value, double randomize)
        {
            this.value = value;
            this.randomize = randomize;
        }

        @Override
        public int compareTo(MannWhitneyValue o) {
            if(this.value < o.value)
            {
                return -1;
            }
            else
            if(this.value > o.value)
            {
                return 1;
            }
            else
            if(this.randomize < o.randomize)
            {
                return -1;
            }
            else
            {
                return 1;
            }
            
        }
    }
    
    
    private void compute(ArrayList<Double> xin, ArrayList<Double> yin)
    {
        int removed = 0;
        while(xin.contains(Double.NaN))
        {
            xin.remove(Double.NaN);
            removed++;
        }
        
        while(yin.contains(Double.NaN))
        {
            yin.remove(Double.NaN);
            removed++;
        }       
        
        long seed = 2272749019639563492L;
        for(int i = 0 ; i < xin.size() ; i++)
        {
            seed += xin.get(i);
        }
        for(int i = 0 ; i < yin.size() ; i++)
        {
            seed += yin.get(i);
        }
        
        if(xin.size()+yin.size() % 2 == 0)
        {
            seed = -seed;
        }
        
        ArrayList<MannWhitneyValue> x = new ArrayList<>();
        ArrayList<MannWhitneyValue> y = new ArrayList<>();
    
        Random random = new Random(seed);
        for(int i = 0 ; i < xin.size() ; i++)
        {
            x.add(new MannWhitneyValue(xin.get(i), random.nextDouble()));
        }
        for(int i = 0 ; i < yin.size() ; i++)
        {
             y.add(new MannWhitneyValue(yin.get(i), random.nextDouble()));
        }
                
        this.nx = x.size();
        this.ny = y.size();
        this.N = this.nx + this.ny;

        double[] counts_x = new double[x.size()];
        double[] counts_y = new double[y.size()];
        for (int i = 0; i < x.size(); i++) {
            for (int j = 0; j < y.size(); j++) {
                /*if(x.get(i).compareTo(y.get(j)) > 0)
                {
                    counts_x[i]++;
                }
                else
                {
                    counts_y[j]++;
                }*/
                if(x.get(i).value > y.get(j).value)
                {
                    counts_x[i]++;
                }
                else
                if(x.get(i).value < y.get(j).value)
                {
                    counts_y[j]++;
                }
                else
                if(random.nextBoolean())
                {
                    counts_x[i]++;
                }
                else
                {
                    counts_y[j]++;
                }
            }
        }

        for (int i = 0; i < counts_x.length; i++) {
            ux += counts_x[i];
        }

        for (int i = 0; i < counts_y.length; i++) {
            uy += counts_y[i];
        }

        tieCorrectionFactor = 0;
    }
    
    public MyMannWhitney(ArrayList<Double> xin, ArrayList<Double> yin) {        
       compute(xin, yin);
    }
    
    public MyMannWhitney(double[] x, double[] y) {
        
        ArrayList<Double> xin = new ArrayList<>(x.length);
        ArrayList<Double> yin = new ArrayList<>(y.length);
        for(int i = 0 ; i < x.length ; i++)
        {
            xin.add(x[i]);
        }
        for(int i = 0 ; i < y.length ; i++)
        {
            yin.add(y[i]);
        }
        compute(xin, yin);
    }

    public double getTestStatistic() {
        return Math.max(ux, uy);
    }

    public double getZ() {
        double n = ((nx * ny) / (N * (N - 1)));
        double d = ((Math.pow(N, 3) - N) / 12 - tieCorrectionFactor);

        double variance = Math.sqrt(n * d);
        
        return (ux - (nx * ny / 2)) / variance;
        
        /*double mu = (nx*ny)/2;
        double variance = Math.sqrt((nx*ny*(nx+ny+1))/12);
        return (ux-mu)/variance;*/
    }
}
