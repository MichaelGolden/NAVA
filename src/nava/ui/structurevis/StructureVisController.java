/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.ui.structurevis;

import java.nio.file.Paths;
import java.util.Hashtable;
import nava.utils.Mapping;
import nava.data.types.Alignment;
import nava.utils.Pair;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class StructureVisController 
{
    Hashtable<Pair<Alignment, Alignment>, Mapping> mappings = new Hashtable<>();
    
    public Mapping createMapping(Alignment a, Alignment b)
    {
        Mapping mapping = Mapping.createMapping(Paths.get(a.importedDataSourcePath).toFile(), Paths.get(b.importedDataSourcePath).toFile(), 1);
        mappings.put(new Pair(a, b), mapping);
        return mapping;
    }
}
