/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.alignment;

import javax.swing.SpinnerNumberModel;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class SettingsPanel extends javax.swing.JPanel {

    SpinnerNumberModel substructureWindowSpinnerModel = new SpinnerNumberModel(75, 10, Integer.MAX_VALUE, 1);
    
    /**
     * Creates new form SettingsPanel
     */
    public SettingsPanel() {
        initComponents();
        substructureWindowSpinner.setModel(substructureWindowSpinnerModel);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        substructureWindowGroup = new javax.swing.ButtonGroup();
        substructureWindowSpinner = new javax.swing.JSpinner();
        jLabel1 = new javax.swing.JLabel();
        relaxedRadioButton = new javax.swing.JRadioButton();
        strictRadioButton = new javax.swing.JRadioButton();
        jLabel2 = new javax.swing.JLabel();

        jLabel1.setText("Sliding window size");

        substructureWindowGroup.add(relaxedRadioButton);
        relaxedRadioButton.setSelected(true);
        relaxedRadioButton.setText("Relaxed");

        substructureWindowGroup.add(strictRadioButton);
        strictRadioButton.setText("Strict");

        jLabel2.setText("Substructure window setting");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(strictRadioButton)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(substructureWindowSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(relaxedRadioButton)
                    .addComponent(jLabel2))
                .addContainerGap(43, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(substructureWindowSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(relaxedRadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(strictRadioButton)
                .addContainerGap(84, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    public javax.swing.JRadioButton relaxedRadioButton;
    public javax.swing.JRadioButton strictRadioButton;
    private javax.swing.ButtonGroup substructureWindowGroup;
    public javax.swing.JSpinner substructureWindowSpinner;
    // End of variables declaration//GEN-END:variables
}
