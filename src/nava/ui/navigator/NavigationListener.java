/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.ui.navigator;

import java.util.EventListener;

/**
 *
 * @author Michael
 */
public interface NavigationListener extends EventListener
{
    public void dataSourceSelectionChanged(NavigationEvent e);
}
