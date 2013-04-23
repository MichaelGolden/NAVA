/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.structurevis;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import nava.experimental.CorrelatedSitesTest;
import nava.experimental.MappedData;

/**
 * Given a list of paired sites this class constructs a graph (distance matrix)
 * where a set of paired nucleotides (i, j) or adjacent nucleotides (i, i+1)
 * have d=1, (i, i) d=0, otherwise d=infinity. It then executes the all shortest
 * paths algorithm. This distance matrix gives an indication of physical
 * distance between nucleotides within the genome. In order to speed up
 * computation, only the central diagonal of the matrix is computer (i.e. only
 * distances for nucleotides nearby one another in the sequence are computed).
 * In addition, the matrix is binned, so the distances are only an
 * approximation.
 *
 * @author Michael Golden
 */
public class DistanceMatrix {

    int n;
    int nd;
    int[][] matrix;
    int binSize;
    int radius;

    public DistanceMatrix(int binSize, int radius, int length) {
        this.n = length;
        this.binSize = binSize;
        this.radius = radius;
        this.nd = n / binSize + 1;
        this.matrix = new int[nd][nd];
    }

    public DistanceMatrix(int binSize, int radius, int[] pairedSites) {
        this.n = pairedSites.length;
        this.binSize = binSize;
        this.radius = radius;
        this.nd = n / binSize + 1;
        this.matrix = new int[nd][nd];

        for (int i = 0; i < matrix.length; i++) {
            Arrays.fill(matrix[i], Integer.MAX_VALUE);
        }

        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                matrix[i][j] = Math.abs(i - j);
            }
        }

        for (int i = 0; i < pairedSites.length; i++) {
            int x = (i) / binSize;
            int y = (pairedSites[i] - 1) / binSize;
            //System.out.println(pairedSites[0][i]+"\t"+pairedSites[1][i]);
            // System.out.println(x + "\t" + y + "\t" + pairedSites[0][i] + "\t" + pairedSites[1][i] + "\t" + matrix.length + "\t" + matrix[0].length);
            //System.out.println(pairedSites[1][i]);
            if (pairedSites[i] != 0) {
                matrix[x][y] = 1;
                matrix[y][x] = 1;
            }
        }

        for (int i = 0; i < matrix.length; i++) {
            matrix[i][i] = 0;
        }
    }

    public DistanceMatrix(int[] pairedSites) {
        this.n = pairedSites.length;
        this.binSize = 1;
        //this.radius = radius;
        this.nd = n;
        this.matrix = new int[nd][nd];

        for (int i = 0; i < matrix.length; i++) {
            Arrays.fill(matrix[i], Integer.MAX_VALUE);
        }

        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                matrix[i][j] = Math.abs(i - j);
            }
        }

        for (int i = 0; i < pairedSites.length; i++) {
            int x = i;
            int y = pairedSites[i] - 1;
            if (pairedSites[i] != 0) {
                matrix[x][y] = 1;
                matrix[y][x] = 1;
            }
        }

        for (int i = 0; i < matrix.length; i++) {
            matrix[i][i] = 0;
        }

        this.computeFloydWarshall2();
    }

    public static int getBestBinSize(int genomeLength, int radius) {
        double minIterations = 1e8;
        double maxIterations = 2e9 / 10000 * genomeLength; // scale number of iterations linearly according to genomeLength
        int binSize = 1;
        while (true) {
            double numIterations = getNumberOfIterations(binSize, genomeLength, radius);
            if (numIterations < minIterations) {
                return binSize;
            }
            if (numIterations < maxIterations) {
                return binSize;
            }
            binSize++;
        }
    }

    public static double getNumberOfIterations(int binSize, int genomeLength, double radius) {
        return Math.pow((genomeLength / binSize), 3) * ((double) Math.min(radius * 2, genomeLength) / genomeLength);
    }

    public void computeFloydWarshall() {
        int diagRadius = radius / binSize;
        for (int k = 0; k < nd; k++) {
            if (k % 100 == 0) {
                System.out.println(k + " out of " + nd);
            }

            // j < i
            for (int i = 0; i < nd; i++) {
                int minj = i - diagRadius;
                int maxj = i + diagRadius;

                for (int j = minj; j < 0; j++) {
                    int js = nd + j;
                    // if (js < i) {
                    matrix[i][js] = Math.min(matrix[i][js], matrix[i][k] + matrix[k][js]);
                    //   matrix[js][i] = matrix[i][js];
                    // }
                }

                for (int j = Math.max(minj, 0); j < Math.min(maxj, nd); j++) {
                    //if (j < i) {
                    matrix[i][j] = Math.min(matrix[i][j], matrix[i][k] + matrix[k][j]);
                    //   matrix[j][i] = matrix[i][j];
                    //  }
                }

                for (int j = nd; j < maxj; j++) {
                    int js = j - nd;
                    // if (js < i) {
                    matrix[i][js] = Math.min(matrix[i][js], matrix[i][k] + matrix[k][js]);
                    //    matrix[js][i] = matrix[i][js];
                    // }
                }
            }
        }
    }

    public void computeSubmatrix(int[][] matrix, int startx, int starty, int length) {
        int endx = Math.min(startx + length, matrix.length);
        int endy = Math.min(starty + length, matrix.length);
        for (int k = 0; k < length; k++) {
            int ik = startx + k;
            int jk = starty + k;
            if (jk < matrix.length && ik < matrix.length) {
                for (int i = startx; i < endx; i++) {
                    for (int j = starty; j < endy; j++) {
                        int dist = matrix[i][jk] + matrix[ik][j];
                        if (dist < matrix[i][j]) {
                            matrix[i][j] = dist;
                        }
                    }
                }
            }
        }
    }

    class Input {

        int[][] matrix;
        int startx;
        int starty;
        int length;
    }

    public void processInputs(List<Input> inputs)
            throws InterruptedException, ExecutionException {

        int threads = Runtime.getRuntime().availableProcessors();
        ExecutorService service = Executors.newFixedThreadPool(threads);

        List<Future<Object>> futures = new ArrayList<>();
        for (final Input input : inputs) {
            Callable<Object> callable = new Callable<Object>() {

                public Object call() throws Exception {
                    computeSubmatrix(input.matrix, input.startx, input.starty, input.length);
                    return null;
                }
            };
            futures.add(service.submit(callable));
        }

        service.shutdown();
    }

    public void computeFloydWarshallParallelised() {
        int length = (radius / binSize) * 2;
        int shift = radius / binSize;
        //int shift = length;

        //ArrayList<Input> inputs = new ArrayList<>();
        for (int startx = 0; startx < matrix.length; startx = startx + shift) {
            System.out.println("progress " + startx + "/" + matrix.length);
            for (int starty = 0; starty < matrix.length; starty = starty + shift) {
                computeSubmatrix(matrix, startx, starty, length);
                Input input = new Input();
                /*input.matrix = matrix;
                input.startx = startx;
                input.starty = starty;
                input.length = length;
                inputs.add(input);*/
            }
        }
        /*
        try {
            processInputs(inputs);
        } catch (InterruptedException ex) {
            Logger.getLogger(DistanceMatrix.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ExecutionException ex) {
            Logger.getLogger(DistanceMatrix.class.getName()).log(Level.SEVERE, null, ex);
        }*/
    }

    public void computeFloydWarshall3() {
        int diagRadius = radius / binSize;
        for (int k = 0; k < nd; k++) {
            if (k % 100 == 0) {
                System.out.println(k + " out of " + nd);
            }

            // j < i
            for (int i = 0; i < nd; i++) {
                int minj = i - diagRadius;
                int maxj = i + diagRadius;

                for (int j = minj; j < 0; j++) {
                    int js = nd + j;
                    // if (js < i) {
                    matrix[i][js] = Math.min(matrix[i][js], matrix[i][k] + matrix[k][js]);
                    //   matrix[js][i] = matrix[i][js];
                    // }
                }

                for (int j = Math.max(minj, 0); j < Math.min(maxj, nd); j++) {
                    //if (j < i) {
                    matrix[i][j] = Math.min(matrix[i][j], matrix[i][k] + matrix[k][j]);
                    //   matrix[j][i] = matrix[i][j];
                    //  }
                }

                for (int j = nd; j < maxj; j++) {
                    int js = j - nd;
                    // if (js < i) {
                    matrix[i][js] = Math.min(matrix[i][js], matrix[i][k] + matrix[k][js]);
                    //    matrix[js][i] = matrix[i][js];
                    // }
                }
            }
        }
    }

    public void computeFloydWarshall2() {
        for (int k = 0; k < nd; k++) {
            for (int i = 0; i < nd; i++) {
                for (int j = 0; j < nd; j++) {
                    matrix[i][j] = Math.min(matrix[i][j], matrix[i][k] + matrix[k][j]);
                }
            }
        }
    }

    public void printMatrix() {
        for (int i = 0; i < nd; i++) {
            for (int j = 0; j < nd; j++) {
                System.out.print((matrix[i][j] * binSize) + "\t");
            }
            System.out.println();
        }
    }

    // zero offset
    public int getDistance(int i, int j) {
        return matrix[i / binSize][j / binSize] * binSize;
    }

    public static File getCacheFile(File collectionFolder/*
             * , int binSize, int radius
             */) {
        return new File(collectionFolder.getPath() + File.separatorChar + "distancematrix.cache");
        //return new File(collectionFolder.getPath() + "/" + binSize + "_" + radius + "_distancematrix.cache");
    }

    public void cache(File collectionFolder) {
        File file = getCacheFile(collectionFolder/*
                 * , binSize, radius
                 */);
        try {
            //DataOutputStream out = new DataOutputStream(new FileOutputStream(file));
            DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
            //  out.w
            out.writeInt(binSize);
            out.writeInt(radius);
            out.writeInt(n);
            for (int i = 0; i < nd; i++) {
                for (int j = 0; j < nd; j++) {
                    out.writeInt(matrix[i][j]);
                }
            }
            out.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static DistanceMatrix load(File collectionFolder/*
             * , int binSize, int radius
             */) {
        DistanceMatrix matrix = null;
        File file = getCacheFile(collectionFolder/*
                 * , binSize, radius
                 */);
        if (file.exists()) {
            try {
                DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
                matrix = new DistanceMatrix(in.readInt(), in.readInt(), in.readInt());
                for (int i = 0; i < matrix.nd; i++) {
                    for (int j = 0; j < matrix.nd; j++) {
                        matrix.matrix[i][j] = in.readInt();
                    }
                }
                in.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            return matrix;
        }
        return null;
    }

    public static void main(String[] args) {
        //System.out.println(DistanceMatrix.getNumberOfIterations(3, 3000, 1000));
        //System.out.println(DistanceMatrix.getNumberOfIterations(1, 10000, 1000));
        System.out.println(DistanceMatrix.getBestBinSize(800, 1000));
        System.out.println(DistanceMatrix.getBestBinSize(3000, 1000));
        System.out.println(DistanceMatrix.getBestBinSize(5000, 1000));
        System.out.println(DistanceMatrix.getBestBinSize(10000, 1000));
        System.out.println(DistanceMatrix.getBestBinSize(20000, 1000));
        System.out.println(DistanceMatrix.getBestBinSize(40000, 1000));
        /*
         * try { long startTime = System.currentTimeMillis(); Structure s =
         * StructureParser.parseNaspCtFile(new
         * File("C:/project/hepacivirus/10seq_aligned_d0.fasta.ct"));
         * DistanceMatrix dm = new DistanceMatrix(5, 1000, s.pairedSites);
         * dm.computeFloydWarshall(); System.out.println("dist: " +
         * dm.getDistance(4, 21)); //
         * System.out.println((System.currentTimeMillis() - startTime) / 1000);
         * dm.printMatrix(); } catch (Exception ex) {
         * Logger.getLogger(DistanceMatrix.class.getName()).log(Level.SEVERE,
         * null, ex); }
         */
    }
}
