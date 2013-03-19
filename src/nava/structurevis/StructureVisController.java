/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.structurevis;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.TreeModelListener;
import nava.data.io.IO;
import nava.data.types.Alignment;
import nava.data.types.DataSource;
import nava.data.types.DataType;
import nava.structurevis.data.*;
import nava.tasks.MappingTask;
import nava.ui.MainFrame;
import nava.ui.ProjectController;
import nava.ui.ProjectModel;
import nava.utils.Mapping;
import nava.utils.Pair;
import nava.utils.SafeListModel;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class StructureVisController implements ListDataListener {

    public StructureVisModel structureVisModel;
   
    transient ProjectController projectController;
    transient ProjectModel projectModel;

    public StructureVisController(ProjectController projectController, ProjectModel projectModel, File workingDirectory) {
        this.projectController = projectController;
        this.projectModel = projectModel;
        
        structureVisModel = new StructureVisModel();
        structureVisModel.substructureModel = new SubstructureModel(this);
        structureVisModel.structureVisModelFile = new File(workingDirectory.getAbsolutePath() + File.separatorChar + "structurevis.model");

        structureVisModel.substructureModel.loadData();
    }
    ArrayList<StructureVisView> structureVisViews = new ArrayList<>();

    public void addView(StructureVisView view) {
        System.out.println("addView:"+view);
        structureVisViews.add(view);
    }

    public void removeView(StructureVisView view) {
        structureVisViews.remove(view);
    }

    public void refreshMappings() {
        for (int i = 0; i < structureVisModel.structureSources.size(); i++) {
            StructureSource s = structureVisModel.structureSources.get(i);

            for (int j = 0; j < structureVisModel.structureVisDataOverlays1D.size(); j++) {
                DataOverlay1D dataSource = structureVisModel.structureVisDataOverlays1D.get(j);
                if (s.mappingSource != null && dataSource.mappingSource != null) {
                    MainFrame.taskManager.queueUITask(new MappingTask(this, dataSource.mappingSource, s.mappingSource));
                }
            }

            for (int j = 0; j < structureVisModel.structureVisDataOverlays2D.size(); j++) {
                DataOverlay2D dataSource = structureVisModel.structureVisDataOverlays2D.get(j);
                if (s.mappingSource != null && dataSource.mappingSource != null) {
                    MainFrame.taskManager.queueUITask(new MappingTask(this, dataSource.mappingSource, s.mappingSource));
                }
            }

            for (int j = 0; j < structureVisModel.annotationSources.size(); j++) {
                AnnotationSource annotationSource = structureVisModel.annotationSources.get(j);
                for (Feature f : annotationSource.features) // probably all have the same source, so this is not too slow
                {
                    if (s.mappingSource != null && f.mappingSource != null) {
                        //getMapping(f.mappingSource, s.mappingSource);
                        MainFrame.taskManager.queueUITask(new MappingTask(this, f.mappingSource, s.mappingSource));
                    }
                }
            }

            for (int j = 0; j < structureVisModel.nucleotideSources.size(); j++) {
                NucleotideComposition nucleotideComposition = structureVisModel.nucleotideSources.get(j);
                System.out.println("CREATING NUC " + j + "\t" + nucleotideComposition);
                if (s.mappingSource != null && nucleotideComposition.mappingSource != null) {
                    //getMapping(f.mappingSource, s.mappingSource);
                    MainFrame.taskManager.queueUITask(new MappingTask(this, nucleotideComposition.mappingSource, s.mappingSource));
                }
            }
        }
    }

    public void addStructureSource(StructureSource structureSource) {
        if (!structureVisModel.structureSources.contains(structureSource)) {
            if (structureSource.addMappingSourceAsNucleotideOverlay) {
                System.out.println(structureSource.details());
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
            structureVisModel.structureSources.addElement(structureSource);
            refreshMappings();
        }
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
        return getMapping(a, b, 1);
    }

    public Mapping getMapping(MappingSource a, MappingSource b, int select) {
        if (a == null || b == null) {
            return null;
        }

        Pair p = new Pair(a, b);
        Mapping m = structureVisModel.mappings.get(p);
        if (m == null) {
            m = createMapping(a, b, select);
            structureVisModel.mappings.put(p, m);
        }
        return m;
    }

    public Mapping createMapping(MappingSource a, MappingSource b, int select) {
        ArrayList<String> sequencesA = new ArrayList<>();
        ArrayList<String> sequencesNamesA = new ArrayList<>();
        ArrayList<String> sequencesB = new ArrayList<>();
        ArrayList<String> sequencesNamesB = new ArrayList<>();

        int maxSequencesToLoad = Math.max(Mapping.select, 100); // to ensure fast loading limit the number of sequences to be loaded

        if (a.alignmentSource != null) {
            IO.loadFastaSequences(Paths.get(a.alignmentSource.importedDataSourcePath).toFile(), sequencesA, sequencesNamesA, maxSequencesToLoad);
        }

        if (b.alignmentSource != null) {
            IO.loadFastaSequences(Paths.get(b.alignmentSource.importedDataSourcePath).toFile(), sequencesB, sequencesNamesB, maxSequencesToLoad);
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

        Mapping mapping = Mapping.createMapping(sequencesA, sequencesNamesA, sequencesB, sequencesNamesB, select);
        if(mapping != null)
        {
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


        System.out.println(structureVisModel.substructureModel.overlayNavigatorTreeModel.getTreeModelListeners().length);
        ArrayList<TreeModelListener> treeListenersList = new ArrayList<>();
        TreeModelListener[] overlayTreeListeners = structureVisModel.substructureModel.overlayNavigatorTreeModel.getTreeModelListeners();
        for (int i = 0; i < overlayTreeListeners.length; i++) {
            treeListenersList.add(overlayTreeListeners[i]);
            structureVisModel.substructureModel.overlayNavigatorTreeModel.removeTreeModelListener(overlayTreeListeners[i]);
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

        System.out.println(structureVisModel.substructureModel.overlayNavigatorTreeModel.getTreeModelListeners().length);

        try {
            ObjectOutput out = new ObjectOutputStream(new FileOutputStream(outFile));
            out.writeObject(this);
            out.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        // re-add the listeners, this is only necessary if the application stays open
        for (int i = 0; i < overlayTreeListeners.length; i++) {
            structureVisModel.substructureModel.overlayNavigatorTreeModel.addTreeModelListener(treeListenersList.get(i));
        }
        System.out.println(structureVisModel.substructureModel.overlayNavigatorTreeModel.getTreeModelListeners().length);
        System.out.println("StructureVis project saved");
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
    public void intervalAdded(ListDataEvent e) {
        System.out.println("intervalAdded 1 " + e.getSource());
        if (e.getSource() instanceof SafeListModel) {
            SafeListModel list = (SafeListModel) e.getSource();
            System.out.println("intervalAdded 2");
            for (int i = 0; i < structureVisViews.size(); i++) {
                for (int j = e.getIndex0() ; j <= e.getIndex1() ; j++) {
                    Object o = list.get(j);
                    System.out.println("intervalAdded 3");
                    if (o instanceof Overlay) {
                        System.out.println("intervalAdded 4");
                        System.out.println("intervalAdded 5"+ structureVisViews.get(i));
                        structureVisViews.get(i).dataOverlayAdded((Overlay) o);
                    }
                }
            }
        }
    }

    @Override
    public void intervalRemoved(ListDataEvent e) {
        if (e.getSource() instanceof SafeListModel) {
            SafeListModel list = (SafeListModel) e.getSource();
            for (int i = 0; i < structureVisViews.size(); i++) {
                for (int j = e.getIndex0() ; j <= e.getIndex1() ; j++) {
                    Object o = list.get(j);
                    if (o instanceof Overlay) {
                        structureVisViews.get(i).dataOverlayRemoved((Overlay) o);
                    }
                }
            }
        }
    }

    @Override
    public void contentsChanged(ListDataEvent e) {
        if (e.getSource() instanceof SafeListModel) {
            SafeListModel list = (SafeListModel) e.getSource();
            for (int i = 0; i < structureVisViews.size(); i++) {
                for (int j = e.getIndex0() ; j <= e.getIndex1() ; j++) {
                    Object o = list.get(j);
                    if (o instanceof Overlay) {
                        structureVisViews.get(i).dataOverlayChanged((Overlay) o);
                    }
                }
            }
        }
    }
}
