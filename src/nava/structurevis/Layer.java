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
public class Layer {

    public LayerPanel parent;
    private JPanel left;
    private JPanel right;
    public int preferredHeight = -1;

    public Layer(JPanel left, JPanel right) {
        this.left = left;
        this.right = right;
    }

    public Layer(LayerPanel parent, JPanel left, JPanel right) {
        this.parent = parent;
        this.left = left;
        this.right = right;

        refresh();
    }

    public JPanel getLeft() {
        return this.left;
    }

    public JPanel getRight() {
        return this.right;
    }

    public void refresh() {
        int layerHeight = preferredHeight;
        if (layerHeight == -1) {
            layerHeight = Math.max(left.getPreferredSize().height, right.getPreferredSize().height);
        }

        left.setMinimumSize(new Dimension(0, layerHeight));
        left.setPreferredSize(new Dimension(100, layerHeight));
        left.setMaximumSize(new Dimension(Integer.MAX_VALUE, layerHeight));

        right.setMinimumSize(new Dimension(0, layerHeight));
        right.setPreferredSize(new Dimension(100, layerHeight));
        right.setMaximumSize(new Dimension(Integer.MAX_VALUE, layerHeight));
    }
}
