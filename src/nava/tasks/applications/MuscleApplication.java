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
import nava.data.types.Alignment;
import nava.data.types.DataSource;
import nava.data.types.SecondaryStructure;
import nava.data.types.SecondaryStructureData;
import nava.ui.ProjectModel;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class MuscleApplication extends Application {

    Process process = null;
    public static String MUSCLE_EXECUTABLE = "bin/muscle3.8.31_i86win32.exe";
    Alignment inputDataSource = null;
    ArrayList<ApplicationOutput> outputFiles = new ArrayList<>();
    
    public MuscleApplication()
    {
        setApplicationPanel(new MusclePanel());
    }
    
    public String extraParameters = "";

    @Override
    protected void start() {
        File tempDir = createTemporaryDirectory();

        File inFastaFile = new File(inputDataSource.getNormalisedDataSourcePath(ProjectModel.path));
        File outFastaFile = new File(tempDir.getAbsolutePath() + File.separator + "temp.fas");

        try {
            String cmd = new File(MUSCLE_EXECUTABLE).getAbsolutePath() + " -in " + inFastaFile.getAbsolutePath() + " -out " + outFastaFile.getAbsolutePath() + " "+extraParameters;
 
            process = Runtime.getRuntime().exec(cmd, null, tempDir);

            startConsoleInputBuffer(process);
            startConsoleErrorBuffer(process);

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                ApplicationOutput outputFile1 = new ApplicationOutput();

                outputFile1.file = null;
                Alignment alignment = new Alignment();
                alignment.title = inputDataSource.title + "_muscle_aligned";
                alignment.originalFile = outFastaFile;
                outputFile1.dataSource = alignment;
                outputFiles.add(outputFile1);
            } else {
            }

        } catch (InterruptedException ex) {
            Logger.getLogger(MuscleApplication.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MuscleApplication.class.getName()).log(Level.SEVERE, null, ex);
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
        return "MUSCLE alignment";
    }

    @Override
    public String getDescription() {
        return "";
    }
}
