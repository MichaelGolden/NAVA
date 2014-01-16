/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.structurevis;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EventObject;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import nava.ranking.Location;
import nava.structurevis.data.SubstructureList;
import nava.utils.Mapping;
import nava.utils.TableCellListener;
import nava.utils.TableSorter;

/**
 *
 * @author Michael
 */
public class SubstructureListTable extends JPanel implements ChangeListener {

    JSpinner minSpinner = new JSpinner();
    JSpinner maxSpinner = new JSpinner();
    SpinnerNumberModel minSpinnerModel = new SpinnerNumberModel(10, 1, Integer.MAX_VALUE, 1);
    SpinnerNumberModel maxSpinnerModel = new SpinnerNumberModel(250, 0, Integer.MAX_VALUE, 1);
    public TableDataModel tableDataModel;
    final JTable table;
    public JScrollPane scrollPane;

    public SubstructureListTable() {
        super(new BorderLayout());

        tableDataModel = new TableDataModel();
        TableSorter sorter = new TableSorter(tableDataModel);
        table = new JTable(sorter);
        sorter.setTableHeader(table.getTableHeader());
        sorter.sortOnColumn(table.getTableHeader(), 0, 1);
        table.setFillsViewportHeight(true);
        table.addMouseListener(new MouseAdapter() {

            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    JTable target = (JTable) e.getSource();
                    int row = target.getSelectedRow();
                    int column = target.getSelectedColumn();

                    int s = ((Integer) table.getModel().getValueAt(row, 0)).intValue() - 1;
                    // TODO fire an event here to open substructure
                }
            }
        });


        Action action = new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                TableCellListener tcl = (TableCellListener) e.getSource();
                /*
                 * System.out.println("Row : " + tcl.getRow());
                 * System.out.println("Column: " + tcl.getColumn());
                 * System.out.println("Old : " + tcl.getOldValue());
                 * System.out.println("New : " + tcl.getNewValue());
                 */

                int i = tcl.getRow();
                int j = tcl.getColumn();
                if (i >= 0 && j >= 2 && j <= 3) {
                    Location loc = new Location((Integer) tableDataModel.getValueAt(i, 2), (Integer) tableDataModel.getValueAt(i, 3));
                    tableDataModel.setValueAt(loc, i, 4);
                }
            }
        };

        TableCellListener tcl = new TableCellListener(table, action);

        minSpinner.setModel(minSpinnerModel);
        maxSpinner.setModel(maxSpinnerModel);
        table.getColumnModel().getColumn(2).setCellEditor(new SpinnerEditor(minSpinner));
        table.getColumnModel().getColumn(3).setCellEditor(new SpinnerEditor(maxSpinner));
        minSpinner.addChangeListener(this);
        maxSpinner.addChangeListener(this);

        scrollPane = new JScrollPane(table);
        add(scrollPane);
    }

    @Override
    public void stateChanged(ChangeEvent e) {
    }

    class TableDataModel extends AbstractTableModel {

        String[] columnNames = {"Structure #", "Structure ID", "Start position", "End position", "Location", "Structure Length"};
        Class[] columnClasses = {Integer.class, String.class, Integer.class, Integer.class, Location.class, Integer.class};
        
        SubstructureList substructureList = null;
        
        //public ArrayList<Object[]> rows = new ArrayList<>();
        
        public void setSubtructureList(SubstructureList substructureList)
        {
            this.substructureList = substructureList;
            this.fireTableDataChanged();
        }

        public int getColumnCount() {
            return columnNames.length;
        }

        public int getRowCount() {
            if(substructureList == null)
            {
                return 0;
            }
            return substructureList.substructures.size();
            //return rows.size();
        }

        @Override
        public String getColumnName(int col) {
            return columnNames[col];
        }

        public Object getValueAt(int row, int col) {
            switch(col)
            {
                case 0:
                    return row;
                case 1:
                    return row+"";
                case 2:
                    return substructureList.substructures.get(row).startPosition+1;
                case 3:
                    return substructureList.substructures.get(row).startPosition+substructureList.substructures.get(row).length;
                case 4:
                    return new Location(substructureList.substructures.get(row).startPosition+1, substructureList.substructures.get(row).startPosition+substructureList.substructures.get(row).length);
                case 5:
                    return substructureList.substructures.get(row).length;
                default:
                    return null;                            
            }
        }
        boolean hasMappedData = false;

        public void clear() {
            int rows = substructureList.substructures.size();
            substructureList.substructures.clear();
            fireTableRowsDeleted(0,rows);
        }

        public void addRows(ArrayList rowsArray) {
            //int start = rows.size() + 1;
           // int end = rows.size() + rowsArray.size();
            //rows.addAll(rowsArray);
            //fireTableRowsInserted(start, end);
        }

        public void addRow(Object[] row) {
            //rows.add(row);
            //fireTableRowsInserted(rows.size(), rows.size());
        }

        public void removeRow(int index) {
           // rows.remove(index);
            //fireTableRowsDeleted(index, index);
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

           // Object[] array = rows.remove(index);
            //rows.add(index + value, array);
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
            if (col == 2 || col == 3) {
                return false;
            }
            return false;
        }

        /*
         * Don't need to implement this method unless your table's data can
         * change.
         */
        public void setValueAt(Object value, int row, int col) {
            //rows.get(row)[col] = value;
            //fireTableCellUpdated(row, col);
        }
    }

    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("SimpleTableDemo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create and set up the content pane.
        SubstructureListTable newContentPane = new SubstructureListTable();
        //Integer.class, String.class, Integer.class, Integer.class, Location.class, Integer.class
        Object[] structure = {1, "1", 100, 200, new Location(100, 200), 100};
        newContentPane.tableDataModel.addRow(structure);
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
                createAndShowGUI();
            }
        });
    }

    public class SpinnerEditor extends DefaultCellEditor {

        JSpinner spinner;
        JSpinner.DefaultEditor editor;
        JTextField textField;
        boolean valueSet;
        //SpinnerNumberModel spinnerModel;

        // Initializes the spinner.
        public SpinnerEditor(JSpinner spinner) {
            super(new JTextField());
            this.spinner = spinner;
            editor = ((JSpinner.DefaultEditor) spinner.getEditor());
            textField = editor.getTextField();
            textField.addFocusListener(new FocusListener() {

                public void focusGained(FocusEvent fe) {
                    System.err.println("Got focus");
                    //textField.setSelectionStart(0);
                    //textField.setSelectionEnd(1);
                    SwingUtilities.invokeLater(new Runnable() {

                        public void run() {
                            if (valueSet) {
                                textField.setCaretPosition(1);
                            }
                        }
                    });
                }

                public void focusLost(FocusEvent fe) {
                }
            });
            textField.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent ae) {

                    stopCellEditing();
                }
            });
        }

        // Prepares the spinner component and returns it.
        public Component getTableCellEditorComponent(
                JTable table, Object value, boolean isSelected, int row, int column) {
            if (!valueSet) {
                spinner.setValue(value);
            }
            SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                    textField.requestFocus();
                }
            });
            return spinner;
        }

        public boolean isCellEditable(EventObject eo) {
            System.err.println("isCellEditable");
            if (eo instanceof KeyEvent) {
                KeyEvent ke = (KeyEvent) eo;
                System.err.println("key event: " + ke.getKeyChar());
                textField.setText(String.valueOf(ke.getKeyChar()));
                //textField.select(1,1);
                //textField.setCaretPosition(1);
                //textField.moveCaretPosition(1);
                valueSet = true;
            } else {
                valueSet = false;
            }
            return true;
        }

        // Returns the spinners current value.
        public Object getCellEditorValue() {
            return spinner.getValue();
        }

        public boolean stopCellEditing() {
            System.err.println("Stopping edit");
            try {
                editor.commitEdit();
                spinner.commitEdit();
            } catch (java.text.ParseException e) {
                JOptionPane.showMessageDialog(null,
                        "Invalid value, discarding.");
            }
            return super.stopCellEditing();
        }
    }
}
