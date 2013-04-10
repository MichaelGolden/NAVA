/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.ranking;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import nava.structurevis.StructureVisController;
import nava.structurevis.data.*;
import nava.ui.MainFrame;
import nava.utils.Mapping;
import nava.utils.Pair;

/**
 *
 * @author Michael
 */
public class PairTestPanel extends javax.swing.JPanel {

    ArrayList<Overlay> dataOverlays = new ArrayList();
    ArrayList<StructureOverlay> structureOverlays = new ArrayList();
    PairTestTable pairTable = null;
    public RankingThread currentThread = null;
    StructureVisController structureVisController;
    NHistogramPanel nHistPanel = new NHistogramPanel();
    //StructureOverlay structureOverlay;

    /**
     * Creates new form AnalysesPanel
     */
    public PairTestPanel(StructureVisController structureVisController) {
        initComponents();
        this.structureVisController = structureVisController;

        pairTable = new PairTestTable();
        jPanel1.add(pairTable);

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

        pairTable.table.addMouseListener(new MouseAdapter() {

            public void mouseClicked(MouseEvent e) {
                JTable target = (JTable) e.getSource();
                int row = target.getSelectedRow();
                int column = target.getSelectedColumn();

                int s = ((Integer) pairTable.table.getModel().getValueAt(row, 0)).intValue() - 1;
                Ranking ranking = (Ranking) pairTable.table.getModel().getValueAt(row, 13);
                nHistPanel.setNHistogram(ranking.nhist);
            }
        });
        histogramPanel.add(nHistPanel);

        this.nHistPanel.setNullText("Click on a substructure to compare the distribution of it's paired nucleotides to that of it's unpaired nucleotides.");
    }
    Hashtable<PairTestKey, ArrayList> rowCache = new Hashtable<>();

    public ArrayList getListFromCache(PairTestKey key) {
        ArrayList ret = rowCache.get(key);
        if (ret == null) {
            return new ArrayList();
        }

        return ret;
    }

    class PairTestKey {

        Overlay dataOverlay;
        StructureOverlay structureOverlay;

        public PairTestKey(Overlay dataOverlay, StructureOverlay structureOverlay) {
            this.dataOverlay = dataOverlay;
            this.structureOverlay = structureOverlay;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final PairTestKey other = (PairTestKey) obj;
            if (!Objects.equals(this.dataOverlay, other.dataOverlay)) {
                return false;
            }
            if (!Objects.equals(this.structureOverlay, other.structureOverlay)) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 61 * hash + Objects.hashCode(this.dataOverlay);
            hash = 61 * hash + Objects.hashCode(this.structureOverlay);
            return hash;
        }
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

        @Override
        public void run() {
            hasStopped = false;
            running = true;

            saveAsCSVButton.setEnabled(false);
            statusLabel.setForeground(Color.red);
            statusLabel.setText("Ranking...");

            pairTable.tableDataModel.clear();

            if (dataOverlay instanceof DataOverlay1D) {
                DataOverlay1D dataOverlay1D = (DataOverlay1D) dataOverlay;
                //String cacheKey = dataOverlay1D.name + "_" + PAIR_PARAMETER;
                // Pair<Overlay, Integer> cacheKey = new Pair((Overlay) dataOverlay1D, new Integer(PAIR_PARAMETER));
                PairTestKey key = new PairTestKey(dataOverlay1D, structureOverlay);
                ArrayList rows = getListFromCache(key);
                pairTable.tableDataModel.addRows(rows);
                ArrayList<Substructure> structures = new ArrayList<>();
                structures.add(new Substructure(0, structureOverlay.pairedSites));
                structures.addAll(structureOverlay.substructureList.substructures);
                for (int i = rows.size(); running && i < structures.size(); i++) {
                    statusLabel.setText("Ranking (" + (i + 1) + " of " + structures.size() + ")");
                    Substructure structure = structures.get(i);
                    Mapping mapping = structureVisController.getMapping(structureOverlay.mappingSource, dataOverlay1D.mappingSource);

                    Ranking ranking = RankingAnalyses.basePairComparison1D(dataOverlay1D, mapping, structure, structureOverlay.pairedSites, i + 1);

                    Object[] row1 = {new Integer(i), structure.name, new Location(structure.startPosition, structure.startPosition + structure.length), new Integer(structure.length), new Integer(ranking.xN), new Integer(ranking.yN), new Double(ranking.xMean), new Double(ranking.yMean), new Double(ranking.xMedian), new Double(ranking.yMedian), new Double(ranking.mannWhitneyU), new Double(RankingAnalyses.NormalZ(Math.abs(ranking.zScore)) / 2), new Double(ranking.zScore), ranking};
                    final Object[] row;
                    if (i == 0) {
                        Object[] row2 = {new Integer(i), "Full genome", new Location(structure.startPosition, structure.startPosition + structure.length), new Integer(structure.length), new Integer(ranking.xN), new Integer(ranking.yN), new Double(ranking.xMean), new Double(ranking.yMean), new Double(ranking.xMedian), new Double(ranking.yMedian), new Double(ranking.mannWhitneyU), new Double(RankingAnalyses.NormalZ(Math.abs(ranking.zScore)) / 2), new Double(ranking.zScore), ranking};
                        row = row2;
                    } else {
                        row = row1;
                    }
                    pairTable.tableDataModel.addRow(row);
                    pairTable.repaint();

                }

                ArrayList<Object[]> clone = new ArrayList<>();
                for (int i = 0; i < pairTable.tableDataModel.rows.size(); i++) {
                    clone.add(pairTable.tableDataModel.rows.get(i));
                }
                rowCache.put(key, clone);
            } else if (dataOverlay instanceof DataOverlay2D) {
                DataOverlay2D dataOverlay2D = (DataOverlay2D) dataOverlay;
                //Pair<Overlay, Integer> cacheKey = new Pair((Overlay) dataOverlay2D, new Integer(PAIR_PARAMETER));
                PairTestKey key = new PairTestKey(dataOverlay2D, structureOverlay);
                ArrayList rows = getListFromCache(key);
                pairTable.tableDataModel.addRows(rows);
                ArrayList<Substructure> structures = new ArrayList<>();
                structures.add(new Substructure(0, structureOverlay.pairedSites));
                structures.addAll(structureOverlay.substructureList.substructures);
                for (int i = rows.size(); running && i < structures.size(); i++) {
                    statusLabel.setText("Ranking (" + (i + 1) + " of " + structures.size() + ")");
                    Substructure structure = structures.get(i);

                    Ranking ranking;
                    try {
                        ranking = RankingAnalyses.basePairComparison2D(dataOverlay2D, structureVisController.getMapping(structureOverlay.mappingSource, dataOverlay2D.mappingSource), structure, structureOverlay.pairedSites, i + 1);

                        Object[] row1 = {new Integer(i), structure.name, new Location(structure.startPosition, structure.startPosition + structure.length), new Integer(structure.length), new Integer(ranking.xN), new Integer(ranking.yN), new Double(ranking.xMean), new Double(ranking.yMean), new Double(ranking.xMedian), new Double(ranking.yMedian), new Double(ranking.mannWhitneyU), new Double(RankingAnalyses.NormalZ(Math.abs(ranking.zScore)) / 2), new Double(ranking.zScore), ranking};
                        final Object[] row;
                        if (i == 0) {
                            Object[] row2 = {new Integer(i), "Full genome", new Location(structure.startPosition, structure.startPosition + structure.length), new Integer(structure.length), new Integer(ranking.xN), new Integer(ranking.yN), new Double(ranking.xMean), new Double(ranking.yMean), new Double(ranking.xMedian), new Double(ranking.yMedian), new Double(ranking.mannWhitneyU), new Double(RankingAnalyses.NormalZ(Math.abs(ranking.zScore)) / 2), new Double(ranking.zScore), ranking};
                            row = row2;
                        } else {
                            row = row1;
                        }

                        pairTable.tableDataModel.addRow(row);
                        pairTable.repaint();

                    } catch (IOException ex) {
                        Logger.getLogger(PairTestPanel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                ArrayList<Object[]> clone = new ArrayList<Object[]>();
                for (int i = 0; i < pairTable.tableDataModel.rows.size(); i++) {
                    clone.add(pairTable.tableDataModel.rows.get(i));
                }
                rowCache.put(key, clone);
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
                    Logger.getLogger(PairTestPanel.class.getName()).log(Level.SEVERE, null, ex);
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
                nHistPanel.setNHistogram(null);
                structureOverlay.loadData();
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
        jSplitPane1 = new javax.swing.JSplitPane();
        histogramPanel = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        dataOverlayBox = new javax.swing.JComboBox();
        saveAsCSVButton = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        structureOverlayComboBox = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        statusLabel = new javax.swing.JLabel();

        jSplitPane1.setDividerLocation(300);
        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane1.setResizeWeight(0.8);

        histogramPanel.setLayout(new java.awt.BorderLayout());
        jSplitPane1.setBottomComponent(histogramPanel);

        dataOverlayBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dataOverlayBoxActionPerformed(evt);
            }
        });

        saveAsCSVButton.setText("Save as CSV");
        saveAsCSVButton.setEnabled(false);
        saveAsCSVButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveAsCSVButtonActionPerformed(evt);
            }
        });

        jLabel2.setForeground(new java.awt.Color(102, 102, 102));
        jLabel2.setText("Double-click on a row to open the corresponding structure in the viewer.");

        jPanel1.setLayout(new java.awt.BorderLayout());

        structureOverlayComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                structureOverlayComboBoxActionPerformed(evt);
            }
        });

        jLabel1.setText("Data overlay");

        jLabel3.setText("Structure");

        statusLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        statusLabel.setText(" ");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGap(317, 317, 317)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(dataOverlayBox, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 26, Short.MAX_VALUE)
                        .addComponent(statusLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(saveAsCSVButton))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 922, Short.MAX_VALUE)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addComponent(jLabel3)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(structureOverlayComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(0, 666, Short.MAX_VALUE)))
                    .addContainerGap()))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(saveAsCSVButton)
                    .addComponent(statusLabel)
                    .addComponent(dataOverlayBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel2)
                .addContainerGap())
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel3)
                        .addComponent(structureOverlayComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(31, 31, 31)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 273, Short.MAX_VALUE)
                    .addContainerGap()))
        );

        jSplitPane1.setLeftComponent(jPanel2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 402, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void structureOverlayComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_structureOverlayComboBoxActionPerformed
        performRanking();
    }//GEN-LAST:event_structureOverlayComboBoxActionPerformed

    private void saveAsCSVButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveAsCSVButtonActionPerformed
        String name = ((Overlay) dataOverlayBox.getSelectedItem()).title;
        File outFile = new File(MainFrame.saveDialog.getCurrentDirectory().getPath() + "/" + name + "-pair-ranking.csv");
        MainFrame.saveDialog.setDialogTitle("Save CSV");
        MainFrame.saveDialog.setSelectedFile(outFile);
        int returnVal = MainFrame.saveDialog.showSaveDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            saveAsCSV(MainFrame.saveDialog.getSelectedFile());
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
    }//GEN-LAST:event_saveAsCSVButtonActionPerformed

    private void dataOverlayBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dataOverlayBoxActionPerformed
        performRanking();
    }//GEN-LAST:event_dataOverlayBoxActionPerformed

    public void saveAsCSV(File outFile) {
        try {
            BufferedWriter buffer = new BufferedWriter(new FileWriter(outFile));
            TableColumnModel columnModel = pairTable.table.getTableHeader().getColumnModel();
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

            TableModel tableModel = pairTable.table.getModel();
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
        PairTestPanel newContentPane = new PairTestPanel(null);
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
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox dataOverlayBox;
    private javax.swing.JPanel histogramPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JButton saveAsCSVButton;
    private javax.swing.JLabel statusLabel;
    private javax.swing.JComboBox structureOverlayComboBox;
    // End of variables declaration//GEN-END:variables
}
