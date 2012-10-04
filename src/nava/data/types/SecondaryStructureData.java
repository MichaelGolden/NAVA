/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.data.types;

/**
 *
 * @author Michael
 */
public class SecondaryStructureData {
    
    public String title;    
    public String sequence;
    public int[] pairedSites;
    
    public SecondaryStructureData()
    {
        
    }
    
    public SecondaryStructureData(String title, String sequence, int [] pairedSites)
    {
        this.title = title;
        this.sequence = sequence;
        this.pairedSites = pairedSites;
    }
}
