/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.vienna.inverserna;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class TargetAndCandidate extends ParetoItem {
    
    TargetStructure target;
    String sequence;  

    public TargetAndCandidate(TargetStructure target, String sequence) {
        this.target = target;
        this.sequence = sequence;
    } 
}
