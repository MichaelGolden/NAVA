/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.alignment;

import java.util.ArrayList;
import nava.utils.RNAFoldingTools;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class SecondaryStructureItem extends AlignmentItem {
    
    public SecondaryStructureItem(String name, String sequence, int [] pairedSites, int modelIndex)
    {
        super();
        this.name = name;
        this.subItems = new ArrayList<>();
        this.subItems.add(sequence);
        this.subItems.add(RNAFoldingTools.getDotBracketStringFromPairedSites(pairedSites));
        this.subItemNames = new ArrayList<>();
        this.modelIndex = modelIndex; 
    }    
    
    public void setSequence(String sequence)
    {
        this.setSubItem(0, sequence);
    }
    
    public void setStructure(int [] pairedSites)
    {
        this.setSubItem(1, RNAFoldingTools.getDotBracketStringFromPairedSites(pairedSites));
    }
    
    public void setStructure(String structure)
    {
        this.setSubItem(1, name);
    }
}
