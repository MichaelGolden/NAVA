/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import nava.tasks.applications.Application;
import nava.tasks.applications.PosteriorDecodingApplication;
import nava.tasks.applications.ApplicationController;
import nava.tasks.applications.RNAalifoldApplication;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import nava.data.types.DataSource;
import nava.tasks.applications.*;

/**
 *
 * @author Michael
 */
public class ApplicationPanel extends javax.swing.JPanel implements ListSelectionListener {

    ApplicationController appController;
    ProjectController projectController;
    ArrayList<Application> applications;
    DefaultListModel<Application> applicationListModel;
    //DefaultListModel<ApplicationListObject> applicationListModel;
    List<DataSource> selectedDataSources;

    /**
     * Creates new form ApplicationPanel
     */
    public ApplicationPanel(ApplicationController appController, ProjectController projectController) {
        initComponents();
        this.appController = appController;
        this.projectController = projectController;

        appController.registerApplication(new RNAalifoldApplication());
        appController.registerApplication(new PosteriorDecodingApplication());
        appController.registerApplication(new MAFFTApplication());
        appController.registerApplication(new MuscleApplication());
        appController.registerApplication(new ClustalWApplication());
        applications = appController.getApplications();

        applicationListModel = new DefaultListModel<>();
        jList1.setModel(applicationListModel);
        jList1.setCellRenderer(new ApplicationCellRenderer());
        jList1.addListSelectionListener(this);
        
        runButton.setEnabled(false);

    }

    public void showUsableApplications(List<DataSource> dataSources) {
        selectedDataSources = dataSources;
        if (dataSources.size() == 0) {
            applicationListModel.clear();
        } else if (dataSources.size() == 1) {
            applicationListModel.clear();
            for (int i = 0; i < applications.size(); i++) {
                if (applications.get(i).canProcessDataSources(dataSources)) {
                    //applicationListModel.addElement(new ApplicationListObject(applications.get(i)));
                    applicationListModel.addElement(applications.get(i));
                }
            }
            for (int i = 0; i < applications.size(); i++) {
                if (!applications.get(i).canProcessDataSources(dataSources)) {
                    //applicationListModel.addElement(new ApplicationListObject(applications.get(i)));
                    applicationListModel.addElement(applications.get(i));
                }
            }
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        Application app = (Application) jList1.getSelectedValue();
        if(app.canProcessDataSources(selectedDataSources))
        {
            runButton.setEnabled(true);
        }        
        else
        {
            runButton.setEnabled(false);
        }
    }

    class ApplicationCellRenderer extends JPanel
            implements ListCellRenderer {

        public ApplicationCellRenderer() {
            setOpaque(true);
            setLayout(new BorderLayout());
        }

        /*
         * This method finds the image and text corresponding to the selected
         * value and returns the label, set up to display the text and image.
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
            Application app = (Application) value;
            boolean useable = app.canProcessDataSources(selectedDataSources);
            Color useableSelectedColor = new Color(190,255,190);
            Color useableUnselectedColor = new Color(230,255,230);
            
            Color unusuableSelectedColor = new Color(255, 200, 225);
            Color unusueableUnselectedColor = new Color(255, 220, 245);
            ApplicationPanelItem item = new ApplicationPanelItem(app.getName(), null, app.getDescription());
            if (isSelected) {
                if (useable) {
                    item.setBackground(useableSelectedColor);
                } else {
                    item.setBackground(unusuableSelectedColor);
                }
                item.setForeground(list.getSelectionForeground());
            } else {
                if (useable) {
                    item.setBackground(useableUnselectedColor);
                } else {
                    item.setBackground(unusueableUnselectedColor);
                }
                item.setForeground(list.getForeground());
            }


            return item;
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
        runButton = new javax.swing.JButton();

        setLayout(new java.awt.BorderLayout());

        jList1.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(jList1);

        add(jScrollPane1, java.awt.BorderLayout.CENTER);

        runButton.setText("Run");
        runButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                runButtonActionPerformed(evt);
            }
        });
        add(runButton, java.awt.BorderLayout.SOUTH);
    }// </editor-fold>//GEN-END:initComponents

    private void runButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_runButtonActionPerformed
        if (jList1.getSelectedValue() != null) {
            try {
                Application app = (Application) jList1.getSelectedValue().getClass().newInstance();
                app.setDataSource(selectedDataSources.get(0));
                MainFrame.taskManager.queueTask(app);
            } catch (InstantiationException ex) {
                Logger.getLogger(ApplicationPanel.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(ApplicationPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        /*
         * ApplicationListObject applicationObject = (ApplicationListObject)
         * jList1.getSelectedValue(); if (applicationObject != null) { try {
         * Application app =
         * applicationObject.application.getClass().newInstance();
         * app.setDataSource(selectedDataSources.get(0));
         * MainFrame.taskManager.queueTask(app); } catch (InstantiationException
         * ex) {
         * Logger.getLogger(ApplicationPanel.class.getName()).log(Level.SEVERE,
         * null, ex); } catch (IllegalAccessException ex) {
         * Logger.getLogger(ApplicationPanel.class.getName()).log(Level.SEVERE,
         * null, ex); }
        }
         */
    }//GEN-LAST:event_runButtonActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JList jList1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton runButton;
    // End of variables declaration//GEN-END:variables
}
