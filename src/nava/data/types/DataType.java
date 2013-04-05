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
    private static final long serialVersionUID = -6001358785173256742L;

    /*
    public enum StructureFormat {

        CONNECT_FILE, VIENNA_DOT_BRACKET, DOT_BRACKET_ONLY, BPSEQ, TAB_DELIMITTED_HELIX
    };
    * 
    */

    public enum TabularFormat {

        EXCEL, CSV
    };

    public enum MatrixFormat {

        DENSE_MATRIX, COORDINATE_LIST_MATRIX
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
        FASTA(Primary.ALIGNMENT), PHYLIP4(Primary.ALIGNMENT), NEXUS(Primary.ALIGNMENT), CLUSTAL(Primary.ALIGNMENT),
        CONNECT_FILE(Primary.SECONDARY_STRUCTURE), VIENNA_DOT_BRACKET(Primary.SECONDARY_STRUCTURE), DOT_BRACKET_ONLY(Primary.SECONDARY_STRUCTURE), BPSEQ(Primary.SECONDARY_STRUCTURE), TAB_DELIMITTED_HELIX(Primary.SECONDARY_STRUCTURE),
        GENBANK(Primary.ANNOTATION_DATA),
        EXCEL(Primary.TABULAR_DATA), CSV(Primary.TABULAR_DATA),
        COORDINATE_LIST_MATRIX(Primary.MATRIX), DENSE_MATRIX(Primary.MATRIX);
        public Primary primaryType = Primary.UNKNOWN;

        FileFormat(Primary primaryType) {
            this.primaryType = primaryType;
        }

        public String getExtension() {
            switch (this) {
                case UNKNOWN:
                    return "dat";
                case FASTA:
                    return "fas";
                case PHYLIP4:
                    return "phy";
                case NEXUS:
                    return "nex";
                case CLUSTAL:
                    return "aln";
                case CONNECT_FILE:
                    return "ct";
                case VIENNA_DOT_BRACKET:
                    return "dbn";
                case DOT_BRACKET_ONLY:
                    return "dbs";
                case BPSEQ:
                    return "bp";
                case TAB_DELIMITTED_HELIX:
                    return "helix";
                case GENBANK:
                    return "gb";
                case EXCEL:
                    return "xlsx";
                case CSV:
                    return "csv";
                case COORDINATE_LIST_MATRIX:
                    return "cmt";
                case DENSE_MATRIX:
                    return "mt";
                default:
                    return "dat";
            }
        }

        @Override
        public String toString() {
            switch (this) {
                case UNKNOWN:
                    return "Unknown";
                case FASTA:
                    return "Fasta";
                case PHYLIP4:
                    return "Phylip4";
                case NEXUS:
                    return "Nexus";
                case CLUSTAL:
                    return "Clustal";
                case CONNECT_FILE:
                    return "Connect";
                case VIENNA_DOT_BRACKET:
                    return "Vienna dot bracket";
                case DOT_BRACKET_ONLY:
                    return "Dot bracket string only";
                case BPSEQ:
                    return "Bpseq";
                case TAB_DELIMITTED_HELIX:
                    return "Tab-delimitted helix";
                case GENBANK:
                    return "Genbank";
                case EXCEL:
                    return "Excel workbook";
                case CSV:
                    return "Comma-seperated values";
                case COORDINATE_LIST_MATRIX:
                    return "Co-ordinate list matrix";
                case DENSE_MATRIX:
                    return "Dense matrix";
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

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DataType other = (DataType) obj;
        if (this.primaryType != other.primaryType) {
            return false;
        }
        if (this.fileFormat != other.fileFormat) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + (this.primaryType != null ? this.primaryType.hashCode() : 0);
        hash = 53 * hash + (this.fileFormat != null ? this.fileFormat.hashCode() : 0);
        return hash;
    }
    
}
