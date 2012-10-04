/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.data.types;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import nava.data.io.FileImport;
import nava.data.io.FileImport.ParserException;
import nava.utils.RNAFoldingTools;

/**
 *
 * @author Michael
 */
public class SecondaryStructure extends DataSource {
    
    //public int[] pairedSites;
   // public String sequence;
    
    @Override
    public Icon getIcon() {
        return new ImageIcon(ClassLoader.getSystemResource("resources/icons/structure-16x16.png"));        
    }
    
    @Override
    public ArrayList<DataSource> getChildren() {
        return new ArrayList();
    }    

    @Override
    public SecondaryStructureData getObject() {
        try {
            return FileImport.readDotBracketOnlyFile(originalFile).get(0);
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
    public void persistObject(Object object) {
        if(object instanceof SecondaryStructureData)
        {
            SecondaryStructureData structure = (SecondaryStructureData)object;
            RNAFoldingTools.saveDotBracketFile(this.importedDataSourcePath.toFile(), structure.pairedSites, structure.title, structure.sequence);
        }
    }


}
