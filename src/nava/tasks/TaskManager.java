/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.tasks;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.EventListenerList;
import nava.tasks.applications.Application;
import nava.tasks.Task.Status;
import nava.tasks.applications.ApplicationOutput;
import nava.ui.ProjectController;
import nava.ui.navigator.NavigationEvent;
import nava.ui.navigator.NavigationListener;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class TaskManager extends Thread {

    LinkedList<Task> runQueue = new LinkedList<>();
    LinkedList<Task> generalTaskQueue = new LinkedList<>();
    LinkedList<Task> deferrableTaskQueue = new LinkedList<>();
    LinkedList<UITask> uiTaskQueue = new LinkedList<>();
    int totalSlots = Math.max(Runtime.getRuntime().availableProcessors(), 1);
    int usedSlots = 0;
    int availableSlots = totalSlots - usedSlots;
    int deferrableTasksUsedSlots = 0;
    ProjectController projectController;

    public TaskManager(ProjectController projectController) {
        this.projectController = projectController;
        start();
    }

    private void runTask(Task task) {
        if (task.getStatus() != Status.RUNNING && task.getStatus() != Status.FINISHED) {
            usedSlots += task.slotUsage;
            availableSlots = totalSlots - usedSlots;
            if (task.deferrable) {
                deferrableTasksUsedSlots += task.slotUsage;
            }
            runQueue.add(task);
            execute(task);
        }
        checkQueue();
    }

    private void execute(final Task task) {
        task.setStatus(Status.STARTED);
        task.startTime = System.currentTimeMillis();
        Thread taskThread = new Thread() {

            @Override
            public void run() {
                task.setStatus(Status.RUNNING);
                try {
                    task.task();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    //System.out.println("CONSOLE STATUS"+task.consoleErrorBuffer);
                    task.combinedBuffer.bufferedWrite("Finished with error:\n" + ex.getMessage(), task.taskInstanceId, "standard_err");
                    task.setStatus(Status.FINISHED);
                }
                if (task instanceof Application) {
                    Application app = (Application) task;
                    if (app.isCanceled()) {
                        task.setStatus(Status.STOPPED);
                    } else {
                        task.setStatus(Status.FINISHED);
                        List<ApplicationOutput> outputFiles = app.getOutputFiles();
                        for (ApplicationOutput outputFile : outputFiles) {
                            projectController.importDataSourceFromOutputFile(outputFile);
                        }
                    }
                } else {
                    if (task.getStatus() == Status.STOPPED) {
                    } else {
                        task.setStatus(Status.FINISHED);
                    }
                }
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
        deferrableTaskQueue.remove(task);

        usedSlots -= task.slotUsage;
        availableSlots = totalSlots - usedSlots;
        if (task.deferrable) {
            deferrableTasksUsedSlots -= task.slotUsage;
        }
    }

    public void queueTask(Task task, boolean deferrable) {
        task.deferrable = deferrable;
        task.taskManager = this;

        if (runQueue.contains(task) || generalTaskQueue.contains(task) || deferrableTaskQueue.contains(task)) {
            System.err.println("Task is already queued: "+task.getClass().toString());
        } else {
            task.before();
            if (task.deferrable) {
                deferrableTaskQueue.add(task);
            } else {
                generalTaskQueue.add(task);
            }
            task.setStatus(Status.QUEUED);
            task.queueTime = System.currentTimeMillis();
        }
        checkQueue();
    }

    public void stopTaskManager() {
        running = false;

        for (Task t : runQueue) {
            t.cancelTask();
        }

        checkQueue();
    }

    /*
     * public void queueUITask(UITask task) { task.taskManager = this;
     *
     * if (runQueue.contains(task) || uiTaskQueue.contains(task)) {
     * System.err.println("Task is already queued."); } else { task.before();
     * uiTaskQueue.add(task); task.setStatus(Status.QUEUED); task.queueTime =
     * System.currentTimeMillis(); } }
     */
    public synchronized void checkQueue() {
        if (!running) {
            // cancel all tasks
            for (Task t : runQueue) {
                t.cancelTask();
            }
            return;
        }

        availableSlots = totalSlots - usedSlots;
        /*
         * if (availableSlots > 0 && uiTaskQueue.size() > 0) { UITask task =
         * uiTaskQueue.removeFirst(); runTask(task); }
         */

        if (availableSlots <= 0 && generalTaskQueue.size() > 0) {
            // cancel a deferrable task if not enough slots available
            for (Task t : runQueue) {
                if (t.deferrable) {
                    t.cancelTask();
                    // re-queue
                    queueTask(t, true);
                    break;
                }
            }
        }

        if (availableSlots > 0 && generalTaskQueue.size() > 0) {
            Task task = generalTaskQueue.removeFirst();
            runTask(task);
        } else if (availableSlots > 0 && deferrableTaskQueue.size() > 0) {
            Task task = deferrableTaskQueue.removeFirst();
            runTask(task);
        }
    }
    boolean running = true;

    @Override
    public void run() {
        while (running) {
            checkQueue();

            try {
                Thread.sleep(2000);
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
        if (newStatus == Status.FINISHED || newStatus == Status.STOPPED) {
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
