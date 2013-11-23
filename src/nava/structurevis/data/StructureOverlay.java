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

    private static final long serialVersionUID = -1056305922110763626L;
    public SecondaryStructure structure;
    public MappingSource mappingSource;

    public enum MappingSourceOption {

        EMBEDDED, ALIGNMENT, STRING
    };
    public MappingSourceOption mappingSourceOption = MappingSourceOption.EMBEDDED;
    public boolean addMappingSourceAsNucleotideOverlay = true;
    public int minStructureSize = 10;
    public int maxStructureSize = 250;
    public boolean nonOverlappingSubstructures = false;
    public transient SecondaryStructureData data;
    public transient int[] pairedSites;
    public SubstructureList substructureList;
    public boolean circular = false;
    public Substructure selectedSubstructure = null;

    public StructureOverlay() {
    }

    public StructureOverlay(SecondaryStructure structure, MappingSource mappingSource) {
        setStructureAndMapping(structure, mappingSource);
    }

    public void setStructureAndMapping(SecondaryStructure structure, MappingSource mappingSource) {
        this.structure = structure;
        this.mappingSource = mappingSource;
        this.title = structure.title;
    }

    public void loadData() {
        this.data = structure.getObject(ProjectModel.path, MainFrame.dataSourceCache);
        this.pairedSites = this.data.pairedSites;
    }

    /**
     * Returns a list of adjacent non-overlapping substructures.
     *
     * @param dotBracketString
     * @param maxLength the maximum length a substructure may be.
     * @return a list of adjacent non-overlapping substructures.
     */
    public static ArrayList<Substructure> enumerateAdjacentSubstructures(int[] pairedSitesIn, int minLength, int maxLength, boolean circularize) {
        ArrayList<Substructure> structures = new ArrayList<>();


        int[] pairedSites = Arrays.copyOf(pairedSitesIn, pairedSitesIn.length);
        int genomeLength = pairedSites.length;
        if (circularize) {
            pairedSites = new int[pairedSitesIn.length * 2];
            for (int i = 0; i < pairedSitesIn.length; i++) {
                pairedSites[i] = pairedSitesIn[i];
                if ((i - pairedSites[i]) > pairedSitesIn.length / 2) {
                    if (pairedSitesIn[i] != 0) {
                        pairedSites[i] = pairedSitesIn[i] + pairedSitesIn.length;
                    }
                }

            }
            for (int i = 0; i < pairedSitesIn.length; i++) {

                if ((pairedSites[i] - i) > pairedSitesIn.length / 2) {
                    if (pairedSitesIn[i] != 0) {
                        pairedSites[i + pairedSitesIn.length] = (pairedSitesIn.length - (pairedSitesIn[i] - 1)) + pairedSitesIn.length;
                    }
                }
            }
            genomeLength *= 2;
        }


        boolean lastStructureAdded = false;
        for (int i = 0; i < pairedSites.length; i++) {
            int x = i;
            int y = pairedSites[i];

            if (y > 0 && y - x + 1 > 0) {

                // System.out.println(">> " + x +"\t"+y);

                int[] pairedSitesSub = new int[y - x];
                for (int j = 0; j < pairedSitesSub.length; j++) {
                    if (pairedSites[i + j] != 0) {
                        pairedSitesSub[j] = pairedSites[i + j] - i;
                    } else {
                        pairedSitesSub[j] = 0;
                    }
                }

                Substructure s = new Substructure(y - x);
                s.pairedSites = pairedSitesSub;
                s.startPosition = x;
                s.name = structures.size() + "";

                // System.out.println(s.startPosition+"\t"+pairedSitesIn.length+"\t"+s.length);
                if (circularize) {
                    if (s.startPosition >= pairedSitesIn.length) {
                        continue;
                    }
                }

                if (maxLength == 0 || s.length <= maxLength) {
                    i += s.length;
                    if (s.length >= minLength && x + s.length < genomeLength) {
                        if (circularize) {
                            for (int j = 0; j < pairedSitesSub.length; j++) {
                                if (pairedSitesSub[j] != 0 && j < pairedSitesSub.length / 2 + 1) {
                                    pairedSitesSub[pairedSitesSub[j] - 1] = j + 1;
                                }
                                //System.out.println(s.startPosition + "\t" + (j + 1) + "\t" + pairedSitesSub[j] + "\t" + pairedSitesSub.length);
                            }
                        }
                        //System.out.println(structures.size() + "\t" + s.getDotBracketString());
                        structures.add(s);
                    }
                }
            }
        }

        return structures;
    }

    public static ArrayList<Substructure> enumerateSubstructures(int[] pairedSites, int minLength, int maxLength, boolean circularize) {
        ArrayList<Substructure> structures = enumerateAdjacentSubstructures(pairedSites, minLength, maxLength, circularize);
        recursivelyEnumerateSubstructures(minLength, maxLength, structures, 0, 0, pairedSites.length);
        return structures;
    }

    private static void recursivelyEnumerateSubstructures(int minLength, int maxLength, ArrayList<Substructure> substructures, int startIndex, int level, int genomeLength) {
        double minProportion = 0.75; // substructure must be at least minProportion% length of parent structure;
        int minDifference = 6; // substructure must be at least minDifference nucleotides smaller than parent structure

        int added = 0;

        int end = substructures.size();
        for (int k = startIndex; k < end; k++) {
            int kAdded = 0;

            int[] pairedSites = Arrays.copyOf(substructures.get(k).pairedSites, substructures.get(k).pairedSites.length);
            int fullStructureLength = substructures.get(k).getDotBracketString().length();


            for (int i = 0; i < pairedSites.length; i++) {
                int x = substructures.get(k).startPosition + i;
                int y = substructures.get(k).startPosition + pairedSites[i];
                int length = y - x;

                if (x < genomeLength && y > 0 & length > 0) {
                    Substructure s = new Substructure(length);

                    int[] pairedSitesSub = new int[length];
                    for (int j = 0; j < pairedSitesSub.length; j++) {
                        //System.out.println("->"+x+"\t"+y+"\t"+i+"\t"+j+"\t"+pairedSitesSub.length+"\t"+(i+j)+"\t"+pairedSites.length);
                        if (pairedSites[i + j] != 0) {
                            pairedSitesSub[j] = pairedSites[i + j] - i;
                            pairedSitesSub[j] = pairedSites[i + j] - i;
                        }
                        //System.out.println("$$$ " + j + "\t" + pairedSitesSub[j]+"\t"+ + pairedSites[i + j]);
                    }
                    s.pairedSites = pairedSitesSub;
                    s.startPosition = x;
                    if (maxLength == 0 || (s.length < fullStructureLength * minProportion && s.length + minDifference <= fullStructureLength)) {
                        i += s.length;
                        if (s.length >= minLength && s.length < fullStructureLength) {
                            if (!substructures.contains(s)) {
                                s.name = substructures.size() + "";
                                //s.name = structures.get(k).name + "." + kAdded;
                                substructures.add(s);
                                added++;
                                kAdded++;
                            } else {
                                int index = substructures.indexOf(s);
                                //System.out.println("already contains " + s.toString() + "\t" +index+"\t"+ structures.get(index));
                            }
                        }
                    }
                }
            }
        }

        if (added > 0) {
            //System.out.println("added=" + added + ", n="+structures.size());
            recursivelyEnumerateSubstructures(minLength, maxLength, substructures, end, level + 1, genomeLength);
        }
    }

    /*
     * private static void recursivelyEnumerateSubstructures(int minLength, int
     * maxLength, ArrayList<Substructure> structures, int startIndex, int level)
     * { int added = 0;
     *
     * int end = structures.size(); for (int k = startIndex; k < end; k++) { int
     * kAdded = 0;
     *
     * //int[][] pairedSites =
     * getPairedNucleotidePositions(structures.get(k).getDotBracketString(),
     * structures.get(k).getStartPosition()); int[] pairedSites =
     * Arrays.copyOf(structures.get(k).pairedSites,
     * structures.get(k).pairedSites.length);
     *
     * int fullStructureLength = pairedSites.length;
     *
     * for (int i = 0; i < fullStructureLength; i++) { int x = i; int y =
     * pairedSites[i]; int length = y - x; // System.out.println(x + "\t" + y +
     * "\t" + length); if (y > 0 & length > 0) { Substructure s = new
     * Substructure(length);
     *
     * int[] pairedSitesSub = new int[length]; for (int j = 0; j <
     * pairedSitesSub.length; j++) { //System.out.println(i + ":" + j + " -> " +
     * pairedSitesSub.length + ", " + (i + j) + " -> " + pairedSites.length);
     * pairedSitesSub[j] = pairedSites[i + j]-i; } s.pairedSites =
     * pairedSitesSub; s.startPosition = x; if (maxLength == 0 || s.length <
     * (double)fullStructureLength * 0.75) { System.out.println(s.length+" :
     * "+fullStructureLength+" = " + ((double)fullStructureLength * 0.75)); i +=
     * s.length; if (s.length >= minLength && s.length < fullStructureLength) {
     * if (!structures.contains(s)) { s.name = structures.size() + ""; //s.name
     * = structures.get(k).name + "." + kAdded; structures.add(s); added++;
     * kAdded++; } else { //int index = structures.indexOf(s);
     * //System.out.println("already contains " + s.toString() + "\t"
     * +index+"\t"+ structures.get(index)); } } } } } }
     *
     * if (added > 0) { //System.out.println("added=" + added + ",
     * n="+structures.size()); recursivelyEnumerateSubstructures(minLength,
     * maxLength, structures, end, level + 1); } }
     */
    @Override
    public Icon getIcon() {
        return new ImageIcon(ClassLoader.getSystemResource("resources/icons/structure-16x16.png"));
    }

    public String details() {
        String ret = "details:" + mappingSource.mappingType + " - " + structure.getObject(ProjectModel.path, MainFrame.dataSourceCache).sequence + "";
        return ret;
    }
}
