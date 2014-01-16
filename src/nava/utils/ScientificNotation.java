/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.utils;

import java.text.DecimalFormat;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class ScientificNotation implements Comparable<ScientificNotation> {
    
    static DecimalFormat df = new DecimalFormat("0.000");
    static DecimalFormat df2 = new DecimalFormat("0.00E0");
    Double d;

    public ScientificNotation(Double d)
    {
        this.d = d;
    }

    @Override
    public String toString()
    {
        return df2.format(d);
    }

    @Override
    public int compareTo(ScientificNotation o) {
        return this.d.compareTo(o.d);
    }
}
