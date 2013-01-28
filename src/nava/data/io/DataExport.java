/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.data.io;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import nava.data.types.*;
import nava.data.types.DataType.FileFormat;
import nava.ui.MainFrame;
import nava.utils.RNAFoldingTools;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class DataExport {

    private ArrayList<ExportableFormat> exportableFormats = new ArrayList<>();
    private List<DataSource> dataSources;
    private List<FileFormat> formats;

    public DataExport() {
        exportableFormats.add(new ExportableFormat("alignment", Alignment.class, FileFormat.FASTA));
        exportableFormats.add(new ExportableFormat("alignment", Alignment.class, FileFormat.PHYLIP4));
        exportableFormats.add(new ExportableFormat("alignment", Alignment.class, FileFormat.NEXUS));
        exportableFormats.add(new ExportableFormat("alignment", Alignment.class, FileFormat.CLUSTAL));
        exportableFormats.add(new ExportableFormat("tabular", Tabular.class, FileFormat.CSV));
        exportableFormats.add(new ExportableFormat("structure", SecondaryStructure.class, FileFormat.VIENNA_DOT_BRACKET));
        exportableFormats.add(new ExportableFormat("structure", SecondaryStructure.class, FileFormat.CONNECT_FILE));
       // exportableFormats.add(new ExportableFormat("structure", SecondaryStructure.class, FileFormat.BPSEQ));
        exportableFormats.add(new ExportableFormat("matrix", Matrix.class, FileFormat.COORDINATE_LIST_MATRIX));
        exportableFormats.add(new ExportableFormat("matrix", Matrix.class, FileFormat.DENSE_MATRIX));
    }

    public ArrayList<ExportableFormat> getExportableFormats(DataSource dataSource) {
        ArrayList<ExportableFormat> exportableFormatsForDataSource = new ArrayList<>();
        for (int i = 0; i < exportableFormats.size(); i++) {
            System.out.println(exportableFormats.get(i));
            if (dataSource.getClass().equals(exportableFormats.get(i).dataSourceClass)) {
                exportableFormatsForDataSource.add(exportableFormats.get(i));
            }
        }
        return exportableFormatsForDataSource;
    }

    public void export(DataSource dataSource, FileFormat format, File outputFile) throws Exception {
        switch (format) {
            case FASTA:
                ReadseqTools.convertToFormat(8, new File(dataSource.importedDataSourcePath), outputFile);
                break;
            case PHYLIP4:
                ReadseqTools.convertToFormat(12, new File(dataSource.importedDataSourcePath), outputFile);
                break;
            case NEXUS:
                ReadseqTools.convertToFormat(17, new File(dataSource.importedDataSourcePath), outputFile);
                break;
            case CLUSTAL:
                ReadseqTools.convertToFormat(22, new File(dataSource.importedDataSourcePath), outputFile);
                break;
            case CSV:
                Files.copy(Paths.get(dataSource.importedDataSourcePath), Paths.get(outputFile.getAbsolutePath()));
            case VIENNA_DOT_BRACKET:
                SecondaryStructureData s = FileImport.readDotBracketFile(new File(dataSource.importedDataSourcePath)).get(0);
                RNAFoldingTools.saveDotBracketFile(outputFile, s.pairedSites, s.title, s.sequence);
                break;
            case CONNECT_FILE:
                SecondaryStructureData s2 = FileImport.readDotBracketFile(new File(dataSource.importedDataSourcePath)).get(0);
                RNAFoldingTools.saveCtFile(outputFile, s2.pairedSites, s2.title, s2.sequence);
                break;
            //case BPSEQ:
            //    break;
            case COORDINATE_LIST_MATRIX:
                if(dataSource instanceof Matrix)
                {
                    ((Matrix)dataSource).getObject(MainFrame.dataSourceCache).saveAsCoordinateListMatrix(outputFile);
                }
                break;
             case DENSE_MATRIX:
                if(dataSource instanceof Matrix)
                {
                    ((Matrix)dataSource).getObject(MainFrame.dataSourceCache).saveAsDenseMatrix(outputFile);
                }
                break;
        }
    }

    public void setDataSourcesToExport(List<DataSource> dataSources) {
        this.dataSources = dataSources;
    }

    public void setExportFormats(List<FileFormat> formats) {
        this.formats = formats;
    }

    public class ExportableFormat {

        public String groupName;
        public Class dataSourceClass;
        public FileFormat exportFormat;

        public ExportableFormat(String groupName, Class dataSourceClass, FileFormat exportFormat) {
            this.groupName = groupName;
            this.dataSourceClass = dataSourceClass;
            this.exportFormat = exportFormat;
        }
    }
}
