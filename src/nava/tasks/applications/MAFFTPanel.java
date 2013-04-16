/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.tasks.applications;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Icon;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class MAFFTPanel extends javax.swing.JPanel implements ActionListener, ApplicationPanelInterface {

    /**
     * Creates new form MAFFTPanel
     */
    public MAFFTPanel() {
        initComponents();
        this.jRadioButton1.addActionListener(this);
        this.jRadioButton2.addActionListener(this);
        this.jRadioButton3.addActionListener(this);
        this.jRadioButton4.addActionListener(this);
        this.jRadioButton5.addActionListener(this);
        this.jRadioButton6.addActionListener(this);
        this.jRadioButton7.addActionListener(this);
        this.jRadioButton8.addActionListener(this);
        this.jRadioButton9.addActionListener(this);
        this.jRadioButton10.addActionListener(this);
        
        
        this.jRadioButton1.setActionCommand("--localpair --maxiterate 1000");
        this.jRadioButton2.setActionCommand("--auto");
        this.jRadioButton3.setActionCommand("--globalpair --maxiterate 1000");
        this.jRadioButton4.setActionCommand("--ep 0 --genafpair --maxiterate 1000");
        this.jRadioButton5.setActionCommand("--retree 2 --maxiterate 2");
        this.jRadioButton6.setActionCommand("--retree 2 --maxiterate 1000");
        this.jRadioButton7.setActionCommand("--retree 2 --maxiterate 0");
        this.jRadioButton8.setActionCommand("--retree 2 --maxiterate 2 --nofft");
        this.jRadioButton9.setActionCommand("--retree 2 --maxiterate 0 --nofft");
        this.jRadioButton10.setActionCommand("--retree 1 --maxiterate 0 --nofft --parttree");
        
        arguments =  this.jRadioButton2.getActionCommand();
        this.jTextField1.setText(arguments);
    }
    
    String arguments = "";
    public String getArgumentString()
    {
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

        group = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        jRadioButton3 = new javax.swing.JRadioButton();
        jRadioButton4 = new javax.swing.JRadioButton();
        jLabel2 = new javax.swing.JLabel();
        jRadioButton5 = new javax.swing.JRadioButton();
        jRadioButton6 = new javax.swing.JRadioButton();
        jRadioButton7 = new javax.swing.JRadioButton();
        jRadioButton8 = new javax.swing.JRadioButton();
        jRadioButton9 = new javax.swing.JRadioButton();
        jRadioButton10 = new javax.swing.JRadioButton();
        jLabel3 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();

        jLabel1.setText("Accuracy-orientated methods");

        group.add(jRadioButton1);
        jRadioButton1.setText("L-INS-i (most accurate; <200 sequences; iterative refinement, using local pairwise infromation)");
        jRadioButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton1ActionPerformed(evt);
            }
        });

        group.add(jRadioButton2);
        jRadioButton2.setSelected(true);
        jRadioButton2.setText("Automatic (Selects one of: L-INS-i, FFT-NS-i and FFT-NS-2 according to data size)");

        group.add(jRadioButton3);
        jRadioButton3.setText("G-INS-i (for sequences of similar lengths; <200 sequences, iterative refinement, using global pairwise information)");

        group.add(jRadioButton4);
        jRadioButton4.setText("E-INS-i (for sequences containing large unalignable regions; <200 sequences)");

        jLabel2.setText("Speed-orientated methods");

        group.add(jRadioButton5);
        jRadioButton5.setText("FFT-NS-i (iterative refinement method; two cycles only)");

        group.add(jRadioButton6);
        jRadioButton6.setText("FFT-NS-i (iterative refinement method; max. 1000 iterations)");

        group.add(jRadioButton7);
        jRadioButton7.setText("FFT-NS-1 (very fast; recommended for >2000 sequences; progressive method with a rough guide tree)");

        group.add(jRadioButton8);
        jRadioButton8.setText("NW-NS-i (iterative refinement method without FFT approximation; two cycles only)");

        group.add(jRadioButton9);
        jRadioButton9.setText("NW-NS-2 (fast; progressive method without the FFT approximation)");

        group.add(jRadioButton10);
        jRadioButton10.setText("NW-NS-PartTree-1 (recommended for ~10,000 to ~50,000 sequences; progressive method with the PartTree algorithm)");

        jLabel3.setText("Command");

        jTextField1.setEditable(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jRadioButton6)
                            .addComponent(jRadioButton5)
                            .addComponent(jRadioButton7)
                            .addComponent(jRadioButton8)
                            .addComponent(jRadioButton9)
                            .addComponent(jRadioButton10)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 337, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jRadioButton3)
                            .addComponent(jRadioButton1)
                            .addComponent(jRadioButton4)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(9, 9, 9)
                        .addComponent(jRadioButton2)))
                .addContainerGap(10, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jRadioButton2)
                .addGap(18, 18, 18)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButton3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButton4)
                .addGap(24, 24, 24)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButton5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButton6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButton7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButton8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButton9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButton10)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(13, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jRadioButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jRadioButton1ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup group;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton10;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JRadioButton jRadioButton3;
    private javax.swing.JRadioButton jRadioButton4;
    private javax.swing.JRadioButton jRadioButton5;
    private javax.swing.JRadioButton jRadioButton6;
    private javax.swing.JRadioButton jRadioButton7;
    private javax.swing.JRadioButton jRadioButton8;
    private javax.swing.JRadioButton jRadioButton9;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables

    @Override
    public void setupApplication(Application application) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getTitle() {
        return "MAFFT settings";
    }

    @Override
    public Icon getIcon() {
        return null;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        arguments = e.getActionCommand();
        this.jTextField1.setText(arguments);
    }
}