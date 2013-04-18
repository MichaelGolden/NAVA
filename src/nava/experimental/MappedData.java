/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.experimental;

import java.io.File;
import java.util.ArrayList;
import nava.utils.Mapping;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class MappedData {
    
    String name;
    double [] values;
    int [] codon;
    boolean [] used;
    Mapping mapping;
    
    public static MappedData getMappedData(File referenceAlignment, File inputAlignment, double [] values, boolean codon, String name)
    {
        Mapping mapping = Mapping.createMapping(referenceAlignment, inputAlignment, 1);
        
        MappedData mappedData = new MappedData();
        
        mappedData.values = new double[mapping.getALength()];
        mappedData.used = new boolean[mapping.getALength()];
        mappedData.name = name;
        mappedData.mapping = mapping;
        
        for(int i = 0 ; i < mappedData.values.length ; i++)
        {
            int aToB = mapping.aToB(i);
            if(aToB != -1)
            {
                mappedData.values[i] = codon ? values[aToB/3] : values[aToB];
                mappedData.used[i] = true;
                
                    if(codon)
                    {
                        mappedData.codon[i] = aToB%3+1;
                    }
            }
        }
        
        return mappedData;
    }
    
    public static MappedData getMappedData(File referenceAlignment, File inputAlignment, ArrayList<String> values, boolean codon, String name)
    {
        Mapping mapping = Mapping.createMapping(referenceAlignment, inputAlignment, 3);
        
        MappedData mappedData = new MappedData();
        
        mappedData.values = new double[mapping.getALength()];
        mappedData.codon = new int[mapping.getALength()];
        mappedData.used = new boolean[mapping.getALength()];
        mappedData.name = name;        
        mappedData.mapping = mapping;
        
        for(int i = 0 ; i < mappedData.values.length ; i++)
        {
            int aToB = mapping.aToB(i);
            if(aToB != -1)
            {
                try
                {
                    mappedData.values[i] = codon ? Double.parseDouble(values.get(aToB/3)) : Double.parseDouble(values.get(aToB));
                    mappedData.used[i] = true;
                    if(codon)
                    {
                        mappedData.codon[i] = aToB%3+1;
                    }
                }
                catch(NumberFormatException ex)
                {
                    
                }
            }
        }
        return mappedData;
    }
    
    @Override
    public String toString()
    {
        String ret = "";
        for(int i = 0 ; i < values.length ; i++)
        {
            ret += (i+1)+"\t"+this.mapping.alignedA0.charAt(Mapping.getUngappedPosition(mapping.alignedA0, i))+"\t" + (used[i] ? values[i] : "")+"\n";
        }
        return ret;
    }
    
}
