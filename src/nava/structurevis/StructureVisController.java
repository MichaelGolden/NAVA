/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.structurevis;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import javax.swing.event.ListDataEvent;
import javax.swing.event.TreeModelListener;
import nava.data.io.IO;
import nava.data.types.Alignment;
import nava.data.types.DataSource;
import nava.data.types.DataType;
import nava.structurevis.data.*;
import nava.tasks.MappingTask;
import nava.tasks.ProcessReference;
import nava.ui.MainFrame;
import nava.ui.ProjectController;
import nava.ui.ProjectModel;
import nava.ui.ProjectView;
import nava.utils.*;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class StructureVisController implements SafeListListener, ProjectView {

    public StructureVisModel structureVisModel;
    ProjectController projectController;
    ProjectModel projectModel;

    public StructureVisController(ProjectController projectController, ProjectModel projectModel) {
        this.projectController = projectController;
        this.projectController.addView(this);
        this.projectModel = projectModel;

        openStructureVisModel(new StructureVisModel());
    }

    public void openStructureVisModel(StructureVisModel structureVisModel) {
        this.structureVisModel = structureVisModel;
        if (structureVisModel.substructureModel == null) {
            structureVisModel.substructureModel = new SubstructureModel(this);
        } else {
            this.structureVisModel.substructureModel.structureVisController = this;
        }

        structureVisModel.substructureModel.loadData();
        for (int i = 0; i < structureVisViews.size(); i++) {
            structureVisViews.get(i).structureVisModelChanged(structureVisModel);
        }
    }

    public File getWorkingDirectory() {
        return projectController.projectModel.getProjectPath().toFile();
    }
    ArrayList<StructureVisView> structureVisViews = new ArrayList<>();

    public void addView(StructureVisView view) {
        System.out.println("addView:" + view);
        structureVisViews.add(view);
    }

    public void removeView(StructureVisView view) {
        structureVisViews.remove(view);
    }

    public void refreshMappings() {
        for (int i = 0; i < structureVisModel.structureSources.size(); i++) {
            StructureOverlay s = structureVisModel.structureSources.get(i);

            for (int j = 0; j < structureVisModel.structureVisDataOverlays1D.size(); j++) {
                DataOverlay1D dataSource = structureVisModel.structureVisDataOverlays1D.get(j);
                if (s.mappingSource != null && dataSource.mappingSource != null) {
                    if (!structureVisModel.mappings.containsKey(new Pair<>(s.mappingSource, dataSource.mappingSource))) {
                        MainFrame.taskManager.queueTask(new MappingTask(this, s.mappingSource, dataSource.mappingSource), true);
                    }
                }
            }

            for (int j = 0; j < structureVisModel.structureVisDataOverlays2D.size(); j++) {
                DataOverlay2D dataSource = structureVisModel.structureVisDataOverlays2D.get(j);
                if (s.mappingSource != null && dataSource.mappingSource != null) {
                    if (!structureVisModel.mappings.containsKey(new Pair<>(s.mappingSource, dataSource.mappingSource))) {
                        MainFrame.taskManager.queueTask(new MappingTask(this, s.mappingSource, dataSource.mappingSource), true);
                    }
                }
            }

            for (int j = 0; j < structureVisModel.annotationSources.size(); j++) {
                AnnotationSource annotationSource = structureVisModel.annotationSources.get(j);
                if (annotationSource != null) {
                    for (Feature f : annotationSource.features) // probably all have the same source, so this is not too slow
                    {
                        if (s.mappingSource != null && f.mappingSource != null) {
                            if (!structureVisModel.mappings.containsKey(new Pair<>(s.mappingSource, f.mappingSource))) {
                                MainFrame.taskManager.queueTask(new MappingTask(this, s.mappingSource, f.mappingSource), true);
                            }
                        }
                    }
                }
            }

            for (int j = 0; j < structureVisModel.nucleotideSources.size(); j++) {
                NucleotideComposition nucleotideComposition = structureVisModel.nucleotideSources.get(j);
                System.out.println("CREATING NUC " + j + "\t" + nucleotideComposition);
                if (s.mappingSource != null && nucleotideComposition.mappingSource != null) {
                    if (!structureVisModel.mappings.containsKey(new Pair<>(s.mappingSource, nucleotideComposition.mappingSource))) {
                        MainFrame.taskManager.queueTask(new MappingTask(this, s.mappingSource, nucleotideComposition.mappingSource), true);
                    }
                }
            }
        }
    }

    public void addStructureSource(StructureOverlay structureSource) {
        if (!structureVisModel.structureSources.contains(structureSource)) {
            if (structureSource.addMappingSourceAsNucleotideOverlay) {
                addAssociatedStructureMappingSource(structureSource);
            }
            structureVisModel.structureSources.addElement(structureSource);
            refreshMappings();
        }
    }

    public void addAssociatedStructureMappingSource(StructureOverlay structureSource) {
        switch (structureSource.mappingSourceOption) {
            case ALIGNMENT:
                addNucleotideCompositionSource(NucleotideCompositionPanel.getNucleotideSource(structureSource.mappingSource.alignmentSource));
                break;
            case EMBEDDED:
                if (structureSource.structure != null && structureSource.structure.parentSource != null && structureSource.structure.parentSource instanceof Alignment) {
                    addNucleotideCompositionSource(NucleotideCompositionPanel.getNucleotideSource((Alignment) structureSource.structure.parentSource));
                    break;
                }
            case STRING:
                try {
                    File dir = new File(System.getProperty("java.io.tmpdir") + File.separator + System.currentTimeMillis() + File.separator);
                    dir.mkdirs();
                    File fastaFile = new File(dir.getAbsolutePath() + File.separator + "sequence.fas");
                    BufferedWriter buffer = new BufferedWriter(new FileWriter(fastaFile));
                    buffer.write(">seq");
                    buffer.newLine();
                    buffer.write(structureSource.mappingSource.sequence);
                    buffer.newLine();
                    buffer.close();

                    DataSource dataSource = projectController.importDataSourceFromFile(fastaFile, new DataType(DataType.Primary.ALIGNMENT, DataType.FileFormat.FASTA));
                    dataSource.title = structureSource.title;
                    if (dataSource instanceof Alignment) {
                        addNucleotideCompositionSource(NucleotideCompositionPanel.getNucleotideSource((Alignment) dataSource));
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
        }
    }

    /**
     * Replace the current overlay with a new overlay (usually a edited version
     * of the same overlay)
     *
     * @param currentOverlay
     * @param structureSource
     */
    public void setStructureSource(Overlay currentOverlay, StructureOverlay structureSource) {
        int index = structureVisModel.structureSources.indexOf(currentOverlay);
        if (index >= 0) {
            structureVisModel.structureSources.set(index, structureSource);
            if (structureSource.addMappingSourceAsNucleotideOverlay) {
                addAssociatedStructureMappingSource(structureSource);
            }
        } else {
            System.err.println("ERR");
        }
        refreshMappings();
    }

    /**
     * Replace the current overlay with a new overlay (usually a edited version
     * of the same overlay)
     *
     * @param currentOverlay
     * @param structureSource
     */
    public void setStructureVisDataSource1D(Overlay currentOverlay, DataOverlay1D dataSource) {
        int index = structureVisModel.structureVisDataOverlays1D.indexOf(currentOverlay);
        if (index >= 0) {
            structureVisModel.structureVisDataOverlays1D.set(index, dataSource);
        } else {
            System.err.println("ERR");
        }
        refreshMappings();
    }

    /**
     * Replace the current overlay with a new overlay (usually a edited version
     * of the same overlay)
     *
     * @param currentOverlay
     * @param structureSource
     */
    public void setStructureVisDataSource2D(Overlay currentOverlay, DataOverlay2D dataSource) {
        int index = structureVisModel.structureVisDataOverlays2D.indexOf(currentOverlay);
        if (index >= 0) {
            structureVisModel.structureVisDataOverlays2D.set(index, dataSource);
        } else {
            System.err.println("ERR");
        }
        refreshMappings();
    }

    /**
     * Replace the current overlay with a new overlay (usually a edited version
     * of the same overlay)
     *
     * @param currentOverlay
     * @param structureSource
     */
    public void setAnnotationsSource(Overlay currentOverlay, AnnotationSource annotationSource) {
        int index = structureVisModel.annotationSources.indexOf(currentOverlay);
        if (index >= 0) {
            structureVisModel.annotationSources.set(index, annotationSource);
        } else {
            System.err.println("ERR");
        }
        refreshMappings();
    }

    /**
     * Replace the current overlay with a new overlay (usually a edited version
     * of the same overlay)
     *
     * @param currentOverlay
     * @param structureSource
     */
    public void setNucleotideCompositionSource(Overlay currentOverlay, NucleotideComposition nucleotideComposition) {
        int index = structureVisModel.nucleotideSources.indexOf(currentOverlay);
        if (index >= 0) {
            structureVisModel.nucleotideSources.set(index, nucleotideComposition);
        } else {
            System.err.println("ERR");
        }
        refreshMappings();
    }

    public void addStructureVisDataSource1D(DataOverlay1D dataSource) {
        structureVisModel.structureVisDataOverlays1D.addElement(dataSource);
        refreshMappings();
    }

    public void addStructureVisDataSource2D(DataOverlay2D dataSource) {
        structureVisModel.structureVisDataOverlays2D.addElement(dataSource);
        System.out.println(structureVisModel.structureVisDataOverlays2D);
        refreshMappings();
    }

    public void addAnnotationsSource(AnnotationSource annotationSource) {
        structureVisModel.annotationSources.addElement(annotationSource);
        refreshMappings();
    }

    public void addNucleotideCompositionSource(NucleotideComposition nucleotideComposition) {
        structureVisModel.nucleotideSources.addElement(nucleotideComposition);
        refreshMappings();
    }

    public Mapping getMapping(MappingSource a, MappingSource b) {
        return getMapping(a, b, 1, new ProcessReference());
    }

    public Mapping getMapping(MappingSource a, MappingSource b, int select, ProcessReference processReference) {
        if (a == null || b == null) {
            return null;
        }

        Pair p = new Pair(a, b);
        Mapping m = structureVisModel.mappings.get(p);
        if (m == null) {
            m = createMapping(a, b, select, processReference);
            if (m != null) {
                structureVisModel.mappings.put(p, m);
            } else {
                // throw new Error("Mapping was null");
            }
        }
        return m;
    }

    public Mapping createMapping(MappingSource a, MappingSource b, int select, ProcessReference processReference) {
        ArrayList<String> sequencesA = new ArrayList<>();
        ArrayList<String> sequencesNamesA = new ArrayList<>();
        ArrayList<String> sequencesB = new ArrayList<>();
        ArrayList<String> sequencesNamesB = new ArrayList<>();

        int maxSequencesToLoad = Math.max(Mapping.select, 100); // to ensure fast loading limit the number of sequences to be loaded

        if (a.alignmentSource != null) {
            IO.loadFastaSequences(Paths.get(a.alignmentSource.getImportedDataSourcePath(projectModel.getProjectPathString())).toFile(), sequencesA, sequencesNamesA, maxSequencesToLoad);
        }

        if (b.alignmentSource != null) {
            IO.loadFastaSequences(Paths.get(b.alignmentSource.getImportedDataSourcePath(projectModel.getProjectPathString())).toFile(), sequencesB, sequencesNamesB, maxSequencesToLoad);
        }

        if (a.sequence != null) {
            sequencesA.add(a.sequence);
            sequencesNamesA.add("seq1");
        }

        if (b.sequence != null) {
            sequencesB.add(b.sequence);
            sequencesNamesB.add("seq2");
        }


        /*
         * System.out.println("Mapping"); System.out.println((a.sequenceSource
         * == null) + "\t" + (a.sequence == null));
         * System.out.println((b.sequenceSource == null) + "\t" + (b.sequence ==
         * null)); System.out.println(sequencesA);
         * System.out.println(sequencesB); System.out.println("----------");
         */

        Mapping mapping = Mapping.createMapping(sequencesA, sequencesNamesA, sequencesB, sequencesNamesB, select, processReference);
        if (mapping != null) {
            structureVisModel.mappings.put(new Pair(a, b), mapping);
        }
        return mapping;
    }
    // public ArrayList<StructureSource> structureSourcesPersistent = new ArrayList<>();
    // public ArrayList<DataOverlay1D> structureVisDataSources1DPersistent = new ArrayList<>();
    //public ArrayList<DataOverlay2D> structureVisDataSources2DPersistent = new ArrayList<>();
    //public ArrayList<AnnotationSource> annotationSourcesPersistent = new ArrayList<>();
    // public ArrayList<NucleotideComposition> nucleotideSourcesPersistent = new ArrayList<>();

    public void saveStructureVisModel2(File outFile) {
        //structureSourcesPersistent = Collections.list(structureSources.elements());
        //structureVisDataSources1DPersistent = Collections.list(structureVisDataOverlays1D.elements());
        //structureVisDataSources2DPersistent = Collections.list(structureVisDataOverlays2D.elements());
        //annotationSourcesPersistent = Collections.list(annotationSources.elements());
        //nucleotideSourcesPersistent = Collections.list(nucleotideSources.elements());


        System.out.println(structureVisModel.overlayNavigatorTreeModel.getTreeModelListeners().length);
        ArrayList<TreeModelListener> treeListenersList = new ArrayList<>();
        TreeModelListener[] overlayTreeListeners = structureVisModel.overlayNavigatorTreeModel.getTreeModelListeners();
        for (int i = 0; i < overlayTreeListeners.length; i++) {
            treeListenersList.add(overlayTreeListeners[i]);
            structureVisModel.overlayNavigatorTreeModel.removeTreeModelListener(overlayTreeListeners[i]);
        }

        TreeModelListener[] navigatorTreeListeners = projectModel.navigatorTreeModel.getTreeModelListeners();
        for (int i = 0; i < navigatorTreeListeners.length; i++) {
            //treeListenersList.add(navigatorTreeListeners[i]);
            projectModel.navigatorTreeModel.removeTreeModelListener(navigatorTreeListeners[i]);
        }

        /*
         * TreeModelListener[] projectTreeListneers =
         * substructureModel.overlayNavigatorTreeModel.getTreeModelListeners();
         * for (int i = 0; i < projectTreeListneers.length; i++) { //
         * treeListenersList.add(projectTreeListneers[i]);
         * substructureModel.overlayNavigatorTreeModel.removeTreeModelListener(projectTreeListneers[i]);
         * }
         */

        System.out.println(structureVisModel.overlayNavigatorTreeModel.getTreeModelListeners().length);

        try {
            ObjectOutput out = new ObjectOutputStream(new FileOutputStream(outFile));
            out.writeObject(this);
            out.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        // re-add the listeners, this is only necessary if the application stays open
        for (int i = 0; i < overlayTreeListeners.length; i++) {
            structureVisModel.overlayNavigatorTreeModel.addTreeModelListener(treeListenersList.get(i));
        }
    }

    public static StructureVisController loadProject2(File inFile, ProjectController projectController) throws IOException, ClassNotFoundException {
        ObjectInputStream in = new ObjectInputStream(new FileInputStream(inFile));

        //ProjectController oldController = this
        StructureVisController ret = (StructureVisController) in.readObject();
        ret.projectController = projectController;
        ret.projectModel = projectController.projectModel;

        /*
         * ret.structureSources = new DefaultListModel<>(); for (StructureSource
         * s : ret.structureSourcesPersistent) {
         * ret.structureSources.addElement(s); } ret.structureSourcesPersistent
         * = null;
         *
         * ret.structureVisDataOverlays1D = new DefaultListModel<>(); for
         * (DataOverlay1D s : ret.structureVisDataSources1DPersistent) {
         * ret.structureVisDataOverlays1D.addElement(s); }
         * ret.structureVisDataSources1DPersistent = null;
         *
         * ret.structureVisDataOverlays2D = new DefaultListModel<>(); for
         * (DataOverlay2D s : ret.structureVisDataSources2DPersistent) {
         * ret.structureVisDataOverlays2D.addElement(s); }
         * ret.structureVisDataSources2DPersistent = null;
         *
         * ret.annotationSources = new DefaultListModel<>(); for
         * (AnnotationSource annotationSource : ret.annotationSourcesPersistent)
         * { ret.annotationSources.addElement(annotationSource); }
         * ret.annotationSourcesPersistent = null;
         *
         * ret.nucleotideSources = new DefaultListModel<>(); for
         * (NucleotideComposition nucleotideSource :
         * ret.nucleotideSourcesPersistent) {
         * ret.nucleotideSources.addElement(nucleotideSource); }
         * ret.nucleotideSourcesPersistent = null;
         */

        //ret.structureVisModel.substructureModel.initialise(this);
        in.close();

        System.out.println("StructureVis project loaded");
        return ret;
    }

    @Override
    public void intervalAdded(SafeListEvent e) {
        if (e.getSource() instanceof SafeListModel) {
            SafeListModel list = (SafeListModel) e.getSource();
            for (int i = 0; i < structureVisViews.size(); i++) {
                for (int j = e.getIndex0(); j <= e.getIndex1(); j++) {
                    Object o = list.get(j);
                    if (o instanceof Overlay) {
                        System.out.println("Overlay added " + o);
                        structureVisViews.get(i).dataOverlayAdded((Overlay) o);
                    }
                }
            }
        }
    }

    @Override
    public void intervalRemoved(SafeListEvent e) {
        if (e.getSource() instanceof SafeListModel) {
            SafeListModel list = (SafeListModel) e.getSource();
            for (int i = 0; i < structureVisViews.size(); i++) {
                for (int j = e.getIndex0(); j <= e.getIndex1(); j++) {
                    Object o = list.get(j);
                    if (o instanceof Overlay) {
                        System.out.println("Overlay removed " + o);
                        structureVisViews.get(i).dataOverlayRemoved((Overlay) o);
                    }
                }
            }
        }
    }

    @Override
    public void contentsChanged(SafeListEvent e) {
        if (e.getSource() instanceof SafeListModel) {
            SafeListModel list = (SafeListModel) e.getSource();
            for (int i = 0; i < structureVisViews.size(); i++) {
                for (int j = e.getIndex0(); j <= e.getIndex1(); j++) {
                    Object o = list.get(j);
                    if (o instanceof Overlay) {
                        System.out.println("CONENTS CHANGEDxxx" + e.getOldElement().toString() + "\t" + e.getNewElement().toString());
                        structureVisViews.get(i).dataOverlayChanged((Overlay) e.getOldElement(), (Overlay) e.getNewElement());
                    }
                }
            }
        }
    }

    @Override
    public void projectModelChanged(ProjectModel newProjectModel) {
        this.projectModel = newProjectModel;

    }

    @Override
    public void dataSourcesLoaded() {
    }

    public void dataSourcesIntervalAdded(ListDataEvent e) {
        autoCreateMappings();
    }

    public void autoCreateMappings() {
        System.out.println("autoCreateMappings");
        System.out.println("V" + structureVisModel.structureSources.size());
        for (int j = 0; j < projectModel.dataSources.size(); j++) {
            DataSource d = projectModel.dataSources.get(j);
            MappingSource m = null;
            if (d instanceof Alignment) {
                Alignment alignment = (Alignment) d;
                m = new MappingSource(alignment);
            }
            System.out.println("A" + structureVisModel.structureSources.size());
            for (int i = 0; i < structureVisModel.structureSources.size(); i++) {
                StructureOverlay s = structureVisModel.structureSources.get(i);
                System.out.println("N" + i);
                if (s.mappingSource != null && m != null) {
                    System.out.println("M" + i);
                    if (!this.structureVisModel.mappings.containsKey(new Pair<>(s.mappingSource, m))) {
                        System.out.println("Q" + i);
                        MainFrame.taskManager.queueTask(new MappingTask(this, s.mappingSource, m), true);
                    }
                }
            }
        }
    }

    @Override
    public void dataSourcesIntervalRemoved(ListDataEvent e) {
    }

    @Override
    public void dataSourcesContentsChanged(ListDataEvent e) {
    }
}
