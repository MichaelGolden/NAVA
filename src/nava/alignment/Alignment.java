/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.alignment;

import java.util.List;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class Alignment<T extends AlignmentItem> {
    
    List<T> items;
    
    public Alignment(List<T> items)
    {
        this.items = items;
    }
    
}
