/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.structurevis;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import nava.structurevis.data.*;
import nava.ui.ProjectController;
import nava.ui.ProjectView;
import org.biojava.bio.BioException;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class StructureVisPanel extends javax.swing.JPanel implements ItemListener, ProjectView, SubstructureModelListener {

    //DefaultComboBoxModel<Alignment> mappingSourceComboBoxModel = new DefaultComboBoxModel<>();
    DefaultComboBoxModel<DataSource1D> data1DComboBoxModel = new DefaultComboBoxModel<>();
    ProjectController projectController;
    public StructureVisController structureVisController;
    SubstructurePanel substructurePanel;
    LayerPanel layerPanel;
    AnnotationsLayer annotationsLayerRight;
    DefaultComboBoxModel<NucleotideComposition> nucleotideComboBoxModel = new DefaultComboBoxModel<>();

    /**
     * Creates new form StructureVisPanel
     */
    public StructureVisPanel(ProjectController projectController) {
        initComponents();

        File structureVisModelFile = new File(projectController.projectModel.getProjectPath().toFile().getAbsolutePath() + File.separatorChar + "structurevis.model");
        if (structureVisModelFile.exists()) {
            try {
                this.structureVisController = StructureVisController.loadProject(structureVisModelFile);

            } catch (Exception ex) {
                this.structureVisController = new StructureVisController(projectController.projectModel.getProjectPath().toFile());
                // TODO - handle this better.
            }
        } else {
            this.structureVisController = new StructureVisController(projectController.projectModel.getProjectPath().toFile());
        }
        this.projectController = projectController;


        layerPanel = new LayerPanel();


        annotationsLayerRight = new AnnotationsLayer(null, structureVisController, projectController);

        JPanel annotationsLayerLeft = new JPanel();
        annotationsLayerLeft.setLayout(new BorderLayout());
        annotationsLayerLeft.add(new JLabel("Sequence annotations"), BorderLayout.WEST);
        annotationsLayerLeft.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.darkGray));
        Layer layer = new Layer(annotationsLayerLeft, annotationsLayerRight);
        annotationsLayerRight.parent = layer;
        layerPanel.addLayer(layer, true);

        topScrollPane.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.darkGray));
        topScrollPane.setViewportView(layerPanel);

        verticalSplitPane.setDividerLocation(annotationsLayerRight.getPreferredSize().height + jPanel1.getPreferredSize().height + 3);


        substructurePanel = new SubstructurePanel(structureVisController, projectController);
        bottomSplit.add(substructurePanel, BorderLayout.CENTER);
        // bottomScrollPane.setViewportView(structurePanel);

        // populateStructureComboBox(Collections.list(projectController.projectModel.dataSources.elements()));
        projectController.addView(this);

        data1DComboBox.setModel(data1DComboBoxModel);
        data1DComboBox.addItemListener(this);
        populateDataSource1DComboBox();
        substructurePanel.refresh();
        
        nucleotideAlignmentComboBox.setModel(nucleotideComboBoxModel);        
        nucleotideAlignmentComboBox.addItemListener(this);
        populateNucleotideComboBox();


        if (structureVisController.substructureModel.getAnnotationSource() == null) {
            try {
                AnnotationSource annotationData = AnnotationSource.stackFeatures(AnnotationSource.readAnnotations(new File("examples/annotations/refseq.gb")));

                structureVisController.substructureModel.setAnnotationSource(annotationData);
                annotationsLayerRight.setAnnotationData(structureVisController.substructureModel.getAnnotationSource(), true);
                layerPanel.updatePanel();
            } catch (BioException ex) {
                Logger.getLogger(StructureVisPanel.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(StructureVisPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            annotationsLayerRight.setAnnotationData(structureVisController.substructureModel.getAnnotationSource(), true);
            layerPanel.updatePanel();
        }
        structureVisController.substructureModel.addSubstructureModelListener(this);
    }

    public void populateDataSource1DComboBox() {
        data1DComboBoxModel.removeAllElements();
        ArrayList<DataSource1D> list = Collections.list(structureVisController.structureVisDataSources.elements());
        for (int i = 0; i < list.size(); i++) {
            //ComboBoxItem<Substructure> item = new ComboBoxItem<>(list.get(i), i + "");
            data1DComboBoxModel.addElement(list.get(i));
        }
    }

    public void populateNucleotideComboBox() {
        nucleotideComboBoxModel.removeAllElements();       
        ArrayList<NucleotideComposition> list = Collections.list(structureVisController.nucleotideSources.elements());       
        for (int i = 0; i < list.size(); i++) {
            nucleotideComboBoxModel.addElement(list.get(i));
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

        jScrollPane1 = new javax.swing.JScrollPane();
        verticalSplitPane = new javax.swing.JSplitPane();
        topSplit = new javax.swing.JPanel();
        topScrollPane = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        data1DComboBox = new javax.swing.JComboBox();
        edit1DDataButton = new javax.swing.JButton();
        add1DDataButton = new javax.swing.JButton();
        filler4 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        nucleotideAlignmentComboBox = new javax.swing.JComboBox();
        jButton1 = new javax.swing.JButton();
        bottomSplit = new javax.swing.JPanel();

        verticalSplitPane.setDividerLocation(300);
        verticalSplitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        topSplit.setLayout(new javax.swing.BoxLayout(topSplit, javax.swing.BoxLayout.PAGE_AXIS));

        topScrollPane.setBorder(null);
        topSplit.add(topScrollPane);

        jPanel1.setMaximumSize(new java.awt.Dimension(32767, 100));
        jPanel1.setMinimumSize(new java.awt.Dimension(83, 10));
        jPanel1.setPreferredSize(new java.awt.Dimension(461, 35));
        jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        data1DComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jPanel1.add(data1DComboBox);

        edit1DDataButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/icons/tabular-field-16x16.png"))); // NOI18N
        edit1DDataButton.setText("Edit 1D data");
        edit1DDataButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                edit1DDataButtonActionPerformed(evt);
            }
        });
        jPanel1.add(edit1DDataButton);

        add1DDataButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/icons/tabular-field-16x16.png"))); // NOI18N
        add1DDataButton.setText("Add 1D data");
        add1DDataButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                add1DDataButtonActionPerformed(evt);
            }
        });
        jPanel1.add(add1DDataButton);
        jPanel1.add(filler4);

        nucleotideAlignmentComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jPanel1.add(nucleotideAlignmentComboBox);

        jButton1.setText("Add nucleotide overlay");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton1);

        topSplit.add(jPanel1);

        verticalSplitPane.setLeftComponent(topSplit);

        bottomSplit.setPreferredSize(new java.awt.Dimension(461, 500));
        bottomSplit.setLayout(new java.awt.BorderLayout());
        verticalSplitPane.setRightComponent(bottomSplit);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(verticalSplitPane, javax.swing.GroupLayout.DEFAULT_SIZE, 679, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(verticalSplitPane, javax.swing.GroupLayout.DEFAULT_SIZE, 375, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void add1DDataButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_add1DDataButtonActionPerformed
        Data1DDialog d = new Data1DDialog(null, true, projectController.projectModel, structureVisController);
        d.setSize(920, 690);
        d.editMode = false;
        d.setVisible(true);
    }//GEN-LAST:event_add1DDataButtonActionPerformed

    private void edit1DDataButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_edit1DDataButtonActionPerformed
        Data1DDialog d = new Data1DDialog(null, true, projectController.projectModel, structureVisController);
        DataSource1D dataSource1D = (DataSource1D) data1DComboBoxModel.getSelectedItem();
        if (dataSource1D != null) {
            d.data1DPanel.setDataSource1D(dataSource1D);
            d.editMode = true;
            d.setSize(920, 690);
            d.setVisible(true);
        }
    }//GEN-LAST:event_edit1DDataButtonActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        NucleotideCompositionDialog d = new NucleotideCompositionDialog(null, true, projectController.projectModel, structureVisController);
        d.setSize(920, 690);
        //d.editMode = false;
        d.setVisible(true);
        populateNucleotideComboBox();
    }//GEN-LAST:event_jButton1ActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton add1DDataButton;
    private javax.swing.JPanel bottomSplit;
    private javax.swing.JComboBox data1DComboBox;
    private javax.swing.JButton edit1DDataButton;
    private javax.swing.Box.Filler filler4;
    private javax.swing.JButton jButton1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JComboBox nucleotideAlignmentComboBox;
    private javax.swing.JScrollPane topScrollPane;
    private javax.swing.JPanel topSplit;
    private javax.swing.JSplitPane verticalSplitPane;
    // End of variables declaration//GEN-END:variables

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getSource().equals(data1DComboBox)) {
            DataSource1D dataSource1D = (DataSource1D) data1DComboBox.getSelectedItem();
            if (dataSource1D != null) {
                structureVisController.substructureModel.setDataSource1D(dataSource1D);
            }
        }
        else
        if(e.getSource().equals(nucleotideAlignmentComboBox))
        {
            NucleotideComposition nucleotideComposition = (NucleotideComposition) nucleotideAlignmentComboBox.getSelectedItem();
            if(nucleotideComposition != null)
            {
                structureVisController.substructureModel.setNucleotideSource(nucleotideComposition);
            }
        }
            
    }

    @Override
    public void dataSourcesLoaded() {
        //structurePanel.populateStructureComboBox(Collections.list(projectController.projectModel.dataSources.elements()));
    }

    @Override
    public void dataSourcesIntervalAdded(ListDataEvent e) {
        // structurePanel.populateStructureComboBox(Collections.list(projectController.projectModel.dataSources.elements()));
    }

    @Override
    public void dataSourcesIntervalRemoved(ListDataEvent e) {
        //structurePanel.populateStructureComboBox(Collections.list(projectController.projectModel.dataSources.elements()));
    }

    @Override
    public void dataSourcesContentsChanged(ListDataEvent e) {
        //structurePanel.populateStructureComboBox(Collections.list(projectController.projectModel.dataSources.elements()));
    }

    /*
    @Override
    public void intervalAdded(ListDataEvent e) {
        populateDataSource1DComboBox();
    }

    @Override
    public void intervalRemoved(ListDataEvent e) {
        populateDataSource1DComboBox();
    }

    @Override
    public void contentsChanged(ListDataEvent e) {
        populateDataSource1DComboBox();
    }*/

    /*
     * protected EventListenerList listeners = new EventListenerList();
     *
     * public void addStructureVisListener(StructureVisListener listener) {
     * listeners.add(StructureVisListener.class, listener); }
     *
     * public void removeStructureVisListener(StructureVisListener listener) {
     * listeners.remove(StructureVisListener.class, listener); }
     *
     * public void fireWindowClosingEvent(WindowEvent e) { Object[] listeners =
     * this.listeners.getListenerList(); // Each listener occupies two elements
     * - the first is the listener class // and the second is the listener
     * instance for (int i = 0; i < listeners.length; i += 2) { if (listeners[i]
     * == NavigationListener.class) { ((StructureVisListener) listeners[i +
     * 1]).windowClosingEvent(e); } } }
     *
     */
    @Override
    public void dataSource1DChanged(DataSource1D dataSource1D) {
    }

    @Override
    public void dataSource2DChanged(DataSource2D dataSource2D) {
    }

    @Override
    public void structureSourceChanged(StructureSource structureSource) {
        if (structureVisController.substructureModel != null && structureVisController.substructureModel.getAnnotationSource() != null) {
            annotationsLayerRight.setAnnotationData(structureVisController.substructureModel.getAnnotationSource(), true);
        }
    }

    @Override
    public void annotationSourceChanged(AnnotationSource annotationSource) {
        annotationsLayerRight.setAnnotationData(annotationSource, true);
    }

    @Override
    public void nucleotideSourceChanged(NucleotideComposition nucleotideSource) {
        
    }
}
