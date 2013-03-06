/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.structurevis.data;

import java.io.Serializable;
import java.util.ArrayList;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import nava.data.types.Alignment;
import nava.data.types.Matrix;
import nava.data.types.TabularField;
import nava.ui.MainFrame;
import nava.utils.ColorGradient;
import nava.utils.Mapping;
import nava.utils.Utils;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class DataOverlay2D extends Overlay implements Serializable {

    public ColorGradient defaultColorGradient;
    public ColorGradient colorGradient;
    public DataTransform dataTransform;
    //public Matrix dataMatrix;
    public Matrix matrix;
    public PersistentSparseMatrix dataMatrix;
    public MappingSource mappingSource;
    public String mappingSequence;
    public boolean naturalPositions;
    public boolean oneOffset;
    public boolean codonPositions;
    public boolean excludeValuesOutOfRange = false;
    public double minValue;
    public double maxValue;
    public transient double[] data;
    public transient String[] data2;
    public transient boolean[] used;
    public double emptyValue;
    
    
    public static enum MatrixRegion{FULL, UPPER_TRIANGLE, LOWER_TRIANGLE};
    MatrixRegion matrixRegion = MatrixRegion.FULL;
    
    public void loadData() {
        dataMatrix = matrix.getObject(MainFrame.dataSourceCache);
        if(dataMatrix != null)
        {
            emptyValue = dataMatrix.getEmptyValue();
        }
    }

    public static DataOverlay2D getDataOverlay2D(Matrix matrix, String title,boolean naturalPositions, boolean oneOffset, boolean codonPositions, double min, double max, boolean excludeValuesOutOfRange, DataTransform dataTransform, ColorGradient colorGradient, MappingSource mappingSource, MatrixRegion matrixRegion) {
        DataOverlay2D dataOverlay = new DataOverlay2D();
        dataOverlay.matrix = matrix;
        dataOverlay.title = title;
        dataOverlay.naturalPositions = naturalPositions;
        dataOverlay.mappingSource = mappingSource;
        dataOverlay.oneOffset = oneOffset;
        dataOverlay.codonPositions = codonPositions;
        dataOverlay.minValue = min;
        dataOverlay.maxValue = max;
        dataOverlay.excludeValuesOutOfRange = excludeValuesOutOfRange;
        dataOverlay.dataTransform = dataTransform;
        dataOverlay.colorGradient = colorGradient;
        if (colorGradient != null) {
            dataOverlay.defaultColorGradient = colorGradient.clone();
        }
        dataOverlay.matrixRegion = matrixRegion;

        return dataOverlay;
    }
    
    public double get(int i, int j, Mapping mapping)
    {
        int x = mapping.aToB(i);
        int y = mapping.aToB(j);
        try
        {
            switch(matrixRegion)
            {
                case FULL:
                    return dataMatrix.getValue(x, y);
                case UPPER_TRIANGLE:
                    if(x <= y)
                    {
                        return dataMatrix.getValue(x, y);
                    }
                    else
                    {
                        return emptyValue;
                    }
                case LOWER_TRIANGLE:
                    if(x >= y)
                    {
                        return dataMatrix.getValue(x, y);
                    }
                    else
                    {
                        return emptyValue;
                    }
                default:
                    return emptyValue;
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            return emptyValue;
        }
    }
    
    @Override
    public Icon getIcon() {
        return new ImageIcon(ClassLoader.getSystemResource("resources/icons/matrix-16x16.png"));
    }
}
