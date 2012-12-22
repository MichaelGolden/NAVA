/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.tasks;

import java.util.Objects;
import nava.structurevis.StructureVisController;
import nava.structurevis.data.MappingSource;

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

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MappingTask<Mapping> other = (MappingTask<Mapping>) obj;
        if (!Objects.equals(this.structureVisController, other.structureVisController)) {
            return false;
        }
        if (!Objects.equals(this.a, other.a)) {
            return false;
        }
        if (!Objects.equals(this.b, other.b)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.structureVisController);
        hash = 97 * hash + Objects.hashCode(this.a);
        hash = 97 * hash + Objects.hashCode(this.b);
        return hash;
    }

    @Override
    public void before() {
        
    }

    @Override
    public void after() {
        
    }
    
    
}
