/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.tasks;

import nava.ui.console.ConsoleBuffer;
import nava.ui.console.ConsoleDatabase;
import nava.ui.console.ConsoleInputHandler;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public abstract class Task<T> {

    public static long taskRuntimeID = System.currentTimeMillis();
    public static long taskInstanceCount = 0;
    public static ConsoleDatabase consoleDatabase = new ConsoleDatabase();
    protected String taskInstanceId;
    TaskManager taskManager;
    protected boolean deferrable = false;
    
    
    public ConsoleBuffer combinedBuffer; // the actively used buffer
    
    // these buffered used if combined buffer not available
    public ConsoleBuffer consoleInputBuffer;
    public ConsoleBuffer consoleErrorBuffer;

    public Task() {
        taskInstanceCount++;
        taskInstanceId = taskRuntimeID + "_" + taskInstanceCount + "";

        combinedBuffer = new ConsoleBuffer(consoleDatabase, taskInstanceId, null);
    }
    double progress = -1;

    public enum Status {

        NOT_STARTED, QUEUED, STARTED, RUNNING, FINISHED, STOPPED, PAUSED;

        @Override
        public String toString() {
            switch (this) {
                case NOT_STARTED:
                    return "Not started";
                case QUEUED:
                    return "Queued";
                case STARTED:
                    return "Started";
                case RUNNING:
                    return "Running";
                case FINISHED:
                    return "Complete";
                case STOPPED:
                    return "Canceled";
                case PAUSED:
                    return "Paused";
                default:
                    return "";
            }
        }
    };
    Status status = Status.NOT_STARTED;
    long queueTime = -1;
    long startTime = -1;
    long finishTime = -1;
    protected int slotUsage = 1;

    public abstract void before();

    public abstract void task();

    public abstract void after();

    protected abstract void pause();

    public void pauseTask() {
        pause();
        setStatus(Status.PAUSED);
    }

    protected abstract void resume();

    public void resumeTask() {
        resume();
        setStatus(Status.RUNNING);
    }

    protected abstract void cancel();

    public void cancelTask() {
        cancel();
        setStatus(Status.STOPPED);
    }

    public abstract String getName();

    public abstract String getDescription();

    public abstract T get();

    public double getProgress() {
        return progress;
    }

    public void setProgress(double progress) {
        this.progress = progress;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        Status oldStatus = this.status;
        this.status = status;
        if (taskManager != null) {
            taskManager.fireTaskStatusChanged(this, oldStatus, status);
        }
    }

    /**
     * Returns the time since the task started running in milliseconds.
     *
     * @return the time since the task started running in milliseconds.
     */
    public long getTimeRunning() {
        if (startTime == -1) {
            return 0;
        }

        if (finishTime != -1) {
            return finishTime - startTime;
        }

        
        return System.currentTimeMillis() - startTime;
    }

    /**
     * Returns the length of time the task has been in the queue in
     * milliseconds.
     *
     * @return the length of time the task has been in the queue in
     * milliseconds.
     */
    public long getTimeQueued() {
        if (queueTime == -1) {
            return 0;
        }

        if (startTime != -1) {
            return startTime - queueTime;
        }

        
        return System.currentTimeMillis() - queueTime;
    }

    public boolean isStarted() {
        return status != Status.NOT_STARTED;
    }

    public boolean isRunning() {
        return status == Status.RUNNING;
    }

    public boolean isCanceled() {
        return status == Status.STOPPED;
    }

    public boolean isFinished() {
        return status == Status.FINISHED;
    }

    public boolean isPaused() {
        return status == Status.PAUSED;
    }
}
