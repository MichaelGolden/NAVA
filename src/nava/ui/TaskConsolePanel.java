/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.ui;

import java.awt.Color;
import nava.tasks.applications.Application;
import nava.ui.console.ConsolePanel;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class TaskConsolePanel extends javax.swing.JPanel {

    Application task;
    ConsolePanel standardConsolePanel;
    ConsolePanel errorConsolePanel;

    /**
     * Creates new form ConsolePanel
     */
    public TaskConsolePanel() {
        initComponents();

        standardConsolePanel = new ConsolePanel();
        errorConsolePanel = new ConsolePanel();

        standardScrollPane.setViewportView(standardConsolePanel);
        //errorScrollPane.setViewportView(errorConsolePanel);
    }

    public void setApplicationTask(Application task) {
        this.task = task;

        standardConsolePanel = new ConsolePanel();
        standardConsolePanel.setTypeColor("standard_out", Color.black);
        standardConsolePanel.setTypeColor("standard_err", Color.red);
        standardConsolePanel.setTypeColor("console", Color.green);

       // errorConsolePanel = new ConsolePanel();
       // errorConsolePanel.setTypeColor("standard_err", Color.red);
        //errorConsolePanel.setTypeColor("standard_out", Color.black);

        
        if (task.combinedBuffer == null) {
            standardConsolePanel.setConsoleBuffer(task.consoleInputBuffer);
            errorConsolePanel.setConsoleBuffer(task.consoleErrorBuffer);

            standardScrollPane.setViewportView(standardConsolePanel);
            //errorScrollPane.setViewportView(errorConsolePanel);
        } else {
            standardConsolePanel.setConsoleBuffer(task.combinedBuffer);
            //errorConsolePanel.setConsoleBuffer(task.getApplication().consoleErrorBuffer);

            standardScrollPane.setViewportView(standardConsolePanel);
            //errorScrollPane.setViewportView(errorConsolePanel);
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

        standardScrollPane = new javax.swing.JScrollPane();

        setLayout(new java.awt.BorderLayout());
        add(standardScrollPane, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane standardScrollPane;
    // End of variables declaration//GEN-END:variables
}
