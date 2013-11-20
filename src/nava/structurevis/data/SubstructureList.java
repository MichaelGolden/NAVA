/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.structurevis.data;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class SubstructureList implements Serializable {

    private static final long serialVersionUID = -6573293554805270269L;
    public StructureOverlay structureOverlay;
    public String title = null;
    public ArrayList<Substructure> substructures = null;
    public int coverageCount;
    public int sequenceLength;
    public double coveragePercent;
    public int min = 10;
    public int max = 300;
    public boolean recursive = false;

    protected SubstructureList() {
    }

    public SubstructureList(StructureOverlay structureOverlay) {
        this.structureOverlay = structureOverlay;
        this.structureOverlay.loadData();
        if(structureOverlay.title != null)
        {
            this.title = structureOverlay.title+ " substructures";
        }
        else
        {
            this.title = "Substructure list";
        }
        if (substructures == null) {
            generateSubstructureList(structureOverlay, this.title, this.min, this.max, structureOverlay.circular, this.recursive);
        }
    }

    @Override
    public String toString() {
        return title;
    }

    public void generateSubstructureList(StructureOverlay structureOverlay, String title, int minLength, int maxLength, boolean circularize, boolean recursive) {
        if (structureOverlay != null && structureOverlay.structure != null) {
            structureOverlay.loadData();
            this.structureOverlay = structureOverlay;
            this.title = title;
            this.min = minLength;
            this.max = maxLength;
            this.recursive = recursive;
            sequenceLength = structureOverlay.pairedSites.length;
            substructures = new ArrayList<>();
            if (recursive) {
                substructures = StructureOverlay.enumerateSubstructures(structureOverlay.pairedSites, minLength, maxLength, circularize);
            } else {
                substructures = StructureOverlay.enumerateAdjacentSubstructures(structureOverlay.pairedSites, minLength, maxLength, circularize);
            }
            coverageCount = getCoverageCount(structureOverlay.pairedSites, substructures);
            coveragePercent = (double) coverageCount / (double) sequenceLength;
        }
    }

    public static int getCoverageCount(int[] pairedSites, ArrayList<Substructure> substructures) {
        boolean[] covered = new boolean[pairedSites.length];
        for (Substructure s : substructures) {
            for (int i = s.startPosition; i < s.startPosition + s.length; i++) {
                covered[i%pairedSites.length] = true;
            }
        }
        int count = 0;
        for (int i = 0; i < covered.length; i++) {
            count += covered[i%pairedSites.length] ? 1 : 0;
        }

        return count;
    }

    @Override
    public SubstructureList clone() {
        SubstructureList cloned = new SubstructureList();
        cloned.structureOverlay = structureOverlay;
        cloned.title = title;
        cloned.substructures = substructures == null ? null : (ArrayList<Substructure>) substructures.clone();
        cloned.coverageCount = coverageCount;
        cloned.coveragePercent = coveragePercent;
        cloned.sequenceLength = sequenceLength;
        cloned.min = min;
        cloned.max = max;
        cloned.recursive = recursive;
        return cloned;
    }
}
