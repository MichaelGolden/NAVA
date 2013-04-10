/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.data.types;

import java.io.File;
import java.io.Serializable;
import java.nio.file.Paths;
import java.util.ArrayList;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import nava.data.io.IO;
import nava.utils.AlignmentType;

/**
 *
 * @author Michael
 */
public class Alignment extends DataSource {
    private static final long serialVersionUID = 2751887600835071870L;

    public AlignmentType alignmentType = AlignmentType.PROTEIN_ALIGNMENT;
    public boolean aligned = false;
    public int numSequences = 0;
    public int length = 0;

    @Override
    public Icon getIcon() {
        switch (alignmentType) {
            case NUCLEOTIDE_ALIGNMENT:
                return new ImageIcon(ClassLoader.getSystemResource("resources/icons/nucleotide-alignment-16x16.png"));
            case CODON_ALIGNMENT:
                return new ImageIcon(ClassLoader.getSystemResource("resources/icons/coding-alignment-16x16.png"));
            case PROTEIN_ALIGNMENT:
                return new ImageIcon(ClassLoader.getSystemResource("resources/icons/protein-alignment-16x16.png"));
            default:
                return null;
        }
    }
    
    
    @Override
    public String getTypeName() {
        return "Alignment";
    }

    @Override
    public ArrayList<DataSource> getChildren() {
        return new ArrayList();
    }

    @Override
    public AlignmentData getObject(String projectDir) {
        AlignmentData alignmentData = new AlignmentData();
        IO.loadFastaSequences(Paths.get(getImportedDataSourcePath(projectDir)).toFile(), alignmentData.sequences, alignmentData.sequenceNames);
        return alignmentData;
    }

    @Override
    public AlignmentData getObject(String projectDir, DataSourceCache cache) {
       AlignmentData cachedObject = (AlignmentData) cache.getObject(this);
       if(cachedObject == null)
       {
           return (AlignmentData) cache.cache(this, getObject(projectDir));
       }
       return cachedObject;
    }

    /*public String getSequence(int i) {
        return getObject().sequences.get(i);
    }*/

    @Override
    public void persistObject(String projectDir, Object object) {
        if (object instanceof AlignmentData) {
            AlignmentData alignmentData = (AlignmentData) object;
            IO.saveToFASTAfile(alignmentData.sequences, alignmentData.sequenceNames, Paths.get(getImportedDataSourcePath(projectDir)).toFile());
        }
    }
}
