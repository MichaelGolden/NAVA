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
import nava.data.types.Tree;
import nava.ui.ProjectModel;
import nava.utils.AlignmentType;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class FastTreeApplication extends Application {

    Process process = null;
    public static String FAST_TREE_EXECUTABLE = "bin/FastTree.exe";
    Alignment inputDataSource = null;
    ArrayList<ApplicationOutput> outputFiles = new ArrayList<>();
    
    public FastTreeApplication()
    {
        
    }
    
    public String arguments = "";

    @Override
    protected void start() {
        File tempDir = createTemporaryDirectory();

        File inFastaFile = new File(inputDataSource.getNormalisedDataSourcePath(ProjectModel.path));
        File outNewickFile = new File(tempDir.getAbsolutePath() + File.separator + "temp.nwk");
        
        String nucleotideAlignmentParam = "";
        if(inputDataSource.alignmentType == AlignmentType.NUCLEOTIDE_ALIGNMENT)
        {
            nucleotideAlignmentParam = " -nt -gtr";
        }
        arguments += nucleotideAlignmentParam;

        
        try {
            String cmd = "cmd /c "+new File(FAST_TREE_EXECUTABLE).getAbsolutePath() + " " + arguments + " " + inFastaFile.getAbsolutePath() + " > " + outNewickFile.getAbsolutePath();
            System.out.println("cmd "+cmd);
            process = Runtime.getRuntime().exec(cmd, null, tempDir);

            startConsoleInputBuffer(process);
            startConsoleErrorBuffer(process);

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                ApplicationOutput outputFile1 = new ApplicationOutput();

                outputFile1.file = outNewickFile;
                Tree tree = new Tree();
                tree.title = inputDataSource.title + "_fast_tree";
                tree.originalFile = outNewickFile;
                tree.parentSource = inputDataSource;
                outputFile1.dataSource = tree;
                outputFiles.add(outputFile1);
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(FastTreeApplication.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FastTreeApplication.class.getName()).log(Level.SEVERE, null, ex);
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
        return "FastTree";
    }

    @Override
    public String getDescription() {
        return "Exceptionally fast and accurate approach for inferring a phylogenetic tree from a set of aligned sequences.";
    }
}
