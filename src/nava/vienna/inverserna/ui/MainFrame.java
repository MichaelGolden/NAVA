/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.vienna.inverserna.ui;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import nava.utils.GraphicsUtils;
import nava.utils.Pair;
import nava.utils.RNAFoldingTools;
import nava.vienna.inverserna.TargetStructure;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class MainFrame extends javax.swing.JFrame implements ItemListener {

    DefaultListModel<TargetStructure> targetListModel = new DefaultListModel<>();
    ArrayList<String> criteria = new ArrayList<>();
    
    DefaultComboBoxModel xComboBoxModel = new DefaultComboBoxModel<>();
    DefaultComboBoxModel yComboBoxModel = new DefaultComboBoxModel<>();
    //final DefaultListModel<JCheckBox> criteriaListModel = new DefaultListModel<>();
    //CheckBoxList checkBoxList = new CheckBoxList();
    /**
     * Creates new form MainFrame
     */
    UpdatePlot updatePlot = new UpdatePlot();
    
    public MainFrame() {
        initComponents();
        
        targetListModel.addElement(new TargetStructure("target1",RNAFoldingTools.getPairedSitesFromDotBracketString(".....(((((.............))))).........(((((((.....)).))..)))...........((((..........))))....(((.(((.....(((......)))......((((((................)))))).))))))..."), 37.0));
        
        this.targetList.setModel(targetListModel);
        
        this.xComboBox.addItemListener(this);
        this.xComboBox.setModel(xComboBoxModel);
        this.yComboBox.addItemListener(this);
        this.yComboBox.setModel(yComboBoxModel);
        
        
        (new Thread(updatePlot)).start();
        
        xComboBox.setWide(true);
        yComboBox.setWide(true);
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
        jScrollPane1 = new javax.swing.JScrollPane();
        targetList = new javax.swing.JList();
        addTargetButton = new javax.swing.JButton();
        searchButton = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        scatterPlot1 = new nava.vienna.inverserna.ui.ScatterPlot();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        xComboBox = new nava.utils.WiderDropDownComboBox();
        jLabel2 = new javax.swing.JLabel();
        yComboBox = new nava.utils.WiderDropDownComboBox();
        jCheckBox1 = new javax.swing.JCheckBox();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Targets"));

        jScrollPane1.setBorder(null);

        targetList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(targetList);

        addTargetButton.setText("Add target");
        addTargetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addTargetButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(addTargetButton)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(addTargetButton))
        );

        searchButton.setText("Search");
        searchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchButtonActionPerformed(evt);
            }
        });

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Pareto plot"));

        javax.swing.GroupLayout scatterPlot1Layout = new javax.swing.GroupLayout(scatterPlot1);
        scatterPlot1.setLayout(scatterPlot1Layout);
        scatterPlot1Layout.setHorizontalGroup(
            scatterPlot1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        scatterPlot1Layout.setVerticalGroup(
            scatterPlot1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 322, Short.MAX_VALUE)
        );

        jLabel1.setText("X");

        jLabel2.setText("Y");

        jCheckBox1.setSelected(true);
        jCheckBox1.setText("Show pareto optimal set for X and Y only");
        jCheckBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(xComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 292, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(31, 31, 31)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(yComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 292, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jCheckBox1)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel1)
                                .addComponent(xComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel2)
                                .addComponent(yComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addComponent(jCheckBox1)))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(83, 83, 83))
            .addComponent(scatterPlot1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(scatterPlot1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jMenu1.setText("File");
        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");
        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(searchButton)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(searchButton)
                        .addGap(0, 178, Short.MAX_VALUE))))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void addTargetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addTargetButtonActionPerformed
        TargetDialog d = new TargetDialog(this, true);
        
        int id = 1;
        String name = "";
        while(true)
        {
            name = "target"+id;
            boolean exists = false;
            for(int i = 0 ; i < targetListModel.getSize() ; i++)
            {
                if(name.equalsIgnoreCase(targetListModel.get(i).uniqueIdentifier))
                {                    
                    System.out.println(name +"\t"+targetListModel.get(i).uniqueIdentifier);
                    exists = true;
                    break;
                }                        
            }
            if(!exists)
            {
                break;
            }
            id++;
        }
        
        d.setNameField(name);
        
        GraphicsUtils.centerWindowOnWindow(d, this);
        d.setVisible(true);
        if(d.getReturnValue() != null)
        {
            targetListModel.addElement(d.getReturnValue());
        }
    }//GEN-LAST:event_addTargetButtonActionPerformed

    Search search = null;
    private void searchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchButtonActionPerformed
        ArrayList<TargetStructure> targetStructures = new  ArrayList<>();
        for(int i = 0 ; i < targetListModel.getSize() ; i++)
        {
            targetStructures.add(targetListModel.get(i));
        }
        search = new Search(targetStructures);
        search.start();
        this.searchButton.setEnabled(false);
    }//GEN-LAST:event_searchButtonActionPerformed

    private void jCheckBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox1ActionPerformed
        updatePlot.updatePlot();
    }//GEN-LAST:event_jCheckBox1ActionPerformed

    Boolean suppressUpdate = false;
    @Override
    public void itemStateChanged(ItemEvent e) {
        
        synchronized(suppressUpdate)
        {
            if(!suppressUpdate)
            {
                updatePlot.setLabels((String)xComboBoxModel.getSelectedItem(), (String)yComboBoxModel.getSelectedItem());
            }
        }
    }

    public class UpdatePlot implements Runnable {
        public String xlab = "target1: gc_content_paired_sites";
        public String ylab = "target1: gc_content_unpaired_sites";
        //public String xlab = "target1: rnafold ensemble prob @ 37.0C";
        //public String ylab = "target1: gc_content_paired_sites";
        
        public void setLabels(String xlab, String ylab)
        {
            this.xlab = xlab;
            this.ylab = ylab;
            updatePlot();
        }
        
        public void updatePlot()
        {
            Pair<ArrayList<Double>, ArrayList<Double>> xy = search.getXY(xlab, ylab, MainFrame.this.jCheckBox1.isSelected());                    
            scatterPlot1.xlab = xlab;
            scatterPlot1.ylab = ylab;
            scatterPlot1.repaint();
            if(xy.getLeft().size() > 0)
            {
                scatterPlot1.setData(xy.getLeft(), xy.getRight());
            }
        }
        
        @Override
        public void run() {
            while(true)
            {
                if(search != null)
                {                 
                    if(MainFrame.this.xComboBoxModel.getSize() == 0)
                    {
                        suppressUpdate = true;
                        synchronized(suppressUpdate)
                        {
                            for(String criteria : search.alphabeticalCriteria)
                            {
                                xComboBoxModel.addElement(criteria);
                                yComboBoxModel.addElement(criteria);
                            }
                        }
                        suppressUpdate = false;
                    }
                    
                    updatePlot();                  
                }
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }    
    }
    
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
                new MainFrame().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addTargetButton;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private nava.vienna.inverserna.ui.ScatterPlot scatterPlot1;
    private javax.swing.JButton searchButton;
    private javax.swing.JList targetList;
    private nava.utils.WiderDropDownComboBox xComboBox;
    private nava.utils.WiderDropDownComboBox yComboBox;
    // End of variables declaration//GEN-END:variables
}
