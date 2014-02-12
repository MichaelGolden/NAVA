/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.vienna.inverserna;

import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class EvaluationTypeRegister {
    
    HashMap<String, EvaluationType> registerMap = new HashMap<>();
    
    public EvaluationType getType(String uniqueName)
    {
        return registerMap.get(uniqueName);
    }
    
    public EvaluationType getType(String uniqueName, TargetStructure target, EvaluationType.ParetoRanking ranking, boolean criterionIsTargetDependent, double lowerBound, double upperBound, String ... uniquefiers)
    {
        EvaluationType type = new EvaluationType(uniqueName, target, ranking, criterionIsTargetDependent, lowerBound, upperBound, uniquefiers);
        if(!registerMap.containsKey(type.getUniqueName()))
        {
            registerMap.put(type.getUniqueName(), type);
        }        
        return registerMap.get(type.getUniqueName());
    }
}
