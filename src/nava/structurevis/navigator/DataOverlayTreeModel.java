/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.structurevis.navigator;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Objects;
import javax.swing.DefaultListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import nava.data.types.DataSource;
import nava.structurevis.StructureVisController;
import nava.structurevis.data.*;
import nava.ui.ProjectView;

/**
 *
 * @author Michael
 */
public class DataOverlayTreeModel extends DefaultTreeModel implements Serializable, ProjectView, ListDataListener {
    //DefaultMutableTreeNode origDataNode = null;
    //DefaultMutableTreeNode dataNode = null;

    DefaultMutableTreeNode oneDimensionalData = null;
    DefaultMutableTreeNode twoDimensionalData = null;
    DefaultMutableTreeNode nucleotideData = null;
    DefaultMutableTreeNode structureData = null;
    StructureVisController structureVisController;
    //ProjectModel projectModel;

    public DataOverlayTreeModel(DefaultMutableTreeNode root, StructureVisController structureVisController) {
        super(root);
        this.structureVisController = structureVisController;
        //this.projectModel = projectModel;

        //origDataNode = NavigatorTreeNode.createFolderNode("Original data sources");
        // dataNode = DataOverlayTreeNode.createFolderNode("Data overlays");
        //root.add(origDataNode);
        //root.add(dataNode);
        // root.add(dataNode);

        oneDimensionalData = DataOverlayTreeNode.createFolderNode("1D overlays");
        root.add(oneDimensionalData);

        twoDimensionalData = DataOverlayTreeNode.createFolderNode("2D overlays");
        root.add(twoDimensionalData);

        nucleotideData = DataOverlayTreeNode.createFolderNode("Nucleotide overlays");
        root.add(nucleotideData);

        structureData = DataOverlayTreeNode.createFolderNode("Structures");
        root.add(structureData);

        for (int i = 0; i < structureVisController.structureVisDataOverlays1D.size(); i++) {
            oneDimensionalData.add(new DataOverlayTreeNode(structureVisController.structureVisDataOverlays1D.get(i)));
        }
        for (int i = 0; i < structureVisController.structureVisDataOverlays2D.size(); i++) {
            twoDimensionalData.add(new DataOverlayTreeNode(structureVisController.structureVisDataOverlays2D.get(i)));
        }
        for (int i = 0; i < structureVisController.nucleotideSources.size(); i++) {
            nucleotideData.add(new DataOverlayTreeNode(structureVisController.nucleotideSources.get(i)));
        }
        for (int i = 0; i < structureVisController.structureSources.size(); i++) {
            System.out.println(i + "[]\t" + structureVisController.structureSources.get(i).title);
            structureData.add(new DataOverlayTreeNode(structureVisController.structureSources.get(i)));
        }

        structureVisController.structureVisDataOverlays1D.addListDataListener(this);
        structureVisController.structureVisDataOverlays2D.addListDataListener(this);
        structureVisController.nucleotideSources.addListDataListener(this);
        structureVisController.structureSources.addListDataListener(this);

    }

    public DataOverlayTreeNode findNode(DataSource dataSource) {
        Enumeration<DefaultMutableTreeNode> en = ((DefaultMutableTreeNode) getRoot()).breadthFirstEnumeration();
        while (en.hasMoreElements()) {
            DefaultMutableTreeNode node = en.nextElement();
            if (node instanceof DataOverlayTreeNode) {
                DataOverlayTreeNode nnode = (DataOverlayTreeNode) node;
                if (Objects.equals(nnode.overlay, dataSource)) {
                    return nnode;
                }
            }
        }
        return null;
    }

    public void addDataOverlay(Overlay dataOverlay) {
        if (dataOverlay instanceof DataOverlay1D) {
             insertNodeInto(new DataOverlayTreeNode(dataOverlay), oneDimensionalData, oneDimensionalData.getChildCount());
        }

        if (dataOverlay instanceof DataOverlay2D) {
            insertNodeInto(new DataOverlayTreeNode(dataOverlay), twoDimensionalData, twoDimensionalData.getChildCount());            
        }

        if (dataOverlay instanceof NucleotideComposition) {
            insertNodeInto(new DataOverlayTreeNode(dataOverlay), nucleotideData, nucleotideData.getChildCount());
        }

        if (dataOverlay instanceof StructureSource) {
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

    @Override
    public void dataSourcesLoaded() {
        // do nothing
    }

    @Override
    public void dataSourcesIntervalAdded(ListDataEvent e) {
        System.out.println("NavigatorTreeModel dataSourcesIntervalAdded");
        for (int i = e.getIndex0(); i < e.getIndex1() + 1; i++) {
//            addDataSource(projectModel.dataSources.get(i));
        }
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void dataSourcesIntervalRemoved(ListDataEvent e) {
        System.out.println("dataSourcesIntervalRemoved");
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void dataSourcesContentsChanged(ListDataEvent e) {
        System.out.println("dataSourcesContentsChanged");
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void intervalAdded(ListDataEvent e) {
        System.out.println("StructureVis source added");
        for (int i = e.getIndex0(); i < e.getIndex1() + 1; i++) {
            addDataOverlay(((DefaultListModel<Overlay>)e.getSource()).get(i));
        }
    }

    @Override
    public void intervalRemoved(ListDataEvent e) {
    }

    @Override
    public void contentsChanged(ListDataEvent e) {
    }
}
