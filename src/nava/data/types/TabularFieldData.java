/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.data.types;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import nava.data.io.CsvReader;
import nava.utils.Utils;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class TabularFieldData {

    public ArrayList<String> values;
    
    public TabularFieldData(ArrayList<String> values)
    {
        this.values = values;
    }
    
    public int getLength()
    {
        return values.size();
    }
    
    public ArrayList<Double> getNumericValues()
    {
        ArrayList<Double> ret = new  ArrayList<>();
        for(int i = 0 ; i < values.size() ; i++)
        {
            if(Utils.isNumeric(values.get(i)))
            {
                ret.add(Double.parseDouble(values.get(i)));
            }
        }
        return ret;
    }
    
    public static TabularFieldData getColumn(File csvFile, int index) throws IOException
    {
        return new TabularFieldData(CsvReader.getColumn(csvFile, index));
    }
}
