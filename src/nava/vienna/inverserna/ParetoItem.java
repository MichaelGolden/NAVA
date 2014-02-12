/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.vienna.inverserna;

import java.util.*;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public abstract class ParetoItem {
    
    HashMap<String, EvaluationValue> evaluatedCriteria = new HashMap<>();
    
    public void addEvaluatedCriteria(List<EvaluationValue> evaluatedCriteria)
    {
        for(EvaluationValue criterion : evaluatedCriteria)
        {
            setCriterion(criterion.type.getUniqueIdentifier(), criterion);
        }
    }
    
    public void setCriterion(String name, EvaluationValue criterion)
    {
        evaluatedCriteria.put(name, criterion);
    }
    
    public EvaluationValue getCriterion(String name)
    {
        return evaluatedCriteria.get(name);
    }
    
    public ArrayList<String> getAlphabeticalCriteria()
    {
        ArrayList<String> list = new ArrayList<>();
        Set<String> keys = evaluatedCriteria.keySet();
        for(String key : keys)
        {
            list.add(key);
        }
        Collections.sort(list);
        return list;
    }
    
    public HashMap<String, EvaluationValue> getEvaluatedCriteria() {
        return evaluatedCriteria;
    }
    
    public void printCriteria()
    {
        Set<String> keys = evaluatedCriteria.keySet();
        for(String key : keys)
        {
            System.out.println(key+"\t"+evaluatedCriteria.get(key));
        }
    }

}
