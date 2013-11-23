/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.data.io;

import java.io.*;
import java.util.ArrayList;
import nava.data.types.DataType;
import nava.data.types.DataType.FileFormat;
import nava.data.types.DataType.MatrixFormat;
import nava.data.types.SecondaryStructureData;
import nava.utils.RNAFoldingTools;
import org.biojavax.bio.seq.io.GenbankFormat;

/**
 *
 * @author Michael
 */
public class FileImport {

    public static ArrayList<FileFormat> parsableStructureFormats(File inFile) {
        ArrayList<FileFormat> parsableFormats = new ArrayList<>();

        try {
            readConnectFile(inFile);
            parsableFormats.add(FileFormat.CONNECT_FILE);
        } catch (Exception ex) {
            //ex.printStackTrace();
        }

        try {
            readDotBracketFile(inFile);
            parsableFormats.add(FileFormat.VIENNA_DOT_BRACKET);
        } catch (Exception ex) {
        }

        try {
            readDotBracketOnlyFile(inFile);
            parsableFormats.add(FileFormat.DOT_BRACKET_ONLY);
        } catch (Exception ex) {
            // ex.printStackTrace();
        }

        try {
            readTabDelimittedHelixFile(inFile);
            parsableFormats.add(FileFormat.TAB_DELIMITTED_HELIX);
        } catch (Exception ex) {
            //ex.printStackTrace();
        }

        try {
            readBpseqFile(inFile);
            parsableFormats.add(FileFormat.BPSEQ);
        } catch (Exception ex) {
            //ex.printStackTrace();
        }


        return parsableFormats;
    }

    public static ArrayList<SecondaryStructureData> loadStructures(File inFile, FileFormat format) throws ParserException, IOException, Exception {
        ArrayList<SecondaryStructureData> structures = new ArrayList<SecondaryStructureData>();
        switch (format) {
            case CONNECT_FILE:
                return readConnectFile(inFile);
            case VIENNA_DOT_BRACKET:
                return readDotBracketFile(inFile);
            case DOT_BRACKET_ONLY:
                return readDotBracketOnlyFile(inFile);
            case TAB_DELIMITTED_HELIX:
                return readTabDelimittedHelixFile(inFile);
            case BPSEQ:
                return readBpseqFile(inFile);
        }


        return structures;
    }

    public static ArrayList<MatrixFormat> parsableMatrixFormats(File inFile) {
        ArrayList<MatrixFormat> parsableMatrixFormats = new ArrayList<>();
        
        try {
            MatrixReader.parseCoordinateListMatrix(inFile, "[,\t]+");
            parsableMatrixFormats.add(DataType.MatrixFormat.COORDINATE_LIST_MATRIX);
        } catch (Exception ex) {
        }
        
        try {
            MatrixReader.parseDenseFloatMatrix(inFile);
            parsableMatrixFormats.add(DataType.MatrixFormat.DENSE_MATRIX);
        } catch (Exception ex) {
        }
        return parsableMatrixFormats;
    }

    public static boolean isGenbankFormat(File inFile) throws IOException {
        GenbankFormat gbFormat = new GenbankFormat();
        return gbFormat.canRead(inFile);
    }

    public static ArrayList<DataType> getPossibleDataTypes(File inFile) {
        ArrayList<DataType> possibleDataTypes = new ArrayList<DataType>();
        ArrayList<FileFormat> parsableStructureFormats = parsableStructureFormats(inFile);

        for (int i = 0; i < parsableStructureFormats.size(); i++) {
            possibleDataTypes.add(new DataType(DataType.Primary.SECONDARY_STRUCTURE, parsableStructureFormats.get(i)));
        }
        try {
            if (FileImport.isGenbankFormat(inFile)) {
                possibleDataTypes.add(new DataType(DataType.Primary.ANNOTATION_DATA, DataType.FileFormat.GENBANK));
            }
        } catch (IOException ex) {
        }

        try {
            if (ExcelIO.isExcelWorkbook(inFile)) {
                possibleDataTypes.add(new DataType(DataType.Primary.TABULAR_DATA, DataType.FileFormat.EXCEL));
            }
        } catch (FileNotFoundException ex) {
        }

        if (ReadseqTools.isInFastaFormat(inFile)) {
            possibleDataTypes.add(new DataType(DataType.Primary.ALIGNMENT, DataType.FileFormat.FASTA));
        }

        if (CsvReader.isCsvFormat(inFile)) {
            possibleDataTypes.add(new DataType(DataType.Primary.TABULAR_DATA, DataType.FileFormat.CSV));
        }

        ArrayList<MatrixFormat> matrixFormats = FileImport.parsableMatrixFormats(inFile);
        for (MatrixFormat matrixFormat : matrixFormats) {
            if (matrixFormat == MatrixFormat.COORDINATE_LIST_MATRIX) {
                possibleDataTypes.add(new DataType(DataType.Primary.MATRIX, DataType.FileFormat.COORDINATE_LIST_MATRIX));
            }

            if (matrixFormat == MatrixFormat.DENSE_MATRIX) {
                possibleDataTypes.add(new DataType(DataType.Primary.MATRIX, DataType.FileFormat.DENSE_MATRIX));
            }
        }

        if (ReadseqTools.isKnownFormat(inFile)) {
            
            DataType dataType = new DataType(DataType.Primary.ALIGNMENT, ReadseqTools.getAlignmentFileFormat(inFile));
            if(!possibleDataTypes.contains(dataType) && dataType.fileFormat != FileFormat.UNKNOWN)
            {
                possibleDataTypes.add(dataType);
            }
        }

        return possibleDataTypes;
    }

    public static void main(String[] args) {
        ArrayList<File> files = new ArrayList<File>();
        files.add(new File("examples\\structures\\Crab_rRNA.ct"));
        files.add(new File("examples\\structures\\fmdv_72seq.ct"));
        files.add(new File("examples\\structures\\hiv-shape.txt"));
        files.add(new File("examples\\structures\\bpseq_noheader.bpseq"));
        files.add(new File("examples\\structures\\bpseq_withheader.bpseq"));
        files.add(new File("examples\\structures\\TestRNAData2.dbn"));
        files.add(new File("examples\\structures\\TestRNAData8.dbs"));

        files.add(new File("examples\\alignments\\TestRNAData7.dat.fas"));

        files.add(new File("examples\\annotations\\refseq.gb"));

        files.add(new File("examples\\tabular\\Benchmarks.xlsx"));
        files.add(new File("examples\\tabular\\Benchmarks.xls"));
        files.add(new File("examples\\tabular\\Benchmarks.csv"));
        files.add(new File("examples\\tabular\\hard_parse.csv"));

        System.out.println(getPossibleDataTypes(new File("examples\\tabular\\hard_parse.csv")));

        for (int j = 0; j < files.size(); j++) {
            try {
                ArrayList<FileFormat> parsableFormats = parsableStructureFormats(files.get(j));
                System.out.println(">" + files.get(j));
                for (int i = 0; i < parsableFormats.size(); i++) {
                    System.out.println(parsableFormats.get(i).name());
                }
                System.out.println(ReadseqTools.isKnownFormat(files.get(j)) + "\t" + ReadseqTools.getFormatName(files.get(j)));
                System.out.println("isGenbank?" + isGenbankFormat(files.get(j)));
                System.out.println("isExcel?" + ExcelIO.isExcelWorkbook(files.get(j)));
                System.out.println("isCSV?" + CsvReader.isCsvFormat(files.get(j)));
                System.out.println();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public static ArrayList<SecondaryStructureData> readConnectFile(File inFile) throws IOException, ParserException, Exception {
        ArrayList<SecondaryStructureData> structures = new ArrayList<SecondaryStructureData>();
        try {
            BufferedReader buffer = new BufferedReader(new FileReader(inFile));
            String textline = null;
            //tring header = null;
            SecondaryStructureData s = null;
            while ((textline = buffer.readLine()) != null) {
                if (textline.trim().equals("")) {
                    continue;
                }

                s = new SecondaryStructureData();
                s.sequence = "";
                s.title = textline.trim();
                try {
                    s.pairedSites = new int[Integer.parseInt(textline.trim().split("(\\s)+")[0])];
                } catch (NumberFormatException ex) {
                    throw new ParserException("Connect format expects the first line of a new structure to begin with an integer specifying the length of that structure.");
                }

                for (int i = 0; i < s.pairedSites.length; i++) {
                    textline = buffer.readLine();
                    String[] split = textline.trim().split("(\\s)+");
                    if (split.length >= 5) {
                        if (split[1].length() != 1) {
                            throw new ParserException("Connect format expects 1 base character in 2nd column position.");
                        }
                        s.sequence += split[1];
                        s.pairedSites[i] = Integer.parseInt(split[4]);
                    } else {
                        throw new ParserException("At least 5 columns expected for connect format.");
                    }
                }
                s.sequence = s.sequence.toUpperCase();
                structures.add(s);
            }

            buffer.close();
            return structures;
        } catch (Exception ex) {
            throw ex;
        }
    }

    public static ArrayList<SecondaryStructureData> readDotBracketFile(File inFile) throws IOException, ParserException, Exception {
        ArrayList<SecondaryStructureData> structures = new ArrayList<SecondaryStructureData>();
        try {
            BufferedReader buffer = new BufferedReader(new FileReader(inFile));
            String textline = null;
            //tring header = null;
            SecondaryStructureData s = null;
            while ((textline = buffer.readLine()) != null) {
                if (textline.trim().equals("")) {
                    continue;
                }

                if (textline.startsWith(">")) {
                    s = new SecondaryStructureData();
                    s.sequence = "";
                    s.title = textline.trim().substring(1);
                    s.sequence = buffer.readLine().toUpperCase();
                    s.pairedSites = RNAFoldingTools.getPairedSitesFromDotBracketString(buffer.readLine());
                    structures.add(s);
                } else {
                    throw new ParserException("Dot bracket format expects header line starting with '>'.");
                }
            }

            buffer.close();
            return structures;
        } catch (Exception ex) {
            throw ex;
        }
    }

    public static ArrayList<SecondaryStructureData> readDotBracketOnlyFile(File inFile) throws IOException, ParserException, Exception {
        ArrayList<SecondaryStructureData> structures = new ArrayList<SecondaryStructureData>();
        try {
            BufferedReader buffer = new BufferedReader(new FileReader(inFile));
            String textline = null;
            //tring header = null;
            SecondaryStructureData s = null;
            while ((textline = buffer.readLine()) != null) {
                if (textline.trim().equals("")) {
                    continue;
                }

                s = new SecondaryStructureData();
                s.sequence = "";
                s.title = "";
                if (textline.matches("[)(.]*")) {
                    s.pairedSites = RNAFoldingTools.getPairedSitesFromDotBracketString(textline);
                } else {
                    throw new ParserException("Dot-bracket only format expects only '(.)' characters");
                }
                structures.add(s);
            }

            buffer.close();
            return structures;
        } catch (Exception ex) {
            throw ex;
        }
    }

    public static ArrayList<SecondaryStructureData> readTabDelimittedHelixFile(File helixFile) throws ParserException, Exception {
        return readTabDelimittedHelixFile(helixFile, 0);
    }

    public static ArrayList<SecondaryStructureData> readTabDelimittedHelixFile(File helixFile, int length) throws ParserException, Exception {
        ArrayList<Integer> c1 = new ArrayList<Integer>();
        ArrayList<Integer> c2 = new ArrayList<Integer>();
        ArrayList<Integer> c3 = new ArrayList<Integer>();
        int len = length;

        SecondaryStructureData s = new SecondaryStructureData();
        try {
            BufferedReader buffer = new BufferedReader(new FileReader(helixFile));

            String textline = null;
            while ((textline = buffer.readLine()) != null) {
                String[] split = textline.trim().split("(\\s)+");
                if (split.length == 3) {
                    c1.add(Integer.parseInt(split[0]));
                    c2.add(Integer.parseInt(split[1]));
                    c3.add(Integer.parseInt(split[2]));
                    len = Math.max(len, Integer.parseInt(split[1]));
                } else if (textline.length() > 0) {
                    throw new ParserException("Tab-delimitted helix format expects 3 columns, " + split.length + " were found.");
                }
            }

            buffer.close();
        } catch (Exception ex) {
            throw ex;
        }

        s.pairedSites = new int[len];
        s.title = "";
        for (int i = 0; i < c1.size(); i++) {
            int a = c1.get(i);
            int b = c2.get(i);
            int helixLen = c3.get(i);

            for (int j = 0; j < helixLen; j++) {
                s.pairedSites[a - 1 + j] = b - j;
                s.pairedSites[b - 1 - j] = a + j;
            }
        }
        s.sequence = "";
        ArrayList<SecondaryStructureData> structures = new ArrayList<>();
        structures.add(s);
        return structures;
    }

    public static ArrayList<SecondaryStructureData> readBpseqFile(File inFile) throws IOException, ParserException, Exception {
        try {
            ArrayList<SecondaryStructureData> structures = new ArrayList<SecondaryStructureData>();
            BufferedReader buffer = new BufferedReader(new FileReader(inFile));
            String textline = null;
            //tring header = null;
            SecondaryStructureData s = new SecondaryStructureData();
            ArrayList<String> textlines = new ArrayList<String>();
            while ((textline = buffer.readLine()) != null) {
                if (textline.trim().length() > 0) {
                    textlines.add(textline);
                }

                if (textlines.size() > 4) {
                    String[] split2 = textline.trim().split("(\\s)+");
                    if (!(isInteger(split2[0]) && isInteger(split2[2]))) {
                        throw new ParserException("Bpseq format expects integer positions in columns 1 and 3.");
                    }
                    if (isInteger(split2[1])) {
                        throw new ParserException("Bpseq format expects a nucleotide character in column 2.");
                    }
                }
            }

            String[] split = textlines.get(0).trim().split("(\\s)+");
            int start = 0;
            if (split.length == 3) {
                boolean allIntegers = isInteger(split[0]) && isInteger(split[2]);

                if (!allIntegers) {
                    start = 4;
                    s.title = "";
                    for (int i = 0; i < 4; i++) {
                        s.title += textlines.get(i);
                    }
                } else {
                    s.title = "";
                }
            } else {
                start = 4;
            }

            s.pairedSites = new int[textlines.size() - start];
            s.sequence = "";
            for (int i = start; i < textlines.size(); i++) {
                String[] split2 = textlines.get(i).trim().split("(\\s)+");
                if (split2.length == 3) {
                    if (split2[1].length() == 1 && !isInteger(split2[1])) {
                        s.sequence += split2[1];
                    } else {
                        throw new ParserException("Bpseq format expects 1 base character in 2nd column position.");
                    }
                    if (isInteger(split2[0]) && isInteger(split2[2])) {
                        s.pairedSites[Integer.parseInt(split2[0]) - 1] = Integer.parseInt(split2[2]);
                    } else {
                        throw new ParserException("Bpseq format expects integer positions in columns 1 and 3.");
                    }
                    
                    if(isInteger(split2[1]))
                    {
                        throw new ParserException("Bpseq format expects a base character in column 2.");
                    }
                            
                } else {
                    throw new ParserException("Bpseq format expects exactly 3 columns, " + split2.length + " were found.");
                }

            }
            buffer.close();
            structures.add(s);
            return structures;
        } catch (Exception ex) {
            throw ex;
        }
    }

    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    public static class ParserException extends Exception {

        public ParserException(String message) {
            super(message);
        }
    }
}
