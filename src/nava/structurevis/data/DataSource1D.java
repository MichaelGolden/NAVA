/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.structurevis.data;

import nava.data.types.TabularField;
import nava.utils.ColorGradient;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class DataSource1D 
{    
    double [] data;
    boolean [] used; // is value missing or is it used?
    
    ColorGradient colorGradient;
    DataTransform transform;
    double minValue;
    double maxValue;
    
    public static DataSource1D getDataSource1D (TabularField field)
    {
        //field.
        return null;
    }
}
