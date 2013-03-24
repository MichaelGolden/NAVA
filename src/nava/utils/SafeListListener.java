/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.utils;

import java.util.EventListener;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public interface SafeListListener extends EventListener {
    public void intervalAdded(SafeListEvent e);
    public void intervalRemoved(SafeListEvent e);
    public void contentsChanged(SafeListEvent e);
}
