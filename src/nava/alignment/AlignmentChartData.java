/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.alignment;

import java.awt.Color;
import java.util.Arrays;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class AlignmentChartData {

    public enum ChartType {

        DASHED_LINE, LINE, BAR
    };
    public enum Marker{
        NONE,CIRCLE,SQUARE
    }
    double[] data;
    double[] normalised;
    double min = Double.MAX_VALUE;
    double max = Double.MIN_VALUE;
    Color lineColor1;
    Color lineColor2;
    String lineLabel = null;
    ChartType chartType;
    Marker marker;
    

    public AlignmentChartData(double[] data,ChartType chartType, Color lineColor1, Color lineColor2, String lineLabel, Marker marker) {
        this.data = data;
        this.lineColor1 = lineColor1;
        this.lineColor2 = lineColor2;
        this.chartType = chartType;
        this.lineLabel = lineLabel;
        this.marker = marker;

        for (int i = 0; i < data.length; i++) {
            min = Math.min(min, data[i]);
            max = Math.max(max, data[i]);
        }
        normalised = new double[data.length];
        //System.out.println("MIN"+min+"\tMAX"+max);
        if (max != min) {
            for (int i = 0; i < data.length; i++) {
                if(data[i] == Double.MIN_VALUE)
                {
                    normalised[i] = Double.MIN_VALUE;
                }
                else
                {
                    normalised[i] = (data[i] - min) / (max - min);
                }
            }
        } else {
            Arrays.fill(normalised, 0);
        }
    }
}
