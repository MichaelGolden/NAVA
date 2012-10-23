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
import nava.data.io.FileImport;
import nava.data.io.FileImport.ParserException;

/**
 *
 * @author Michael
 */
public class StructureList extends DataSource {

    public enum Type {

        ALIGNED, UNALIGNED
    };
    public ArrayList<SecondaryStructure> structures = new ArrayList<>();

    public StructureList(String title) {
        this.title = title;
    }

    @Override
    public Icon getIcon() {
        return new ImageIcon(ClassLoader.getSystemResource("resources/icons/structure-multi-16x16.png"));
    }

    @Override
    public ArrayList<SecondaryStructure> getChildren() {
        return structures;
    }

    @Override
    public ArrayList<SecondaryStructureData> getObject() {
        try {
            return FileImport.readDotBracketFile(Paths.get(importedDataSourcePath).toFile());
        } catch (IOException ex) {
            Logger.getLogger(StructureList.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserException ex) {
            Logger.getLogger(StructureList.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(StructureList.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    @Override
    public ArrayList<SecondaryStructureData> getObject(DataSourceCache cache) {
        ArrayList<SecondaryStructureData> cachedObject = (ArrayList<SecondaryStructureData>) cache.getObject(this);
        if (cachedObject == null) {
            return getObject();
        }
        return cachedObject;
    }

    @Override
    public void persistObject(Object object) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
