/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.structurevis;

import java.awt.event.WindowEvent;
import java.util.EventListener;
import nava.structurevis.data.*;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public interface SubstructureModelListener extends EventListener {

    public void dataSource1DChanged(DataSource1D dataSource1D);

    public void dataSource2DChanged(DataSource2D dataSource2D);

    public void structureSourceChanged(StructureSource structureSource);
    
    public void annotationSourceChanged(AnnotationSource annotationSource);
            
   public void nucleotideSourceChanged(NucleotideComposition nucleotideSource);
}
