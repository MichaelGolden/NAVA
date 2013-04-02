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
import nava.utils.RNAFoldingTools;

/**
 *
 * @author Michael
 */
public class SecondaryStructure extends DataSource {
    
    public int length;
    
    @Override
    public Icon getIcon() {
        return new ImageIcon(ClassLoader.getSystemResource("resources/icons/structure-16x16.png"));
    }
    
    @Override
    public String getTypeName() {
        return "Secondary structure";
    }

    @Override
    public ArrayList<DataSource> getChildren() {
        return new ArrayList();
    }

    @Override
    public SecondaryStructureData getObject(String projectDir) {
        try {
            return FileImport.readDotBracketFile(Paths.get(getImportedDataSourcePath(projectDir)).toFile()).get(0);
        } catch (IOException ex) {
            Logger.getLogger(SecondaryStructure.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserException ex) {
            Logger.getLogger(SecondaryStructure.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(SecondaryStructure.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public SecondaryStructureData getObject(String projectDir, DataSourceCache cache) {
        SecondaryStructureData cachedObject = (SecondaryStructureData) cache.getObject(this);
        if (cachedObject == null) {
            return (SecondaryStructureData) cache.cache(this, getObject(projectDir));
        }
        return cachedObject;
    }

    @Override
    public void persistObject(String projectDir, Object object) {
        if (object instanceof SecondaryStructureData) {
            SecondaryStructureData structure = (SecondaryStructureData) object;
            RNAFoldingTools.saveDotBracketFile(Paths.get(getImportedDataSourcePath(projectDir)).toFile(), structure.pairedSites, structure.title, structure.sequence);
        }
    }
}
