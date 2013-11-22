/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.structurevis.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;
import nava.data.types.Alignment;
import nava.data.types.Sequence;
import nava.ui.MainFrame;
import nava.ui.ProjectModel;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class MappingSource implements Serializable {
    private static final long serialVersionUID = -912648825685262857L;
    
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
    
    public String getRepresentativeSequence(ProjectModel projectModel)
    {
        switch(mappingType)
        {
            case ALIGNMENT:
                if(alignmentSource != null)
                {
                    ArrayList<String> sequences = alignmentSource.getObject(projectModel.getProjectPathString(), MainFrame.dataSourceCache).sequences;                
                    return sequences.size() > 0 ? sequences.get(0) : "";
                }
                return "";
            case SEQUENCE:
                if(sequenceSource != null)
                {
                   // return sequenceSource.getObject(projectModel.getProjectPathString(), MainFrame.dataSourceCache);
                }
                return "";
            case STRING:
                return sequence;
            default:
                return "";
                
        }
    }
    
    public int getLength()
    {
        switch(mappingType)
        {
            case ALIGNMENT:
                if(alignmentSource != null)
                {
                    return alignmentSource.length;
                }
            case SEQUENCE:
                return -1;
            case STRING:
                return sequence.length();
            default:
                return -2;
                
        }
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
