/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.vienna.inverserna;

import java.util.Arrays;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public abstract class EvaluationCriterion<T> implements Comparable<T> 
{   
    public static enum ParetoRanking {NONE, HIGHER_IS_BETTER, LOWER_IS_BETTER, CLOSER_TO_TARGET_VALUE_IS_BETTER};
    
    public double value = Double.NaN;
    public double targetValue = Double.NaN;
    public double lowerBound = Double.NEGATIVE_INFINITY;
    public double upperBound = Double.POSITIVE_INFINITY;
    public ParetoRanking ranking = ParetoRanking.NONE;
    public TargetStructure target;
    public boolean criterionIsTargetDependent = true;
    
    public String getUniqueIdentifier()
    {
        if(criterionIsTargetDependent)
        {
            return target.getUniqueIdentifier()+": "+getUniqueName();
        }
        return getUniqueName();
    }
    
    public abstract String getUniqueName();
    public abstract String getDescriptiveName();

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final EvaluationCriterion other = (EvaluationCriterion) obj;
        return this.getUniqueName().equals(other.getUniqueName());
    }
    
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + getUniqueName().hashCode();
        return hash;
    }
    
    @Override
    public String toString()
    {
        return getUniqueIdentifier()+"="+value;
    }
}
