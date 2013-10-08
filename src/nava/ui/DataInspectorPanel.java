/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.ui;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import javax.swing.event.ListDataEvent;
import nava.data.types.Alignment;
import nava.data.types.DataSource;
import nava.ui.navigator.NavigatorTreeNode;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class DataInspectorPanel extends javax.swing.JPanel implements ItemListener, ProjectView {

    ArrayList<DataSource> selectedDataSources = new ArrayList<>();
    ProjectController projectController;
    //DataInspectorAlignmentPanel alignmentPanel = new DataInspectorAlignmentPanel();

    /**
     * Creates new form DataInspectorPanel
     */
    public DataInspectorPanel(ProjectController projectController) {
        initComponents();
        this.projectController = projectController;
       // WrapLayout wrapLayout = new WrapLayout(WrapLayout.LEFT);
        //wrapLayout.setHgap(8);
        //exportPanel.setLayout(wrapLayout);
        projectController.addView(this);
        editTitle();
    }

    public void updatePanel(DataSource dataSource) {
        titleField.setEnabled(false);
        editTitleButton.setSelected(false);
        selectedDataSources.clear();
        selectedDataSources.add(dataSource);
        dataTypeLabel.setIcon(dataSource.getIcon());
        dataTypeLabel.setText(dataSource.getTypeName());
        titleField.setText(dataSource.title);
        
        
        if(dataSource instanceof Alignment)
        {
            this.holderPanel.removeAll();
            this.holderPanel.add(new DataInspectorAlignmentPanel(projectController, (Alignment)dataSource));
            this.holderPanel.revalidate();
        }
        else
        {
           this.holderPanel.removeAll();
        }
       // exportPanel.revalidate();
        //exportPanel.repaint();
        //jPanel2.revalidate();
        //jPanel2.repaint();
        editTitle();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        dataTypeLabel = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        titleField = new javax.swing.JTextField();
        editTitleButton = new javax.swing.JToggleButton();
        holderPanel = new javax.swing.JPanel();

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Properties"));

        jLabel1.setText("Data type");

        dataTypeLabel.setText("Icon and data type");
        dataTypeLabel.setMaximumSize(new java.awt.Dimension(34, 16));
        dataTypeLabel.setMinimumSize(new java.awt.Dimension(34, 16));
        dataTypeLabel.setPreferredSize(new java.awt.Dimension(34, 16));

        jLabel3.setText("Title");

        editTitleButton.setText("Edit");
        editTitleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editTitleButtonActionPerformed(evt);
            }
        });

        holderPanel.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel3))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(dataTypeLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(titleField, javax.swing.GroupLayout.DEFAULT_SIZE, 326, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(editTitleButton)))
                .addContainerGap())
            .addComponent(holderPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(dataTypeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(titleField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(editTitleButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(holderPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 216, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    static int id = 0;
    public void editTitle() {
        editTitleButton.setEnabled(true);
        if (selectedDataSources.size() == 1) {
            if (editTitleButton.isSelected()) {
                titleField.setEnabled(true);
                editTitleButton.setText("Save");
            } else {
                // selectedDataSources.get(0).s
                titleField.setEnabled(false);
                editTitleButton.setText("Edit");
                DataSource dataSource = selectedDataSources.get(0);
                dataSource.title = titleField.getText();
                projectController.projectModel.navigatorTreeModel.nodeChanged(projectController.projectModel.navigatorTreeModel.findNode(dataSource));
            }
        } else {
            titleField.setEnabled(false);
            editTitleButton.setText("Edit");
            //editTitleButton.setEnabled(false);
        }
    }

    private void editTitleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editTitleButtonActionPerformed
        editTitle();
    }//GEN-LAST:event_editTitleButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel dataTypeLabel;
    private javax.swing.JToggleButton editTitleButton;
    private javax.swing.JPanel holderPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField titleField;
    // End of variables declaration//GEN-END:variables

    @Override
    public void itemStateChanged(ItemEvent e) {
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
        
        for(DataSource d : selectedDataSources)
        {
             this.updatePanel(d);
        }
    }
}
