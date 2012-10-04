/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.data.types;

import java.util.ArrayList;

/**
 *
 * @author Michael
 */
public class AlignmentData {

    public ArrayList<String> sequences;
    public ArrayList<String> sequenceNames;

    public AlignmentData() {
        sequences = new ArrayList<String>();
        sequenceNames = new ArrayList<String>();
    }

    public AlignmentData(ArrayList<String> sequences, ArrayList<String> sequenceNames) {
        this.sequences = sequences;
        this.sequenceNames = sequenceNames;
    }
}
