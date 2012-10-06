/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.structurevis.data;

import java.awt.Color;

/**
 *
 * @author Michael
 */
public class Block {
    
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
    
    public String getName()
    {
        return parent.name;
    }
    
    @Override
    public String toString()
    {
        return "["+getName()+", "+min+"-"+max+"]";
    }
}
