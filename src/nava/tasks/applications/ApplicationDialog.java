/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.tasks.applications;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public interface ApplicationDialog {
    public void setupApplication(Application application);
    public boolean shouldRunOnDialogClose();
}
