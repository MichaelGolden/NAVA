/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.data.io;

import au.com.bytecode.opencsv.CSVReader;
import java.io.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Michael
 */
public class CsvReader {

    public static void main(String[] args) {
        try {
            System.out.println(CsvReader.getColumn(new File("workspace/test_project/0.csv"), 0));
        } catch (IOException ex) {
            Logger.getLogger(CsvReader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static ArrayList<String> getColumn(File inFile, int column) throws IOException {
        ArrayList<String> cells = new ArrayList<>();

        CSVReader reader = new CSVReader(new FileReader(inFile));
        String[] nextLine;
        while ((nextLine = reader.readNext()) != null) {
             if (column < nextLine.length) {
                cells.add(nextLine[column]);
            } else {
                cells.add("");
            }
        }
        reader.close();

        return cells;
    }

    public static int getNumberOfColumns(File inFile) {
        try {
            BufferedReader buffer = new BufferedReader(new FileReader(inFile));
            int tempCount = count(buffer.readLine().replaceAll("\"[^\"]*\"", ""), ',');
            buffer.close();
            return (tempCount + 1);
        } catch (IOException ex) {
            Logger.getLogger(CsvReader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    public static boolean isCsvFormat(File inFile) {
        try {
            BufferedReader buffer = new BufferedReader(new FileReader(inFile));
            String textline = null;
            int columns = -1;
            for (long line = 0; (textline = buffer.readLine()) != null; line++) {
                int tempCount = count(textline.replaceAll("\"[^\"]*\"", ""), ',');
                //System.out.println(textline.replaceAll("\"[^\"]*\"", ""));
                //System.out.println(tempCount);
                if (line == 0 && tempCount == 0) // first line must have at least 1 comma
                {
                    return false;
                } else if (line == 0 || columns == tempCount) // lines must have same number of commas
                {
                } else if (tempCount == 0) // rest of the lines must have zero commas
                {
                } else {
                    return false;
                }

                columns = tempCount;
            }
            buffer.close();
        } catch (IOException ex) {
            return false;
        }

        return true;
    }

    public static int count(String s, char c) {
        int count = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == c) {
                count++;
            }
        }
        return count;
    }
}
