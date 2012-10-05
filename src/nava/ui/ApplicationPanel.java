/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import nava.analyses.*;
import nava.data.types.DataSource;
import nava.ui.navigator.NavigationListener;

/**
 *
 * @author Michael
 */
public class ApplicationPanel extends javax.swing.JPanel {

    ApplicationController appController;
    ProjectController projectController;
    ArrayList<Application> applications;
    DefaultListModel<ApplicationListObject> applicationListModel;
    List<DataSource> selectedDataSources;

    /**
     * Creates new form ApplicationPanel
     */
    public ApplicationPanel(ApplicationController appController, ProjectController projectController) {
        initComponents();
        this.appController = appController;
        this.projectController = projectController;

        appController.registerApplication(new RNAalifold());
        appController.registerApplication(new PosteriorDecoding());
        applications = appController.getApplications();

        applicationListModel = new DefaultListModel<>();
        jList1.setModel(applicationListModel);

    }

    public void showUsableApplications(List<DataSource> dataSources) {
        selectedDataSources = dataSources;
        if (dataSources.size() == 0) {
            applicationListModel.clear();
        } else if (dataSources.size() == 1) {
            applicationListModel.clear();
            for (int i = 0; i < applications.size(); i++) {
                if (applications.get(i).canProcessDataSource(dataSources.get(0))) {
                    applicationListModel.addElement(new ApplicationListObject(applications.get(i)));
                }
            }
        }
    }

    public class ApplicationListObject {

        Application application;

        public ApplicationListObject(Application application) {
            this.application = application;
        }

        @Override
        public String toString() {
            return application.getName();
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

        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        jButton1 = new javax.swing.JButton();

        setLayout(new java.awt.BorderLayout());

        jList1.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(jList1);

        add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jButton1.setText("Run");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        add(jButton1, java.awt.BorderLayout.SOUTH);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        ApplicationListObject applicationObject = (ApplicationListObject) jList1.getSelectedValue();
        if (applicationObject != null) {
            try {
                Application app = applicationObject.application.getClass().newInstance();
                app.setDataSource(selectedDataSources.get(0));
                app.start();
                app.getOutputFiles();
                List<ApplicationOutput> output = app.getOutputFiles();
                for (int i = 0; i < output.size(); i++) {
                    projectController.importDataSourceFromOutputFile(output.get(i));
                }
            } catch (InstantiationException ex) {
                Logger.getLogger(ApplicationPanel.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(ApplicationPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_jButton1ActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JList jList1;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
