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

    String originalSequence;
    int[] pairedSites;

    public SecondaryStructureItem(String name, String sequence, int[] pairedSites, int modelIndex) {
        super();
        this.name = name;
        this.subItems = new ArrayList<>();
        this.originalSequence = sequence;
        this.subItems.add(sequence);
        this.subItems.add(RNAFoldingTools.getDotBracketStringFromPairedSites(pairedSites));
        this.pairedSites = pairedSites;
        this.subItemNames = new ArrayList<>();
        this.modelIndex = modelIndex;
    }
    
     public SecondaryStructureItem(String name, String sequence, String dotBracketStructure, int modelIndex) {
        super();
        this.name = name;
        this.subItems = new ArrayList<>();
        this.originalSequence = sequence;
        this.subItems.add(sequence);
        this.subItems.add(dotBracketStructure);
        this.pairedSites = RNAFoldingTools.getPairedSitesFromDotBracketString(dotBracketStructure);
        this.subItemNames = new ArrayList<>();
        this.modelIndex = modelIndex;
    }
    
    public String getOriginalSequence()
    {
        return originalSequence;
    }
    
    public String getSequence()
    {
        return subItems.get(0);
    }
    
    public int [] getOriginalPairedSites()
    {
        return pairedSites;
    }
    
     public int[] getPairedSites()
    {
        return RNAFoldingTools.getPairedSitesFromDotBracketString(subItems.get(1));
    }

    public void setSequence(String sequence) {
        this.setSubItem(0, sequence);
    }

    public void setStructure(int[] pairedSites) {
        this.setSubItem(1, RNAFoldingTools.getDotBracketStringFromPairedSites(pairedSites));
    }

    public void setStructure(String structure) {
        this.setSubItem(1, name);
    }

    @Override
    public String toString() {
        return "SecondaryStructureItem{" + "originalSequence=" + originalSequence + ", pairedSites=" + pairedSites + '}';
    }
}
