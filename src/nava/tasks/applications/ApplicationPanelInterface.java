/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.tasks.applications;

import javax.swing.Icon;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public interface ApplicationPanelInterface {
    public void setupApplication(Application application);
    public String getTitle();
    public Icon getIcon();
}
