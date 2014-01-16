/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.structurevis;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import nava.structurevis.data.DataOverlay1D;
import nava.ui.ProjectModel;
import nava.utils.TableSorter;
import nava.utils.Utils;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class DataPreviewTable extends JPanel {

    public TableDataModel tableDataModel;
    public ColorRenderer tableRenderer;
    final JTable table;
    JScrollPane scrollPane;

    public DataPreviewTable() {
        super(new BorderLayout());

        tableDataModel = new TableDataModel();
       TableSorter sorter = new TableSorter(tableDataModel);
        table = new JTable(sorter);
        tableRenderer = new ColorRenderer(true);
        table.setDefaultRenderer(Object.class, tableRenderer);
        //sorter.setTableHeader(table.getTableHeader());
        // sorter.sortOnColumn(table.getTableHeader(),table.getColumnCount()-1,-1);
        table.setFillsViewportHeight(true);
        table.addMouseListener(new MouseAdapter() {

            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                }
            }
        });

        scrollPane = new JScrollPane(table);
        add(scrollPane);
        
        
        table.getColumnModel().getColumn(3).setWidth(table.getRowHeight());
        table.getColumnModel().getColumn(3).setPreferredWidth(table.getRowHeight());
        table.getColumnModel().getColumn(3).setMinWidth(table.getRowHeight());
        table.getColumnModel().getColumn(3).setMaxWidth(table.getRowHeight());
        
        
          table.getColumnModel().getColumn(0).setWidth(40);
        table.getColumnModel().getColumn(0).setPreferredWidth(40);
        
          table.getColumnModel().getColumn(1).setWidth(table.getRowHeight()+4);
        table.getColumnModel().getColumn(1).setPreferredWidth(table.getRowHeight()+4);
        
        
      
    }

    class TableDataModel extends AbstractTableModel {

        String[] columnNames = {"Position", "Mapping sequence", "Value", ""};
        Class[] columnClasses = {String.class, String.class, String.class, String.class};
        public ArrayList<Object[]> rows = new ArrayList<>();
        DataOverlay1D dataOverlay1D;
        
        public void setDataSource1D(DataOverlay1D dataOverlay1D, ProjectModel projectModel) {
            if (dataOverlay1D != null) {
                this.dataOverlay1D = dataOverlay1D;
                this.dataOverlay1D.loadData();
                ArrayList<Object[]> rows = new ArrayList<>();
                int j = 1;

                String mappingSequence = dataOverlay1D.mappingSource.getRepresentativeSequence(projectModel);
        
                for (int i = 0; i < dataOverlay1D.dataOffsetCorrected; i++) {
                    if ((!dataOverlay1D.codonPositions || i % 3 == 0) && i < dataOverlay1D.stringData.length) {
                        Object[] row = {"", "", dataOverlay1D.stringData[i] == null ? "" : dataOverlay1D.stringData[i],""};
                        if (mappingSequence != null && i < mappingSequence.length()) {
                            //row[1] = mappingSequence.charAt(i) + "";
                        }
                        
                        rows.add(row);
                        
                    }
                }

                for (int i = dataOverlay1D.dataOffsetCorrected; i < dataOverlay1D.data.length; i++) {
                    Object[] row = {j, "?", dataOverlay1D.stringData[i] == null ? "" : dataOverlay1D.stringData[i],""};
                    if (mappingSequence != null && j-1< mappingSequence.length()) {
                        row[1] = mappingSequence.charAt(j-1) + "";
                    }
                    
                    if(dataOverlay1D.used[j-1])
                    {                        
                        double p = dataOverlay1D.data[j-1];
                        if(!dataOverlay1D.excludeValuesOutOfRange)
                        {
                            // in range
                        }
                        else
                        if (((!dataOverlay1D.useLowerThreshold || p >= dataOverlay1D.thresholdMin) && (!dataOverlay1D.useUpperThreshold || p <= dataOverlay1D.thresholdMax))) 
                        {
                            // in range
                        }
                        else
                        {
                            row[3]= "-";
                        }
                    }
                    else
                    {
                        row[3]= "-";
                    }
                    //addRow(row);
                    rows.add(row);
                    j++;
                }
                clear();
                addRows(rows);
            }
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public int getRowCount() {
            return rows.size();
        }

        @Override
        public String getColumnName(int col) {
            return columnNames[col];
        }

        @Override
        public Object getValueAt(int row, int col) {
            return rows.get(row)[col];
        }

        public void clear() {
            rows.clear();
            fireTableRowsDeleted(0, rows.size());
        }

        public void addRows(ArrayList rowsArray) {
            int start = rows.size() + 1;
            int end = rows.size() + rowsArray.size();
            rows.addAll(rowsArray);
            fireTableRowsInserted(start, end);
        }

        public void addRow(Object[] row) {
            rows.add(row);
            fireTableRowsInserted(rows.size(), rows.size());
        }

        public void removeRow(int index) {
            rows.remove(index);
            fireTableRowsDeleted(index, index);
        }

        public void removeRows(int[] indices) {
            Arrays.sort(indices);
            int correction = 0;
            for (int i = 0; i < indices.length; i++) {
                removeRow(indices[i] - correction);
                correction++;
            }
        }

        public void move(int index, int value) {

            Object[] array = rows.remove(index);
            rows.add(index + value, array);
            /*
             * if(value < 0) { rows.add(index+value, array); } else {
             *
             * }
             */
            fireTableDataChanged();
        }

        /*
         * JTable uses this method to determine the default renderer/ editor for
         * each cell. If we didn't implement this method, then the last column
         * would contain text ("true"/"false"), rather than a check box.
         */
        @Override
        public Class getColumnClass(int c) {
            return columnClasses[c];
        }

        /*
         * Don't need to implement this method unless your table's editable.
         */
        public boolean isCellEditable(int row, int col) {
            return false;
        }

        /*
         * Don't need to implement this method unless your table's data can
         * change.
         */
        public void setValueAt(Object value, int row, int col) {
            rows.get(row)[col] = value;
            fireTableCellUpdated(row, col);
        }
    }

    //Color headerColor = new Color(210,230,230);
    Color headerColor = new Color(220,244,235);
    public class ColorRenderer extends JLabel
            implements TableCellRenderer {

        Border unselectedBorder = null;
        Border selectedBorder = null;
        boolean isBordered = true;
        Font plainFont;
        Font boldFont;
        DataOverlay1D dataOverlay1D;
        
        
        public ColorRenderer(boolean isBordered) {
            this.isBordered = isBordered;
            setOpaque(true); //MUST do this for background to show up.
            plainFont = this.getFont();
            boldFont = plainFont.deriveFont(Font.BOLD);
        }
        
        public void setDataOverlay1D(DataOverlay1D dataOverlay1D)
        {
            this.dataOverlay1D = dataOverlay1D;
        }

        public Component getTableCellRendererComponent(
                JTable table, Object object,
                boolean isSelected, boolean hasFocus,
                int row, int column) {
            
            this.setFont(plainFont);
            String posValue = table.getValueAt(row, 0).toString();
            if(table.getValueAt(row, 0) == null || !Utils.isInteger(posValue))
            {
               // System.out.println("HERE"+((Integer)object).intValue());
                this.setBackground(headerColor);
                this.setFont(boldFont);
            }
            else            
            if(row % 2 == 0)
            {
                
                if(column == 3)
                {
                    this.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.white));
                }
                else
                {
                    this.setBorder(BorderFactory.createEmptyBorder());
                }
                this.setBackground(Color.white);
            }
            else
            {
                this.setBackground(new Color(240,240,240));   
                if(column == 3)
                {
                    this.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(240,240,240)));
                }
                else
                {
                    this.setBorder(BorderFactory.createEmptyBorder());
                }                
            }
            
            if(Utils.isInteger(posValue) && column == 3)
            {
                this.setHorizontalAlignment(SwingConstants.CENTER);
                int pos = Integer.parseInt(posValue)-1;

                double p = dataOverlay1D.data[pos];
                
                Color c = dataOverlay1D.colorGradient.getColor(dataOverlay1D.dataTransform.transform((float) p));            
                if(dataOverlay1D.used[pos])
                {
                    if(dataOverlay1D.excludeValuesOutOfRange)
                    {
                        if (((!dataOverlay1D.useLowerThreshold || p >= dataOverlay1D.thresholdMin) && (!dataOverlay1D.useUpperThreshold || p <= dataOverlay1D.thresholdMax))) 
                        {

                            this.setBackground(c);
                        }
                    }
                    else
                    {
                        this.setBackground(c);
                    }
                }
            }
            else
            {         
                this.setHorizontalAlignment(SwingConstants.LEFT);
            }
                 
            this.setText(object.toString());
            return this;
        }
    }
}
