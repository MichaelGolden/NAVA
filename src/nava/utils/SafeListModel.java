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
    private static final long serialVersionUID = -6447941655821501722L;

    ArrayList<T> data = new ArrayList<>();
    transient EventListenerList listenerList = new EventListenerList();
    
    public SafeListModel()
    {
        listenerList = new EventListenerList();
    }
    
    public T get(int index)
    {
        return data.get(index);
    }
    
    public boolean contains(T element)
    {
        return data.contains(element);
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
        ArrayList<T> newList = new ArrayList<>();
        for(T elem : data){
            newList.add(elem);
        }
        return newList;
    }
    
    public int size()
    {
        return data.size();
    }

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
        if(listenerList == null)
        {
            listenerList = new EventListenerList();
        }
        listenerList.add(ListDataListener.class, l);
    }

    @Override
    public void removeListDataListener(ListDataListener l) {
        listenerList.remove(ListDataListener.class, l);
    }

    public void fireListDataEvent(Object source, int type, int index0, int index1) {
        Object[] listeners = this.listenerList.getListenerList();
        // Each listener occupies two elements - the first is the listener class
        // and the second is the listener instance
        for (int i = 0; i < listeners.length; i += 2) {
            if (listeners[i] == ListDataListener.class) {
                ((ListDataListener) listeners[i + 1]).intervalAdded(new ListDataEvent(source, type, index0, index1));
            }
        }
    }
    
    @Override
    public String toString()
    {
        return data.toString();
    }
}
