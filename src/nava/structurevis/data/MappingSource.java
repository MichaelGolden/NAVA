/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.structurevis.data;

import java.io.Serializable;
import nava.data.types.Alignment;
import nava.data.types.Sequence;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class MappingSource implements Serializable {
    
    public Alignment alignmentSource;
    public Sequence sequenceSource;
    public String sequence;
}
