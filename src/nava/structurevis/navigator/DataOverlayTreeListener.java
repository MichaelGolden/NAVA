/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.structurevis.navigator;

import java.util.EventListener;

/**
 *
 * @author Michael
 */
public interface DataOverlayTreeListener extends EventListener
{
    public void dataSourceSelectionChanged(DataOverlayTreeEvent e);
}
