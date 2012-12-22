/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.tasks;

import java.util.Objects;
import nava.structurevis.AnnotationsLayer;
import nava.structurevis.StructureVisController;
import nava.structurevis.data.AnnotationSource;
import nava.structurevis.data.StructureSource;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class AnnotationMappingTask extends UITask {
    
    AnnotationSource annotationSource;
    AnnotationSource mappedAnnotationSource;
    StructureSource structureSource;
    StructureVisController structureVisController;
    AnnotationsLayer annotationsLayer;
    
    public AnnotationMappingTask(AnnotationSource annotationSource, StructureSource structureSource, StructureVisController structureVisController, AnnotationsLayer annotationsLayer)
    {
        this.annotationSource = annotationSource;
        this.structureSource = structureSource;
        this.structureVisController = structureVisController;
        this.annotationsLayer = annotationsLayer;
    }

    @Override
    public void task() {
        this.mappedAnnotationSource = AnnotationSource.getMappedAnnotations(annotationSource, structureSource, structureVisController);
        this.annotationsLayer.setAnnotationData(mappedAnnotationSource);
        this.annotationsLayer.showAnnotations();
        System.out.println("Complete");
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AnnotationMappingTask other = (AnnotationMappingTask) obj;
        if (!Objects.equals(this.annotationSource, other.annotationSource)) {
            return false;
        }
        if (!Objects.equals(this.structureSource, other.structureSource)) {
            return false;
        }
        if (!Objects.equals(this.structureVisController, other.structureVisController)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + Objects.hashCode(this.annotationSource);
        hash = 83 * hash + Objects.hashCode(this.structureSource);
        hash = 83 * hash + Objects.hashCode(this.structureVisController);
        return hash;
    }

    @Override
    public Object get() {
        return null;
    }

    @Override
    public void before() {
        this.annotationsLayer.showLoading();
    }

    @Override
    public void after() {
        
    }
    
}