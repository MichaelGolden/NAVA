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
import nava.utils.Utils;

/**
 *
 * @author Michael
 */
public class MatrixReader {

    public static void parseCoordinateListMatrix(File inFile, String regexSeperator) throws ParserException, IOException {
        BufferedReader buffer = new BufferedReader(new FileReader(inFile));

        String textline = null;
        int last = 0;
        for (int i = 0; (textline = buffer.readLine()) != null; i++) {
            String[] split = textline.trim().split(regexSeperator);

            if (split.length == 3) {
                if (!Utils.isInteger(split[0])) {
                     throw new ParserException("Integer x positions expected in first column.");
                }

                if (!Utils.isInteger(split[1])) {
                    throw new ParserException("Integer x positions expected in second column.");
                }

                if (!Utils.isNumeric(split[2])) {
                    throw new ParserException("Numeric values expected in third column.");
                }
            } else if (split.length != 3 && split.length != 0) {
                throw new ParserException("3 columns expected for coordinate list matrix.");
            } else if (split.length == 0 && (last != 3 || last != 0)) {
                throw new ParserException("Reached end of file unexpectedly.");
            }
        }


        buffer.close();
    }

    public static void parseDenseFloatMatrix(File inFile, String regexSeperator) throws ParserException, IOException {
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
                for (int j = 0; j < n; j++) {
                    try {
                        double f = Double.parseDouble(split[j].trim());
                    } catch (NumberFormatException ex) {
                        throw new ParserException("Matrices expect numeric data only.");
                    }
                }

                m++;
            } else {
                throw new ParserException("Row " + (i + 1) + " of matrix contains " + split.length + " columns instead of " + n + ".");
            }
        }


        buffer.close();
    }

    public static void parseDenseFloatMatrix(File inFile) throws ParserException, IOException {
        parseDenseFloatMatrix(inFile, "[\\s,;]+");
    }
}
