/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.alignment;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.io.File;
import javax.swing.JLabel;
import javax.swing.JPanel;
import nava.data.io.IO;
import nava.data.types.AlignmentData;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class AlignmentEditor extends javax.swing.JPanel {

    /**
     * Creates new form AlignmentEditor
     */
    public AlignmentEditor() {
        initComponents();

        AlignmentData al = new AlignmentData();
        IO.loadFastaSequences(new File("examples/alignments/hiv500.fas"), al.sequences, al.sequenceNames);
        
        SequenceListModel sequenceModel = new SequenceListModel();
        sequenceModel.setAlignment(al);
        
        JPanel panel = new JPanel();
        AlignmentPanel alignmentPanel = new AlignmentPanel(sequenceModel);
        
        leftPanel.add(panel, BorderLayout.CENTER);
        rightScrollPane.setViewportView(alignmentPanel);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSplitPane1 = new javax.swing.JSplitPane();
        leftPanel = new javax.swing.JPanel();
        rightPanel = new javax.swing.JPanel();
        rightScrollPane = new javax.swing.JScrollPane();

        setLayout(new java.awt.BorderLayout());

        jSplitPane1.setDividerLocation(80);

        leftPanel.setLayout(new java.awt.BorderLayout());
        jSplitPane1.setLeftComponent(leftPanel);

        rightPanel.setLayout(new java.awt.BorderLayout());
        rightPanel.add(rightScrollPane, java.awt.BorderLayout.CENTER);

        jSplitPane1.setRightComponent(rightPanel);

        add(jSplitPane1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JPanel leftPanel;
    private javax.swing.JPanel rightPanel;
    private javax.swing.JScrollPane rightScrollPane;
    // End of variables declaration//GEN-END:variables
}
