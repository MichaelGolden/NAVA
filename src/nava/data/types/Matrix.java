/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.data.types;

import java.nio.file.Paths;
import java.util.ArrayList;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import nava.utils.RNAFoldingTools;

/**
 *
 * @author Michael
 */
public class Matrix extends DataSource {

    @Override
    public Icon getIcon() {
        return new ImageIcon(ClassLoader.getSystemResource("resources/icons/matrix-16x16.png"));  
    }

    @Override
    public ArrayList<DataSource> getChildren() {
        return new ArrayList();
    }

    @Override
    public Object getObject() {
        return new DenseMatrixData( RNAFoldingTools.loadMatrix(Paths.get(importedDataSourcePath).toFile()));
    }

    @Override
    public void persistObject(Object object) {
        if(object instanceof DenseMatrixData)
        {
            DenseMatrixData denseMatrixData = (DenseMatrixData)object;
            RNAFoldingTools.writeMatrix(denseMatrixData.matrix, Paths.get(importedDataSourcePath).toFile());
        }
    }
    
}
