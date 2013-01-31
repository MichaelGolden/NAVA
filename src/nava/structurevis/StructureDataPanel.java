/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.structurevis;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Collections;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import nava.data.types.*;
import nava.structurevis.data.MappingSource;
import nava.structurevis.data.StructureSource;
import nava.ui.MainFrame;
import nava.ui.ProjectModel;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class StructureDataPanel extends javax.swing.JPanel implements ChangeListener, ItemListener {

    ProjectModel projectModel;
    DefaultComboBoxModel<SecondaryStructure> structureComboBoxModel = new DefaultComboBoxModel<>();
    DefaultComboBoxModel<Alignment> alignmentComboBoxModel = new DefaultComboBoxModel<>();
    StructureSource structureSource;
    SpinnerNumberModel minSpinnerModel = new SpinnerNumberModel(10, 1, Integer.MAX_VALUE, 1);
    SpinnerNumberModel maxSpinnerModel = new SpinnerNumberModel(250, 0, Integer.MAX_VALUE, 1);

    /**
     * Creates new form StructureDataPanel
     */
    public StructureDataPanel(ProjectModel projectModel) {
        initComponents();
        this.projectModel = projectModel;

        this.structureComboBox.setModel(structureComboBoxModel);
        this.structureComboBox.addItemListener(this);

        this.alignmentComboBox.setModel(alignmentComboBoxModel);
        this.alignmentComboBox.addItemListener(this);

        this.minSpinner.setModel(this.minSpinnerModel);
        this.minSpinner.addChangeListener(this);
        this.maxSpinner.setModel(this.maxSpinnerModel);
        this.maxSpinner.addChangeListener(this);

        populateStructureComboBox(Collections.list(projectModel.dataSources.elements()));
        populateAlignmentComboBox(Collections.list(projectModel.dataSources.elements()));
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
        jPanel2 = new javax.swing.JPanel();
        minSpinner = new javax.swing.JSpinner();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        maxSpinner = new javax.swing.JSpinner();
        jCheckBox1 = new javax.swing.JCheckBox();
        jButton3 = new javax.swing.JButton();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        linearRadioButton = new javax.swing.JRadioButton();
        jLabel2 = new javax.swing.JLabel();
        circularRadioButton = new javax.swing.JRadioButton();

        structureComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel1.setText("Select structure");

        jLabel3.setText("or");

        jButton1.setText("Add structure from file");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Choose a mapping alignment"));

        mappingGroup.add(embeddSequenceRadioButton);
        embeddSequenceRadioButton.setSelected(true);
        embeddSequenceRadioButton.setText("Use embedded sequence");

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

        mappingGroup.add(fromSequenceRadioButton);
        fromSequenceRadioButton.setText("From sequence string");

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
                            .addComponent(sequenceTextField))))
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
                .addGap(12, 12, 12)
                .addComponent(fromSequenceRadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sequenceTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Create a list of substructures"));

        jLabel6.setText("Min. substructure length");

        jLabel7.setText("Max. substructure length");

        jCheckBox1.setText("Force substructures to be non-overlapping");

        jButton3.setText("Test settings");

        substructureGroup.add(jRadioButton1);
        jRadioButton1.setSelected(true);
        jRadioButton1.setText("Automatically generate a list of substructures");

        substructureGroup.add(jRadioButton2);
        jRadioButton2.setText("From a file");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jCheckBox1)
                                .addGap(18, 18, 18)
                                .addComponent(jButton3))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(minSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(maxSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addComponent(jRadioButton1)
                    .addComponent(jRadioButton2))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jRadioButton1)
                .addGap(6, 6, 6)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(minSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7)
                    .addComponent(maxSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jCheckBox1)
                    .addComponent(jButton3))
                .addGap(18, 18, 18)
                .addComponent(jRadioButton2)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void fromAlignmentRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fromAlignmentRadioButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_fromAlignmentRadioButtonActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox alignmentComboBox;
    private javax.swing.JRadioButton circularRadioButton;
    private javax.swing.ButtonGroup conformationGroup;
    private javax.swing.JRadioButton embeddSequenceRadioButton;
    private javax.swing.JRadioButton fromAlignmentRadioButton;
    private javax.swing.JRadioButton fromSequenceRadioButton;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JRadioButton linearRadioButton;
    private javax.swing.ButtonGroup mappingGroup;
    private javax.swing.JSpinner maxSpinner;
    private javax.swing.JSpinner minSpinner;
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
        MappingSource mappingSource = null;
        if (this.embeddSequenceRadioButton.isSelected()) {
            SecondaryStructureData data = (SecondaryStructureData) structure.getObject(MainFrame.dataSourceCache);
            mappingSource = new MappingSource(data.sequence);
            System.out.println(data.sequence);
        } else if (this.fromAlignmentRadioButton.isSelected()) {
            mappingSource = new MappingSource((Alignment) alignmentComboBox.getSelectedItem());
        } else if (this.fromSequenceRadioButton.isSelected()) {
            mappingSource = new MappingSource(sequenceTextField.getText());
        }
        structureSource = new StructureSource(structure, mappingSource);

        if (this.circularRadioButton.isSelected()) {
            structureSource.circular = true;
        }
    }

    public void post() {
        update();
        structureSource.loadData();
        if (structureSource != null && structureSource.pairedSites != null) {
            structureSource.substructures = StructureSource.enumerateAdjacentSubstructures(structureSource.pairedSites, 10, 250, false);
            //System.out.println(structureSource.substructures);
        }
    }

    public StructureSource getStructureSource() {
        return structureSource;
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        int min = (Integer) minSpinner.getValue();
        int max = (Integer) maxSpinner.getValue();
        if (e.getSource().equals(minSpinner)) {
            if (min > max) {
                maxSpinnerModel.setValue(min);
            }
        }
        if (e.getSource().equals(maxSpinner)) {
            if (min > max) {
                minSpinnerModel.setValue(max);
            }
        }
    }
}
