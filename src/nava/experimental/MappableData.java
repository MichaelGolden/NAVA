/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.experimental;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import nava.data.io.CsvReader;

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
    
    public MappableData(File inputAlignment, File csvFile, int column, int skipLines, boolean codon, String name)
    {
        this.inputAlignment = inputAlignment;
        this.codon = codon;
        this.name = name;
        try {
            this.values = CsvReader.getColumn(csvFile, column);
            for(int i = 0 ; i < skipLines ; i++)
            {
                this.values.remove(i);
            }
        } catch (IOException ex) {
            Logger.getLogger(MappableData.class.getName()).log(Level.SEVERE, null, ex);
        }
          
    }
}
