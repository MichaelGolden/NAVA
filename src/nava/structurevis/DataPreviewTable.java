/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.structurevis;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.table.AbstractTableModel;
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
        //table.setModel(tableDataModel);
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
        
        public void setDataSource1D(DataSource1D dataSource1D)
        {
            ArrayList<Object[]> rows = new ArrayList<Object[]>();
            for(int i = 0 ; i < dataSource1D.data.length ; i++)
            {
                Object[] row = {new Integer(i+1),"A",dataSource1D.data[i]+""};
                //addRow(row);
                rows.add(row);
            }
            clear();
            addRows(rows);
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
}
