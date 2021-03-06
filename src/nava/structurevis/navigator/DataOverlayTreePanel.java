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
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import nava.data.types.DataSource;
import nava.data.types.DataType;
import nava.data.types.Tabular;
import nava.structurevis.*;
import nava.structurevis.data.*;
import nava.ui.*;
import nava.ui.navigator.NavigatorPanel;
import nava.utils.Pair;

/**
 *
 * @author Michael
 */
public class DataOverlayTreePanel extends javax.swing.JPanel implements ActionListener, MouseListener, TreeSelectionListener, TreeModelListener, ProjectView, StructureVisView {

    ProjectController projectController;
    StructureVisController structureVisController;
    JPopupMenu popupMenu = new JPopupMenu();
    //JPopupMenu popupMenu2 = new JPopupMenu();
    JMenuItem setAsOverlayItem = new JMenuItem("View");
    JMenuItem editItem = new JMenuItem("Edit");
    JMenuItem deleteItem = new JMenuItem("Delete");
    JMenuItem saveItem = new JMenuItem("Save mapped data (CSV)");

    /**
     * Creates new form NavigatorPanel
     */
    public DataOverlayTreePanel(ProjectController projectController, StructureVisController structureVisController) {
        initComponents();

        this.projectController = projectController;
        this.structureVisController = structureVisController;

        projectController.addView(this);
        structureVisController.addView(this);

        if (structureVisController.structureVisModel.overlayNavigatorTreeModel == null) {
            structureVisController.structureVisModel.overlayNavigatorTreeModel = new DataOverlayTreeModel(new DefaultMutableTreeNode(), structureVisController);
        }
        
        structureVisController.structureVisModel.overlayNavigatorTreeModel.addTreeModelListener(this);

        DataOverlayTreeRenderer navigatorRenderer = new DataOverlayTreeRenderer();
        overlayTree.setRootVisible(false);
        overlayTree.setModel(structureVisController.structureVisModel.overlayNavigatorTreeModel);
        overlayTree.setCellRenderer(navigatorRenderer);
        overlayTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        overlayTree.addTreeSelectionListener(this);
        overlayTree.setDropTarget(new DropTarget() {

            public synchronized void drop(DropTargetDropEvent evt) {
                try {
                    evt.acceptDrop(DnDConstants.ACTION_COPY);
                    final List<File> droppedFiles = (List<File>) evt.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                    if (droppedFiles.size() == 1) {
                        Thread taskThread = new Thread() {

                            public void run() {
                                try {
                                    SwingUtilities.invokeAndWait(new Runnable() {

                                        public void run() {
                                            MainFrame.progressBarMonitor.set(true, ProgressBarMonitor.IMPORT_DATASOURCE, 0);
                                            MainFrame.self.setEnabled(false);
                                        }
                                    });
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                for (int i = 0; i < droppedFiles.size(); i++) {
                                    if (droppedFiles.get(i).isDirectory()) {
                                        System.err.println("TODO this file is a folder. How should we handle this?");
                                    } else {
                                        Pair<DataType, DataSource> dataTypeAndSource = DataOverlayTreePanel.this.projectController.autoAddDataSourceWithAmbiguityResolution(droppedFiles.get(i));
                                        if (dataTypeAndSource != null) {
                                            DataSource dataSource = dataTypeAndSource.getRight();
                                            StructureVisPanel.showAddDialog(null, DataOverlayTreePanel.this.projectController.projectModel, DataOverlayTreePanel.this.structureVisController, dataSource);
                                        }
                                    }
                                }

                                MainFrame.progressBarMonitor.set(false, ProgressBarMonitor.INACTIVE, ProgressBarMonitor.INACTIVE_VALUE);
                                MainFrame.self.setEnabled(true);
                            }
                        };
                        taskThread.start();



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

        /*
         * popupMenu2.add(setAsOverlayItem); editItem.addActionListener(this);
         * popupMenu2.add(editItem); deleteItem.addActionListener(this);
         * popupMenu2.add(deleteItem);
         */
        saveItem.addActionListener(this);
        popupMenu.add(saveItem);

        overlayTree.addMouseListener(this);
        overlayTree.expandRow(0);
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
        overlayTree = new javax.swing.JTree();

        setLayout(new java.awt.BorderLayout());

        jScrollPane1.setViewportView(overlayTree);

        add(jScrollPane1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTree overlayTree;
    // End of variables declaration//GEN-END:variables
    protected EventListenerList listeners = new EventListenerList();

    public void addNavigationListener(DataOverlayTreeListener listener) {
        listeners.add(DataOverlayTreeListener.class, listener);
    }

    public void removeNavigationListener(DataOverlayTreeListener listener) {
        listeners.remove(DataOverlayTreeListener.class, listener);
    }

    public void fireDataOverlaySelectionChanged(DataOverlayTreeEvent evt) {
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
        TreePath[] paths = overlayTree.getSelectionPaths();
        if (paths != null) {
            for (TreePath path : paths) {
                DataOverlayTreeNode node = (DataOverlayTreeNode) path.getLastPathComponent();
                if (node.overlay != null) {
                    navigationEvent.selectedDataSources.add(node.overlay);
                }
            }

            this.fireDataOverlaySelectionChanged(navigationEvent);
        }
    }

    @Override
    public void treeNodesChanged(TreeModelEvent e) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void treeNodesInserted(TreeModelEvent e) {
        overlayTree.expandPath(e.getTreePath());
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
            TreePath tp = overlayTree.getClosestPathForLocation(e.getX(), e.getY());
            Overlay selectedOverlay = ((DataOverlayTreeNode) tp.getLastPathComponent()).overlay;
            if (selectedOverlay != null) {
                if (tp != null) {
                    overlayTree.setSelectionPath(tp);
                }
                if (selectedOverlay instanceof DataOverlay1D && structureVisController.structureVisModel.substructureModel.structureOverlay != null) {
                    this.saveItem.setEnabled(true);
                } else {
                    this.saveItem.setEnabled(false);
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
            Overlay overlay = ((DataOverlayTreeNode) overlayTree.getSelectionPath().getLastPathComponent()).overlay;
            Overlay oldOverlay = null;

            if (overlay instanceof DataOverlay1D) {
                oldOverlay = structureVisController.structureVisModel.substructureModel.data1D;
                if (overlay.getState() == Overlay.OverlayState.PRIMARY_SELECTED) {
                    structureVisController.structureVisModel.substructureModel.setOverlay1D(null);
                } else {
                    structureVisController.structureVisModel.substructureModel.setOverlay1D((DataOverlay1D) overlay);
                }
            } else if (overlay instanceof DataOverlay2D) {
                oldOverlay = structureVisController.structureVisModel.substructureModel.data2D;
                if (overlay.getState() == Overlay.OverlayState.PRIMARY_SELECTED) {
                    structureVisController.structureVisModel.substructureModel.setOverlay2D(null);
                } else {
                    structureVisController.structureVisModel.substructureModel.setOverlay2D((DataOverlay2D) overlay);
                }
            } else if (overlay instanceof NucleotideComposition) {
                oldOverlay = structureVisController.structureVisModel.substructureModel.nucleotideSource;
                if (overlay.getState() == Overlay.OverlayState.PRIMARY_SELECTED) {
                    structureVisController.structureVisModel.substructureModel.setNucleotideOverlay(null);
                } else {
                    structureVisController.structureVisModel.substructureModel.setNucleotideOverlay((NucleotideComposition) overlay);
                }
            } else if (overlay instanceof StructureOverlay) {
                oldOverlay = structureVisController.structureVisModel.substructureModel.structureOverlay;
                if (overlay.getState() == Overlay.OverlayState.PRIMARY_SELECTED) {
                    structureVisController.structureVisModel.substructureModel.setStructureOverlay(null);
                } else {
                    structureVisController.structureVisModel.substructureModel.setStructureOverlay((StructureOverlay) overlay);
                }

            }

            // update node icons on tree
            if (oldOverlay != null) {
                structureVisController.structureVisModel.overlayNavigatorTreeModel.valueForPathChanged(new TreePath(structureVisController.structureVisModel.overlayNavigatorTreeModel.findNode(oldOverlay).getPath()), oldOverlay);
            }
            structureVisController.structureVisModel.overlayNavigatorTreeModel.valueForPathChanged(overlayTree.getSelectionPath(), overlay);
        } else if (e.getSource().equals(editItem)) {
            Overlay overlay = ((DataOverlayTreeNode) overlayTree.getSelectionPath().getLastPathComponent()).overlay;

            if (overlay instanceof DataOverlay1D) {
                StructureVisPanel.showEditDialog((DataOverlay1D) overlay, null, projectController.projectModel, structureVisController);
            } else if (overlay instanceof DataOverlay2D) {
                StructureVisPanel.showEditDialog((DataOverlay2D) overlay, null, projectController.projectModel, structureVisController);

            } else if (overlay instanceof NucleotideComposition) {
                StructureVisPanel.showEditDialog((NucleotideComposition) overlay, null, projectController.projectModel, structureVisController);

            } else if (overlay instanceof StructureOverlay) {
                StructureVisPanel.showEditDialog((StructureOverlay) overlay, null, projectController.projectModel, structureVisController);
            }

            // update node icons on tree
            structureVisController.structureVisModel.overlayNavigatorTreeModel.valueForPathChanged(overlayTree.getSelectionPath(), overlay);
        } else if (e.getSource().equals(saveItem)) {
            File directoryFile = MainFrame.saveDialog.getCurrentDirectory();
            if (MainFrame.saveDialog.getSelectedFile() != null) {
                directoryFile = MainFrame.saveDialog.getSelectedFile().isDirectory() ? MainFrame.saveDialog.getSelectedFile() : MainFrame.saveDialog.getSelectedFile().getParentFile();
            }


            MainFrame.saveDialog.setSelectedFile(new File(directoryFile.getAbsolutePath() + File.separator + "data.csv"));
            MainFrame.saveDialog.showSaveDialog(MainFrame.self);
            File saveFile = MainFrame.saveDialog.getSelectedFile();
            if (saveFile != null) {
                try {
                    Overlay overlay = ((DataOverlayTreeNode) overlayTree.getSelectionPath().getLastPathComponent()).overlay;
                    if (overlay instanceof DataOverlay1D) {
                        structureVisController.structureVisModel.substructureModel.saveDataOverlay1DAndStructure((DataOverlay1D) overlay, saveFile);
                    }
                } catch (IOException ex) {
                    Logger.getLogger(DataOverlayTreePanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
         else if (e.getSource().equals(deleteItem)) 
        {
            Overlay overlay = ((DataOverlayTreeNode) overlayTree.getSelectionPath().getLastPathComponent()).overlay;
            int n = JOptionPane.showConfirmDialog(MainFrame.self,
            "Warning, you are about to delete a data overlay!\nAre you sure you wish to continue?",
            "Warning",
             JOptionPane.YES_NO_OPTION);
            if(n == 0)
            {
                this.structureVisController.structureVisModel.overlayNavigatorTreeModel.deleteDataOverlay(overlay);
            }
        }
    }

    @Override
    public void projectModelChanged(ProjectModel newProjectModel) {
    }

    @Override
    public void dataSourcesLoaded() {
    }

    @Override
    public void dataSourcesIntervalAdded(ListDataEvent e) {
    }

    @Override
    public void dataSourcesIntervalRemoved(ListDataEvent e) {
    }

    @Override
    public void dataSourcesContentsChanged(ListDataEvent e) {
    }

    @Override
    public void structureVisModelChanged(StructureVisModel newStructureVisModel) {
        
        this.overlayTree.setModel(newStructureVisModel.overlayNavigatorTreeModel);
        newStructureVisModel.overlayNavigatorTreeModel.addTreeModelListener(this);
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
