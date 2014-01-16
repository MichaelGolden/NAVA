/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.experimental;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import nava.data.io.CsvReader;
import nava.data.io.FileImport;
import nava.data.io.FileImport.ParserException;
import nava.data.types.DataType;
import nava.data.types.SecondaryStructureData;
import nava.ranking.RankingAnalyses;
import nava.structurevis.DistanceMatrix;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.stat.correlation.SpearmansCorrelation;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class CorrelatedSitesTest {

    class Input {

        MappedData data;
        int[] mappedPairedSites;
        int offset;

        public Input(MappedData data, int[] mappedPairedSites, int offset) {
            this.data = data;
            this.mappedPairedSites = mappedPairedSites;
            this.offset = offset;
        }
    }

    public class Output {

        int offset;
        double pval;
    }

    public List<Output> processInputs(List<Input> inputs, int numThreads)
            throws InterruptedException, ExecutionException {

        int threads = numThreads;
        ExecutorService service = Executors.newFixedThreadPool(threads);

        List<Future<Output>> futures = new ArrayList<>();
        for (final Input input : inputs) {
            Callable<Output> callable = new Callable<Output>() {

                public Output call() throws Exception {
                    Output output = new Output();
                    output.offset = input.offset;
                    output.pval = calculatePairedSitesCorrelation(input.data, input.mappedPairedSites, input.offset)[0];
                    return output;
                }
            };
            futures.add(service.submit(callable));
        }

        service.shutdown();

        List<Output> outputs = new ArrayList<>();
        for (Future<Output> future : futures) {
            outputs.add(future.get());
        }
        return outputs;
    }

    static Random random = new Random(367080280244348720L);
    public static double[] calculatePairedSitesCorrelation(MappedData data, int[] mappedPairedSites, int offset) {
        ArrayList<Double> sites1 = new ArrayList<>();
        ArrayList<Double> sites2 = new ArrayList<>();
        for (int i = 0; i < mappedPairedSites.length; i++) {
            int x = i;
            int y = mappedPairedSites[x] - 1;
            if (y >= 0) {
                int xo = (x + offset) % data.values.length;
                int yo = (y + offset) % data.values.length;
                if (data.used[xo] && data.used[yo]) {
                    boolean switchSites = random.nextBoolean();
                    if(switchSites)
                    {
                        sites2.add(data.values[xo]);
                        sites1.add(data.values[yo]);
                    }
                    else
                    {
                        sites1.add(data.values[xo]);
                        sites2.add(data.values[yo]);
                    }
                }
            }
        }

        double[][] xy = new double[sites1.size()][2];
        for (int i = 0; i < sites1.size(); i++) {
            xy[i][0] = sites1.get(i);
            xy[i][1] = sites2.get(i);
        }

        //PearsonsCorrelation pearsons = new PearsonsCorrelation(new Array2DRowRealMatrix(xy));
        //double[] ret = {pearsons.getCorrelationMatrix().getEntry(0, 1), pearsons.getCorrelationPValues().getEntry(0, 1)}; 
        SpearmansCorrelation spearmansCorrelation = new SpearmansCorrelation(new Array2DRowRealMatrix(xy));
        double[] ret = {spearmansCorrelation.getCorrelationMatrix().getEntry(0, 1), spearmansCorrelation.getRankCorrelation().getCorrelationPValues().getEntry(0, 1)};
        return ret;
    }

    class PairedSitesPermutationTestResult {

        double r;
        double pval;
        double count;
        double total;
        double permutation;

        public PairedSitesPermutationTestResult(double r, double pval, double count, double total, double permutation) {
            this.r = r;
            this.pval = pval;
            this.count = count;
            this.total = total;
            this.permutation = permutation;
        }
    }

    public PairedSitesPermutationTestResult pairedSitesCorrelationPermutationTest(MappedData data, int[] mappedPairedSites, int threads) throws InterruptedException, ExecutionException {
        double count = 0;
        double total = 0;
        double[] result = calculatePairedSitesCorrelation(data, mappedPairedSites, 0);
        double actual = result[0];
        double pvalue = result[1];

        ArrayList<Input> inputs = new ArrayList<>(mappedPairedSites.length);
        for (int i = 0; i < mappedPairedSites.length; i++) {
            inputs.add(new Input(data, mappedPairedSites, i));
        }
        List<Output> outputs = processInputs(inputs, threads);
        for (int i = 0; i < outputs.size(); i++) {
            double permuted = outputs.get(i).pval;
            if (permuted >= actual) {
                count++;
            }
            total++;
        }

        System.out.println(actual + "\t" + pvalue + "\t" + count + "\t" + total + "\t" + (count / total));
        return new PairedSitesPermutationTestResult(actual, pvalue, count, total, (count / total));
    }

    public void realDistanceTest(MappedData data, int[] mappedPairedSites) {
        int binSize = 4;
        int radius = 1000;
        DistanceMatrix distanceMatrix = new DistanceMatrix(binSize, radius, mappedPairedSites);
        distanceMatrix.computeFloydWarshall();

        Random random = new Random(8563597266566844151L);

        ArrayList<Double> sitesNear1 = new ArrayList<>();
        ArrayList<Double> sitesNear2 = new ArrayList<>();

        ArrayList<Double> sitesNear1Ex = new ArrayList<>();
        ArrayList<Double> sitesNear2Ex = new ArrayList<>();

        ArrayList<Double> sitesFar1 = new ArrayList<>();
        ArrayList<Double> sitesFar2 = new ArrayList<>();

        for (int i = 0; i < 1000000; i++) {
            int x = random.nextInt(mappedPairedSites.length);
            int y = random.nextInt(mappedPairedSites.length);

            if (data.used[x] && data.used[y]) {

                int floydDistance = distanceMatrix.getDistance(x, y);
                int distance = Math.abs(x - y);
                if (floydDistance < 100) {
                    sitesNear1.add(data.values[x]);
                    sitesNear2.add(data.values[y]);
                    if (distance >= 100) {
                        sitesNear1Ex.add(data.values[x]);
                        sitesNear2Ex.add(data.values[y]);
                    }
                } else {
                    sitesFar1.add(data.values[x]);
                    sitesFar2.add(data.values[y]);
                }
            }
        }

        double[] ret1 = calculateCorrelation(sitesNear1, sitesNear2);
        System.out.println(ret1[0] + "\t" + ret1[1] + "\t" + sitesNear1.size());
        double[] ret3 = calculateCorrelation(sitesNear1Ex, sitesNear2Ex);
        System.out.println(ret3[0] + "\t" + ret3[1] + "\t" + sitesNear1Ex.size());
        double[] ret2 = calculateCorrelation(sitesFar1, sitesFar2);
        System.out.println(ret2[0] + "\t" + ret2[1] + "\t" + sitesFar1.size());


    }

    public void realDistanceTest2(MappedData data, int[] mappedPairedSites) {
        int binSize = 4;
        int radius = 1000;
        DistanceMatrix distanceMatrix = new DistanceMatrix(binSize, radius, mappedPairedSites);
        distanceMatrix.computeFloydWarshall();

        Random random = new Random(8563597266566844151L);

        ArrayList<Double> sitesFloyd1 = new ArrayList<>();
        ArrayList<Double> sitesFloyd2 = new ArrayList<>();

        ArrayList<Double> sitesNormal1 = new ArrayList<>();
        ArrayList<Double> sitesNormal2 = new ArrayList<>();

        for (int i = 0; i < 1000000; i++) {
            int x = random.nextInt(mappedPairedSites.length);
            int y = random.nextInt(mappedPairedSites.length);

            if (Math.abs(x - y) >= radius) {
                continue;
            }

            int normalDistance = Math.abs(x - y);
            int floydDistance = distanceMatrix.getDistance(x, y);

            int xr = random.nextInt(mappedPairedSites.length);
            int yr = xr + normalDistance;

            if (data.used[x] && data.used[y] && yr < mappedPairedSites.length && data.used[xr] && data.used[yr]) {

                //int distance = Math.abs(x - y);
                sitesFloyd1.add(data.values[x]);
                sitesFloyd2.add(data.values[y]);

                sitesNormal1.add(data.values[xr]);
                sitesNormal2.add(data.values[yr]);
            }
        }

        double[] ret1 = calculateCorrelation(sitesFloyd1, sitesFloyd2);
        System.out.println(ret1[0] + "\t" + ret1[1] + "\t" + sitesFloyd1.size());
        double[] ret3 = calculateCorrelation(sitesNormal1, sitesNormal2);
        System.out.println(ret3[0] + "\t" + ret3[1] + "\t" + sitesNormal1.size());
        //double[] ret2 = calculateCorrelation(sitesFar1, sitesFar2);
        //System.out.println(ret2[0] + "\t" + ret2[1] + "\t" + sitesFar1.size());


    }

    public static void distanceCorrelationTest(MappedData data, int[] mappedPairedSites) {
        short[][] distanceMatrix = Dijkstra.getDistanceMatrix(mappedPairedSites);
        
            double realR = 0;
            double realP = 0;
            double count = 0;
            double total = 0;
            Random random = new Random(2480910481441575555L);
            
            for (int i = 0; i < 1000 ; i++) {
               // double [] ret1 = distanceCorrelationTestOffset(data,mappedPairedSites,distanceMatrix,i,false);
                //double [] ret1 = distanceCorrelationTestOffset(data,mappedPairedSites,distanceMatrix,i, i == 0,false,random);
                double [] ret1 = distanceCorrelationTestOffset(data,mappedPairedSites,distanceMatrix,0, i == 0,true,random);
                if(i == 0)
                {
                    realR = ret1[0];
                    realP = ret1[1];
                }
                double perm = ret1[0];
                count += perm >= realR ? 1 : 0;
                total += 1;
                //System.out.println("distanceCorrelationTest\t" + ret1[0] + "\t" + ret1[1]+"\t"+count+"\t"+total+"\t"+(count/total) );
            }
            System.out.println("correlation \t" + realR + "\t" + realP+"\t"+count+"\t"+total+"\t"+(count/total) );

        //double[] ret1 = calculateCorrelation(distances, squaredDistances);
       
    }
    
    public static double [] distanceCorrelationTestOffset(MappedData data, int[] mappedPairedSites, short [][] distanceMatrix, int offset, boolean real, boolean useRandom, Random random2) {
        Random random = new Random(701964140144225913L);
        //Random random2 = new Random();
        ArrayList<Double> distances = new ArrayList<>();
        ArrayList<Double> squaredDistances = new ArrayList<>();
        for (int i = 0; i < 10000 ; i++) {
            int x = random.nextInt(mappedPairedSites.length);
            int y = random.nextInt(mappedPairedSites.length);
            int xr = x;
            int yr = y;
            if(!real && useRandom)
            {
                xr = random2.nextInt(mappedPairedSites.length);
                yr = xr + Math.abs(x-y);
            }
            if (data.used[(xr+offset)%mappedPairedSites.length] && data.used[(yr+offset)%mappedPairedSites.length]) {
                double diff = Math.abs(data.values[(xr+offset)%mappedPairedSites.length] - data.values[(yr+offset)%mappedPairedSites.length]);
                if(real)                    
                {
                   // System.out.println(x+"\t"+y+"\t"+Math.abs(x-y) +"\t"+distanceMatrix[x][y]+"\t"+diff);
                }
                distances.add((double) distanceMatrix[x][y]);
                squaredDistances.add(diff);
            }
        }

        return calculateCorrelation(distances, squaredDistances);
    }

    public class BinMatrix {

        HashSet<XY> set = new HashSet<>();
        Bin[][] bins;
        int gridSizeNormal;
        int gridSizeFloyd;
        int n;
        int m;

        public BinMatrix(int gridSizeNormal, int gridSizeFloyd, int n, int m) {
            bins = new Bin[n][m];
            this.gridSizeNormal = gridSizeNormal;
            this.gridSizeFloyd = gridSizeFloyd;
            this.n = n;
            this.m = m;
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
                    bins[i][j] = new Bin();
                    bins[i][j].normalDistStartRangeInclusive = i * gridSizeNormal;
                    bins[i][j].normalDistEndRangeExclusive = (i + 1) * gridSizeNormal;
                    bins[i][j].floydDistStartRangeInclusive = j * gridSizeFloyd;
                    bins[i][j].floydDistEndRangeExclusive = (j + 1) * gridSizeFloyd;
                }
            }
        }

        public int getBinCount(int normalDistance, int floydDistance) {
            return bins[normalDistance / gridSizeNormal][floydDistance / gridSizeFloyd].x.size();
        }

        public void put(int normalDistance, int floydDistance, int xpos, int ypos, double x, double y) {
            bins[normalDistance / gridSizeNormal][floydDistance / gridSizeFloyd].x.add(x);
            bins[normalDistance / gridSizeNormal][floydDistance / gridSizeFloyd].y.add(y);
            set.add(new XY(xpos, ypos));
        }

        public boolean contains(int xpos, int ypos) {
            return set.contains(new XY(xpos, ypos));
        }

        public String getCorrelationMatrixString() {
            String ret = "";
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
                    if (bins[i][j].x.size() > 2) {
                        ret += calculateCorrelation(bins[i][j].x, bins[i][j].y)[0] + "\t";
                    } else {
                        ret += "NA" + "\t";
                    }
                }
                ret += "\n";
            }
            return ret;
        }

        public String getCountMatrixString() {
            String ret = "";
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
                    if (bins[i][j].x.size() > 2) {
                        ret += bins[i][j].x.size() + "\t";
                    } else {
                        ret += "NA" + "\t";
                    }
                }
                ret += "\n";
            }
            return ret;
        }

        @Override
        public String toString() {
            String ret = "";
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
                    double correlation = -2;
                    if (bins[i][j].x.size() > 2) {
                        correlation = calculateCorrelation(bins[i][j].x, bins[i][j].y)[0];
                    }
                    ret += (i * gridSizeNormal) + "-" + ((i + 1) * gridSizeNormal) + "\t" + (j * gridSizeFloyd) + "-" + ((j + 1) * gridSizeFloyd) + "\t" + bins[i][j].x.size() + "\t" + correlation + "\n";

                }
            }

            return ret;
        }
    }

    public class Bin {

        int normalDistStartRangeInclusive;
        int normalDistEndRangeExclusive;
        int floydDistStartRangeInclusive;
        int floydDistEndRangeExclusive;
        ArrayList<Double> x = new ArrayList<>();
        ArrayList<Double> y = new ArrayList<>();
    }

    public class XY {

        int x;
        int y;

        public XY(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final XY other = (XY) obj;
            if (this.x != other.x) {
                return false;
            }
            if (this.y != other.y) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 29 * hash + this.x;
            hash = 29 * hash + this.y;
            return hash;
        }
    }

    public void realDistanceTest3(MappedData data, int[] mappedPairedSites) {
        int binSize = 1;
        int radius = 200;
        DistanceMatrix distanceMatrix = new DistanceMatrix(binSize, radius, mappedPairedSites);
        distanceMatrix.computeFloydWarshallParallelised();
        distanceMatrix.computeFloydWarshallParallelised();
        distanceMatrix.computeFloydWarshallParallelised();
        // distanceMatrix.computeFloydWarshallParallelised();
        // distanceMatrix.computeFloydWarshallParallelised();
        for (int i = 0; i < mappedPairedSites.length; i++) {
            ArrayList<Double> values = new ArrayList<>();
            for (int j = 0; j < mappedPairedSites.length; j++) {
                values.add((double) distanceMatrix.getDistance(i, j));
            }
            System.out.println(i + "\t" + RankingAnalyses.getMedian(values));
        }

        Random random = new Random(8563597266566844151L);

        int distance = 20;
        //int distance 
        int npoints = 20;
        int mpoints = 20;
        int gridSizeNormal = distance / npoints;
        int gridSizeFloyd = distance / mpoints;
        System.out.println(gridSizeNormal + "\t" + gridSizeFloyd);
        BinMatrix binMatrix = new BinMatrix(gridSizeNormal, gridSizeFloyd, npoints, mpoints);
        //int distance = npoints * gridSizeNormal;
        int maxIterations = 1000000000;
        for (int i = 0; i < maxIterations; i++) {
            if (i % 10000000 == 0) {
                System.out.println((((double) i / (double) maxIterations) * 100) + "%");
            }
            int x = random.nextInt(mappedPairedSites.length);
            int y = x + random.nextInt(distance);

            int normalDistance = Math.abs(x - y);
            if (normalDistance > 0 && normalDistance < distance && y < mappedPairedSites.length) {
                int floydDistance = distanceMatrix.getDistance(x, y);
                if (floydDistance < distance) {
                    if (data.used[x] && data.used[y] && binMatrix.getBinCount(normalDistance, floydDistance) < 5000) {

                        if (!binMatrix.contains(x, y)) {
                            binMatrix.put(normalDistance, floydDistance, x, y, data.values[x], data.values[y]);
                            // binMatrix.put(normalDistance, floydDistance, x, y, normalDistance, Math.abs(data.values[x]- data.values[y]));
                        }

                    }
                }
            }
        }

        System.out.println(binMatrix.getCountMatrixString());
        System.out.println(binMatrix.getCorrelationMatrixString());


    }

    public static double[] calculateCorrelation(ArrayList<Double> x, ArrayList<Double> y) {
        double[][] xy = new double[x.size()][2];
        for (int i = 0; i < x.size(); i++) {
            xy[i][0] = x.get(i);
            xy[i][1] = y.get(i);
        }

        SpearmansCorrelation spearmansCorrelation = new SpearmansCorrelation(new Array2DRowRealMatrix(xy));

        double[] ret = {spearmansCorrelation.getCorrelationMatrix().getEntry(0, 1), spearmansCorrelation.getRankCorrelation().getCorrelationPValues().getEntry(0, 1)};
        return ret;
    }

    public static void main(String[] args) throws IOException, ParserException, Exception {
 
      int threads = 4;
        /*
       File referenceAlignment = new File("C:/dev/thesis/hcv/hcv_genotypes2_aligned.fas");
       File structureAlignment = new File("C:/dev/thesis/hcv/hcv_genotypes2_aligned.dbn");
       File dataAlignment = new File("C:/dev/thesis/hcv/1a/100/hcv1a_all_100_aligned.fas");
       File csvFile = new File("C:/dev/thesis/hcv/1a/100/hcv1a_all_100_aligned.csv");
       String name = "hcv1a";
       boolean codon = false;
       int column = 1;
       * */
       
      /* File referenceAlignment = new File("C:/dev/thesis/hcv/hcv_genotypes2_aligned.fas");
       File structureAlignment = new File("C:/dev/thesis/hcv/hcv_genotypes2_aligned.dbn");
       File dataAlignment = new File("C:/dev/thesis/hcv/1/100/hcv1_all_100_aligned.fas");
       File csvFile = new File("C:/dev/thesis/hcv/1/100/hcv1_all_100_aligned.csv");
       String name = "hcv1";
       boolean codon = false;
       int column = 1;*/
        
    /* File referenceAlignment = new File("C:/dev/thesis/hcv/hcv_genotypes2_aligned.fas");
       File structureAlignment = new File("C:/dev/thesis/hcv/hcv_genotypes2_aligned.dbn");
       File dataAlignment = new File("C:/dev/thesis/hcv/1b/100/hcv1b_all_100_aligned.fas");
       File csvFile = new File("C:/dev/thesis/hcv/1b/100/hcv1b_all_100_aligned.csv");
       String name = "hcv1b";
       boolean codon = false;
       int column = 1;*/
      
     /*File referenceAlignment = new File("C:/dev/thesis/hcv/hcv_genotypes2_aligned.fas");
       File structureAlignment = new File("C:/dev/thesis/hcv/hcv_genotypes2_aligned.dbn");
       File dataAlignment = new File("C:/dev/thesis/hcv/2/100/hcv2_all_100_aligned_edit.fas");
       File csvFile = new File("C:/dev/thesis/hcv/2/100/hcv2_all_100_aligned_edit.csv");
       String name = "hcv2";
       boolean codon = false;
       int column = 1;*/
        
      /*File referenceAlignment = new File("C:/dev/thesis/hcv/hcv_genotypes2_aligned.fas");
       File structureAlignment = new File("C:/dev/thesis/hcv/hcv_genotypes2_aligned.dbn");
       File dataAlignment = new File("C:/dev/thesis/hcv/6/100/hcv6_all_100_aligned.fas");
       File csvFile = new File("C:/dev/thesis/hcv/6/100/hcv6_all_100_aligned.csv");
       String name = "hcv6";
       boolean codon = false;
       int column = 1;
      */
      
        /*File referenceAlignment = new File("C:/dev/thesis/hiv_full/hiv_not_siv_full_aligned.fas");
        File structureAlignment =  new File("C:/dev/thesis/hiv_full/hiv_not_siv_full_aligned.dbn");
        File csvFile = new File("C:/dev/thesis/hiv_full/hiv1/100/hiv1_all_100_aligned.csv");
        File dataAlignment = new File("C:/dev/thesis/hiv_full/hiv1/100/hiv1_all_100_aligned.fas");
         String name = "hiv1";
        boolean codon = false;
        int column = 1;*/
        
        /*File referenceAlignment = new File("C:/dev/thesis/hiv_full/hiv_not_siv_full_aligned.fas");
        File structureAlignment =  new File("C:/dev/thesis/hiv_full/hiv_not_siv_full_aligned.dbn");
        File csvFile = new File("C:/dev/thesis/hiv_full/1b/100/hiv1b_all_100_aligned.csv");
        File dataAlignment = new File("C:/dev/thesis/hiv_full/1b/100/hiv1b_all_100_aligned.fas");
         String name = "hiv1b";
        boolean codon = false;
        int column = 1;*/
        
      /* File referenceAlignment = new File("C:/dev/thesis/hiv_full/hiv_not_siv_full_aligned.fas");
        File structureAlignment =  new File("C:/dev/thesis/hiv_full/hiv_not_siv_full_aligned.dbn");
        File csvFile = new File("C:/dev/thesis/hiv_full/1c/100/hiv1c_all_100_aligned.csv");
        File dataAlignment = new File("C:/dev/thesis/hiv_full/1c/100/hiv1c_all_100_aligned.fas");
         String name = "hiv1c";
        boolean codon = false;
        int column = 1;
        */
      
      /* File referenceAlignment = new File("C:/dev/thesis/hiv_full/hiv_not_siv_full_aligned.fas");
        File structureAlignment =  new File("C:/dev/thesis/hiv_full/hiv_not_siv_full_aligned.dbn");
        File csvFile = new File("C:/dev/thesis/hiv_full/1d/100/hiv1d_all_100_aligned.csv");
        File dataAlignment = new File("C:/dev/thesis/hiv_full/1d/100/hiv1d_all_100_aligned.fas");
         String name = "hiv1d";
        boolean codon = false;
        int column = 1;*/
        
        /* File referenceAlignment = new File("C:/dev/thesis/hiv_full/hiv_not_siv_full_aligned.fas");
        File structureAlignment =  new File("C:/dev/thesis/hiv_full/hiv_not_siv_full_aligned.dbn");
        File csvFile = new File("C:/dev/thesis/hiv_full/2/100/hiv2_all_100_aligned.csv");
        File dataAlignment = new File("C:/dev/thesis/hiv_full/2/100/hiv2_all_100_aligned.fas");
         String name = "hiv2";
        boolean codon = false;
        int column = 1;*/
                
                
       /*File referenceAlignment = new File("C:/dev/thesis/dengue/dengue-alignment.fas");
        File structureAlignment = new File("C:/dev/thesis/dengue/dengue-alignment.dbn");
      File csvFile = new File("C:/dev/thesis/dengue/100/dengue_all_100_aligned_full.csv");
       File dataAlignment = new File("C:/dev/thesis/dengue/100/dengue_all_100_aligned.fas");
       String name = "dengue";
     boolean codon = false;
      int column = 1;*/
        
      /*
      File referenceAlignment = new File("C:/dev/thesis/dengue/dengue-alignment.fas");
        File structureAlignment = new File("C:/dev/thesis/dengue/dengue-alignment.dbn");
      File csvFile = new File("C:/dev/thesis/dengue1/100/dengue1_all_100_aligned.csv");
       File dataAlignment = new File("C:/dev/thesis/dengue1/100/dengue1_all_100_aligned.fas");
       String name = "dengue2";
     boolean codon = false;
      int column = 1;*/
      
       /*File referenceAlignment = new File("C:/dev/thesis/dengue/dengue-alignment.fas");
        File structureAlignment = new File("C:/dev/thesis/dengue/dengue-alignment.dbn");
      File csvFile = new File("C:/dev/thesis/dengue4/100/dengue4_all_100_aligned.csv");
       File dataAlignment = new File("C:/dev/thesis/dengue4/100/dengue4_all_100_aligned.fas");
       String name = "dengue4";
     boolean codon = false;
      int column = 1;*/
      /*File referenceAlignment = new File("C:/dev/thesis/bvdv/bvdv_all_aligned.fas");
      File structureAlignment = new File("C:/dev/thesis/bvdv/bvdv_all_aligned.dbn");
      File dataAlignment = new File("C:/dev/thesis/bvdv/100/all_100_aligned.fas");
       File csvFile = new File("C:/dev/thesis/bvdv/100/all_100_aligned.csv");
      String name = "bvdv";
      boolean codon = false;
      int column = 1;*/
      
      /* File referenceAlignment = new File("C:/dev/thesis/westnile/westnile_all_200_aligned.fas");
       File structureAlignment =  new File("C:/dev/thesis/westnile/westnile_all_200_aligned.dbn");
        File csvFile = new File("C:/dev/thesis/westnile/100/westnile_all_100_aligned.csv");
        File dataAlignment = new File("C:/dev/thesis/westnile/100/westnile_all_100_aligned.fas");
        String name = "westnile";
        boolean codon = false;
        int column = 1;*.
        
      
       /* File referenceAlignment = new File("C:/dev/thesis/hcv/hcv_genotypes2_aligned.fas");
        File structureAlignment = new File("C:/dev/thesis/hcv/hcv_genotypes2_aligned.dbn");
        File dataAlignment = new File("C:/dev/thesis/hcv/1a/300/hcv1a_polyprotein_300_aligned.fas");
        File csvFile = new File("C:/dev/thesis/hcv/1a/300/hcv1a_polyprotein_300_aligned.csv");
       String name = "hcv1a";
        boolean codon = true;
        int column = 1;*/
        
       
        // File referenceAlignment = new File("C:/dev/thesis/hcv/hcv_genotypes2_aligned.fas");
        // File structureAlignment = new File("C:/dev/thesis/hcv/hcv_genotypes2_aligned.dbn");
        // File dataAlignment = new File("C:/dev/thesis/hcv/1b/300/hcv1b_polyprotein_300_aligned.fas");
        //File csvFile = new File("C:/dev/thesis/hcv/1b/300/hcv1b_polyprotein_300_aligned.csv");
        // String name = "hcv1b";
        //  boolean codon = true;

      //  File referenceAlignment = new File("C:/dev/thesis/hcv/hcv_genotypes2_aligned.fas");
      //  File structureAlignment = new File("C:/dev/thesis/hcv/hcv_genotypes2_aligned.dbn");
      //   File dataAlignment = new File("C:/dev/thesis/hcv/6/300/hcv6_polyprotein_300_aligned.fas");
      //   File csvFile = new File("C:/dev/thesis/hcv/6/300/hcv6_polyprotein_300_aligned.csv");
       //  String name = "hcv6";
       // boolean codon = true;
       // int column = 1;

        //  File referenceAlignment = new File("C:/dev/thesis/hcv/hcv_genotypes2_aligned.fas");
        //  File structureAlignment = new File("C:/dev/thesis/hcv/hcv_genotypes2_aligned.dbn");
        // File dataAlignment = new File("C:/dev/thesis/hcv/1b/300/hcv1b_all_300_aligned.fas_norm");
        // File csvFile = new File("C:/dev/thesis/hcv/1b/300/hcv1b_all_300_aligned.fas.rates.csv");
        // String name = "hcv1b";
        //  boolean codon = false;
        // int column = 1;

        //File referenceAlignment = new File("C:/dev/thesis/hcv/hcv_genotypes2_aligned.fas");
        // File structureAlignment = new File("C:/dev/thesis/hcv/hcv_genotypes2_aligned.dbn");
        // File dataAlignment = new File("C:/dev/thesis/hcv/2/300/hcv2_all_300_aligned.fas_norm");
        //File csvFile = new File("C:/dev/thesis/hcv/2/300/hcv2_all_300_aligned.fas.rates.csv");
        // String name = "hcv2";
        // boolean codon = false;
        //int column = 0;
      

        //  File referenceAlignment = new File("C:/dev/thesis/hcv/hcv_genotypes2_aligned.fas");
        //  File structureAlignment = new File("C:/dev/thesis/hcv/hcv_genotypes2_aligned.dbn");
        // File dataAlignment = new File("C:/dev/thesis/hcv/6/300/hcv6_all_300_aligned.fas_norm");
        // File csvFile = new File("C:/dev/thesis/hcv/6/300/hcv6_all_300_aligned.fas.rates.csv");
        //String name = "hcv6";
        //boolean codon = false;
        //int column = 0;
        //File referenceAlignment = new File("C:/dev/thesis/hcv/hcv_genotypes2_aligned.fas");
       // File structureAlignment = new File("C:/dev/thesis/hcv/hcv_genotypes2_aligned.dbn");
       // File dataAlignment = new File("C:/dev/thesis/hcv/1/300/hcv1_all_300_aligned.fas_norm");
      //  File csvFile = new File("C:/dev/thesis/hcv/1/300/hcv1_all_300_aligned.fas.rates.csv");
       // String name = "hcv1";
       // boolean codon = false;
       // int column = 1;


        //File referenceAlignment = new File("C:/dev/thesis/hcv/hcv_genotypes2_aligned.fas");
        // File structureAlignment = new File("C:/dev/thesis/hcv/hcv_genotypes2_aligned.dbn");
        // File dataAlignment = new File("C:/dev/thesis/hcv/conservation/hcv_genotypes2_aligned.fas");
        //  File csvFile = new File("C:/dev/thesis/hcv/conservation/rates.csv");
        // String name = "";
        // boolean codon = false;

        //   File dataAlignment = new File("C:/hcv/1b_coding_aligned_97.fas");        
        // File csvFile = new File("C:/hcv/1b_coding_aligned_97.nex.csv");

        // String name = "hcv1b";

       // File referenceAlignment = new File("C:/dev/thesis/hcv/hcv_genotypes2_aligned.fas");
        //File structureAlignment = new File("C:/dev/thesis/hcv/hcv_genotypes2_aligned.dbn");
        //File dataAlignment = new File("C:/dev/thesis/hcv/1/300/hcv1_polyprotein_300_aligned.fas");
        //File csvFile = new File("C:/dev/thesis/hcv/1/300/hcv1_polyprotein_300_aligned.csv");
        //String name = "hcv1";
         //boolean codon = true;
         //int column = 1;

      
          /*File referenceAlignment = new File("C:/dev/thesis/tbv/all_tbv_aligned.fas");
        File structureAlignment = new File("C:/dev/thesis/tbv/all_tbv_aligned.dbn");
         File dataAlignment = new File("C:/dev/thesis/tbv/100/tbv_all_100_aligned.fas");
        File csvFile = new File("C:/dev/thesis/tbv/100/tbv_all_100_aligned.csv");
        String name = "tbv";
         boolean codon = false;
         int column = 1;
         * */
       /*File referenceAlignment = new File("C:/dev/thesis/csfv/csfv_all_aligned.fas");
         File structureAlignment = new File("C:/dev/thesis/csfv/csfv_all_aligned.dbn");
         File dataAlignment = new File("C:/dev/thesis/csfv/100/all_100_aligned.fas");
       File csvFile = new File("C:/dev/thesis/csfv/100/all_100_aligned.csv");
         String name = "csfv";
         boolean codon = false;
         int column = 1;*/

        //File dataAlignment = new File("C:/Users/Michael/Dropbox/Weeks/Fubar/100_coding.fas");
        //File csvFile = new File("C:/Users/Michael/Dropbox/Weeks/Fubar/2 partition/fubar_2.csv");
        //boolean codon = true;

        // File referenceAlignment = new File("C:/dev/thesis/hcv/hcv_genotypes2_aligned.fas");
        // File structureAlignment = new File("C:/dev/thesis/hcv/hcv_genotypes2_aligned.dbn");
        // File dataAlignment = new File("C:/dev/thesis/hcv/2/300/hcv2_polyprotein_300_aligned.fas");
        // File csvFile = new File("C:/dev/thesis/hcv/2/300/hcv2_polyprotein_300_aligned.csv");
        // String name = "hcv2";
        //   boolean codon = true;
        //File csvFile = new File("C:/dev/thesis/dengue/300/site rates.csv");
        // File dataAlignment = new File("C:/dev/thesis/dengue/300/all_300_aligned.fas");
      // File referenceAlignment = new File("C:/dev/thesis/dengue/dengue-alignment.fas");
      //  File structureAlignment = new File("C:/dev/thesis/dengue/dengue-alignment.dbn");
     //  File csvFile = new File("C:/dev/thesis/dengue/300/dengue_polyprotein_300_aligned.csv");
      // File dataAlignment = new File("C:/dev/thesis/dengue/300/dengue_polyprotein_300_aligned.fas");
        //File csvFile = new File("C:/dev/thesis/dengue4/300/dengue4_polyprotein_300_aligned.csv");
        //File dataAlignment = new File("C:/dev/thesis/dengue4/300/dengue4_polyprotein_300_aligned.fas");
      //  String name = "dengue";
      // boolean codon = true;
     //  int column = 1;

      // File referenceAlignment = new File("C:/dev/thesis/dengue/dengue-alignment.fas");
      //  File structureAlignment = new File("C:/dev/thesis/dengue/dengue-alignment.dbn");
       // File csvFile = new File("C:/dev/thesis/dengue/300/dengue_all_300_aligned.fas.rates.csv");
       // File dataAlignment = new File("C:/dev/thesis/dengue/300/dengue_all_300_aligned.fas_norm");
        //String name = "dengue";
        //boolean codon = false;
       // int column = 0;
        
       /* File referenceAlignment = new File("C:/dev/thesis/dengue/dengue-alignment.fas");
        File structureAlignment = new File("C:/dev/thesis/dengue/dengue-alignment.dbn");
        File csvFile = new File("C:/dev/thesis/dengue4/250/dengue4_polyprotein_250_aligned.csv");
        File dataAlignment = new File("C:/dev/thesis/dengue4/250/dengue4_polyprotein_250_aligned.fas");
        String name = "dengue4";
        boolean codon = true;
        int column = 1;*/
        
        /*File referenceAlignment = new File("C:/dev/thesis/dengue/dengue-alignment.fas");
        File structureAlignment = new File("C:/dev/thesis/dengue/dengue-alignment.dbn");
        File csvFile = new File("C:/dev/thesis/dengue2/300/dengue2_polyprotein_300_aligned.csv");
        File dataAlignment = new File("C:/dev/thesis/dengue2/300/dengue2_polyprotein_300_aligned.fas");
        String name = "dengue2";
        boolean codon = true;
        int column = 1;*/
      
      /*File referenceAlignment = new File("C:/dev/thesis/dengue/dengue-alignment.fas");
        File structureAlignment = new File("C:/dev/thesis/dengue/dengue-alignment.dbn");
        File csvFile = new File("C:/dev/thesis/dengue2/300/dengue2_polyprotein_300_aligned.csv");
        File dataAlignment = new File("C:/dev/thesis/dengue2/300/dengue2_polyprotein_300_aligned.fas");
        String name = "dengue2";
        boolean codon = true;
        int column = 1;*/
        
         /*File referenceAlignment = new File("C:/dev/thesis/jev/jev-alignment.fas");
        File structureAlignment =  new File("C:/dev/thesis/jev/jev-alignment.dbn");
         File csvFile = new File("C:/dev/thesis/jev/100/jev_all_100_aligned.csv");
       File dataAlignment = new File("C:/dev/thesis/jev/100/jev_all_100_aligned.fas");
        String name = "jev";
        boolean codon = false;
         int column = 1;*/
      
      

/*         File referenceAlignment = new File("C:/dev/thesis/westnile/westnile_all_200_aligned.fas");
         File structureAlignment =  new File("C:/dev/thesis/westnile/westnile_all_200_aligned.dbn");
         File csvFile = new File("C:/dev/thesis/westnile/300/westnile_polyprotein_300_aligned.csv");
         File dataAlignment = new File("C:/dev/thesis/westnile/300/westnile_polyprotein_300_aligned.fas");
         String name = "westnile";
         boolean codon = true;
         int column = 2;*/
      
      

       /* File referenceAlignment = new File("C:/dev/thesis/jev/jev-alignment.fas");
        File structureAlignment =  new File("C:/dev/thesis/jev/jev-alignment.dbn");
         File csvFile = new File("C:/dev/thesis/jev/300/jev_polyprotein_300_aligned.csv");
        File dataAlignment = new File("C:/dev/thesis/jev/300/jev_polyprotein_300_aligned.fas");
        String name = "jev";
        boolean codon = true;
        int column = 1;*/
         
       /*  File referenceAlignment = new File("C:/dev/thesis/jev/jev-alignment.fas");
        File structureAlignment =  new File("C:/dev/thesis/jev/jev-alignment.dbn");
         //File csvFile = new File("C:/dev/thesis/jev/300/jev_polyprotein_300_aligned.csv");
        //File dataAlignment = new File("C:/dev/thesis/jev/300/jev_polyprotein_300_aligned.fas");
        File csvFile = new File("C:/dev/thesis/jev/300/jev_all_300_aligned.fas.rates.csv");
        File dataAlignment = new File("C:/dev/thesis/jev/300/jev_all_300_aligned.fas_norm");
        String name = "jev";
         boolean codon = false;
         int column = 0;*/


        /*File referenceAlignment = new File("C:/dev/thesis/tbv/all_tbv_aligned.fas");
         File structureAlignment = new File("C:/dev/thesis/tbv/all_tbv_aligned.dbn");
         File dataAlignment = new File("C:/dev/thesis/tbv/300/tbv_polyprotein_300_aligned.fas");
        File csvFile = new File("C:/dev/thesis/tbv/300/tbv_polyprotein_300_aligned.csv");
        String name = "tbv";
        boolean codon = true;
        int column = 1;*/

        /*File referenceAlignment = new File("C:/dev/thesis/bvdv/bvdv_all_aligned.fas");
        File structureAlignment = new File("C:/dev/thesis/bvdv/bvdv_all_aligned.dbn");
         File dataAlignment = new File("C:/dev/thesis/bvdv/300/polyprotein_300_aligned_trim.fas");
         File csvFile = new File("C:/dev/thesis/bvdv/300/bvdv_polyprotein_300_aligned_trim.csv");
        String name = "bvdv";
         boolean codon = true;
            int column = 2;*/
        File referenceAlignment = new File("C:/dev/thesis/csfv/csfv_all_aligned.fas");
         File structureAlignment = new File("C:/dev/thesis/csfv/csfv_all_aligned.dbn");
         File dataAlignment = new File("C:/dev/thesis/csfv/300/polyprotein_300_aligned.fas");
        File csvFile = new File("C:/dev/thesis/csfv/300/csfv_polyprotein_300_aligned.csv");
         String name = "csfv";
         boolean codon = true;
         int column = 1;

        ArrayList<String> values = CsvReader.getColumn(csvFile, column);
        values.remove(0);

        ArrayList<SecondaryStructureData> structureData2 = FileImport.loadStructures(structureAlignment, DataType.FileFormat.VIENNA_DOT_BRACKET);
        ArrayList<SecondaryStructureData> structureData = new ArrayList<SecondaryStructureData>();
        for (int i = 0; i < structureData2.size(); i++) {
            String[] titleSplit = structureData2.get(i).title.split("\\s+");
            if (titleSplit[titleSplit.length - 1].contains(name)) {
                structureData.add(structureData2.get(i));
            }
        }


        //MappedData mappedData = PairwiseStructureComparison.hivMapping(referenceAlignment);
        MappedData mappedData = MappedData.getMappedData(referenceAlignment, dataAlignment, values, codon, "", 1000, false);


        CorrelatedSitesTest test = new CorrelatedSitesTest();

        //test.realDistanceTest3(mappedData, structureData.get(0).pairedSites);

        Random random = new Random(314104911491491L);
        boolean[] used = new boolean[structureData.size()];
        ArrayList<Double> rvalues = new ArrayList<>();
        ArrayList<Double> pvalues = new ArrayList<>();
        ArrayList<Double> permvalues = new ArrayList<>();
        for (int i = 0; i < structureData.size(); i++) {
            int j = random.nextInt(structureData.size());
            while (used[j]) {
                j = random.nextInt(structureData.size());
            }

            SecondaryStructureData structure = structureData.get(j);
            // test.realDistanceTest3(mappedData, structure.pairedSites);
            // System.out.println(structure.title);
           System.out.print(j + "\t");
            PairedSitesPermutationTestResult result = test.pairedSitesCorrelationPermutationTest(mappedData, structure.pairedSites, threads);
            rvalues.add(result.r);
            pvalues.add(result.pval);
            permvalues.add(result.permutation);
            System.out.println(rvalues.size() + "\t" + RankingAnalyses.getMedian(rvalues) + "\t" + RankingAnalyses.getMedian(pvalues) + "\t" + RankingAnalyses.getMedian(permvalues));
     
            
            //CorrelatedSitesTest.distanceCorrelationTest(mappedData,structure.pairedSites);
           // used[j] = true;
        }

        /*
         * for (SecondaryStructureData structure : structureData) {
         * test.pairedSitesCorrelationPermutationTest(mappedData,
         * structure.pairedSites); }
         *
         *
         */
    }
}
