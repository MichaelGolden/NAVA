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
    
    public static String wrapText(String s, int width) {
        String wrappedText = "";

        String[] split = s.split("\n");
        for (int k = 0 ; k < split.length ; k++) {
            String wrappedLine = "";
            String line = split[k];
            String remainingText = line;
            while (remainingText.length() > width) {
                int i = remainingText.lastIndexOf(" ", width);
                if (i == -1) {
                    i = width;
                }
                wrappedLine += remainingText.substring(0, i).trim() + "\n";
                remainingText = remainingText.substring(i);
            }
            wrappedLine += remainingText.trim();
            wrappedText += wrappedLine;
            if(k != split.length-1)
            {
                wrappedText += "\n";
            }
        }
        
        return wrappedText;
    }
    
    public static String plainTextToHtml(String plainText)
    {
        return "<html>"+plainText.replaceAll("\n", "<br>") +"</html>";
    }
}
