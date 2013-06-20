/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.ui;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import nava.tasks.applications.ApplicationController;
import nava.data.types.DataSourceCache;
import nava.ranking.PairTestPanel;
import nava.ranking.RankingPanel;
import nava.structurevis.FullGenomeDrawPanel;
import nava.structurevis.StructureVisModel;
import nava.structurevis.StructureVisPanel;
import nava.tasks.TaskManager;
import nava.ui.StartupDialog.Startup;
import nava.ui.navigator.NavigatorPanel;
import nava.utils.GraphicsUtils;
import nava.utils.MenuAction;

/**
 *
 * @author Michael
 */
public class MainFrame extends javax.swing.JFrame implements WindowListener, ActionListener {

    public static PropertyResourceBundle resources = (PropertyResourceBundle) ResourceBundle.getBundle("resources.text.text");
    public static TaskManager taskManager;
    public static DataSourceCache dataSourceCache = new DataSourceCache();
    public static ProgressBarMonitor progressBarMonitor = new ProgressBarMonitor();
    public static ProgressMonitor progressMonitor;
    public static MainFrame self;
    ProjectController projectController;
    ApplicationController appController;
    NavigatorPanel navigatorPanel;
    //ApplicationPanel applicationPanel;
    public static Font fontLiberationSans = new Font("Sans", Font.PLAIN, 12);
    public static Font fontDroidSansMono = new Font("Sans", Font.PLAIN, 12);
    StructureVisPanel structureVisPanel;
    public static JFileChooser saveDialog = new JFileChooser();
    public static JFileChooser browseDialog = new JFileChooser();
    public static JFileChooser projectDialog = new JFileChooser();
    StartupDialog startupDialog;
    public static File workspaceDirectory = new File("workspace" + File.separator);

    public void startup() {
        MainFrame.self = this;
        progressMonitor = new ProgressMonitor(this, "", "", 0, 100);;
        try {
            fontLiberationSans = Font.createFont(Font.PLAIN, ClassLoader.getSystemResourceAsStream("resources/fonts/LiberationSans-Regular.ttf")).deriveFont(12.0f);
            fontDroidSansMono = Font.createFont(Font.PLAIN, ClassLoader.getSystemResourceAsStream("resources/fonts/DroidSansMono.ttf")).deriveFont(12.0f);
        } catch (FontFormatException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        workspaceDirectory.mkdir();
    }

    /**
     * Creates new form MainFrame
     */
    public MainFrame() {
        initComponents();
        startup();


        ProjectModel model = new ProjectModel();
        File projectFile = new File("workspace/test_project/projaaect.data");
        if (projectFile.exists()) {
            try {
                model = ProjectModel.loadProject(projectFile);
            } catch (IOException ex) {
                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        projectController = new ProjectController();
        projectController.openProject(model);
        taskManager = new TaskManager(projectController);
        appController = new ApplicationController(MainFrame.taskManager);

        DataPanel dataPanel = new DataPanel(projectController, appController);
        jPanel1.add(dataPanel, BorderLayout.CENTER);
        structureVisPanel = new StructureVisPanel(projectController);
        jPanel2.add(structureVisPanel, BorderLayout.CENTER);

        //projectDialog.setFileFilter(new ProjectFileFilter());
        projectDialog.setFileView(new ProjectFileView());
        projectDialog.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        this.setIconImage(new ImageIcon(ClassLoader.getSystemResource("resources/icons/icon-32x32.png")).getImage());
        this.addWindowListener(this);
    }

    public void createNewProject(File projectDirectory) {
        if (projectDirectory != null) {
            projectDirectory.mkdirs();
            projectController.openProject(new ProjectModel(projectDirectory.getAbsolutePath()));
            StartupDialog.addProjectFileToRecentProjects(projectDirectory);
            StructureVisModel model = new StructureVisModel();
            model.initialise(structureVisPanel.structureVisController);
            structureVisPanel.structureVisController.openStructureVisModel(model);
        }
    }

    public void openProject(File projectDirectory) {
        try {
            File projectFile = new File(projectDirectory.getAbsoluteFile() + File.separator + "project.data");
            if (projectFile.exists()) {
                projectController.openProject(ProjectModel.loadProject(projectFile));
                StartupDialog.addProjectFileToRecentProjects(projectFile);
                File structureVisModelFile = new File(projectDirectory.getAbsolutePath() + File.separatorChar + "structurevis.model");
                if (structureVisModelFile.exists()) {
                    structureVisPanel.structureVisController.openStructureVisModel(StructureVisModel.loadProject(structureVisModelFile, structureVisPanel.structureVisController));
                } else {
                    StructureVisModel model = new StructureVisModel();
                    model.initialise(structureVisPanel.structureVisController);
                    structureVisPanel.structureVisController.openStructureVisModel(model);
                }
            } else {
                projectController.openProject(new ProjectModel(projectDirectory.getAbsolutePath()));
                StructureVisModel model = new StructureVisModel();
                model.initialise(structureVisPanel.structureVisController);
                structureVisPanel.structureVisController.openStructureVisModel(model);

            }
        } catch (IOException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem6 = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenu4 = new javax.swing.JMenu();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        jMenuItem3 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Nucleic Acid Visualisation and Analysis");
        setMinimumSize(new java.awt.Dimension(600, 400));
        setPreferredSize(new java.awt.Dimension(800, 600));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jTabbedPane1.setToolTipText("");

        jPanel1.setLayout(new java.awt.BorderLayout());
        jTabbedPane1.addTab("Data input", jPanel1);

        jPanel2.setPreferredSize(new java.awt.Dimension(800, 0));
        jPanel2.setLayout(new java.awt.BorderLayout());
        jTabbedPane1.addTab("Visualisation", jPanel2);

        jTabbedPane1.setSelectedComponent(jPanel1);

        getContentPane().add(jTabbedPane1, java.awt.BorderLayout.CENTER);

        jMenu1.setText("File");

        jMenuItem6.setText("Start a new project");
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem6ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem6);

        jMenuItem1.setText("Open project");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenu2.setText("Open recently used");
        jMenu2.addMenuListener(new javax.swing.event.MenuListener() {
            public void menuCanceled(javax.swing.event.MenuEvent evt) {
            }
            public void menuSelected(javax.swing.event.MenuEvent evt) {
                jMenu2MenuSelected(evt);
            }
            public void menuDeselected(javax.swing.event.MenuEvent evt) {
            }
        });
        jMenu2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu2ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenu2);

        jMenuItem2.setText("Exit");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem2);

        jMenuBar1.add(jMenu1);

        jMenu4.setText("Analysis");

        jMenuItem5.setText("Paired sites comparison");
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        jMenu4.add(jMenuItem5);

        jMenuItem4.setText("Ranking");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu4.add(jMenuItem4);

        jMenuBar1.add(jMenu4);

        jMenu3.setText("Help");

        jMenuItem3.setText("About");
        jMenu3.add(jMenuItem3);

        jMenuBar1.add(jMenu3);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        projectController.saveProject();
    }//GEN-LAST:event_formWindowClosing

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        int ret = projectDialog.showOpenDialog(this);
        if (ret == JFileChooser.APPROVE_OPTION) {
            File projectFile = new File(projectDialog.getSelectedFile().getAbsoluteFile() + File.separator + "project.data");
            try {
                projectController.openProject(ProjectModel.loadProject(projectFile));
                File structureVisModelFile = new File(projectDialog.getSelectedFile().getAbsolutePath() + File.separatorChar + "structurevis.model");
                structureVisPanel.structureVisController.openStructureVisModel(StructureVisModel.loadProject(structureVisModelFile, structureVisPanel.structureVisController));
            } catch (IOException ex) {
                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    public void close() {
        projectController.saveProject();
        taskManager.stopTaskManager();
        structureVisPanel.structureVisController.structureVisModel.saveStructureVisModel(new File(structureVisPanel.structureVisController.structureVisModel.getStructureVisModelPathString(structureVisPanel.structureVisController)));
        System.exit(0);
    }

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        close();
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed

        JFrame frame = new JFrame("Structure ranking");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setIconImage(getIconImage());


        //Create and set up the content pane.
        final RankingPanel newContentPane = new RankingPanel(structureVisPanel.structureVisController);
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent we) {
                newContentPane.kill();
            }
        });

        frame.setContentPane(newContentPane);
        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
        JFrame frame = new JFrame("Structure ranking");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setIconImage(getIconImage());

        //Create and set up the content pane.
        final PairTestPanel newContentPane = new PairTestPanel(structureVisPanel.structureVisController);
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);
        frame.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent we) {
                newContentPane.kill();
            }
        });

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }//GEN-LAST:event_jMenuItem5ActionPerformed

    private void jMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem6ActionPerformed
        NewProjectDialog newProjectDialog = new NewProjectDialog(this, true);
        newProjectDialog.setIconImage(new ImageIcon(ClassLoader.getSystemResource("resources/icons/icon-32x32.png")).getImage());
        GraphicsUtils.centerWindowOnScreen(newProjectDialog);
        newProjectDialog.setVisible(true);
        if (newProjectDialog.createNewProject) {
            System.out.println(newProjectDialog.projectFile);
            createNewProject(newProjectDialog.projectFile);
        }
    }//GEN-LAST:event_jMenuItem6ActionPerformed

    private void jMenu2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu2ActionPerformed
    }//GEN-LAST:event_jMenu2ActionPerformed

    private void jMenu2MenuSelected(javax.swing.event.MenuEvent evt) {//GEN-FIRST:event_jMenu2MenuSelected
        ArrayList<File> recentlyUsedFiles = StartupDialog.getRecentlyUsedProjectFiles();
        jMenu2.removeAll();
        for (File recent : recentlyUsedFiles) {
            JMenuItem menuItem = new JMenuItem(recent.getAbsolutePath());
            menuItem.addActionListener(this);
            MenuAction openAction = new MenuAction(recent.getAbsolutePath());
            openAction.putValue("file", recent);
            menuItem.setAction(openAction);
            jMenu2.add(menuItem);
        }
    }//GEN-LAST:event_jMenu2MenuSelected

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /*
         * Set the Nimbus look and feel
         */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /*
         * If Nimbus (introduced in Java SE 6) is not available, stay with the
         * default look and feel. For details see
         * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */

        /*
         * for (javax.swing.UIManager.LookAndFeelInfo info :
         * javax.swing.UIManager.getInstalledLookAndFeels()) {
         * System.out.println(info.getName()); if
         * ("Nimbus".equals(info.getName())) {
         * //javax.swing.UIManager.setLookAndFeel(info.getClassName()); break; }
            }
         */

        try {
            //javax.swing.UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                System.out.println(info.getName());
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /*
         * Create and display the form
         */
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                MainFrame mainFrame = new MainFrame();
                GraphicsUtils.centerWindowOnScreen(mainFrame);
                mainFrame.setVisible(true);

                mainFrame.startupDialog = new StartupDialog(mainFrame, true);
                mainFrame.startupDialog.setIconImage(new ImageIcon(ClassLoader.getSystemResource("resources/icons/icon-32x32.png")).getImage());
                GraphicsUtils.centerWindowOnScreen(mainFrame.startupDialog);
                mainFrame.startupDialog.setVisible(true);

                File projectDir = mainFrame.startupDialog.getSelectedFile();
                if (projectDir != null) {
                    if (mainFrame.startupDialog.startup == Startup.OPEN) {
                        if (!projectDir.isDirectory()) {
                            projectDir = projectDir.getParentFile();
                        }
                        mainFrame.openProject(projectDir);
                    } else if (mainFrame.startupDialog.startup == Startup.CREATE_NEW) {
                        mainFrame.createNewProject(projectDir);
                    }
                }
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JTabbedPane jTabbedPane1;
    // End of variables declaration//GEN-END:variables

    @Override
    public void windowOpened(WindowEvent e) {
    }

    @Override
    public void windowClosing(WindowEvent e) {
        close();
    }

    @Override
    public void windowClosed(WindowEvent e) {
    }

    @Override
    public void windowIconified(WindowEvent e) {
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
    }

    @Override
    public void windowActivated(WindowEvent e) {
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println(e.getSource());
        if (e.getSource() instanceof JMenuItem) {
            JMenuItem menuItem = (JMenuItem) e.getSource();
            MenuAction action = (MenuAction) menuItem.getAction();
            File projectFile = (File) action.getValue("file");
            System.out.println("Opening project file " + projectFile);
            openProject(projectFile);
        }
    }
}
