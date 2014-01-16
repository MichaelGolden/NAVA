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
import nava.utils.Utils;

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
    boolean isMinAndMaxDetermined = false;
    double minimum;
    double maximum;

    public TabularField(Tabular parent, String header, int sheet, int indexInSheet, int index) {
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
    public String getTypeName() {
        return "Tabular column";
    }

    @Override
    public ArrayList<DataSource> getChildren() {
        return new ArrayList();
    }

    @Override
    public TabularFieldData getObject(String projectDir) {
        try {
            return TabularFieldData.getColumn(Paths.get(parent.getImportedDataSourcePath(projectDir)).toFile(), index);
        } catch (IOException ex) {
            Logger.getLogger(TabularField.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
        
    @Override
    public TabularFieldData getObject(String projectDir, DataSourceCache cache) {
       TabularFieldData cachedObject = (TabularFieldData) cache.getObject(this);
       if(cachedObject == null)
       {
           return (TabularFieldData) cache.cache(this, getObject(projectDir));
       }
       return cachedObject;
    }

    @Override
    public void persistObject(String projectDir, Object object) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private void determineMinAndMax(String projectDir) {
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        ArrayList<String> values = this.getObject(projectDir).values;
        for (int i = 0; i < values.size(); i++) {
            if (Utils.isNumeric(values.get(i).trim())) {
                double val = Double.parseDouble(values.get(i).trim());
                min = Math.min(min, val);
                max = Math.max(max, val);
            }
        }
        this.minimum = min;
        this.maximum = max;

        if (this.minimum == Double.MAX_VALUE && this.maximum == Double.MIN_VALUE) {
            this.minimum = 0;
            this.maximum = 0;
        }

        this.isMinAndMaxDetermined = true;
    }

    /**
     * Returns the minimum value in the field.
     *
     * @return the minimum value in the field.
     */
    public double getMinimum(String projectDir) {
        if (!isMinAndMaxDetermined) {
            determineMinAndMax(projectDir);
        }
        return minimum;
    }

    /**
     * Returns the maximum value in the field.
     *
     * @return the maximum value in the field.
     */
    public double getMaximum(String projectDir) {
        if (!isMinAndMaxDetermined) {
            determineMinAndMax(projectDir);
        }
        return maximum;
    }
}
