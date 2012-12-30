/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.tasks;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.EventListenerList;
import nava.tasks.Task.Status;
import nava.ui.navigator.NavigationEvent;
import nava.ui.navigator.NavigationListener;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class TaskManager extends Thread {

    LinkedList<Task> runQueue = new LinkedList<>();
    LinkedList<Task> generalTaskQueue = new LinkedList<>();
    LinkedList<UITask> uiTaskQueue = new LinkedList<>();
    int totalSlots = Runtime.getRuntime().availableProcessors();
    int usedSlots = 0;
    int availableSlots = totalSlots - usedSlots;

    public TaskManager() {
        start();
    }

    private void runTask(Task task) {
        usedSlots += task.slotUsage;
        availableSlots = totalSlots - usedSlots;
        runQueue.add(task);
        execute(task);
    }

    private void execute(final Task task) {
        task.setStatus(Status.STARTED);
        task.startTime = System.currentTimeMillis();
        Thread taskThread = new Thread() {

            @Override
            public void run() {
                task.setStatus(Status.RUNNING);
                task.task();
                task.setStatus(Status.FINISHED);
                task.finishTime = System.currentTimeMillis();
                task.setProgress(1.0);
                task.after();
            }
        };
        taskThread.start();
    }

    private void dequeTask(Task task) {
        runQueue.remove(task);
        if (task instanceof UITask) {
            uiTaskQueue.remove((UITask) task);
        }
        generalTaskQueue.remove(task);
    }

    public void queueTask(Task task) {
        task.taskManager = this;
        
        if (runQueue.contains(task) ||  generalTaskQueue.contains(task))
        {
            System.err.println("Task is already queued.");
        }
        else
        {
            task.before();
            generalTaskQueue.add(task);            
            task.setStatus(Status.QUEUED);
            task.queueTime = System.currentTimeMillis();
        }
    }

    public void queueUITask(UITask task) {
        task.taskManager = this;

        if (runQueue.contains(task) || uiTaskQueue.contains(task)) {
            System.err.println("Task is already queued.");
        } else {
            task.before();
            uiTaskQueue.add(task);            
            task.setStatus(Status.QUEUED);
            task.queueTime = System.currentTimeMillis();
        }
    }

    @Override
    public void run() {
        while (true) {
            if (availableSlots > 0 && uiTaskQueue.size() > 0) {
                UITask task = uiTaskQueue.removeFirst();
                runTask(task);
            }
            
            
            if (availableSlots > 0 && generalTaskQueue.size() > 0) {
                Task task = generalTaskQueue.removeFirst();
                runTask(task);
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                Logger.getLogger(TaskManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    protected EventListenerList listeners = new EventListenerList();

    public void addTaskListener(TaskListener listener) {
        listeners.add(TaskListener.class, listener);
    }

    public void removeTaskListener(TaskListener listener) {
        listeners.remove(TaskListener.class, listener);
    }

    public void fireTaskStatusChanged(Task task, Status oldStatus, Status newStatus) {
        if (newStatus == Status.FINISHED) {
            dequeTask(task);
        }

        Object[] listeners = this.listeners.getListenerList();
        // Each listener occupies two elements - the first is the listener class
        // and the second is the listener instance
        for (int i = 0; i < listeners.length; i += 2) {
            if (listeners[i] == TaskListener.class) {
                ((TaskListener) listeners[i + 1]).taskStatusChanged(task, oldStatus, newStatus);
            }
        }
    }

    public void fireTaskProgressChanged(Task task, double progress) {

        Object[] listeners = this.listeners.getListenerList();
        // Each listener occupies two elements - the first is the listener class
        // and the second is the listener instance
        for (int i = 0; i < listeners.length; i += 2) {
            if (listeners[i] == TaskListener.class) {
                ((TaskListener) listeners[i + 1]).taskProgressChanged(task, progress);
            }
        }
    }
}
