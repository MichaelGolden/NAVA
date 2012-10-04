/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.data.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import nava.data.io.FileImport.ParserException;

/**
 *
 * @author Michael
 */
public class MatrixReader {
    
   

    public static float[][] readDenseFloatMatrix(File inFile, String regexSeperator) throws ParserException, IOException 
    {
        BufferedReader buffer = new BufferedReader(new FileReader(inFile));

        String textline = null;
        int n = 0;
        int m = 0;
        for (int i = 0; (textline = buffer.readLine()) != null; i++) {
            String[] split = textline.trim().split(regexSeperator);
            if (i == 0) {
                n = split.length;
            }

            if (n == split.length || split.length == 0) {
                for(int j = 0 ; j < n ; j++)
                {
                    try
                    {
                        float f = Float.parseFloat(textline);
                    }
                    catch(NumberFormatException ex)
                    {
                        throw new ParserException("Matrices expect numeric data only.");
                    }
                }
                
                m++;
            } else {
                throw new ParserException("Row " + (i + 1) + " of matrix contains " + split.length + " columns instead of " + n + ".");
            }
        }


        buffer.close();
        
        return null;
    }
    
    public static float [][] readDenseFloatMatrix(File inFile) throws ParserException, IOException
    {
        return readDenseFloatMatrix(inFile, "[\\s,;]+");
    }
}
