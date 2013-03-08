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
import nava.data.io.IO;
import nava.structurevis.data.*;
import nava.tasks.MappingTask;
import nava.ui.MainFrame;
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

    public StructureVisController(File workingDirectory) {
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
                if (s.mappingSource != null && nucleotideComposition.mappingSource != null) {
                    //getMapping(f.mappingSource, s.mappingSource);
                    MainFrame.taskManager.queueUITask(new MappingTask(this, nucleotideComposition.mappingSource, s.mappingSource));
                }
            }
        }
    }

    public void addStructureSource(StructureSource structureSource) {
        if (!structureSources.contains(structureSource)) {
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
    
    public Mapping getMapping(MappingSource a, MappingSource b)
    {
        return getMapping(a,b,1);
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


        System.out.println("Mapping");
        System.out.println((a.sequenceSource == null) + "\t" + (a.sequence == null));
        System.out.println((b.sequenceSource == null) + "\t" + (b.sequence == null));
        System.out.println(sequencesA);
        System.out.println(sequencesB);
        System.out.println("----------");

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
        try {
            ObjectOutput out = new ObjectOutputStream(new FileOutputStream(outFile));
            out.writeObject(this);
            out.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
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
