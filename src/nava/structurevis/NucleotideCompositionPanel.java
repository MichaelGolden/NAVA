/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.structurevis;

import java.util.List;
import javax.swing.DefaultComboBoxModel;
import nava.data.types.Alignment;
import nava.data.types.DataSource;
import nava.structurevis.data.NucleotideComposition;
import nava.ui.ProjectModel;
import nava.utils.AlignmentType;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class NucleotideCompositionPanel extends javax.swing.JPanel {

    ProjectModel projectModel;
    DefaultComboBoxModel<Alignment> nucleotideAlignmentComboBoxModel = new DefaultComboBoxModel<>();
    Alignment selectedAlignment;

    /**
     * Creates new form NucleotideCompositionPanel
     */
    public NucleotideCompositionPanel(ProjectModel projectModel) {
        initComponents();
        this.projectModel = projectModel;

        
        
        //widerDropDownComboBox1.setPreferredSize(new Dimension(80,20));
        widerDropDownComboBox1.setModel(nucleotideAlignmentComboBoxModel);
      

        populateNucleotideAlignmentComboBox(projectModel.dataSources.getArrayListShallowCopy());
        widerDropDownComboBox1.setWide(true);
    }

    public void populateNucleotideAlignmentComboBox(List<DataSource> dataSources) {
        //Alignment alignment = (Alignment) nucleotideAlignmentComboBox.getSelectedItem();

        nucleotideAlignmentComboBoxModel.removeAllElements();
        for (DataSource dataSource : dataSources) {
            if (dataSource instanceof Alignment) {
                Alignment al = (Alignment) dataSource;
                if (al.alignmentType == AlignmentType.NUCLEOTIDE_ALIGNMENT || al.alignmentType == AlignmentType.CODON_ALIGNMENT) {
                    nucleotideAlignmentComboBoxModel.addElement(al);
                }
            }
        }
    }

    public void setNucleotideSource(NucleotideComposition nucleotideComposition) {
        this.nucleotideAlignmentComboBoxModel.setSelectedItem(nucleotideComposition.alignment);
    }

    public static NucleotideComposition getNucleotideSource(Alignment alignment) {
        if (alignment != null) {
            NucleotideComposition nuc = new NucleotideComposition(alignment);
            nuc.title = alignment.title;
            return nuc;
        }
        return null;
    }

    public NucleotideComposition getNucleotideSource() {
        return getNucleotideSource((Alignment) nucleotideAlignmentComboBoxModel.getSelectedItem());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        widerDropDownComboBox1 = new nava.utils.WiderDropDownComboBox();

        jLabel1.setText("Select nucleotide source");

        widerDropDownComboBox1.setMaximumSize(new java.awt.Dimension(100, 32767));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(widerDropDownComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(widerDropDownComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 26, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private nava.utils.WiderDropDownComboBox widerDropDownComboBox1;
    // End of variables declaration//GEN-END:variables
}
