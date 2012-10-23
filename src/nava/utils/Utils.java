/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.utils;

import java.util.Scanner;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class Utils {
    
    public static String nChars(char c, int n)
    {
        String ret = "";
        for(int i = 0 ; i < n ; i++)
        {
            ret += c;
        }
        return ret;
    }
    
    public static boolean isInteger(String s)
    {
        try
        {
            Integer.parseInt(s);
        }
        catch(NumberFormatException ex)
        {
            return false;
        }
        
        return true;       
    }

    public static boolean isNumeric(String s) {
        try
        {
            Double.parseDouble(s);
        }
        catch(NumberFormatException ex)
        {
            return false;
        }
        
        return true;        
    }
}