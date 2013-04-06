/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.structurevis.layerpanel;

import java.io.Serializable;
import java.util.ArrayList;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListDataEvent;
import nava.structurevis.data.AnnotationSource;
import nava.structurevis.data.DataOverlay1D;
import nava.structurevis.data.Overlay;
import nava.structurevis.layerpanel.LayerItem.LayerType;
import nava.utils.SafeListEvent;
import nava.utils.SafeListListener;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class LayerModel implements Serializable {

    private static final long serialVersionUID = 3879990042201539258L;
    ArrayList<LayerItem> items = new ArrayList<>();
    
    public LayerModel()
    {        
        addAnnotationSource(null);
        addDataOverlay1D("1D overlay (none)", null);
    }

    public void addAnnotationSource(AnnotationSource annotationSource) {
        items.add(new LayerItem(LayerType.ANNOTATIONS, "Sequence annotations", annotationSource));
        fireLayerModelEvent(ListDataEvent.INTERVAL_ADDED, items.size() - 1, items.size() - 1);
    }

    public void setAnnotationSource(AnnotationSource annotationSource) {
        items.set(0, new LayerItem(LayerType.ANNOTATIONS, "Sequence annotations", annotationSource));
        fireLayerModelEvent(ListDataEvent.CONTENTS_CHANGED, 0, 0);
    }

    public void addDataOverlay1D(String label, DataOverlay1D dataOverlay1D) {
        items.add(new LayerItem(LayerType.DATAOVERLAY_1D, label, dataOverlay1D));
        fireLayerModelEvent(ListDataEvent.INTERVAL_ADDED, items.size() - 1, items.size() - 1);
    }

    public void setDataOverlay1D(int index, String label, DataOverlay1D dataOverlay1D) {
        items.set(index, new LayerItem(LayerType.DATAOVERLAY_1D, label, dataOverlay1D));
        fireLayerModelEvent(ListDataEvent.CONTENTS_CHANGED, index, index);

    }
    transient EventListenerList listenerList = new EventListenerList();

    public void addLayerModelListener(LayerModelListener l) {
        if (listenerList == null) {
            listenerList = new EventListenerList();
        }
        listenerList.add(LayerModelListener.class, l);
    }

    public void removeLayerModelListener(LayerModelListener l) {
        listenerList.remove(LayerModelListener.class, l);
    }

    public void fireLayerModelEvent(int type, int index0, int index1) {
        Object[] listeners = this.listenerList.getListenerList();
        for (int i = 0; i < listeners.length; i += 2) {
            if (listeners[i] == LayerModelListener.class) {
                switch (type) {
                    case ListDataEvent.INTERVAL_ADDED:
                        ((LayerModelListener) listeners[i + 1]).intervalAdded(index0, index1);
                        break;
                    case ListDataEvent.INTERVAL_REMOVED:
                        ((LayerModelListener) listeners[i + 1]).intervalRemoved(index0, index1);
                        break;
                    case ListDataEvent.CONTENTS_CHANGED:
                        ((LayerModelListener) listeners[i + 1]).contentsChanged(index0, index1);
                        break;
                }
            }
        }
    }
}

class LayerItem implements Serializable {

    enum LayerType {

        ANNOTATIONS, DATAOVERLAY_1D
    };
    LayerType type;
    String label;
    Object object;
    int slidingWindowSize = 20;

    public LayerItem(LayerType type, String label, Object object) {
        this.type = type;
        this.label = label;
        this.object = object;
    }
}
