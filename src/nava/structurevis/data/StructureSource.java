/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.structurevis.data;

import nava.data.types.Alignment;
import nava.data.types.SecondaryStructure;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class StructureSource {
    public SecondaryStructure structure;
    public MappingSource mappingSource;
    
    public StructureSource(SecondaryStructure structure, MappingSource mappingSource)
    {
        this.structure = structure;
        this.mappingSource = mappingSource;
    }
}
