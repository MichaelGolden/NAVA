/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.utils;

import javax.swing.JMenuItem;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class CustomJMenuItem<T> extends JMenuItem {
    private T object;
    
    public CustomJMenuItem(String label)
    {
        super(label);
    }
    
    public void setObject(T object)
    {
        this.object = object;
    }
    
    public T getObject()
    {
        return object;
    }
}
