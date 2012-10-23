/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.structurevis;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class Layer 
{
    private JPanel left;
    private JPanel right;
    
    public Layer(JPanel left, JPanel right)
    {
        this.left = left;
        this.right = right;
        
        int layerHeight = Math.max(left.getPreferredSize().height, right.getPreferredSize().height);
        
        left.setMinimumSize(new Dimension(0, layerHeight));
        left.setPreferredSize(new Dimension(100, layerHeight));
        left.setMaximumSize(new Dimension(Integer.MAX_VALUE, layerHeight));
        
        right.setMinimumSize(new Dimension(0, layerHeight));
        right.setPreferredSize(new Dimension(100, layerHeight));
        right.setMaximumSize(new Dimension(Integer.MAX_VALUE, layerHeight));
    }
    
    public JPanel getLeft()
    {
        return this.left;
    }
    
    public JPanel getRight()
    {
        return this.right;
    }
}