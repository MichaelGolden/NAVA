package nava.ui;

/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import nava.structurevis.StructureVisModel;
import nava.utils.GraphicsUtils;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class StartupDialog extends javax.swing.JDialog {

    public static Preferences prefs = null;
    DefaultComboBoxModel<Object> recentFilesModel = new DefaultComboBoxModel<>();
    
    

    public enum Startup {

        OPEN, CREATE_NEW
    };
    Startup startup = Startup.OPEN;

    /**
     * Creates new form StartupDialog
     */
    public StartupDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        prefs = Preferences.userRoot().node(this.getClass().getName());
        recentFilesModel.removeAllElements();
        recentFilesModel.addElement("Select a recently used project");
        for (int i = 0;; i++) {
            String recent = prefs.get("recentfile" + i, null);
            if (recent != null) {
                File projectFile = new File(recent);
                if (projectFile.exists()) {
                    recentFilesModel.addElement(new File(recent));
                }
            } else {
                break;
            }
        }

        if (recentFilesModel.getSize() > 1) {
            recentFilesComboBox.setModel(recentFilesModel);
            recentFilesComboBox.setEnabled(true);
        }

        this.recentFilesComboBox.setRenderer(new ComboBoxRenderer());
        MainFrame.projectDialog.setCurrentDirectory(MainFrame.workspaceDirectory);
    }

    public static ArrayList<File> getRecentlyUsedProjectFiles() {
        ArrayList<File> recentlyUsedFiles = new ArrayList<>();
        for (int i = 0;; i++) {
            String recent = prefs.get("recentfile" + i, null);
            if (recent != null) {
                File projectFile = new File(recent);
                if (projectFile.exists()) {
                    recentlyUsedFiles.add(new File(recent));
                }
            } else {
                break;
            }
        }
        return recentlyUsedFiles;
    }

    public File getSelectedFile() {
        return selectedFile;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        recentFilesComboBox = new javax.swing.JComboBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Open a NAVA project");
        setResizable(false);

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/icons/new-project-32x32.png"))); // NOI18N
        jButton1.setText("Start a new NAVA project");
        jButton1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jButton1.setIconTextGap(10);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/icons/open-project-32x32.png"))); // NOI18N
        jButton2.setText("Open an existing NAVA project");
        jButton2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jButton2.setIconTextGap(10);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        recentFilesComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "No recent projects available." }));
        recentFilesComboBox.setEnabled(false);
        recentFilesComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                recentFilesComboBoxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 264, Short.MAX_VALUE)
                    .addComponent(recentFilesComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, 54, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, 56, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(recentFilesComboBox, javax.swing.GroupLayout.DEFAULT_SIZE, 56, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    File selectedFile = null;
    private void recentFilesComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_recentFilesComboBoxActionPerformed
        selectedFile = null;
        if (recentFilesComboBox.getSelectedItem() instanceof File) {
            selectedFile = (File) recentFilesComboBox.getSelectedItem();
            dispose();
        }
    }//GEN-LAST:event_recentFilesComboBoxActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        int ret = MainFrame.projectDialog.showOpenDialog(this);
        if (ret == JFileChooser.APPROVE_OPTION) {
            selectedFile = new File(MainFrame.projectDialog.getSelectedFile().getAbsoluteFile() + File.separator + "project.data");
            dispose();
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        NewProjectDialog newProjectDialog = new NewProjectDialog((Frame) this.getParent(), true);
        newProjectDialog.setIconImage(new ImageIcon(ClassLoader.getSystemResource("resources/icons/icon-32x32.png")).getImage());
        GraphicsUtils.centerWindowOnScreen(newProjectDialog);
        newProjectDialog.setVisible(true);
        if (newProjectDialog.createNewProject) {
            selectedFile = newProjectDialog.projectFile;
            startup = Startup.CREATE_NEW;
        }
        this.dispose();
    }//GEN-LAST:event_jButton1ActionPerformed

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
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(StartupDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(StartupDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(StartupDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(StartupDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /*
         * Create and display the dialog
         */
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                StartupDialog dialog = new StartupDialog(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {

                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }
    public static ArrayList<String> recentFiles = new ArrayList<>();

    public static void addProjectFileToRecentProjects(File inFile) {
        if (inFile.exists()) {
            recentFiles.clear();
            String filePath = (inFile.isDirectory() ? inFile : inFile.getParentFile()).getAbsolutePath();

            for (int i = 0; i < 5; i++) {
                String recentFile = StartupDialog.prefs.get("recentfile" + i, null);
                if (recentFile != null && !recentFiles.contains(recentFile)) {
                    recentFiles.add(recentFile);
                }
            }
            recentFiles.add(0, filePath);

            for (int i = 1; i < recentFiles.size(); i++) {
                if (filePath.equals(recentFiles.get(i))) {
                    recentFiles.remove(i);
                }
            }

            while (recentFiles.size() > 5) {
                recentFiles.remove(5);
            }

            if (StartupDialog.prefs != null) {
                for (int i = 0; i < recentFiles.size(); i++) {
                    StartupDialog.prefs.put("recentfile" + i, recentFiles.get(i));
                }
            }
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JComboBox recentFilesComboBox;
    // End of variables declaration//GEN-END:variables
}

class ComboBoxRenderer extends JLabel implements ListCellRenderer {
    
    public static final ImageIcon recentIcon = new ImageIcon(ClassLoader.getSystemResource("resources/icons/recent-project-icon-32x32.png"));
    public static final ImageIcon centeredIcon = new ImageIcon(ClassLoader.getSystemResource("resources/icons/centered-icon-32x32.png"));

    public ComboBoxRenderer() {
        setOpaque(true);
        //this.set
        setBorder(new EmptyBorder(2, 13, 2, 2));
        //this.setHorizontalTextPosition(SwingConstants.RIGHT);
        setHorizontalAlignment(LEFT);
        //s/etVerticalAlignment(CENTER);
    }
    String tooltip;

    /*
     * This method finds the image and text corresponding to the selected value
     * and returns the label, set up to display the text and image.
     */
    @Override
    public Component getListCellRendererComponent(
            JList list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus) {
        //Get the selected index. (The index param isn't
        //always valid, so just use the value.)
        int selectedIndex = index;

        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }
        if (value instanceof File) {            
            setIcon(centeredIcon);            
            this.setIconTextGap(10);
            File file = ((File) value);
            file = file.isDirectory() ? file : file.getParentFile();
            setText(file.getName());            
            tooltip = file.getAbsolutePath();
        } else {
            setIcon(recentIcon);
            this.setIconTextGap(10);
            tooltip = value.toString();
            setText(value.toString());
        }
        // setToolTipText(value.toString());
        return this;
    }

    @Override
    public String getToolTipText(MouseEvent e) {
        return tooltip;
    }
}