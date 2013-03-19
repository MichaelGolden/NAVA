/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.ui.navigator;

import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
import java.util.List;
import javax.swing.event.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import nava.ui.ProjectController;

/**
 *
 * @author Michael
 */
public class NavigatorPanel extends javax.swing.JPanel implements TreeSelectionListener, TreeModelListener {

    ProjectController projectController;   
    /**
     * Creates new form NavigatorPanel
     */
    public NavigatorPanel(ProjectController projectController) {
        initComponents();

        this.projectController = projectController;


        DefaultMutableTreeNode root = new DefaultMutableTreeNode();
        if(projectController.projectModel.navigatorTreeModel == null)
        {
            projectController.projectModel.navigatorTreeModel = new NavigatorTreeModel(root, projectController.projectModel);
        }
        
        projectController.projectModel.navigatorTreeModel.addTreeModelListener(this);
        projectController.addView(projectController.projectModel.navigatorTreeModel);
        
        NavigatorTreeRenderer navigatorRenderer = new NavigatorTreeRenderer();
        navigationTree.setRootVisible(false);
        navigationTree.setModel(projectController.projectModel.navigatorTreeModel);
        navigationTree.setCellRenderer(navigatorRenderer);
        navigationTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        navigationTree.addTreeSelectionListener(this);

        navigationTree.setDropTarget(new DropTarget() {

            public synchronized void drop(DropTargetDropEvent evt) {
                try {
                    evt.acceptDrop(DnDConstants.ACTION_COPY);
                    List<File> droppedFiles = (List<File>) evt.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                    for (int i = 0; i < droppedFiles.size(); i++) {
                        NavigatorPanel.this.projectController.autoAddDataSource(droppedFiles.get(i));
                        if (droppedFiles.get(i).isDirectory()) {
                            System.err.println("TODO this file is a folder. How should we handle this?");
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }); 
        
        
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
        navigationTree = new javax.swing.JTree();

        setLayout(new java.awt.BorderLayout());

        jScrollPane1.setViewportView(navigationTree);

        add(jScrollPane1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTree navigationTree;
    // End of variables declaration//GEN-END:variables
    protected EventListenerList listeners = new EventListenerList();

    public void addNavigationListener(NavigationListener listener) {
        listeners.add(NavigationListener.class, listener);
    }

    public void removeNavigationListener(NavigationListener listener) {
        listeners.remove(NavigationListener.class, listener);
    }

    public void fireDataSourceSelectionChanged(NavigationEvent evt) {
        Object[] listeners = this.listeners.getListenerList();
        // Each listener occupies two elements - the first is the listener class
        // and the second is the listener instance
        for (int i = 0; i < listeners.length; i += 2) {
            if (listeners[i] == NavigationListener.class) {
                ((NavigationListener) listeners[i + 1]).dataSourceSelectionChanged(evt);
            }
        }
    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        NavigationEvent navigationEvent = new NavigationEvent();       
        TreePath[] paths = navigationTree.getSelectionPaths();
        for (TreePath path : paths) {
            NavigatorTreeNode node = (NavigatorTreeNode) path.getLastPathComponent();
            node.isNew = false;
            if (node.dataSource != null) 
            {               
                navigationEvent.selectedDataSources.add(node.dataSource);                
            }
        }
        
        this.fireDataSourceSelectionChanged(navigationEvent);
    }

    @Override
    public void treeNodesChanged(TreeModelEvent e) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void treeNodesInserted(TreeModelEvent e) {
        navigationTree.expandPath(e.getTreePath());
    }

    @Override
    public void treeNodesRemoved(TreeModelEvent e) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void treeStructureChanged(TreeModelEvent e) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }
}
