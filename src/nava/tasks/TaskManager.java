/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.tasks;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import nava.tasks.Task.Status;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class TaskManager extends Thread {

    LinkedList<Task> runQueue = new LinkedList<>();
    LinkedList<UITask> uiTaskQueue = new LinkedList<>();
    LinkedList<Task> completeQueue = new LinkedList<>();
    
    int totalSlots = Runtime.getRuntime().availableProcessors();
    int usedSlots = 0;
    int availableSlots = totalSlots - usedSlots;

    private void runTask(Task task) {
        usedSlots += task.slotUsage;
        availableSlots = totalSlots - usedSlots;
        runQueue.add(task);
        execute(task);
    }

    private void execute(final Task task) {
        task.setStatus(Status.STARTED);
        Thread taskThread = new Thread() {
            @Override
            public void run() {
                task.setStatus(Status.RUNNING);
                task.task();                
                task.setStatus(Status.FINISHED);
            }
        };
        taskThread.start();
    }
    
    private void dequeTask(Task task)
    {        
        runQueue.remove(task);
        completeQueue.add(task);
    }

    public void queueTask(Task task) {
    }

    public void queueUITask(UITask task) {
        if (runQueue.contains(task) || uiTaskQueue.contains(task)) {
            System.err.println("Task is already queued.");
        } else {
            uiTaskQueue.add(task);
        }
    }

    @Override
    public void run() {
        while (true) {
            if (availableSlots > 0 && uiTaskQueue.size() > 0) {
                UITask task = uiTaskQueue.removeFirst();
                runTask(task);
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                Logger.getLogger(TaskManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void fireStatusChanged(Task task, Status oldStatus, Status newStatus) {
        if(newStatus == Status.FINISHED)
        {
            dequeTask(task);
        }
    }
}
