/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.tasks.applications;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import nava.data.io.FileImport;
import nava.data.io.FileImport.ParserException;
import nava.data.io.IO;
import nava.data.types.*;
import nava.ui.ProjectModel;
import nava.utils.RNAFoldingTools;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class PPfoldApplication extends Application {

    Process process = null;
    public static String PPFOLD_EXECUTABLE = "bin/PPfold-v3-0.jar.";
    Alignment inputDataSource = null;
    ArrayList<ApplicationOutput> outputFiles = new ArrayList<>();
    
    PPfoldPanel ppfoldPanel;

    public PPfoldApplication() {
        this.slotUsage = Runtime.getRuntime().availableProcessors();
       /// System.out.println("VALUE " + this);
        ppfoldPanel =  new PPfoldPanel(this, inputDataSource);
        setApplicationPanel(ppfoldPanel);
    }
    public String arguments = "";

    @Override
    protected void start() {
        File tempDir = createTemporaryDirectory();

        File inFastaFile = new File(tempDir.getAbsoluteFile().getAbsolutePath() + File.separator + "temp.fas");
        IO.copyFile(new File(inputDataSource.getNormalisedDataSourcePath(ProjectModel.path)), inFastaFile);

        try {
            String cmd = "java -jar " + new File(PPFOLD_EXECUTABLE).getAbsolutePath() + " \"" + inFastaFile.getAbsolutePath() + "\" "+arguments+"  --exports";
            System.out.println(cmd);
            process = Runtime.getRuntime().exec(cmd, null, tempDir);

            startConsoleInputBuffer(process);
            startConsoleErrorBuffer(process);

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                ApplicationOutput outputFile1 = new ApplicationOutput();
                outputFile1.file = null;
                SecondaryStructure structure = new SecondaryStructure();
                structure.title = inputDataSource.title+"_ppfold";
                structure.parentSource = inputDataSource;
                outputFile1.dataSource = structure;
                ArrayList<SecondaryStructureData> structures = FileImport.readConnectFile(new File(tempDir.getAbsoluteFile().getAbsolutePath() + File.separator + "temp.ct"));
                if (structures.size() > 0) {
                    outputFile1.object = structures.get(0);
                    outputFiles.add(outputFile1);
                }

                File matrixOutputFile = new File(tempDir.getAbsoluteFile().getAbsolutePath() + File.separator + "temp.bp");
                if (matrixOutputFile.exists()) {
                    ApplicationOutput outputFile2 = new ApplicationOutput();
                    outputFile2.fileFormat = DataType.FileFormat.DENSE_MATRIX;
                    outputFile2.file = matrixOutputFile;
                    Matrix matrix = new Matrix();
                    matrix.title = inputDataSource.title+"_ppfold";
                    matrix.parentSource = inputDataSource;
                    outputFile2.dataSource = matrix;
                    outputFiles.add(outputFile2);
                }
            } else {
            }

        } catch (InterruptedException ex) {
            Logger.getLogger(PPfoldApplication.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(PPfoldApplication.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(PPfoldApplication.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public boolean canProcessDataSources(List<DataSource> dataSources) {
        if (dataSources.size() == 1) {
            if (dataSources.get(0) instanceof Alignment) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void setDataSource(DataSource dataSource) {
        this.inputDataSource = (Alignment) dataSource;
        this.ppfoldPanel.setAlignment(inputDataSource);
    }

    @Override
    public List<ApplicationOutput> getOutputFiles() {
        return outputFiles;
    }

    @Override
    protected void pause() {
    }

    @Override
    protected void resume() {
    }

    @Override
    protected void cancel() {
        process.destroy();
    }

    @Override
    public String getName() {
        return "PPfold";
    }

    @Override
    public String getDescription() {
        return "Predicts a consensus secondary structure from a set of aligned sequences.";
    }
}
