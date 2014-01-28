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
import nava.vienna.inverserna.EvaluationCriterion.ParetoRanking;
import nava.vienna.inverserna.criteria.InverseRNAUtils;
import nava.vienna.inverserna.criteria.SimpleCriterion;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class RNAfoldEvaluator extends Evaluator {
    ViennaRuntime viennaRuntime;
    
    public RNAfoldEvaluator(ViennaRuntime viennaRuntime)
    {
        this.viennaRuntime = viennaRuntime;
    }

    @Override
    public ArrayList<EvaluationCriterion> evaluate(TargetAndCandidate tc) {
        ArrayList<EvaluationCriterion> evaluatedCriteria = new ArrayList<>();
        try {            
            String sequence = tc.sequence;
            int [] target = tc.target.pairedSites;

            double tempCelsius = tc.target.tempCelsius;
            RNAfold rnafold = new RNAfold(viennaRuntime);
            double foldingTemp = Double.isNaN(tempCelsius) ? ViennaRuntime.defaultTempCelsius : tempCelsius;
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
        
            evaluatedCriteria.add(new SimpleCriterion("rnafold free energy @ "+tempCelsius+"C", freeEnergy, ParetoRanking.NONE, true, Double.NEGATIVE_INFINITY, 0));
            evaluatedCriteria.add(new SimpleCriterion("rnafold ensemble prob @ "+tempCelsius+"C", ensembleProb, ParetoRanking.HIGHER_IS_BETTER, true, 0,1));
            evaluatedCriteria.add(new SimpleCriterion("rnafold norm. ensemble defect @ "+tempCelsius+"C", ensembleDefect, ParetoRanking.LOWER_IS_BETTER, true,0,1));
            evaluatedCriteria.add(new SimpleCriterion("mountain_similarity p=1 [target, rnafold(mfe, "+tempCelsius+"C)]",  1-MountainMetrics.calculateNormalizedMountainDistance(mfe.pairedSites, target,1), ParetoRanking.HIGHER_IS_BETTER, true,0,1));
            evaluatedCriteria.add(new SimpleCriterion("mountain_similarity weighted [target, rnafold(mfe, "+tempCelsius+"C)]",  1 - MountainMetrics.calculateNormalizedWeightedMountainDistance(mfe.pairedSites, target), ParetoRanking.HIGHER_IS_BETTER, true,0,1));
            evaluatedCriteria.add(new SimpleCriterion("sensitivity[target, rnafold(mfe, "+tempCelsius+"C)]",  BenchmarkMetrics.calculateSensitivity(target, mfe.pairedSites), ParetoRanking.HIGHER_IS_BETTER, true,0,1));
            evaluatedCriteria.add(new SimpleCriterion("ppv[target, rnafold(mfe, "+tempCelsius+"C)]",  BenchmarkMetrics.calculatePPV(target, mfe.pairedSites), ParetoRanking.HIGHER_IS_BETTER, true,0,1));
            evaluatedCriteria.add(new SimpleCriterion("fscore[target, rnafold(mfe, "+tempCelsius+"C)]",  BenchmarkMetrics.calculateFScore(target, mfe.pairedSites), ParetoRanking.HIGHER_IS_BETTER, true,0,1));
        } catch (Exception ex) {
            Logger.getLogger(RNAfoldEvaluator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return evaluatedCriteria;        
    }
    
}
