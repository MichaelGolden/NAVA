/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.vienna.inverserna;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public abstract class Evaluator {
        
    public abstract List<EvaluationCriterion> evaluate(TargetAndCandidate targetAndCandidate);
    
    public List<EvaluationCriterion> evaluate(MultiTargetAndCandidate targetAndCandidates)
    {
        ArrayList<EvaluationCriterion> criteria = new ArrayList<>();
        for(int i = 0 ; i < targetAndCandidates.targetStructures.size() ; i++)
        {
            TargetStructure target = targetAndCandidates.targetStructures.get(i);
            List<EvaluationCriterion> targetCriteria = evaluate(new TargetAndCandidate(target, targetAndCandidates.sequence));
            for(EvaluationCriterion criterion : targetCriteria)
            {
                criterion.target = target;   
                if(criterion.criterionIsTargetDependent)
                {             
                    criteria.add(criterion);
                }
                else
                if(i == 0) // only needs to be added for first target, if independent of target
                {
                    criteria.add(criterion);
                }
            }
        }
        return criteria;
    }
    
    public void evaluate(List<MultiTargetAndCandidate> mtcs)
    {
        for(MultiTargetAndCandidate mtc : mtcs)
        {
            mtc.addEvaluatedCriteria(evaluate(mtc));
        }
    }
}
