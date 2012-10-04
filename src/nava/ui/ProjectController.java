/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.ui;

//import nava.data.types.DataSource;
import nava.data.types.DataType;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import nava.analyses.OutputFile;
import nava.analyses.RNAalifold;
import nava.data.io.ExcelReader;
import nava.data.io.FileImport;
import nava.data.io.FileImport.ParserException;
import nava.data.io.IO;
import nava.data.io.ReadseqTools;
import nava.data.types.*;
import nava.utils.RNAFoldingTools;

/**
 *
 * @author Michael
 */
public class ProjectController implements ListDataListener {

    private long importCount = 0;
    ArrayList<ProjectView> projectViews = new ArrayList<ProjectView>();
    public ProjectModel projectModel;

    public ProjectController(ProjectModel projectModel) {
        this.projectModel = projectModel;

        this.projectModel.dataSources.addListDataListener(this);
    }

    public void importDataSourceFromFile(File dataFile, DataType dataType) {
        Path sourcePath = Paths.get(dataFile.getAbsolutePath());

        DataSource dataSource = null;
        switch (dataType.primaryType) {
            case SECONDARY_STRUCTURE:
                try {
                    ArrayList<SecondaryStructureData> structures = FileImport.loadStructures(dataFile, dataType.fileFormat);

                    if (structures.size() == 1) {
                        dataSource = new SecondaryStructure();
                        dataSource.setImportId(getNextImportId());
                        dataSource.originalFile = dataFile;
                        dataSource.originalDataSourcePath = generatePath(dataSource.getImportId(), "orig." + dataFile.getName().substring(dataFile.getName().lastIndexOf('.') + 1));
                        dataSource.importedDataSourcePath = generatePath(dataSource.getImportId(), dataFile.getName().substring(dataFile.getName().lastIndexOf('.') + 1));
                        dataSource.title = dataFile.getName().replaceAll("\\.[^\\.]+$", "");
                        dataSource.dataType = dataType;
                        dataSource.persistObject(structures.get(0));
                        dataSource.fileSize = new FileSize(dataFile.length());
                        //projectModel.dataSources.addElement(dataSource);
                    } else {
                        dataSource = new StructureList(dataFile.getName());
                        dataSource.setImportId(getNextImportId());
                        dataSource.originalFile = dataFile;
                        dataSource.originalDataSourcePath = generatePath(dataSource.getImportId(), "orig." + dataFile.getName().substring(dataFile.getName().lastIndexOf('.') + 1));
                        dataSource.importedDataSourcePath = generatePath(dataSource.getImportId(), dataFile.getName().substring(dataFile.getName().lastIndexOf('.') + 1));
                        dataSource.title = dataFile.getName().replaceAll("\\.[^\\.]+$", "");
                        dataSource.dataType = dataType;
                        dataSource.persistObject(structures);
                        dataSource.fileSize = new FileSize(dataFile.length());
                    }
                } catch (ParserException ex) {
                    Logger.getLogger(ProjectController.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(ProjectController.class.getName()).log(Level.SEVERE, null, ex);
                } catch (Exception ex) {
                    Logger.getLogger(ProjectController.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
            case TABULAR_DATA:
                try {
                    dataSource = ExcelReader.getTabularRepresentatation(dataFile);
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(ProjectController.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
            case ANNOTATION_DATA:
                dataSource = new Annotations();
                break;
            case MATRIX:
                dataSource = new Matrix();
                break;
            case ALIGNMENT:
                Alignment al = new Alignment();
                al.setImportId(getNextImportId());
                al.title = dataFile.getName().replaceAll("\\.[^\\.]+$", "");
                al.originalDataSourcePath = generatePath(al.getImportId(), "orig." + dataFile.getName().substring(dataFile.getName().lastIndexOf('.') + 1));
                al.importedDataSourcePath = generatePath(al.getImportId(), "fas");
                al.title = dataFile.getName().replaceAll("\\.[^\\.]+$", "");
                al.dataType = dataType;
                al.fileSize = new FileSize(dataFile.length());

                try {
                    ReadseqTools.saveAsFASTA(dataFile, al.importedDataSourcePath.toFile());
                    al.numSequences = IO.countFastaSequences(al.importedDataSourcePath.toFile());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                dataSource = al;
                break;
        }

        // place a copy of the original data source in the workspace
        try {
            System.out.println(sourcePath);
            System.out.println(dataSource.originalDataSourcePath);
            Files.copy(sourcePath, dataSource.originalDataSourcePath);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        projectModel.dataSources.addElement(dataSource);
    }

    public void importDataSourceFromOutputFile(OutputFile outputFile) {
        if (outputFile.dataSource instanceof SecondaryStructure) {
            if (outputFile.object != null) {
                SecondaryStructure structure = (SecondaryStructure) outputFile.dataSource;
                structure.setImportId(getNextImportId());
                structure.originalDataSourcePath = generatePath(outputFile.dataSource.getImportId(), "dbn");
                structure.importedDataSourcePath = generatePath(outputFile.dataSource.getImportId(), "dbn");
                structure.title = outputFile.dataSource.title;
                structure.persistObject(outputFile.object);

                //dataSource.dataType = DataType.Primary.SECONDARY_STRUCTURE;
                structure.fileSize = new FileSize(structure.originalDataSourcePath.toFile().length());
                projectModel.dataSources.addElement(structure);
            }
        } else if (outputFile.dataSource instanceof Matrix) {
            if (outputFile.object != null) {
                Matrix matrix = (Matrix) outputFile.dataSource;
                matrix.setImportId(getNextImportId());
                matrix.originalDataSourcePath = generatePath(outputFile.dataSource.getImportId(), "matrix");
                matrix.importedDataSourcePath = generatePath(outputFile.dataSource.getImportId(), "matrix");
                matrix.title = outputFile.dataSource.title;
                matrix.persistObject(outputFile.object);

                matrix.fileSize = new FileSize(matrix.originalDataSourcePath.toFile().length());
                projectModel.dataSources.addElement(matrix);
            }
        }

    }

    public Path generatePath(long id, String extension) {
        Path p = projectModel.getProjectPath().resolve(Paths.get(id + "." + extension));
        if (Files.exists(p)) {
            System.err.println("This file already exists should throw an error or do something about it.");
        }
        return p;
    }

    public void autoAddDataSource(File dataFile) {
        ArrayList<DataType> possibleDataTypes = null;
        if (dataFile.isFile()) {
            possibleDataTypes = FileImport.getPossibleDataTypes(dataFile);
        }
        if (possibleDataTypes != null && possibleDataTypes.size() > 0) {
            importDataSourceFromFile(dataFile, possibleDataTypes.get(0));
        }
    }

    public void addView(ProjectView view) {
        projectViews.add(view);
    }

    public void removeView(ProjectView view) {
        projectViews.remove(view);
    }

    public void openProject(ProjectModel projectModel) {
        this.projectModel = projectModel;

        DataSource.setCount(projectModel.dataSourceCounter);

        // re-register listeners
        this.projectModel.dataSources.addListDataListener(this);

        for (int i = 0; i < projectViews.size(); i++) {
            projectViews.get(i).dataSourcesLoaded();
        }
    }

    public void saveProject() {
        System.out.println("Saving project " + projectModel.dataSources.size());
        projectModel.dataSourceCounter = DataSource.getCount();
        projectModel.saveProject(projectModel.getProjectPath().resolve(Paths.get("project.data")).toFile());
    }

    @Override
    public void intervalAdded(ListDataEvent e) {
        System.out.println("intervalAdded: " + e);
        if (e.getSource().equals(this.projectModel.dataSources)) {
            for (int i = 0; i < projectViews.size(); i++) {
                projectViews.get(i).dataSourcesIntervalAdded(e);
            }
        }
    }

    @Override
    public void intervalRemoved(ListDataEvent e) {
        System.out.println("intervalRemoved: " + e);
        if (e.getSource().equals(this.projectModel.dataSources)) {
            for (int i = 0; i < projectViews.size(); i++) {
                projectViews.get(i).dataSourcesIntervalRemoved(e);
            }
        }
    }

    @Override
    public void contentsChanged(ListDataEvent e) {
        System.out.println("contentsChanged: " + e);
        if (e.getSource().equals(this.projectModel.dataSources)) {
            for (int i = 0; i < projectViews.size(); i++) {
                projectViews.get(i).dataSourcesContentsChanged(e);
            }
        }
    }

    public long getNextImportId() {
        return importCount++;
    }
}
