/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.data.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import nava.data.types.TabularData;
import nava.data.types.TabularField;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;

/**
 *
 * @author Michael
 */
public class ExcelReader {

    File file = null;
    Workbook workbook = null;
    DataFormatter formatter = null;
    FormulaEvaluator evaluator = null;

    public ExcelReader(File file) {
        this.file = file;
    }

    public static boolean isExcelWorkbook(File inFile) throws FileNotFoundException {
        try {
            Workbook workbook = WorkbookFactory.create(inFile);
            DataFormatter formatter = new DataFormatter(true);
            //FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
            int numSheets = workbook.getNumberOfSheets();
            return true;
        } catch (IOException ex) {
        } catch (InvalidFormatException ex) {
        }

        return false;
    }

    public static int getNumberOfSheets(File inFile) throws FileNotFoundException {
        try {
            Workbook workbook = WorkbookFactory.create(inFile);
            DataFormatter formatter = new DataFormatter(true);
            //FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
            return workbook.getNumberOfSheets();
        } catch (IOException ex) {
        } catch (InvalidFormatException ex) {
        }

        return 0;
    }

    public static int getNumberOfColumns(File inFile) throws FileNotFoundException {
        try {
            Workbook workbook = WorkbookFactory.create(inFile);
            DataFormatter formatter = new DataFormatter(true);
            int numSheets = workbook.getNumberOfSheets();
            int totalColumns = 0;
            Sheet sheet = null;
            Row row = null;
            int lastRowNum = 0;
            for (int i = 0; i < numSheets; i++) {
                sheet = workbook.getSheetAt(i);
                totalColumns += sheet.getLastRowNum();
                if (sheet.getPhysicalNumberOfRows() > 0) {
                    lastRowNum = sheet.getLastRowNum();
                    for (int j = 0; j <= lastRowNum; j++) {
                        row = sheet.getRow(j);
                    }
                }
            }

            return totalColumns;
        } catch (IOException ex) {
        } catch (InvalidFormatException ex) {
        }

        return 0;
    }

    public static TabularData getTabularRepresentatation(File inFile) throws FileNotFoundException {
        TabularData tabularData = new TabularData();

        DecimalFormat df = new DecimalFormat("00");
        try {
            Workbook workbook = WorkbookFactory.create(inFile);
            DataFormatter formatter = new DataFormatter(true);
            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
            int numSheets = workbook.getNumberOfSheets();
            Sheet sheet = null;
            //Row row = null;
            for (int i = 0; i < numSheets; i++) {
                sheet = workbook.getSheetAt(i);
                if (sheet.getPhysicalNumberOfRows() > 0) {
                    ArrayList<String> header = rowToCSV(sheet.getRow(0), formatter, evaluator);
                    for (int j = 0; j < header.size(); j++) {
                        tabularData.fields.add(new TabularField((i+1)+"."+df.format(j+1)+": "+header.get(j)));
                    }
                }
            }
        } catch (IOException ex) {
        } catch (InvalidFormatException ex) {
        }

        return tabularData;
    }

    public void printWorkbook() throws FileNotFoundException,
            IOException, InvalidFormatException {
        FileInputStream fis = null;
        try {
            System.out.println("Opening workbook [" + file.getName() + "]");

            fis = new FileInputStream(file);

            // Open the workbook and then create the FormulaEvaluator and
            // DataFormatter instances that will be needed to, respectively,
            // force evaluation of forumlae found in cells and create a
            // formatted String encapsulating the cells contents.

            workbook = WorkbookFactory.create(fis);
            formatter = new DataFormatter(true);
            evaluator = workbook.getCreationHelper().createFormulaEvaluator();

            Sheet sheet = null;
            Row row = null;
            int lastRowNum = 0;

            System.out.println("Converting files contents to CSV format.");

            // Discover how many sheets there are in the workbook....
            int numSheets = workbook.getNumberOfSheets();

            // and then iterate through them.
            for (int i = 0; i < numSheets; i++) {

                // Get a reference to a sheet and check to see if it contains
                // any rows.
                sheet = workbook.getSheetAt(i);
                if (sheet.getPhysicalNumberOfRows() > 0) {

                    // Note down the index number of the bottom-most row and
                    // then iterate through all of the rows on the sheet starting
                    // from the very first row - number 1 - even if it is missing.
                    // Recover a reference to the row and then call another method
                    // which will strip the data from the cells and build lines
                    // for inclusion in the resylting CSV file.
                    lastRowNum = sheet.getLastRowNum();
                    for (int j = 0; j <= lastRowNum; j++) {
                        row = sheet.getRow(j);
//                        this.rowToCSV(row);
                        System.out.println(i + "," + j + "\t" + rowToCSV(row, formatter, evaluator).toString());
                    }
                }
            }
        } finally {
            if (fis != null) {
                fis.close();
            }
        }
    }

    private static ArrayList<String> rowToCSV(Row row, DataFormatter formatter, FormulaEvaluator evaluator) {
        Cell cell = null;
        int lastCellNum = 0;
        ArrayList<String> csvLine = new ArrayList<String>();

        // Check to ensure that a row was recovered from the sheet as it is
        // possible that one or more rows between other populated rows could be
        // missing - blank. If the row does contain cells then...
        if (row != null) {

            // Get the index for the right most cell on the row and then
            // step along the row from left to right recovering the contents
            // of each cell, converting that into a formatted String and
            // then storing the String into the csvLine ArrayList.
            lastCellNum = row.getLastCellNum();
            for (int i = 0; i <= lastCellNum; i++) {
                cell = row.getCell(i);
                if (cell == null) {
                    csvLine.add("");
                } else {
                    if (cell.getCellType() != Cell.CELL_TYPE_FORMULA) {
                        csvLine.add(formatter.formatCellValue(cell));
                    } else {
                        csvLine.add(formatter.formatCellValue(cell, evaluator));
                    }
                }
            }
        }
        return csvLine;
    }

    public static void main(String[] args) {
        try {
            new ExcelReader(new File("C:\\Users\\Michael\\Dropbox\\RNA and StatAlign\\correlation_zsexcelfix.xlsx")).printWorkbook();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ExcelReader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ExcelReader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidFormatException ex) {
            Logger.getLogger(ExcelReader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
