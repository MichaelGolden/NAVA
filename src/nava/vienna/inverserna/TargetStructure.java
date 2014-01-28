/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.vienna.inverserna;

import java.util.Arrays;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class TargetStructure {
    String uniqueIdentifier;
    int [] pairedSites;
    double tempCelsius;
    
    public TargetStructure(String uniqueIdentifier, int [] pairedSites, double tempCelsius)
    {
        this.uniqueIdentifier = uniqueIdentifier;
        this.pairedSites = pairedSites;
        this.tempCelsius = tempCelsius;
    }
    
    public String getUniqueIdentifier()
    {
        return uniqueIdentifier;
    }
}
