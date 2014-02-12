/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.vienna.inverserna;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class EvaluationValue implements Comparable<EvaluationValue> 
{    
    public double value = Double.NaN; 
    public EvaluationType type;   
    
    public EvaluationValue(double value, EvaluationType type) {
        this.value = value;
        this.type = type;
    }
    

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final EvaluationValue other = (EvaluationValue) obj;
        return type.getUniqueName().equals(other.type.getUniqueName());
    }
    
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + type.getUniqueName().hashCode();
        return hash;
    }
    
    @Override
    public String toString()
    {
        return type.getUniqueIdentifier()+"="+value;
    }
    
    /*
    @Override
    public EvaluationCriterion<T> clone()
    {
        try {
            EvaluationCriterion<T> ret = this.getClass().newInstance();
            ret.value = value;
            ret.targetValue = targetValue;
            ret.lowerBound = lowerBound;
            ret.upperBound = upperBound;
            ret.ranking = this.ranking;
            ret.target = target;
            ret.criterionIsTargetDependent = criterionIsTargetDependent;
            
            return ret;
        } catch (InstantiationException ex) {
            Logger.getLogger(EvaluationCriterion.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(EvaluationCriterion.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;        
    }*/

    @Override
    public int compareTo(EvaluationValue o) {
        return Double.compare(this.value, o.value);
    }
}
