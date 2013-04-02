/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.structurevis.layerpanel;

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class LabelLayer extends JPanel {
    
    JLabel label = new JLabel("");
    String labelText;
    
    public LabelLayer(String labelText)
    {
        this.labelText = labelText;
        
        setLayout(new BorderLayout());
        label = new JLabel(labelText);
        add(label, BorderLayout.WEST);
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.darkGray));
    }
    
    public void setText(String labelText)
    {
        label.setText(labelText);
    }
    
}
