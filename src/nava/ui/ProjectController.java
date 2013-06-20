/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.ui;

//import nava.data.types.DataSource;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.TreeModelListener;
import nava.data.io.FileImport.ParserException;
import nava.data.io.*;
import nava.data.types.*;
import nava.data.types.DataType.FileFormat;
import nava.structurevis.data.PersistentSparseMatrix;
import nava.tasks.applications.ApplicationOutput;
import nava.utils.AlignmentUtils;
import nava.utils.GraphicsUtils;
import nava.utils.Pair;
import nava.utils.SafeListEvent;
import nava.utils.SafeListListener;

/**
 *
 * @author Michael
 */
public class ProjectController implements SafeListListener {

    public transient ArrayList<ProjectView> projectViews = new ArrayList<>();
    public ProjectModel projectModel;

    public ProjectController() {
        projectViews = new ArrayList<>();
        /*
         * this.projectModel = projectModel;
         *
         * this.projectModel.dataSources.addListDataListener(this);
         */
    }

    public DataSource importDataSourceFromFile(File dataFile, DataType dataType) {
        Path sourcePath = Paths.get(dataFile.getAbsolutePath());

        DataSource dataSource = null;
        switch (dataType.primaryType) {
            case SECONDARY_STRUCTURE:
                try {
                    ArrayList<SecondaryStructureData> structures = FileImport.loadStructures(dataFile, dataType.fileFormat);

                    if (structures.size() == 1) {
                        SecondaryStructure secondaryStructure = new SecondaryStructure();
                        createPaths(secondaryStructure, dataFile.getName().substring(dataFile.getName().lastIndexOf('.') + 1), dataFile.getName().substring(dataFile.getName().lastIndexOf('.') + 1));
                        secondaryStructure.originalFile = dataFile;
                        secondaryStructure.title = dataFile.getName().replaceAll("\\.[^\\.]+$", "");
                        secondaryStructure.dataType = dataType;
                        secondaryStructure.persistObject(projectModel.getProjectPathString(), structures.get(0));
                        secondaryStructure.fileSize = new FileSize(dataFile.length());
                        secondaryStructure.length = structures.get(0).pairedSites.length;
                        dataSource = secondaryStructure;
                    } else {
                        StructureList list = new StructureList(dataFile.getName());
                        list = new StructureList(dataFile.getName());
                        createPaths(list, dataFile.getName().substring(dataFile.getName().lastIndexOf('.') + 1), dataFile.getName().substring(dataFile.getName().lastIndexOf('.') + 1));
                        list.originalFile = dataFile;
                        list.title = dataFile.getName().replaceAll("\\.[^\\.]+$", "");
                        list.dataType = dataType;
                        list.persistObject(projectModel.getProjectPathString(), structures);
                        list.fileSize = new FileSize(dataFile.length());
                        list.structures = new ArrayList<>();
                        for (SecondaryStructureData s : structures) {
                            SecondaryStructure secondaryStructure = new SecondaryStructure();
                            createPaths(secondaryStructure, dataFile.getName().substring(dataFile.getName().lastIndexOf('.') + 1), dataFile.getName().substring(dataFile.getName().lastIndexOf('.') + 1));
                            secondaryStructure.originalFile = dataFile;
                            secondaryStructure.title = dataFile.getName().replaceAll("\\.[^\\.]+$", "");
                            secondaryStructure.dataType = dataType;
                            secondaryStructure.persistObject(projectModel.getProjectPathString(), s);
                            secondaryStructure.fileSize = new FileSize(dataFile.length());
                            secondaryStructure.length = s.pairedSites.length;
                            list.structures.add(secondaryStructure);
                        }
                        dataSource = list;
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
                    if (dataType.fileFormat.equals(DataType.FileFormat.EXCEL)) {
                        dataSource = ExcelIO.getTabularRepresentation(dataFile);
                        createPaths(dataSource, dataFile.getName().substring(dataFile.getName().lastIndexOf('.') + 1), "csv");
                        dataSource.originalFile = dataFile;
                        dataSource.title = dataFile.getName().replaceAll("\\.[^\\.]+$", "");
                        dataSource.dataType = dataType;
                        dataSource.persistObject(projectModel.getProjectPathString(), dataSource);
                        dataSource.fileSize = new FileSize(dataFile.length());
                        ExcelIO.saveAsCSV(dataFile, Paths.get(dataSource.getImportedDataSourcePath(projectModel.getProjectPathString())).toFile());
                    } else if (dataType.fileFormat.equals(DataType.FileFormat.CSV)) {
                        dataSource = CsvReader.getTabularRepresentation(dataFile);
                        createPaths(dataSource, dataFile.getName().substring(dataFile.getName().lastIndexOf('.') + 1), "csv");
                        dataSource.originalFile = dataFile;
                        dataSource.title = dataFile.getName().replaceAll("\\.[^\\.]+$", "");
                        dataSource.dataType = dataType;
                        dataSource.persistObject(projectModel.getProjectPathString(), dataSource);
                        dataSource.fileSize = new FileSize(dataFile.length());
                        Files.copy(Paths.get(dataFile.getAbsolutePath()), Paths.get(dataSource.getImportedDataSourcePath(projectModel.getProjectPathString())));
                    }
                } catch (IOException ex) {
                    Logger.getLogger(ProjectController.class.getName()).log(Level.SEVERE, null, ex);
                } catch (Exception ex) {
                    Logger.getLogger(ProjectController.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
            case ANNOTATION_DATA:
                try {
                    dataSource = new Annotations();
                    createPaths(dataSource, dataFile.getName().substring(dataFile.getName().lastIndexOf('.') + 1), "gb");
                    dataSource.originalFile = dataFile;
                    dataSource.title = dataFile.getName().replaceAll("\\.[^\\.]+$", "");
                    dataSource.dataType = dataType;
                    //dataSource.persistObject(dataSource);
                    dataSource.fileSize = new FileSize(dataFile.length());
                    Files.copy(Paths.get(dataFile.getAbsolutePath()), Paths.get(dataSource.getImportedDataSourcePath(projectModel.getProjectPathString())));
                } catch (IOException ex) {
                    Logger.getLogger(ProjectController.class.getName()).log(Level.SEVERE, null, ex);
                } catch (Exception ex) {
                    Logger.getLogger(ProjectController.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
            case MATRIX:
                dataSource = new Matrix();
                createPaths(dataSource, dataFile.getName().substring(dataFile.getName().lastIndexOf('.') + 1), "matrix");
                dataSource.title = dataFile.getName().replaceAll("\\.[^\\.]+$", "");
                dataSource.title = dataFile.getName().replaceAll("\\.[^\\.]+$", "");
                dataSource.dataType = dataType;
                dataSource.fileSize = new FileSize(dataFile.length());

                if (dataType.fileFormat == DataType.FileFormat.DENSE_MATRIX) {
                    try {
                        PersistentSparseMatrix.createMatrixFromDenseMatrixFile(dataFile, "[\\s,;]+", new File(dataSource.getImportedDataSourcePath(projectModel.getProjectPathString())));
                    } catch (IOException ex) {
                        Logger.getLogger(ProjectController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else if (dataType.fileFormat == DataType.FileFormat.COORDINATE_LIST_MATRIX) {
                    try {
                        PersistentSparseMatrix.createMatrixFromCoordinateListMatrixFile(dataFile, "[\\s,;]+", new File(dataSource.getImportedDataSourcePath(projectModel.getProjectPathString())));
                    } catch (IOException ex) {
                        Logger.getLogger(ProjectController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                break;
            case ALIGNMENT:
                Alignment al = new Alignment();
                createPaths(al, dataFile.getName().substring(dataFile.getName().lastIndexOf('.') + 1), "fas");
                al.title = dataFile.getName().replaceAll("\\.[^\\.]+$", "");
                al.dataType = dataType;
                al.fileSize = new FileSize(dataFile.length());

                try {
                    ReadseqTools.saveAsFASTA(dataFile, Paths.get(al.getImportedDataSourcePath(projectModel.getProjectPathString())).toFile());
                    IO.resaveAsNormalisedFasta(Paths.get(al.getImportedDataSourcePath(projectModel.getProjectPathString())).toFile(), Paths.get(al.getNormalisedDataSourcePath(projectModel.getProjectPathString())).toFile());
                    AlignmentMetadata metadata = IO.getAlignmentMetadata(Paths.get(al.getImportedDataSourcePath(projectModel.getProjectPathString())).toFile());
                    al.numSequences = metadata.numSequences;
                    al.length = metadata.maxSequenceLength;

                    ArrayList<String> sequences = new ArrayList<>();
                    ArrayList<String> sequenceNames = new ArrayList<>();
                    IO.loadFastaSequences(dataFile, sequences, sequenceNames, 1000);
                    al.alignmentType = AlignmentUtils.guessAlignmentType(sequences);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                dataSource = al;
                break;
        }

        // place a copy of the original data source in the workspace
        try {
            Files.copy(sourcePath, Paths.get(dataSource.getOriginalDataSourcePath(projectModel.getProjectPathString())));
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        projectModel.dataSources.addElement(dataSource);
        return dataSource;
    }

    public void importDataSourceFromOutputFile(ApplicationOutput outputFile) {
        if (outputFile.dataSource instanceof SecondaryStructure) {
            if (outputFile.object != null) {
                SecondaryStructure structure = (SecondaryStructure) outputFile.dataSource;
                structure.setImportId(getNextImportId());
                structure.originalDataSourcePath = generatePath(outputFile.dataSource.getImportId(), "orig.dbn").toString();
                structure.importedDataSourcePath = generatePath(outputFile.dataSource.getImportId(), "dbn").toString();
                structure.title = outputFile.dataSource.title;
                structure.persistObject(projectModel.getProjectPathString(), outputFile.object);

                //dataSource.dataType = DataType.Primary.SECONDARY_STRUCTURE;
                structure.fileSize = new FileSize(Paths.get(structure.getImportedDataSourcePath(projectModel.getProjectPathString())).toFile().length());
                projectModel.dataSources.addElement(structure);
            }
        } else if (outputFile.dataSource instanceof Matrix) {
            if (outputFile.file != null) {
                Matrix matrix = (Matrix) outputFile.dataSource;
                matrix.setImportId(getNextImportId());
                matrix.originalDataSourcePath = generatePath(outputFile.dataSource.getImportId(), "orig.matrix").toString();
                matrix.importedDataSourcePath = generatePath(outputFile.dataSource.getImportId(), "matrix").toString();
                matrix.title = outputFile.dataSource.title;
                System.out.println("outputFile.fileFormat" + outputFile.fileFormat);
                try {
                    if (outputFile.fileFormat == FileFormat.COORDINATE_LIST_MATRIX) {
                        PersistentSparseMatrix.createMatrixFromCoordinateListMatrixFile(outputFile.file, "[\\s,;]+", new File(matrix.getImportedDataSourcePath(projectModel.getProjectPathString())));
                    } else if (outputFile.fileFormat == FileFormat.DENSE_MATRIX) {
                        PersistentSparseMatrix.createMatrixFromDenseMatrixFile(outputFile.file, "[\\s,;]+", new File(matrix.getImportedDataSourcePath(projectModel.getProjectPathString())));
                    }
                } catch (IOException ex) {
                    Logger.getLogger(ProjectController.class.getName()).log(Level.SEVERE, null, ex);
                }

                matrix.fileSize = new FileSize(Paths.get(matrix.getImportedDataSourcePath(projectModel.getProjectPathString())).toFile().length());
                projectModel.dataSources.addElement(matrix);
            }
        } else if (outputFile.dataSource instanceof Alignment) {
            Alignment alignment = (Alignment) outputFile.dataSource;
            alignment.setImportId(getNextImportId());
            alignment.originalDataSourcePath = generatePath(outputFile.dataSource.getImportId(), "orig.fas").toString();
            alignment.importedDataSourcePath = generatePath(outputFile.dataSource.getImportId(), "fas").toString();
            alignment.normalisedDataSourcePath = generatePath(outputFile.dataSource.getImportId(), "norm.fas").toString();
            alignment.title = outputFile.dataSource.title;

            ArrayList<String> sequences = new ArrayList<>();
            ArrayList<String> sequenceNames = new ArrayList<>();
            IO.loadFastaSequences(alignment.originalFile, sequences, sequenceNames);
            IO.saveToFASTAfile(sequences, sequenceNames, new File(alignment.getOriginalDataSourcePath(projectModel.getProjectPathString())));
            IO.saveToFASTAfile(sequences, sequenceNames, new File(alignment.getImportedDataSourcePath(projectModel.getProjectPathString())));
            IO.resaveAsNormalisedFasta(Paths.get(alignment.getImportedDataSourcePath(projectModel.getProjectPathString())).toFile(), Paths.get(alignment.getNormalisedDataSourcePath(projectModel.getProjectPathString())).toFile());
            alignment.fileSize = new FileSize(Paths.get(alignment.getImportedDataSourcePath(projectModel.getProjectPathString())).toFile().length());

            try {
                AlignmentMetadata metadata = IO.getAlignmentMetadata(Paths.get(alignment.getImportedDataSourcePath(projectModel.getProjectPathString())).toFile());
                alignment.numSequences = metadata.numSequences;
                alignment.length = metadata.maxSequenceLength;
            } catch (IOException ex) {
                Logger.getLogger(ProjectController.class.getName()).log(Level.SEVERE, null, ex);
            }

            alignment.alignmentType = AlignmentUtils.guessAlignmentType(sequences);

            projectModel.dataSources.addElement(alignment);
        } else if (outputFile.dataSource instanceof Tabular) {
            try {
                Tabular tabular = CsvReader.getTabularRepresentation(outputFile.dataSource.originalFile);
                tabular.setImportId(getNextImportId());
                tabular.originalDataSourcePath = generatePath(outputFile.dataSource.getImportId(), "orig.csv").toString();
                tabular.importedDataSourcePath = generatePath(outputFile.dataSource.getImportId(), "csv").toString();
                tabular.title = outputFile.dataSource.title;

                IO.copyFile(outputFile.dataSource.originalFile, new File(tabular.getOriginalDataSourcePath(projectModel.getProjectPathString())));
                IO.copyFile(outputFile.dataSource.originalFile, new File(tabular.getImportedDataSourcePath(projectModel.getProjectPathString())));
                tabular.fileSize = new FileSize(Paths.get(tabular.getImportedDataSourcePath(projectModel.getProjectPathString())).toFile().length());

                projectModel.dataSources.addElement(tabular);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else if (outputFile.dataSource instanceof Tree) {
            Tree tree = new Tree();
            tree.setImportId(getNextImportId());
            tree.originalDataSourcePath = generatePath(outputFile.dataSource.getImportId(), "orig.nwk").toString();
            tree.importedDataSourcePath = generatePath(outputFile.dataSource.getImportId(), "nwk").toString();
             tree.normalisedDataSourcePath = generatePath(outputFile.dataSource.getImportId(), "norm." + "nwk").toString();
            tree.title = outputFile.dataSource.title;
            tree.parentSource = outputFile.dataSource.parentSource;

            IO.copyFile(outputFile.dataSource.originalFile, new File(tree.getOriginalDataSourcePath(projectModel.getProjectPathString())));
            IO.copyFile(outputFile.dataSource.originalFile, new File(tree.getImportedDataSourcePath(projectModel.getProjectPathString())));
            IO.copyFile(outputFile.dataSource.originalFile, new File(tree.getNormalisedDataSourcePath(projectModel.getProjectPathString())));
            tree.fileSize = new FileSize(Paths.get(tree.getImportedDataSourcePath(projectModel.getProjectPathString())).toFile().length());

            projectModel.dataSources.addElement(tree);
        }

    }

    public void createPaths(DataSource dataSource, String origExtension, String newExtension) {
        dataSource.setImportId(getNextImportId());
        Path p = generatePath(dataSource.getImportId(), newExtension);
        while (Files.exists(projectModel.getProjectPath().resolve(p))) {
            dataSource.setImportId(getNextImportId());
            p = generatePath(dataSource.getImportId(), newExtension);
        }
        dataSource.originalDataSourcePath = generatePath(dataSource.getImportId(), "orig." + origExtension).toString();
        dataSource.importedDataSourcePath = generatePath(dataSource.getImportId(), newExtension).toString();
        dataSource.normalisedDataSourcePath = generatePath(dataSource.getImportId(), "norm." + newExtension).toString();
    }

    public Path generatePath(long id, String extension) {
        Path p = Paths.get(id + "." + extension);

        return p;
    }

    public Pair<DataType, DataSource> autoAddDataSourceWithAmbiguityResolution(final File dataFile) {
        DataType dataType = null;

        ArrayList<DataType> possibleDataTypes = null;
        if (dataFile.isFile()) {
            possibleDataTypes = FileImport.getPossibleDataTypes(dataFile);
        }
        if (possibleDataTypes != null && possibleDataTypes.size() > 0) {
            if (possibleDataTypes.size() == 1) {
                dataType = possibleDataTypes.get(0);
                //importDataSourceFromFile(dataFile, possibleDataTypes.get(0));
                //return new Pair(possibleDataTypes.get(0), dataSource);
            } else {
                ResolveImportAmbiguityDialog resolveDialog = new ResolveImportAmbiguityDialog(MainFrame.self, true, dataFile, possibleDataTypes);
                resolveDialog.setIconImage(new ImageIcon(ClassLoader.getSystemResource("resources/icons/icon-32x32.png")).getImage());
                GraphicsUtils.centerWindowOnScreen(resolveDialog);
                resolveDialog.setVisible(true);

                dataType = resolveDialog.getSelectedDataType();
            }

            final DataType dataType2 = dataType;
            DataSource dataSource = importDataSourceFromFile(dataFile, dataType2);
            return new Pair<>(dataType2, dataSource);
        }
        return null;
    }

    public Pair<DataType, DataSource> autoAddDataSource(File dataFile) {
        ArrayList<DataType> possibleDataTypes = null;
        if (dataFile.isFile()) {
            possibleDataTypes = FileImport.getPossibleDataTypes(dataFile);
        }
        if (possibleDataTypes != null && possibleDataTypes.size() > 0) {
            DataSource dataSource = importDataSourceFromFile(dataFile, possibleDataTypes.get(0));
            return new Pair(possibleDataTypes.get(0), dataSource);
        }

        return null;
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
        this.projectModel.dataSources.addSafeListListener(this);

        for (int i = 0; i < projectViews.size(); i++) {
            projectViews.get(i).projectModelChanged(projectModel);
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
    public void intervalAdded(SafeListEvent e) {
        System.out.println("intervalAdded: " + e);
        if (e.getSource().equals(this.projectModel.dataSources)) {
            for (int i = 0; i < projectViews.size(); i++) {
                projectViews.get(i).dataSourcesIntervalAdded(e);
            }
        }
    }

    @Override
    public void intervalRemoved(SafeListEvent e) {
        System.out.println("intervalRemoved: " + e);
        if (e.getSource().equals(this.projectModel.dataSources)) {
            for (int i = 0; i < projectViews.size(); i++) {
                projectViews.get(i).dataSourcesIntervalRemoved(e);
            }
        }
    }

    @Override
    public void contentsChanged(SafeListEvent e) {
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
