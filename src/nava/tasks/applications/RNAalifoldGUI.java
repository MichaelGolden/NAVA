/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.tasks.applications;

import javax.swing.SpinnerNumberModel;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class RNAalifoldGUI extends javax.swing.JDialog implements ApplicationDialog {

    SpinnerNumberModel temperatureModel = new SpinnerNumberModel(37, -273, 1000, 0.5);
    SpinnerNumberModel covarianceModel = new SpinnerNumberModel(1, 0, 1000, 0.1);
    SpinnerNumberModel nonCompatiblityModel = new SpinnerNumberModel(1, 0, 1000, 0.1);

    /**
     * Creates new form RNAalifoldGUI
     */
    public RNAalifoldGUI(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        this.temperatureSpinner.setModel(temperatureModel);
        this.covarianceSpinner.setModel(covarianceModel);
        this.nonCompatibilitySpinner.setModel(nonCompatiblityModel);
    }
    
    public String getArgumentString()
    {
        String arguments = "";
        arguments += "-cv "+covarianceSpinner.getValue()+ " -nc "+nonCompatibilitySpinner.getValue() + " -T "+temperatureSpinner.getValue();
        if(this.circularRadioButton.isSelected())
        {
            arguments += " -circ ";
        }
        if(!this.partionFunctionCheckBox.isSelected())
        {
            arguments += " -p0 ";
        }
        return arguments;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        conformationGroup = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        temperatureSpinner = new javax.swing.JSpinner();
        partionFunctionCheckBox = new javax.swing.JCheckBox();
        jLabel2 = new javax.swing.JLabel();
        nonCompatibilitySpinner = new javax.swing.JSpinner();
        jLabel3 = new javax.swing.JLabel();
        covarianceSpinner = new javax.swing.JSpinner();
        runButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jRadioButton1 = new javax.swing.JRadioButton();
        circularRadioButton = new javax.swing.JRadioButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("RNAalifold settings");

        jLabel1.setText("Temperature");

        partionFunctionCheckBox.setSelected(true);
        partionFunctionCheckBox.setText("Generate base-pairing probability matrix (calculate partition function)");

        jLabel2.setText("Covariance weighting");

        jLabel3.setText("Non-compatibility weighting");

        runButton.setText("Run");
        runButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                runButtonActionPerformed(evt);
            }
        });

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        jLabel4.setText("Conformation");

        conformationGroup.add(jRadioButton1);
        jRadioButton1.setSelected(true);
        jRadioButton1.setText("Linear");

        conformationGroup.add(circularRadioButton);
        circularRadioButton.setText("Circular");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(partionFunctionCheckBox, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel3)
                                    .addComponent(jLabel2)
                                    .addComponent(jLabel1)
                                    .addComponent(jLabel4))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jRadioButton1)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(circularRadioButton))
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(nonCompatibilitySpinner, javax.swing.GroupLayout.DEFAULT_SIZE, 61, Short.MAX_VALUE)
                                        .addComponent(covarianceSpinner)
                                        .addComponent(temperatureSpinner)))))
                        .addContainerGap(24, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(cancelButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(runButton)
                        .addGap(12, 12, 12))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(temperatureSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(covarianceSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(nonCompatibilitySpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(7, 7, 7)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jRadioButton1)
                    .addComponent(circularRadioButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(partionFunctionCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 17, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelButton)
                    .addComponent(runButton))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    boolean shouldRun = false;
    private void runButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_runButtonActionPerformed
        System.out.println(this.getArgumentString());
        shouldRun = true;
        dispose();
    }//GEN-LAST:event_runButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        shouldRun = false;
        dispose();
    }//GEN-LAST:event_cancelButtonActionPerformed

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
            java.util.logging.Logger.getLogger(RNAalifoldGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(RNAalifoldGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(RNAalifoldGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(RNAalifoldGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /*
         * Create and display the dialog
         */
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                RNAalifoldGUI dialog = new RNAalifoldGUI(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton cancelButton;
    private javax.swing.JRadioButton circularRadioButton;
    private javax.swing.ButtonGroup conformationGroup;
    private javax.swing.JSpinner covarianceSpinner;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JSpinner nonCompatibilitySpinner;
    private javax.swing.JCheckBox partionFunctionCheckBox;
    private javax.swing.JButton runButton;
    private javax.swing.JSpinner temperatureSpinner;
    // End of variables declaration//GEN-END:variables

    @Override
    public void setupApplication(Application application) {
        if(application instanceof RNAalifoldApplication)
        {
            RNAalifoldApplication app = (RNAalifoldApplication)application;
            app.setArguments(getArgumentString());
        } 
        else
        {
            
        }
    }

    @Override
    public boolean shouldRunOnDialogClose() {
        return shouldRun;
    }
}
