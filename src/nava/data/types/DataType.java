/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.data.types;

import java.io.Serializable;

/**
 *
 * @author Michael
 */
public class DataType implements Serializable {

    public enum StructureFormat {

        CONNECT_FILE, VIENNA_DOT_BRACKET, DOT_BRACKET_ONLY, BPSEQ, TAB_DELIMITTED_HELIX
    };

    public enum TabularFormat {

        EXCEL, CSV
    };

    public enum MatrixFormat {

        DENSE_MATRIX
    };

    public enum Primary {

        UNKNOWN, SECONDARY_STRUCTURE, ALIGNMENT, ANNOTATION_DATA, TABULAR_DATA, MATRIX;

        @Override
        public String toString() {
            String s = "Unknown";
            switch (this) {
                case UNKNOWN:
                    s = "Unknown";
                    break;
                case SECONDARY_STRUCTURE:
                    s = "Structure data";
                    break;
                case ALIGNMENT:
                    s = "Alignment";
                    break;
                case ANNOTATION_DATA:
                    s = "Annotation data";
                    break;
                case TABULAR_DATA:
                    s = "Tabular data";
                    break;
                case MATRIX:
                    s = "Matrix";
                    break;
            }
            return s;
        }
    };

    public enum FileFormat {

        UNKNOWN(Primary.UNKNOWN),
        CONNECT_FILE(Primary.SECONDARY_STRUCTURE), VIENNA_DOT_BRACKET(Primary.SECONDARY_STRUCTURE), DOT_BRACKET_ONLY(Primary.SECONDARY_STRUCTURE), BPSEQ(Primary.SECONDARY_STRUCTURE), TAB_DELIMITTED_HELIX(Primary.SECONDARY_STRUCTURE),
        GENBANK(Primary.ANNOTATION_DATA),
        EXCEL(Primary.TABULAR_DATA), CSV(Primary.TABULAR_DATA);
        
        public Primary primaryType = Primary.UNKNOWN;
        
        FileFormat(Primary primaryType)
        {
            this.primaryType = primaryType;
        }

        @Override
        public String toString() {
            switch (this) {
                case UNKNOWN:
                    return "Unknown";
                case CONNECT_FILE:
                    return "Connect format";
                case VIENNA_DOT_BRACKET:
                    return "Vienna dot bracket format";
                case DOT_BRACKET_ONLY:
                    return "Dot bracket string only";
                case BPSEQ:
                    return "Bpseq format";
                case TAB_DELIMITTED_HELIX:
                    return "Tab-delimitted helix format";
                case GENBANK:
                    return "Genbank";
                case EXCEL:
                    return "Excel workbook";
                case CSV:
                    return "Comma-seperated values";
                default:
                    return "Unknown";
            }
        }
    };
    public Primary primaryType = Primary.UNKNOWN;
    public FileFormat fileFormat = FileFormat.UNKNOWN;

    public DataType(Primary type) {
        this.primaryType = type;
        this.fileFormat = FileFormat.UNKNOWN;
    }

    public DataType(Primary primaryType, FileFormat fileFormat) {
        this.primaryType = primaryType;
        this.fileFormat = fileFormat;
    }

    @Override
    public String toString() {
        return primaryType.toString() + " (" + fileFormat.toString() + ")";
    }
}
