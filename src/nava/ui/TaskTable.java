/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import nava.tasks.*;
import nava.tasks.Task.Status;

/**
 *
 * @author Michael
 */
public class TaskTable extends JPanel implements TaskListener {

    TableDataModel tableDataModel = new TableDataModel();
    final JTable table;
    public JScrollPane scrollPane;
    TaskManager taskManager;

    public TaskTable(TaskManager taskManager) {
        super(new BorderLayout());
        this.taskManager = taskManager;

        TableSorter sorter = new TableSorter(tableDataModel);
        table = new JTable(sorter);
        sorter.setTableHeader(table.getTableHeader());
        sorter.sortOnColumn(table.getTableHeader(), table.getColumnCount() - 1, -1);
        table.setFillsViewportHeight(true);
        table.addMouseListener(new MouseAdapter() {

            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    JTable target = (JTable) e.getSource();
                    int row = target.getSelectedRow();
                    int column = target.getSelectedColumn();

                    int s = ((Integer) table.getModel().getValueAt(row, 3)).intValue();

                    //System.out.println(row+"\tID"+);
                    // do some action
                }
            }
        });
        
        table.getColumnModel().getColumn(4).setCellRenderer(new ProgressBarTableCellRenderer());

        scrollPane = new JScrollPane(table);
        add(scrollPane);
        
        taskManager.addTaskListener(this);  
        
        TestTask t = new TestTask();
        taskManager.queueTask(t);
    }

    @Override
    public void taskProgressChanged(Task task, double progress) {
        this.tableDataModel.updateTask(task);
    }

    @Override
    public void taskStatusChanged(Task task, Status oldStatus, Status newStatus) {
        System.out.println("taskStatusChanged " + task);
        this.tableDataModel.updateTask(task);
    }

    class TableDataModel extends AbstractTableModel {

        ArrayList<Task> tasks = new ArrayList<>();
        String[] columnNames = {"#", "Task name", "Description", "Time elapsed", "Progress"};
        Class[] columnClasses = {String.class, String.class, String.class, String.class, Task.class};
        public ArrayList<Object[]> rows = new ArrayList<Object[]>();

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
        boolean hasMappedData = false;

        public void clear() {
            rows.clear();
            fireTableRowsDeleted(0, rows.size());
        }

        public void addTask(Task task, int index) {            
            tasks.add(task);
            Object[] row = {index, task.getName(), task.getDescription(), 0, task};
            tableDataModel.addRow(row);
        }

        public void updateTask(Task task) {
            int index = tasks.indexOf(task);
            if (index != -1) {
                Object[] row = {index+1,task.getName(), task.getDescription(), 0, task};
                rows.set(index, row);
                this.fireTableRowsUpdated(index, index);
            } else {
                System.out.println("Adding task " + task);
                addTask(task, tasks.size()+1);
            }
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

        /*
         * Don't need to implement this method unless your table's data can
         * change.
         */
        public void setValueAt(Object value, int row, int col) {
            rows.get(row)[col] = value;
            fireTableCellUpdated(row, col);
        }
    }

    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("SimpleTableDemo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create and set up the content pane.
        TaskTable newContentPane = new TaskTable(null);
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

            /*
             * Component c = super.getTableCellRendererComponent(table, value,
             * isSelected, hasFocus, row, col); String s =
             * table.getModel().getValueAt(row, col).toString();
             *
             * if (s.equalsIgnoreCase("yellow")) {
             * c.setForeground(Color.YELLOW); } else {
             * c.setForeground(Color.WHITE); }
             */
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
