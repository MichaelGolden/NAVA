/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.structurevis.layerpanel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.Scrollable;
import javax.swing.event.ListDataEvent;
import nava.structurevis.StructureVisController;
import nava.structurevis.StructureVisModel;
import nava.structurevis.StructureVisView;
import nava.structurevis.SubstructureModelListener;
import nava.structurevis.data.*;
import nava.structurevis.layerpanel.LayerItem.LayerType;
import nava.ui.ProjectController;
import nava.ui.ProjectModel;
import nava.ui.ProjectView;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class LayerPanel extends javax.swing.JPanel implements Scrollable, LayerModelListener, StructureVisView, SubstructureModelListener {

    int width = 1000;
    boolean trackWidth = true;
    int height = 0;
    ArrayList<Layer> layers = new ArrayList<>();
    //LayerModel layerModel;
    StructureVisController structureVisController;
    ProjectController projectController;

    /**
     * Creates new form LayerPanel
     */
    public LayerPanel(StructureVisController structureVisController, ProjectController projectController) {
        initComponents();
        jSplitPane1.setDividerLocation(150);
        this.structureVisController = structureVisController;
        this.projectController = projectController;
        this.structureVisController.addView(this);
        structureVisController.structureVisModel.substructureModel.addSubstructureModelListener(this);
    }

    public void setLayerModel(LayerModel layerModel) {
        if (structureVisController.structureVisModel.layerModel != null) {
            structureVisController.structureVisModel.layerModel.removeLayerModelListener(this);
        }
        structureVisController.structureVisModel.layerModel = layerModel;
        structureVisController.structureVisModel.layerModel.addLayerModelListener(this);
        resetLayerModel();
    }

    public void updatePanel() {
        /*
         * removeAllLayers(); if (genomeLayer != null) { genomeLayer.canPin =
         * false; addLayer(genomeLayer); } if (graphLayer1D != null) {
         * addLayer(graphLayer1D); } for (int i = 0; i < pinnedLayers.size();
         * i++) { if (!pinnedLayers.get(i).equals(graphLayer1D)) { if
         * (pinnedLayers.get(i).isPinned) { addLayer(pinnedLayers.get(i)); } } }
         */

        refresh();
        revalidate();
        repaint();
        /*
         * for (int i = 0; i < layers.size(); i++) { layers.get(i).redraw(); }
         */
    }

    public void refresh() {
        //leftPanel.removeAll();
        //rightPanel.removeAll();
        height = 0;
        for (Layer layer : layers) {
            layer.refresh();
            //addLayer(layer,false);
            height += layer.getLeft().getPreferredSize().height;
            layer.getRight().setPreferredSize(layer.getLeft().getPreferredSize());
        }
        this.setPreferredSize(new Dimension(this.getPreferredSize().width, height));
    }

    public void addLayer(Layer layer, boolean add) {
        layer.parent = this;
        layer.refresh();
        if (add) {
            layers.add(layer);
        }
        layer.getLeft().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
        leftPanel.add(layer.getLeft());

        layer.getRight().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
        rightPanel.add(layer.getRight());

        height += layer.getLeft().getPreferredSize().height;

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

        setLayout(new java.awt.BorderLayout());

        leftPanel.setLayout(new javax.swing.BoxLayout(leftPanel, javax.swing.BoxLayout.PAGE_AXIS));
        jSplitPane1.setLeftComponent(leftPanel);

        rightPanel.setLayout(new javax.swing.BoxLayout(rightPanel, javax.swing.BoxLayout.PAGE_AXIS));
        jSplitPane1.setRightComponent(rightPanel);

        add(jSplitPane1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JPanel leftPanel;
    private javax.swing.JPanel rightPanel;
    // End of variables declaration//GEN-END:variables

    public Dimension getPreferredScrollableViewportSize() {
        return this.getPreferredSize();
        // return new Dimension(1000,100);
    }

    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        return -1;
    }

    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        return -1;
    }

    public boolean getScrollableTracksViewportWidth() {
        return trackWidth;
    }

    public boolean getScrollableTracksViewportHeight() {
        return false;
    }

    public void autofitWidth() {
        trackWidth = true;
        this.revalidate();
        width = getWidth();
    }

    public void resizeWidth(int width) {
        trackWidth = false;
        this.setPreferredSize(new Dimension(width, height));
        this.revalidate();
    }

    public void zoomIn() {
        width = (int) ((double) getWidth() * 1.5);
        resizeWidth(width);
    }

    public void zoomOut() {
        width = (int) ((double) getWidth() / 1.5);
        resizeWidth(width);
    }

    public void addItems(int index0, int index1) {
        for (int i = index0; i <= index1; i++) {
            LayerItem item = structureVisController.structureVisModel.layerModel.items.get(i);
            if (item.type == LayerType.ANNOTATIONS) {
                AnnotationSource annotationSource = (AnnotationSource) item.object;
                AnnotationsLayer annotationsLayer = new AnnotationsLayer(null, structureVisController, projectController);
                Layer layer = new Layer(new LabelLayer("Sequence annotations"), annotationsLayer);
                annotationsLayer.setAnnotationData(annotationSource, true);
                annotationsLayer.parent = layer;
                addLayer(layer, true);
            } else if (item.type == LayerType.DATAOVERLAY_1D) {
                DataOverlay1D dataOverlay1D = (DataOverlay1D) item.object;
                GraphLayer graphLayer = new GraphLayer(null, structureVisController, projectController);
                graphLayer.setData(dataOverlay1D, 20);
                Layer layer2 = new Layer(new LabelLayer(item.label), graphLayer);
                graphLayer.parent = layer2;
                addLayer(layer2, true);
            }
        }
        refresh();
    }

    public void resetLayerModel() {
        this.leftPanel.removeAll();
        this.rightPanel.removeAll();
        height = 0;
        addItems(0, structureVisController.structureVisModel.layerModel.items.size() - 1);
    }

    @Override
    public void intervalAdded(int index0, int index1) {
        addItems(index0, index1);
    }

    @Override
    public void intervalRemoved(int index0, int index1) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void contentsChanged(int index0, int index1) {
        resetLayerModel();
    }

    @Override
    public void dataSource1DChanged(DataOverlay1D dataSource1D) {
        System.out.println("LayerPanel.dataSource1DChanged " + dataSource1D);
        if (structureVisController.structureVisModel.layerModel != null) {
            if (dataSource1D == null) {
                structureVisController.structureVisModel.layerModel.setDataOverlay1D(1, "1D overlay (none)", null);
            } else {
                structureVisController.structureVisModel.layerModel.setDataOverlay1D(1, dataSource1D.title, dataSource1D);
            }
        }
    }

    @Override
    public void dataSource2DChanged(DataOverlay2D dataSource2D) {
    }

    @Override
    public void structureSourceChanged(StructureOverlay structureSource) {
    }

    @Override
    public void annotationSourceChanged(AnnotationSource annotationSource) {
    }

    @Override
    public void nucleotideSourceChanged(NucleotideComposition nucleotideSource) {
    }

    @Override
    public void structureVisModelChanged(StructureVisModel newStructureVisModel) {
        this.structureVisController.structureVisModel.substructureModel.removeSubstructureModelListener(this);
        structureVisController.structureVisModel = newStructureVisModel;
        newStructureVisModel.substructureModel.addSubstructureModelListener(this);
        this.setLayerModel(newStructureVisModel.layerModel);
    }

    @Override
    public void dataOverlayAdded(Overlay overlay) {
    }

    @Override
    public void dataOverlayRemoved(Overlay overlay) {
    }

    @Override
    public void dataOverlayChanged(Overlay oldOverlay, Overlay newOverlay) {
    }
}
