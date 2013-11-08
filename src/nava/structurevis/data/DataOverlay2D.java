/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.structurevis.data;

import java.io.Serializable;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import nava.data.types.Matrix;
import nava.ui.MainFrame;
import nava.ui.ProjectModel;
import nava.utils.ColorGradient;
import nava.utils.Mapping;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class DataOverlay2D extends Overlay implements Serializable {

    private static final long serialVersionUID = 923970269971431138L;
    public ColorGradient defaultColorGradient;
    public ColorGradient colorGradient;
    public DataTransform dataTransform;
    //public Matrix dataMatrix;
    public Matrix matrix;
    public transient PersistentSparseMatrix dataMatrix;
    public MappingSource mappingSource;
    public boolean naturalPositions;
    public int dataOffset;
    public boolean codonPositions;
    public boolean excludeValuesOutOfRange = false;
    public double minValue;
    public double maxValue;
    public transient double[] data;
    public transient String[] data2;
    public transient boolean[] used;
    public double emptyValue;

    public static enum MatrixRegion {

        FULL, UPPER_TRIANGLE, LOWER_TRIANGLE
    };
    public MatrixRegion matrixRegion = MatrixRegion.FULL;

    public void loadData() {
        dataMatrix = matrix.getObject(ProjectModel.path, MainFrame.dataSourceCache);
        if (dataMatrix != null) {
            emptyValue = dataMatrix.getEmptyValue();
        }
    }

    public static DataOverlay2D getDataOverlay2D(Matrix matrix, String title, boolean naturalPositions, int dataOffset, boolean codonPositions, double min, double max, boolean excludeValuesOutOfRange, DataTransform dataTransform, ColorGradient colorGradient, MappingSource mappingSource, MatrixRegion matrixRegion) {
        DataOverlay2D dataOverlay = new DataOverlay2D();
        dataOverlay.matrix = matrix;
        dataOverlay.title = title;
        dataOverlay.naturalPositions = naturalPositions;
        dataOverlay.mappingSource = mappingSource;
        dataOverlay.dataOffset = dataOffset;
        dataOverlay.codonPositions = codonPositions;
        dataOverlay.minValue = min;
        dataOverlay.maxValue = max;
        dataOverlay.thresholdMin = min;
        dataOverlay.thresholdMax = max;
        dataOverlay.excludeValuesOutOfRange = excludeValuesOutOfRange;
        dataOverlay.dataTransform = dataTransform;
        dataOverlay.colorGradient = colorGradient;
        if (colorGradient != null) {
            dataOverlay.defaultColorGradient = colorGradient.clone();
        }
        dataOverlay.matrixRegion = matrixRegion;

        return dataOverlay;
    }

    public double get(int i, int j, Mapping mapping) {
        if(dataMatrix == null)
        {
            return emptyValue;
        }
        int x = i - dataOffset;
        int y = j - dataOffset;
        if (codonPositions) {
            x = x / 3;
            y = y / 3;
        }
        if (mapping != null) {
            x = mapping.aToB(i - dataOffset);
            y = mapping.aToB(j - dataOffset);
        }

        
        try {
         
            
            switch (matrixRegion) {
                case FULL:
                    /*System.out.println("full\t"+x+"\t"+y+"\t"+i+"\t"+j+"\t"+dataMatrix.getValue(x, y));
                    if(x == 8154 && y == 8205)
                    {
                        System.out.println("coevolution\t"+x+"\t"+y+"\t"+i+"\t"+j+"\t"+dataMatrix.getValue(x, y));
                    }*/
                    return dataMatrix.getValue(x, y);
                case UPPER_TRIANGLE:
                    
                    /*System.out.println("upper\t"+x+"\t"+y+"\t"+i+"\t"+j+"\t"+dataMatrix.getValue(x, y));
                    if(x == 8154 && y == 8205)
                    {
                        System.out.println("coevolution\t"+x+"\t"+y+"\t"+i+"\t"+j+"\t"+dataMatrix.getValue(x, y));
                    }*/
                    if (i <= j) {
                        return dataMatrix.getValue(x, y);
                    } else {
                        return emptyValue;
                    }
                case LOWER_TRIANGLE:                    
                    /*System.out.println("lower\t"+x+"\t"+y+"\t"+i+"\t"+j+"\t"+dataMatrix.getValue(x, y));
                    if(x == 8154 && y == 8205)
                    {
                        System.out.println("coevolution\t"+x+"\t"+y+"\t"+i+"\t"+j+"\t"+dataMatrix.getValue(x, y));
                    }*/
                    if (i >= j) {
                        return dataMatrix.getValue(x, y);
                    } else {
                        return emptyValue;
                    }
                default:
                    return emptyValue;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return emptyValue;
        }
        

    }

    @Override
    public Icon getIcon() {
        return new ImageIcon(ClassLoader.getSystemResource("resources/icons/matrix-16x16.png"));
    }
}
