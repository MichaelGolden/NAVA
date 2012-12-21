/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.structurevis.data;

import java.awt.Color;
import java.io.Serializable;

/**
 *
 * @author Michael
 */
public class Block implements Serializable {
    
    public Feature parent;
    public int min;
    public int max;
    public Color color = Color.lightGray;
    
    public Block(Feature parent, int min, int max)
    {
        this.parent = parent;
        this.min = min;
        this.max = max;
    }
    
     public Block(Feature parent, int min, int max, Color color)
    {
        this.parent = parent;
        this.min = min;
        this.max = max;
        this.color = color;
    }
    
    public String getName()
    {
        return parent.name;
    }
    
    @Override
    public String toString()
    {
        return "["+getName()+", "+min+"-"+max+"]";
    }
    
    public Block clone(Feature parent)
    {
        return new Block(parent, min, max, color);
    }
    
}
