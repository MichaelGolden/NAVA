/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.ui.console;

import java.util.EventListener;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public interface ConsoleListener extends EventListener
{
    public void lineAddedEvent(int totalLines);
}
