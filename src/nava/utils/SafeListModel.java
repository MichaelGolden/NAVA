/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.utils;

import java.io.Serializable;
import java.util.ArrayList;
import javax.swing.AbstractListModel;
import javax.swing.DefaultListModel;
import javax.swing.ListModel;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import nava.ui.navigator.NavigationEvent;
import nava.ui.navigator.NavigationListener;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class SafeListModel<T> implements Serializable {

    private static final long serialVersionUID = -6447941655821501722L;
    ArrayList<T> data = new ArrayList<>();
    transient EventListenerList listenerList = new EventListenerList();

    public SafeListModel() {
        listenerList = new EventListenerList();
    }

    public T get(int index) {
        return data.get(index);
    }

    public boolean contains(T element) {
        return data.contains(element);
    }

    public int indexOf(Object object) {
        return data.indexOf(object);
    }

    public void add(int index, T element) {
        data.add(index, element);
        fireSafeListEvent(this, ListDataEvent.INTERVAL_ADDED, index, index, null, null);
    }

    public void addElement(T element) {
        data.add(element);
        fireSafeListEvent(this, ListDataEvent.INTERVAL_ADDED, data.size() - 1, data.size() - 1, null, null);
    }

    public boolean removeElement(T element) {
        int index = data.indexOf(element);
        T removed = data.remove(index);
        fireSafeListEvent(this, ListDataEvent.INTERVAL_REMOVED, index, index, null, null);
        return removed != null;
    }

    public void removeAllElements() {
        int end = data.size() - 1;
        data.clear();
        fireSafeListEvent(this, ListDataEvent.INTERVAL_REMOVED, 0, end, null, null);
    }

    public T set(int index, T element) {
        T previousElement = data.set(index, element);
        fireSafeListEvent(this, ListDataEvent.CONTENTS_CHANGED, index, index, previousElement, element);
        return previousElement;
    }

    public ArrayList<T> getArrayListShallowCopy() {
        ArrayList<T> newList = new ArrayList<>();
        for (T elem : data) {
            newList.add(elem);
        }
        return newList;
    }

    public int size() {
        return data.size();
    }

    public int getSize() {
        return data.size();
    }

    public T getElementAt(int index) {
        return data.get(index);
    }

    public void addSafeListListener(SafeListListener l) {
        if (listenerList == null) {
            listenerList = new EventListenerList();
        }
        listenerList.add(SafeListListener.class, l);
    }

    public void removeSafeListListener(SafeListListener l) {
        listenerList.remove(SafeListListener.class, l);
    }

    public void fireSafeListEvent(Object source, int type, int index0, int index1, Object oldElement, Object newElement) {
        if (this.listenerList != null) {
            Object[] listeners = this.listenerList.getListenerList();
            // Each listener occupies two elements - the first is the listener class
            // and the second is the listener instance
            for (int i = 0; i < listeners.length; i += 2) {
                if (listeners[i] == SafeListListener.class) {
                    switch (type) {
                        case ListDataEvent.INTERVAL_ADDED:
                            ((SafeListListener) listeners[i + 1]).intervalAdded(new SafeListEvent(source, type, index0, index1, oldElement, newElement));
                            break;
                        case ListDataEvent.INTERVAL_REMOVED:
                            ((SafeListListener) listeners[i + 1]).intervalRemoved(new SafeListEvent(source, type, index0, index1, oldElement, newElement));
                            break;
                        case ListDataEvent.CONTENTS_CHANGED:
                            ((SafeListListener) listeners[i + 1]).contentsChanged(new SafeListEvent(source, type, index0, index1, oldElement, newElement));
                            break;
                    }
                }
            }
        } else {
            System.err.println("No listeners for this list.");
        }
    }

    @Override
    public String toString() {
        return data.toString();
    }
}
