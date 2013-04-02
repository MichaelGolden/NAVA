/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.structurevis.layerpanel;

import java.util.EventListener;
import nava.utils.SafeListEvent;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public interface LayerModelListener extends EventListener {
    public void intervalAdded(int index0, int index1);
    public void intervalRemoved(int index0, int index1);
    public void contentsChanged(int index0, int index1);
}
