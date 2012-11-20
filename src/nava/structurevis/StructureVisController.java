/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.structurevis;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Hashtable;
import nava.data.io.IO;
import nava.data.types.Alignment;
import nava.structurevis.data.DataSource1D;
import nava.structurevis.data.MappingSource;
import nava.structurevis.data.StructureSource;
import nava.utils.Mapping;
import nava.utils.Pair;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class StructureVisController {

    ArrayList<StructureSource> structureSources = new ArrayList<>();
    ArrayList<DataSource1D> structureVisDataSources = new ArrayList<>();
    Hashtable<Pair<MappingSource, MappingSource>, Mapping> mappings = new Hashtable<>();

    public void addStructureSource(StructureSource structureSource) {
        if (!structureSources.contains(structureSource)) {
            structureSources.add(structureSource);
            for (StructureSource s : structureSources) {
                System.out.println("A" + s.mappingSource);
                System.out.println(structureVisDataSources.size());
                for (DataSource1D dataSource : structureVisDataSources) {
                    System.out.println("B" + dataSource.mappingSource);
                    if (s.mappingSource != null && dataSource.mappingSource != null) {
                        System.out.println("Making");
                        getMapping(dataSource.mappingSource, s.mappingSource);
                    }
                }
            }
        }
    }

    public void addStructureVisDataSource(DataSource1D dataSource) {
        System.out.println("adding data source " + dataSource);
        structureVisDataSources.add(dataSource);
        System.out.println("size " + structureVisDataSources.size());
    }

    public Mapping getMapping(MappingSource a, MappingSource b) {
        Pair p = new Pair(a, b);
        Mapping m = mappings.get(p);
        if (m == null) {
            m = createMapping(a, b);
            mappings.put(p, m);
        }
        return m;
    }

    public Mapping createMapping(MappingSource a, MappingSource b) {


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

        Mapping mapping = Mapping.createMapping(sequencesA, sequencesNamesA, sequencesB, sequencesNamesB, maxSequencesToLoad);
        mappings.put(new Pair(a, b), mapping);
        return mapping;
    }
}
