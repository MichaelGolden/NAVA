/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.structurevis;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListDataEvent;
import nava.data.types.*;
import nava.structurevis.data.MappingSource;
import nava.structurevis.data.StructureOverlay;
import nava.structurevis.data.StructureOverlay.MappingSourceOption;
import nava.structurevis.data.SubstructureList;
import nava.ui.MainFrame;
import nava.ui.ProjectController;
import nava.ui.ProjectModel;
import nava.ui.ProjectView;
import nava.utils.GraphicsUtils;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class StructureOverlayPanel extends javax.swing.JPanel implements ChangeListener, ItemListener, KeyListener {

    //ProjectController projectController;
    ProjectModel projectModel;
    DefaultComboBoxModel<SecondaryStructure> structureComboBoxModel = new DefaultComboBoxModel<>();
    DefaultComboBoxModel<Alignment> alignmentComboBoxModel = new DefaultComboBoxModel<>();
    StructureOverlay structureOverlay = new StructureOverlay();
    //SpinnerNumberModel minSpinnerModel = new SpinnerNumberModel(10, 1, Integer.MAX_VALUE, 1);
    //SpinnerNumberModel maxSpinnerModel = new SpinnerNumberModel(250, 0, Integer.MAX_VALUE, 1);

    /**
     * Creates new form StructureDataPanel
     */
    public StructureOverlayPanel(ProjectModel projectModel) {
        initComponents();

        this.projectModel = projectModel;

        this.structureComboBox.setModel(structureComboBoxModel);
        this.structureComboBox.addItemListener(this);

        this.alignmentComboBox.setModel(alignmentComboBoxModel);
        this.alignmentComboBox.addItemListener(this);

        //this.minSpinner.setModel(this.minSpinnerModel);
        //this.minSpinner.addChangeListener(this);
        //this.maxSpinner.setModel(this.maxSpinnerModel);
        //this.maxSpinner.addChangeListener(this);
        circularRadioButton.addItemListener(this);
        sequenceTextField.addKeyListener(this);
        populateStructureComboBox(projectModel.dataSources.getArrayListShallowCopy());
        populateAlignmentComboBox(projectModel.dataSources.getArrayListShallowCopy());
    }

    public void populateStructureComboBox(List<DataSource> dataSources) {
        structureComboBoxModel.removeAllElements();
        for (int i = 0; i < dataSources.size(); i++) {
            if (dataSources.get(i) instanceof SecondaryStructure) {
                structureComboBoxModel.addElement((SecondaryStructure) dataSources.get(i));
            }
        }
    }

    public void populateAlignmentComboBox(List<DataSource> dataSources) {
        alignmentComboBoxModel.removeAllElements();
        for (int i = 0; i < dataSources.size(); i++) {
            if (dataSources.get(i) instanceof Alignment) {
                alignmentComboBoxModel.addElement((Alignment) dataSources.get(i));
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mappingGroup = new javax.swing.ButtonGroup();
        substructureGroup = new javax.swing.ButtonGroup();
        conformationGroup = new javax.swing.ButtonGroup();
        structureComboBox = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        embeddSequenceRadioButton = new javax.swing.JRadioButton();
        fromAlignmentRadioButton = new javax.swing.JRadioButton();
        jLabel4 = new javax.swing.JLabel();
        alignmentComboBox = new javax.swing.JComboBox();
        jLabel5 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        fromSequenceRadioButton = new javax.swing.JRadioButton();
        sequenceTextField = new javax.swing.JTextField();
        addMappingAlignmentAsOverlayCheckBox = new javax.swing.JCheckBox();
        jButton4 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jButton3 = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        linearRadioButton = new javax.swing.JRadioButton();
        jLabel2 = new javax.swing.JLabel();
        circularRadioButton = new javax.swing.JRadioButton();

        structureComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel1.setText("Select structure");

        jLabel3.setText("or");

        jButton1.setText("Add structure from file");
        jButton1.setEnabled(false);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Choose a mapping alignment"));

        mappingGroup.add(embeddSequenceRadioButton);
        embeddSequenceRadioButton.setSelected(true);
        embeddSequenceRadioButton.setText("Use embedded sequence alignment or sequence");

        mappingGroup.add(fromAlignmentRadioButton);
        fromAlignmentRadioButton.setText("From alignment");
        fromAlignmentRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fromAlignmentRadioButtonActionPerformed(evt);
            }
        });

        jLabel4.setText("Select alignment");

        alignmentComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel5.setText("or");

        jButton2.setText("Add alignment from file");
        jButton2.setEnabled(false);

        mappingGroup.add(fromSequenceRadioButton);
        fromSequenceRadioButton.setText("Paste from sequence string");

        sequenceTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sequenceTextFieldActionPerformed(evt);
            }
        });

        addMappingAlignmentAsOverlayCheckBox.setSelected(true);
        addMappingAlignmentAsOverlayCheckBox.setText("Add this mapping alignment as a nucleotide overlay (recommended)");

        jButton4.setText("Paste");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(fromSequenceRadioButton)
                    .addComponent(fromAlignmentRadioButton)
                    .addComponent(embeddSequenceRadioButton)
                    .addComponent(addMappingAlignmentAsOverlayCheckBox)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(alignmentComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jButton2))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(sequenceTextField)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton4)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(embeddSequenceRadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(fromAlignmentRadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(alignmentComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(jButton2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(fromSequenceRadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton4)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(sequenceTextField)
                        .addGap(2, 2, 2)))
                .addGap(8, 8, 8)
                .addComponent(addMappingAlignmentAsOverlayCheckBox)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Create a list of substructures"));

        jButton3.setText("Edit list");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jLabel8.setText("A list has already been generated");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton3)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(jButton3))
                .addGap(0, 112, Short.MAX_VALUE))
        );

        conformationGroup.add(linearRadioButton);
        linearRadioButton.setSelected(true);
        linearRadioButton.setText("Linear");

        jLabel2.setText("Conformation");

        conformationGroup.add(circularRadioButton);
        circularRadioButton.setText("Circular");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(9, 9, 9)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addGap(18, 18, 18)
                                .addComponent(linearRadioButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(circularRadioButton)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(structureComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jButton1)
                                .addContainerGap())))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(structureComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(jButton1))
                .addGap(11, 11, 11)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(linearRadioButton)
                    .addComponent(circularRadioButton))
                .addGap(8, 8, 8)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void fromAlignmentRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fromAlignmentRadioButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_fromAlignmentRadioButtonActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        if (structureOverlay != null && structureOverlay.structure != null) {
            if (structureOverlay.substructureList == null) {
                structureOverlay.substructureList = new SubstructureList(structureOverlay);
            }
            SubstructureListDialog dialog = new SubstructureListDialog(new javax.swing.JFrame(), true, structureOverlay.substructureList.clone());
            GraphicsUtils.centerWindowOnScreen(dialog);
            dialog.setVisible(true);
            if (dialog.save) {
                System.out.println("saving list");
                structureOverlay.substructureList = dialog.substructureList;
                System.out.println("isRecusrive?"+structureOverlay.substructureList.recursive);
                jLabel8.setText("You have defined a list. Click to edit.");
            }

        }
        System.out.println(structureOverlay.substructureList);
    }//GEN-LAST:event_jButton3ActionPerformed

    private void sequenceTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sequenceTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_sequenceTextFieldActionPerformed

    
    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        Transferable contents = clipboard.getContents(null);
        
        boolean hasTransferableText = (contents != null) && contents.isDataFlavorSupported(DataFlavor.stringFlavor);
    
        if(hasTransferableText) {
            try {
                sequenceTextField.setText((String)contents.getTransferData(DataFlavor.stringFlavor));
                fromSequenceRadioButton.setSelected(true);
            }
            catch (UnsupportedFlavorException | IOException ex){
            }
        }
    }//GEN-LAST:event_jButton4ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox addMappingAlignmentAsOverlayCheckBox;
    private javax.swing.JComboBox alignmentComboBox;
    private javax.swing.JRadioButton circularRadioButton;
    private javax.swing.ButtonGroup conformationGroup;
    private javax.swing.JRadioButton embeddSequenceRadioButton;
    private javax.swing.JRadioButton fromAlignmentRadioButton;
    private javax.swing.JRadioButton fromSequenceRadioButton;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JRadioButton linearRadioButton;
    private javax.swing.ButtonGroup mappingGroup;
    private javax.swing.JTextField sequenceTextField;
    private javax.swing.JComboBox structureComboBox;
    private javax.swing.ButtonGroup substructureGroup;
    // End of variables declaration//GEN-END:variables

    @Override
    public void itemStateChanged(ItemEvent e) {
        update();
    }

    public void update() {
        SecondaryStructure structure = (SecondaryStructure) structureComboBox.getSelectedItem();
        if (structure != null) {
            MappingSource mappingSource = null;

            SecondaryStructureData data = (SecondaryStructureData) structure.getObject(projectModel.getProjectPathString(), MainFrame.dataSourceCache);
            if (data.sequence.length() == 0) {
                embeddSequenceRadioButton.setEnabled(false);
                if (embeddSequenceRadioButton.isSelected()) {
                    fromAlignmentRadioButton.setSelected(true);
                }
            } else {
                embeddSequenceRadioButton.setEnabled(true);
            }

            if (this.embeddSequenceRadioButton.isSelected()) {
                mappingSource = new MappingSource(data.sequence);
            } else if (this.fromAlignmentRadioButton.isSelected()) {
                Alignment alignment = (Alignment) alignmentComboBox.getSelectedItem();
                mappingSource = new MappingSource(alignment);
            } else if (this.fromSequenceRadioButton.isSelected()) {
                mappingSource = new MappingSource(sequenceTextField.getText());
            }


            // = new StructureOverlay(structure, mappingSource);
            if (structureOverlay != null && structureOverlay.structure != null &&  structureOverlay.substructureList == null) {
                structureOverlay.substructureList = new SubstructureList(structureOverlay);
                //  if(this.circularRadioButton.isSelected() != structureOverlay.circular  || !structureOverlay.structure.equals(structure))
                //  {
                // if either parameter has changed need to regenerate list
              //  structureOverlay.substructureList = new SubstructureList(structureOverlay);
                //   jLabel8.setText("A new list has been generated.");
                // }
            }
            structureOverlay.setStructureAndMapping(structure, mappingSource);
            // structureOverlay.minStructureSize = (Integer) this.minSpinnerModel.getValue();
            // structureOverlay.maxStructureSize = (Integer) this.maxSpinnerModel.getValue();
            //structureOverlay.nonOverlappingSubstructures = this.jCheckBox1.isSelected();

            if (this.embeddSequenceRadioButton.isSelected()) {
                structureOverlay.mappingSourceOption = MappingSourceOption.EMBEDDED;
            } else if (this.fromAlignmentRadioButton.isSelected()) {
                structureOverlay.mappingSourceOption = MappingSourceOption.ALIGNMENT;
            } else if (this.fromSequenceRadioButton.isSelected()) {
                structureOverlay.mappingSourceOption = MappingSourceOption.STRING;
            }

            structureOverlay.addMappingSourceAsNucleotideOverlay = addMappingAlignmentAsOverlayCheckBox.isSelected();

            structureOverlay.circular = circularRadioButton.isSelected();
        }
    }

    public void post() {
        update();
        structureOverlay.loadData();
        if (structureOverlay != null && structureOverlay.pairedSites != null) {
            if (structureOverlay.substructureList == null) {
                structureOverlay.substructureList = new SubstructureList(structureOverlay);
            }
        }
    }

    public void setStructureSource(StructureOverlay structureOverlay) {
        // TODO
        this.structureComboBoxModel.setSelectedItem(structureOverlay.structure);
        this.circularRadioButton.setSelected(structureOverlay.circular);
        switch (structureOverlay.mappingSourceOption) {
            case EMBEDDED:
                this.embeddSequenceRadioButton.setSelected(true);
                break;
            case ALIGNMENT:
                this.fromAlignmentRadioButton.setSelected(true);         
                if(structureOverlay.mappingSource != null)
                {
                    alignmentComboBoxModel.setSelectedItem(structureOverlay.mappingSource.alignmentSource);
                }
                break;
            case STRING:
                this.fromSequenceRadioButton.setSelected(true);
                sequenceTextField.setText(structureOverlay.mappingSource.sequence);
                break;
        }
        this.structureOverlay.substructureList = structureOverlay.substructureList;
        //this.minSpinnerModel.setValue(structureSource.minStructureSize);
        //this.maxSpinnerModel.setValue(structureSource.maxStructureSize);
        //this.jCheckBox1.setSelected(structureSource.nonOverlappingSubstructures);
    }

    public StructureOverlay getStructureSource() {
        post();
        return structureOverlay;
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        if(e.getSource().equals(this.structureComboBoxModel))
        {
            SecondaryStructure structure = (SecondaryStructure) structureComboBox.getSelectedItem();
            if(structureOverlay != null)
            {
                structureOverlay.structure = structure;
                structureOverlay.substructureList = new SubstructureList(structureOverlay);
            }
            
        }
        update();
        /*
         * int min = (Integer) minSpinner.getValue(); int max = (Integer)
         * maxSpinner.getValue(); if (e.getSource().equals(minSpinner)) { if
         * (min > max) { maxSpinnerModel.setValue(min); } } if
         * (e.getSource().equals(maxSpinner)) { if (min > max) {
         * minSpinnerModel.setValue(max); } }
         *
         */
    }

    @Override
    public void keyTyped(KeyEvent e) {
        fromSequenceRadioButton.setSelected(true);
    }

    @Override
    public void keyPressed(KeyEvent e) {
       fromSequenceRadioButton.setSelected(true);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        fromSequenceRadioButton.setSelected(true);
    }
}
