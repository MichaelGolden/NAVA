/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.alignment;

import nava.utils.TableSorter;
import nava.ui.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import nava.tasks.*;
import nava.tasks.Task.Status;

/**
 *
 * @author Michael
 */
public class SubstructureTable extends JPanel {

    public TableDataModel tableDataModel = new TableDataModel();
    final JTable table;
    public JScrollPane scrollPane;

    public SubstructureTable() {
        super(new BorderLayout());

        TableSorter sorter = new TableSorter(tableDataModel);
        table = new JTable(sorter);
        table.setRowHeight(20);
        sorter.setTableHeader(table.getTableHeader());
        sorter.sortOnColumn(table.getTableHeader(), 1, 1);
        table.setFillsViewportHeight(true);
        table.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    JTable target = (JTable) e.getSource();
                    int row = target.getSelectedRow();
                    int column = target.getSelectedColumn();

                }
            }
        });

        //table.getColumnModel().getColumn(4).setCellRenderer(new ProgressBarTableCellRenderer());

        scrollPane = new JScrollPane(table);
        add(scrollPane);

    }

    class TableDataModel extends AbstractTableModel {
        String[] columnNames = {"#", "Location", "Length", "Score"};
        Class[] columnClasses = {Integer.class, Location.class, String.class, Double.class};
        public ArrayList<Object[]> rows = new ArrayList<>();

        public TableDataModel() {
            
        }

       

        public int getColumnCount() {
            return columnNames.length;
        }

        public int getRowCount() {
            return rows.size();
        }

        @Override
        public String getColumnName(int col) {
            return columnNames[col];
        }

        public Object getValueAt(int row, int col) {
            return rows.get(row)[col];
        }
        
        /*
        public Task getTask(int row)
        {
            if(row >= 0 && row < rows.size())
            {
                return (Task) rows.get(row)[4];
            }
            return null;
        }*/

        public void setValueAt(Object value, int row, int col) {
            rows.get(row)[col] = value;
            int selected = SubstructureTable.this.table.getSelectedRow();
            this.fireTableCellUpdated(row, col);
            if(selected != -1)
            {
                SubstructureTable.this.table.setRowSelectionInterval(selected, selected);
            }
        }
        public void clear() {
            rows.clear();
            fireTableRowsDeleted(0, rows.size());
        }

        public void addSubstructure(int i, Location location, int length, double score) {
            Object[] row = {i, location, length, score};
            tableDataModel.addRow(row);
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
        public Class getColumnClass(int c) {
            return columnClasses[c];
        }

        /*
         * Don't need to implement this method unless your table's editable.
         */
        public boolean isCellEditable(int row, int col) {
            return false;
        }
    }

    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("SimpleTableDemo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create and set up the content pane.
        SubstructureTable newContentPane = new SubstructureTable();
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    public class ProgressBarTableCellRenderer extends DefaultTableCellRenderer {

        private static final long serialVersionUID = 1L;
        JProgressBar bar = new JProgressBar();

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {

            // TODO try get bar to display blue background when selected, code below is not working.            
            if (isSelected) {
                bar.setBackground(table.getSelectionBackground());
            } else {
                bar.setBackground(table.getBackground());
            }
            
            Task task = (Task) value;
            bar.setString(task.getStatus().toString());
            bar.setStringPainted(true);
            bar.setValue((int) (task.getProgress() * 100));

            return bar;
        }
    }

    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                createAndShowGUI();
            }
        });
    }
}
