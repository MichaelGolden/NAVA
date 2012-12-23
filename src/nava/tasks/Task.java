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
    
    double progress = -1;

    public enum Status {

        NOT_STARTED, QUEUED, STARTED, RUNNING, FINISHED, STOPPED, PAUSED;
        
        @Override
        public String toString()
        {
            switch(this)
            {
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
    
    int slotUsage = 1;
    
    public abstract void before();
    public abstract void task();
    public abstract void after();
    public abstract String getName();
    public abstract String getDescription();
    
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
            taskManager.fireTaskStatusChanged(this, oldStatus, status);
        }
    }
    
    public long timeRunning()
    {
        if(startTime == -1)
        {
            return 0;
        }
        
        if(finishTime != -1)
        {
            return finishTime - startTime;
        }
        
        return System.currentTimeMillis() - startTime;
    }
    
    public long timeQueued()
    {
        if(startTime != -1)
        {
            return startTime - queueTime;
        }
        
        return System.currentTimeMillis() - startTime;
    }
}
