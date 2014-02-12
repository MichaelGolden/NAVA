/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.vienna.inverserna.criteria;

import nava.vienna.inverserna.EvaluationType;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class SimpleCriterionType2 extends EvaluationType {
    public SimpleCriterionType(String uniqueName, ParetoRanking ranking)
     {
         this.uniqueName = uniqueName;
         this.descriptiveName = uniqueName;
         this.ranking = ranking;
     }
     
     public SimpleCriterionType(String uniqueName, ParetoRanking ranking, boolean criterionIsTargetDependent, double lowerBound, double upperBound)
     {
         this.uniqueName = uniqueName;
         this.descriptiveName = uniqueName;
         this.ranking = ranking;
         this.criterionIsTargetDependent = criterionIsTargetDependent;
         this.lowerBound = lowerBound;
         this.upperBound = upperBound;
     }     

    @Override
    public String getUniqueName() {
        return this.uniqueName;
    }

    @Override
    public String getDescriptiveName() {
        return this.descriptiveName;
    }
}
