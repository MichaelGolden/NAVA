/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.ui.navigator;

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
public class NavigatorTreeModel extends DefaultTreeModel implements Serializable, ProjectView {

    DefaultMutableTreeNode origDataNode = null;
    DefaultMutableTreeNode dataNode = null;
    DefaultMutableTreeNode alignmentsNode = null;
    DefaultMutableTreeNode annotationsNode = null;
    DefaultMutableTreeNode matricesNode = null;
    DefaultMutableTreeNode structuresNode = null;
    DefaultMutableTreeNode tabularNode = null;
    
    ProjectModel projectModel;

    public NavigatorTreeModel(DefaultMutableTreeNode root, ProjectModel projectModel) {
        super(root);
        this.projectModel = projectModel;

        origDataNode = NavigatorTreeNode.createFolderNode("Original data sources");
        dataNode = NavigatorTreeNode.createFolderNode("Imported data sources");
        root.add(origDataNode);
        root.add(dataNode);
        
        alignmentsNode = NavigatorTreeNode.createFolderNode("Alignments");
        dataNode.add(alignmentsNode);

        annotationsNode = NavigatorTreeNode.createFolderNode("Annotations");
        dataNode.add(annotationsNode);

        structuresNode = NavigatorTreeNode.createFolderNode("Structures");
        dataNode.add(structuresNode);

        matricesNode = NavigatorTreeNode.createFolderNode("Matrix data");
        dataNode.add(matricesNode);

        tabularNode = NavigatorTreeNode.createFolderNode("Tabular data");
        dataNode.add(tabularNode);
    }
    
    public NavigatorTreeNode findNode(DataSource dataSource)
    {
        Enumeration<DefaultMutableTreeNode> en = ((DefaultMutableTreeNode) getRoot()).breadthFirstEnumeration();
        while(en.hasMoreElements())
        {
            DefaultMutableTreeNode node = en.nextElement();
            if(node instanceof NavigatorTreeNode)
            {
                NavigatorTreeNode nnode = (NavigatorTreeNode)node;
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
            insertNodeInto(new NavigatorTreeNode(dataSource), alignmentsNode, alignmentsNode.getChildCount());
        }
        
        if(dataSource instanceof Annotations)
        {
            insertNodeInto(new NavigatorTreeNode(dataSource), annotationsNode, annotationsNode.getChildCount());
        }
        
        if(dataSource instanceof Matrix)
        {
             insertNodeInto(new NavigatorTreeNode(dataSource), matricesNode, matricesNode.getChildCount());
        }
        
        if(dataSource instanceof SecondaryStructure || dataSource instanceof StructureList)
        {
            insertNodeInto(new NavigatorTreeNode(dataSource), structuresNode, structuresNode.getChildCount());
        }
        
        if(dataSource instanceof Tabular || dataSource instanceof TabularField)
        {
            insertNodeInto(new NavigatorTreeNode(dataSource), tabularNode, tabularNode.getChildCount());
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
