/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.experimental;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import nava.utils.RNAFoldingTools;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class SparseBasePairMatrix {
    
    public int numStructures = 0;
    HashMap<CoordinatePair, Integer> matrix = new HashMap<CoordinatePair, Integer>();

    public ArrayList<CoordinatePair> getPairsWithAtLeastN(int n) {
        ArrayList<CoordinatePair> pairsWithAtLeastN = new ArrayList<CoordinatePair>();
        Iterator<Map.Entry<CoordinatePair, Integer>> pairs = matrix.entrySet().iterator();
        Map.Entry<CoordinatePair, Integer> current = null;
        while (pairs.hasNext()) {
            current = pairs.next();
            if (current.getValue() >= n) {
                pairsWithAtLeastN.add(current.getKey());
            }
        }

        return pairsWithAtLeastN;
    }

    public int increment(int i, int j) {
        CoordinatePair index = new CoordinatePair(i, j);
        Integer value = matrix.get(index);
        if (value == null) {
            matrix.put(index, 1);
            return 1;
        } else {
            matrix.put(index, value + 1);
            return value+1;
        }
    }    
        
    public void increment(int [] pairedSites)
    {
        for(int i = 0 ; i < pairedSites.length ; i++)
        {
            this.increment(i, pairedSites[i]);
        }
        numStructures++;
    }
    
    public int getHighConfidencePairedPositionCount(double f, int [] pairedSites)
    {
        int count = 0;
        int [] hcpp = getHighConfidencePairedPositions(f, pairedSites);
        for(int i = 0 ; i < hcpp.length ; i++)
        {
            if(hcpp[i] != 0)
            {
                count++;
            }
        }
        
        return count;
    }
    
    public int [] getHighConfidencePairedPositions(int n, int [] pairedSites)
    {
        int [] hcpp = new int[pairedSites.length];
        for(int i = 0 ; i < pairedSites.length ; i++)
        {
            if(pairedSites[i] != 0 && get(i, pairedSites[i]) >= n)
            {
                hcpp[i] = pairedSites[i];
            }
        }
        return hcpp;
    }
    
    public int [] getHighConfidencePairedPositions(double f, int [] pairedSites)
    {
        return getHighConfidencePairedPositions((int)(f*numStructures), pairedSites);        
    }
    
    public int get(int i, int j) {
        CoordinatePair index = new CoordinatePair(i, j);
        Integer value = matrix.get(index);
        if (value == null) {
            return 0;
        } else {
            return value;
        }
    }

    class CoordinatePair {

        int i;
        int j;

        public CoordinatePair(int i, int j) {
            this.i = i;
            this.j = j;
        }

        @Override
        public String toString() {
            return "CoordinatePair{" + "i=" + i + ", j=" + j + '}';
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final CoordinatePair other = (CoordinatePair) obj;
            if (this.i != other.i) {
                return false;
            }
            if (this.j != other.j) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 23 * hash + this.i;
            hash = 23 * hash + this.j;
            return hash;
        }
    }

    public static void main(String[] args) {
        SparseBasePairMatrix m = new SparseBasePairMatrix();
        m.increment(RNAFoldingTools.getPairedSitesFromDotBracketString("(.....)"));
        m.increment(RNAFoldingTools.getPairedSitesFromDotBracketString("(.....)"));
        m.increment(RNAFoldingTools.getPairedSitesFromDotBracketString("((...))"));
        m.increment(RNAFoldingTools.getPairedSitesFromDotBracketString("(((.)))"));
        System.out.println(RNAFoldingTools.getDotBracketStringFromPairedSites(m.getHighConfidencePairedPositions(0, RNAFoldingTools.getPairedSitesFromDotBracketString("((...))"))));
        System.out.println(RNAFoldingTools.getDotBracketStringFromPairedSites(m.getHighConfidencePairedPositions(1, RNAFoldingTools.getPairedSitesFromDotBracketString("(((.)))"))));
        System.out.println(RNAFoldingTools.getDotBracketStringFromPairedSites(m.getHighConfidencePairedPositions(2, RNAFoldingTools.getPairedSitesFromDotBracketString("((...))"))));
        System.out.println(RNAFoldingTools.getDotBracketStringFromPairedSites(m.getHighConfidencePairedPositions(3, RNAFoldingTools.getPairedSitesFromDotBracketString("((...))"))));
        System.out.println(RNAFoldingTools.getDotBracketStringFromPairedSites(m.getHighConfidencePairedPositions(4, RNAFoldingTools.getPairedSitesFromDotBracketString("((...))"))));
        System.out.println(RNAFoldingTools.getDotBracketStringFromPairedSites(m.getHighConfidencePairedPositions(5, RNAFoldingTools.getPairedSitesFromDotBracketString("((...))"))));
        
        
             
            
          
    }
}
