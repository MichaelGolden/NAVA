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

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class MAFFTApplication extends Application {

    Process process = null;
    public static String MAFFT_EXECUTABLE = "bin/mafft-6.952-win64/mafft-win/mafft.bat";
    Alignment inputDataSource = null;
    ArrayList<ApplicationOutput> outputFiles = new ArrayList<>();

    @Override
    protected void start() {
        File tempDir = createTemporaryDirectory();
        System.out.println(tempDir);

        File inFastaFile = new File(inputDataSource.importedDataSourcePath);
        File outFastaFile = new File(tempDir.getAbsolutePath() + File.separator + "temp.fas");

        try {
            String cmd = new File(MAFFT_EXECUTABLE).getAbsolutePath() + " " + inFastaFile.getAbsolutePath() + " > " + outFastaFile.getAbsolutePath();
            process = Runtime.getRuntime().exec(cmd, null, tempDir);

            startConsoleInputBuffer(process);
            startConsoleErrorBuffer(process);

            int exitCode = process.waitFor();
            if (exitCode == 0) {                
                ApplicationOutput outputFile1 = new ApplicationOutput();

                outputFile1.file = null;
                Alignment alignment = new Alignment();
                alignment.title = inputDataSource.title + "_mafft_aligned";
                alignment.originalFile = outFastaFile;      
                outputFile1.dataSource = alignment;
                System.out.println("Finalising");
                outputFiles.add(outputFile1);
            } else {
            }

        } catch (InterruptedException ex) {
            Logger.getLogger(MAFFTApplication.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MAFFTApplication.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public boolean canProcessDataSource(DataSource dataSource) {
        if (dataSource instanceof Alignment) {
            return true;
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
        return "MAFFT alignment";
    }

    @Override
    public String getDescription() {
        return "";
    }
}
