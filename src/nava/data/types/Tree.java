/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.data.types;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import nava.data.io.IO;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class Tree extends DataSource {

    private static final long serialVersionUID = -5662223012719228604L;

    @Override
    public Icon getIcon() {
        return new ImageIcon(ClassLoader.getSystemResource("resources/icons/tree-16x16.png"));
    }

    @Override
    public ArrayList<DataSource> getChildren() {
        return new ArrayList<>();
    }

    @Override
    public TreeData getObject(String projectDir) {
        TreeData treeData = new TreeData();
        try {
            BufferedReader buffer = new BufferedReader(new FileReader(Paths.get(getImportedDataSourcePath(projectDir)).toFile()));
            treeData.newickString = buffer.readLine();
            buffer.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return treeData;
    }

    @Override
    public TreeData getObject(String projectDir, DataSourceCache cache) {
        TreeData cachedObject = (TreeData) cache.getObject(this);
        if (cachedObject == null) {
            return (TreeData) cache.cache(this, getObject(projectDir));
        }
        return cachedObject;
    }

    @Override
    public void persistObject(String projectDir, Object object) {
        if (object instanceof TreeData) {
            TreeData treeData = (TreeData) object;
            try {
                BufferedWriter buffer = new BufferedWriter(new FileWriter(Paths.get(getImportedDataSourcePath(projectDir)).toFile()));
                buffer.write(treeData.newickString);
                buffer.newLine();
                buffer.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public String getTypeName() {
        return "Phylogenetic tree";
    }
}
