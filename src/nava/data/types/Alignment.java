/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.data.types;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import nava.data.io.IO;

/**
 *
 * @author Michael
 */
public class Alignment extends DataSource {
    
    public enum Type {NUCLEOTIDE, CODING, PROTEIN};
    
    public Type type = Type.NUCLEOTIDE;
    public boolean aligned = false;
    
    public int numSequences = 0;

    @Override
    public Icon getIcon() {
        switch(type)
        {
            case NUCLEOTIDE:
                return new ImageIcon(ClassLoader.getSystemResource("resources/icons/nucleotide-alignment-16x16.png"));
            case CODING:
                return new ImageIcon(ClassLoader.getSystemResource("resources/icons/coding-alignment-16x16.png"));
            case PROTEIN:
                return new ImageIcon(ClassLoader.getSystemResource("resources/icons/protein-alignment-16x16.png"));
            default:
                return null;
        }
    }

    @Override
    public ArrayList<DataSource> getChildren() {
        return new ArrayList();
    }

    @Override
    public AlignmentData getObject() {        
        AlignmentData alignmentData = new AlignmentData();
        IO.loadFastaSequences(Paths.get(importedDataSourcePath).toFile(), alignmentData.sequences, alignmentData.sequenceNames);
        return alignmentData;
    }    
    
    @Override
    public void persistObject(Object object) {
        if(object instanceof AlignmentData)
        {
            AlignmentData alignmentData = (AlignmentData) object;
            IO.saveToFASTAfile(alignmentData.sequences, alignmentData.sequenceNames, Paths.get(importedDataSourcePath).toFile());
        }
    }    
}
