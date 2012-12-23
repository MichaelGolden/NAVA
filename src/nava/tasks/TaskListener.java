/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.tasks;

import java.util.EventListener;
import nava.tasks.Task;
import nava.tasks.Task.Status;

/**
 *
 * @author Michael
 */
public interface TaskListener extends EventListener
{
    public void taskProgressChanged(Task task, double progress);
    public void taskStatusChanged(Task task, Status oldStatus, Status newStatus);
}
