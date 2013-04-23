/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.experimental;

import java.io.File;
import java.util.ArrayList;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class MappableData {

    File inputAlignment;
    ArrayList<String> values;
    boolean codon;
    String name;

    public MappableData(File inputAlignment, ArrayList<String> values, boolean codon, String name) {
        this.inputAlignment = inputAlignment;
        this.values = values;
        this.codon = codon;
        this.name = name;
    }
}
