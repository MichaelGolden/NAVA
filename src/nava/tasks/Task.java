/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.tasks;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public abstract class Task<T> {
    
    TaskManager taskManager;
    
    double progress;

    public enum Status {

        NOT_STARTED, STARTED, RUNNING, FINISHED, STOPPED, PAUSED
    };
    
    Status status = Status.NOT_STARTED;
    
    int slotUsage = 1;
    
    public abstract void task();
    
    public abstract T get();

    public double getProgress()
    {
        return progress;
    }
    
    public void setProgress(double progress)
    {
        this.progress = progress;
    }
    
    public Status getStatus()
    {
        return status;
    }
    
    public void setStatus(Status status)
    {
        Status oldStatus = this.status;
        this.status = status;
        if(taskManager != null)        
        {
            taskManager.fireStatusChanged(this, oldStatus, status);
        }
    }
}
