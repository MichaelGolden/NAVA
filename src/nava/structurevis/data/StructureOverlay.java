/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.structurevis.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import nava.data.types.SecondaryStructure;
import nava.data.types.SecondaryStructureData;
import nava.ui.MainFrame;
import nava.ui.ProjectModel;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class StructureOverlay extends Overlay implements Serializable {
    
    public SecondaryStructure structure;
    public MappingSource mappingSource;
    
    public enum MappingSourceOption {EMBEDDED, ALIGNMENT, STRING};
    public MappingSourceOption mappingSourceOption = MappingSourceOption.EMBEDDED;
    public boolean addMappingSourceAsNucleotideOverlay = true;
    
    public int minStructureSize = 10;
    public int maxStructureSize = 250;
    public boolean nonOverlappingSubstructures = false;
    
    public transient SecondaryStructureData data;
    public transient int [] pairedSites;
    public ArrayList<Substructure> substructures = new ArrayList<>();
    public boolean circular = false;
    
    public StructureOverlay(SecondaryStructure structure, MappingSource mappingSource)
    {
        this.structure = structure;
        this.mappingSource = mappingSource;
        this.title = structure.title;
    }
    
    public void loadData ()
    {
        this.data = structure.getObject(ProjectModel.path,MainFrame.dataSourceCache);
        this.pairedSites = this.data.pairedSites;
    }
    
    @Override
    public String toString()
    {
        return title;
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
    
     public static ArrayList<Substructure> enumerateSubstructures(int [] pairedSites, int minLength, int maxLength, boolean circularize)
    {
        ArrayList<Substructure> structures = enumerateAdjacentSubstructures(pairedSites, minLength, maxLength, circularize);
        recursivelyEnumerateSubstructures(minLength, maxLength, structures, 0, 0);
        return structures;
    }
    
    private static void recursivelyEnumerateSubstructures(int minLength, int maxLength, ArrayList<Substructure> structures, int startIndex, int level) {
        int added = 0;
        
        int end = structures.size();
        for (int k = startIndex; k < end ; k++) {
            int kAdded = 0;
            
            //int[][] pairedSites = getPairedNucleotidePositions(structures.get(k).getDotBracketString(), structures.get(k).getStartPosition());
            int [] pairedSites = Arrays.copyOf(structures.get(k).pairedSites, structures.get(k).pairedSites.length);
            
            int fullStructureLength = pairedSites.length;

            for (int i = 0 ; i < fullStructureLength ; i++) {
                int x = startIndex;
                int y = pairedSites[i];
                int length = y - x + 1;
                
                if (y > 0 & length > 0) {
                    Substructure s = new Substructure(length);

                    int[] pairedSitesSub = new int[length];
                    for (int j = 0; j < pairedSitesSub.length; j++) {
                        pairedSitesSub[j] = pairedSites[i+j];
                    }
                    s.pairedSites = pairedSitesSub;
                    s.startPosition = x;
                    
                    if (maxLength == 0 || s.length < fullStructureLength*0.75) {
                        i += s.length;
                        if (s.length >= minLength && s.length < fullStructureLength) {
                           if(!structures.contains(s))
                           {
                                s.name = structures.size()+"";
                                //s.name = structures.get(k).name + "." + kAdded;
                                structures.add(s);
                                added++;
                                kAdded++;
                           }
                           else
                           {
                               int index = structures.indexOf(s);
                               //System.out.println("already contains " + s.toString() + "\t" +index+"\t"+ structures.get(index));
                           }
                        }
                    }
                }
            }
        }
        
        if(added > 0)
        {            
            //System.out.println("added=" + added + ", n="+structures.size());
            recursivelyEnumerateSubstructures(minLength, maxLength, structures, end, level+1);
        }
    }

    @Override
    public Icon getIcon() {
        return new ImageIcon(ClassLoader.getSystemResource("resources/icons/structure-16x16.png"));
    }
    
    public String details()
    {
        String ret =  "details:"+mappingSource.mappingType+" - " +structure.getObject(ProjectModel.path,MainFrame.dataSourceCache).sequence+"";
        return ret;
    }
}
