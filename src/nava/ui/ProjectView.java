/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.ui;

import java.util.List;
import javax.swing.event.ListDataEvent;

/**
 *
 * @author Michael
 */
public interface ProjectView 
{
    public void dataSourcesLoaded();
    
    public void dataSourcesIntervalAdded(ListDataEvent e);
    public void dataSourcesIntervalRemoved(ListDataEvent e);
    public void dataSourcesContentsChanged(ListDataEvent e);
}
