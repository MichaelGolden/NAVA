/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.vienna.inverserna;

import nava.vienna.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import nava.structure.BenchmarkMetrics;
import nava.structure.MountainMetrics;
import nava.utils.RNAFoldingTools;
import nava.vienna.inverserna.EvaluationCriterion.ParetoRanking;
import nava.vienna.inverserna.criteria.SimpleCriterion;
import org.apache.commons.math3.stat.correlation.SpearmansCorrelation;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class ParetoOptimization {
    
    int paretoRank;
    int maxParetoRank;
    
    ParetoOptimization nextLevel = null;
    

    ArrayList<ParetoItem> items = new ArrayList<>();
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
                item.setCriterion("pareto_rank", new SimpleCriterion("pareto_rank", -this.maxParetoRank, ParetoRanking.NONE,false,0,Double.POSITIVE_INFINITY));
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
        item.setCriterion("pareto_rank", new SimpleCriterion("pareto_rank", this.paretoRank, ParetoRanking.NONE,false,0,Double.POSITIVE_INFINITY));
        items.add(item);
        removeAllNonDominant();
    }
    
    public static boolean doesDominate(ParetoItem a, ParetoItem b, Collection<String> criteria)
    { 
        for(String key : criteria)
        {
            EvaluationCriterion criterion = a.getEvaluatedCriteria().get(key);
            EvaluationCriterion otherCriterion =  b.getEvaluatedCriteria().get(key);
            switch(criterion.ranking)
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
