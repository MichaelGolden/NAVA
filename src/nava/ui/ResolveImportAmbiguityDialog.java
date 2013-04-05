/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.ui;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.ArrayList;
import javax.swing.DefaultComboBoxModel;
import nava.data.types.DataType;
import nava.utils.CustomItem;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class ResolveImportAmbiguityDialog extends javax.swing.JDialog implements ItemListener {

    ArrayList<DataType> possibleDataTypes;
    DefaultComboBoxModel<CustomItem<DataType>> alignmentComboBoxModel = new DefaultComboBoxModel<>();
    DefaultComboBoxModel<CustomItem<DataType>> annotationsComboBoxModel = new DefaultComboBoxModel<>();
    DefaultComboBoxModel<CustomItem<DataType>> matrixComboBoxModel = new DefaultComboBoxModel<>();
    DefaultComboBoxModel<CustomItem<DataType>> structureComboBoxModel = new DefaultComboBoxModel<>();
    DefaultComboBoxModel<CustomItem<DataType>> tabularComboBoxModel = new DefaultComboBoxModel<>();

    /**
     * Creates new form ResolveImportAmbiguityDialog
     */
    public ResolveImportAmbiguityDialog(java.awt.Frame parent, boolean modal, File importFile, ArrayList<DataType> possibleDataTypes) {
        super(parent, modal);
        initComponents();
        this.possibleDataTypes = possibleDataTypes;
        System.out.println(possibleDataTypes);
        this.alignmentComboBox.setModel(alignmentComboBoxModel);
        this.annotationsComboBox.setModel(annotationsComboBoxModel);
        this.matrixComboBox.setModel(matrixComboBoxModel);
        this.structureComboBox.setModel(structureComboBoxModel);
        this.tabularComboBox.setModel(tabularComboBoxModel);

         this.alignmentComboBox.addItemListener(this);
        this.annotationsComboBox.addItemListener(this);
        this.matrixComboBox.addItemListener(this);
        this.structureComboBox.addItemListener(this);
        this.tabularComboBox.addItemListener(this);
        
        this.alignmentRadioButton.addItemListener(this);
        this.annotationsRadioButton.addItemListener(this);
        this.matrixRadioButton.addItemListener(this);
        this.structureRadioButton.addItemListener(this);
        this.tabularRadioButton.addItemListener(this);
        
        this.jTextField1.setText(importFile.getAbsolutePath());
        this.jTextField1.setCaretPosition(importFile.getAbsolutePath().length());
        initialise();
    }
    private DataType selectedDataType = null;

    public void initialise() {

        this.alignmentRadioButton.setEnabled(false);
        this.alignmentComboBox.setEnabled(false);
        this.annotationsRadioButton.setEnabled(false);
        this.annotationsComboBox.setEnabled(false);
        this.matrixRadioButton.setEnabled(false);
        this.matrixComboBox.setEnabled(false);
        this.structureRadioButton.setEnabled(false);
        this.structureComboBox.setEnabled(false);
        this.tabularRadioButton.setEnabled(false);
        this.tabularComboBox.setEnabled(false);
        if (possibleDataTypes == null) {
        } else {
            for (int i = 0; i < possibleDataTypes.size(); i++) {
                DataType dataType = possibleDataTypes.get(possibleDataTypes.size() - 1 - i);
                switch (dataType.primaryType) {
                    case ALIGNMENT:
                        this.alignmentRadioButton.setEnabled(true);
                        this.alignmentComboBox.setEnabled(true);
                        alignmentComboBoxModel.addElement(getItem(dataType));
                        alignmentRadioButton.setSelected(true);
                        alignmentComboBoxModel.setSelectedItem(getItem(dataType));
                        break;
                    case ANNOTATION_DATA:
                        this.annotationsRadioButton.setEnabled(true);
                        this.annotationsComboBox.setEnabled(true);
                        annotationsComboBoxModel.addElement(getItem(dataType));
                        annotationsRadioButton.setSelected(true);
                        annotationsComboBoxModel.setSelectedItem(getItem(dataType));
                        break;
                    case MATRIX:
                        this.matrixRadioButton.setEnabled(true);
                        this.matrixComboBox.setEnabled(true);
                        matrixComboBoxModel.addElement(getItem(dataType));
                        matrixRadioButton.setSelected(true);
                        matrixComboBoxModel.setSelectedItem(getItem(dataType));
                        break;
                    case SECONDARY_STRUCTURE:
                        this.structureRadioButton.setEnabled(true);
                        this.structureComboBox.setEnabled(true);
                        structureComboBoxModel.addElement(getItem(dataType));
                        structureRadioButton.setSelected(true);
                        structureComboBoxModel.setSelectedItem(getItem(dataType));
                        break;
                    case TABULAR_DATA:
                        this.tabularRadioButton.setEnabled(true);
                        this.tabularComboBox.setEnabled(true);
                        tabularComboBoxModel.addElement(getItem(dataType));
                        tabularRadioButton.setSelected(true);
                        tabularComboBoxModel.setSelectedItem(getItem(dataType));
                        break;
                }
            }
        }
    }

    public CustomItem<DataType> getItem(DataType dataType) {
        return new CustomItem<>(dataType, dataType.fileFormat.toString());
    }
    
    public DataType getSelectedDataType()
    {
        return selectedDataType;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        structureComboBox = new javax.swing.JComboBox();
        annotationsComboBox = new javax.swing.JComboBox();
        alignmentComboBox = new javax.swing.JComboBox();
        tabularComboBox = new javax.swing.JComboBox();
        alignmentRadioButton = new javax.swing.JRadioButton();
        matrixRadioButton = new javax.swing.JRadioButton();
        annotationsRadioButton = new javax.swing.JRadioButton();
        tabularRadioButton = new javax.swing.JRadioButton();
        structureRadioButton = new javax.swing.JRadioButton();
        matrixComboBox = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        importButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Resolve data import type ambiguity");
        setModal(true);
        setResizable(false);

        structureComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        annotationsComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        alignmentComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        tabularComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        buttonGroup1.add(alignmentRadioButton);
        alignmentRadioButton.setText("Alignment");

        buttonGroup1.add(matrixRadioButton);
        matrixRadioButton.setText("Matrix");

        buttonGroup1.add(annotationsRadioButton);
        annotationsRadioButton.setText("Annotations");

        buttonGroup1.add(tabularRadioButton);
        tabularRadioButton.setText("Tabular data");
        tabularRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tabularRadioButtonActionPerformed(evt);
            }
        });

        buttonGroup1.add(structureRadioButton);
        structureRadioButton.setText("Secondary structure");

        matrixComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel1.setText("The following data file can be imported using more than one data type:");

        jLabel2.setText("Data file");

        jTextField1.setEditable(false);
        jTextField1.setText("jTextField1");

        jLabel3.setText("Please select the one you would like to use.");

        importButton.setText("Import");
        importButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                importButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(importButton)
                .addGap(10, 10, 10))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel2)
                        .addGap(11, 11, 11)
                        .addComponent(jTextField1))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(13, 13, 13)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(tabularRadioButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(tabularComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 217, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(annotationsRadioButton)
                                    .addComponent(alignmentRadioButton)
                                    .addComponent(matrixRadioButton)
                                    .addComponent(structureRadioButton))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                            .addGap(10, 10, 10)
                                            .addComponent(alignmentComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 217, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addComponent(annotationsComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 217, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(matrixComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 217, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(structureComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 217, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(0, 62, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel3))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(alignmentComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(alignmentRadioButton))
                .addGap(8, 8, 8)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(annotationsComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(annotationsRadioButton))
                .addGap(8, 8, 8)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(matrixComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(matrixRadioButton))
                .addGap(8, 8, 8)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(structureComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(structureRadioButton))
                .addGap(8, 8, 8)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 41, Short.MAX_VALUE)
                        .addComponent(importButton))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tabularComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tabularRadioButton))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tabularRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tabularRadioButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tabularRadioButtonActionPerformed

    private void importButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_importButtonActionPerformed
       updateSelectedDataType();
       System.out.println(this.selectedDataType);
       this.dispose();
    }//GEN-LAST:event_importButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /*
         * Set the Nimbus look and feel
         */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /*
         * If Nimbus (introduced in Java SE 6) is not available, stay with the
         * default look and feel. For details see
         * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ResolveImportAmbiguityDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ResolveImportAmbiguityDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ResolveImportAmbiguityDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ResolveImportAmbiguityDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /*
         * Create and display the dialog
         */
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                ResolveImportAmbiguityDialog dialog = new ResolveImportAmbiguityDialog(new javax.swing.JFrame(), true, new File("something"), null);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {

                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox alignmentComboBox;
    private javax.swing.JRadioButton alignmentRadioButton;
    private javax.swing.JComboBox annotationsComboBox;
    private javax.swing.JRadioButton annotationsRadioButton;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton importButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JComboBox matrixComboBox;
    private javax.swing.JRadioButton matrixRadioButton;
    private javax.swing.JComboBox structureComboBox;
    private javax.swing.JRadioButton structureRadioButton;
    private javax.swing.JComboBox tabularComboBox;
    private javax.swing.JRadioButton tabularRadioButton;
    // End of variables declaration//GEN-END:variables

    public void updateSelectedDataType()
    {
         if (this.alignmentRadioButton.isSelected()) {
            selectedDataType = ((CustomItem<DataType>) alignmentComboBoxModel.getSelectedItem()).getObject();
        } else if (this.annotationsRadioButton.isSelected()) {
            selectedDataType = ((CustomItem<DataType>) annotationsComboBoxModel.getSelectedItem()).getObject();
        } else if (this.matrixRadioButton.isSelected()) {
            selectedDataType = ((CustomItem<DataType>) matrixComboBoxModel.getSelectedItem()).getObject();
        } else if (this.structureRadioButton.isSelected()) {
            selectedDataType = ((CustomItem<DataType>) structureComboBoxModel.getSelectedItem()).getObject();
        } else if (this.tabularRadioButton.isSelected()) {
            selectedDataType = ((CustomItem<DataType>) tabularComboBoxModel.getSelectedItem()).getObject();
        }
    }
    
    @Override
    public void itemStateChanged(ItemEvent e) {
        this.alignmentComboBox.setEnabled(false);
        this.annotationsComboBox.setEnabled(false);
        this.matrixComboBox.setEnabled(false);
        this.structureComboBox.setEnabled(false);
        this.tabularComboBox.setEnabled(false);
        
        if (this.alignmentRadioButton.isSelected()) {
            alignmentComboBox.setEnabled(true);
        } else if (this.annotationsRadioButton.isSelected()) {
            annotationsComboBox.setEnabled(true);
        } else if (this.matrixRadioButton.isSelected()) {
            matrixComboBox.setEnabled(true);
        } else if (this.structureRadioButton.isSelected()) {
            this.structureComboBox.setEnabled(true);
        } else if (this.tabularRadioButton.isSelected()) {
            this.tabularComboBox.setEnabled(true);
        }
        
        updateSelectedDataType();
    }
}
