/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.alignment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.AbstractListModel;
import javax.swing.event.EventListenerList;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class AlignmentModel extends AbstractListModel {

    int[] modelToView;
    int[] viewToModel;
    Alignment alignment = null;
    List<AlignmentItem> items = null;
    int[] subItemCount = new int[1];
    int[] itemCount = new int[1];
    int[] itemCountMod = new int[1];
    int maxSequenceLength = 0;

    public void setAlignment(Alignment alignment) {
        this.alignment = alignment;
        this.items = alignment.items;
        setAlignment(alignment.items);
    }

    public void setAlignment(List<AlignmentItem> items) {
        this.items = items;
        modelToView = new int[items.size()];
        viewToModel = new int[modelToView.length];
        for (int i = 0; i < modelToView.length; i++) {
            modelToView[i] = i;
            viewToModel[i] = i;
        }

        this.maxSequenceLength = 0;
        for (AlignmentItem item : items) {
            for (String subitem : item.subItems) {
                this.maxSequenceLength = Math.max(maxSequenceLength, subitem.length());
            }
        }

        calculateCounts();
        fireAlignmentChanged(alignment);
    }

    public void setStructuralAlignment(SecondaryStructureAlignment structureAlignment) {
        setAlignment(structureAlignment);
    }

    public void calculateCounts() {
        if (items.size() > 0) {
            subItemCount = new int[items.size()];
            subItemCount[0] = items.get(0).getSubItemCount();
            for (int i = 1; i < items.size(); i++) {
                subItemCount[i] = subItemCount[i - 1] + items.get(i).getSubItemCount();
            }

            int k = 0;
            itemCount = new int[subItemCount[subItemCount.length - 1]];
            itemCountMod = new int[itemCount.length];
            for (int i = 0; i < items.size(); i++) {
                for (int mod = 0; mod < items.get(i).getSubItemCount(); mod++) {
                    itemCount[k] = i;
                    itemCountMod[k] = mod;
                    k++;
                }
            }
        }
        else
        {
            subItemCount = new int[1];
            subItemCount[0] = 0;
            itemCount = new int[1];
            itemCount[0] = 0;
            itemCountMod = new int[1];
            itemCountMod[0] = 0;
        }
    }

    @Override
    public int getSize() {
        return subItemCount[subItemCount.length - 1];
    }

    public ItemRange getItemRange(int viewIndex) {
        if (viewIndex < itemCount.length) {
            return new ItemRange(itemCount[viewIndex], itemCountMod[viewIndex], items.get(itemCount[viewIndex]).getSubItemCount());
        }
        return null;
    }

    public void setSubItemAt(int viewIndex, String s) {
        items.get(itemCount[viewIndex]).setSubItem(itemCountMod[viewIndex], s);
    }

    public int getElementTypeAt(int viewIndex) {
        return itemCountMod[viewIndex];
    }

    @Override
    public String getElementAt(int viewIndex) {
        if (viewIndex < itemCount.length) {
            return items.get(itemCount[viewIndex]).getSubItem(itemCountMod[viewIndex]);
        }
        return null;
    }

    public AlignmentItem getItemAt(int viewIndex) {
        return items.get(itemCount[viewIndex]);
    }

    public String getElementNameAt(int viewIndex) {
        if (itemCountMod[viewIndex] < items.get(itemCount[viewIndex]).getSubItemNameCount()) {
            return items.get(itemCount[viewIndex]).getSubItemName(itemCountMod[viewIndex]);
        } else {
            return items.get(itemCount[viewIndex]).name;
        }
    }

    public int modelIndex(int viewIndex) {
        return viewToModel[viewIndex];
    }
    public static final int ASCENDING = 1;
    public static final int DESCENDING = -1;
    public static final int NOT_SORTED = 0;
    public static int sortOrder = ASCENDING;

    public void sort(int order) {
        int oldOrder = order;
        AlignmentModel.sortOrder = order;
        Collections.sort(items);
        for (int i = 0; i < items.size(); i++) {
            viewToModel[i] = items.get(i).modelIndex;
            modelToView[items.get(i).modelIndex] = i;
        }
        calculateCounts();
        fireContentsChanged(this, 0, items.size() - 1);
        fireAlignmentSortOrderChanged(oldOrder, order);
    }

    public class ItemRange {

        int startIndex;
        int mod;
        int length;

        public ItemRange(int startIndex, int mod, int length) {
            this.startIndex = startIndex;
            this.mod = mod;
            this.length = length;
        }
    }
    protected EventListenerList listeners = new EventListenerList();

    public void addAlignmentModelListener(AlignmentModelListener listener) {
        listeners.add(AlignmentModelListener.class, listener);
    }

    public void removeAlignmentModelListener(AlignmentModelListener listener) {
        listeners.remove(AlignmentModelListener.class, listener);
    }

    public void fireAlignmentChanged(Alignment alignment) {
        Object[] listeners = this.listeners.getListenerList();
        // Each listener occupies two elements - the first is the listener class
        // and the second is the listener instance
        for (int i = 0; i < listeners.length; i += 2) {
            if (listeners[i] == AlignmentModelListener.class) {
                ((AlignmentModelListener) listeners[i + 1]).alignmentChanged(alignment);
            }
        }
    }

    public void fireAlignmentStateDataChanged(AlignmentItem item) {
        Object[] listeners = this.listeners.getListenerList();
        // Each listener occupies two elements - the first is the listener class
        // and the second is the listener instance
        for (int i = 0; i < listeners.length; i += 2) {
            if (listeners[i] == AlignmentModelListener.class) {
                ((AlignmentModelListener) listeners[i + 1]).itemStateDataChanged(item);
            }
        }
    }

    public void fireAlignmentSortOrderChanged(int oldOrder, int newOlder) {
        Object[] listeners = this.listeners.getListenerList();
        // Each listener occupies two elements - the first is the listener class
        // and the second is the listener instance
        for (int i = 0; i < listeners.length; i += 2) {
            if (listeners[i] == AlignmentModelListener.class) {
                ((AlignmentModelListener) listeners[i + 1]).alignmentSortOrderChanged(oldOrder, newOlder);
            }
        }
    }

    public Alignment getAlignment() {
        return alignment;
    }
}
