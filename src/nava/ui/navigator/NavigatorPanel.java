/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.ui.navigator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
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
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import nava.data.types.DataSource;
import nava.ui.*;
import nava.utils.GraphicsUtils;
import nava.utils.SafeListModel;

/**
 *
 * @author Michael
 */
public class NavigatorPanel extends javax.swing.JPanel implements ActionListener, TreeSelectionListener, TreeModelListener, ProjectView, MouseListener {

    public static final ImageIcon addIcon = new ImageIcon(ClassLoader.getSystemResource("resources/icons/add-16x16.png"));
    JButton importButton = new JButton("Import file", addIcon);
    ProjectController projectController;
    JFileChooser importFileChooser = new JFileChooser();
    JPopupMenu popupMenu = new JPopupMenu();
    //JPopupMenu popupMenu2 = new JPopupMenu();
    JMenuItem exportItem = new JMenuItem("Export");
    JMenuItem deleteItem = new JMenuItem("Delete");

    /**
     * Creates new form NavigatorPanel
     */
    public NavigatorPanel(ProjectController projectController) {
        initComponents();

        this.projectController = projectController;


        if (projectController.projectModel.navigatorTreeModel == null) {
            projectController.projectModel.navigatorTreeModel = new NavigatorTreeModel(new DefaultMutableTreeNode(), this.projectController.projectModel);
        }


        projectController.addView(this);

        NavigatorTreeRenderer navigatorRenderer = new NavigatorTreeRenderer();
        navigationTree.setRootVisible(false);
        navigationTree.setModel(projectController.projectModel.navigatorTreeModel);
        navigationTree.setCellRenderer(navigatorRenderer);
        navigationTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        navigationTree.addTreeSelectionListener(this);

        navigationTree.addMouseListener(this);
        navigationTree.setDropTarget(new DropTarget() {

            public synchronized void drop(DropTargetDropEvent evt) {
                try {
                    evt.acceptDrop(DnDConstants.ACTION_COPY);
                    final List<File> droppedFiles = (List<File>) evt.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                    Thread taskThread = new Thread() {

                        public void run() {
                            try {
                                SwingUtilities.invokeAndWait(new Runnable() {

                                    public void run() {
                                        if (droppedFiles.size() == 1) {
                                            MainFrame.progressBarMonitor.set(true, ProgressBarMonitor.IMPORT_DATASOURCE, 0);
                                        } else {
                                            MainFrame.progressBarMonitor.set(true, ProgressBarMonitor.IMPORT_DATASOURCES, 0);
                                        }
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
                                    NavigatorPanel.this.projectController.autoAddDataSourceWithAmbiguityResolution(droppedFiles.get(i));
                                }
                            }

                            MainFrame.progressBarMonitor.set(false, ProgressBarMonitor.INACTIVE, ProgressBarMonitor.INACTIVE_VALUE);
                            MainFrame.self.setEnabled(true);
                        }
                    };
                    taskThread.start();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });


        JTextArea textArea = new JTextArea("Drop and drag a file into the area above to import or click 'Import file'.");
        textArea.setFont(textArea.getFont().deriveFont(Font.ITALIC));
        textArea.setLineWrap(true);
        textArea.setEditable(false);
        textArea.setBorder(new EmptyBorder(1, 4, 3, 3));
        textArea.setOpaque(false);
        textArea.setBackground(new Color(0, 0, 0, 0));
        textArea.setWrapStyleWord(true);
        this.addDataSourcePanel.add(textArea, BorderLayout.CENTER);

        importButton.addActionListener(this);
        importButton.setIconTextGap(8);
        //importButton.setHorizontalAlignment(SwingConstants.LEFT);
        this.addDataSourcePanel.add(importButton, BorderLayout.SOUTH);

        importFileChooser.setApproveButtonText("Import");
        importFileChooser.setMultiSelectionEnabled(true);

        exportItem.addActionListener(this);
        popupMenu.add(exportItem);
        deleteItem.addActionListener(this);
        popupMenu.add(deleteItem);
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
        jPanel1 = new javax.swing.JPanel();
        navigationTree = new javax.swing.JTree();
        addDataSourcePanel = new javax.swing.JPanel();

        setLayout(new java.awt.BorderLayout());

        jPanel1.setLayout(new java.awt.BorderLayout());
        jPanel1.add(navigationTree, java.awt.BorderLayout.CENTER);

        jScrollPane1.setViewportView(jPanel1);

        add(jScrollPane1, java.awt.BorderLayout.CENTER);

        addDataSourcePanel.setLayout(new java.awt.BorderLayout());
        add(addDataSourcePanel, java.awt.BorderLayout.PAGE_END);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel addDataSourcePanel;
    private javax.swing.JPanel jPanel1;
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
        if(paths != null)
        {
            for (TreePath path : paths) {
                NavigatorTreeNode node = (NavigatorTreeNode) path.getLastPathComponent();
                node.isNew = false;
                if (node.dataSource != null) {
                    navigationEvent.selectedDataSources.add(node.dataSource);
                }
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
        navigationTree.expandPath(e.getTreePath().getParentPath());
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
    public void projectModelChanged(ProjectModel newProjectModel) {
        this.projectController.projectModel.navigatorTreeModel.removeTreeModelListener(this);
        this.projectController.projectModel = newProjectModel;
        navigationTree.setModel(newProjectModel.navigatorTreeModel);
        newProjectModel.navigatorTreeModel.addTreeModelListener(this);
    }

    @Override
    public void dataSourcesLoaded() {
    }

    @Override
    public void dataSourcesIntervalAdded(ListDataEvent e) {
        for (int i = e.getIndex0(); i < e.getIndex1() + 1; i++) {
            //if(projectController.projectModel.dataSources.get(i).deleted)
          //  {
                projectController.projectModel.navigatorTreeModel.addDataSource(projectController.projectModel.dataSources.get(i));
      
            System.out.println("Adding " + i);
                 // }
        }
    }

    @Override
    public void dataSourcesIntervalRemoved(ListDataEvent e) {
    }

    @Override
    public void dataSourcesContentsChanged(ListDataEvent e) {
        SafeListModel<DataSource> dataSources = projectController.projectModel.dataSources;
        for(int i = e.getIndex0() ; i <= e.getIndex1() ; i++)
        {
            //if(projectController.projectModel.dataSources.get(i).deleted)
           // {
                NavigatorTreeNode node = projectController.projectModel.navigatorTreeModel.findNode(dataSources.get(i));
                projectController.projectModel.navigatorTreeModel.nodeChanged(node);
           // }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(this.importButton)) {
            importFileChooser.setSelectedFile(MainFrame.browseDialog.getCurrentDirectory());
            importFileChooser.showOpenDialog(this);
            final File[] files = importFileChooser.getSelectedFiles();
            Thread taskThread = new Thread() {

                public void run() {
                    try {
                        SwingUtilities.invokeAndWait(new Runnable() {

                            public void run() {
                                if (files.length == 1) {
                                    MainFrame.progressBarMonitor.set(true, ProgressBarMonitor.IMPORT_DATASOURCE, 0);
                                } else {
                                    MainFrame.progressBarMonitor.set(true, ProgressBarMonitor.IMPORT_DATASOURCES, 0);
                                }
                                MainFrame.self.setEnabled(false);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    for (File file : files) {
                        NavigatorPanel.this.projectController.autoAddDataSourceWithAmbiguityResolution(file);
                    }

                    MainFrame.progressBarMonitor.set(false, ProgressBarMonitor.INACTIVE, ProgressBarMonitor.INACTIVE_VALUE);
                    MainFrame.self.setEnabled(true);
                }
            };
            taskThread.start();
        }
        else
        if(e.getSource().equals(exportItem))
        {
            DataSource dataSource = ((NavigatorTreeNode) navigationTree.getSelectionPath().getLastPathComponent()).dataSource;
            ExportDataDialog exportDialog = new ExportDataDialog(MainFrame.self, true, projectController, dataSource);
            GraphicsUtils.centerWindowOnWindow(exportDialog, MainFrame.self);
            exportDialog.setVisible(true);
        }
        else
        if(e.getSource().equals(deleteItem))
        {
             DataSource dataSource = ((NavigatorTreeNode) navigationTree.getSelectionPath().getLastPathComponent()).dataSource;
              int n = JOptionPane.showConfirmDialog(MainFrame.self,
            "Warning, you are about to delete a data source!\nAre you sure you wish to continue?",
            "Warning",
             JOptionPane.YES_NO_OPTION);
            if(n == 0)
            {
                this.projectController.projectModel.navigatorTreeModel.deleteDataSource(dataSource);
            }
             
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
            TreePath tp = navigationTree.getClosestPathForLocation(e.getX(), e.getY());
            DataSource dataSource = ((NavigatorTreeNode) tp.getLastPathComponent()).dataSource;
            if (dataSource != null) {
                if (tp != null) {
                    navigationTree.setSelectionPath(tp);
                }

                popupMenu.show(e.getComponent(), e.getX(), e.getY());
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
}
