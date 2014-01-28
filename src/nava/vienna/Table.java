/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.vienna;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class Table 
{
    ArrayList<String> columnNames = new ArrayList<>();
    HashMap<String, ArrayList<Double>> table = new HashMap<>();
    
    public void add(String columnName, double value)
    {
        ArrayList<Double> column = table.get(columnName);
        if(column == null)
        {
            column = new ArrayList<>();
            columnNames.add(columnName);            
            table.put(columnName, column);
        }
        column.add(value);
    }
    
    public ArrayList<Double> getColumn(String column)
    {
        return table.get(column);
    }
    
    public SpearmansCorrelationTest spearmansCorrelation (String col1, String col2)
    {
        return SpearmansCorrelationTest.calculate(getColumn(col1), getColumn(col2));
    }
    
    
    public String getLastRow(String delim)
    {
        String ret = "";
        int lastrow = table.get(columnNames.get(0)).size()-1;
        for(String colName : columnNames)
        {
            ret += getColumn(colName).get(lastrow)+delim;                    
        }
        return ret;
    }
    
    public String getPairwiseSpearmanCorrelations()
    {
        String ret = "";
        for(int i = 0 ; i < columnNames.size() ; i++)
        {
            for(int j = 0 ; j < columnNames.size() ; j++)
            {
                if(i != j)
                {
                    String col1 = columnNames.get(i);
                    String col2 = columnNames.get(j);
                    SpearmansCorrelationTest test = spearmansCorrelation(col1, col2);
                    ret += ""+col1+", "+col2+", "+test.corr+", "+test.pval+"\n";
                }
            }
        }
        return ret;
    }
    
    public void writeTable(File outFile) throws IOException
    {
        BufferedWriter writer = new BufferedWriter(new FileWriter(outFile));
        int row = -1;
        if(row == -1)
        {
            for(String colName : columnNames)
            {
                writer.write(colName+"\t");
            }
            writer.newLine();
            row++;
        }
        
        while(true)
        {
            boolean end = false;
            for(int i = 0 ; i < columnNames.size() ; i++)
            {
                String colName = columnNames.get(i);
                ArrayList<Double> column = getColumn(colName);
                
                if(row < column.size())
                {
                    writer.write(column.get(row) +"\t");
                }
                else
                {
                    end = true;
                    break;
                }
            }
            writer.write("\n");
            if(end)
            {
                break;
            }
            row++;
        }
        
        writer.close();
    }
}
