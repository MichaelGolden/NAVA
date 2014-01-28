/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.vienna.inverserna.criteria;

import nava.vienna.inverserna.EvaluationCriterion;
import nava.vienna.inverserna.TargetStructure;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class SimpleCriterion  extends EvaluationCriterion<SimpleCriterion> {
     public String uniqueName;
     public String descriptiveName;
     
     public SimpleCriterion(String uniqueName, double value, ParetoRanking ranking)
     {
         this.uniqueName = uniqueName;
         this.descriptiveName = uniqueName;
         this.value = value;
         this.ranking = ranking;
     }
     
     public SimpleCriterion(String uniqueName, double value, ParetoRanking ranking, boolean criterionIsTargetDependent, double lowerBound, double upperBound)
     {
         this.uniqueName = uniqueName;
         this.descriptiveName = uniqueName;
         this.value = value;
         this.ranking = ranking;
         this.criterionIsTargetDependent = criterionIsTargetDependent;
         this.lowerBound = lowerBound;
         this.upperBound = upperBound;
     }
     
     public SimpleCriterion(TargetStructure target, String uniqueName, double value, ParetoRanking ranking, double lowerBound, double upperBound)
     {
         this.target = target;
         this.uniqueName = uniqueName;
         this.descriptiveName = uniqueName;
         this.value = value;
         this.ranking = ranking;
         this.criterionIsTargetDependent = true;
         this.lowerBound = lowerBound;
         this.upperBound = upperBound;
     }

    @Override
    public String getUniqueName() {
        return uniqueName;
    }

    @Override
    public String getDescriptiveName() {
        return descriptiveName;
    }

    @Override
    public int compareTo(SimpleCriterion o) {
        return Double.compare(this.value, o.value);
    }
}
