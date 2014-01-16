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
    String sequence;
    int [] pairedSites;
    double freeEnergy = Double.NaN;
    double ensembleFrequency = Double.NaN;
    double structureDistance = Double.NaN;
    double tempCelsius = Double.NaN;

    @Override
    public String toString() {
        return "Structure{" + "sequence=" + sequence + ", pairedSites=" + pairedSites + ", freeEnergy=" + freeEnergy + ", ensembleFrequency=" + ensembleFrequency + ", structureDistance=" + structureDistance + '}';
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
        System.out.println(mfe.freeEnergy+"\t"+result.mfeEnsembleFrequency+"\t"+(difference > 0 ? "yes" : "no")+"\t"+Utils.calculateGC(sequence)+"\t"+Utils.calculatedBasePairedGC(this)+"\t"+Utils.calculatedUnpairedGC(this));
        return Math.exp(difference)*result.mfeEnsembleFrequency;
    }
}
