/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.ui;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public interface Task {
    public enum Status{STARTED,RUNNING,FINISHED,STOPPED,PAUSED};
    
    public void getProgress(double progress);
    public void setProgress(double progress);
}
