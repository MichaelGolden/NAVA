/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.structurevis.navigator;

import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.List;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.event.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import nava.structurevis.StructureVisController;
import nava.structurevis.StructureVisPanel;
import nava.structurevis.SubstructurePanel;
import nava.structurevis.data.*;
import nava.ui.ProjectController;

/**
 *
 * @author Michael
 */
public class DataOverlayTreePanel extends javax.swing.JPanel implements ActionListener, MouseListener, TreeSelectionListener, TreeModelListener {

    ProjectController projectController;
    StructureVisController structureVisController;
    JPopupMenu popupMenu = new JPopupMenu();
    JMenuItem setAsOverlayItem = new JMenuItem("View");
    JMenuItem editItem = new JMenuItem("Edit");
    JMenuItem deleteItem = new JMenuItem("Delete");

    /**
     * Creates new form NavigatorPanel
     */
    public DataOverlayTreePanel(ProjectController projectController, StructureVisController structureVisController) {
        initComponents();

        this.projectController = projectController;
        this.structureVisController = structureVisController;


        DefaultMutableTreeNode root = new DefaultMutableTreeNode();
        if (structureVisController.substructureModel.overlayNavigatorTreeModel == null) {
            structureVisController.substructureModel.overlayNavigatorTreeModel = new DataOverlayTreeModel(root, structureVisController);
        } else {
        }
        
        structureVisController.substructureModel.overlayNavigatorTreeModel.addTreeModelListener(this);

        DataOverlayTreeRenderer navigatorRenderer = new DataOverlayTreeRenderer();
        navigationTree.setRootVisible(false);
        navigationTree.setModel(structureVisController.substructureModel.overlayNavigatorTreeModel);
        navigationTree.setCellRenderer(navigatorRenderer);
        navigationTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        navigationTree.addTreeSelectionListener(this);
        navigationTree.setDropTarget(new DropTarget() {

            public synchronized void drop(DropTargetDropEvent evt) {
                try {
                    evt.acceptDrop(DnDConstants.ACTION_COPY);
                    List<File> droppedFiles = (List<File>) evt.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                    for (int i = 0; i < droppedFiles.size(); i++) {
                        /*
                         * NavigatorPanel.this.projectController.autoAddDataSource(droppedFiles.get(i));
                         * if (droppedFiles.get(i).isDirectory()) {
                         * System.err.println("TODO this file is a folder. How
                         * should we handle this?"); }
                         */
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        setAsOverlayItem.addActionListener(this);
        popupMenu.add(setAsOverlayItem);
        editItem.addActionListener(this);
        popupMenu.add(editItem);
        deleteItem.addActionListener(this);
        popupMenu.add(deleteItem);

        navigationTree.addMouseListener(this);
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

    public void addNavigationListener(DataOverlayTreeListener listener) {
        listeners.add(DataOverlayTreeListener.class, listener);
    }

    public void removeNavigationListener(DataOverlayTreeListener listener) {
        listeners.remove(DataOverlayTreeListener.class, listener);
    }

    public void fireDataSourceSelectionChanged(DataOverlayTreeEvent evt) {
        Object[] listeners = this.listeners.getListenerList();
        // Each listener occupies two elements - the first is the listener class
        // and the second is the listener instance
        for (int i = 0; i < listeners.length; i += 2) {
            if (listeners[i] == DataOverlayTreeListener.class) {
                ((DataOverlayTreeListener) listeners[i + 1]).dataSourceSelectionChanged(evt);
            }
        }
    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        DataOverlayTreeEvent navigationEvent = new DataOverlayTreeEvent();
        TreePath[] paths = navigationTree.getSelectionPaths();
        if (paths != null) {
            for (TreePath path : paths) {
                DataOverlayTreeNode node = (DataOverlayTreeNode) path.getLastPathComponent();
                if (node.overlay != null) {
                    navigationEvent.selectedDataSources.add(node.overlay);
                }
            }

            this.fireDataSourceSelectionChanged(navigationEvent);
        }
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

    @Override
    public void mouseClicked(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
            TreePath tp = navigationTree.getClosestPathForLocation(e.getX(), e.getY());
            Overlay selectedOverlay = ((DataOverlayTreeNode) tp.getLastPathComponent()).overlay;
            if (selectedOverlay != null) {
                if (tp != null) {
                    navigationTree.setSelectionPath(tp);
                }
                popupMenu.show(e.getComponent(), e.getX(), e.getY());
                switch (selectedOverlay.getState()) {
                    case PRIMARY_SELECTED:
                    case SECONDARY_SELECTED:
                        setAsOverlayItem.setText("Hide");
                        break;
                    case UNSELECTED:
                        setAsOverlayItem.setText("View");
                        break;
                }
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(setAsOverlayItem)) {
            Overlay overlay = ((DataOverlayTreeNode) navigationTree.getSelectionPath().getLastPathComponent()).overlay;

            if (overlay instanceof DataOverlay1D) {
                if (overlay.getState() == Overlay.OverlayState.PRIMARY_SELECTED) {
                    structureVisController.substructureModel.setDataSource1D(null);
                } else {
                    structureVisController.substructureModel.setDataSource1D((DataOverlay1D) overlay);
                }
            } else if (overlay instanceof DataOverlay2D) {
                if (overlay.getState() == Overlay.OverlayState.PRIMARY_SELECTED) {
                    structureVisController.substructureModel.setDataSource2D(null);
                } else {
                    structureVisController.substructureModel.setDataSource2D((DataOverlay2D) overlay);
                }
            } else if (overlay instanceof NucleotideComposition) {
                if (overlay.getState() == Overlay.OverlayState.PRIMARY_SELECTED) {
                    structureVisController.substructureModel.setNucleotideSource(null);
                } else {
                    structureVisController.substructureModel.setNucleotideSource((NucleotideComposition) overlay);
                }
            } else if (overlay instanceof StructureSource) {
                if (overlay.getState() == Overlay.OverlayState.PRIMARY_SELECTED) {
                    structureVisController.substructureModel.setStructureSource(null);
                } else {
                    structureVisController.substructureModel.setStructureSource((StructureSource) overlay);
                }

            }

            // update node icons on tree
            structureVisController.substructureModel.overlayNavigatorTreeModel.valueForPathChanged(navigationTree.getSelectionPath(), overlay);
        } else if (e.getSource().equals(editItem)) {
            Overlay overlay = ((DataOverlayTreeNode) navigationTree.getSelectionPath().getLastPathComponent()).overlay;

            if (overlay instanceof DataOverlay1D) {
                StructureVisPanel.showEditDialog((DataOverlay1D) overlay, null, projectController.projectModel, structureVisController);
            } else if (overlay instanceof DataOverlay2D) {
                StructureVisPanel.showEditDialog((DataOverlay2D) overlay, null, projectController.projectModel, structureVisController);

            } else if (overlay instanceof NucleotideComposition) {
                StructureVisPanel.showEditDialog((NucleotideComposition) overlay, null, projectController.projectModel, structureVisController);

            } else if (overlay instanceof StructureSource) {

                SubstructurePanel.showEditDialog((StructureSource) overlay, null, projectController, structureVisController);
            }

            // update node icons on tree
            structureVisController.substructureModel.overlayNavigatorTreeModel.valueForPathChanged(navigationTree.getSelectionPath(), overlay);
        }
    }
}
