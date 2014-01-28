/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.vienna;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class Structure {
    public String sequence;
    public int [] pairedSites;
    public double freeEnergy = Double.NaN;
    public double ensembleFrequency = Double.NaN;
    public double structureDistance = Double.NaN;
    public double tempCelsius = Double.NaN;

    @Override
    public String toString() {
        return "Structure{" + "sequence=" + sequence + ", pairedSites=" + pairedSites + ", freeEnergy=" + freeEnergy + ", ensembleFrequency=" + ensembleFrequency + ", structureDistance=" + structureDistance + '}';
    }
    
    public static double calculateEnsembleFrequency(ViennaRuntime viennaRuntime, String sequence, int [] target, double tempCelsius) throws Exception
    {
        RNAfold rnafold = new RNAfold(viennaRuntime);
        RNAfoldResult result = rnafold.fold(sequence, tempCelsius, true);
        Structure mfe = result.getMFEstructure();
        RNAeval rnaeval = new RNAeval(viennaRuntime);
        double freeEnergy = rnaeval.calculateFreeEnergy(sequence, target, tempCelsius);
        double difference = mfe.freeEnergy - freeEnergy;
        double ensembleFrequencyOfStructure = Math.exp(difference)*result.mfeEnsembleFrequency;        
        return ensembleFrequencyOfStructure;
    }
    
    public double calculateEnsembleFrequency(ViennaRuntime viennaRuntime) throws Exception
    {
        RNAfold rnafold = new RNAfold(viennaRuntime);
        double foldingTemp = Double.isNaN(tempCelsius) ? ViennaRuntime.defaultTempCelsius : tempCelsius;
        RNAfoldResult result = rnafold.fold(sequence, foldingTemp, true);
        Structure mfe = result.getMFEstructure();
        RNAeval rnaeval = new RNAeval(viennaRuntime);
        if(Double.isNaN(freeEnergy))
        {
            this.freeEnergy = rnaeval.calculateFreeEnergy(sequence, pairedSites, foldingTemp);
        }
        double difference = mfe.freeEnergy - this.freeEnergy;
        //System.out.println(mfe.freeEnergy+"\t"+freeEnergy+"\t"+difference);
        //System.out.println(result.mfeEnsembleFrequency);
        double ensembleFrequencyOfStructure = Math.exp(difference)*result.mfeEnsembleFrequency;
        //System.out.println(mfe.freeEnergy+"\t"+ensembleFrequencyOfStructure+"\t"+(difference == 0 ? "mfe" : "no")+"\t"+Utils.calculateGC(sequence)+"\t"+Utils.calculatedBasePairedGC(this)+"\t"+Utils.calculatedUnpairedGC(this));
        return ensembleFrequencyOfStructure;
    }
}
