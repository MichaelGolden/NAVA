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
    
    private static final long serialVersionUID = 8638624765882567070L;    
    
    public SafeListModel<DataSource> dataSources = new SafeListModel();
    
    public NavigatorTreeModel navigatorTreeModel;
    public long dataSourceCounter = 0;
    public long importCounter = 0;
    
    public static String path = "workspace/test_project/";

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
        ret.path = inFile.getParentFile().getAbsolutePath();
        in.close();
        return ret;        
    }
    
    public String getProjectPathString()
    {
        return path;
    }
    
    public Path getProjectPath()
    {
        return Paths.get(path);
        //new File("workspace/test_project/").mkdirs();
        //return Paths.get("workspace/test_project/");
    }
}
