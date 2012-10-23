/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.ui;

//import nava.data.types.DataSource;
import nava.data.types.DataType;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.TreeModelListener;
import nava.analyses.ApplicationOutput;
import nava.data.io.*;
import nava.data.io.FileImport.ParserException;
import nava.data.types.*;

/**
 *
 * @author Michael
 */
public class ProjectController implements ListDataListener {

    ArrayList<ProjectView> projectViews = new ArrayList<ProjectView>();
    public ProjectModel projectModel;

    public ProjectController() {
        /*
         * this.projectModel = projectModel;
         *
         * this.projectModel.dataSources.addListDataListener(this);
         */
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
                        dataSource.originalDataSourcePath = generatePath(dataSource.getImportId(), "orig." + dataFile.getName().substring(dataFile.getName().lastIndexOf('.') + 1)).toString();
                        dataSource.importedDataSourcePath = generatePath(dataSource.getImportId(), dataFile.getName().substring(dataFile.getName().lastIndexOf('.') + 1)).toString();
                        dataSource.title = dataFile.getName().replaceAll("\\.[^\\.]+$", "");
                        dataSource.dataType = dataType;
                        dataSource.persistObject(structures.get(0));
                        dataSource.fileSize = new FileSize(dataFile.length());
                    } else {
                        dataSource = new StructureList(dataFile.getName());
                        dataSource.setImportId(getNextImportId());
                        dataSource.originalFile = dataFile;
                        dataSource.originalDataSourcePath = generatePath(dataSource.getImportId(), "orig." + dataFile.getName().substring(dataFile.getName().lastIndexOf('.') + 1)).toString();
                        dataSource.importedDataSourcePath = generatePath(dataSource.getImportId(), dataFile.getName().substring(dataFile.getName().lastIndexOf('.') + 1)).toString();
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
                    if(dataType.fileFormat.equals(DataType.FileFormat.EXCEL))
                    {
                        dataSource = ExcelIO.getTabularRepresentation(dataFile);
                        dataSource.setImportId(getNextImportId());
                        dataSource.originalFile = dataFile;
                        dataSource.originalDataSourcePath = generatePath(dataSource.getImportId(), "orig." + dataFile.getName().substring(dataFile.getName().lastIndexOf('.') + 1)).toString();
                        dataSource.importedDataSourcePath = generatePath(dataSource.getImportId(), "csv").toString();
                        dataSource.title = dataFile.getName().replaceAll("\\.[^\\.]+$", "");
                        dataSource.dataType = dataType;
                        dataSource.persistObject(dataSource);
                        dataSource.fileSize = new FileSize(dataFile.length());                        
                        ExcelIO.saveAsCSV(dataFile, Paths.get(dataSource.importedDataSourcePath).toFile());
                    }
                    else
                    if(dataType.fileFormat.equals(DataType.FileFormat.CSV))
                    {
                        dataSource = CsvReader.getTabularRepresentation(dataFile);
                        dataSource.setImportId(getNextImportId());
                        dataSource.originalFile = dataFile;
                        dataSource.originalDataSourcePath = generatePath(dataSource.getImportId(), "orig." + dataFile.getName().substring(dataFile.getName().lastIndexOf('.') + 1)).toString();
                        dataSource.importedDataSourcePath = generatePath(dataSource.getImportId(), "csv").toString();
                        dataSource.title = dataFile.getName().replaceAll("\\.[^\\.]+$", "");
                        dataSource.dataType = dataType;
                        dataSource.persistObject(dataSource);
                        dataSource.fileSize = new FileSize(dataFile.length());                        
                        Files.copy(Paths.get(dataFile.getAbsolutePath()), Paths.get(dataSource.importedDataSourcePath));
                    }
                } catch (IOException ex) {
                    Logger.getLogger(ProjectController.class.getName()).log(Level.SEVERE, null, ex);
                }
                catch(Exception ex)
                {
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
                al.originalDataSourcePath = generatePath(al.getImportId(), "orig." + dataFile.getName().substring(dataFile.getName().lastIndexOf('.') + 1)).toString();
                al.importedDataSourcePath = generatePath(al.getImportId(), "fas").toString();
                al.title = dataFile.getName().replaceAll("\\.[^\\.]+$", "");
                al.dataType = dataType;
                al.fileSize = new FileSize(dataFile.length());

                try {
                    ReadseqTools.saveAsFASTA(dataFile, Paths.get(al.importedDataSourcePath).toFile());
                    al.numSequences = IO.countFastaSequences(Paths.get(al.importedDataSourcePath).toFile());
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
            Files.copy(sourcePath, Paths.get(dataSource.originalDataSourcePath));
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        projectModel.dataSources.addElement(dataSource);
    }

    public void importDataSourceFromOutputFile(ApplicationOutput outputFile) {
        if (outputFile.dataSource instanceof SecondaryStructure) {
            if (outputFile.object != null) {
                SecondaryStructure structure = (SecondaryStructure) outputFile.dataSource;
                structure.setImportId(getNextImportId());
                structure.originalDataSourcePath = generatePath(outputFile.dataSource.getImportId(), "dbn").toString();
                structure.importedDataSourcePath = generatePath(outputFile.dataSource.getImportId(), "dbn").toString();
                structure.title = outputFile.dataSource.title;
                structure.persistObject(outputFile.object);

                //dataSource.dataType = DataType.Primary.SECONDARY_STRUCTURE;
                structure.fileSize = new FileSize(Paths.get(structure.originalDataSourcePath).toFile().length());
                projectModel.dataSources.addElement(structure);
            }
        } else if (outputFile.dataSource instanceof Matrix) {
            if (outputFile.object != null) {
                Matrix matrix = (Matrix) outputFile.dataSource;
                matrix.setImportId(getNextImportId());
                matrix.originalDataSourcePath = generatePath(outputFile.dataSource.getImportId(), "matrix").toString();
                matrix.importedDataSourcePath = generatePath(outputFile.dataSource.getImportId(), "matrix").toString();
                matrix.title = outputFile.dataSource.title;
                matrix.persistObject(outputFile.object);

                matrix.fileSize = new FileSize(Paths.get(matrix.originalDataSourcePath).toFile().length());
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
        projectModel.dataSourceCounter = DataSource.getCount();

        // a terrible, but necessary hack in order to save the project (tree listener classes prevent serialization)
        ArrayList<TreeModelListener> treeListenersList = new ArrayList<>();
        TreeModelListener[] treeListeners = projectModel.navigatorTreeModel.getTreeModelListeners();
        for (int i = 0; i < treeListeners.length; i++) {
            treeListenersList.add(treeListeners[i]);
            projectModel.navigatorTreeModel.removeTreeModelListener(treeListeners[i]);
        }

        projectModel.saveProject(projectModel.getProjectPath().resolve(Paths.get("project.data")).toFile());

        // re-add the listeners, this is only necessary if the application stays open
        for (int i = 0; i < treeListeners.length; i++) {
            projectModel.navigatorTreeModel.addTreeModelListener(treeListenersList.get(i));
        }
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
        return projectModel.importCounter++;
    }
}
