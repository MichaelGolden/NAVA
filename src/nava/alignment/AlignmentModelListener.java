/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.alignment;

import java.util.EventListener;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public interface AlignmentModelListener extends EventListener {
    public abstract void alignmentChanged(Alignment alignment);
    public abstract void alignmentSortOrderChanged(int oldOrder, int newOrder);
    public abstract void itemStateDataChanged(AlignmentItem item);
}
