/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.ui;

import java.awt.Component;
import javax.swing.*;
import nava.tasks.Task;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class TaskListCellRenderer extends JPanel implements ListCellRenderer {

    JProgressBar progressBar = new JProgressBar();
    
    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        Task task = (Task)value;
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
        
        panel.add(new JLabel(task.getName()));
        progressBar.setIndeterminate(true);
        panel.add(progressBar);
        
        return panel;
    }
    
}
