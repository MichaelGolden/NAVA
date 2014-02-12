/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.vienna.inverserna;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import nava.structure.BenchmarkMetrics;
import nava.structure.MountainMetrics;
import nava.vienna.*;
import nava.vienna.inverserna.criteria.InverseRNAUtils;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class RNAfoldEvaluator extends Evaluator {
    ViennaRuntime viennaRuntime;
    boolean usePartitionFunction = true;
    
    public RNAfoldEvaluator(EvaluationTypeRegister typeRegister, ViennaRuntime viennaRuntime, boolean usePartitionFunction)
    {
        super(typeRegister);
        this.viennaRuntime = viennaRuntime;
        this.usePartitionFunction = usePartitionFunction;
    }

    @Override
    public ArrayList<EvaluationValue> evaluate(TargetAndCandidate tc) {
        ArrayList<EvaluationValue> evaluatedCriteria = new ArrayList<>();
        try {            
            String sequence = tc.sequence;
            int [] target = tc.target.pairedSites;

            double tempCelsius = tc.target.tempCelsius;
            RNAfold rnafold = new RNAfold(viennaRuntime);
            double foldingTemp = Double.isNaN(tempCelsius) ? ViennaRuntime.defaultTempCelsius : tempCelsius;
           
            if(usePartitionFunction)
            {
                RNAfoldResult result = rnafold.fold(sequence, foldingTemp, true);
                Structure mfe = result.getMFEstructure();
                RNAeval rnaeval = new RNAeval(viennaRuntime);
                double freeEnergy = rnaeval.calculateFreeEnergy(sequence, target, foldingTemp);
                double difference = mfe.freeEnergy - freeEnergy;
                double ensembleProb = Math.exp(difference)*result.mfeEnsembleFrequency;
                double ensembleDefect = target.length;
                for(int i = 0 ; i < target.length ; i++)
                {
                    if(target[i] == 0)
                    {
                        ensembleDefect -= (1-result.basePairProb[i][i]);
                        //System.out.print("U"+result.basePairProb[i][i]+"\t");
                    }
                    else
                    {
                        ensembleDefect -= result.basePairProb[i][target[i]-1];
                        //System.out.print(result.basePairProb[i][target[i]-1]+"\t");
                    }
                }
                // System.out.println();
                ensembleDefect /= (double)target.length;

                evaluatedCriteria.add(new EvaluationValue(freeEnergy, new EvaluationType("rnafold free energy", tc.target, EvaluationType.ParetoRanking.LOWER_IS_BETTER, true, Double.NEGATIVE_INFINITY, 0, "temp="+tempCelsius+"C")));
                evaluatedCriteria.add(new EvaluationValue(ensembleProb, new EvaluationType("rnafold ensemble prob", tc.target, EvaluationType.ParetoRanking.HIGHER_IS_BETTER, true, 0, 1, "temp="+tempCelsius+"C")));
                evaluatedCriteria.add(new EvaluationValue(ensembleDefect, new EvaluationType("rnafold norm. ensemble defect", tc.target, EvaluationType.ParetoRanking.LOWER_IS_BETTER, true, 0, 1, "temp="+tempCelsius+"C")));                
                evaluatedCriteria.add(new EvaluationValue(1-MountainMetrics.calculateNormalizedMountainDistance(mfe.pairedSites, target,1), new EvaluationType("mountain_similarity p=1 (target, rnafold)", tc.target, EvaluationType.ParetoRanking.HIGHER_IS_BETTER, true, 0, 1, "temp="+tempCelsius+"C")));
                evaluatedCriteria.add(new EvaluationValue(1-MountainMetrics.calculateNormalizedWeightedMountainDistance(mfe.pairedSites, target), new EvaluationType("mountain_similarity weighted (target, rnafold)", tc.target, EvaluationType.ParetoRanking.HIGHER_IS_BETTER, true, 0, 1, "temp="+tempCelsius+"C")));
                evaluatedCriteria.add(new EvaluationValue(BenchmarkMetrics.calculateSensitivity(target, mfe.pairedSites), new EvaluationType("sensitivity (target, rnafold)", tc.target, EvaluationType.ParetoRanking.HIGHER_IS_BETTER, true, 0, 1, "temp="+tempCelsius+"C")));
                evaluatedCriteria.add(new EvaluationValue(BenchmarkMetrics.calculatePPV(target, mfe.pairedSites), new EvaluationType("ppv (target, rnafold)", tc.target, EvaluationType.ParetoRanking.HIGHER_IS_BETTER, true, 0, 1, "temp="+tempCelsius+"C")));
                evaluatedCriteria.add(new EvaluationValue(BenchmarkMetrics.calculateFScore(target, mfe.pairedSites), new EvaluationType("fscore (target, rnafold)", tc.target, EvaluationType.ParetoRanking.HIGHER_IS_BETTER, true, 0, 1, "temp="+tempCelsius+"C")));
            }
            else
            {
                RNAfoldResult result = rnafold.fold(sequence, foldingTemp, false);
                Structure mfe = result.getMFEstructure();
                RNAeval rnaeval = new RNAeval(viennaRuntime);
                double freeEnergy = rnaeval.calculateFreeEnergy(sequence, target, foldingTemp);
                double difference = mfe.freeEnergy - freeEnergy;
               
                /*double ensembleProb = Math.exp(difference)*result.mfeEnsembleFrequency;
                double ensembleDefect = target.length;
                for(int i = 0 ; i < target.length ; i++)
                {
                    if(target[i] == 0)
                    {
                        ensembleDefect -= (1-result.basePairProb[i][i]);
                        //System.out.print("U"+result.basePairProb[i][i]+"\t");
                    }
                    else
                    {
                        ensembleDefect -= result.basePairProb[i][target[i]-1];
                        //System.out.print(result.basePairProb[i][target[i]-1]+"\t");
                    }
                }
                // System.out.println();
                ensembleDefect /= (double)target.length;
                */
                
                evaluatedCriteria.add(new EvaluationValue(freeEnergy, new EvaluationType("rnafold free energy", tc.target, EvaluationType.ParetoRanking.LOWER_IS_BETTER, true, Double.NEGATIVE_INFINITY, 0, "temp="+tempCelsius+"C")));
                //evaluatedCriteria.add(new EvaluationValue(ensembleProb, new EvaluationType("rnafold ensemble prob", tc.target, EvaluationType.ParetoRanking.HIGHER_IS_BETTER, true, 0, 1, "temp="+tempCelsius+"C")));
               // evaluatedCriteria.add(new EvaluationValue(ensembleDefect, new EvaluationType("rnafold norm. ensemble defect", tc.target, EvaluationType.ParetoRanking.LOWER_IS_BETTER, true, 0, 1, "temp="+tempCelsius+"C")));                
                evaluatedCriteria.add(new EvaluationValue(1-MountainMetrics.calculateNormalizedMountainDistance(mfe.pairedSites, target,1), new EvaluationType("mountain_similarity p=1 (target, rnafold)", tc.target, EvaluationType.ParetoRanking.HIGHER_IS_BETTER, true, 0, 1, "temp="+tempCelsius+"C")));
                evaluatedCriteria.add(new EvaluationValue(1-MountainMetrics.calculateNormalizedWeightedMountainDistance(mfe.pairedSites, target), new EvaluationType("mountain_similarity weighted (target, rnafold)", tc.target, EvaluationType.ParetoRanking.HIGHER_IS_BETTER, true, 0, 1, "temp="+tempCelsius+"C")));
                evaluatedCriteria.add(new EvaluationValue(BenchmarkMetrics.calculateSensitivity(target, mfe.pairedSites), new EvaluationType("sensitivity (target, rnafold)", tc.target, EvaluationType.ParetoRanking.HIGHER_IS_BETTER, true, 0, 1, "temp="+tempCelsius+"C")));
                evaluatedCriteria.add(new EvaluationValue(BenchmarkMetrics.calculatePPV(target, mfe.pairedSites), new EvaluationType("ppv (target, rnafold)", tc.target, EvaluationType.ParetoRanking.HIGHER_IS_BETTER, true, 0, 1, "temp="+tempCelsius+"C")));
                evaluatedCriteria.add(new EvaluationValue(BenchmarkMetrics.calculateFScore(target, mfe.pairedSites), new EvaluationType("fscore (target, rnafold)", tc.target, EvaluationType.ParetoRanking.HIGHER_IS_BETTER, true, 0, 1, "temp="+tempCelsius+"C")));                
            }
        } catch (Exception ex) {
            Logger.getLogger(RNAfoldEvaluator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return evaluatedCriteria;        
    }
    
}
