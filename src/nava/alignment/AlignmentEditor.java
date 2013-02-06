/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.alignment;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import nava.alignment.AlignmentChartData.ChartType;
import nava.data.io.IO;
import nava.data.types.AlignmentData;
import nava.structure.Structure;
import nava.structure.StructureAlign;
import nava.tasks.applications.Application;
import nava.ui.ApplicationPanelItem;
import nava.utils.RNAFoldingTools;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class AlignmentEditor extends javax.swing.JPanel implements AlignmentPanelListener, ListSelectionListener {

    AlignmentModel alignmentModel;
    AlignmentNamePanel sequenceNamePanel;
    AlignmentChartPanel chartPanel;
    AlignmentPanel alignmentPanel;
    DefaultListModel<LegendItem> legendListModel;
    ArrayList<SecondaryStructureItem> secondaryStructureItems;

    /**
     * Creates new form AlignmentEditor
     */
    public AlignmentEditor() {
        initComponents();


        /*
         * AlignmentModel sequenceModel = new AlignmentModel(); AlignmentData al
         * = new AlignmentData(); IO.loadFastaSequences(new
         * File("examples/alignments/hiv500.fas"), al.sequences,
         * al.sequenceNames);
         *
         *
         * //sequenceModel.setAlignment(al); ArrayList<AlignmentItem> items =
         * new ArrayList<>(); for (int i = 0; i < al.sequenceNames.size(); i++)
         * { ArrayList<String> subItems = new ArrayList<>(); ArrayList<String>
         * subItemNames = new ArrayList<>(); subItems.add(al.sequences.get(i));
         * if (i % 1 == 0) {
         * subItems.add("(((....)..)).................((...((..)))...)"); }
         * items.add(new AlignmentItem(al.sequenceNames.get(i), subItems,
         * subItemNames, i)); } Alignment alignment = new Alignment(items);
         */
        //sequenceModel.setAlignment(alignment);

        alignmentModel = new AlignmentModel();

        sequenceNamePanel = new AlignmentNamePanel(alignmentModel);
        chartPanel = new AlignmentChartPanel();
        alignmentPanel = new AlignmentPanel(alignmentModel, sequenceNamePanel, chartPanel);
        alignmentPanel.addAlignmentPanelListener(this);

        namePanelHolder.add(sequenceNamePanel, BorderLayout.CENTER);
        rightScrollPane.setViewportView(alignmentPanel);
        chartScrollPane.setViewportView(chartPanel);

        /*
         * double[] values = new double[10000]; for (int i = 0; i <
         * values.length; i++) { values[i] = Math.sin(((double) (i % 100) /
         * 100.0) * Math.PI); } chartPanel.setAlignmentChartData(new
         * AlignmentChartData(values, Color.black, Color.blue, ChartType.LINE));
         * ;
         */

        test();

        alignmentModel.sort(AlignmentModel.NOT_SORTED);
        alignmentPanel.repaint();
    }

    public void test() {
        ArrayList<String> sequences = new ArrayList<String>();
        ArrayList<String> sequenceNames = new ArrayList<String>();
        IO.loadFastaSequences(new File("C:/Users/Michael/Dropbox/HCV fold 2/shape_aligned.fasta"), sequences, sequenceNames);

        Structure h77structure = StructureAlign.loadStructureFromCtFile(new File("C:/Users/Michael/Dropbox/HCV fold/Genomic models/H77_genomemodel_corrected.ct"));
        Structure con1structure = StructureAlign.loadStructureFromCtFile(new File("C:/Users/Michael/Dropbox/HCV fold/Genomic models/HCV_Con1bgenome.ct"));
        Structure jfh1structure = StructureAlign.loadStructureFromCtFile(new File("C:/Users/Michael/Dropbox/HCV fold/Genomic models/JFH1_genomicmodel.ct"));

        String h77alignedseq = StructureAlign.findAlignedSequence(sequences, h77structure.sequence);
        String con1alignedseq = StructureAlign.findAlignedSequence(sequences, con1structure.sequence);
        String jfh1alignedseq = StructureAlign.findAlignedSequence(sequences, jfh1structure.sequence);

        String h77alignedstructure = StructureAlign.mapStringToAlignedSequence(RNAFoldingTools.getDotBracketStringFromPairedSites(h77structure.pairedSites), h77alignedseq, "-");
        String con1alignedstructure = StructureAlign.mapStringToAlignedSequence(RNAFoldingTools.getDotBracketStringFromPairedSites(con1structure.pairedSites), con1alignedseq, "-");
        String jfh1alignedstructure = StructureAlign.mapStringToAlignedSequence(RNAFoldingTools.getDotBracketStringFromPairedSites(jfh1structure.pairedSites), jfh1alignedseq, "-");

        int[] pairedSitesH77 = RNAFoldingTools.getPairedSitesFromDotBracketString(h77alignedstructure);
        int[] pairedSitesCon = RNAFoldingTools.getPairedSitesFromDotBracketString(con1alignedstructure);
        int[] pairedSitesJFH1 = RNAFoldingTools.getPairedSitesFromDotBracketString(jfh1alignedstructure);

        secondaryStructureItems = new ArrayList<>();
        secondaryStructureItems.add(new SecondaryStructureItem("h77", h77alignedseq, pairedSitesH77, 0));
        secondaryStructureItems.add(new SecondaryStructureItem("con", con1alignedseq, pairedSitesCon, 1));
        secondaryStructureItems.add(new SecondaryStructureItem("jfh1", jfh1alignedseq, pairedSitesJFH1, 2));

        alignmentModel.setStructuralAlignment(new SecondaryStructureAlignment(secondaryStructureItems));
        Color[] colors = {Color.red, Color.green, Color.blue};

        legendListModel = new DefaultListModel<>();
        for (int i = 0; i < secondaryStructureItems.size(); i++) {
            legendListModel.addElement(new LegendItem(secondaryStructureItems.get(i).name, colors[i], true, secondaryStructureItems.get(i)));
        }
        LegendCellRenderer cellRenderer = new LegendCellRenderer();
        jList1.setCellRenderer(cellRenderer);
        jList1.setModel(legendListModel);
        jList1.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                int index = jList1.locationToIndex(e.getPoint());
                LegendItem item = (LegendItem) jList1.getModel().getElementAt(index);
                item.selected = !item.selected;
                jList1.repaint(jList1.getCellBounds(index, index));
                refreshAlignmentChartData();
            }
        });

        refreshAlignmentChartData();

    }
    
    public void refreshAlignmentChartData()
    {
         ArrayList<AlignmentChartData> chartDataList = new ArrayList<>();
        for (int i = 0; i < legendListModel.size(); i++) {
            for (int j = i + 1; j < legendListModel.size(); j++) {
                if (legendListModel.get(i).selected && legendListModel.get(j).selected) {
                    double[] simStrict = StructureAlign.slidingWeightedMountainSimilarity(secondaryStructureItems.get(i).getPairedSites(), secondaryStructureItems.get(j).getPairedSites(), 75, false);
                    AlignmentChartData dataStrict = new AlignmentChartData(simStrict, ChartType.DASHED_LINE, legendListModel.get(i).color, legendListModel.get(j).color, null);
                    int windowSize = 75;
                    double[] simRelaxed = StructureAlign.slidingWeightedMountainSimilarity(secondaryStructureItems.get(i).getPairedSites(), secondaryStructureItems.get(j).getPairedSites(), windowSize, true);
                    double [] simRelaxedOffset = new double[simRelaxed.length+75];
                    Arrays.fill(simRelaxedOffset, Double.MIN_VALUE);
                    for(int k = 0 ; k < simRelaxed.length; k++)
                    {
                        simRelaxedOffset[k+(windowSize/2)] = simRelaxed[k];
                    }
                    AlignmentChartData dataRelaxed = new AlignmentChartData(simRelaxedOffset, ChartType.DASHED_LINE, legendListModel.get(i).color, legendListModel.get(j).color, null);
                    //chartDataList.add(dataStrict);                    
                    chartDataList.add(dataRelaxed);
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

        jSplitPane1 = new javax.swing.JSplitPane();
        leftPanel = new javax.swing.JPanel();
        namePanelHolder = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        rightPanel = new javax.swing.JPanel();
        rightScrollPane = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        chartScrollPane = new javax.swing.JScrollPane();

        setLayout(new java.awt.BorderLayout());

        jSplitPane1.setDividerLocation(80);

        namePanelHolder.setLayout(new java.awt.BorderLayout());

        jPanel3.setLayout(new java.awt.BorderLayout());

        jList1.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(jList1);

        jPanel3.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        javax.swing.GroupLayout leftPanelLayout = new javax.swing.GroupLayout(leftPanel);
        leftPanel.setLayout(leftPanelLayout);
        leftPanelLayout.setHorizontalGroup(
            leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(namePanelHolder, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, 79, Short.MAX_VALUE)
        );
        leftPanelLayout.setVerticalGroup(
            leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(leftPanelLayout.createSequentialGroup()
                .addComponent(namePanelHolder, javax.swing.GroupLayout.DEFAULT_SIZE, 142, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 0, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jSplitPane1.setLeftComponent(leftPanel);

        rightScrollPane.setBorder(null);

        jPanel1.setLayout(new java.awt.BorderLayout());

        chartScrollPane.setBorder(null);
        jPanel1.add(chartScrollPane, java.awt.BorderLayout.CENTER);

        javax.swing.GroupLayout rightPanelLayout = new javax.swing.GroupLayout(rightPanel);
        rightPanel.setLayout(rightPanelLayout);
        rightPanelLayout.setHorizontalGroup(
            rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(rightScrollPane)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 314, Short.MAX_VALUE)
        );
        rightPanelLayout.setVerticalGroup(
            rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(rightPanelLayout.createSequentialGroup()
                .addComponent(rightScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 142, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 0, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jSplitPane1.setRightComponent(rightPanel);

        add(jSplitPane1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane chartScrollPane;
    private javax.swing.JList jList1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JPanel leftPanel;
    private javax.swing.JPanel namePanelHolder;
    private javax.swing.JPanel rightPanel;
    private javax.swing.JScrollPane rightScrollPane;
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

    class LegendCellRenderer extends JCheckBox
            implements ListCellRenderer {

        public LegendCellRenderer() {
            setOpaque(true);
            setLayout(new BorderLayout());
        }

        /*
         * This method finds the image and text corresponding to the selected
         * value and returns the label, set up to display the text and image.
         */
        @Override
        public Component getListCellRendererComponent(
                JList list,
                Object value,
                int index,
                boolean isSelected,
                boolean cellHasFocus) {

            LegendItem item = (LegendItem) value;
            LegendItemPanel itemPanel = new LegendItemPanel();
            itemPanel.setText(item.text);
            itemPanel.setColor(item.color);
            itemPanel.setSelected(item.selected);
            if (isSelected) {

                itemPanel.setBackground(list.getSelectionBackground());
                itemPanel.setForeground(list.getSelectionForeground());
            } else {
                itemPanel.setBackground(list.getBackground());
                itemPanel.setForeground(list.getForeground());
            }
            this.fireItemStateChanged(new ItemEvent(itemPanel.legendCheckBoxItem, 0, item, item.selected ? ItemEvent.SELECTED : ItemEvent.DESELECTED));
            return itemPanel;
        }
    }

    class LegendItem {

        String text;
        Color color;
        boolean selected;
        SecondaryStructureItem item;

        public LegendItem(String text, Color color, boolean selected, SecondaryStructureItem item) {
            this.text = text;
            this.color = color;
            this.selected = selected;
            this.item = item;
        }
    }
}