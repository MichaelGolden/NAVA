/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.ui;

import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.BadLocationException;
import nava.data.types.DataSource;
import nava.utils.Utils;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class ApplicationPanelItem extends javax.swing.JPanel {

    String name;
    ArrayList<DataSource> dataSourceTypes;
    String description;

    /**
     * Creates new form ApplicationPanelItem
     */
    public ApplicationPanelItem(String name, ArrayList<DataSource> dataSourceTypes, String description) {
        initComponents();
        this.name = name;
        this.dataSourceTypes = dataSourceTypes;
        this.description = description;

        this.appLabel.setText(name);
        String text = "No description available.";
        if (description != null && description.length() != 0) {
            text = description;
        }
        this.descriptionArea.setText(text);
        this.descriptionArea.setBackground(new Color(255,255,255,0));
        this.setToolTipText(Utils.plainTextToHtml(Utils.wrapText(text,70)));
    }
    
    public void setActivated(boolean activated)
    {
        if(activated)
        {
            this.appLabel.setForeground(Color.black);
            this.descriptionArea.setForeground(Color.black);
        }
        else
        {
            this.appLabel.setForeground(Color.gray);
            this.descriptionArea.setForeground(Color.gray);
        }
    }
    
    public static void main(String [] args)
    {
        //System.out.println(ApplicationPanelItem.wrapText("Hello my name is Mark, I'm a robot here to eat you.\nPlease collect $200 before passing GO. Okay I'm not quite sure what is up with that.\nNewlines are like daisies they blow in the wind, but you can't see the wind.", 50));
    }

    

    /*
     * public void resizeTextArea() { Dimension minSize = new Dimension(100,
     * 100); jTextArea2.setPreferredSize(minSize); jTextArea2.setSize(minSize);
     * try { Rectangle r =
     * jTextArea2.modelToView(jTextArea2.getDocument().getLength());
     * jTextArea2.setSize(jTextArea2.getPreferredSize());
     *
     * System.out.println("HAHAH"+jTextArea2.getBounds());
     * System.out.println("HAHAB"+jTextArea2.getPreferredScrollableViewportSize());
     * System.out.println("HAHAH"+jTextArea2.getPreferredSize());
     * jTextArea2.setPreferredSize(new Dimension(minSize.width, r.y +
     * r.height)); } catch (BadLocationException ex) {
     *
     * }
     * }
     */
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel3 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        appLabel = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        descriptionArea = new javax.swing.JTextArea();

        setBackground(new java.awt.Color(255, 255, 255));
        setBorder(javax.swing.BorderFactory.createEtchedBorder());
        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.LINE_AXIS));

        jPanel3.setMinimumSize(new java.awt.Dimension(150, 20));
        jPanel3.setOpaque(false);
        jPanel3.setLayout(new javax.swing.BoxLayout(jPanel3, javax.swing.BoxLayout.PAGE_AXIS));

        jPanel2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jPanel2.setOpaque(false);
        jPanel2.setPreferredSize(new java.awt.Dimension(100, 20));
        jPanel2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        appLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        appLabel.setText("app_name");
        jPanel2.add(appLabel);

        jPanel3.add(jPanel2);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setMinimumSize(new java.awt.Dimension(130, 20));
        jPanel1.setOpaque(false);
        jPanel1.setPreferredSize(new java.awt.Dimension(0, 24));
        jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        descriptionArea.setColumns(20);
        descriptionArea.setRows(5);
        descriptionArea.setBorder(null);
        jPanel1.add(descriptionArea);

        jPanel3.add(jPanel1);

        add(jPanel3);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel appLabel;
    private javax.swing.JTextArea descriptionArea;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    // End of variables declaration//GEN-END:variables
}
