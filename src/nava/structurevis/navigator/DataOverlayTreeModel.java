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
import nava.data.types.*;
import nava.ui.ProjectModel;
import nava.ui.ProjectView;

/**
 *
 * @author Michael
 */
public class DataOverlayTreeModel extends DefaultTreeModel implements Serializable, ProjectView {

    DefaultMutableTreeNode origDataNode = null;
    DefaultMutableTreeNode dataNode = null;
    DefaultMutableTreeNode alignmentsNode = null;
    DefaultMutableTreeNode annotationsNode = null;
    DefaultMutableTreeNode matricesNode = null;
    DefaultMutableTreeNode structuresNode = null;
    DefaultMutableTreeNode tabularNode = null;
    
    ProjectModel projectModel;

    public DataOverlayTreeModel(DefaultMutableTreeNode root) {
        super(root);

        origDataNode = DataOverlayTreeNode.createFolderNode("Original data sources");
        dataNode = DataOverlayTreeNode.createFolderNode("Imported data sources");
        root.add(origDataNode);
        root.add(dataNode);
        
        alignmentsNode = DataOverlayTreeNode.createFolderNode("Alignments");
        dataNode.add(alignmentsNode);

        annotationsNode = DataOverlayTreeNode.createFolderNode("Annotations");
        dataNode.add(annotationsNode);

        structuresNode = DataOverlayTreeNode.createFolderNode("Structures");
        dataNode.add(structuresNode);

        matricesNode = DataOverlayTreeNode.createFolderNode("Matrix data");
        dataNode.add(matricesNode);

        tabularNode = DataOverlayTreeNode.createFolderNode("Tabular data");
        dataNode.add(tabularNode);
    }
    
    public DataOverlayTreeNode findNode(DataSource dataSource)
    {
        Enumeration<DefaultMutableTreeNode> en = ((DefaultMutableTreeNode) getRoot()).breadthFirstEnumeration();
        while(en.hasMoreElements())
        {
            DefaultMutableTreeNode node = en.nextElement();
            if(node instanceof DataOverlayTreeNode)
            {
                DataOverlayTreeNode nnode = (DataOverlayTreeNode)node;
                if(Objects.equals(nnode.dataSource, dataSource))
                {
                    return nnode;
                }
            }
        }
        return null;
    }

    public void addDataSource(DataSource dataSource) {
        if(dataSource instanceof Alignment)
        {
            insertNodeInto(new DataOverlayTreeNode(dataSource), alignmentsNode, alignmentsNode.getChildCount());
        }
        
        if(dataSource instanceof Annotations)
        {
            insertNodeInto(new DataOverlayTreeNode(dataSource), annotationsNode, annotationsNode.getChildCount());
        }
        
        if(dataSource instanceof Matrix)
        {
             insertNodeInto(new DataOverlayTreeNode(dataSource), matricesNode, matricesNode.getChildCount());
        }
        
        if(dataSource instanceof SecondaryStructure || dataSource instanceof StructureList)
        {
            insertNodeInto(new DataOverlayTreeNode(dataSource), structuresNode, structuresNode.getChildCount());
        }
        
        if(dataSource instanceof Tabular || dataSource instanceof TabularField)
        {
            insertNodeInto(new DataOverlayTreeNode(dataSource), tabularNode, tabularNode.getChildCount());
        }
    }

    @Override
    public void dataSourcesLoaded() {
        // do nothing
    }

    @Override
    public void dataSourcesIntervalAdded(ListDataEvent e) {
        System.out.println("NavigatorTreeModel dataSourcesIntervalAdded");
        for(int i = e.getIndex0() ; i < e.getIndex1() + 1 ; i++)
        {
            addDataSource(projectModel.dataSources.get(i));
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
}
