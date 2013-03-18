/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.structurevis;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import javax.swing.DefaultListModel;
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

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class StructureVisController implements Serializable {

    public transient DefaultListModel<StructureSource> structureSources = new DefaultListModel<>();
    public transient DefaultListModel<DataOverlay1D> structureVisDataOverlays1D = new DefaultListModel<>();
    public transient DefaultListModel<DataOverlay2D> structureVisDataOverlays2D = new DefaultListModel<>();
    public transient DefaultListModel<AnnotationSource> annotationSources = new DefaultListModel<>();
    public transient DefaultListModel<NucleotideComposition> nucleotideSources = new DefaultListModel<>();
    public SubstructureModel substructureModel = null;
    Hashtable<Pair<MappingSource, MappingSource>, Mapping> mappings = new Hashtable<>();
    public File structureVisModelFile = null;
    transient ProjectController projectController;
    transient ProjectModel projectModel;

    public StructureVisController(ProjectController projectController, ProjectModel projectModel, File workingDirectory) {
        this.projectController = projectController;
        this.projectModel = projectModel;
        substructureModel = new SubstructureModel(this);
        structureVisModelFile = new File(workingDirectory.getAbsolutePath() + File.separatorChar + "structurevis.model");

        substructureModel.loadData();
    }

    public void refreshMappings() {
        for (int i = 0; i < structureSources.size(); i++) {
            StructureSource s = structureSources.get(i);

            for (int j = 0; j < structureVisDataOverlays1D.size(); j++) {
                DataOverlay1D dataSource = structureVisDataOverlays1D.get(j);
                if (s.mappingSource != null && dataSource.mappingSource != null) {
                    MainFrame.taskManager.queueUITask(new MappingTask(this, dataSource.mappingSource, s.mappingSource));
                }
            }

            for (int j = 0; j < structureVisDataOverlays2D.size(); j++) {
                DataOverlay2D dataSource = structureVisDataOverlays2D.get(j);
                if (s.mappingSource != null && dataSource.mappingSource != null) {
                    MainFrame.taskManager.queueUITask(new MappingTask(this, dataSource.mappingSource, s.mappingSource));
                }
            }

            for (int j = 0; j < annotationSources.size(); j++) {
                AnnotationSource annotationSource = annotationSources.get(j);
                for (Feature f : annotationSource.features) // probably all have the same source, so this is not too slow
                {
                    if (s.mappingSource != null && f.mappingSource != null) {
                        //getMapping(f.mappingSource, s.mappingSource);
                        MainFrame.taskManager.queueUITask(new MappingTask(this, f.mappingSource, s.mappingSource));
                    }
                }
            }

            for (int j = 0; j < nucleotideSources.size(); j++) {
                NucleotideComposition nucleotideComposition = nucleotideSources.get(j);
                System.out.println("CREATING NUC " + j+"\t"+nucleotideComposition);
                if (s.mappingSource != null && nucleotideComposition.mappingSource != null) {
                    //getMapping(f.mappingSource, s.mappingSource);
                    MainFrame.taskManager.queueUITask(new MappingTask(this, nucleotideComposition.mappingSource, s.mappingSource));
                }
            }
        }
    }

    public void addStructureSource(StructureSource structureSource) {
        if (!structureSources.contains(structureSource)) {
            if (structureSource.addMappingSourceAsNucleotideOverlay) {
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
            structureSources.addElement(structureSource);
            refreshMappings();
        }
    }

    public void addStructureVisDataSource1D(DataOverlay1D dataSource) {
        structureVisDataOverlays1D.addElement(dataSource);
        refreshMappings();
    }

    public void addStructureVisDataSource2D(DataOverlay2D dataSource) {
        structureVisDataOverlays2D.addElement(dataSource);
        System.out.println(structureVisDataOverlays2D);
        refreshMappings();
    }

    public void addAnnotationsSource(AnnotationSource annotationSource) {
        annotationSources.addElement(annotationSource);
        refreshMappings();
    }

    public void addNucleotideCompositionSource(NucleotideComposition nucleotideComposition) {
        nucleotideSources.addElement(nucleotideComposition);
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
        Mapping m = mappings.get(p);
        if (m == null) {
            m = createMapping(a, b, select);
            mappings.put(p, m);
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
        mappings.put(new Pair(a, b), mapping);
        return mapping;
    }
    public ArrayList<StructureSource> structureSourcesPersistent = new ArrayList<>();
    public ArrayList<DataOverlay1D> structureVisDataSources1DPersistent = new ArrayList<>();
    public ArrayList<DataOverlay2D> structureVisDataSources2DPersistent = new ArrayList<>();
    public ArrayList<AnnotationSource> annotationSourcesPersistent = new ArrayList<>();
    public ArrayList<NucleotideComposition> nucleotideSourcesPersistent = new ArrayList<>();

    public void saveStructureVisModel(File outFile) {
        structureSourcesPersistent = Collections.list(structureSources.elements());
        structureVisDataSources1DPersistent = Collections.list(structureVisDataOverlays1D.elements());
        structureVisDataSources2DPersistent = Collections.list(structureVisDataOverlays2D.elements());
        annotationSourcesPersistent = Collections.list(annotationSources.elements());
        nucleotideSourcesPersistent = Collections.list(nucleotideSources.elements());


        System.out.println(substructureModel.overlayNavigatorTreeModel.getTreeModelListeners().length);
        ArrayList<TreeModelListener> treeListenersList = new ArrayList<>();
        TreeModelListener[] overlayTreeListeners = substructureModel.overlayNavigatorTreeModel.getTreeModelListeners();
        for (int i = 0; i < overlayTreeListeners.length; i++) {
            treeListenersList.add(overlayTreeListeners[i]);
            substructureModel.overlayNavigatorTreeModel.removeTreeModelListener(overlayTreeListeners[i]);
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
        }
         */

        System.out.println(substructureModel.overlayNavigatorTreeModel.getTreeModelListeners().length);

        try {
            ObjectOutput out = new ObjectOutputStream(new FileOutputStream(outFile));
            out.writeObject(this);
            out.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        // re-add the listeners, this is only necessary if the application stays open
        for (int i = 0; i < overlayTreeListeners.length; i++) {
            substructureModel.overlayNavigatorTreeModel.addTreeModelListener(treeListenersList.get(i));
        }
        System.out.println(substructureModel.overlayNavigatorTreeModel.getTreeModelListeners().length);
        System.out.println("StructureVis project saved");
    }

    public static StructureVisController loadProject(File inFile) throws IOException, ClassNotFoundException {
        ObjectInputStream in = new ObjectInputStream(new FileInputStream(inFile));
        StructureVisController ret = (StructureVisController) in.readObject();
        ret.structureSources = new DefaultListModel<>();
        for (StructureSource s : ret.structureSourcesPersistent) {
            ret.structureSources.addElement(s);
        }
        ret.structureSourcesPersistent = null;

        ret.structureVisDataOverlays1D = new DefaultListModel<>();
        for (DataOverlay1D s : ret.structureVisDataSources1DPersistent) {
            ret.structureVisDataOverlays1D.addElement(s);
        }
        ret.structureVisDataSources1DPersistent = null;

        ret.structureVisDataOverlays2D = new DefaultListModel<>();
        for (DataOverlay2D s : ret.structureVisDataSources2DPersistent) {
            ret.structureVisDataOverlays2D.addElement(s);
        }
        ret.structureVisDataSources2DPersistent = null;

        ret.annotationSources = new DefaultListModel<>();
        for (AnnotationSource annotationSource : ret.annotationSourcesPersistent) {
            ret.annotationSources.addElement(annotationSource);
        }
        ret.annotationSourcesPersistent = null;

        ret.nucleotideSources = new DefaultListModel<>();
        for (NucleotideComposition nucleotideSource : ret.nucleotideSourcesPersistent) {
            ret.nucleotideSources.addElement(nucleotideSource);
        }
        ret.nucleotideSourcesPersistent = null;

        ret.substructureModel.initialise();
        in.close();

        System.out.println("StructureVis project loaded");
        return ret;
    }
}
