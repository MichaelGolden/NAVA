/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.vienna.inverserna;

import java.util.ArrayList;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class EvaluationType {

    public static enum ParetoRanking {

        NONE, HIGHER_IS_BETTER, LOWER_IS_BETTER, CLOSER_TO_TARGET_VALUE_IS_BETTER
    };
    
    public String uniqueName;
    public String descriptiveName;    
    public TargetStructure target;
    public double targetValue = Double.NaN;    
    public double lowerBound = Double.NEGATIVE_INFINITY;
    public double upperBound = Double.POSITIVE_INFINITY;
    public ParetoRanking ranking = ParetoRanking.NONE;
    public boolean criterionIsTargetDependent = true;
    String [] uniquefiers;

   /* public EvaluationType(String uniqueName, ParetoRanking ranking, String ... uniquiefiers) {
        this.uniqueName = uniqueName;
        this.descriptiveName = uniqueName;
        this.ranking = ranking;
    }*/

    public EvaluationType(String uniqueName, TargetStructure target, ParetoRanking ranking, boolean criterionIsTargetDependent, double lowerBound, double upperBound, String ... uniquefiers) {
        this.uniqueName = uniqueName;
        this.target = target;
        this.descriptiveName = uniqueName;
        this.ranking = ranking;
        this.criterionIsTargetDependent = criterionIsTargetDependent;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.uniquefiers = uniquefiers;
    }
    
    
    public String getUniqueIdentifier()
    {
        String uniquefier = "";
        if(uniquefiers.length > 0)
        {
            uniquefier = " [";
            for(int i = 0 ; i <  uniquefiers.length - 1 ; i++)
            {
                uniquefier += uniquefiers[i]+", ";
            }
            uniquefier += uniquefiers[uniquefiers.length - 1];
            uniquefier += "]";
        }
        if(criterionIsTargetDependent)
        {
            return target.getUniqueIdentifier()+": "+getUniqueName()+uniquefier;
        }
        return getUniqueName()+uniquefier;
    }
    

    public String getUniqueName() {
        return this.uniqueName;
    }

    public String getDescriptiveName() {
        return this.descriptiveName;
    }
}
