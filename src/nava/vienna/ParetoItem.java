/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.vienna;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import nava.vienna.ParetoParameter.ParetoRanking;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class ParetoItem<T>
{
    HashMap<String, ParetoParameter> parameterSet = new HashMap<>();    
    T object;
    
    public void setParameter(String name, ParetoRanking ranking, double value)
    {
        parameterSet.put(name, new ParetoParameter(name, ranking, value));
    }
    
    public double getParameterValue(String name)
    {
        return parameterSet.get(name).value;
    }
    
     public boolean dominatesAll(ParetoItem other)
    {
        Set<String> keys = this.parameterSet.keySet();        
        for(String key : keys)
        {
            ParetoParameter param = this.parameterSet.get(key);
            ParetoParameter otherParam = (ParetoParameter)other.parameterSet.get(key);
            if(param.ranking == ParetoRanking.HIGHER_IS_BETTER)
            {
                if(param.value < otherParam.value)
                {
                    return false;
                }
            }
            else
            {
                // Lower is better
                if(param.value > otherParam.value)
                {
                    return false;
                }
            }
            
        }
        
        return true;
    }
    
    public boolean dominates(ParetoItem other)
    {
        Set<String> keys = this.parameterSet.keySet();        
        for(String key : keys)
        {
            ParetoParameter param = this.parameterSet.get(key);
            ParetoParameter otherParam = (ParetoParameter)other.parameterSet.get(key);
            if(param.ranking == ParetoRanking.HIGHER_IS_BETTER)
            {
                if(param.value > otherParam.value)
                {
                    return true;
                }
            }
            else
            {
                // Lower is better
                if(param.value < otherParam.value)
                {
                    return true;
                }
            }
            
        }
        
        return false;
    }

    @Override
    public String toString() {
        return "ParetoItem{" + "parameterSet=" + parameterSet + '}';
    }
    
    public String formattedString()
    {
        Set<String> keys = parameterSet.keySet();
        String ret ="";
        for(String key : keys)
        {
            ret += key+"\t"+parameterSet.get(key).value+"\t";
        }
        return ret;
    }
}
