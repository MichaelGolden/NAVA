/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.data.types;

import java.util.ArrayList;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 *
 * @author Michael
 */
public class Sequence extends DataSource {

    Alignment parentAlignment = null;
    String name;
    int length = 0;

    @Override
    public Icon getIcon() {
        if (parentAlignment != null) {
            switch (parentAlignment.type) {
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
        return null;
    }
    
    @Override
    public String getTypeName() {
        return "Sequence";
    }

    @Override
    public ArrayList<DataSource> getChildren() {
        return new ArrayList();
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
            throw new UnsupportedOperationException("Not supported yet.");
    }

}
