/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.vienna;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class RNAfoldResult 
{
    Structure mfeStructure;
    double mfeEnsembleFrequency = Double.NaN;
    double ensembleDiversity = Double.NaN;
    
    public Structure getMFEstructure()
    {
        return mfeStructure;
    }
    
    public Structure getMEAstructure()
    {
        return null;
    }
    
    public double getMFEfrequency()
    {
        return mfeEnsembleFrequency;
    }

    @Override
    public String toString() {
        return "RNAfoldResult{" + "mfeStructure=" + mfeStructure + ", mfeEnsembleFrequency=" + mfeEnsembleFrequency + ", ensembleDiversity=" + ensembleDiversity + '}';
    }
}
