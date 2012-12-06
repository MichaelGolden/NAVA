/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.structurevis;

import java.awt.event.WindowEvent;
import java.util.EventListener;

/**
 *
 * @author Michael
 */
public interface StructureVisListener extends EventListener
{
    public void windowClosingEvent(WindowEvent e);
}
