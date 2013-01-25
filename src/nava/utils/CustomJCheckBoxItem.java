/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.utils;

import javax.swing.JCheckBox;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class CustomJCheckBoxItem<T> extends JCheckBox {
    private T object;
    
    public CustomJCheckBoxItem(String label)
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
