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
import nava.data.types.Tabular;
import nava.data.types.TabularData;
import nava.data.types.TabularField;
import nava.ui.MainFrame;
import nava.ui.ProjectModel;
import nava.utils.ColorGradient;
import nava.utils.Utils;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class DataOverlay1D extends Overlay implements Serializable {

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
        ArrayList<String> values = dataField.getObject(ProjectModel.path,MainFrame.dataSourceCache).values;
        dataOffsetCorrected = dataOffset;
        if (codonPositions) {
         //   dataOffsetCorrected *= 3;
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
                data = new double[values.size()-dataOffsetCorrected];
                stringData = new String[values.size()];
                used = new boolean[values.size()-dataOffsetCorrected];
                for(int i = 0 ; i < values.size() ; i++)
                {
                    stringData[i] = values.get(i);
                }
                for (int i = 0; i < data.length; i++) {
                    stringData[i] = values.get(i);
                    if (Utils.isNumeric(values.get(i+dataOffsetCorrected))) {
                        data[i] = Double.parseDouble(values.get(i+dataOffsetCorrected));
                        used[i] = true;
                    }
                }
            } else {
                data = new double[(values.size()-dataOffsetCorrected) * 3];
                stringData = new String[values.size() * 3];
                used = new boolean[(values.size()-dataOffsetCorrected) * 3];
                
                for(int i = 0 ; i < values.size() ; i++)
                {
                    stringData[i * 3] = values.get(i);
                    stringData[i * 3 + 1] = values.get(i);
                    stringData[i * 3 + 2] = values.get(i);
                }
                    
                for (int i = 0; i < values.size()-dataOffset; i++) {
                    if (Utils.isNumeric(values.get(i+dataOffsetCorrected))) {
                        data[i * 3] = Double.parseDouble(values.get(i+dataOffsetCorrected));
                        data[i * 3 + 1] = Double.parseDouble(values.get(i+dataOffsetCorrected));
                        data[i * 3 + 2] = Double.parseDouble(values.get(i+dataOffsetCorrected));
                        used[i * 3] = true;
                        used[i * 3 + 1] = true;
                        used[i * 3 + 2] = true;
                    }
                }
            }
        } else {
            if (positionField != null) {
                ArrayList<String> positionValues = positionField.getObject(ProjectModel.path,MainFrame.dataSourceCache).values;
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

        System.out.println("dataOffsetCorrected "+dataOffsetCorrected);
        for (int i = 0; i < dataOffsetCorrected && i < used.length; i++) {
            //used[i] = false;
            System.out.println(i+"\t"+used[i]);
        }
    }

    public static DataOverlay1D getDataOverlay1D(Tabular dataTable, TabularField field, String title, TabularField positionField, boolean naturalPositions, boolean oneOffset, int dataOffset, boolean codonPositions, double min, double max, boolean excludeValuesOutOfRange, DataTransform dataTransform, ColorGradient colorGradient, MappingSource mappingSource) {
        DataOverlay1D dataOverlay = new DataOverlay1D();
        dataOverlay.dataTable = dataTable;
        dataOverlay.dataField = field;
        dataOverlay.title = title;
        dataOverlay.positionField = positionField;
        dataOverlay.naturalPositions = naturalPositions;
        dataOverlay.mappingSource = mappingSource;
        dataOverlay.oneOffset = oneOffset;
        dataOverlay.dataOffset = dataOffset;
        dataOverlay.codonPositions = codonPositions;
        dataOverlay.minValue = min;
        dataOverlay.maxValue = max;
        dataOverlay.thresholdMin = min;
        dataOverlay.thresholdMax= max;
        dataOverlay.excludeValuesOutOfRange = excludeValuesOutOfRange;
        dataOverlay.dataTransform = dataTransform;
        dataOverlay.colorGradient = colorGradient;
        if (colorGradient != null) {
            dataOverlay.defaultColorGradient = colorGradient.clone();
        }

        return dataOverlay;
    }

    @Override
    public Icon getIcon() {
        return new ImageIcon(ClassLoader.getSystemResource("resources/icons/tabular-16x16.png"));
    }
}
