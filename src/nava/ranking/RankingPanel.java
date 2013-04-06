/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.ranking;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import nava.structurevis.StructureVisController;
import nava.structurevis.data.*;
import nava.utils.Mapping;
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
    NHistogramPanel nHistPanel = new NHistogramPanel();
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

        rankingTable.table.addMouseListener(new MouseAdapter() {

            public void mouseClicked(MouseEvent e) {
                JTable target = (JTable) e.getSource();
                int row = target.getSelectedRow();
                int column = target.getSelectedColumn();

                int s = ((Integer) rankingTable.table.getModel().getValueAt(row, 0)).intValue() - 1;
                Ranking ranking = (Ranking) rankingTable.table.getModel().getValueAt(row, 13);
                nHistPanel.setNHistogram(ranking.nhist);
            }
        });
        histogramPanel.add(nHistPanel);
        this.nHistPanel.setNullText("Click on a substructure to compare it's distribution to that of the full sequence.");

    }
    Hashtable<RankingKey, ArrayList> rowCache = new Hashtable<>();

    public ArrayList getListFromCache(RankingKey key) {
        ArrayList ret = rowCache.get(key);
        if (ret == null) {
            return new ArrayList();
        }

        return ret;
    }

    class RankingKey {

        Overlay dataOverlay;
        StructureOverlay structureOverlay;
        int pairingParamter;

        public RankingKey(Overlay dataOverlay, StructureOverlay structureOverlay, int pairingParamter) {
            this.dataOverlay = dataOverlay;
            this.structureOverlay = structureOverlay;
            this.pairingParamter = pairingParamter;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final RankingKey other = (RankingKey) obj;
            if (!Objects.equals(this.dataOverlay, other.dataOverlay)) {
                return false;
            }
            if (!Objects.equals(this.structureOverlay, other.structureOverlay)) {
                return false;
            }
            if (this.pairingParamter != other.pairingParamter) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 83 * hash + Objects.hashCode(this.dataOverlay);
            hash = 83 * hash + Objects.hashCode(this.structureOverlay);
            hash = 83 * hash + this.pairingParamter;
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
                // Pair<Overlay, Integer> cacheKey = new Pair((Overlay) dataOverlay1D, new Integer(PAIR_PARAMETER));
                RankingKey key = new RankingKey(dataOverlay1D, structureOverlay, PAIR_PARAMETER);
                ArrayList rows = getListFromCache(key);
                rankingTable.tableDataModel.addRows(rows);
                ArrayList<Substructure> structures = structureOverlay.substructureList.substructures;
                for (int i = rows.size(); running && i < structures.size(); i++) {
                    statusLabel.setText("Ranking (" + (i + 1) + " of " + structures.size() + ")");
                    Substructure structure = structures.get(i);
                    Mapping mapping = structureVisController.getMapping(structureOverlay.mappingSource, dataOverlay1D.mappingSource);
                    //System.out.println("Mapping "+(mapping == null));
                    Ranking ranking = RankingAnalyses.rankSequenceData1D(dataOverlay1D, mapping, structure, structureOverlay.pairedSites, paired, unpaired, i + 1);

                    //System.out.println(ranking.zScore + "\t" + StatUtil.erf(Math.abs(ranking.zScore/2)) + "\t" + StatUtil.erfc(Math.abs(ranking.zScore))+ "\t" + StatUtil.erfcx(Math.abs(ranking.zScore))+ "\t" + StatUtil.getInvCDF(Math.abs(ranking.zScore), true)+"\t"+RankingAnalyses.NormalZ(Math.abs(ranking.zScore))/2);
                    final Object[] row = {new Integer(i + 1), structure.name, new Location(structure.startPosition, structure.startPosition + structure.length), new Integer(structure.length), new Integer(ranking.xN), new Integer(ranking.yN), new Double(ranking.xMean), new Double(ranking.yMean), new Double(ranking.xMedian), new Double(ranking.yMedian), new Double(ranking.mannWhitneyU), new Double(RankingAnalyses.NormalZ(Math.abs(ranking.zScore)) / 2), new Double(ranking.zScore), ranking};

                    rankingTable.tableDataModel.addRow(row);
                    rankingTable.repaint();

                }

                ArrayList<Object[]> clone = new ArrayList<>();
                for (int i = 0; i < rankingTable.tableDataModel.rows.size(); i++) {
                    clone.add(rankingTable.tableDataModel.rows.get(i));
                }
                rowCache.put(key, clone);
            } else if (dataOverlay instanceof DataOverlay2D) {
                DataOverlay2D dataOverlay2D = (DataOverlay2D) dataOverlay;
                //Pair<Overlay, Integer> cacheKey = new Pair((Overlay) dataOverlay2D, new Integer(PAIR_PARAMETER));
                RankingKey key = new RankingKey(dataOverlay2D, structureOverlay, PAIR_PARAMETER);
                ArrayList rows = getListFromCache(key);
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

                double[] fullGenomeValues = RankingAnalyses.toDoubleArray(fullGenomeList);
                NHistogramClass fullHist = new NHistogramClass("Full", Color.red, fullGenomeList);
                fullHist.calculate(dataOverlay2D.minValue, dataOverlay2D.maxValue, 30, dataOverlay2D.dataTransform);
                for (int i = rows.size(); running && i < structures.size(); i++) {
                    statusLabel.setText("Ranking (" + (i + 1) + " of " + structures.size() + ")");
                    Substructure structure = structures.get(i);

                    Ranking ranking;
                    try {
                        ranking = RankingAnalyses.rankSequenceData2D(dataOverlay2D, structureVisController.getMapping(structureOverlay.mappingSource, dataOverlay2D.mappingSource), structure, structureOverlay.pairedSites, paired, unpaired, fullGenomeList, fullGenomeValues, fullHist, i + 1);

                        final Object[] row = {new Integer(i + 1), structure.name, new Location(structure.startPosition, structure.startPosition + structure.length), new Integer(structure.length), new Integer(ranking.xN), new Integer(ranking.yN), new Double(ranking.xMean), new Double(ranking.yMean), new Double(ranking.xMedian), new Double(ranking.yMedian), new Double(ranking.mannWhitneyU), new Double(RankingAnalyses.NormalZ(Math.abs(ranking.zScore)) / 2), new Double(ranking.zScore), ranking};

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
                nHistPanel.setNHistogram(null);
                structureOverlay.loadData();
                currentThread = new RankingThread(dataOverlay, structureOverlay);
                currentThread.setPriority(Thread.MIN_PRIORITY);
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
        pairedOnlyButton = new javax.swing.JRadioButton();
        dataOverlayBox = new javax.swing.JComboBox();
        unpairedOnlyButton = new javax.swing.JRadioButton();
        saveAsCSVButton = new javax.swing.JButton();
        allSitesButton = new javax.swing.JRadioButton();
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

        buttonGroup1.add(pairedOnlyButton);
        pairedOnlyButton.setText("Paired only");
        pairedOnlyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pairedOnlyButtonActionPerformed(evt);
            }
        });

        dataOverlayBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dataOverlayBoxActionPerformed(evt);
            }
        });

        buttonGroup1.add(unpairedOnlyButton);
        unpairedOnlyButton.setText("Unpaired only");
        unpairedOnlyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                unpairedOnlyButtonActionPerformed(evt);
            }
        });

        saveAsCSVButton.setText("Save as CSV");
        saveAsCSVButton.setEnabled(false);
        saveAsCSVButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveAsCSVButtonActionPerformed(evt);
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
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(626, Short.MAX_VALUE)
                .addComponent(statusLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(saveAsCSVButton)
                .addContainerGap())
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 922, Short.MAX_VALUE)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel2Layout.createSequentialGroup()
                                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel1)
                                        .addComponent(jLabel3))
                                    .addGap(18, 18, 18)
                                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                            .addComponent(structureOverlayComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGap(18, 18, 18)
                                            .addComponent(allSitesButton)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(pairedOnlyButton)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(unpairedOnlyButton))
                                        .addComponent(dataOverlayBox, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addComponent(jLabel2))
                            .addGap(0, 0, Short.MAX_VALUE)))
                    .addContainerGap()))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(saveAsCSVButton)
                    .addComponent(statusLabel))
                .addGap(265, 265, 265))
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel3)
                        .addComponent(structureOverlayComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(allSitesButton)
                        .addComponent(unpairedOnlyButton)
                        .addComponent(pairedOnlyButton))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(dataOverlayBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel1))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(jLabel2)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
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
    private javax.swing.JPanel histogramPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JRadioButton pairedOnlyButton;
    private javax.swing.JButton saveAsCSVButton;
    private javax.swing.JLabel statusLabel;
    private javax.swing.JComboBox structureOverlayComboBox;
    private javax.swing.JRadioButton unpairedOnlyButton;
    // End of variables declaration//GEN-END:variables
}
