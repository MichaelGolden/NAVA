/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.utils;

import javax.swing.event.ListDataEvent;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class SafeListEvent<T> extends ListDataEvent {
    
    T oldElement;
    T newElement;
    int a= 2;
    public SafeListEvent(Object source, int type, int index0, int index1, T oldElement, T newElement)
    {
        super(source,type,index0,index1);
        this.oldElement = oldElement;
        this.newElement = newElement;
    }
    
    public T getOldElement()
    {
        return oldElement;
    }
    
    public T getNewElement()
    {
        return newElement;
    }
            
}
