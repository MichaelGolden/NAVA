/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.data.types;

import java.nio.file.Paths;
import java.util.ArrayList;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import nava.structurevis.data.PersistentSparseMatrix;
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
    public String getTypeName() {
        return "Matrix";
    }

    @Override
    public ArrayList<DataSource> getChildren() {
        return new ArrayList();
    }

    @Override
    public PersistentSparseMatrix getObject() {
        try {
            return new PersistentSparseMatrix(Paths.get(importedDataSourcePath).toFile());
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public PersistentSparseMatrix getObject(DataSourceCache cache) {
        PersistentSparseMatrix cachedObject = (PersistentSparseMatrix) cache.getObject(this);
        if (cachedObject == null) {
            try {
                return (PersistentSparseMatrix) cache.cache(this, new PersistentSparseMatrix(Paths.get(importedDataSourcePath).toFile()));
            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }

        }
        return cachedObject;
    }

    @Override
    public void persistObject(Object object) {
    }
}
