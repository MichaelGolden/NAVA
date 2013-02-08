/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.alignment;

import java.awt.Color;
import java.util.ArrayList;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class AlignmentItem implements Comparable<AlignmentItem> {

    int modelIndex;
    String name;
    protected ArrayList<String> subItems = new ArrayList<>();
    protected ArrayList<String> subItemNames = new ArrayList<>();
    Color color = Color.gray;
    boolean selected = false;
    
    protected AlignmentItem()
    {
        
    }

    public AlignmentItem(String name, String subItem, String subItemName, int modelIndex) {
        this.name = name;
        this.subItems = new ArrayList<>();
        this.subItems.add(subItem);
        this.subItemNames = new ArrayList<>();
        this.subItemNames.add(subItemName);
        this.modelIndex = modelIndex;
    }

    public AlignmentItem(String name, ArrayList<String> subItems, ArrayList<String> subItemNames, int modelIndex) {
        this.name = name;
        this.subItems = subItems;
        this.subItemNames = subItemNames;
        this.modelIndex = modelIndex;
    }
    
    public void setSubItem(int i, String s)
    {
        this.subItems.set(i, s);
    }

    public String getSubItem(int i) {
        return subItems.get(i);
    }
    
    public String getSubItemName(int i) {
        return subItemNames.get(i);
    }
    
    public int getSubItemCount()
    {
        return subItems.size();
    }
    
    public int getSubItemNameCount() {
        return subItemNames.size();
    }
    
    public void setColor(Color color)
    {
        this.color = color;
    }

    @Override
    public int compareTo(AlignmentItem o) {
        if (AlignmentModel.sortOrder == AlignmentModel.NOT_SORTED) {
            if (this.modelIndex < o.modelIndex) {
                return -1;
            } else if (this.modelIndex > o.modelIndex) {
                return 1;
            }
            return 0;
        } else {
            return AlignmentModel.sortOrder * this.name.toLowerCase().compareTo(o.name.toLowerCase());
        }
    }
}