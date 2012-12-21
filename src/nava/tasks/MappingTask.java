/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.tasks;

import nava.structurevis.StructureVisController;
import nava.structurevis.data.MappingSource;
import nava.utils.Mapping;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class MappingTask<Mapping> extends UITask {
    
    nava.utils.Mapping mapping;
    StructureVisController structureVisController;
    MappingSource a;
    MappingSource b;
    
    public MappingTask(StructureVisController structureVisController, MappingSource a, MappingSource b)
    {
        this.structureVisController = structureVisController;
        this.a = a;
        this.b = b;
    }

    @Override
    public void task() {
        mapping = structureVisController.createMapping(a, b);
    }
    
    @Override
    public nava.utils.Mapping get() {
        return mapping;
    }
    
    
}
