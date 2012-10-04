/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.data.io;

import java.io.File;
import nava.data.types.SecondaryStructure;
import nava.data.types.SecondaryStructureData;
import nava.utils.RNAFoldingTools;

/**
 *
 * @author Michael
 */
public class StructureExport {
    
    public static void exportToCtFile(SecondaryStructureData structure, File ctFile)
    {
        RNAFoldingTools.saveCtFile(ctFile, structure.pairedSites, structure.title, structure.sequence);
    }
    
    public static void exportToDotBracketFile(SecondaryStructureData structure, File dotBracketFile)
    {
        RNAFoldingTools.saveDotBracketFile(dotBracketFile, structure.pairedSites, structure.title, structure.sequence);
    }
}
