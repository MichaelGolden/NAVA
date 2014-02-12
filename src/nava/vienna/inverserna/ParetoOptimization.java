/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.vienna.inverserna;

import java.util.ArrayList;
import java.util.Collection;
import nava.vienna.inverserna.EvaluationType.ParetoRanking;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class ParetoOptimization {
    
    EvaluationType paretoCriterion = new EvaluationType("pareto_rank", null, ParetoRanking.NONE,false,0,Double.POSITIVE_INFINITY);
    
    int paretoRank;
    int maxParetoRank;
    
    ParetoOptimization nextLevel = null;
    

    public ArrayList<ParetoItem> items = new ArrayList<>();
    Collection<String> criteria;
    
    public ParetoOptimization(int rank, int maxParetoRank)
    {
        this.paretoRank = rank;
        this.maxParetoRank = maxParetoRank;
    }
    
    
    public void setParetoCriteria(Collection<String> criteria)
    {
        this.criteria = criteria;
        removeAllNonDominant();
    }
    
    public boolean dominatedByAtLeastOne(ParetoItem item)
    {
        if(criteria == null)
        {
            criteria = item.getEvaluatedCriteria().keySet();
        }
        
        for(ParetoItem other : items)
        {
            if(doesDominate(other, item, criteria) && !item.equals(other))
            {
                return true;
            }
        }
        return false;
    }
    
    public void removeAllNonDominant()
    {
        for(int i = 0 ; i < items.size() ; i++)
        {
            if(dominatedByAtLeastOne(items.get(i)))
            {
                ParetoItem item = items.remove(i);
                item.setCriterion("pareto_rank", new EvaluationValue(-this.maxParetoRank, paretoCriterion));
                if(this.paretoRank < this.maxParetoRank)
                {
                    if(nextLevel == null)
                    {
                        nextLevel = new ParetoOptimization(this.paretoRank+1,this.maxParetoRank);
                    }
                    nextLevel.addItem(item);
                }
                i--;
            }
        }
    }
    
    public void addItem(ParetoItem item)
    {
        item.setCriterion("pareto_rank", new EvaluationValue(this.paretoRank, paretoCriterion));
        items.add(item);
        removeAllNonDominant();
    }
    
    public static boolean doesDominate(ParetoItem a, ParetoItem b, Collection<String> criteria)
    { 
        for(String key : criteria)
        {
            EvaluationValue criterion = a.getEvaluatedCriteria().get(key);
            EvaluationValue otherCriterion =  b.getEvaluatedCriteria().get(key);
            switch(criterion.type.ranking)
            {
                case HIGHER_IS_BETTER:
                    if(criterion.value < otherCriterion.value)
                    {
                        return false;
                    }
                    break;
                case LOWER_IS_BETTER:
                        if(criterion.value > otherCriterion.value)
                    {
                        return false;
                    }
                    break;
                default:
                    break;                    
            }
            
        }
        
        return true;
    }
}
