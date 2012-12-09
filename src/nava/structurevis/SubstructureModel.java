/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.structurevis;

import nava.structurevis.data.Substructure;
import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import javax.swing.event.EventListenerList;
import nava.structurevis.data.DataSource1D;
import nava.structurevis.data.DataSource2D;
import nava.structurevis.data.StructureSource;
import nava.ui.navigator.NavigationEvent;
import nava.ui.navigator.NavigationListener;
import nava.utils.Mapping;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class SubstructureModel implements Serializable {

    public static Color missingDataColor = Color.gray;
    public static Color filteredDataColor = Color.darkGray;
    int sequenceLength;
    DataSource1D data1D = null;
    Mapping mapping1D = null;
    DataSource2D data2D = null;
    Mapping mapping2D = null;
    NucleotideComposition nucleotideComposition = null;
    NucleotideComposition.Type nucleotideCompositionType = NucleotideComposition.Type.FREQUENCY;
    StructureSource structureSource = null;
    int numbering = 0;
    Substructure structure = null;
    transient DistanceMatrix distanceMatrix = null;
    transient DistanceMatrix structureDistanceMatrix = null;
    int maxDistance = -1;
    boolean useLowerThreshold1D = false;
    boolean useUpperThreshold1D = false;
    boolean useLowerThreshold2D = false;
    boolean useUpperThreshold2D = false;
    double thresholdMin1D;
    double thresholdMax1D;
    double thresholdMin2D;
    double thresholdMax2D;
    
    protected transient EventListenerList listeners = new EventListenerList();
    
    public void initialise()
    {
         listeners = new EventListenerList();
    }

    public void loadData() {
        if (data1D != null) {
            data1D.loadData();
        }
        if (data2D != null) {
            data2D.loadData();
        }
        if (structureSource != null) {
            structureSource.loadData();
        }
    }

    public void setDataSource1D(DataSource1D dataSource1D) {
        dataSource1D.loadData();
        this.data1D = dataSource1D;
        fireDataSource1DChanged(dataSource1D);
    }

    public void setDataSource2D(DataSource2D dataSource2D) {
        dataSource2D.loadData();
        this.data2D = dataSource2D;
        fireDataSource2DChanged(dataSource2D);
    }

    public void setStructureSource(StructureSource structureSource) {
        structureSource.loadData();
        this.structureSource = structureSource;
        this.sequenceLength = structureSource.pairedSites.length;
        fireStructureSourceChanged(structureSource);
    }

    public ArrayList<Substructure> getSubstructures() {
        if (structureSource != null) {
            return structureSource.substructures;
        }

        return new ArrayList<>();
    }
    
    public void addSubstructureModelListener(SubstructureModelListener listener) {
        listeners.add(SubstructureModelListener.class, listener);
    }

    public void removeSubstructureModelListener(SubstructureModelListener listener) {
        listeners.remove(SubstructureModelListener.class, listener);
    }
    
    public void fireDataSource1DChanged(DataSource1D dataSource1D) {
        Object[] listeners = this.listeners.getListenerList();
        // Each listener occupies two elements - the first is the listener class
        // and the second is the listener instance
        for (int i = 0; i < listeners.length; i += 2) {
            if (listeners[i] == SubstructureModelListener.class) {
                ((SubstructureModelListener) listeners[i + 1]).dataSource1DChanged(dataSource1D);
            }
        }
    }
    
    public void fireDataSource2DChanged(DataSource2D dataSource2D) {
        Object[] listeners = this.listeners.getListenerList();
        // Each listener occupies two elements - the first is the listener class
        // and the second is the listener instance
        for (int i = 0; i < listeners.length; i += 2) {
            if (listeners[i] == SubstructureModelListener.class) {
                ((SubstructureModelListener) listeners[i + 1]).dataSource2DChanged(dataSource2D);
            }
        }
    }
    
    public void fireStructureSourceChanged(StructureSource structureSource) {
        Object[] listeners = this.listeners.getListenerList();
        // Each listener occupies two elements - the first is the listener class
        // and the second is the listener instance
        for (int i = 0; i < listeners.length; i += 2) {
            if (listeners[i] == SubstructureModelListener.class) {
                ((SubstructureModelListener) listeners[i + 1]).structureSourceChanged(structureSource);
            }
        }
    }
}
