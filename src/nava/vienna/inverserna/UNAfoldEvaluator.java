/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.vienna.inverserna;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import nava.structure.BenchmarkMetrics;
import nava.structure.MountainMetrics;
import nava.vienna.*;
import nava.vienna.inverserna.Hybridssmin.Fold;
import nava.vienna.inverserna.criteria.SimpleCriterion;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class UNAfoldEvaluator  extends Evaluator  {

    @Override
    public List<EvaluationCriterion> evaluate(TargetAndCandidate tc) {
        ArrayList<EvaluationCriterion> evaluatedCriteria = new ArrayList<>();
        //Hybridssmin hybriddsmin = new Hybridssmin();
        try {            
            String sequence = tc.sequence;
            int [] target = tc.target.pairedSites;

            double tempCelsius = tc.target.tempCelsius;
            //double foldingTemp = Double.isNaN(tempCelsius) ? ViennaRuntime.defaultTempCelsius : tempCelsius;
            double foldingTemp = tempCelsius;
            
            ArrayList<String> sequences = new ArrayList<>();
            sequences.add(sequence);
            sequences.add(sequence);
            Fold fold = Hybridssmin.fold(sequences, 4, false, tempCelsius, false).get(0);
            int [] mfePairedSites = fold.pairedSites;
            
           
        
            evaluatedCriteria.add(new SimpleCriterion("unafold free energy @ "+tempCelsius+"C",  fold.freeEnergy, EvaluationCriterion.ParetoRanking.NONE, true, Double.NEGATIVE_INFINITY, 0));
            evaluatedCriteria.add(new SimpleCriterion("mountain_similarity p=1 [target, unafold(mfe, "+tempCelsius+"C)]",  1-MountainMetrics.calculateNormalizedMountainDistance(mfePairedSites, target,1), EvaluationCriterion.ParetoRanking.HIGHER_IS_BETTER, true,0,1));
            evaluatedCriteria.add(new SimpleCriterion("mountain_similarity weighted [target, unafold(mfe, "+tempCelsius+"C)]",  1 - MountainMetrics.calculateNormalizedWeightedMountainDistance(mfePairedSites, target), EvaluationCriterion.ParetoRanking.HIGHER_IS_BETTER, true,0,1));
            evaluatedCriteria.add(new SimpleCriterion("sensitivity[target, unafold(mfe, "+tempCelsius+"C)]",  BenchmarkMetrics.calculateSensitivity(target, mfePairedSites), EvaluationCriterion.ParetoRanking.HIGHER_IS_BETTER, true,0,1));
            evaluatedCriteria.add(new SimpleCriterion("ppv[target, unafold(mfe, "+tempCelsius+"C)]",  BenchmarkMetrics.calculatePPV(target, mfePairedSites), EvaluationCriterion.ParetoRanking.HIGHER_IS_BETTER, true,0,1));
            evaluatedCriteria.add(new SimpleCriterion("fscore[target, unafold(mfe, "+tempCelsius+"C)]",  BenchmarkMetrics.calculateFScore(target, mfePairedSites), EvaluationCriterion.ParetoRanking.HIGHER_IS_BETTER, true,0,1));
        } catch (Exception ex) {
            Logger.getLogger(RNAfoldEvaluator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return evaluatedCriteria;  
    }
    
    @Override
    public void evaluate(List<MultiTargetAndCandidate> mtcs)
    {
        HashMap<TargetStructure, ArrayList<Fold>> foldMap = new HashMap<>();
        ArrayList<String> sequences = new ArrayList<>();
        
        List<TargetStructure> targetStructures = mtcs.get(0).targetStructures;
        for(MultiTargetAndCandidate mtc : mtcs)
        {
            sequences.add(mtc.sequence);
        }
        
        for(TargetStructure targetStructure : targetStructures)
        {
            double tempCelsius = targetStructure.tempCelsius;
            ArrayList<Fold> folds = Hybridssmin.fold(sequences, 4, false, tempCelsius, false); 
            foldMap.put(targetStructure, folds);
        }
        
        for(int seq = 0 ; seq < sequences.size() ; seq++)
        {
            MultiTargetAndCandidate mtc = mtcs.get(seq);
            for(TargetStructure targetStructure : targetStructures)
            {
                Fold fold = foldMap.get(targetStructure).get(seq);
                double tempCelsius = targetStructure.tempCelsius;
                
                ArrayList<EvaluationCriterion> evaluatedCriteria = new ArrayList<>();
                int [] mfePairedSites = fold.pairedSites;
                int [] target = targetStructure.pairedSites;
                evaluatedCriteria.add(new SimpleCriterion(targetStructure, "unafold free energy @ "+tempCelsius+"C",  fold.freeEnergy, EvaluationCriterion.ParetoRanking.NONE, Double.NEGATIVE_INFINITY, 0));
                evaluatedCriteria.add(new SimpleCriterion(targetStructure, "mountain_similarity p=1 [target, unafold(mfe, "+tempCelsius+"C)]",  1-MountainMetrics.calculateNormalizedMountainDistance(mfePairedSites, target,1), EvaluationCriterion.ParetoRanking.HIGHER_IS_BETTER,0,1));
                evaluatedCriteria.add(new SimpleCriterion(targetStructure, "mountain_similarity weighted [target, unafold(mfe, "+tempCelsius+"C)]",  1 - MountainMetrics.calculateNormalizedWeightedMountainDistance(mfePairedSites, target), EvaluationCriterion.ParetoRanking.HIGHER_IS_BETTER,0,1));
                evaluatedCriteria.add(new SimpleCriterion(targetStructure, "sensitivity[target, unafold(mfe, "+tempCelsius+"C)]",  BenchmarkMetrics.calculateSensitivity(target, mfePairedSites), EvaluationCriterion.ParetoRanking.HIGHER_IS_BETTER,0,1));
                evaluatedCriteria.add(new SimpleCriterion(targetStructure, "ppv[target, unafold(mfe, "+tempCelsius+"C)]",  BenchmarkMetrics.calculatePPV(target, mfePairedSites), EvaluationCriterion.ParetoRanking.HIGHER_IS_BETTER,0,1));
                evaluatedCriteria.add(new SimpleCriterion(targetStructure, "fscore[target, unafold(mfe, "+tempCelsius+"C)]",  BenchmarkMetrics.calculateFScore(target, mfePairedSites), EvaluationCriterion.ParetoRanking.HIGHER_IS_BETTER,0,1));                
                mtc.addEvaluatedCriteria(evaluatedCriteria);
            }
        }
    }
}
