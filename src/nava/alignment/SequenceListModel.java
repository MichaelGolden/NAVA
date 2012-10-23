/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.alignment;

import java.util.ArrayList;
import java.util.Collections;
import javax.swing.AbstractListModel;
import nava.data.types.AlignmentData;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class SequenceListModel extends AbstractListModel {

    AlignmentData alignment;
    int[] modelToView;
    int[] viewToModel;
    ArrayList<Sequence> sequences = new ArrayList<>();
    
    public void setAlignment(AlignmentData alignment) {
        this.alignment = alignment;
        modelToView = new int[alignment.sequences.size()];
        viewToModel = new int[modelToView.length];
        for (int i = 0; i < modelToView.length; i++) {
            modelToView[i] = i;
            viewToModel[i] = i;
        }
        
        sequences.clear();
        for(int i = 0 ; i < alignment.sequences.size() ; i++)
        {
            sequences.add(new Sequence(alignment, i));
        }
    }

    @Override
    public int getSize() {
        return alignment.sequences.size();
    }

    @Override
    public Sequence getElementAt(int viewIndex) {
        //System.out.println("v"+viewIndex+"m"+modelIndex(viewIndex));
        return new Sequence(alignment, modelIndex(viewIndex));
    }

    public int modelIndex(int viewIndex) {
        return viewToModel[viewIndex];
    }

    public static final int ASCENDING = 1;
    public static final int DESCENDING = -1;
    public static final int NOT_SORTED = 0;
    public int sortOrder = ASCENDING;
    public class Sequence implements Comparable<Sequence> {

        AlignmentData data;
        int modelIndex;
        String sequenceName;

        public Sequence(AlignmentData data, int modelIndex) {
            this.data = data;
            this.sequenceName = data.sequenceNames.get(modelIndex);
            this.modelIndex = modelIndex;
        }
        
        public String getSequence()
        {
            return data.sequences.get(modelIndex);
        }

        @Override
        public int compareTo(Sequence o) {
            return sortOrder*this.sequenceName.toLowerCase().compareTo(o.sequenceName.toLowerCase());
        }        
    }
    
    public void sort (int order)
    {
        Collections.sort(sequences);
        for(int i = 0 ; i < sequences.size() ; i++)
        {
            viewToModel[i] = sequences.get(i).modelIndex;
            modelToView[sequences.get(i).modelIndex] = i;
        }
        fireContentsChanged(this, 0, sequences.size()-1);
    }
}
