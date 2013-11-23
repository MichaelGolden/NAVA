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

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class FigTreeApplication extends Application {

    public static String FIGTREE_EXECUTABLE = "bin/figtree.jar";
    Tree inputDataSource;
    
    @Override
    protected void start() {
         File treeFile = new File(inputDataSource.getNormalisedDataSourcePath(ProjectModel.path));
 
        String cmd = "java -jar \""+new File(FIGTREE_EXECUTABLE).getAbsolutePath() + "\" \""+treeFile.getAbsolutePath()+"\"";
        try {
            Runtime.getRuntime().exec(cmd);
        } catch (IOException ex) {
            Logger.getLogger(FigTreeApplication.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public boolean canProcessDataSources(List<DataSource> dataSources) {
         if (dataSources.size() == 1) {
            if (dataSources.get(0) instanceof Tree) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void setDataSource(DataSource dataSource) {
        this.inputDataSource = (Tree) dataSource;
    }

    @Override
    public List<ApplicationOutput> getOutputFiles() {
        return new ArrayList<>();
    }

    @Override
    protected void pause() {
        
    }

    @Override
    protected void resume() {
        
    }

    @Override
    protected void cancel() {
        
    }

    @Override
    public String getName() {
        return "FigTree";
    }

    @Override
    public String getDescription() {
        return "Graphical viewer and editor for phylogenetic trees";
    }
    
}
