/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.alignment;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import nava.alignment.AlignmentChartData.ChartType;
import nava.alignment.AlignmentChartData.Marker;
import nava.data.io.FileImport;
import nava.data.io.FileImport.ParserException;
import nava.data.types.DataType.FileFormat;
import nava.data.types.SecondaryStructureData;
import nava.structure.Structure;
import nava.structure.StructureAlign;
import nava.structure.StructureAlign.Method;
import nava.structure.StructureAlign.Region;
import nava.utils.RNAFoldingTools;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class AlignmentEditor extends javax.swing.JPanel implements ActionListener, AlignmentModelListener, AlignmentPanelListener, ListSelectionListener, ChangeListener, ItemListener {

    public static PropertyResourceBundle resources = (PropertyResourceBundle) ResourceBundle.getBundle("resources.text.text");
    AlignmentModel alignmentModel;
    AlignmentNamePanel sequenceNamePanel;
    AlignmentChartPanel chartPanel;
    AlignmentPanel alignmentPanel;
    SettingsPanel settingsPanel;
    SortPanel sortPanel;
    RulerPanel rulerPanel;
    SubstructureTable substructureTable = new SubstructureTable();
    //DefaultListModel<LegendItem> legendListModel;
    // ArrayList<SecondaryStructureItem> secondaryStructureItems;
    public static JFileChooser browseDialog = new JFileChooser();
    public static JFileChooser saveDialog = new JFileChooser();

    /**
     * Creates new form AlignmentEditor
     */
    public AlignmentEditor() {
        initComponents();

        alignmentModel = new AlignmentModel();
        alignmentModel.addAlignmentModelListener(this);

        sequenceNamePanel = new AlignmentNamePanel(alignmentModel);
        chartPanel = new AlignmentChartPanel();
        rulerPanel = new RulerPanel();

        sortPanel = new SortPanel();
        jPanel2.add(sortPanel, BorderLayout.CENTER);
        sortPanel.setModel(alignmentModel);

        settingsPanel = new SettingsPanel();
        settingsPanel.substructureWindowSpinner.addChangeListener(this);
        settingsPanel.similarityCutoffSpinner.addChangeListener(this);
        settingsPanel.relaxedRadioButton.addItemListener(this);
        settingsPanel.strictRadioButton.addItemListener(this);
        settingsPanel.identifyConservedSubstructuresCheckBox.addItemListener(this);
        jPanel4.add(settingsPanel, BorderLayout.CENTER);

        rulerPanelHolder.add(rulerPanel, BorderLayout.CENTER);

        alignmentPanel = new AlignmentPanel(alignmentModel, sequenceNamePanel, chartPanel, rulerPanel);
        alignmentPanel.addAlignmentPanelListener(this);

        namePanelHolder.add(sequenceNamePanel, BorderLayout.CENTER);
        alignmentScrollPane.setViewportView(alignmentPanel);
        chartScrollPane.setViewportView(chartPanel);

        DefaultComboBoxModel<Method> methodComboBoxModel = new DefaultComboBoxModel();
        for (Method method : Method.values()) {
            methodComboBoxModel.addElement(method);
        }
        settingsPanel.methodComboBox.setModel(methodComboBoxModel);
        settingsPanel.methodComboBox.addItemListener(this);

        jPanel3.add(substructureTable, BorderLayout.CENTER);

        rightSplitPane.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY, new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent pce) {
                leftSplitPane.setDividerLocation(rightSplitPane.getDividerLocation());
                //leftSplitPane.revalidate();
            }
        });

        leftSplitPane.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY, new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent pce) {
                rightSplitPane.setDividerLocation(leftSplitPane.getDividerLocation());
                //rightSplitPane.revalidate();
            }
        });

        setupMenu();
        setupInstructions();

        leftSplitPane.setDividerLocation(400);
        rightSplitPane.setDividerLocation(400);

        alignmentModel.setStructuralAlignment(new SecondaryStructureAlignment());

        alignmentModel.sort(AlignmentModel.NOT_SORTED);
    }
    JMenuBar menuBar = new JMenuBar();
    JMenu importMenu = new JMenu("Import");
    JMenuItem importStructureItem = new JMenuItem("Import structure(s)");
    JMenu exportMenu = new JMenu("Export");
    JMenuItem exportChartDataItem = new JMenuItem("Chart data (.csv)");
    JMenuItem exportViennaAlignmentItem = new JMenuItem("Full structural alignment (Vienna dot-bracket format)");
    JMenuItem exportSequenceAlignmentItem = new JMenuItem("Sequence alignment only (FASTA format)");
    JMenuItem exportConservedSubstructuresAlignmentViennaItem = new JMenuItem("Conserved substructures alignment (Vienna dot-bracket format)");
    JMenuItem exportConservedSubstructuresTableItem = new JMenuItem("Conserved substructures table (.csv)");
    JMenu alignMenu = new JMenu("Align");
    JMenuItem alignMAFFTItem = new JMenuItem("Align using MAFFT");
    JMenuItem identifySubtructuresItem = new JMenuItem("Identify conserved substructures");
    JMenu helpMenu = new JMenu("Help");
    JMenuItem aboutItem = new JMenuItem("About");

    public void setupMenu() {
        add(menuBar, BorderLayout.NORTH);

        importMenu.add(importStructureItem);
        importStructureItem.addActionListener(this);

        exportViennaAlignmentItem.addActionListener(this);
        exportMenu.add(exportViennaAlignmentItem);
        
        exportSequenceAlignmentItem.addActionListener(this);
        exportMenu.add(exportSequenceAlignmentItem);

        exportConservedSubstructuresAlignmentViennaItem.addActionListener(this);
        exportMenu.add(exportConservedSubstructuresAlignmentViennaItem);

        exportConservedSubstructuresTableItem.addActionListener(this);
        exportMenu.add(exportConservedSubstructuresTableItem);

        exportChartDataItem.addActionListener(this);
        exportMenu.add(exportChartDataItem);

        alignMAFFTItem.addActionListener(this);
        alignMenu.add(alignMAFFTItem);

        //identifySubtructuresItem.addActionListener(this);
        //alignMenu.add(identifySubtructuresItem);

        aboutItem.addActionListener(this);
        helpMenu.add(aboutItem);

        menuBar.add(importMenu);
        menuBar.add(alignMenu);
        menuBar.add(exportMenu);
        menuBar.add(helpMenu);
    }
    JTextPane instructionsTextPane = new JTextPane();

    public void setupInstructions() {
        instructionsTextPane.setEditable(false);
        instructionsTextPane.setContentType("text/html");
        instructionsTextPane.setText(resources.getString("secondaryStructureComparisonInstructionsText"));
    }

    public static Color getColor(int i, int n) {
        return new Color(Color.HSBtoRGB(((float) i) / ((float) n), 1.0f, 1.0f));
    }

    public void refreshAlignmentChartData() {
        ArrayList<AlignmentChartData> chartDataList = new ArrayList<>();
        Alignment alignment = alignmentModel.getAlignment();
        for (int i = 0; i < alignment.items.size(); i++) {
            for (int j = i + 1; j < alignment.items.size(); j++) {
                SecondaryStructureItem itemi = (SecondaryStructureItem) alignment.items.get(i);
                SecondaryStructureItem itemj = (SecondaryStructureItem) alignment.items.get(j);
                if (itemi.selected && itemj.selected) {

                    int windowSize = (Integer) settingsPanel.substructureWindowSpinner.getValue();
                    double[] sim = StructureAlign.slidingWeightedMountainSimilarity(itemi.getPairedSites(), itemj.getPairedSites(), windowSize, settingsPanel.relaxedRadioButton.isSelected());
                    double[] simOffset = new double[sim.length + windowSize];
                    Arrays.fill(simOffset, Double.MIN_VALUE);
                    for (int k = 0; k < sim.length; k++) {
                        simOffset[k + (windowSize / 2)] = sim[k];
                    }
                    AlignmentChartData chartData = new AlignmentChartData(simOffset, ChartType.DASHED_LINE, itemi.color, itemj.color, null, Marker.NONE);
                    chartDataList.add(chartData);

                    double[] sequenceSim = StructureAlign.slidingWeightedSequenceSimilarity(itemi.getSubItem(0), itemj.getSubItem(0), (Integer) settingsPanel.substructureWindowSpinner.getValue());

                    //System.out.println("OVERALL="+(1 - MountainMetrics.calculateNormalizedWeightedMountainDistance(itemi.getPairedSites(), itemj.getPairedSites())));
                    for (int k = 5; k < 5000; k = k + 5) {
                        // System.out.println(k + "\t" + StructureAlign.slidingWeightedMountainSimilarityAverage(itemi.getPairedSites(), itemj.getPairedSites(), k, settingsPanel.relaxedRadioButton.isSelected()));
                    }
                    //chartDataList.add(new AlignmentChartData(sequenceSim, ChartType.DASHED_LINE, itemi.color, itemj.color, null, Marker.CIRCLE));
                }
            }
        }

        chartPanel.setAlignmentChartData(chartDataList);
        refreshConservedStructuresData(alignment);

    }
    ArrayList<Region> conservedRegions = new ArrayList<>();

    public void refreshConservedStructuresData(Alignment alignment) {
        if (this.settingsPanel.identifyConservedSubstructuresCheckBox.isSelected()) {
            conservedRegions = identifyConservedSubstructures(alignment);
            chartPanel.setHighlightRegions(conservedRegions);
            substructureTable.tableDataModel.clear();
            int id = 1;
            for (Region region : conservedRegions) {
                substructureTable.tableDataModel.addSubstructure(id, new Location(region.startPos + 1, region.startPos + region.length), region.length, region.score);
                id++;
            }
        } else {
            conservedRegions = new ArrayList<Region>();
            substructureTable.tableDataModel.clear();
            chartPanel.setHighlightRegions(conservedRegions);
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

        verticalSplitPane = new javax.swing.JSplitPane();
        leftPanel = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        leftSplitPane = new javax.swing.JSplitPane();
        namePanelHolder = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        rightPanel = new javax.swing.JPanel();
        rulerPanelHolder = new javax.swing.JPanel();
        rightSplitPane = new javax.swing.JSplitPane();
        alignmentScrollPane = new javax.swing.JScrollPane();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        chartScrollPane = new javax.swing.JScrollPane();
        jPanel3 = new javax.swing.JPanel();

        setLayout(new java.awt.BorderLayout());

        verticalSplitPane.setDividerLocation(200);

        leftPanel.setLayout(new java.awt.BorderLayout());

        jPanel2.setMaximumSize(new java.awt.Dimension(32767, 17));
        jPanel2.setMinimumSize(new java.awt.Dimension(10, 17));
        jPanel2.setPreferredSize(new java.awt.Dimension(100, 17));
        jPanel2.setLayout(new java.awt.BorderLayout());
        leftPanel.add(jPanel2, java.awt.BorderLayout.NORTH);

        leftSplitPane.setDividerLocation(400);
        leftSplitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        leftSplitPane.setLastDividerLocation(400);

        namePanelHolder.setLayout(new java.awt.BorderLayout());
        leftSplitPane.setLeftComponent(namePanelHolder);

        jPanel4.setLayout(new java.awt.BorderLayout());
        leftSplitPane.setRightComponent(jPanel4);

        leftPanel.add(leftSplitPane, java.awt.BorderLayout.CENTER);

        verticalSplitPane.setLeftComponent(leftPanel);

        rightPanel.setLayout(new java.awt.BorderLayout());

        rulerPanelHolder.setMaximumSize(new java.awt.Dimension(32767, 17));
        rulerPanelHolder.setMinimumSize(new java.awt.Dimension(10, 17));
        rulerPanelHolder.setPreferredSize(new java.awt.Dimension(100, 17));
        rulerPanelHolder.setLayout(new java.awt.BorderLayout());
        rightPanel.add(rulerPanelHolder, java.awt.BorderLayout.NORTH);

        rightSplitPane.setDividerLocation(400);
        rightSplitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        rightSplitPane.setLastDividerLocation(400);

        alignmentScrollPane.setBorder(null);
        rightSplitPane.setLeftComponent(alignmentScrollPane);

        jPanel1.setLayout(new java.awt.BorderLayout());

        chartScrollPane.setBorder(null);
        chartScrollPane.setPreferredSize(new java.awt.Dimension(100, 150));
        jPanel1.add(chartScrollPane, java.awt.BorderLayout.CENTER);

        jTabbedPane1.addTab("Mountain similarity graph", jPanel1);

        jPanel3.setLayout(new java.awt.BorderLayout());
        jTabbedPane1.addTab("Conserved substructures", jPanel3);

        rightSplitPane.setRightComponent(jTabbedPane1);

        rightPanel.add(rightSplitPane, java.awt.BorderLayout.CENTER);

        verticalSplitPane.setRightComponent(rightPanel);

        add(verticalSplitPane, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane alignmentScrollPane;
    private javax.swing.JScrollPane chartScrollPane;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JPanel leftPanel;
    private javax.swing.JSplitPane leftSplitPane;
    private javax.swing.JPanel namePanelHolder;
    private javax.swing.JPanel rightPanel;
    private javax.swing.JSplitPane rightSplitPane;
    private javax.swing.JPanel rulerPanelHolder;
    private javax.swing.JSplitPane verticalSplitPane;
    // End of variables declaration//GEN-END:variables

    @Override
    public void mouseDraggedOffVisibleRegion(int x, int y) {
        alignmentScrollPane.getHorizontalScrollBar().setValue(alignmentScrollPane.getHorizontalScrollBar().getValue() + x);
        alignmentScrollPane.getVerticalScrollBar().setValue(alignmentScrollPane.getVerticalScrollBar().getValue() + y);
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        
    }

    @Override
    public void alignmentChanged(Alignment alignment) {
        if (alignment.items.size() > 0) {

            for (int i = 0; i < alignment.items.size(); i++) {
                AlignmentItem item = (AlignmentItem) alignment.items.get(i);
                item.setColor(AlignmentEditor.getColor(i, alignment.items.size()));
            }
            refreshAlignmentChartData();
            alignmentScrollPane.setViewportView(alignmentPanel);
        } else {
            rulerPanel.setVisibleRect(null);
            alignmentScrollPane.setViewportView(instructionsTextPane);
        }
    }

    @Override
    public void alignmentSortOrderChanged(int oldOrder, int newOrder) {
    }

    @Override
    public void itemStateDataChanged(AlignmentItem item) {
        refreshAlignmentChartData();
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        if (e.getSource().equals(settingsPanel.substructureWindowSpinner)) {
            refreshAlignmentChartData();
        } else if (e.getSource().equals(settingsPanel.similarityCutoffSpinner)) {
            refreshConservedStructuresData(alignmentModel.getAlignment());
        }
    }

    /*
     * class LegendCellRenderer extends JCheckBox implements ListCellRenderer {
     *
     * public LegendCellRenderer() { setOpaque(true); setLayout(new
     * BorderLayout()); } @Override public Component
     * getListCellRendererComponent( JList list, Object value, int index,
     * boolean isSelected, boolean cellHasFocus) {
     *
     * LegendItem item = (LegendItem) value; LegendItemPanel itemPanel = new
     * LegendItemPanel(); itemPanel.setText(item.text);
     * itemPanel.setColor(item.color); itemPanel.setSelected(item.selected); if
     * (isSelected) {
     *
     * itemPanel.setBackground(list.getSelectionBackground());
     * itemPanel.setForeground(list.getSelectionForeground()); } else {
     * itemPanel.setBackground(list.getBackground());
     * itemPanel.setForeground(list.getForeground()); }
     * this.fireItemStateChanged(new ItemEvent(itemPanel.legendCheckBoxItem, 0,
     * item, item.selected ? ItemEvent.SELECTED : ItemEvent.DESELECTED)); return
     * itemPanel; } }
     *
     * class LegendItem {
     *
     * String text; Color color; boolean selected; AlignmentItem item;
     *
     * public LegendItem(String text, Color color, boolean selected,
     * AlignmentItem item) { this.text = text; this.color = color; this.selected
     * = selected; this.item = item; } }
     */
    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getSource().equals(this.settingsPanel.relaxedRadioButton)) {
            refreshAlignmentChartData();
        } else if (e.getSource().equals(settingsPanel.identifyConservedSubstructuresCheckBox)) {
            refreshConservedStructuresData(alignmentModel.getAlignment());
        } else if (e.getSource().equals(settingsPanel.methodComboBox)) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                refreshConservedStructuresData(alignmentModel.getAlignment());
            }
        }
    }

    public ArrayList<Region> identifyConservedSubstructures(Alignment alignment) {
        int windowSize = (Integer) settingsPanel.substructureWindowSpinnerModel.getValue();
        ArrayList<Structure> alignedStructures = new ArrayList<>();
        ArrayList<String> alignedSequences = new ArrayList<>();
        ArrayList<String> names = new ArrayList<>();

        for (int i = 0; i < alignment.items.size(); i++) {
            SecondaryStructureItem item = (SecondaryStructureItem) alignment.items.get(i);
            if (item.selected) {
                alignedStructures.add(new Structure(RNAFoldingTools.getPairedSitesFromDotBracketString(item.getSubItem(1)), item.name));
                alignedSequences.add(item.getSubItem(0));
                names.add(item.name);
            }
        }
        double cutoff = (Double) settingsPanel.similarityCutoffSpinnerModel.getValue();
        boolean relaxed = settingsPanel.relaxedRadioButton.isSelected();

        if (alignedStructures.size() > 1) {
            return StructureAlign.getConservedStructures(alignedStructures, alignedSequences, names, windowSize, cutoff, relaxed, (Method) settingsPanel.methodComboBox.getSelectedItem());
        }

        return new ArrayList<>();
    }

    public void saveConservedTableDataAsCSV(File csvFile) throws IOException {
        BufferedWriter buffer = new BufferedWriter(new FileWriter(csvFile));
        ArrayList<Object[]> rows = substructureTable.tableDataModel.rows;
        buffer.write("id,location,length,mean similarity\n");
        for (Object[] row : rows) {
            buffer.write(row[0] + "," + row[1] + "," + row[2] + "," + row[3]);
        }
        buffer.close();
    }

    public void saveConservedStructuresAsViennaDotBracketAlignment(Alignment alignment, File dbnFile) throws IOException {
        BufferedWriter buffer = new BufferedWriter(new FileWriter(dbnFile));
        int id = 1;
        for (Region conservedRegion : conservedRegions) {
            for (int i = 0; i < alignment.items.size(); i++) {
                SecondaryStructureItem item = (SecondaryStructureItem) alignment.items.get(i);
                if (item.selected) {
                    buffer.write(">" + (conservedRegion.startPos + 1) + "-" + (conservedRegion.startPos + conservedRegion.length + 1) + ", " + id + ", \"" + item.name + "\", sim=" + conservedRegion.score);
                    id++;
                    buffer.newLine();
                    buffer.write(item.getSubItem(0).substring(conservedRegion.startPos, Math.min(item.getSubItem(0).length(), conservedRegion.startPos + conservedRegion.length)));
                    buffer.newLine();
                    buffer.write(item.getSubItem(1).substring(conservedRegion.startPos, Math.min(item.getSubItem(1).length(), conservedRegion.startPos + conservedRegion.length)));
                    buffer.newLine();
                }
            }
        }
        buffer.close();
    }

    public void saveDataAsCSV(Alignment alignment, File csvFile, boolean selectedOnly) throws IOException {
        BufferedWriter buffer = new BufferedWriter(new FileWriter(csvFile));
        //String [] columnNames = {"Structure 1", "Structure 2", "Sequence 1", "Sequence 2", "Mountain metric similarity (relaxed)", "Mountain metric similarity (strict)"};

        buffer.write("Position,");
        for (int i = 0; i < alignmentModel.maxSequenceLength; i++) {
            buffer.write((i + 1) + "");
            if (i != alignmentModel.maxSequenceLength - 1) {
                buffer.write(",");
            }
        }
        buffer.newLine();

        for (int i = 0; i < alignment.items.size(); i++) {
            for (int j = i + 1; j < alignment.items.size(); j++) {
                SecondaryStructureItem itemi = (SecondaryStructureItem) alignment.items.get(i);
                SecondaryStructureItem itemj = (SecondaryStructureItem) alignment.items.get(j);
                if ((itemi.selected && itemj.selected) || !selectedOnly) {

                    int windowSize = (Integer) settingsPanel.substructureWindowSpinner.getValue();

                    buffer.write("\"sim_relaxed(" + itemi.name + ", " + itemj.name + "),\"");
                    double[] sim = StructureAlign.slidingWeightedMountainSimilarity(itemi.getPairedSites(), itemj.getPairedSites(), windowSize, true);
                    double[] simOffset = new double[sim.length + windowSize];
                    Arrays.fill(simOffset, Double.MIN_VALUE);
                    for (int k = 0; k < sim.length; k++) {
                        simOffset[k + (windowSize / 2)] = sim[k];
                    }
                    for (int k = 0; k < simOffset.length; k++) {
                        if (simOffset[k] != Double.MIN_VALUE) {
                            buffer.write("\\" + simOffset[k] + "\"");
                        }
                        if (k != simOffset.length - 1) {
                            buffer.write(",");
                        }
                    }
                    buffer.newLine();

                    buffer.write("sim_strict(" + itemi.name + ", " + itemj.name + "),");
                    sim = StructureAlign.slidingWeightedMountainSimilarity(itemi.getPairedSites(), itemj.getPairedSites(), windowSize, false);
                    simOffset = new double[sim.length + windowSize];
                    Arrays.fill(simOffset, Double.MIN_VALUE);
                    for (int k = 0; k < sim.length; k++) {
                        simOffset[k + (windowSize / 2)] = sim[k];
                    }
                    for (int k = 0; k < sim.length; k++) {
                        simOffset[k + (windowSize / 2)] = sim[k];
                    }
                    for (int k = 0; k < simOffset.length; k++) {
                        if (simOffset[k] != Double.MIN_VALUE) {
                            buffer.write("\"" + simOffset[k] + "\"");
                        }
                        if (k != simOffset.length - 1) {
                            buffer.write(",");
                        }
                    }
                    buffer.newLine();
                }
            }
        }

        buffer.close();
    }

    public void saveStructuralAlignmentAsViennaDotBracketFormat(Alignment alignment, File dbnFile) throws IOException {
        if (alignment instanceof SecondaryStructureAlignment) {
            BufferedWriter buffer = new BufferedWriter(new FileWriter(dbnFile));
            for (int i = 0; i < alignment.items.size(); i++) {
                SecondaryStructureItem item = (SecondaryStructureItem) alignment.items.get(i);
                buffer.write(">" + item.name);
                buffer.newLine();
                buffer.write(item.getSubItem(0));
                buffer.newLine();
                buffer.write(item.getSubItem(1));
                buffer.newLine();
            }
            buffer.close();
        }
    }
    
    public void saveSequenceAlignment(Alignment alignment, File fastaFile) throws IOException {
        if (alignment instanceof SecondaryStructureAlignment) {
            BufferedWriter buffer = new BufferedWriter(new FileWriter(fastaFile));
            for (int i = 0; i < alignment.items.size(); i++) {
                SecondaryStructureItem item = (SecondaryStructureItem) alignment.items.get(i);
                buffer.write(">" + item.name);
                buffer.newLine();
                buffer.write(item.getSubItem(0));
                buffer.newLine();
            }
            buffer.close();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(this.importStructureItem)) {
            int ret = browseDialog.showOpenDialog(this);
            if (ret == JFileChooser.APPROVE_OPTION) {
                File selectedFile = browseDialog.getSelectedFile();
                ArrayList<FileFormat> formats = FileImport.parsableStructureFormats(selectedFile);
                if (formats.size() > 0) {
                    try {
                        ArrayList<SecondaryStructureData> structures = FileImport.loadStructures(selectedFile, formats.get(0));
                        SecondaryStructureAlignment alignment = null;
                        if (alignmentModel.getAlignment() instanceof SecondaryStructureAlignment) {
                            alignment = (SecondaryStructureAlignment) alignmentModel.getAlignment();
                            for (SecondaryStructureData structure : structures) {
                                alignment.items.add(new SecondaryStructureItem(structure.title, structure.sequence, structure.pairedSites, alignment.items.size()));
                            }
                            alignmentModel.setStructuralAlignment(alignment);
                        }
                    } catch (ParserException ex) {
                        Logger.getLogger(AlignmentEditor.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(AlignmentEditor.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (Exception ex) {
                        Logger.getLogger(AlignmentEditor.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        } else if (e.getSource().equals(this.alignMAFFTItem)) {
            Alignment alignment = alignmentModel.getAlignment();
            if (alignment instanceof SecondaryStructureAlignment) {
                alignmentModel.setStructuralAlignment(SecondaryStructureAlignment.mafftAlign((SecondaryStructureAlignment) alignment));
            }
        } else if (e.getSource().equals(this.exportChartDataItem)) {
            saveDialog.setSelectedFile(new File(saveDialog.getCurrentDirectory().getAbsolutePath() + File.separator + "chart.csv"));
            int ret = saveDialog.showSaveDialog(this);
            if (ret == JFileChooser.APPROVE_OPTION) {
                File selectedFile = saveDialog.getSelectedFile();
                try {
                    saveDataAsCSV(alignmentModel.getAlignment(), selectedFile, false);
                } catch (IOException ex) {
                    Logger.getLogger(AlignmentEditor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else if (e.getSource().equals(this.exportViennaAlignmentItem)) {
            saveDialog.setSelectedFile(new File(saveDialog.getCurrentDirectory().getAbsolutePath() + File.separator + "full-alignment.dbn"));
            int ret = saveDialog.showSaveDialog(this);
            if (ret == JFileChooser.APPROVE_OPTION) {
                File selectedFile = saveDialog.getSelectedFile();
                try {
                    saveStructuralAlignmentAsViennaDotBracketFormat(alignmentModel.getAlignment(), selectedFile);
                } catch (IOException ex) {
                    Logger.getLogger(AlignmentEditor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else if (e.getSource().equals(this.exportSequenceAlignmentItem)) {
            saveDialog.setSelectedFile(new File(saveDialog.getCurrentDirectory().getAbsolutePath() + File.separator + "full-alignment.fas"));
            int ret = saveDialog.showSaveDialog(this);
            if (ret == JFileChooser.APPROVE_OPTION) {
                File selectedFile = saveDialog.getSelectedFile();
                try {
                    saveSequenceAlignment(alignmentModel.getAlignment(), selectedFile);
                } catch (IOException ex) {
                    Logger.getLogger(AlignmentEditor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        else if (e.getSource().equals(this.exportConservedSubstructuresTableItem)) {
            saveDialog.setSelectedFile(new File(saveDialog.getCurrentDirectory().getAbsolutePath() + File.separator + "conserved-structures.csv"));
            int ret = saveDialog.showSaveDialog(this);
            if (ret == JFileChooser.APPROVE_OPTION) {
                File selectedFile = saveDialog.getSelectedFile();
                try {
                    saveConservedTableDataAsCSV(selectedFile);
                } catch (IOException ex) {
                    Logger.getLogger(AlignmentEditor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else if (e.getSource().equals(this.exportConservedSubstructuresAlignmentViennaItem)) {
            saveDialog.setSelectedFile(new File(saveDialog.getCurrentDirectory().getAbsolutePath() + File.separator + "conserved-structures.dbn"));
            int ret = saveDialog.showSaveDialog(this);
            if (ret == JFileChooser.APPROVE_OPTION) {
                File selectedFile = saveDialog.getSelectedFile();
                try {
                    saveConservedStructuresAsViennaDotBracketAlignment(alignmentModel.getAlignment(), selectedFile);
                } catch (IOException ex) {
                    Logger.getLogger(AlignmentEditor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else if (e.getSource().equals(this.aboutItem)) {
            AboutDialog d = new AboutDialog(null, true);
            d.setIconImage(new ImageIcon(ClassLoader.getSystemResource("resources/icons/icon-32x32.png")).getImage());
            final Toolkit toolkit = Toolkit.getDefaultToolkit();
            final Dimension screenSize = toolkit.getScreenSize();
            final int x = (screenSize.width - d.getWidth()) / 2;
            final int y = (screenSize.height - d.getHeight()) / 2;
            d.setLocation(x, y);
            d.setVisible(true);
        } else if (e.getSource().equals(identifySubtructuresItem)) {
            ArrayList<Region> conservedRegions = identifyConservedSubstructures(alignmentModel.getAlignment());
            chartPanel.setHighlightRegions(conservedRegions);
        }
    }
}
