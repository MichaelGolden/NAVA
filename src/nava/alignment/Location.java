/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.alignment;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class Location implements Comparable<Location> {

    int startPos;
    int endPos;

    public Location(int startPos, int endPos) {
        this.startPos = startPos;
        this.endPos = endPos;
    }

    @Override
    public String toString() {
        return startPos + " - " + endPos;
    }

    @Override
    public int compareTo(Location o) {
        if (this.startPos < o.startPos) {
            return -1;
        } else if (this.startPos > o.startPos) {
            return 1;
        } else {
            if (this.endPos < o.endPos) {
                return -1;
            } else if (this.endPos > o.endPos) {
                return 1;
            } else {
                return 0;
            }
        }
    }
}
