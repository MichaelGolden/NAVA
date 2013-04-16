/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.tasks.applications;

import javax.swing.Icon;
import nava.ui.ProjectController;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public interface ApplicationPanelInterface {
    public void setupApplication(Application application);
    public String getTitle();
    public Icon getIcon();
    //public void setProjectController(ProjectController projectController);
}
