/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.data.types;

import java.util.HashMap;
import java.util.LinkedList;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class DataSourceCache {
    
    HashMap<DataSource, Object> cache = new HashMap<>();
    LinkedList<DataSource> lru = new LinkedList<>();
    
    public void refresh(DataSource dataSource)
    {
       if(cache.containsKey(dataSource))
       {
           lru.remove(dataSource);
       }
       lru.add(dataSource);
    }
    
    public Object cache(DataSource dataSource, Object object)
    {        
        cache.put(dataSource, object);
        refresh(dataSource);
        
        double freeMemory = Runtime.getRuntime().freeMemory();
        double totalMemory = Runtime.getRuntime().totalMemory();
        double usedMemory = totalMemory-freeMemory;
        
        while(lru.size() > 5)
        {
            cache.remove(lru.removeFirst());
        }
        System.out.println("LRU:" + lru.size()+"\t"+cache.size());
        System.out.println("Memory: " + (usedMemory/1024/1024));
        return object;
    }
    
    public Object getObject(DataSource dataSource)
    {
        refresh(dataSource);
        return cache.get(dataSource);
    }
    
}
