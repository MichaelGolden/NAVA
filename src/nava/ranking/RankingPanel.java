/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.ranking;

import java.awt.Color;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFrame;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import nava.structurevis.StructureVisController;
import nava.structurevis.data.*;
import nava.utils.Pair;

/**
 *
 * @author Michael
 */
public class RankingPanel extends javax.swing.JPanel {

    ArrayList<Overlay> dataOverlays = new ArrayList();
    ArrayList<StructureOverlay> structureOverlays = new ArrayList();
    RankingTable rankingTable = null;
    public RankingThread currentThread = null;
    StructureVisController structureVisController;
    //StructureOverlay structureOverlay;

    /**
     * Creates new form AnalysesPanel
     */
    public RankingPanel(StructureVisController structureVisController) {
        initComponents();
        this.structureVisController = structureVisController;

        rankingTable = new RankingTable();
        jPanel1.add(rankingTable);

        dataOverlays.addAll(structureVisController.structureVisModel.structureVisDataOverlays1D.getArrayListShallowCopy());
        dataOverlays.addAll(structureVisController.structureVisModel.structureVisDataOverlays2D.getArrayListShallowCopy());

        structureOverlays.addAll(structureVisController.structureVisModel.structureSources.getArrayListShallowCopy());

        DefaultComboBoxModel dataOverlaysModel = new DefaultComboBoxModel();
        dataOverlayBox.setModel(dataOverlaysModel);
        for (int i = 0; i < dataOverlays.size(); i++) {
            dataOverlaysModel.addElement(dataOverlays.get(i));
        }

        DefaultComboBoxModel structureOverlaysModel = new DefaultComboBoxModel();
        structureOverlayComboBox.setModel(structureOverlaysModel);
        for (int i = 0; i < structureOverlays.size(); i++) {
            structureOverlaysModel.addElement(structureOverlays.get(i));
        }

    }
    Hashtable<Pair<Overlay, Integer>, ArrayList> rowCache = new Hashtable<>();

    public ArrayList getListFromCache(Pair<Overlay, Integer> key) {
        ArrayList ret = rowCache.get(key);
        if (ret == null) {
            return new ArrayList();
        }

        return ret;
    }

    class RankingThread extends Thread {

        Overlay dataOverlay;
        StructureOverlay structureOverlay;
        boolean running = false;
        boolean hasStopped = true;

        public RankingThread(Overlay dataOverlay, StructureOverlay structureOverlay) {
            this.dataOverlay = dataOverlay;
            this.structureOverlay = structureOverlay;
        }

        public void run() {
            hasStopped = false;
            running = true;

            saveAsCSVButton.setEnabled(false);
            statusLabel.setForeground(Color.red);
            statusLabel.setText("Ranking...");

            rankingTable.tableDataModel.clear();
            boolean paired = true;

            int PAIR_PARAMETER = 0;
            boolean unpaired = true;
            if (RankingPanel.this.pairedOnlyButton.isSelected()) {
                paired = true;
                unpaired = false;
                PAIR_PARAMETER = 1;
            } else if (RankingPanel.this.unpairedOnlyButton.isSelected()) {
                paired = false;
                unpaired = true;
                PAIR_PARAMETER = 2;
            }

            if (dataOverlay instanceof DataOverlay1D) {
                DataOverlay1D dataOverlay1D = (DataOverlay1D) dataOverlay;
                //String cacheKey = dataOverlay1D.name + "_" + PAIR_PARAMETER;
                Pair<Overlay, Integer> cacheKey = new Pair((Overlay) dataOverlay1D, new Integer(PAIR_PARAMETER));
                ArrayList rows = getListFromCache(cacheKey);
                rankingTable.tableDataModel.addRows(rows);
                ArrayList<Substructure> structures = structureOverlay.substructureList.substructures;
                for (int i = rows.size(); running && i < structures.size(); i++) {
                    statusLabel.setText("Ranking (" + (i + 1) + " of " + structures.size() + ")");
                    Substructure structure = structures.get(i);
                    Ranking ranking = RankingAnalyses.rankSequenceData1D(dataOverlay1D, structureVisController.getMapping(structureOverlay.mappingSource, dataOverlay1D.mappingSource), structure, structureOverlay.pairedSites, paired, unpaired);

                    //System.out.println(ranking.zScore + "\t" + StatUtil.erf(Math.abs(ranking.zScore/2)) + "\t" + StatUtil.erfc(Math.abs(ranking.zScore))+ "\t" + StatUtil.erfcx(Math.abs(ranking.zScore))+ "\t" + StatUtil.getInvCDF(Math.abs(ranking.zScore), true)+"\t"+RankingAnalyses.NormalZ(Math.abs(ranking.zScore))/2);
                    Object[] row = {new Integer(i + 1), structure.name, new Location(structure.startPosition, structure.startPosition + structure.length), new Integer(structure.length), new Integer(ranking.xN), new Integer(ranking.yN), new Double(ranking.xMean), new Double(ranking.yMean), new Double(ranking.xMedian), new Double(ranking.yMedian), new Double(ranking.mannWhitneyU), new Double(RankingAnalyses.NormalZ(Math.abs(ranking.zScore)) / 2), new Double(ranking.zScore)};
                    rankingTable.tableDataModel.addRow(row);
                    rankingTable.repaint();
                }

                ArrayList<Object[]> clone = new ArrayList<>();
                for (int i = 0; i < rankingTable.tableDataModel.rows.size(); i++) {
                    clone.add(rankingTable.tableDataModel.rows.get(i));
                }
                rowCache.put(cacheKey, clone);
            } else if (dataOverlay instanceof DataOverlay2D) {
                DataOverlay2D dataOverlay2D = (DataOverlay2D) dataOverlay;
                Pair<Overlay, Integer> cacheKey = new Pair((Overlay) dataOverlay2D, new Integer(PAIR_PARAMETER));
                ArrayList rows = getListFromCache(cacheKey);
                rankingTable.tableDataModel.addRows(rows);
                ArrayList<Substructure> structures = structureOverlay.substructureList.substructures;
                ArrayList<Double> fullGenomeList = null;
                if (running && rows.size() < structures.size()) {
                    try {
                        fullGenomeList = RankingAnalyses.getValues(dataOverlay2D, structureVisController.getMapping(structureOverlay.mappingSource, dataOverlay2D.mappingSource), new Substructure(0, structureOverlay.pairedSites), structureOverlay.pairedSites, paired, unpaired);
                    } catch (IOException ex) {
                        Logger.getLogger(RankingPanel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                for (int i = rows.size(); running && i < structures.size(); i++) {
                    statusLabel.setText("Ranking (" + (i + 1) + " of " + structures.size() + ")");
                    Substructure structure = structures.get(i);

                    Ranking ranking;
                    try {
                        ranking = RankingAnalyses.rankSequenceData2D(dataOverlay2D, structureVisController.getMapping(structureOverlay.mappingSource, dataOverlay2D.mappingSource), structure, structureOverlay.pairedSites, paired, unpaired, fullGenomeList);

                        Object[] row = {new Integer(i + 1), structure.name, new Location(structure.startPosition, structure.startPosition + structure.length), new Integer(structure.length), new Integer(ranking.xN), new Integer(ranking.yN), new Double(ranking.xMean), new Double(ranking.yMean), new Double(ranking.xMedian), new Double(ranking.yMedian), new Double(ranking.mannWhitneyU), new Double(RankingAnalyses.NormalZ(Math.abs(ranking.zScore)) / 2), new Double(ranking.zScore)};
                        rankingTable.tableDataModel.addRow(row);
                        rankingTable.repaint();
                    } catch (IOException ex) {
                        Logger.getLogger(RankingPanel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                ArrayList<Object[]> clone = new ArrayList<Object[]>();
                for (int i = 0; i < rankingTable.tableDataModel.rows.size(); i++) {
                    clone.add(rankingTable.tableDataModel.rows.get(i));
                }
                rowCache.put(cacheKey, clone);
            }

            hasStopped = true;

            statusLabel.setForeground(Color.green);
            statusLabel.setText("Ranking complete");
            saveAsCSVButton.setEnabled(true);
        }

        public void kill() {
            running = false;
            while (!hasStopped) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException ex) {
                    Logger.getLogger(RankingPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public void kill() {
        if (currentThread != null) {
            currentThread.kill();
            currentThread = null;
        }
    }

    public void performRanking() {
        kill();

        if (dataOverlayBox.getSelectedIndex() >= 0) {
            Overlay dataOverlay = dataOverlays.get(dataOverlayBox.getSelectedIndex());
            StructureOverlay structureOverlay = (StructureOverlay) structureOverlayComboBox.getSelectedItem();
            if (dataOverlay != null && structureOverlay != null) {
                currentThread = new RankingThread(dataOverlay, structureOverlay);
               // currentThread.setPriority(Thread.MIN_PRIORITY);
                currentThread.start();
            }
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

        buttonGroup1 = new javax.swing.ButtonGroup();
        dataOverlayBox = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        pairedOnlyButton = new javax.swing.JRadioButton();
        unpairedOnlyButton = new javax.swing.JRadioButton();
        allSitesButton = new javax.swing.JRadioButton();
        statusLabel = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        saveAsCSVButton = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        structureOverlayComboBox = new javax.swing.JComboBox();

        dataOverlayBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dataOverlayBoxActionPerformed(evt);
            }
        });

        jLabel1.setText("Data overlay");

        jPanel1.setLayout(new java.awt.BorderLayout());

        buttonGroup1.add(pairedOnlyButton);
        pairedOnlyButton.setText("Paired only");
        pairedOnlyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pairedOnlyButtonActionPerformed(evt);
            }
        });

        buttonGroup1.add(unpairedOnlyButton);
        unpairedOnlyButton.setText("Unpaired only");
        unpairedOnlyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                unpairedOnlyButtonActionPerformed(evt);
            }
        });

        buttonGroup1.add(allSitesButton);
        allSitesButton.setSelected(true);
        allSitesButton.setText("All nucleotides");
        allSitesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                allSitesButtonActionPerformed(evt);
            }
        });

        statusLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        statusLabel.setText(" ");

        jLabel2.setForeground(new java.awt.Color(102, 102, 102));
        jLabel2.setText("Double-click on a row to open the corresponding structure in the viewer.");

        saveAsCSVButton.setText("Save as CSV");
        saveAsCSVButton.setEnabled(false);
        saveAsCSVButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveAsCSVButtonActionPerformed(evt);
            }
        });

        jLabel3.setText("Structure");

        structureOverlayComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                structureOverlayComboBoxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel3))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(structureOverlayComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(allSitesButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(pairedOnlyButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(unpairedOnlyButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(statusLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 212, Short.MAX_VALUE))
                            .addComponent(dataOverlayBox, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(saveAsCSVButton))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(statusLabel)
                        .addComponent(saveAsCSVButton))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(structureOverlayComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(allSitesButton)
                            .addComponent(unpairedOnlyButton)
                            .addComponent(pairedOnlyButton))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(dataOverlayBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 307, Short.MAX_VALUE)
                .addGap(22, 22, 22))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void dataOverlayBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dataOverlayBoxActionPerformed
        performRanking();
    }//GEN-LAST:event_dataOverlayBoxActionPerformed

    private void allSitesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_allSitesButtonActionPerformed
        performRanking();
    }//GEN-LAST:event_allSitesButtonActionPerformed

    private void pairedOnlyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pairedOnlyButtonActionPerformed
        performRanking();
    }//GEN-LAST:event_pairedOnlyButtonActionPerformed

    private void unpairedOnlyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_unpairedOnlyButtonActionPerformed
        performRanking();
    }//GEN-LAST:event_unpairedOnlyButtonActionPerformed

    private void saveAsCSVButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveAsCSVButtonActionPerformed

        /*
         * String name = ((SequenceData1D)
         * sequenceData.get(dataSourceBox.getSelectedIndex())).name; if
         * (RankingPanel.this.pairedOnlyButton.isSelected()) { name = name +
         * "-paired_only"; } else if
         * (RankingPanel.this.unpairedOnlyButton.isSelected()) { name = name +
         * "-unpaired_only"; }
         *
         * File outFile = new
         * File(MainApp.fileChooserSave.getCurrentDirectory().getPath() + "/" +
         * name + "-ranking.csv"); MainApp.fileChooserSave.setDialogTitle("Save
         * CSV"); MainApp.fileChooserSave.setSelectedFile(outFile); int
         * returnVal = MainApp.fileChooserSave.showSaveDialog(this); if
         * (returnVal == JFileChooser.APPROVE_OPTION) {
         * setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
         * saveAsCSV(MainApp.fileChooserSave.getSelectedFile());
         * setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR)); }
         * MainApp.fileChooserSave.setDialogTitle("Open");
         *
         * System.out.println(name + "\t" +
         * rankingTable.tableDataModel.rows.size());
         *
         */
    }//GEN-LAST:event_saveAsCSVButtonActionPerformed

    private void structureOverlayComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_structureOverlayComboBoxActionPerformed
        performRanking();
    }//GEN-LAST:event_structureOverlayComboBoxActionPerformed

    public void saveAsCSV(File outFile) {
        try {
            BufferedWriter buffer = new BufferedWriter(new FileWriter(outFile));
            TableColumnModel columnModel = rankingTable.table.getTableHeader().getColumnModel();
            for (int i = 0; i < columnModel.getColumnCount() - 1; i++) {
                buffer.write(columnModel.getColumn(i).getHeaderValue().toString() + ",");
            }
            buffer.write(columnModel.getColumn(columnModel.getColumnCount() - 1).getHeaderValue().toString());
            buffer.newLine();
            //ArrayList<Object[]> rows = rankingTable.table.getModel().;

            /*
             * TableModel tableModel = rankingTable.table.getModel(); for (int i
             * = 0; i < rows.size(); i++) { Object[] row = rows.get(i); for (int
             * j = 0; j < row.length - 1 ; j++) { buffer.write(row[j].toString()
             * + ","); } buffer.write(row[row.length - 1].toString());
             * buffer.newLine(); }
             */

            TableModel tableModel = rankingTable.table.getModel();
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                //Object[] row = rows.get(i);
                for (int j = 0; j < tableModel.getColumnCount() - 1; j++) {
                    buffer.write(tableModel.getValueAt(i, j).toString() + ",");
                }
                buffer.write(tableModel.getValueAt(i, tableModel.getColumnCount() - 1).toString().toString());
                buffer.newLine();
            }
            buffer.close();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void createAndShowGUI(JFrame parent) {
        //Create and set up the window.
        JFrame frame = new JFrame("Structure ranking");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setIconImage(parent.getIconImage());

        //Create and set up the content pane.
        RankingPanel newContentPane = new RankingPanel(null);
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                createAndShowGUI(null);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton allSitesButton;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox dataOverlayBox;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JRadioButton pairedOnlyButton;
    private javax.swing.JButton saveAsCSVButton;
    private javax.swing.JLabel statusLabel;
    private javax.swing.JComboBox structureOverlayComboBox;
    private javax.swing.JRadioButton unpairedOnlyButton;
    // End of variables declaration//GEN-END:variables
}
