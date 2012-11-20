/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.utils;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class ComboBoxItem<T> {
    private T object;
    private String title;
    
    public ComboBoxItem(T object, String title)
    {
        this.object = object;
        this.title = title;
    }
    
    public T getObject()
    {
        return object;
    }
    
    public String toString()
    {
        return title;
    }
}
