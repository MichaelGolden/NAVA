/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.ui;

import java.util.ArrayList;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class ProgressBarMonitor {

    ArrayList<JProgressBar> progressBars = new ArrayList<>();
    boolean indeterminate = false;
    String label = "";
    double value = 1;
    
    public static String CREATE_MAPPING = "Creating mapping";
    public static String IMPORT_DATASOURCE = "Importing data source";
    public static String ADDING_DATA_OVERLAY = "Adding data overlay";
    public static String INACTIVE = "Inactive";
    
    public static int INACTIVE_VALUE = 0;

    public void addJProgressBar(JProgressBar jProgressBar) {
        progressBars.add(jProgressBar);
        set(false,INACTIVE,INACTIVE_VALUE);
    }

    public void removeJProgressBar(JProgressBar jProgressBar) {
        progressBars.remove(jProgressBar);
    }

    public void set(boolean indeterminate, String label, double value) {
        this.indeterminate = indeterminate;
        this.label = label;
        this.value = value;
        for (JProgressBar jProgressBar : progressBars) {            
            jProgressBar.setValue((int) (value * jProgressBar.getMaximum()));
            jProgressBar.setIndeterminate(indeterminate);
            if (label != null) {
                jProgressBar.setStringPainted(true);
                jProgressBar.setString(label);
            } else {
                jProgressBar.setStringPainted(false);
            }
        }
    }
}
