/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.alignment;

import java.awt.Color;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class LegendItemPanel extends javax.swing.JPanel {

    /**
     * Creates new form LegendItem
     */
    public LegendItemPanel() {
        initComponents();
    }
    
    public void setSelected(boolean selected)
    {
        this.legendCheckBoxItem.setSelected(selected);
    }
    
    public void setColor(Color c)
    {
        this.colorSwatch.setBackground(c);
    }
    
    public void setText(String s)
    {
        this.legendCheckBoxItem.setText(s);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 32767));
        legendCheckBoxItem = new javax.swing.JCheckBox();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 2), new java.awt.Dimension(0, 2), new java.awt.Dimension(32767, 2));
        colorSwatch = new javax.swing.JLabel();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 32767));

        setPreferredSize(new java.awt.Dimension(50, 30));
        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.LINE_AXIS));
        add(filler1);

        legendCheckBoxItem.setSelected(true);
        legendCheckBoxItem.setText("con1");
        legendCheckBoxItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                legendCheckBoxItemActionPerformed(evt);
            }
        });
        add(legendCheckBoxItem);
        add(filler2);

        colorSwatch.setBackground(new java.awt.Color(153, 255, 0));
        colorSwatch.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        colorSwatch.setMaximumSize(new java.awt.Dimension(16, 16));
        colorSwatch.setMinimumSize(new java.awt.Dimension(16, 16));
        colorSwatch.setOpaque(true);
        colorSwatch.setPreferredSize(new java.awt.Dimension(16, 16));
        add(colorSwatch);
        add(filler3);
    }// </editor-fold>//GEN-END:initComponents

    private void legendCheckBoxItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_legendCheckBoxItemActionPerformed
        
    }//GEN-LAST:event_legendCheckBoxItemActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel colorSwatch;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    public javax.swing.JCheckBox legendCheckBoxItem;
    // End of variables declaration//GEN-END:variables

}