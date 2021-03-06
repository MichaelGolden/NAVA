/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.data.types;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 *
 * @author Michael
 */
public class Annotations extends DataSource {

    @Override
    public Icon getIcon() {
        return new ImageIcon(ClassLoader.getSystemResource("resources/icons/annotations-16x16.png"));
    }

    @Override
    public String getTypeName() {
        return "Annotations";
    }

    @Override
    public ArrayList<DataSource> getChildren() {
        return new ArrayList();
    }

    @Override
    public Object getObject(String projectDir) {
        //throw new UnsupportedOperationException("Not supported yet.");
        return null;
    }

    @Override
    public Object getObject(String projectDir, DataSourceCache cache) {
        //throw new UnsupportedOperationException("Not supported yet.");
        return null;
    }

    @Override
    public void persistObject(String projectDir, Object object) {
        try {
            Files.copy(Paths.get(originalFile.getAbsolutePath()), Paths.get(getImportedDataSourcePath(projectDir)));
        } catch (IOException ex) {
            Logger.getLogger(Annotations.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
