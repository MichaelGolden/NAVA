/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.utils;

import java.io.File;
import java.io.IOException;
import java.util.Random;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class Utils {

    public static String padStringRight(String s, int n, char c) {
        String ret = s;
        for (int i = s.length(); i < n; i++) {
            ret += c;
        }
        return ret;
    }

    public static String padStringLeft(String s, int n, char c) {
        String ret = s;
        for (int i = s.length(); i < n; i++) {
            ret = c + ret;
        }
        return ret;
    }

    public static String nChars(char c, int n) {
        String ret = "";
        for (int i = 0; i < n; i++) {
            ret += c;
        }
        return ret;
    }

    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException ex) {
            return false;
        }

        return true;
    }

    public static boolean isNumeric(String s) {
        try {
            Double.parseDouble(s);
        } catch (NumberFormatException ex) {
            return false;
        }

        return true;
    }

    public static String wrapText(String s, int width) {
        String wrappedText = "";

        String[] split = s.split("\n");
        for (int k = 0; k < split.length; k++) {
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
            if (k != split.length - 1) {
                wrappedText += "\n";
            }
        }

        return wrappedText;
    }

    public static String plainTextToHtml(String plainText) {
        return "<html>" + plainText.replaceAll("\n", "<br>") + "</html>";
    }

    public static boolean[] randomBooleanArray(Random random, int n, boolean[] array) {
        int end = Math.min(n, array.length);
        for (int i = 0; i < end; i++) {
            int a = random.nextInt(array.length);
            for (int j = a; j < a + array.length; j++) {
                if (!array[j % array.length]) {
                    array[j % array.length] = true;
                    break;
                }
            }
        }
        return array;
    }

    public static boolean isFilenameValid(File f) {
        try {
            f.getCanonicalPath();
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
