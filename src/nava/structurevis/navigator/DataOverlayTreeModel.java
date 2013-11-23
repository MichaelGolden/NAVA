/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.structurevis.navigator;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Objects;
import javax.swing.event.ListDataEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import nava.structurevis.StructureVisController;
import nava.structurevis.StructureVisModel;
import nava.structurevis.StructureVisView;
import nava.structurevis.SubstructureModelListener;
import nava.structurevis.data.*;
import nava.ui.ProjectModel;
import nava.ui.ProjectView;

/**
 *
 * @author Michael
 */
public class DataOverlayTreeModel extends DefaultTreeModel implements Serializable, ProjectView, StructureVisView, SubstructureModelListener {

    private static final long serialVersionUID = -1625375843157333064L;
    DefaultMutableTreeNode oneDimensionalData = null;
    DefaultMutableTreeNode twoDimensionalData = null;
    DefaultMutableTreeNode nucleotideData = null;
    DefaultMutableTreeNode structureData = null;
    StructureVisModel structureVisModel;

    public DataOverlayTreeModel(DefaultMutableTreeNode root, StructureVisController structureVisController) {
        super(root);
        this.structureVisModel = structureVisController.structureVisModel;
        structureVisController.structureVisModel.substructureModel.addSubstructureModelListener(this);
        setup();
    }

    public void setup() {
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) this.getRoot();
        root.removeAllChildren();

        oneDimensionalData = DataOverlayTreeNode.createFolderNode("1D overlays");
        root.add(oneDimensionalData);

        twoDimensionalData = DataOverlayTreeNode.createFolderNode("2D overlays");
        root.add(twoDimensionalData);

        nucleotideData = DataOverlayTreeNode.createFolderNode("Nucleotide overlays");
        root.add(nucleotideData);

        structureData = DataOverlayTreeNode.createFolderNode("Structures");
        root.add(structureData);

        for (int i = 0; i < structureVisModel.structureVisDataOverlays1D.size(); i++) {
           if(!structureVisModel.structureVisDataOverlays1D.get(i).deleted)
           {
                oneDimensionalData.add(new DataOverlayTreeNode(structureVisModel.structureVisDataOverlays1D.get(i)));
           }
        }
        for (int i = 0; i < structureVisModel.structureVisDataOverlays2D.size(); i++) {
            if(!structureVisModel.structureVisDataOverlays2D.get(i).deleted)
            {
                twoDimensionalData.add(new DataOverlayTreeNode(structureVisModel.structureVisDataOverlays2D.get(i)));
            }
        }
        for (int i = 0; i < structureVisModel.nucleotideSources.size(); i++) {
            if(!structureVisModel.nucleotideSources.get(i).deleted)
            {
                nucleotideData.add(new DataOverlayTreeNode(structureVisModel.nucleotideSources.get(i)));
            }
        }
        for (int i = 0; i < structureVisModel.structureSources.size(); i++) {
            if(!structureVisModel.structureSources.get(i).deleted)
            {
                structureData.add(new DataOverlayTreeNode(structureVisModel.structureSources.get(i)));
            }
        }
    }

    /*
     * public void addListDataListeners() {
     * structureVisController.structureVisDataOverlays1D.addListDataListener(this);
     * structureVisController.structureVisDataOverlays2D.addListDataListener(this);
     * structureVisController.nucleotideSources.addListDataListener(this);
     * structureVisController.structureSources.addListDataListener(this); }
     */
    public DataOverlayTreeNode findNode(Overlay overlay) {
        Enumeration<DefaultMutableTreeNode> en = ((DefaultMutableTreeNode) getRoot()).breadthFirstEnumeration();
        while (en.hasMoreElements()) {
            DefaultMutableTreeNode node = en.nextElement();
            if (node instanceof DataOverlayTreeNode) {
                DataOverlayTreeNode nnode = (DataOverlayTreeNode) node;
                if (Objects.equals(nnode.overlay, overlay)) {
                    return nnode;
                }
            }
        }
        return null;
    }

    public void addDataOverlay(Overlay dataOverlay) {
        if(!dataOverlay.deleted)
        {
            if (dataOverlay instanceof DataOverlay1D) {
                insertNodeInto(new DataOverlayTreeNode(dataOverlay), oneDimensionalData, oneDimensionalData.getChildCount());
            }

            if (dataOverlay instanceof DataOverlay2D) {
                insertNodeInto(new DataOverlayTreeNode(dataOverlay), twoDimensionalData, twoDimensionalData.getChildCount());
            }

            if (dataOverlay instanceof NucleotideComposition) {
                insertNodeInto(new DataOverlayTreeNode(dataOverlay), nucleotideData, nucleotideData.getChildCount());
            }

            if (dataOverlay instanceof StructureOverlay) {
                insertNodeInto(new DataOverlayTreeNode(dataOverlay), structureData, structureData.getChildCount());
            }
        /*
         * if (dataSource instanceof Alignment) { insertNodeInto(new
         * NavigatorTreeNode(dataSource), alignmentsNode,
         * alignmentsNode.getChildCount()); }
         *
         * if (dataSource instanceof Annotations) { insertNodeInto(new
         * NavigatorTreeNode(dataSource), annotationsNode,
         * annotationsNode.getChildCount()); }
         *
         * if (dataSource instanceof Matrix) { insertNodeInto(new
         * NavigatorTreeNode(dataSource), matricesNode,
         * matricesNode.getChildCount()); }
         *
         * if (dataSource instanceof SecondaryStructure || dataSource instanceof
         * StructureList) { insertNodeInto(new NavigatorTreeNode(dataSource),
         * structuresNode, structuresNode.getChildCount()); }
         *
         * if (dataSource instanceof Tabular || dataSource instanceof
         * TabularField) { insertNodeInto(new NavigatorTreeNode(dataSource),
         * tabularNode, tabularNode.getChildCount()); }
         */
        }
    }
    
    public void deleteDataOverlay(Overlay dataOverlay)
    {
        dataOverlay.deleted = true;
        this.structureVisModel.overlayNavigatorTreeModel.removeNodeFromParent(findNode(dataOverlay));        
    }

    @Override
    public void dataSourcesLoaded() {
        setup();
    }

    @Override
    public void dataSourcesIntervalAdded(ListDataEvent e) {
        for (int i = e.getIndex0(); i < e.getIndex1() + 1; i++) {
//            addDataSource(projectModel.dataSources.get(i));
        }
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void dataSourcesIntervalRemoved(ListDataEvent e) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void dataSourcesContentsChanged(ListDataEvent e) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    /*
     * @Override public void intervalAdded(ListDataEvent e) {
     * System.out.println("StructureVis source added"); for (int i =
     * e.getIndex0(); i < e.getIndex1() + 1; i++) {
     * addDataOverlay(((SafeListModel<Overlay>) e.getSource()).get(i)); } }
     *
     * @Override public void intervalRemoved(ListDataEvent e) { }
     *
     * @Override public void contentsChanged(ListDataEvent e) { }
     */
    @Override
    public void dataOverlayAdded(Overlay overlay) {
        addDataOverlay(overlay);
    }

    @Override
    public void dataOverlayRemoved(Overlay overlay) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void dataOverlayChanged(Overlay oldOverlay, Overlay newOverlay) {
        DataOverlayTreeNode node = this.findNode(oldOverlay);
        if (!oldOverlay.deleted && !newOverlay.deleted &&  node != null) {
            node.overlay = newOverlay;
            DataOverlayTreeNode parent = (DataOverlayTreeNode) node.getParent();
            int[] indices = {parent.getIndex(node)};
            Object[] children = {node};
            if (oldOverlay.getState() == Overlay.OverlayState.PRIMARY_SELECTED) {
                structureVisModel.substructureModel.setAsPrimaryOverlay(newOverlay);
            }
            this.fireTreeNodesChanged(this, parent.getPath(), indices, children);
        }
    }

    @Override
    public void projectModelChanged(ProjectModel newProjectModel) {
    }

    @Override
    public void structureVisModelChanged(StructureVisModel newStructureVisModel) {
        this.structureVisModel.substructureModel.removeSubstructureModelListener(this);
        this.structureVisModel = newStructureVisModel;
        newStructureVisModel.substructureModel.addSubstructureModelListener(this);

    }

    @Override
    public void substructureChanged(Substructure substructure) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void dataSource1DChanged(DataOverlay1D dataSource1D) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void dataSource2DChanged(DataOverlay2D dataSource2D) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void structureOverlayChanged(StructureOverlay structureOverlay) {
        
        //structureVisModel.substructureModel.setAsPrimaryOverlay(structureOverlay);
        for(int i = 0 ; i < structureData.getChildCount() ; i++)
        {
            //if(structureData.)
            DataOverlayTreeNode node = (DataOverlayTreeNode)structureData.getChildAt(i);
        
            DataOverlayTreeNode parent = (DataOverlayTreeNode) structureData.getChildAt(i).getParent();
            int[] indices = {parent.getIndex(node)};
            Object[] children = {node};
            this.fireTreeNodesChanged(this, parent.getPath(), indices, children);
        }
    }

    @Override
    public void annotationSourceChanged(AnnotationSource annotationSource) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void nucleotideSourceChanged(NucleotideComposition nucleotideSource) {
       // throw new UnsupportedOperationException("Not supported yet.");
    }
}
