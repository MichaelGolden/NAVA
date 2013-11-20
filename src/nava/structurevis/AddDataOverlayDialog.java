/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.structurevis;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import nava.data.types.Alignment;
import nava.data.types.Matrix;
import nava.data.types.SecondaryStructure;
import nava.data.types.Tabular;
import nava.ui.MainFrame;
import nava.ui.ProjectController;
import nava.utils.GraphicsUtils;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class AddDataOverlayDialog extends javax.swing.JDialog {

    ProjectController projectController;
    StructureVisController structureVisController;

    /**
     * Creates new form AddDataOverlayDialog
     */
    public AddDataOverlayDialog(java.awt.Frame parent, boolean modal, ProjectController projectController, StructureVisController structureVisController) {
        super(parent, modal);
        initComponents();
        this.projectController = projectController;
        this.structureVisController = structureVisController;
        
        this.setIconImage(new ImageIcon(ClassLoader.getSystemResource("resources/icons/icon-32x32.png")).getImage());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        add1DButton = new javax.swing.JButton();
        add2DButton = new javax.swing.JButton();
        addNucleotideButton = new javax.swing.JButton();
        addStructureButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Add data overlay");

        add1DButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/icons/tabular-16x16.png"))); // NOI18N
        add1DButton.setText("Add 1D overlay");
        add1DButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        add1DButton.setIconTextGap(10);
        add1DButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                add1DButtonActionPerformed(evt);
            }
        });

        add2DButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/icons/matrix-16x16.png"))); // NOI18N
        add2DButton.setText("Add 2D overlay");
        add2DButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        add2DButton.setIconTextGap(10);
        add2DButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                add2DButtonActionPerformed(evt);
            }
        });

        addNucleotideButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/icons/nucleotide-alignment-16x16.png"))); // NOI18N
        addNucleotideButton.setText("Add nucleotide overlay");
        addNucleotideButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        addNucleotideButton.setIconTextGap(10);
        addNucleotideButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addNucleotideButtonActionPerformed(evt);
            }
        });

        addStructureButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/icons/structure-16x16.png"))); // NOI18N
        addStructureButton.setText("Add structure");
        addStructureButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        addStructureButton.setIconTextGap(10);
        addStructureButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addStructureButtonActionPerformed(evt);
            }
        });

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(cancelButton)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(add1DButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(add2DButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(addStructureButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(addNucleotideButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(add1DButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(add2DButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(addStructureButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(addNucleotideButton)
                .addGap(18, 18, 18)
                .addComponent(cancelButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        this.dispose();
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void addNucleotideButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addNucleotideButtonActionPerformed
      
        if(projectController.projectModel.dataSourcesContainInstanceOf(Alignment.class))
        {
            this.dispose();
            NucleotideCompositionDialog d = new NucleotideCompositionDialog(null, true, projectController.projectModel, structureVisController);
            d.setSize(500, 150);
            GraphicsUtils.centerWindowOnWindow(d, MainFrame.self);
            d.setEditMode(null);
            d.setVisible(true);
        }
        else
        {
               JOptionPane.showMessageDialog(MainFrame.self, "You need to import or create an alignment data source in the\n'Data input' tab before adding a nucleotide data overlay.", "Cannot create nucleotide overlay",  JOptionPane.WARNING_MESSAGE);
        }        
    }//GEN-LAST:event_addNucleotideButtonActionPerformed

    private void add1DButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_add1DButtonActionPerformed
        if(projectController.projectModel.dataSourcesContainInstanceOf(Tabular.class))
        {
            this.dispose();
            Data1DDialog d = new Data1DDialog(null, true, projectController.projectModel, structureVisController);
            d.setSize(920, 690);
            GraphicsUtils.centerWindowOnWindow(d, MainFrame.self);
            d.setEditMode(null);
            d.setVisible(true);
        }
        else
        {
               JOptionPane.showMessageDialog(MainFrame.self, "You need to import or create a tabular data source in the\n'Data input' tab before adding a one-dimensional data overlay.", "Cannot create one-dimensional overlay",  JOptionPane.WARNING_MESSAGE);
        }  
    }//GEN-LAST:event_add1DButtonActionPerformed

    private void add2DButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_add2DButtonActionPerformed

        if(projectController.projectModel.dataSourcesContainInstanceOf(Matrix.class))
        {
            this.dispose();
            Data2DDialog d = new Data2DDialog(null, true, projectController.projectModel, structureVisController);
            d.setSize(750, 720);
            GraphicsUtils.centerWindowOnWindow(d, MainFrame.self);
            d.setEditMode(null);
            d.setVisible(true);
        }
        else
        {
               JOptionPane.showMessageDialog(MainFrame.self, "You need to import or create a matrix data source in the\n'Data input' tab before adding a two-dimensional data overlay.", "Cannot create two-dimensional overlay",  JOptionPane.WARNING_MESSAGE);
        } 
    }//GEN-LAST:event_add2DButtonActionPerformed

    private void addStructureButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addStructureButtonActionPerformed
        if(projectController.projectModel.dataSourcesContainInstanceOf(SecondaryStructure.class))
        {
            this.dispose();
            StructureOverlayDialog d = new StructureOverlayDialog(null, true, projectController.projectModel, structureVisController);
            d.setSize(640, 580);
            GraphicsUtils.centerWindowOnWindow(d, MainFrame.self);
            d.setEditMode(null);
            d.setVisible(true);
        }
        else
        {
               JOptionPane.showMessageDialog(MainFrame.self, "You need to import or create a secondary structure data source in the\n'Data input' tab before adding a structure overlay.", "Cannot create structure overlay",  JOptionPane.WARNING_MESSAGE);
        } 
        
    }//GEN-LAST:event_addStructureButtonActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton add1DButton;
    private javax.swing.JButton add2DButton;
    private javax.swing.JButton addNucleotideButton;
    private javax.swing.JButton addStructureButton;
    private javax.swing.JButton cancelButton;
    // End of variables declaration//GEN-END:variables
}
