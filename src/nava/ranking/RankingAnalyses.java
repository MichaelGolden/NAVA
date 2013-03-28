/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.ranking;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import nava.structurevis.data.DataOverlay1D;
import nava.structurevis.data.DataOverlay2D;
import nava.structurevis.data.PersistentSparseMatrix;
import nava.structurevis.data.PersistentSparseMatrix.Element;
import nava.structurevis.data.Substructure;
import nava.utils.Mapping;

/**
 *
 * @author Michael
 */
public class RankingAnalyses {

    public static double[] toDoubleArray(ArrayList<Double> array) {
        double[] values = new double[array.size()];
        for (int i = 0; i < array.size(); i++) {
            values[i] = array.get(i);
        }
        return values;
    }

    public static ArrayList<Double> getValues(DataOverlay1D dataOverlay1D, Mapping mapping, Substructure substructure, int[] pairedSites, boolean paired, boolean unpaired, int genomeLength) {
        ArrayList<Double> values = new ArrayList<>();
        boolean codon = dataOverlay1D.codonPositions;
        if (codon) {
            for (int i = substructure.startPosition; i < substructure.startPosition + substructure.length; i = i + 3) {
                boolean ispaired = pairedSites[i % genomeLength] != 0;
                int dataPos = mapping.aToB(i % genomeLength);
                if (dataPos != -1 && ((ispaired && paired) || (!ispaired && unpaired)) && dataOverlay1D.used[dataPos]) {
                    values.add(dataOverlay1D.data[dataPos]);
                }
            }
        } else {
            for (int i = substructure.startPosition; i < substructure.startPosition + substructure.length; i++) {
                boolean ispaired = pairedSites[i % genomeLength] != 0;
                int dataPos = mapping.aToB(i % genomeLength);
                if (dataPos != -1 && ((ispaired && paired) || (!ispaired && unpaired)) && dataOverlay1D.used[dataPos]) {
                    values.add(dataOverlay1D.data[dataPos]);
                }
            }
        }

        return values;
    }

    public static Ranking rankSequenceData1D(DataOverlay1D dataOverlay1D, Mapping mapping, Substructure substructure, int[] pairedSites, boolean paired, boolean unpaired) {
        ArrayList<Double> fullGenomeList = getValues(dataOverlay1D, mapping, new Substructure(0, pairedSites), pairedSites, paired, unpaired, pairedSites.length);
        ArrayList<Double> substructureList = getValues(dataOverlay1D, mapping, substructure, pairedSites, paired, unpaired, pairedSites.length);


        double[] substructureValues = toDoubleArray(substructureList);
        double[] fullGenomeValues = toDoubleArray(fullGenomeList);

        double substructureMedian = Double.NaN;
        double fullGenomeMedian = Double.NaN;
        if (substructureList.size() > 0) {
            substructureMedian = getMedian(substructureList);
        }
        if (fullGenomeList.size() > 0) {
            fullGenomeMedian = getMedian(fullGenomeList);
        }

        MyMannWhitney mw = new MyMannWhitney(substructureValues, fullGenomeValues);

        Ranking r = new Ranking();
        r.mannWhitneyU = mw.getTestStatistic();
        r.zScore = mw.getZ();
        r.xN = substructureList.size();
        r.yN = fullGenomeList.size();
        r.xMean = getAverage(substructureList);
        r.yMean = getAverage(fullGenomeList);
        r.xMedian = substructureMedian;
        r.yMedian = fullGenomeMedian;

        return r;
    }

    public static ArrayList<Double> getValues(DataOverlay2D dataOverlay2D, Mapping mapping, Substructure substructure, int[] pairedSites, boolean paired, boolean unpaired) throws IOException {
        ArrayList<Double> values = new ArrayList<>();
        int length = pairedSites.length;
        boolean codon = dataOverlay2D.codonPositions;
        PersistentSparseMatrix matrix = dataOverlay2D.dataMatrix;
        if (codon) {
            for (int i = substructure.startPosition; i < substructure.startPosition + substructure.length; i = i + 3) {
                int x = mapping.aToB(i % length);
                int y = mapping.aToB(pairedSites[i % length] - 1);
                if (x != -1 && y != -1) {
                    double val = matrix.getValue(x, y);
                    if (val != matrix.emptyValue) {
                        values.add(val);
                    }
                }
            }
        } else {
            if (paired && !unpaired) {
                for (int i = substructure.startPosition; i < substructure.startPosition + substructure.length; i = i + 1) {
                    int x = mapping.aToB(i % length);
                    int y = mapping.aToB(pairedSites[i % length] - 1);
                    if (x != -1 && y != -1) {
                        double val = matrix.getValue(x, y);
                        if (val != matrix.emptyValue) {
                            values.add(val);
                        }
                    }
                }
            } else {
                int start = mapping.aToB(substructure.startPosition);
                int end = mapping.aToB(substructure.startPosition + substructure.length);

                start = start < 0 ? 0 : start;
                end = end < 0 ? matrix.n : end;
                //Iterator<Element> iterator = matrix.iterator(substructure.startPosition,substructure.startPosition+substructure.length,substructure.startPosition,substructure.startPosition+substructure.length);
                Iterator<Element> iterator = matrix.unorderedIterator(start, end, start, end);
                
           
                Element element = null;
                while (iterator.hasNext()) {
                    element = iterator.next();
                    if (element.value != matrix.emptyValue) {                        
                        int x = mapping.bToA(element.i);
                        int y = mapping.bToA(element.j);
                        if (x >= 0 && y >= 0) {
                            if (x >= substructure.startPosition && x < substructure.startPosition + substructure.length && y >= substructure.startPosition && y < substructure.startPosition + substructure.length) {
                                // if in sub-matrix
                                if (paired && pairedSites[x] == y + 1) {
                                    values.add(element.value);
                                } else if (unpaired && pairedSites[x] != y + 1) {
                                    values.add(element.value);
                                }
                            } else // deal with wrap around
                            if (x >= substructure.startPosition && (x + length < substructure.startPosition + substructure.length || x < substructure.startPosition + substructure.length) && y >= substructure.startPosition && (y + length < substructure.startPosition + substructure.length || y < substructure.startPosition + substructure.length)) {
                                if (paired && (pairedSites[x % length] == y + 1 || pairedSites[x % length] == y % length + 1)) {
                                    values.add(element.value);
                                } else if (paired && (pairedSites[x % length] != y + 1 && pairedSites[x % length] != y % length + 1)) {
                                    values.add(element.value);
                                }
                            }
                        }
                    }
                }
            }
        }
        return values;
    }
    double[] fullGenomeValuesAll = null;
    double[] fullGenomeValuesPaired = null;
    double[] fullGenomeValuesUnparedaired = null;

    public static Ranking rankSequenceData2D(DataOverlay2D dataOverlay2D, Mapping mapping, Substructure substructure, int[] pairedSites, boolean paired, boolean unpaired, ArrayList<Double> fullGenomeList) throws IOException {
        ArrayList<Double> substructureList = getValues(dataOverlay2D, mapping, substructure, pairedSites, paired, unpaired);

        /*
         * if (substructure.startPosition + substructure.length - 1 >=
         * genomeLength) { substructureList = getValues(sequenceData2D,
         * substructure.startPosition - 1, genomeLength, codonData,
         * fullStructure, paired, unpaired);
         * substructureList.addAll(getValues(sequenceData2D, 0,
         * (substructure.startPosition + substructure.length - 1) %
         * genomeLength, codonData, fullStructure, paired, unpaired)); }
         */


        double[] substructureValues = toDoubleArray(substructureList);
        double[] fullGenomeValues = toDoubleArray(fullGenomeList);

        double substructureMedian = Double.NaN;
        double fullGenomeMedian = Double.NaN;
        if (substructureList.size() > 0) {
            substructureMedian = getMedian(substructureList);
        }
        if (fullGenomeList.size() > 0) {
            fullGenomeMedian = getMedian(fullGenomeList);
        }
        // System.out.println("Medians: " + substructureMedian + "\t" + fullGenomeMedian + "\t" + getMedian2(substructureList)+"\t"+getMedian2(fullGenomeList));

        MyMannWhitney mw = new MyMannWhitney(substructureValues, fullGenomeValues);

        Ranking r = new Ranking();
        r.mannWhitneyU = mw.getTestStatistic();
        r.zScore = mw.getZ();
        r.xN = substructureList.size();
        r.yN = fullGenomeList.size();
        r.xMean = getAverage(substructureList);
        r.yMean = getAverage(fullGenomeList);
        r.xMedian = substructureMedian;
        r.yMedian = fullGenomeMedian;

        return r;
    }

    public static Ranking rankSequenceData2D(DataOverlay2D dataOverlay2D, Mapping mapping, Substructure substructure, int[] pairedSites, boolean paired, boolean unpaired) throws IOException {
        ArrayList<Double> substructureList = getValues(dataOverlay2D, mapping, substructure, pairedSites, paired, unpaired);
        ArrayList<Double> fullGenomeList = getValues(dataOverlay2D, mapping, new Substructure(0, pairedSites), pairedSites, paired, unpaired);


        /*
         * if (substructure.startPosition + substructure.length - 1 >=
         * genomeLength) { substructureList = getValues(sequenceData2D,
         * substructure.startPosition - 1, genomeLength, codonData,
         * fullStructure, paired, unpaired);
         * substructureList.addAll(getValues(sequenceData2D, 0,
         * (substructure.startPosition + substructure.length - 1) %
         * genomeLength, codonData, fullStructure, paired, unpaired)); }
         */


        double[] substructureValues = toDoubleArray(substructureList);
        double[] fullGenomeValues = toDoubleArray(fullGenomeList);

        double substructureMedian = Double.NaN;
        double fullGenomeMedian = Double.NaN;
        if (substructureList.size() > 0) {
            substructureMedian = getMedian(substructureList);
        }
        if (fullGenomeList.size() > 0) {
            fullGenomeMedian = getMedian(fullGenomeList);
        }
        // System.out.println("Medians: " + substructureMedian + "\t" + fullGenomeMedian + "\t" + getMedian2(substructureList)+"\t"+getMedian2(fullGenomeList));

        MyMannWhitney mw = new MyMannWhitney(substructureValues, fullGenomeValues);

        Ranking r = new Ranking();
        r.mannWhitneyU = mw.getTestStatistic();
        r.zScore = mw.getZ();
        r.xN = substructureList.size();
        r.yN = fullGenomeList.size();
        r.xMean = getAverage(substructureList);
        r.yMean = getAverage(fullGenomeList);
        r.xMedian = substructureMedian;
        r.yMedian = fullGenomeMedian;

        return r;
    }

    public static double getAverage(ArrayList<Double> list) {
        double sum = 0;
        for (int i = 0; i < list.size(); i++) {
            sum += list.get(i);
        }

        return sum / (double) list.size();
    }

    public static double getMedian(ArrayList<Double> list) {
        if (list.size() % 2 == 1) {
            return select(list, list.size() / 2 + 1);
        } else {
            Double x = select(list, list.size() / 2 + 1);
            Double y = select(list, list.size() / 2 + 1 + 1);

            return (x.doubleValue() + y.doubleValue()) / 2;
        }
    }

    public static double getMedian2(ArrayList<Double> list) {
        if (list.size() == 0) {
            return Double.NaN;
        }

        Collections.sort(list);
        if (list.size() % 2 == 1) {
            return list.get(list.size() / 2);
        } else {
            Double x = list.get(list.size() / 2);
            Double y = list.get(list.size() / 2 + 1);

            return (x.doubleValue() + y.doubleValue()) / 2;
        }
    }

    public static Double select(ArrayList<Double> list, int k) {
        if (list.size() <= 10) {
            Collections.sort(list);
            return list.get(Math.min(k - 1, list.size() - 1));
        }

        int numSubsets = list.size() / 5;
        ArrayList[] subsets = new ArrayList[numSubsets];
        for (int i = 0; i < numSubsets; i++) {
            subsets[i] = new ArrayList<Double>();
            for (int j = 0; j < 5; j++) {
                subsets[i].add(list.get(i * 5 + j));
            }
        }

        ArrayList<Double> x = new ArrayList<Double>(numSubsets);
        for (int i = 0; i < numSubsets; i++) {
            x.add(select(subsets[i], 3));
        }


        Double M = select(x, list.size() / 10);

        ArrayList<Double> L1 = new ArrayList<Double>();
        ArrayList<Double> L2 = new ArrayList<Double>();
        ArrayList<Double> L3 = new ArrayList<Double>();
        for (int i = 0; i < list.size(); i++) {
            Double item = list.get(i);

            if (item.compareTo(M) < 0) {
                L1.add(item);
            } else if (item.compareTo(M) > 0) {
                L3.add(item);
            } else {
                L2.add(item);
            }
        }

        if (k <= L1.size()) {
            return select(L1, k);
        } else if (k > L1.size() + L2.size()) {
            return select(L3, k - L1.size() - L2.size());
        }

        return M;
    }

    /**
     * Returns two-tailed p-value from z-value
     *
     * @param Z
     * @return
     */
    public static double NormalZ(double Z) {

        double Y, X, w, temp, Temp2;
        double Z_MAX, NormalZx = 0, WinP;

        Z_MAX = 6;

        if (Math.abs(Z) < 5.9999999) {
            if (Z == 0.0) {
                X = 0.0;
            } else {
                Y = 0.5 * Math.abs(Z);
                if (Y >= (Z_MAX * 0.5)) {
                    X = 1.0;
                } else if (Y < 1.0) {

                    w = Y * Y;
                    X = ((((((((0.000124818987 * w - 0.001075204047) * w + 0.005198775019) * w - 0.019198292004) * w + 0.059054035642) * w - 0.151968751364) * w + 0.319152932694) * w - 0.5319230073) * w + 0.797884560593) * Y * 2.0;

                } else {

                    Y = Y - 2.0;
                    X = (((((((((((((-0.000045255659 * Y
                            + 0.00015252929) * Y - 0.000019538132) * Y
                            - 0.000676904986) * Y + 0.001390604284) * Y
                            - 0.00079462082) * Y - 0.002034254874) * Y
                            + 0.006549791214) * Y - 0.010557625006) * Y
                            + 0.011630447319) * Y - 0.009279453341) * Y
                            + 0.005353579108) * Y - 0.002141268741) * Y
                            + 0.000535310849) * Y + 0.999936657524;
                }

                if ((X + 1.0) < (1.0 - X)) {
                    NormalZx = (X + 1.0);
                } else {
                    NormalZx = (1.0 - X);
                }

            }
        } else {
            temp = ((Math.abs(Z) - 5.999999) * 10);
            Temp2 = Math.pow(1.6, temp);
            WinP = Math.pow(10, -9);
            WinP = WinP / Temp2;
            NormalZx = WinP;

        }
        return (NormalZx);
    }
}