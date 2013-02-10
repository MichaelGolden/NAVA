/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.alignment;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
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
import nava.structure.StructureAlign.Region;
import nava.utils.RNAFoldingTools;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class AlignmentEditor extends javax.swing.JPanel implements ActionListener, AlignmentModelListener, AlignmentPanelListener, ListSelectionListener, ChangeListener, ItemListener {

    AlignmentModel alignmentModel;
    AlignmentNamePanel sequenceNamePanel;
    AlignmentChartPanel chartPanel;
    AlignmentPanel alignmentPanel;
    SettingsPanel settingsPanel;
    SortPanel sortPanel;
    RulerPanel rulerPanel;
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
        settingsPanel.relaxedRadioButton.addItemListener(this);
        settingsPanel.strictRadioButton.addItemListener(this);
        jPanel4.add(settingsPanel, BorderLayout.CENTER);

        rulerPanelHolder.add(rulerPanel, BorderLayout.CENTER);
        alignmentPanel = new AlignmentPanel(alignmentModel, sequenceNamePanel, chartPanel, rulerPanel);
        alignmentPanel.addAlignmentPanelListener(this);

        namePanelHolder.add(sequenceNamePanel, BorderLayout.CENTER);
        rightScrollPane.setViewportView(alignmentPanel);
        chartScrollPane.setViewportView(chartPanel);

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


        /*
         * LegendCellRenderer cellRenderer = new LegendCellRenderer();
         * jList1.setCellRenderer(cellRenderer); jList1.addMouseListener(new
         * MouseAdapter() {
         *
         * @Override public void mouseClicked(MouseEvent e) { if
         * (SwingUtilities.isLeftMouseButton(e)) { int index =
         * jList1.locationToIndex(e.getPoint()); LegendItem item = (LegendItem)
         * jList1.getModel().getElementAt(index); item.selected =
         * !item.selected; jList1.repaint(jList1.getCellBounds(index, index));
         * refreshAlignmentChartData(); } } });
         */

        leftSplitPane.setDividerLocation(400);
        rightSplitPane.setDividerLocation(400);

        alignmentModel.setStructuralAlignment(new SecondaryStructureAlignment());

        alignmentModel.sort(AlignmentModel.NOT_SORTED);
    }
    JMenuBar menuBar = new JMenuBar();
    JMenu importMenu = new JMenu("Import");
    JMenuItem importStructureItem = new JMenuItem("Import structure(s)");
    JMenu exportMenu = new JMenu("Export");
    JMenuItem exportChartDataItem = new JMenuItem("Export chart data (.csv)");
    JMenuItem exportViennaAlignmentItem = new JMenuItem("Export structural alignment (Vienna dot-bracket format)");
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

        exportChartDataItem.addActionListener(this);
        exportMenu.add(exportChartDataItem);

        alignMAFFTItem.addActionListener(this);
        alignMenu.add(alignMAFFTItem);

        identifySubtructuresItem.addActionListener(this);
        alignMenu.add(identifySubtructuresItem);

        aboutItem.addActionListener(this);
        helpMenu.add(aboutItem);

        menuBar.add(importMenu);
        menuBar.add(alignMenu);
        menuBar.add(exportMenu);
        menuBar.add(helpMenu);
    }

    /*
     * public void test() { ArrayList<String> sequences = new
     * ArrayList<String>(); ArrayList<String> sequenceNames = new
     * ArrayList<String>(); IO.loadFastaSequences(new
     * File("C:/Users/Michael/Dropbox/HCV fold 2/shape_aligned.fasta"),
     * sequences, sequenceNames);
     *
     * Structure h77structure = StructureAlign.loadStructureFromCtFile(new
     * File("C:/Users/Michael/Dropbox/HCV fold/Genomic
     * models/H77_genomemodel_corrected.ct")); Structure con1structure =
     * StructureAlign.loadStructureFromCtFile(new
     * File("C:/Users/Michael/Dropbox/HCV fold/Genomic
     * models/HCV_Con1bgenome.ct")); Structure jfh1structure =
     * StructureAlign.loadStructureFromCtFile(new
     * File("C:/Users/Michael/Dropbox/HCV fold/Genomic
     * models/JFH1_genomicmodel.ct"));
     *
     * secondaryStructureItems = new ArrayList<>();
     * secondaryStructureItems.add(new SecondaryStructureItem("h77",
     * h77structure.sequence, h77structure.pairedSites, 0));
     * secondaryStructureItems.add(new SecondaryStructureItem("con",
     * con1structure.sequence, con1structure.pairedSites, 1));
     * secondaryStructureItems.add(new SecondaryStructureItem("jfh1",
     * jfh1structure.sequence, jfh1structure.pairedSites, 2));
     *
     * alignmentModel.setStructuralAlignment(new
     * SecondaryStructureAlignment(secondaryStructureItems));
     *
     * legendListModel = new DefaultListModel<>(); for (int i = 0; i <
     * secondaryStructureItems.size(); i++) { legendListModel.addElement(new
     * LegendItem(secondaryStructureItems.get(i).name, getColor(i,
     * secondaryStructureItems.size()), true, secondaryStructureItems.get(i)));
     * } LegendCellRenderer cellRenderer = new LegendCellRenderer();
     * jList1.setCellRenderer(cellRenderer); jList1.setModel(legendListModel);
     * jList1.addMouseListener(new MouseAdapter() {
     *
     * @Override public void mouseClicked(MouseEvent e) { if
     * (SwingUtilities.isLeftMouseButton(e)) { int index =
     * jList1.locationToIndex(e.getPoint()); LegendItem item = (LegendItem)
     * jList1.getModel().getElementAt(index); item.selected = !item.selected;
     * jList1.repaint(jList1.getCellBounds(index, index));
     * refreshAlignmentChartData(); } } });
     *
     * refreshAlignmentChartData(); }
     */
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
                    for (int k = 0; k < simOffset.length && k < sequenceSim.length; k++) {
                        System.out.println(k + "\t"+ simOffset[k]+"\t"+ sequenceSim[k]);
                    }
                    chartDataList.add(new AlignmentChartData(sequenceSim, ChartType.DASHED_LINE, itemi.color, itemj.color, null, Marker.CIRCLE));
                }
            }
        }
        
        chartPanel.setAlignmentChartData(chartDataList);
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
        rightScrollPane = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        chartScrollPane = new javax.swing.JScrollPane();

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

        rightScrollPane.setBorder(null);
        rightSplitPane.setLeftComponent(rightScrollPane);

        jPanel1.setLayout(new java.awt.BorderLayout());

        chartScrollPane.setBorder(null);
        chartScrollPane.setPreferredSize(new java.awt.Dimension(100, 150));
        jPanel1.add(chartScrollPane, java.awt.BorderLayout.CENTER);

        rightSplitPane.setRightComponent(jPanel1);

        rightPanel.add(rightSplitPane, java.awt.BorderLayout.CENTER);

        verticalSplitPane.setRightComponent(rightPanel);

        add(verticalSplitPane, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane chartScrollPane;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel leftPanel;
    private javax.swing.JSplitPane leftSplitPane;
    private javax.swing.JPanel namePanelHolder;
    private javax.swing.JPanel rightPanel;
    private javax.swing.JScrollPane rightScrollPane;
    private javax.swing.JSplitPane rightSplitPane;
    private javax.swing.JPanel rulerPanelHolder;
    private javax.swing.JSplitPane verticalSplitPane;
    // End of variables declaration//GEN-END:variables

    @Override
    public void mouseDraggedOffVisibleRegion(int x, int y) {
        rightScrollPane.getHorizontalScrollBar().setValue(rightScrollPane.getHorizontalScrollBar().getValue() + x);
        rightScrollPane.getVerticalScrollBar().setValue(rightScrollPane.getVerticalScrollBar().getValue() + y);
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        System.out.println(e.getSource());
    }

    @Override
    public void alignmentChanged(Alignment alignment) {
         for (int i = 0; i < alignment.items.size(); i++) {
            AlignmentItem item = (AlignmentItem) alignment.items.get(i);
            item.setColor(AlignmentEditor.getColor(i, alignment.items.size()));
        }
        refreshAlignmentChartData();
        //this.chartPanel.revalidate();
        //this.verticalSplitPane.revalidate();
        //this.rightScrollPane.revalidate();
        System.out.println("Alignment changed");
        this.alignmentPanel.revalidate();
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
        boolean useMinMethod = settingsPanel.useMinMethod.isSelected();

        return StructureAlign.getConservedStructures(alignedStructures, alignedSequences, names, windowSize, cutoff, useMinMethod);
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
            int ret = saveDialog.showSaveDialog(this);
            if (ret == JFileChooser.APPROVE_OPTION) {
                File selectedFile = saveDialog.getSelectedFile();
                try {
                    saveStructuralAlignmentAsViennaDotBracketFormat(alignmentModel.getAlignment(), selectedFile);
                } catch (IOException ex) {
                    Logger.getLogger(AlignmentEditor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else if (e.getSource().equals(this.aboutItem)) {
            System.out.println("About item");
        } else if (e.getSource().equals(identifySubtructuresItem)) {
            ArrayList<Region> conservedRegions = identifyConservedSubstructures(alignmentModel.getAlignment());
            System.out.println(conservedRegions);
            chartPanel.setHighlightRegions(conservedRegions);
        }
    }
}
