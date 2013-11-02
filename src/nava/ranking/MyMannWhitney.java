/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.ranking;

import java.util.ArrayList;
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
    
    double pvalue = Double.NaN;
    double uscore = Double.NaN;
    double zscore = Double.NaN;
    int direction = 0;
    double approxp = Double.NaN;
    public MyMannWhitney(ArrayList<Double> x, ArrayList<Double> y) {
       /* this.nx = x.size();
        this.ny = y.size();
        this.N = this.nx + this.ny;
        
        int removed = 0;
        while(x.contains(Double.NaN))
        {
            x.remove(Double.NaN);
            removed++;
        }
        
        while(y.contains(Double.NaN))
        {
            y.remove(Double.NaN);
            removed++;
        }       

        for (int i = 0; i < x.size(); i++) {
            groups.put(x.get(i), getGroup(x.get(i)) + 1);
        }

        for (int i = 0; i < y.size(); i++) {
            groups.put(y.get(i), getGroup(y.get(i)) + 1);
        }

        double[] counts_x = new double[x.size()];
        double[] counts_y = new double[y.size()];
        for (int i = 0; i < x.size(); i++) {
            for (int j = 0; j < y.size(); j++) {
                if (x.get(i) == y.get(j)) {
                    // deal with ties
                    counts_x[i] += 0.5;
                    counts_y[j] += 0.5;
                } else if (x.get(i) > y.get(j)) {
                    counts_x[i]++;
                } else {
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
        Enumeration<Double> keys = groups.keys();
        while (keys.hasMoreElements()) {
            double c = groups.get(keys.nextElement());
            double q = (Math.pow(c, 3) - c) / 12;
            tieCorrectionFactor += q;
        }*/
        int removed = 0;
        while(x.contains(Double.NaN))
        {
            x.remove(Double.NaN);
            removed++;
        }
        
        while(y.contains(Double.NaN))
        {
            y.remove(Double.NaN);
            removed++;
        }  
        
        MannWhitneyUTest test = new MannWhitneyUTest(NaNStrategy.REMOVED, TiesStrategy.RANDOM);
        double [] x1 = new double[x.size()];
        double [] y1 = new double[y.size()];
        for(int i = 0 ; i < x.size() ; i++)
        {
            x1[i] = x.get(i);
        }
        for(int i = 0 ; i < y.size() ; i++)
        {
            y1[i] = y.get(i);
        }
        
        
        if(x1.length == 0 || y1.length == 0)
        {
            
        }
        else
        {        
            pvalue = test.mannWhitneyUTest(x1, y1);
            uscore = test.mannWhitneyU(x1, y1);
            zscore = -StatUtils.getInvCDF(pvalue/2, true);
            approxp = approximatePvalue(x1,y1);
            direction = direction(x1,y1);
        }
    }
    
    public MyMannWhitney(double[] x, double[] y) {
        /*this.nx = x.length;
        this.ny = y.length;
        this.N = this.nx + this.ny;

        for (int i = 0; i < x.length; i++) {
            groups.put(x[i], getGroup(x[i]) + 1);
        }

        for (int i = 0; i < y.length; i++) {
            groups.put(y[i], getGroup(y[i]) + 1);
        }

        double[] counts_x = new double[x.length];
        double[] counts_y = new double[y.length];
        for (int i = 0; i < x.length; i++) {
            for (int j = 0; j < y.length; j++) {
                if (x[i] == y[j]) {
                    // deal with ties
                    counts_x[i] += 0.5;
                    counts_y[j] += 0.5;
                } else if (x[i] > y[j]) {
                    counts_x[i]++;
                } else {
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
        Enumeration<Double> keys = groups.keys();
        while (keys.hasMoreElements()) {
            double c = groups.get(keys.nextElement());
            double q = (Math.pow(c, 3) - c) / 12;
            tieCorrectionFactor += q;
        }
        
        * */
           MannWhitneyUTest test = new MannWhitneyUTest(NaNStrategy.REMOVED, TiesStrategy.RANDOM);

        pvalue = test.mannWhitneyUTest(x, y);
        uscore = test.mannWhitneyU(x, y);
        zscore = -StatUtils.getInvCDF(pvalue/2, true);
        approxp = approximatePvalue(x,y);
        direction = direction(x,y);
    }

    public double getTestStatistic() {
        return Math.max(ux, uy);
    }

    static double min = 1;
    public double getZ() {
      /*double n = ((nx * ny) / (N * (N - 1)));
        double d = ((Math.pow(N, 3) - N) / 12 - tieCorrectionFactor);

        double variance = Math.sqrt(n * d);
        //System.out.println(ux + "\t" + nx + "\t" + ny + "\t" + tieCorrectionFactor + "\t" + n + "\t" + d + "\t" + variance);
  
  
        System.out.println(pvalue+"\t"+zscore+"\t"+uscore+"\t"+(direction*zscore));
        return (ux - (nx * ny / 2)) / variance;*/
        /*System.out.println(pvalue+"\t"+zscore+"\t"+direction);
        if(pvalue > 0)
        {
            min = Math.min(pvalue, min);
        }*/
        if(pvalue < 1.1102230246251565E-16)
        {
            zscore = -StatUtils.getInvCDF(1.1102230246251565E-16/2, true);
            System.out.println("*");
        }
        System.out.println(zscore+"\t"+pvalue+"\t"+approxp);
        return (direction*zscore);
    }
}
