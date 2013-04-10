/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.tasks.applications;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import nava.data.io.IO;
import nava.data.types.Alignment;
import nava.data.types.DataSource;
import nava.data.types.SecondaryStructure;
import nava.data.types.SecondaryStructureData;
import nava.ui.ProjectModel;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class MAFFTApplication extends Application {

    Process process = null;
    public static String MAFFT_EXECUTABLE = "bin/mafft-6.952-win64/mafft-win/mafft.bat";
    Alignment inputDataSource = null;
    ArrayList<ApplicationOutput> outputFiles = new ArrayList<>();
    
    public MAFFTApplication()
    {
        setApplicationPanel(new MAFFTPanel());
    }

    String arguments = "";
    public ArrayList<String> align(ArrayList<String> inSequences) throws Exception {
        File tempDir = createTemporaryDirectory();

        File inFastaFile = new File(tempDir.getAbsolutePath() + File.separator + "in.fas");
        File outFastaFile = new File(tempDir.getAbsolutePath() + File.separator + "out.fas");
        BufferedWriter writer = new BufferedWriter(new FileWriter(inFastaFile));
        for (int i = 0; i < inSequences.size(); i++) {
            writer.write(">a" + i + "\n");
            writer.write(inSequences.get(i) + "\n");
        }
        writer.close();

        //String cmd = new File(MAFFT_EXECUTABLE).getAbsolutePath() + " --auto " + inFastaFile.getAbsolutePath() + " > " + outFastaFile.getAbsolutePath();
        String cmd = new File(MAFFT_EXECUTABLE).getAbsolutePath() + " "+arguments +" " + inFastaFile.getAbsolutePath() + " > " + outFastaFile.getAbsolutePath();
        process = Runtime.getRuntime().exec(cmd, null, tempDir);

        Application.nullOutput(process.getInputStream());
        try {

            BufferedReader buffer = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String textline = null;
            while ((textline = buffer.readLine()) != null) {
                
            }
            buffer.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        ArrayList<String> sequences = new ArrayList<>();
        ArrayList<String> sequencesOrdered;
        ArrayList<String> sequenceNames = new ArrayList<>();

        int exitCode = process.waitFor();
        if (exitCode == 0) {
            IO.loadFastaSequences(outFastaFile, sequences, sequenceNames);
            sequencesOrdered  = new ArrayList<>();
            for(int i = 0 ; i < sequences.size() ; i++)
            {
                sequencesOrdered.add("");
            }
            for(int i = 0 ; i < sequences.size() ; i++)
            {
                int no = Integer.parseInt(sequenceNames.get(i).substring(1));
                sequencesOrdered.set(no, sequences.get(i));
            }
            
            return sequencesOrdered;
        } else {
            throw new Exception("");
        }
    }

    @Override
    protected void start() {
        File tempDir = createTemporaryDirectory();

        File inFastaFile = new File(inputDataSource.getImportedDataSourcePath(ProjectModel.path));
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
        return "MAFFT alignment";
    }

    @Override
    public String getDescription() {
        return "";
    }
}
