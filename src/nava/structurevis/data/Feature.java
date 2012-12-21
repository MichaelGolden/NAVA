/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.structurevis.data;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author Michael
 */
public class Feature implements Comparable<Feature>, Serializable {

    public String name;
    public int min;
    public int max;
    public int row = 0;
    public boolean visible = true;
    public ArrayList<Block> blocks = new ArrayList<>();
    public MappingSource mappingSource = null;

    @Override
    public String toString() {
        return row + ":" + blocks.toString();
    }
    
    public String getName()
    {
        return name + " [" + min + ".." + max + ", " + (row + 1) + "]";
    }

    public int getLength() {
        return max - min;
    }

    /**
     * Orders features by their length, shortest to longest.
     *
     * @param other
     * @return
     */
    @Override
    public int compareTo(Feature other) {
        if (this.getLength() < other.getLength()) {
            return -1;
        } else if (this.getLength() > other.getLength()) {
            return 1;
        } else if (this.min < other.min) {
            return -1;
        } else if (this.min > other.min) {
            return 1;
        } else if (this.max < other.max) {
            return -1;
        } else if (this.max > other.max) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public Feature clone() {
        Feature feature = new Feature();
        feature.name = name;
        feature.min = min;
        feature.max = max;
        feature.row = row;
        feature.visible = visible;
        feature.blocks = new ArrayList<>();
        feature.mappingSource = mappingSource;
        for (int i = 0; i < this.blocks.size(); i++) {
            feature.blocks.add(blocks.get(i).clone(feature));
        }
        return feature;
    }

    public static boolean isOverlap(Feature f1, Feature f2) {
        if (f1.row == f2.row) {
            if ((f1.min >= f2.min && f1.min <= f2.max) || (f1.max >= f2.min && f1.max <= f2.max)) // does start or end of f1 lie inside f2
            {
                return true;
            }
            if ((f2.min >= f1.min && f2.min <= f1.max) || (f2.max >= f1.min && f2.max <= f1.max)) // does start or end of f2 lie inside f1
            {
                return true;
            }
        }

        return false;
    }
}
