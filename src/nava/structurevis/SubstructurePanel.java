/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.structurevis;

import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import javax.swing.DefaultComboBoxModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import nava.structurevis.data.*;
import nava.structurevis.navigator.DataOverlayTreePanel;
import nava.ui.MainFrame;
import nava.ui.ProjectController;
import nava.utils.CustomItem;
import nava.utils.GraphicsUtils;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class SubstructurePanel extends javax.swing.JPanel implements ChangeListener, ItemListener, ListDataListener, SubstructureModelListener, StructureVisView {

    DefaultComboBoxModel<StructureOverlay> structureComboBoxModel = new DefaultComboBoxModel<>();
    DefaultComboBoxModel<CustomItem> substructureComboBoxModel = new DefaultComboBoxModel<>();
    StructureVisController structureVisController;
    ProjectController projectController;
    public SubstructureDrawPanel structureDrawPanel;
    public FullGenomeDrawPanel fullGenomeDrawPanel;
    DataLegend dataLegend1D = new DataLegend();
    DataLegend dataLegend2D = new DataLegend();

    /**
     * Creates new form StructurePanel
     */
    public SubstructurePanel(StructureVisController structureVisController, ProjectController projectController) {
        initComponents();
        this.structureVisController = structureVisController;
        this.projectController = projectController;

        structureDrawPanel = new SubstructureDrawPanel(structureVisController.structureVisModel.substructureModel);
        fullGenomeDrawPanel = new FullGenomeDrawPanel(structureVisController);
        
        structureVisController.structureVisModel.substructureModel.addSubstructureModelListener(this);
        topScrollPane.setViewportView(structureDrawPanel);

        structureComboBox.setModel(structureComboBoxModel);
        structureComboBox.addItemListener(this);

        substructureComboBox.setModel(substructureComboBoxModel);
        substructureComboBox.addItemListener(this);

        structureVisController.addView(this);

        treePanel.add(new DataOverlayTreePanel(projectController, structureVisController), BorderLayout.CENTER);

        dataLegend1D.addChangeListener(this);
        dataLegend2D.addChangeListener(this);
        legendPanel.add(dataLegend1D);
        legendPanel.add(dataLegend2D);

        distanceSlider.addChangeListener(this);
        //jProgressBar1.
         MainFrame.progressBarMonitor.addJProgressBar(jProgressBar);
        //populateStructureComboBox(Collections.list(projectController.projectModel.dataSources.elements()));
    }

    public void refresh() {
        this.populateStructureSourceComboBox();
        this.populateSubtructureComboBox();
        // TODO remember which structures/substructures were selected previously
    }

    /*
     * public void populateStructureComboBox(List<DataSource> dataSources) {
     * structureComboBoxModel.removeAllElements();
     * //mappingSourceComboBoxModel.removeAllElements(); for (DataSource
     * dataSource : dataSources) { if (dataSource instanceof SecondaryStructure)
     * { structureComboBoxModel.addElement((SecondaryStructure) dataSource); }
     * if (dataSource instanceof Alignment) { //
     * mappingSourceComboBoxModel.addElement((Alignment)dataSource); } } }
     */
    public void populateSubtructureComboBox() {
        substructureComboBoxModel.removeAllElements();
        ArrayList<Substructure> list = structureVisController.structureVisModel.substructureModel.getSubstructures();
        for (int i = 0; i < list.size(); i++) {
            CustomItem<Substructure> item = new CustomItem<>(list.get(i), i + "");
            substructureComboBoxModel.addElement(item);
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

        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel4 = new javax.swing.JPanel();
        topScrollPane = new javax.swing.JScrollPane();
        legendPanel = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        treePanel = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        structureComboBox = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        substructureComboBox = new javax.swing.JComboBox();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(15, 0), new java.awt.Dimension(15, 0), new java.awt.Dimension(15, 32767));
        jLabel3 = new javax.swing.JLabel();
        distanceSlider = new javax.swing.JSlider();
        distanceLabel = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 32767));
        jProgressBar = new javax.swing.JProgressBar();
        viewToggleButton = new javax.swing.JToggleButton();

        jSplitPane1.setDividerLocation(150);

        jPanel4.setLayout(new javax.swing.BoxLayout(jPanel4, javax.swing.BoxLayout.LINE_AXIS));

        topScrollPane.setMinimumSize(new java.awt.Dimension(40, 23));
        topScrollPane.setPreferredSize(new java.awt.Dimension(500, 2));
        jPanel4.add(topScrollPane);

        legendPanel.setMaximumSize(new java.awt.Dimension(300, 32767));
        legendPanel.setPreferredSize(new java.awt.Dimension(150, 289));
        legendPanel.setLayout(new javax.swing.BoxLayout(legendPanel, javax.swing.BoxLayout.PAGE_AXIS));
        jPanel4.add(legendPanel);

        jSplitPane1.setRightComponent(jPanel4);

        jPanel1.setPreferredSize(new java.awt.Dimension(200, 319));

        treePanel.setLayout(new java.awt.BorderLayout());

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/icons/add-16x16.png"))); // NOI18N
        jButton2.setText("Add data overlay");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton2)
                .addGap(10, 10, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(treePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(treePanel, javax.swing.GroupLayout.DEFAULT_SIZE, 297, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2))
        );

        jSplitPane1.setLeftComponent(jPanel1);

        jPanel2.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 3, 1));

        jLabel1.setText("Structure");

        structureComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel2.setText("Substructure");

        substructureComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel3.setText("Limit nearby 2D interactions");

        distanceSlider.setPaintLabels(true);
        distanceSlider.setValue(100);

        distanceLabel.setText("No limit");

        jLabel4.setText("Status");

        jProgressBar.setPreferredSize(new java.awt.Dimension(146, 20));

        viewToggleButton.setText("Full view");
        viewToggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewToggleButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(structureComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(substructureComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(40, 40, 40)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(distanceSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(distanceLabel)
                .addGap(97, 97, 97)
                .addComponent(filler1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 222, Short.MAX_VALUE)
                .addComponent(filler3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(viewToggleButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jProgressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(structureComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel1)
                                        .addComponent(jLabel2)
                                        .addComponent(substructureComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel3))
                                    .addComponent(distanceSlider, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(16, 16, 16)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(filler1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(filler3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jProgressBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(viewToggleButton)
                                .addComponent(distanceLabel)))))
                .addGap(0, 0, 0))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jSplitPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 330, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        AddDataOverlayDialog dialog = new AddDataOverlayDialog(null, true, projectController, structureVisController);
        GraphicsUtils.centerWindowOnWindow(dialog, MainFrame.self);
        dialog.setVisible(true);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void viewToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewToggleButtonActionPerformed
        if(viewToggleButton.isSelected())
        {
            this.topScrollPane.setViewportView(fullGenomeDrawPanel);
            this.viewToggleButton.setText("Substructure view");
        }
        else
        {
            this.topScrollPane.setViewportView(structureDrawPanel);
            this.viewToggleButton.setText("Full view");
        }
    }//GEN-LAST:event_viewToggleButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel distanceLabel;
    private javax.swing.JSlider distanceSlider;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler3;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JProgressBar jProgressBar;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JPanel legendPanel;
    private javax.swing.JComboBox structureComboBox;
    private javax.swing.JComboBox substructureComboBox;
    private javax.swing.JScrollPane topScrollPane;
    private javax.swing.JPanel treePanel;
    private javax.swing.JToggleButton viewToggleButton;
    // End of variables declaration//GEN-END:variables

    @Override
    public void itemStateChanged(ItemEvent e) {

        System.out.println(e.getSource());
        if (e.getSource().equals(structureComboBox)) {
            StructureOverlay structureSource = (StructureOverlay) structureComboBox.getSelectedItem();

            if (structureSource != null && structureSource.mappingSource != null) {
                structureVisController.structureVisModel.substructureModel.setStructureOverlay(structureSource);
            }

            /*
             * if (structureSource != null && structureSource.mappingSource !=
             * null) {
             * //structureVisController.addStructureSource(structureSource);
             * structureSource.loadData();
             * structureVisController.substructureModel.setStructureSource(structureSource);
             * if (structureSource.substructures.size() > 0) {
             * structureDrawPanel.openSubstructure(structureSource.substructures.get(0));
             * } populateSubtructureComboBox(); }
             */
        } else if (e.getSource().equals(substructureComboBox)) {
            CustomItem<Substructure> comboBoxItem = (CustomItem<Substructure>) substructureComboBoxModel.getSelectedItem();
            if (comboBoxItem != null) {
                structureDrawPanel.openSubstructure(comboBoxItem.getObject());
            }
        }
    }

    public void populateStructureSourceComboBox() {
        structureComboBoxModel.removeAllElements();
        ArrayList<StructureOverlay> list = structureVisController.structureVisModel.structureSources.getArrayListShallowCopy();
        for (int i = 0; i < list.size(); i++) {
            //ComboBoxItem<StructureSource> item = new ComboBoxItem<>(list.get(i), list.get(i).structure.toString());
            structureComboBoxModel.addElement(list.get(i));
        }
    }

    @Override
    public void intervalAdded(ListDataEvent e) {
        //populateStructureSourceComboBox();
    }

    @Override
    public void intervalRemoved(ListDataEvent e) {
        // populateStructureSourceComboBox();
    }

    @Override
    public void contentsChanged(ListDataEvent e) {
        // populateStructureSourceComboBox();
    }

    @Override
    public void dataSource1DChanged(DataOverlay1D dataSource1D) {
        if (dataSource1D == null) {
            dataLegend1D.setVisible(false);
        } else {
            dataLegend1D.setVisible(true);
            dataLegend1D.setLegend(dataSource1D.title, dataSource1D.dataTransform, dataSource1D.colorGradient, dataSource1D.defaultColorGradient, dataSource1D.useLowerThreshold, dataSource1D.useUpperThreshold, dataSource1D.thresholdMinPerc, dataSource1D.thresholdMaxPerc, dataSource1D);
        }
        structureDrawPanel.redraw();
        fullGenomeDrawPanel.redraw();
    }

    @Override
    public void dataSource2DChanged(DataOverlay2D dataSource2D) {
        if (dataSource2D == null) {
            dataLegend2D.setVisible(false);
        } else {
            dataLegend2D.setVisible(true);
            dataLegend2D.setLegend(dataSource2D.title, dataSource2D.dataTransform, dataSource2D.colorGradient, dataSource2D.defaultColorGradient, dataSource2D.useLowerThreshold, dataSource2D.useUpperThreshold, dataSource2D.thresholdMinPerc, dataSource2D.thresholdMaxPerc, dataSource2D);
        }

        structureDrawPanel.redraw();
        fullGenomeDrawPanel.redraw();
    }

    @Override
    public void structureSourceChanged(StructureOverlay structureSource) {
        if (structureSource != null && structureSource.mappingSource != null) {
            //structureVisController.addStructureSource(structureSource);
            structureSource.loadData();
            //System.out.println("structureSource.substructures.size() = "+structureSource.substructures.size());
            if (structureSource.substructureList.substructures.size() > 0) {
                structureDrawPanel.openSubstructure(structureSource.substructureList.substructures.get(0));
            } else {
                structureDrawPanel.openSubstructure(null);
            }
            populateSubtructureComboBox();
        } else {
            structureDrawPanel.openSubstructure(null);
        }
        DataOverlay1D dataOverlay1D = structureVisController.structureVisModel.substructureModel.data1D;
        if (dataOverlay1D != null) {
            this.dataLegend1D.setLegend(dataOverlay1D.title, dataOverlay1D.dataTransform, dataOverlay1D.colorGradient, dataOverlay1D.defaultColorGradient, dataOverlay1D.useLowerThreshold, dataOverlay1D.useUpperThreshold, dataOverlay1D.thresholdMinPerc, dataOverlay1D.thresholdMaxPerc, dataOverlay1D);
        }

        DataOverlay2D dataOverlay2D = structureVisController.structureVisModel.substructureModel.data2D;
        if (dataOverlay2D != null) {
            this.dataLegend2D.setLegend(dataOverlay2D.title, dataOverlay2D.dataTransform, dataOverlay2D.colorGradient, dataOverlay2D.defaultColorGradient, dataOverlay2D.useLowerThreshold, dataOverlay2D.useUpperThreshold, dataOverlay2D.thresholdMinPerc, dataOverlay2D.thresholdMaxPerc, dataOverlay2D);
        }
        
        structureDrawPanel.redraw();
        fullGenomeDrawPanel.initialise(structureVisController.structureVisModel.substructureModel.structureOverlay, fullGenomeDrawPanel.maxSubstructureSize);
        fullGenomeDrawPanel.redraw();
    }

    @Override
    public void annotationSourceChanged(AnnotationSource annotationSource) {
        structureDrawPanel.redraw();
        fullGenomeDrawPanel.redraw();
    }

    @Override
    public void nucleotideSourceChanged(NucleotideComposition nucleotideSource) {
        structureDrawPanel.redraw();
        fullGenomeDrawPanel.redraw();
    }

    public void setDistanceLimit(int value) {
        if(!(distanceSlider.getValue() == value || value == -1 && distanceSlider.getValue() == distanceSlider.getMaximum()))
        {
             distanceSlider.setValue(value == -1 ? distanceSlider.getMaximum() : value);
        }
        
        if (value == distanceSlider.getMaximum() || value == -1) {
            structureVisController.structureVisModel.substructureModel.maxDistance = -1;
            distanceLabel.setText("No limit");
        } else {
            structureVisController.structureVisModel.substructureModel.maxDistance = value;
            distanceLabel.setText(value + "");
            structureDrawPanel.redraw();
        }
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        if (e.getSource().equals(distanceSlider)) {
            setDistanceLimit(distanceSlider.getValue());
        }
        this.structureDrawPanel.redraw();
    }

    @Override
    public void dataOverlayAdded(Overlay overlay) {
        populateStructureSourceComboBox();
    }

    @Override
    public void dataOverlayRemoved(Overlay overlay) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void dataOverlayChanged(Overlay oldOverlay, Overlay newOverlay) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void structureVisModelChanged(StructureVisModel newStructureVisModel) {
        structureVisController.structureVisModel.substructureModel.removeSubstructureModelListener(this);
        structureVisController.structureVisModel = newStructureVisModel;
        structureDrawPanel.setModel(newStructureVisModel.substructureModel);
        structureVisController.structureVisModel.substructureModel.addSubstructureModelListener(this);
        setDistanceLimit(structureVisController.structureVisModel.substructureModel.maxDistance);
        if (newStructureVisModel.substructureModel.data1D == null) {
            dataLegend1D.setVisible(false);
        }
        if (newStructureVisModel.substructureModel.data2D == null) {
            dataLegend2D.setVisible(false);
        }
        refresh();
    }
}
