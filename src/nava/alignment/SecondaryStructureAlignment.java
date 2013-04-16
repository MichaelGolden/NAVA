/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.alignment;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import nava.structure.StructureAlign;
import nava.tasks.applications.MAFFTApplication;
import nava.utils.RNAFoldingTools;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class SecondaryStructureAlignment extends Alignment {
    
    public SecondaryStructureAlignment()
    {
        super(new ArrayList<SecondaryStructureItem>());
    }

    public SecondaryStructureAlignment(List<SecondaryStructureItem> items) {
        super(items);
    }

    public static SecondaryStructureAlignment mafftAlign(SecondaryStructureAlignment inAlignment) {
        if(inAlignment.items.size() < 2)
        {
            return inAlignment;
        }
        
        ArrayList<String> sequences = new ArrayList<>();
        for (int i = 0; i < inAlignment.items.size(); i++) {
            SecondaryStructureItem item = (SecondaryStructureItem) inAlignment.items.get(i);
            sequences.add(item.getOriginalSequence());
        }        
        
        MAFFTApplication mafft = new MAFFTApplication();
        mafft.arguments = " --retree 2 --maxiterate 1000 ";
        try {
            ArrayList<String> alignedSequences = mafft.align(sequences);
            ArrayList<SecondaryStructureItem> alignedItems = new ArrayList<>();

            
            for (int i = 0; i < alignedSequences.size(); i++) {
                
                // get dot bracket structure corresponding to non-gap characters in sequence
                String newDbn = "";
                String dbn = ((SecondaryStructureItem) inAlignment.items.get(i)).getSubItem(1);                
                String seq = ((SecondaryStructureItem) inAlignment.items.get(i)).getSubItem(0);
                for(int j = 0 ; j < seq.length() ; j++)
                {
                    if(seq.charAt(j) != '-' && j < dbn.length())
                    {
                        newDbn += dbn.charAt(j);
                    }
                }
                
                String structure = StructureAlign.mapStringToAlignedSequence(newDbn, alignedSequences.get(i), "-");
                SecondaryStructureItem origItem = (SecondaryStructureItem) inAlignment.items.get(i);
                SecondaryStructureItem item = new SecondaryStructureItem(origItem.name, alignedSequences.get(i), structure, i);
                item.selected = origItem.selected;
                alignedItems.add(item);
                //System.out.println();
            }
            
            /*
            BufferedWriter buffer = new BufferedWriter(new FileWriter("c:/dev/out.txt"));
            for (int i = 0; i < alignedSequences.size(); i++) {
                buffer.write(i+"\t"+alignedItems.get(i)+"\n");
                buffer.newLine();
            }
            buffer.close();*/

            return new SecondaryStructureAlignment(alignedItems);
        } catch (Exception ex) {
            Logger.getLogger(SecondaryStructureAlignment.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
