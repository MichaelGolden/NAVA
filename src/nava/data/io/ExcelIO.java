/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.data.io;

import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import nava.data.types.Tabular;
import nava.data.types.TabularData;
import nava.data.types.TabularField;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;

/**
 *
 * @author Michael
 */
public class ExcelIO {

    File file = null;
    Workbook workbook = null;
    DataFormatter formatter = null;
    FormulaEvaluator evaluator = null;

    public ExcelIO(File file) {
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

    public static Tabular getTabularRepresentatation(File excelFile) throws IOException, InvalidFormatException {
        Tabular tabular = new Tabular();

        DecimalFormat df = new DecimalFormat("00");
        Workbook workbook = WorkbookFactory.create(excelFile);
        DataFormatter formatter = new DataFormatter(true);
        FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();

        int numSheets = workbook.getNumberOfSheets();
        tabular.numSheets = numSheets;

        Sheet sheet;

        for (int i = 0; i < numSheets; i++) {
            sheet = workbook.getSheetAt(i);
            if (sheet.getPhysicalNumberOfRows() > 0) {
                ArrayList<String> header = rowToStringList(sheet.getRow(0), formatter, evaluator);
                for (int j = 0; j < header.size(); j++) {
                    tabular.fields.add(new TabularField(tabular, header.get(j), i, j, i * numSheets + j));
                }
            }
        }

        return tabular;
    }

    public static void saveAsCSV(File excelFile, File outFile) throws IOException, InvalidFormatException {
        Workbook workbook = WorkbookFactory.create(excelFile);
        DataFormatter formatter = new DataFormatter(true);
        FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();

        int numSheets = workbook.getNumberOfSheets();

        Sheet sheet;

        BufferedWriter buffer = new BufferedWriter(new FileWriter(outFile));

        for (int r = 0;; r++) {
            ArrayList<String> row = new ArrayList<>();
            boolean cont = false;
            for (int i = 0; i < numSheets; i++) {
                sheet = workbook.getSheetAt(i);
                if (r < sheet.getPhysicalNumberOfRows()) {
                    row.addAll(rowToStringList(sheet.getRow(r), formatter, evaluator));
                    cont = true;
                }
            }

            // if there is a row to write
            if (cont) {
                for (int k = 0; k < row.size() - 1; k++) {
                    buffer.write("\"" + row.get(k) + "\",");
                }
                buffer.write("\"" + row.get(row.size() - 1) + "\"");
                buffer.newLine();
            } else {
                break;
            }
        }
        buffer.close();
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
                        System.out.println(i + "," + j + "\t" + rowToStringList(row, formatter, evaluator).toString());
                    }
                }
            }
        } finally {
            if (fis != null) {
                fis.close();
            }
        }
    }

    private static ArrayList<String> rowToStringList(Row row, DataFormatter formatter, FormulaEvaluator evaluator) {
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
                        if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC)
                        {
                            csvLine.add(cell.getNumericCellValue()+"");
                        }
                        else
                        {
                           csvLine.add(formatter.formatCellValue(cell, evaluator));
                        }
                    } else {
                        CellValue cellValue = evaluator.evaluate(cell);
                        if(cellValue.getCellType() == Cell.CELL_TYPE_NUMERIC)
                        {
                            csvLine.add(cellValue.getNumberValue()+"");
                        }
                        else
                        {
                            csvLine.add(formatter.formatCellValue(cell, evaluator));
                        }                        
                    }
                }
            }
        }
        return csvLine;
    }

    public static void main(String[] args) {
        try {
            new ExcelIO(new File("C:\\Users\\Michael\\Dropbox\\RNA and StatAlign\\correlation_zsexcelfix.xlsx")).printWorkbook();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ExcelIO.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ExcelIO.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidFormatException ex) {
            Logger.getLogger(ExcelIO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
