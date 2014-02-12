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
import nava.vienna.inverserna.Hybridssmin.Fold;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class UNAfoldEvaluator extends Evaluator  {
    
    public UNAfoldEvaluator(EvaluationTypeRegister typeRegister)
    {
        super(typeRegister);               
    }
    
    public static int numThreads = 8;

    @Override
    public List<EvaluationValue> evaluate(TargetAndCandidate tc) {
        ArrayList<EvaluationValue> evaluatedCriteria = new ArrayList<>();
        //Hybridssmin hybriddsmin = new Hybridssmin();
        try {            
            String sequence = tc.sequence;
            int [] target = tc.target.pairedSites;

            double tempCelsius = tc.target.tempCelsius;
            //double foldingTemp = Double.isNaN(tempCelsius) ? ViennaRuntime.defaultTempCelsius : tempCelsius;
            
            ArrayList<String> sequences = new ArrayList<>();
            sequences.add(sequence);
            sequences.add(sequence);
            int threads = numThreads;
            Fold fold = Hybridssmin.fold(sequences, threads, false, tempCelsius, false).get(0);
            int [] mfePairedSites = fold.pairedSites;           
           
            
            evaluatedCriteria.add(new EvaluationValue(fold.freeEnergy, new EvaluationType("unafold free energy", tc.target, EvaluationType.ParetoRanking.LOWER_IS_BETTER, true, Double.NEGATIVE_INFINITY, 0, "temp="+tempCelsius+"C")));
            evaluatedCriteria.add(new EvaluationValue(1-MountainMetrics.calculateNormalizedMountainDistance(mfePairedSites, target,1), new EvaluationType("mountain_similarity p=1 (target, unafold)", tc.target, EvaluationType.ParetoRanking.HIGHER_IS_BETTER, true, 0, 1, "temp="+tempCelsius+"C")));
            evaluatedCriteria.add(new EvaluationValue(1-MountainMetrics.calculateNormalizedWeightedMountainDistance(mfePairedSites, target), new EvaluationType("mountain_similarity weighted (target, unafold)", tc.target, EvaluationType.ParetoRanking.HIGHER_IS_BETTER, true, 0, 1, "temp="+tempCelsius+"C")));
            evaluatedCriteria.add(new EvaluationValue(BenchmarkMetrics.calculateSensitivity(target, mfePairedSites), new EvaluationType("sensitivity (target, unafold)", tc.target, EvaluationType.ParetoRanking.HIGHER_IS_BETTER, true, 0, 1, "temp="+tempCelsius+"C")));
            evaluatedCriteria.add(new EvaluationValue(BenchmarkMetrics.calculatePPV(target, mfePairedSites), new EvaluationType("ppv (target, unafold)", tc.target, EvaluationType.ParetoRanking.HIGHER_IS_BETTER, true, 0, 1, "temp="+tempCelsius+"C")));
            evaluatedCriteria.add(new EvaluationValue(BenchmarkMetrics.calculateFScore(target, mfePairedSites), new EvaluationType("fscore (target, unafold)", tc.target, EvaluationType.ParetoRanking.HIGHER_IS_BETTER, true, 0, 1, "temp="+tempCelsius+"C")));
          
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
        
        if(mtcs.size() > 0)
        {
            List<TargetStructure> targetStructures = mtcs.get(0).targetStructures;
            for(MultiTargetAndCandidate mtc : mtcs)
            {
                sequences.add(mtc.sequence);
            }

            for(TargetStructure targetStructure : targetStructures)
            {
                double tempCelsius = targetStructure.tempCelsius;
                int threads = numThreads;
                ArrayList<Fold> folds = Hybridssmin.fold(sequences, threads, false, tempCelsius, false); 
                foldMap.put(targetStructure, folds);
            }

            for(int seq = 0 ; seq < sequences.size() ; seq++)
            {
                MultiTargetAndCandidate mtc = mtcs.get(seq);
                for(TargetStructure targetStructure : targetStructures)
                {
                    Fold fold = foldMap.get(targetStructure).get(seq);
                    double tempCelsius = targetStructure.tempCelsius;

                    ArrayList<EvaluationValue> evaluatedCriteria = new ArrayList<>();
                    int [] mfePairedSites = fold.pairedSites;
                    int [] target = targetStructure.pairedSites;
                    evaluatedCriteria.add(new EvaluationValue(fold.freeEnergy, new EvaluationType("unafold free energy", targetStructure, EvaluationType.ParetoRanking.LOWER_IS_BETTER, true, Double.NEGATIVE_INFINITY, 0, "temp="+tempCelsius+"C")));
                    evaluatedCriteria.add(new EvaluationValue(1-MountainMetrics.calculateNormalizedMountainDistance(mfePairedSites, target,1), new EvaluationType("mountain_similarity p=1 (target, unafold)", targetStructure, EvaluationType.ParetoRanking.HIGHER_IS_BETTER, true, 0, 1, "temp="+tempCelsius+"C")));
                    evaluatedCriteria.add(new EvaluationValue(1-MountainMetrics.calculateNormalizedWeightedMountainDistance(mfePairedSites, target), new EvaluationType("mountain_similarity weighted (target, unafold)", targetStructure, EvaluationType.ParetoRanking.HIGHER_IS_BETTER, true, 0, 1, "temp="+tempCelsius+"C")));
                    evaluatedCriteria.add(new EvaluationValue(BenchmarkMetrics.calculateSensitivity(target, mfePairedSites), new EvaluationType("sensitivity (target, unafold)", targetStructure, EvaluationType.ParetoRanking.HIGHER_IS_BETTER, true, 0, 1, "temp="+tempCelsius+"C")));
                    evaluatedCriteria.add(new EvaluationValue(BenchmarkMetrics.calculatePPV(target, mfePairedSites), new EvaluationType("ppv (target, unafold)", targetStructure, EvaluationType.ParetoRanking.HIGHER_IS_BETTER, true, 0, 1, "temp="+tempCelsius+"C")));
                    evaluatedCriteria.add(new EvaluationValue(BenchmarkMetrics.calculateFScore(target, mfePairedSites), new EvaluationType("fscore (target, unafold)", targetStructure, EvaluationType.ParetoRanking.HIGHER_IS_BETTER, true, 0, 1, "temp="+tempCelsius+"C")));
       
                    mtc.addEvaluatedCriteria(evaluatedCriteria);
                }
            }
        }
    }

    /*
    @Override
    public List<EvaluationType> getTypes() {
        List<EvaluationType> types = new ArrayList<>();
        evaluatedCriteria.add(new SimpleCriterionValue(targetStructure, "unafold free energy @ "+tempCelsius+"C",  fold.freeEnergy, EvaluationValue.ParetoRanking.NONE, Double.NEGATIVE_INFINITY, 0));
        evaluatedCriteria.add(new SimpleCriterionValue(targetStructure, "mountain_similarity p=1 [target, unafold(mfe, "+tempCelsius+"C)]",  1-MountainMetrics.calculateNormalizedMountainDistance(mfePairedSites, target,1), EvaluationValue.ParetoRanking.HIGHER_IS_BETTER,0,1));
        evaluatedCriteria.add(new SimpleCriterionValue(targetStructure, "mountain_similarity weighted [target, unafold(mfe, "+tempCelsius+"C)]",  1 - MountainMetrics.calculateNormalizedWeightedMountainDistance(mfePairedSites, target), EvaluationValue.ParetoRanking.HIGHER_IS_BETTER,0,1));
        evaluatedCriteria.add(new SimpleCriterionValue(targetStructure, "sensitivity[target, unafold(mfe, "+tempCelsius+"C)]",  BenchmarkMetrics.calculateSensitivity(target, mfePairedSites), EvaluationValue.ParetoRanking.HIGHER_IS_BETTER,0,1));
        evaluatedCriteria.add(new SimpleCriterionValue(targetStructure, "ppv[target, unafold(mfe, "+tempCelsius+"C)]",  BenchmarkMetrics.calculatePPV(target, mfePairedSites), EvaluationValue.ParetoRanking.HIGHER_IS_BETTER,0,1));
        evaluatedCriteria.add(new SimpleCriterionValue(targetStructure, "fscore[target, unafold(mfe, "+tempCelsius+"C)]",  BenchmarkMetrics.calculateFScore(target, mfePairedSites), EvaluationValue.ParetoRanking.HIGHER_IS_BETTER,0,1));                

        types.add(new SimpleCriterionType("gc_content_all_sites", EvaluationType.ParetoRanking.LOWER_IS_BETTER, false,0,1));
        types.add(new SimpleCriterionType("gc_content_paired_sites", EvaluationType.ParetoRanking.LOWER_IS_BETTER, true,0,1));
        types.add(new SimpleCriterionType("gc_content_unpaired_sites", EvaluationType.ParetoRanking.LOWER_IS_BETTER, true,0,1));
        return types;
    }*/
}
