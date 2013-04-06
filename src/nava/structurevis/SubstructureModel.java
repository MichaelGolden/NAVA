/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.structurevis;

import java.awt.Color;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import javax.swing.event.EventListenerList;
import nava.data.types.SecondaryStructureData;
import nava.data.types.TabularField;
import nava.data.types.TabularFieldData;
import nava.structurevis.data.*;
import nava.structurevis.navigator.DataOverlayTreeModel;
import nava.ui.MainFrame;
import nava.ui.ProgressBarMonitor;
import nava.ui.ProjectModel;
import nava.utils.Mapping;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class SubstructureModel implements Serializable {

    private static final long serialVersionUID = -6779025860776191876L;
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
    public StructureOverlay structureOverlay = null;
    int numbering = 0;
    Substructure structure = null;
    transient DistanceMatrix distanceMatrix = null;
    transient DistanceMatrix structureDistanceMatrix = null;
    int maxDistance = -1;
    protected transient EventListenerList listeners = new EventListenerList();
    /*
     * boolean useLowerThreshold1D = false; boolean useUpperThreshold1D = false;
     * boolean useLowerThreshold2D = false; boolean useUpperThreshold2D = false;
     * double thresholdMin1D; double thresholdMax1D; double thresholdMin2D;
     * double thresholdMax2D;
     */
    transient StructureVisController structureVisController;

    public SubstructureModel(StructureVisController structureVisController) {
        this.structureVisController = structureVisController;
    }

    public void initialise(StructureVisController structureVisController) {
        listeners = new EventListenerList();
        this.structureVisController = structureVisController;
        loadData();
    }

    public void loadData() {
        if (data1D != null) {
            data1D.loadData();
        }
        if (data2D != null) {
            data2D.loadData();
        }
        if (structureOverlay != null) {
            structureOverlay.loadData();
        }
    }

    public void setAsPrimaryOverlay(Overlay overlay) {
        if (overlay instanceof DataOverlay1D) {
            this.setOverlay1D((DataOverlay1D) overlay);
        } else if (overlay instanceof DataOverlay2D) {
            this.setOverlay2D((DataOverlay2D) overlay);
        } else if (overlay instanceof StructureOverlay) {
            this.setStructureOverlay((StructureOverlay) overlay);
        } else if (overlay instanceof NucleotideComposition) {
            this.setNucleotideOverlay((NucleotideComposition) overlay);
        }

    }

    public void setOverlay1D(final DataOverlay1D dataSource1D) {

        this.data1D = dataSource1D;

        Thread taskThread = new Thread() {

            public void run() {
                try {
                    SwingUtilities.invokeAndWait(
                            new Runnable() {

                                public void run() {
                                    MainFrame.progressBarMonitor.set(true, ProgressBarMonitor.CREATE_MAPPING, 0);
                                    MainFrame.self.setEnabled(false);
                                }
                            });

                    if (dataSource1D != null) {
                        dataSource1D.loadData();
                    }
                    if (data1D != null && data1D.mappingSource != null && structureOverlay != null && structureOverlay.mappingSource != null) {
                        mapping1D = structureVisController.getMapping(structureOverlay.mappingSource, data1D.mappingSource);
                    }
                    fireDataOverlay1DChanged(dataSource1D);

                    MainFrame.progressBarMonitor.set(false, ProgressBarMonitor.INACTIVE, ProgressBarMonitor.INACTIVE_VALUE);
                    MainFrame.self.setEnabled(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        taskThread.start();

        // set selection state
        for (int i = 0; i < structureVisController.structureVisModel.structureVisDataOverlays1D.size(); i++) {
            if (structureVisController.structureVisModel.structureVisDataOverlays1D.get(i).equals(data1D)) {
                structureVisController.structureVisModel.structureVisDataOverlays1D.get(i).setState(Overlay.OverlayState.PRIMARY_SELECTED);
            } else {
                structureVisController.structureVisModel.structureVisDataOverlays1D.get(i).setState(Overlay.OverlayState.UNSELECTED);
            }
        }
        
    }

    public void setOverlay2D(final DataOverlay2D dataSource2D) {

        this.data2D = dataSource2D;

        final Runnable progressBarThread = new Runnable() {

            public void run() {
                MainFrame.progressBarMonitor.set(true, ProgressBarMonitor.CREATE_MAPPING, 0);
                MainFrame.self.setEnabled(false);
            }
        };

        Thread taskThread = new Thread() {

            public void run() {
                try {
                    SwingUtilities.invokeAndWait(progressBarThread);

                    if (data2D != null && data2D.mappingSource != null && structureOverlay != null && structureOverlay.mappingSource != null) {
                        mapping2D = structureVisController.getMapping(structureOverlay.mappingSource, data2D.mappingSource);
                    }
                    fireDataOverlay2DChanged(dataSource2D);

                    MainFrame.progressBarMonitor.set(false, ProgressBarMonitor.INACTIVE, ProgressBarMonitor.INACTIVE_VALUE);
                    MainFrame.self.setEnabled(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        taskThread.start();

        // set selection state
        for (int i = 0; i < structureVisController.structureVisModel.structureVisDataOverlays2D.size(); i++) {
            if (structureVisController.structureVisModel.structureVisDataOverlays2D.get(i).equals(data2D)) {
                structureVisController.structureVisModel.structureVisDataOverlays2D.get(i).setState(Overlay.OverlayState.PRIMARY_SELECTED);
            } else {
                structureVisController.structureVisModel.structureVisDataOverlays2D.get(i).setState(Overlay.OverlayState.UNSELECTED);
            }
        }
    }

    public void setStructureOverlay(final StructureOverlay structureOverlay) {

        this.structureOverlay = structureOverlay;

        final Runnable progressBarThread = new Runnable() {

            public void run() {
                MainFrame.progressBarMonitor.set(true, ProgressBarMonitor.CREATE_MAPPING, 0);
                MainFrame.self.setEnabled(false);
            }
        };

        Thread taskThread = new Thread() {

            public void run() {
                try {
                    SwingUtilities.invokeAndWait(progressBarThread);

                    if (structureOverlay != null) {
                        structureOverlay.loadData();
                    }


                    if (structureOverlay != null) {

                        SubstructureModel.this.sequenceLength = structureOverlay.pairedSites.length;
                        if (data1D != null && data1D.mappingSource != null && structureOverlay.mappingSource != null) {
                            mapping1D = structureVisController.getMapping(structureOverlay.mappingSource, data1D.mappingSource);
                        }
                        if (data2D != null && data2D.mappingSource != null && structureOverlay.mappingSource != null) {
                            mapping2D = structureVisController.getMapping(structureOverlay.mappingSource, data2D.mappingSource);
                        }
                        if (nucleotideSource != null && nucleotideSource.mappingSource != null) {
                            nucleotideMapping = structureVisController.getMapping(structureOverlay.mappingSource, nucleotideSource.mappingSource);
                        }
                    }
                    fireStructureOverlayChanged(structureOverlay);

                    MainFrame.progressBarMonitor.set(false, ProgressBarMonitor.INACTIVE, ProgressBarMonitor.INACTIVE_VALUE);
                    MainFrame.self.setEnabled(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        taskThread.start();

        for (int i = 0; i < structureVisController.structureVisModel.structureSources.size(); i++) {
            if (structureVisController.structureVisModel.structureSources.get(i).equals(structureOverlay)) {
                structureVisController.structureVisModel.structureSources.get(i).setState(Overlay.OverlayState.PRIMARY_SELECTED);
            } else {
                structureVisController.structureVisModel.structureSources.get(i).setState(Overlay.OverlayState.UNSELECTED);
            }
        }
    }

    public void setNucleotideOverlay(final NucleotideComposition nucleotideOverlay) {
        this.nucleotideSource = nucleotideOverlay;
        if (nucleotideOverlay != null && nucleotideOverlay.mappingSource != null && structureOverlay != null) {

            final Runnable progressBarThread = new Runnable() {

                public void run() {
                    MainFrame.progressBarMonitor.set(true, ProgressBarMonitor.CREATE_MAPPING, 0);
                    MainFrame.self.setEnabled(false);
                }
            };
            Thread taskThread = new Thread() {

                public void run() {
                    try {
                        SwingUtilities.invokeAndWait(progressBarThread);

                        nucleotideMapping = structureVisController.getMapping(structureOverlay.mappingSource, nucleotideOverlay.mappingSource);
                        fireNucleotideOverlayChanged(nucleotideOverlay);

                        MainFrame.progressBarMonitor.set(false, ProgressBarMonitor.INACTIVE, ProgressBarMonitor.INACTIVE_VALUE);
                        MainFrame.self.setEnabled(true);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            taskThread.start();
        }

        for (int i = 0; i < structureVisController.structureVisModel.nucleotideSources.size(); i++) {
            if (structureVisController.structureVisModel.nucleotideSources.get(i).equals(nucleotideOverlay)) {
                structureVisController.structureVisModel.nucleotideSources.get(i).setState(Overlay.OverlayState.PRIMARY_SELECTED);
            } else {
                structureVisController.structureVisModel.nucleotideSources.get(i).setState(Overlay.OverlayState.UNSELECTED);
            }
        }
    }

    public AnnotationSource getAnnotationSource() {
        return annotationSource;
    }

    public void setAnnotationSource(AnnotationSource annotationSource) {
        //this.annotationSource = AnnotationSource.getMappedAnnotations(annotationSource, structureSource, structureVisController);
        fireAnnotationSourceChanged(annotationSource);
    }

    public ArrayList<Substructure> getSubstructures() {
        if (structureOverlay != null) {
            return structureOverlay.substructureList.substructures;
        }

        return new ArrayList<>();
    }

    public void addSubstructureModelListener(SubstructureModelListener listener) {
        listeners.add(SubstructureModelListener.class, listener);
    }

    public void removeSubstructureModelListener(SubstructureModelListener listener) {
        listeners.remove(SubstructureModelListener.class, listener);
    }

    public void fireDataOverlay1DChanged(DataOverlay1D dataSource1D) {
        Object[] listeners = this.listeners.getListenerList();
        // Each listener occupies two elements - the first is the listener class
        // and the second is the listener instance
        for (int i = 0; i < listeners.length; i += 2) {
            if (listeners[i] == SubstructureModelListener.class) {
                ((SubstructureModelListener) listeners[i + 1]).dataSource1DChanged(dataSource1D);
            }
        }
    }

    public void fireDataOverlay2DChanged(DataOverlay2D dataSource2D) {
        Object[] listeners = this.listeners.getListenerList();
        // Each listener occupies two elements - the first is the listener class
        // and the second is the listener instance
        for (int i = 0; i < listeners.length; i += 2) {
            if (listeners[i] == SubstructureModelListener.class) {
                ((SubstructureModelListener) listeners[i + 1]).dataSource2DChanged(dataSource2D);
            }
        }
    }

    public void fireStructureOverlayChanged(StructureOverlay structureSource) {
        Object[] listeners = this.listeners.getListenerList();
        // Each listener occupies two elements - the first is the listener class
        // and the second is the listener instance
        for (int i = 0; i < listeners.length; i += 2) {
            if (listeners[i] == SubstructureModelListener.class) {
                System.out.println("fireStructureOverlayChanged " + ((SubstructureModelListener) listeners[i + 1]));
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

    public void fireNucleotideOverlayChanged(NucleotideComposition nucleotideSource) {
        Object[] listeners = this.listeners.getListenerList();
        // Each listener occupies two elements - the first is the listener class
        // and the second is the listener instance
        for (int i = 0; i < listeners.length; i += 2) {
            if (listeners[i] == SubstructureModelListener.class) {
                ((SubstructureModelListener) listeners[i + 1]).nucleotideSourceChanged(nucleotideSource);
            }
        }
    }

    public void saveDataOverlay1DAndStructure(DataOverlay1D dataOverlay1D, File outFile) throws IOException {
        BufferedWriter buffer = new BufferedWriter(new FileWriter(outFile));
        SecondaryStructureData structure = structureOverlay.structure.getObject(ProjectModel.path, MainFrame.dataSourceCache);
        buffer.write("Structure position,Paired position,Data position,Data value\n");

        Mapping mapping = structureVisController.getMapping(structureOverlay.mappingSource, dataOverlay1D.mappingSource);
        dataOverlay1D.loadData();
        for (int i = 0; i < structure.pairedSites.length; i++) {

            int dataPos = mapping.aToB(i);
            if (dataPos != -1) {
                buffer.write((i + 1) + "," + (structure.pairedSites[i] == 0 ? "-" : structure.pairedSites[i] +1) + "," + (dataPos+1) + "," + dataOverlay1D.data[i] + "\n");
            } else {
                buffer.write((i + 1) + "," + (structure.pairedSites[i] == 0 ? "-" : structure.pairedSites[i] +1) + "," + "," + "\n");
            }
        }
        buffer.close();
    }
}
