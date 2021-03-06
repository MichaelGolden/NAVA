/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.ui;

import java.util.Objects;
import javax.swing.ImageIcon;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import nava.tasks.applications.Application;
import nava.tasks.Task;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class TaskPanel extends javax.swing.JPanel implements ListSelectionListener {

    TaskTable taskTable;
    TaskConsolePanel taskConsolePanel;
    ImageIcon playIcon = new ImageIcon(ClassLoader.getSystemResource("resources/icons/play.png"));
    ImageIcon playMouseoverIcon = new ImageIcon(ClassLoader.getSystemResource("resources/icons/play_mouseover.png"));
    ImageIcon pauseIcon = new ImageIcon(ClassLoader.getSystemResource("resources/icons/pause.png"));
    ImageIcon pauseMouseoverIcon = new ImageIcon(ClassLoader.getSystemResource("resources/icons/pause_mouseover.png"));
    ImageIcon stopIcon = new ImageIcon(ClassLoader.getSystemResource("resources/icons/stop.png"));
    ImageIcon stopMouseverIcon = new ImageIcon(ClassLoader.getSystemResource("resources/icons/stop_mouseover.png"));

    /**
     * Creates new form TaskPanel
     */
    public TaskPanel() {
        initComponents();
        /*
         * TaskListCellRenderer taskListCellRenderer = new
         * TaskListCellRenderer(); DefaultListModel<Task> taskListModel = new
         * DefaultListModel<Task>();
         *
         * jList1.setCellRenderer(taskListCellRenderer);
         * jList1.setModel(taskListModel);
         *
         * taskListModel.addElement(new AnnotationMappingTask(null,null,null,
         * null)); taskListModel.addElement(new MappingTask(null,null,null));
         */
        taskTable = new TaskTable(MainFrame.taskManager);
        taskTable.table.getSelectionModel().addListSelectionListener(this);
        jSplitPane1.setDividerLocation(0.6);
        jSplitPane1.setResizeWeight(0.3);
        jPanel3.add(taskTable);
        //jSplitPane1.setLeftComponent(taskTable);

        taskConsolePanel = new TaskConsolePanel();
        jSplitPane1.setRightComponent(taskConsolePanel);
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
        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 5), new java.awt.Dimension(0, 5), new java.awt.Dimension(32767, 5));
        jButton1 = new javax.swing.JButton();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 5), new java.awt.Dimension(0, 5), new java.awt.Dimension(32767, 5));
        jButton2 = new javax.swing.JButton();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 32767));

        setLayout(new java.awt.BorderLayout());

        jSplitPane1.setDividerLocation(500);
        jSplitPane1.setDividerSize(3);

        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.LINE_AXIS));

        jPanel3.setLayout(new java.awt.BorderLayout());
        jPanel1.add(jPanel3);

        jPanel2.setMinimumSize(new java.awt.Dimension(30, 30));
        jPanel2.setPreferredSize(new java.awt.Dimension(30, 100));
        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.PAGE_AXIS));
        jPanel2.add(filler2);

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/icons/play.png"))); // NOI18N
        jButton1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jButton1.setBorderPainted(false);
        jButton1.setContentAreaFilled(false);
        jButton1.setEnabled(false);
        jButton1.setPreferredSize(new java.awt.Dimension(24, 24));
        jButton1.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/icons/play_mouseover.png"))); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel2.add(jButton1);
        jPanel2.add(filler3);

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/icons/stop.png"))); // NOI18N
        jButton2.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jButton2.setBorderPainted(false);
        jButton2.setContentAreaFilled(false);
        jButton2.setEnabled(false);
        jButton2.setPreferredSize(new java.awt.Dimension(24, 24));
        jButton2.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/icons/stop_mouseover.png"))); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel2.add(jButton2);
        jPanel2.add(filler1);

        jPanel1.add(jPanel2);

        jSplitPane1.setLeftComponent(jPanel1);

        add(jSplitPane1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        if(currentlySelectedTask != null)
        {
            Application task = (Application) currentlySelectedTask;
            if(task.isPaused())
            {
                task.resumeTask();
            }
            else
            {
                task.pauseTask();
            }   
            
            setIcons(task);
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        if(currentlySelectedTask != null && currentlySelectedTask instanceof Application)
        {
            Application task = (Application) currentlySelectedTask;
            task.cancelTask();
            setIcons(task);
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JSplitPane jSplitPane1;
    // End of variables declaration//GEN-END:variables
    Task currentlySelectedTask = null;

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            int selectedRow = taskTable.table.getSelectedRow();
            //Task selectedTask = (Task) taskTable.tableDataModel.getValueAt(selectedRow, 4);
            if (selectedRow > -1) {
                Task selectedTask = (Task) taskTable.table.getValueAt(selectedRow, 4);
                if (!Objects.equals(selectedTask, currentlySelectedTask)) {
                    if (selectedTask instanceof Application) {
                        taskConsolePanel.setApplicationTask((Application) selectedTask);
                    } else {
                        taskConsolePanel.errorConsolePanel.clearScreen();
                        taskConsolePanel.standardConsolePanel.clearScreen();
                    }
                    setIcons(selectedTask);
                    currentlySelectedTask = selectedTask;
                }
            } else {
                taskConsolePanel.errorConsolePanel.clearScreen();
                taskConsolePanel.standardConsolePanel.clearScreen();
                currentlySelectedTask = null;
            }
        }
    }

    public void setIcons(Task task) {
        switch (task.getStatus()) {
            case PAUSED:
                jButton1.setIcon(playIcon);
                jButton1.setRolloverIcon(playMouseoverIcon);
                jButton1.setEnabled(true);
                jButton2.setEnabled(true);
                break;
            case RUNNING:
                jButton1.setIcon(pauseIcon);
                jButton1.setRolloverIcon(pauseMouseoverIcon);
                jButton1.setEnabled(true);
                jButton2.setEnabled(true);
                break;
            default:
                jButton1.setIcon(playIcon);
                jButton1.setRolloverIcon(playMouseoverIcon);
                jButton1.setEnabled(false);
                jButton2.setEnabled(false);
        }
    }
}
