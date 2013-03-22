/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.data.types;

import java.util.ArrayList;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import nava.data.io.ExcelIO;

/**
 *
 * @author Michael
 */
public class Tabular extends DataSource {

    public int numSheets = 0;
    public ArrayList<TabularField> fields = new ArrayList<TabularField>();
    
    @Override
    public Icon getIcon() {
        return new ImageIcon(ClassLoader.getSystemResource("resources/icons/tabular-16x16.png"));
    }
    
    @Override
    public String getTypeName() {
        return "Tabular";
    }

    @Override
    public ArrayList<TabularField> getChildren() {
        return fields;
    }

    @Override
    public Object getObject(String projectDir) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
        
    @Override
    public Object getObject(String projectDir, DataSourceCache cache) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void persistObject(String projectDir, Object object) {
        //ExcelIO.saveAsCSV(this.originalDataSourcePath, originalFile);
        // TODO
    }

    
}
