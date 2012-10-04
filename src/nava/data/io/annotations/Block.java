/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.data.io.annotations;

/**
 *
 * @author Michael
 */
public class Block {
    
    Feature parent;
    int min;
    int max;
    
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
