/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.vienna.inverserna;

import java.util.Comparator;
import nava.vienna.inverserna.EvaluationType.ParetoRanking;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class CandidateComparator implements Comparator<ParetoItem>
{
    String sortOnField;

    public CandidateComparator(String sortOnField)
    {
        this.sortOnField = sortOnField;
    }

    @Override
    public int compare(ParetoItem o1, ParetoItem o2) {
        EvaluationValue c1 = o1.getCriterion(sortOnField);
        EvaluationValue c2 = o2.getCriterion(sortOnField);
        
        int compare = 0;
        if(c1.value < c2.value)
        {
            compare = -1;
        }
        else
        if(c1.value > c2.value)
        {
            compare = 1;
        }
        if(c1.type.ranking  == ParetoRanking.HIGHER_IS_BETTER)
        {
            compare = -compare;
        }        
        
        return compare;
    }
}
