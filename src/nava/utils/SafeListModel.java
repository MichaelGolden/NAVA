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
public class SafeListModel<T> implements ListModel, Serializable {

    ArrayList<T> data = new ArrayList<>();
    transient EventListenerList listenerList;
    
    public T get(int index)
    {
        return data.get(index);
    }

    public void add(int index, T element) {
        data.add(index, element);
        fireListDataEvent(this, ListDataEvent.INTERVAL_ADDED, index, index);
    }

    public void addElement(T element) {
        data.add(element);
        fireListDataEvent(this, ListDataEvent.INTERVAL_ADDED, data.size()-1, data.size()-1);
    }

    public boolean removeElement(T element) {
        int index = data.indexOf(element);
        T removed = data.remove(index);
        fireListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, index, index);
        return removed != null;
    }
    
    public void removeAllElements()
    {
        int end = data.size()-1;
        data.clear();
        fireListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, 0, end);
    }
    
    public T set(int index, T element)
    {
        T previousElement = data.set(index, element);
        this.fireListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, index, index);
        return previousElement;
    }
    
    public ArrayList<T> getArrayListShallowCopy()
    {
        return (ArrayList<T>)data.clone();
    }
    
    protected EventListenerList listeners = new EventListenerList();

    @Override
    public int getSize() {
        return data.size();
    }

    @Override
    public T getElementAt(int index) {
        return data.get(index);
    }

    @Override
    public void addListDataListener(ListDataListener l) {
        listeners.add(ListDataListener.class, l);
    }

    @Override
    public void removeListDataListener(ListDataListener l) {
        listeners.remove(ListDataListener.class, l);
    }

    public void fireListDataEvent(Object source, int type, int index0, int index1) {
        Object[] listeners = this.listeners.getListenerList();
        // Each listener occupies two elements - the first is the listener class
        // and the second is the listener instance
        for (int i = 0; i < listeners.length; i += 2) {
            if (listeners[i] == ListDataListener.class) {
                ((ListDataListener) listeners[i + 1]).intervalAdded(new ListDataEvent(source, type, index0, index1));
            }
        }
    }
}
