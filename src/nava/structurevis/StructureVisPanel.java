/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.structurevis;

import nava.structurevis.layerpanel.Layer;
import nava.structurevis.layerpanel.LayerPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Frame;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.event.ListDataEvent;
import nava.data.types.*;
import nava.structurevis.data.*;
import nava.structurevis.layerpanel.*;
import nava.ui.MainFrame;
import nava.ui.ProjectController;
import nava.ui.ProjectModel;
import nava.ui.ProjectView;
import nava.utils.GraphicsUtils;
import org.biojava.bio.BioException;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class StructureVisPanel extends javax.swing.JPanel implements ItemListener, ProjectView, StructureVisView, SubstructureModelListener {

    //DefaultComboBoxModel<Alignment> mappingSourceComboBoxModel = new DefaultComboBoxModel<>();
    DefaultComboBoxModel<DataOverlay1D> data1DComboBoxModel = new DefaultComboBoxModel<>();
    DefaultComboBoxModel<DataOverlay2D> data2DComboBoxModel = new DefaultComboBoxModel<>();
    ProjectController projectController;
    public StructureVisController structureVisController;
    SubstructurePanel substructurePanel;
    LayerPanel layerPanel;
   // LayerModel layerModel;
    //AnnotationsLayer annotationsLayerRight;
    DefaultComboBoxModel<NucleotideComposition> nucleotideComboBoxModel = new DefaultComboBoxModel<>();    
    /**
     * Creates new form StructureVisPanel
     */
    public StructureVisPanel(ProjectController projectController) {
        initComponents();

        File structureVisModelFile = new File(projectController.projectModel.getProjectPath().toFile().getAbsolutePath() + File.separatorChar + "structurevis.model");
        if (structureVisModelFile.exists()) {
            try {
                this.structureVisController = new StructureVisController(projectController, projectController.projectModel);
                this.structureVisController.structureVisModel = StructureVisModel.loadProject(structureVisModelFile, structureVisController);

            } catch (Exception ex) {
                this.structureVisController = new StructureVisController(projectController, projectController.projectModel);
                ex.printStackTrace();
                // TODO - handle this better.
            }
        } else {
            this.structureVisController = new StructureVisController(projectController, projectController.projectModel);
            this.structureVisController.structureVisModel.initialise(structureVisController);
        }
        this.projectController = projectController;


        layerPanel = new LayerPanel(structureVisController, projectController);
        //layerPanel.setLayerModel(structureVisController.structureVisModel.layerModel);

        /*
        annotationsLayerRight = new AnnotationsLayer(null, structureVisController, projectController);
        Layer layer = new Layer(new LabelLayer("Sequence annotations"), annotationsLayerRight);
        annotationsLayerRight.parent = layer;
        layerPanel.addLayer(layer, true);

        graphLayer = new GraphLayer(null, structureVisController, projectController);
        
        Layer layer2 = new Layer(new LabelLayer("Graph"), graphLayer);
        graphLayer.parent = layer2;
        layerPanel.addLayer(layer2, true);*/

        topScrollPane.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.darkGray));
        topScrollPane.setViewportView(layerPanel);

        //verticalSplitPane.setDividerLocation(100 + 3 + 0);
        verticalSplitPane.setDividerLocation(80 + 0);

        substructurePanel = new SubstructurePanel(structureVisController, projectController);
        bottomSplit.add(substructurePanel, BorderLayout.CENTER);
        // bottomScrollPane.setViewportView(structurePanel);

        // populateStructureComboBox(Collections.list(projectController.projectModel.dataSources.elements()));
        projectController.addView(this);
        this.structureVisController.addView(this);

        substructurePanel.refresh();

        //layerModel.setAnnotationSource(structureVisController.structureVisModel.substructureModel.getAnnotationSource());
        structureVisController.structureVisModel.substructureModel.addSubstructureModelListener(this);
    }

    public void populateDataSource1DComboBox() {
        data1DComboBoxModel.removeAllElements();
        ArrayList<DataOverlay1D> list = structureVisController.structureVisModel.structureVisDataOverlays1D.getArrayListShallowCopy();
        for (int i = 0; i < list.size(); i++) {
            //ComboBoxItem<Substructure> item = new ComboBoxItem<>(list.get(i), i + "");
            data1DComboBoxModel.addElement(list.get(i));
        }
    }

    public void populateDataSource2DComboBox() {
        data1DComboBoxModel.removeAllElements();
        ArrayList<DataOverlay2D> list = structureVisController.structureVisModel.structureVisDataOverlays2D.getArrayListShallowCopy();
        for (int i = 0; i < list.size(); i++) {
            //ComboBoxItem<Substructure> item = new ComboBoxItem<>(list.get(i), i + "");
            data2DComboBoxModel.addElement(list.get(i));
        }
    }

    public void populateNucleotideComboBox() {
        nucleotideComboBoxModel.removeAllElements();
        ArrayList<NucleotideComposition> list = structureVisController.structureVisModel.nucleotideSources.getArrayListShallowCopy();
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
    public static void showAddDialog(Frame parent, ProjectModel projectModel, StructureVisController structureVisController, DataSource selectedDataSource) {
        if (selectedDataSource instanceof Tabular) {
            Data1DDialog d = new Data1DDialog(parent, true, projectModel, structureVisController);
            d.data1DPanel.dataSourceComboBoxModel.setSelectedItem(selectedDataSource);
            d.setEditMode(null);
            GraphicsUtils.centerWindowOnWindow(d, MainFrame.self);
            d.setSize(Data1DDialog.defaultWidth, Data1DDialog.defaultHeight);
            d.setVisible(true);
        } else if (selectedDataSource instanceof Matrix) {
            Data2DDialog d = new Data2DDialog(null, true, projectModel, structureVisController);
            d.data2DPanel.dataMatrixComboBoxModel.setSelectedItem(selectedDataSource);
            d.setSize(Data2DDialog.defaultWidth, Data2DDialog.defaultHeight);
            GraphicsUtils.centerWindowOnWindow(d, MainFrame.self);
            d.setEditMode(null);
            d.setVisible(true);
        } else if (selectedDataSource instanceof SecondaryStructure) {
            StructureOverlayDialog d = new StructureOverlayDialog(null, true, projectModel, structureVisController);
            d.structureDataPanel.structureComboBoxModel.setSelectedItem(selectedDataSource);
            d.setSize(640, 580);
            GraphicsUtils.centerWindowOnWindow(d, MainFrame.self);
            d.setEditMode(null);
            d.setVisible(true);
        } else if (selectedDataSource instanceof Alignment) {
            NucleotideCompositionDialog d = new NucleotideCompositionDialog(null, true, projectModel, structureVisController);
            d.nucleotidePanel.nucleotideAlignmentComboBoxModel.setSelectedItem(selectedDataSource);
            d.setSize(375, 150);
            GraphicsUtils.centerWindowOnWindow(d, MainFrame.self);
            d.setEditMode(null);
            d.setVisible(true);
        }
    }

    public static void showEditDialog(Overlay overlay, Frame parent, ProjectModel projectModel, StructureVisController structureVisController) {
        if (overlay != null) {
            if (overlay instanceof DataOverlay1D) {
                Data1DDialog d = new Data1DDialog(parent, true, projectModel, structureVisController);
                d.data1DPanel.setDataSource1D((DataOverlay1D) overlay);
                d.setEditMode(overlay);
                d.setSize(Data1DDialog.defaultWidth, Data1DDialog.defaultHeight);
                GraphicsUtils.centerWindowOnWindow(d, MainFrame.self);
                d.setVisible(true);
            } else if (overlay instanceof DataOverlay2D) {
                Data2DDialog d = new Data2DDialog(parent, true, projectModel, structureVisController);
                d.data2DPanel.setDataSource2D((DataOverlay2D) overlay);
                d.setEditMode(overlay);
                d.setSize(Data2DDialog.defaultWidth, Data2DDialog.defaultHeight);
                GraphicsUtils.centerWindowOnWindow(d, MainFrame.self);
                d.setVisible(true);
            } else if (overlay instanceof NucleotideComposition) {
                NucleotideCompositionDialog d = new NucleotideCompositionDialog(parent, true, projectModel, structureVisController);
                d.setEditMode(overlay);
                d.nucleotidePanel.setNucleotideSource((NucleotideComposition) overlay);
                d.setSize(375, 150);
                GraphicsUtils.centerWindowOnWindow(d, MainFrame.self);
                d.setVisible(true);
            } else if (overlay instanceof StructureOverlay) {
                StructureOverlayDialog d = new StructureOverlayDialog(parent, true, projectModel, structureVisController);
                d.setEditMode(overlay);
                d.structureDataPanel.setStructureOverlay((StructureOverlay) overlay);
                d.setSize(640, 580);
                GraphicsUtils.centerWindowOnWindow(d, MainFrame.self);
                d.setVisible(true);
            }
        }
    }
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        verticalSplitPane = new javax.swing.JSplitPane();
        topSplit = new javax.swing.JPanel();
        topScrollPane = new javax.swing.JScrollPane();
        bottomSplit = new javax.swing.JPanel();

        verticalSplitPane.setDividerLocation(250);
        verticalSplitPane.setDividerSize(3);
        verticalSplitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        topSplit.setLayout(new javax.swing.BoxLayout(topSplit, javax.swing.BoxLayout.PAGE_AXIS));

        topScrollPane.setBorder(null);
        topSplit.add(topScrollPane);

        verticalSplitPane.setLeftComponent(topSplit);

        bottomSplit.setPreferredSize(new java.awt.Dimension(461, 500));
        bottomSplit.setLayout(new java.awt.BorderLayout());
        verticalSplitPane.setRightComponent(bottomSplit);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(verticalSplitPane, javax.swing.GroupLayout.DEFAULT_SIZE, 444, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(verticalSplitPane, javax.swing.GroupLayout.DEFAULT_SIZE, 364, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    /*
     * public static void showEditDialog(DataOverlay1D dataOverlay1D, Frame
     * parent, ProjectModel projectModel, StructureVisController
     * structureVisController) { Data1DDialog d = new Data1DDialog(parent, true,
     * projectModel, structureVisController); if (dataOverlay1D != null) {
     * d.data1DPanel.setDataSource1D(dataOverlay1D); d.setEditMode(true);
     * d.setSize(920, 690); d.setVisible(true); } }
     *
     * public static void showEditDialog(NucleotideComposition
     * nucleotideComposition, Frame parent, ProjectModel projectModel,
     * StructureVisController structureVisController) {
     * NucleotideCompositionDialog d = new NucleotideCompositionDialog(parent,
     * true, projectModel, structureVisController); if (nucleotideComposition !=
     * null) { d.setEditMode(true);
     * d.nucleotidePanel.setNucleotideSource(nucleotideComposition);
     * d.setSize(600, 150); d.setVisible(true); } }
     *
     * public static void showEditDialog(DataOverlay2D dataSource2D, Frame
     * parent, ProjectModel projectModel, StructureVisController
     * structureVisController) { Data2DDialog d = new Data2DDialog(parent, true,
     * projectModel, structureVisController); if (dataSource2D != null) {
     * d.data2DPanel.setDataSource2D(dataSource2D); d.setEditMode(true);
     * d.setSize(750, 690); d.setVisible(true); } }
     *
     * public static void showEditDialog(StructureSource structureSource, Frame
     * parent, ProjectModel projectModel, StructureVisController
     * structureVisController) { StructureDataDialog d = new
     * StructureDataDialog(parent, true, projectModel, structureVisController);
     * if (structureSource != null) { d.setEditMode(true);
     * d.structureDataPanel.setStructureSource(structureSource); d.setSize(640,
     * 580); d.setVisible(true); } }
     */
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel bottomSplit;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane topScrollPane;
    private javax.swing.JPanel topSplit;
    private javax.swing.JSplitPane verticalSplitPane;
    // End of variables declaration//GEN-END:variables

    @Override
    public void itemStateChanged(ItemEvent e) {
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
     * @Override public void intervalAdded(ListDataEvent e) {
     * populateDataSource1DComboBox(); }
     *
     * @Override public void intervalRemoved(ListDataEvent e) {
     * populateDataSource1DComboBox(); }
     *
     * @Override public void contentsChanged(ListDataEvent e) {
     * populateDataSource1DComboBox(); }
     */

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
    public void dataSource1DChanged(DataOverlay1D dataSource1D) {
    }

    @Override
    public void dataSource2DChanged(DataOverlay2D dataSource2D) {
    }

    @Override
    public void structureOverlayChanged(StructureOverlay structureSource) {
        if (structureVisController.structureVisModel.substructureModel != null && structureVisController.structureVisModel.substructureModel.getAnnotationSource() != null) {
           structureVisController.structureVisModel.layerModel.setAnnotationSource(structureVisController.structureVisModel.substructureModel.getAnnotationSource());          
        }
    }

    @Override
    public void annotationSourceChanged(AnnotationSource annotationSource) {
       //TODO 888 layerModel.setAnnotationSource(structureVisController.structureVisModel.substructureModel.getAnnotationSource());          
    }

    @Override
    public void nucleotideSourceChanged(NucleotideComposition nucleotideSource) {
    }

    @Override
    public void projectModelChanged(ProjectModel newProjectModel) {
    }
    
    @Override
    public void structureVisModelChanged(StructureVisModel newStructureVisModel) {
        this.structureVisController.structureVisModel.substructureModel.removeSubstructureModelListener(this);
        structureVisController.structureVisModel = newStructureVisModel;
        newStructureVisModel.substructureModel.addSubstructureModelListener(this);
       // throw new UnsupportedOperationException("Not supported yet.");
        this.layerPanel.setLayerModel(newStructureVisModel.layerModel);
    }

    @Override
    public void dataOverlayAdded(Overlay overlay) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void dataOverlayRemoved(Overlay overlay) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void dataOverlayChanged(Overlay oldOverlay, Overlay newOverlay) {
       // throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void substructureChanged(Substructure substructure) {
        this.substructurePanel.structureDrawPanel.openSubstructure(substructure);
        if(this.layerPanel.annotationsLayer != null)
        {
            this.layerPanel.annotationsLayer.updateSubstructures();
        }
    }
}
