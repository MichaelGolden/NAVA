/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.structurevis;

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import javax.swing.event.EventListenerList;
import nava.structurevis.data.*;
import nava.structurevis.navigator.DataOverlayTreeModel;
import nava.utils.Mapping;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class SubstructureModel implements Serializable {

    public static Color missingDataColor = Color.gray;
    public static Color filteredDataColor = Color.darkGray;
    int sequenceLength;
    private AnnotationSource annotationSource = null;
    DataOverlay1D data1D = null;
    Mapping mapping1D = null;
    DataOverlay2D data2D = null;
    Mapping mapping2D = null;
    NucleotideComposition nucleotideSource = null;
    Mapping nucleotideMapping = null;
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
    transient StructureVisController structureVisController;
    public DataOverlayTreeModel overlayNavigatorTreeModel;

    public SubstructureModel(StructureVisController structureVisController) {
        this.structureVisController = structureVisController;
    }
    protected transient EventListenerList listeners = new EventListenerList();

    public void initialise(StructureVisController structureVisController) {
        listeners = new EventListenerList();
        this.structureVisController = structureVisController;
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

    public void setDataSource1D(DataOverlay1D dataSource1D) {
        if (dataSource1D != null) {
            dataSource1D.loadData();
        }
        this.data1D = dataSource1D;
        if (data1D != null && data1D.mappingSource != null && structureSource != null && structureSource.mappingSource != null) {
            mapping1D = structureVisController.getMapping(data1D.mappingSource, structureSource.mappingSource);
        }

        // set selection state
        for (int i = 0; i < structureVisController.structureVisModel.structureVisDataOverlays1D.size(); i++) {
            if (structureVisController.structureVisModel.structureVisDataOverlays1D.get(i).equals(data1D)) {
                structureVisController.structureVisModel.structureVisDataOverlays1D.get(i).setState(Overlay.OverlayState.PRIMARY_SELECTED);
            } else {
                structureVisController.structureVisModel.structureVisDataOverlays1D.get(i).setState(Overlay.OverlayState.UNSELECTED);
            }
        }
        fireDataSource1DChanged(dataSource1D);
    }

    public void setDataSource2D(DataOverlay2D dataSource2D) {
        if (dataSource2D != null) {
            dataSource2D.loadData();
        }
        this.data2D = dataSource2D;
        if (data2D != null && data2D.mappingSource != null && structureSource != null && structureSource.mappingSource != null) {
            mapping2D = structureVisController.getMapping(data2D.mappingSource, structureSource.mappingSource);
        }

        // set selection state
        for (int i = 0; i < structureVisController.structureVisModel.structureVisDataOverlays2D.size(); i++) {
            if (structureVisController.structureVisModel.structureVisDataOverlays2D.get(i).equals(data2D)) {
                structureVisController.structureVisModel.structureVisDataOverlays2D.get(i).setState(Overlay.OverlayState.PRIMARY_SELECTED);
            } else {
                structureVisController.structureVisModel.structureVisDataOverlays2D.get(i).setState(Overlay.OverlayState.UNSELECTED);
            }
        }
        fireDataSource2DChanged(dataSource2D);
    }

    public void setStructureSource(StructureSource structureSource) {
        if (structureSource != null) {
            structureSource.loadData();
        }
        this.structureSource = structureSource;
        if (structureSource != null) {

            this.sequenceLength = structureSource.pairedSites.length;
            if (data1D != null && data1D.mappingSource != null && structureSource.mappingSource != null) {
                mapping1D = structureVisController.getMapping(data1D.mappingSource, structureSource.mappingSource);
            }
            if (data2D != null && data2D.mappingSource != null && structureSource.mappingSource != null) {
                mapping2D = structureVisController.getMapping(data2D.mappingSource, structureSource.mappingSource);
            }
            if (nucleotideSource != null && nucleotideSource.mappingSource != null) {
                nucleotideMapping = structureVisController.getMapping(nucleotideSource.mappingSource, structureSource.mappingSource);
            }
        }

        for (int i = 0; i < structureVisController.structureVisModel.structureSources.size(); i++) {
            if (structureVisController.structureVisModel.structureSources.get(i).equals(structureSource)) {
                structureVisController.structureVisModel.structureSources.get(i).setState(Overlay.OverlayState.PRIMARY_SELECTED);
            } else {
                structureVisController.structureVisModel.structureSources.get(i).setState(Overlay.OverlayState.UNSELECTED);
            }
        }
        fireStructureSourceChanged(structureSource);
    }

    public void setNucleotideSource(NucleotideComposition nucleotideSource) {
        this.nucleotideSource = nucleotideSource;
        if (nucleotideSource != null && nucleotideSource.mappingSource != null && structureSource != null) {
            nucleotideMapping = structureVisController.getMapping(nucleotideSource.mappingSource, structureSource.mappingSource);
        }
        for (int i = 0; i < structureVisController.structureVisModel.nucleotideSources.size(); i++) {
            if (structureVisController.structureVisModel.nucleotideSources.get(i).equals(nucleotideSource)) {
                structureVisController.structureVisModel.nucleotideSources.get(i).setState(Overlay.OverlayState.PRIMARY_SELECTED);
            } else {
                structureVisController.structureVisModel.nucleotideSources.get(i).setState(Overlay.OverlayState.UNSELECTED);
            }
        }
        fireNucleotideSourceChanged(nucleotideSource);
    }

    public AnnotationSource getAnnotationSource() {
        return annotationSource;
    }

    public void setAnnotationSource(AnnotationSource annotationSource) {
        //this.annotationSource = AnnotationSource.getMappedAnnotations(annotationSource, structureSource, structureVisController);
        fireAnnotationSourceChanged(annotationSource);
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

    public void fireDataSource1DChanged(DataOverlay1D dataSource1D) {
        Object[] listeners = this.listeners.getListenerList();
        // Each listener occupies two elements - the first is the listener class
        // and the second is the listener instance
        for (int i = 0; i < listeners.length; i += 2) {
            if (listeners[i] == SubstructureModelListener.class) {
                ((SubstructureModelListener) listeners[i + 1]).dataSource1DChanged(dataSource1D);
            }
        }
    }

    public void fireDataSource2DChanged(DataOverlay2D dataSource2D) {
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

    public void fireAnnotationSourceChanged(AnnotationSource annotationSource) {
        Object[] listeners = this.listeners.getListenerList();
        // Each listener occupies two elements - the first is the listener class
        // and the second is the listener instance
        for (int i = 0; i < listeners.length; i += 2) {
            if (listeners[i] == SubstructureModelListener.class) {
                ((SubstructureModelListener) listeners[i + 1]).annotationSourceChanged(annotationSource);
            }
        }
    }

    public void fireNucleotideSourceChanged(NucleotideComposition nucleotideSource) {
        Object[] listeners = this.listeners.getListenerList();
        // Each listener occupies two elements - the first is the listener class
        // and the second is the listener instance
        for (int i = 0; i < listeners.length; i += 2) {
            if (listeners[i] == SubstructureModelListener.class) {
                ((SubstructureModelListener) listeners[i + 1]).nucleotideSourceChanged(nucleotideSource);
            }
        }
    }
}
