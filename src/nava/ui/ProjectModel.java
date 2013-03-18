/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.ui;

import nava.data.types.DataSource;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.swing.DefaultListModel;
import nava.ui.navigator.NavigatorTreeModel;
import nava.utils.SafeListModel;

/**
 *
 * @author Michael
 */
public class ProjectModel implements Serializable {
    
    public SafeListModel<DataSource> dataSources = new SafeListModel();
    
    public NavigatorTreeModel navigatorTreeModel;
    public long dataSourceCounter = 0;
    public long importCounter = 0;

    public void saveProject(File outFile) {
        try {
            ObjectOutput out = new ObjectOutputStream(new FileOutputStream(outFile));
            out.writeObject(this);
            out.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static ProjectModel loadProject(File inFile) throws IOException, ClassNotFoundException {
        ObjectInputStream in = new ObjectInputStream(new FileInputStream(inFile));
        ProjectModel ret = (ProjectModel) in.readObject();
        in.close();
        return ret;        
    }
    
    public Path getProjectPath()
    {
        new File("workspace/test_project/").mkdirs();
        return Paths.get("workspace/test_project/");
    }
}
