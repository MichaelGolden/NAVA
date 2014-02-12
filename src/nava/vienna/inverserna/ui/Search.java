/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.vienna.inverserna.ui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import nava.utils.Pair;
import nava.vienna.*;
import nava.vienna.inverserna.*;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class Search extends Thread {
    
    ArrayList<TargetStructure> targetStructures;    
    ParetoOptimization pareto = new ParetoOptimization(0, 1);
    ArrayList<String> alphabeticalCriteria = new ArrayList<>();
    
    public Search(ArrayList<TargetStructure> targetStructures)
    {
        this.targetStructures = targetStructures;
    }
    
    @Override
    public void run()
    {
        ViennaRuntime viennaRuntime = new ViennaRuntime(new File("C:/Program Files (x86)/ViennaRNA Package/"), ViennaRuntime.OS.WINDOWS);
        RNAinverse rnainverse = new RNAinverse(viennaRuntime);
        IncaRNAtion2 incarnation = new IncaRNAtion2();
        int incarnationSampleSize = 10;
        EvaluationTypeRegister typeRegister = new EvaluationTypeRegister();
        GCcontentEvaluator linEvaluator = new GCcontentEvaluator(typeRegister);
        RNAfoldEvaluator rnaFoldEvaluator = new RNAfoldEvaluator(typeRegister,viennaRuntime, true);
        //RNAfoldEvaluator rnaFoldEvaluatorNoPartition = new RNAfoldEvaluator(viennaRuntime, false);
        AlternativeStructureEvaluator alternativeEvaluator = new AlternativeStructureEvaluator(typeRegister);
        UNAfoldEvaluator unafoldEvaluator = new UNAfoldEvaluator(typeRegister);
        ArrayList<Evaluator> evaluators = new ArrayList<>();
        evaluators.add(linEvaluator);
        evaluators.add(rnaFoldEvaluator);
        evaluators.add(alternativeEvaluator);
        evaluators.add(unafoldEvaluator);
        
        TargetStructure target1 = targetStructures.get(0);
        
        while(true)
        {
            try {
                List<String> sequences = incarnation.generateSample(target1.pairedSites, target1.tempCelsius, 0.5, 20, incarnationSampleSize);
                sequences = sequences.subList(0, Math.min(sequences.size(), incarnationSampleSize));
         
                ArrayList<MultiTargetAndCandidate> mtcs = new ArrayList<>(sequences.size());
                for(String sequence : sequences)
                {
                    MultiTargetAndCandidate mtc =  new MultiTargetAndCandidate(targetStructures, sequence);
                    mtcs.add(mtc);
                }
                
                for(Evaluator evaluator : evaluators)
                {
                    evaluator.evaluate(mtcs);
                }
                
                if(mtcs.size() > 0)
                {
                    alphabeticalCriteria = mtcs.get(0).getAlphabeticalCriteria();
                }
                
                synchronized(pareto)
                {
                    for(MultiTargetAndCandidate mtc : mtcs)
                    {
                        System.out.println(mtc);
                        pareto.addItem(mtc);
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(Search.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InterruptedException ex) {
                Logger.getLogger(Search.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public Pair<ArrayList<Double>, ArrayList<Double>> getXY(String xlab, String ylab, boolean paretoSetForXandYOnly)
    {
        if(paretoSetForXandYOnly)
        {
            ParetoOptimization pareto2 = new ParetoOptimization(0, 1);
            ArrayList<String> criteria = new ArrayList<>();
            criteria.add(xlab);
            criteria.add(ylab);
            pareto2.setParetoCriteria(criteria);
            synchronized(pareto)
            {
                for(nava.vienna.inverserna.ParetoItem item : pareto.items)
                {
                    pareto2.addItem(item);
                }
            }

            ArrayList<Double> x = new ArrayList<>();
            ArrayList<Double> y = new ArrayList<>();

            for(nava.vienna.inverserna.ParetoItem item : pareto2.items)
            {
                x.add(item.getCriterion(xlab).value);
                y.add(item.getCriterion(ylab).value);
            }            
            return new Pair(x, y);
        }
        else
        {   
            ArrayList<Double> x = new ArrayList<>();
            ArrayList<Double> y = new ArrayList<>();

            synchronized(pareto)
            {
                for(nava.vienna.inverserna.ParetoItem item : pareto.items)
                {
                    x.add(item.getCriterion(xlab).value);
                    y.add(item.getCriterion(ylab).value);
                } 
            }
            return new Pair(x, y);
        }
    }
}
