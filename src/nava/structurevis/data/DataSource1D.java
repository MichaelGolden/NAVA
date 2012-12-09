/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.structurevis.data;

import java.io.Serializable;
import java.util.ArrayList;
import nava.data.types.Alignment;
import nava.data.types.Tabular;
import nava.data.types.TabularData;
import nava.data.types.TabularField;
import nava.ui.MainFrame;
import nava.utils.ColorGradient;
import nava.utils.Utils;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class DataSource1D implements Serializable {

    public String title;
    public ColorGradient defaultColorGradient;
    public ColorGradient colorGradient;
    public DataTransform dataTransform;
    public Tabular dataTable;
    public TabularField dataField;
    public TabularField positionField;
    public MappingSource mappingSource;
    public String mappingSequence;
    public boolean naturalPositions;
    public boolean oneOffset;
    public boolean codonPositions;
    public boolean excludeValuesOutOfRange = false;
    public double minValue;
    public double maxValue;
    public transient double[] data;
    public transient String[] stringData;
    public transient boolean[] used;
    public int dataOffset = 0;
    public int dataOffsetCorrected = 0;

    public void loadData() {
        ArrayList<String> values = dataField.getObject(MainFrame.dataSourceCache).values;

        dataOffsetCorrected = dataOffset;
        if (codonPositions) {
            dataOffsetCorrected *= 3;
        }

        /*
         * if (mappingSource != null && mappingSource.numSequences > 0) {
         * mappingSequence =
         * mappingSource.getObject(MainFrame.dataSourceCache).sequences.get(0);
         * } else { mappingSequence = null;
        }
         */

        if (naturalPositions) {
            if (!codonPositions) {
                data = new double[values.size()];
                stringData = new String[values.size()];
                used = new boolean[values.size()];
                for (int i = 0; i < data.length; i++) {
                    stringData[i] = values.get(i);
                    if (Utils.isNumeric(values.get(i))) {
                        data[i] = Double.parseDouble(values.get(i));
                        used[i] = true;
                    }
                }
            } else {
                data = new double[values.size() * 3];
                stringData = new String[values.size() * 3];
                used = new boolean[values.size() * 3];
                for (int i = 0; i < values.size(); i++) {
                    stringData[i * 3] = values.get(i);
                    stringData[i * 3 + 1] = values.get(i);
                    stringData[i * 3 + 2] = values.get(i);
                    if (Utils.isNumeric(values.get(i))) {
                        data[i * 3] = Double.parseDouble(values.get(i));
                        data[i * 3 + 1] = Double.parseDouble(values.get(i));
                        data[i * 3 + 2] = Double.parseDouble(values.get(i));
                        used[i * 3] = true;
                        used[i * 3 + 1] = true;
                        used[i * 3 + 2] = true;
                    }
                }
            }
        } else {
            if (positionField != null) {
                ArrayList<String> positionValues = positionField.getObject(MainFrame.dataSourceCache).values;
                ArrayList<Integer> positions = new ArrayList<>();
                int length = 0;
                for (int i = 0; i < positionValues.size(); i++) {
                    if (Utils.isNumeric(positionValues.get(i))) {
                        double p = Double.parseDouble(positionValues.get(i));
                        if ((double) ((int) p) == p) {
                            int pos = (int) p;
                            if (oneOffset) {
                                pos = pos - 1;
                            }
                            length = Math.max(length, pos + 1);
                            if (pos >= 0) {
                                positions.add(pos);
                            } else {
                                positions.add(-1);
                            }
                        }
                    } else {
                        positions.add(-1);
                    }
                }

                if (!codonPositions) {
                    data = new double[length];
                    stringData = new String[length];
                    used = new boolean[length];
                    for (int i = 0; i < positions.size(); i++) {
                        if (positions.get(i) != -1 && i < values.size()) {
                            stringData[positions.get(i)] = values.get(i);
                            if (Utils.isNumeric(values.get(i))) {
                                data[positions.get(i)] = Double.parseDouble(values.get(i));
                                used[positions.get(i)] = true;
                            }
                        }
                    }
                } else {
                    data = new double[length * 3];
                    stringData = new String[length * 3];
                    used = new boolean[length * 3];
                    for (int i = 0; i < positions.size(); i++) {
                        if (positions.get(i) != -1 && i < values.size()) {
                            stringData[positions.get(i) * 3] = values.get(i);
                            stringData[positions.get(i) * 3 + 1] = values.get(i);
                            stringData[positions.get(i) * 3 + 2] = values.get(i);
                            if (Utils.isNumeric(values.get(i))) {

                                data[positions.get(i) * 3] = Double.parseDouble(values.get(i));
                                data[positions.get(i) * 3 + 1] = Double.parseDouble(values.get(i));
                                data[positions.get(i) * 3 + 2] = Double.parseDouble(values.get(i));
                                used[positions.get(i) * 3] = true;
                                used[positions.get(i) * 3 + 1] = true;
                                used[positions.get(i) * 3 + 2] = true;
                            }
                        }
                    }
                }
            }
        }

        for (int i = 0; i < dataOffsetCorrected; i++) {
            used[i] = false;
        }
    }

    public static DataSource1D getDataSource1D(Tabular dataTable, TabularField field, String title, TabularField positionField, boolean naturalPositions, boolean oneOffset, int dataOffset, boolean codonPositions, double min, double max, boolean excludeValuesOutOfRange, DataTransform dataTransform, ColorGradient colorGradient, MappingSource mappingSource) {
        DataSource1D dataSource = new DataSource1D();
        dataSource.dataTable = dataTable;
        dataSource.dataField = field;
        dataSource.title = title;
        dataSource.positionField = positionField;
        dataSource.naturalPositions = naturalPositions;
        dataSource.mappingSource = mappingSource;
        dataSource.oneOffset = oneOffset;
        dataSource.dataOffset = dataOffset;
        dataSource.codonPositions = codonPositions;
        dataSource.minValue = min;
        dataSource.maxValue = max;
        dataSource.excludeValuesOutOfRange = excludeValuesOutOfRange;
        dataSource.dataTransform = dataTransform;
        dataSource.colorGradient = colorGradient;
        if (colorGradient != null) {
            dataSource.defaultColorGradient = colorGradient.clone();
        }

        return dataSource;
    }
}
