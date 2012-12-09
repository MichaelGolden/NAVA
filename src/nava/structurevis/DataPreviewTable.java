/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.structurevis;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import nava.structurevis.data.DataSource1D;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class DataPreviewTable extends JPanel {

    public TableDataModel tableDataModel;
    final JTable table;
    JScrollPane scrollPane;

    public DataPreviewTable() {
        super(new BorderLayout());

        tableDataModel = new TableDataModel();
        TableSorter sorter = new TableSorter(tableDataModel);
        table = new JTable(sorter);
        table.setDefaultRenderer(Object.class, new ColorRenderer(true));
        sorter.setTableHeader(table.getTableHeader());
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
    }

    class TableDataModel extends AbstractTableModel {

        String[] columnNames = {"Position", "Sequence", "Value"};
        Class[] columnClasses = {Integer.class, String.class, String.class};
        public ArrayList<Object[]> rows = new ArrayList<>();

        public void setDataSource1D(DataSource1D dataSource1D) {
            if (dataSource1D != null) {
                ArrayList<Object[]> rows = new ArrayList<>();
                int j = 1;

                for (int i = 0; i < dataSource1D.dataOffsetCorrected; i++) {
                    if (!dataSource1D.codonPositions || i % 3 == 0) {
                        Object[] row = {-1, "?", dataSource1D.stringData[i] == null ? "" : dataSource1D.stringData[i]};
                        if (dataSource1D.mappingSequence != null && i < dataSource1D.mappingSequence.length()) {
                            row[1] = dataSource1D.mappingSequence.charAt(i) + "";
                        }
                        rows.add(row);
                    }
                }

                for (int i = dataSource1D.dataOffsetCorrected; i < dataSource1D.data.length; i++) {
                    Object[] row = {j, "?", dataSource1D.stringData[i] == null ? "" : dataSource1D.stringData[i]};
                    if (dataSource1D.mappingSequence != null && i < dataSource1D.mappingSequence.length()) {
                        row[1] = dataSource1D.mappingSequence.charAt(i) + "";
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

    public class ColorRenderer extends JLabel
            implements TableCellRenderer {

        Border unselectedBorder = null;
        Border selectedBorder = null;
        boolean isBordered = true;

        public ColorRenderer(boolean isBordered) {
            this.isBordered = isBordered;
            setOpaque(true); //MUST do this for background to show up.
        }

        public Component getTableCellRendererComponent(
                JTable table, Object object,
                boolean isSelected, boolean hasFocus,
                int row, int column) {
            
            if(column == 0)
            {
                System.out.println(((Integer)object).intValue());
            }
            
            if(table.getValueAt(row, 0) == null || ((Integer)table.getValueAt(row, 0)).intValue() == -1)
            {
               // System.out.println("HERE"+((Integer)object).intValue());
                this.setBackground(new Color(255,230,230));
            }
            else            
            if(row % 2 == 0)
            {
                this.setBackground(Color.white);
            }
            else
            {
                this.setBackground(new Color(240,240,240));                
            }
            //this.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.white));
                 
            this.setText(object.toString());
            return this;
        }
    }
}
