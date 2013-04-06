/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.ui;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class ProgressHandle {

    enum ProgressState {

        INDETERMINATE, RUNNING, FINISHED
    };
    double progress = 0;
    String label;
    ProgressState itemState = ProgressState.INDETERMINATE;

    public ProgressHandle(String label, ProgressState itemState, double progress) {
        this.label = label;
        this.progress = progress;
        this.itemState = itemState;
    }
}
