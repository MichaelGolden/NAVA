/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.structurevis.data;

import java.io.Serializable;
import java.util.Objects;
import nava.data.types.Alignment;
import nava.data.types.Sequence;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class MappingSource implements Serializable {
    
    public enum Type{ALIGNMENT, SEQUENCE, STRING};
    
    public Type mappingType = Type.ALIGNMENT; 
    
    public Alignment alignmentSource;
    public Sequence sequenceSource;
    public String sequence;
    
    public MappingSource (Alignment alignmentSource)
    {
        this.alignmentSource = alignmentSource;
        this.mappingType = Type.ALIGNMENT;
    }
    
    public MappingSource(Sequence sequenceSource)
    {
        this.sequenceSource = sequenceSource;
        this.mappingType = Type.SEQUENCE;
    }
    
    public MappingSource(String sequence)
    {
        this.sequence = sequence;
        this.mappingType = Type.STRING;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MappingSource other = (MappingSource) obj;
        if (!Objects.equals(this.alignmentSource, other.alignmentSource)) {
            return false;
        }
        if (!Objects.equals(this.sequenceSource, other.sequenceSource)) {
            return false;
        }
        if (!Objects.equals(this.sequence, other.sequence)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Objects.hashCode(this.alignmentSource);
        hash = 97 * hash + Objects.hashCode(this.sequenceSource);
        hash = 97 * hash + Objects.hashCode(this.sequence);
        return hash;
    }
}
