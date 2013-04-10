/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.ui;

import java.awt.Desktop;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import nava.data.io.DataExport;
import nava.data.io.DataExport.ExportableFormat;
import nava.data.types.DataSource;
import nava.utils.CustomJCheckBoxItem;
import nava.utils.GraphicsUtils;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class ExportDataDialog extends javax.swing.JDialog implements ItemListener {

    ArrayList<DataSource> selectedDataSources = new ArrayList<>();
    DataExport dataExport = new DataExport();
    ArrayList<DataExport.ExportableFormat> exportableFormats = new ArrayList<>();
    ArrayList<CustomJCheckBoxItem> exportCheckBoxes = new ArrayList<>();
    String defaultExportPrefix = "export";
    ProjectController projectController;

    /**
     * Creates new form ExportDataDialog
     */
    public ExportDataDialog(java.awt.Frame parent, boolean modal, ProjectController projectController, DataSource dataSource) {
        super(parent, modal);
        initComponents();
        
        this.projectController = projectController;
        updatePanel(dataSource);
        exportButton.setEnabled(false);
    }

    public void updatePanel(DataSource dataSource) {
        selectedDataSources.clear();
        selectedDataSources.add(dataSource);
        exportableFormats = dataExport.getExportableFormats(dataSource);

        exportPanel.removeAll();
        exportCheckBoxes.clear();
        if (exportableFormats.size() > 0) {
            for (ExportableFormat exportableFormat : exportableFormats) {
                CustomJCheckBoxItem<ExportableFormat> checkbox = new CustomJCheckBoxItem(exportableFormat.exportFormat.toString() + " (." + exportableFormat.exportFormat.getExtension() + ")");
                checkbox.addItemListener(this);
                checkbox.setObject(exportableFormat);
                exportPanel.add(checkbox);
                exportCheckBoxes.add(checkbox);
                browseField.setText(System.getProperty("user.dir") + File.separator + exportableFormat.groupName);
            }
        } else {
            exportPanel.add(new JLabel("No export options are available for this item."));
            exportButton.setEnabled(false);
        }
        exportPanel.revalidate();
        exportPanel.repaint();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel2 = new javax.swing.JLabel();
        exportPanel = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        browseField = new javax.swing.JTextField();
        openFolderButton = new javax.swing.JButton();
        titleAsNameButton = new javax.swing.JButton();
        exportButton = new javax.swing.JButton();
        browseButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Data export");

        jLabel2.setText("Select the format(s) you would like to export to:");

        exportPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jLabel5.setText("Files are exported with the specified extension.");

        jLabel4.setText("Export prefix");

        openFolderButton.setText("Open folder");
        openFolderButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openFolderButtonActionPerformed(evt);
            }
        });

        titleAsNameButton.setText("Use title as name");
        titleAsNameButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                titleAsNameButtonActionPerformed(evt);
            }
        });

        exportButton.setText("Export");
        exportButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportButtonActionPerformed(evt);
            }
        });

        browseButton.setText("Browse...");
        browseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(78, 78, 78))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(openFolderButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(titleAsNameButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(exportButton, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(browseField, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(browseButton)))
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(exportPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 362, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5))
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(exportPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(browseButton)
                    .addComponent(browseField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(exportButton)
                    .addComponent(titleAsNameButton)
                    .addComponent(openFolderButton))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    static int id = 0;
    private void exportButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportButtonActionPerformed

        final String className = id + "_" + System.currentTimeMillis();
        id++;
        final ExportConsoleDialog exportConsoleDialog = new ExportConsoleDialog(null, true, className, "out");
        exportConsoleDialog.setTitle("Export output");
        exportConsoleDialog.setSize(640, 480);
        exportConsoleDialog.closeButton.setEnabled(false);

        new Thread() {

            public void run() {
                File file = new File(browseField.getText());
                File directory = file;
                if (!file.isDirectory()) {
                    directory = file.getParentFile();
                } else {
                    browseField.setText(browseField.getText() + defaultExportPrefix);
                }
                if (!directory.exists() && directory.mkdir()) {
                    JOptionPane.showMessageDialog(ExportDataDialog.this, "Export failed, the specified directory does not exist.", "Export failed", JOptionPane.WARNING_MESSAGE);
                } else {
                    for (CustomJCheckBoxItem<ExportableFormat> checkbox : exportCheckBoxes) {
                        ExportableFormat f = checkbox.getObject();
                        for (DataSource dataSource : selectedDataSources) {
                            if (checkbox.isSelected()) {
                                File outFile = new File(browseField.getText() + "." + f.exportFormat.getExtension());
                                int i = 2;
                                while (outFile.exists()) {
                                    outFile = new File(browseField.getText() + "_" + i + "." + f.exportFormat.getExtension());
                                    i++;
                                }
                                exportConsoleDialog.consoleBuffer.bufferedWrite("Exporting " + dataSource.getTypeName().toLowerCase() + " '" + dataSource.getTitle() + "' to path: '" + outFile.getAbsolutePath() + "' in '" + f.exportFormat.toString() + "' format.", className, "out");
                                try {
                                    dataExport.export(dataSource, f.exportFormat, outFile);
                                    exportConsoleDialog.consoleBuffer.bufferedWrite("Completed successfully.", className, "out", true);
                                    exportConsoleDialog.consoleBuffer.bufferedWrite("", className, "out", true);
                                } catch (Exception ex) {
                                    exportConsoleDialog.consoleBuffer.bufferedWrite("Failed.", className, "out", true);
                                    exportConsoleDialog.consoleBuffer.bufferedWrite("", className, "out", true);
                                    Logger.getLogger(ExportDataDialog.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        }
                    }
                }

                exportConsoleDialog.closeButton.setEnabled(true);
            }
        }.start();

        GraphicsUtils.centerWindowOnWindow(exportConsoleDialog, this);
        this.dispose();
        exportConsoleDialog.setVisible(true);
    }//GEN-LAST:event_exportButtonActionPerformed

    private void browseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseButtonActionPerformed
        MainFrame.browseDialog.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        File currentFile = new File(browseField.getText());
        MainFrame.browseDialog.setSelectedFile(currentFile);
        String suffix = currentFile.getName();
        if (currentFile.isDirectory()) {
            suffix = "export";
            for (CustomJCheckBoxItem<ExportableFormat> checkbox : exportCheckBoxes) {
                ExportableFormat f = checkbox.getObject();
                suffix = f.groupName;
                break;
            }
        }
        int ret = MainFrame.browseDialog.showDialog(this, "Select folder");
        if (ret == JFileChooser.APPROVE_OPTION) {
            File selectedFile = MainFrame.browseDialog.getSelectedFile();
            browseField.setText(selectedFile.getAbsolutePath() + File.separatorChar + suffix);
            browseField.setSelectionStart(browseField.getText().length() - suffix.length());
            browseField.setSelectionEnd(browseField.getText().length());
        }
    }//GEN-LAST:event_browseButtonActionPerformed

    private void titleAsNameButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_titleAsNameButtonActionPerformed
        if (selectedDataSources.size() == 1) {
            File file = new File(browseField.getText());
            File directory = file;
            if (!file.isDirectory()) {
                directory = file.getParentFile();
            }

            browseField.setText(directory.getAbsolutePath() + File.separator + selectedDataSources.get(0));
        }
    }//GEN-LAST:event_titleAsNameButtonActionPerformed

    private void openFolderButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openFolderButtonActionPerformed
        File folder = new File(browseField.getText()); // path to the directory to be opened
        folder = (folder.isDirectory() ? folder : folder.getParentFile());
        Desktop desktop = null;
        if (Desktop.isDesktopSupported()) {
            desktop = Desktop.getDesktop();
            try {
                if (folder.exists()) {
                    desktop.open(folder);
                } else {
                    // TODO error dialog
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }//GEN-LAST:event_openFolderButtonActionPerformed

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
            java.util.logging.Logger.getLogger(ExportDataDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ExportDataDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ExportDataDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ExportDataDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /*
         * Create and display the dialog
         */
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                ExportDataDialog dialog = new ExportDataDialog(new javax.swing.JFrame(), true, null, null);
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
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton browseButton;
    private javax.swing.JTextField browseField;
    private javax.swing.JButton exportButton;
    private javax.swing.JPanel exportPanel;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JButton openFolderButton;
    private javax.swing.JButton titleAsNameButton;
    // End of variables declaration//GEN-END:variables

    @Override
    public void itemStateChanged(ItemEvent e) {
        int sel = 0;
        for (CustomJCheckBoxItem<ExportableFormat> checkbox : exportCheckBoxes) {
            if (checkbox.isSelected()) {
                sel++;
            }
        }
        if (sel == 0) {
            exportButton.setEnabled(false);
        } else {
            exportButton.setEnabled(true);
        }
    }
}
