/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.data.types;

import java.io.IOException;
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
public class TabularField extends DataSource {
    
    Tabular parent;
    String header;
    int sheet;
    int indexInSheet;
    int index;
    
    public TabularField(Tabular parent, String header, int sheet, int indexInSheet, int index)
    {
        this.parent = parent;
        this.header = header;
        this.title = header;
        this.sheet = sheet;
        this.indexInSheet = indexInSheet;
        this.index = index;
    }

    @Override
    public Icon getIcon() {
        return new ImageIcon(ClassLoader.getSystemResource("resources/icons/tabular-field-16x16.png"));
    }

    @Override
    public ArrayList<DataSource> getChildren() {
        return new ArrayList();
    }

    @Override
    public TabularFieldData getObject() {
        try {
            return TabularFieldData.getColumn(Paths.get(parent.importedDataSourcePath).toFile(), index);
        } catch (IOException ex) {
            Logger.getLogger(TabularField.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public void persistObject(Object object) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
