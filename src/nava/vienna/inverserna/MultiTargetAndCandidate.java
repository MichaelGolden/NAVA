/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.vienna.inverserna;

import java.util.*;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class MultiTargetAndCandidate extends ParetoItem {
    List<TargetStructure> targetStructures = new ArrayList<>();
    String sequence;
    
    public MultiTargetAndCandidate(List<TargetStructure> targetStructures, String sequence)
    {
        this.targetStructures = targetStructures;
        this.sequence = sequence;
    }
    
    public MultiTargetAndCandidate(String sequence)
    {
        this.sequence = sequence;
    }
    
    public void addTargetStructure(TargetStructure structure)
    {
        targetStructures.add(structure);
    }
}
