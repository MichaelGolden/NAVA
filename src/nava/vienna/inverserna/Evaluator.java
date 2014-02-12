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
    
    EvaluationTypeRegister typeRegister;
    
    public Evaluator(EvaluationTypeRegister typeRegister)
    {
        this.typeRegister = typeRegister;
    }    
    
    public abstract List<EvaluationValue> evaluate(TargetAndCandidate targetAndCandidate);
            
    public List<EvaluationValue> evaluate(MultiTargetAndCandidate targetAndCandidates)
    {
        ArrayList<EvaluationValue> criteria = new ArrayList<>();
        for(int i = 0 ; i < targetAndCandidates.targetStructures.size() ; i++)
        {
            TargetStructure target = targetAndCandidates.targetStructures.get(i);
            List<EvaluationValue> targetCriteria = evaluate(new TargetAndCandidate(target, targetAndCandidates.sequence));
            for(EvaluationValue criterion : targetCriteria)
            {
                System.out.println(criterion);
                System.out.println(criterion.type);
                System.out.println(criterion.type.target);
                criterion.type.target = target;
                
                if(criterion.type.criterionIsTargetDependent)
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
