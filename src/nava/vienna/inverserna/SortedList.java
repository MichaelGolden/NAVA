/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.vienna.inverserna;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class SortedList<T> {
    ArrayList<T> values = new ArrayList<>();
    
    Comparator comparator;
    
    public SortedList(Comparator comparator)
    {
        this.comparator = comparator;
    }
    
    public T get(int index)
    {
        return values.get(index);
    }
    
    public void add(T element)
    {
        int index = Collections.binarySearch(values, element, comparator);
        if(index <= 0)
        {
            index = -(index+1);            
        }
        values.add(index, element);
    }
    
    public int size()
    {
        return values.size();
    }
    
    @Override
    public String toString()
    {
        return values.toString();
    }
}
