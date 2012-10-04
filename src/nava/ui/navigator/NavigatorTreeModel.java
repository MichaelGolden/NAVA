/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.ui.navigator;

import javax.swing.event.ListDataEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import nava.data.types.*;
import nava.ui.ProjectController;
import nava.ui.ProjectView;

/**
 *
 * @author Michael
 */
public class NavigatorTreeModel extends DefaultTreeModel implements ProjectView {

    DefaultMutableTreeNode origDataNode = null;
    DefaultMutableTreeNode dataNode = null;
    DefaultMutableTreeNode alignmentsNode = null;
    DefaultMutableTreeNode annotationsNode = null;
    DefaultMutableTreeNode matricesNode = null;
    DefaultMutableTreeNode structuresNode = null;
    DefaultMutableTreeNode tabularNode = null;
    
    ProjectController projectController;

    public NavigatorTreeModel(DefaultMutableTreeNode root, ProjectController projectController) {
        super(root);
        this.projectController = projectController;

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
        
        if(dataSource instanceof TabularData || dataSource instanceof TabularField)
        {
            insertNodeInto(new NavigatorTreeNode(dataSource), tabularNode, tabularNode.getChildCount());
        }
    }

    @Override
    public void dataSourcesLoaded() {
        System.out.println("dataSourcesLoaded");
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void dataSourcesIntervalAdded(ListDataEvent e) {
        System.out.println("NavigatorTreeModel dataSourcesIntervalAdded");
        for(int i = e.getIndex0() ; i < e.getIndex1() + 1 ; i++)
        {
            addDataSource(projectController.projectModel.dataSources.get(i));
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
