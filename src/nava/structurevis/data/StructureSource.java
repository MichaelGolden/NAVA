/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.structurevis.data;

import java.util.ArrayList;
import java.util.Arrays;
import nava.data.types.Alignment;
import nava.data.types.SecondaryStructure;
import nava.data.types.SecondaryStructureData;
import nava.utils.RNAFoldingTools;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class StructureSource {
    
    public SecondaryStructure structure;
    public MappingSource mappingSource;
    
    public SecondaryStructureData data;
    public int [] pairedSites;
    public ArrayList<Substructure> substructures = new ArrayList<>();
    
    public StructureSource(SecondaryStructure structure, MappingSource mappingSource)
    {
        this.structure = structure;
        this.mappingSource = mappingSource;
    }
    
    public void loadData ()
    {
        this.data = structure.getObject();
        this.pairedSites = this.data.pairedSites;
        substructures = enumerateAdjacentSubstructures(this.pairedSites, 10, 300, false);
    }
    
     /**
     * Returns a list of adjacent non-overlapping substructures.
     *
     * @param dotBracketString
     * @param maxLength the maximum length a substructure may be.
     * @return a list of adjacent non-overlapping substructures.
     */
    public static ArrayList<Substructure> enumerateAdjacentSubstructures(int [] pairedSitesIn, int minLength, int maxLength, boolean circularize) {
        ArrayList<Substructure> structures = new ArrayList<>();
        
        int [] pairedSites = Arrays.copyOf(pairedSitesIn, pairedSitesIn.length);
        int genomeLength = pairedSites.length;

        boolean lastStructureAdded = false;
        for (int i = 0; i < pairedSites.length; i++) {
            int x = i;
            int y = pairedSites[i];

            if (y > 0 & y - x + 1 > 0) {

                int[] pairedSitesSub = new int[y - x];
                for (int j = 0; j < pairedSitesSub.length; j++) {
                    if(pairedSites[i + j] != 0)
                    {
                        pairedSitesSub[j] = pairedSites[i + j] - i;
                    }
                    else
                    {
                        pairedSites[i + j] = 0;
                    }
                }

                Substructure s = new Substructure(y - x);
                s.pairedSites = pairedSitesSub;
                s.startPosition = x;
                s.name = structures.size() + "";
                if (maxLength == 0 || s.length <= maxLength) {
                    i += s.length;

                    if (s.length >= minLength && x + s.length < genomeLength) {
                        structures.add(s);
                    }

                    if (!lastStructureAdded && x < genomeLength && x + s.length >= genomeLength && circularize) {
                        structures.add(s);
                        lastStructureAdded = true;
                    }
                }
            }
        }

        return structures;
    }
}
