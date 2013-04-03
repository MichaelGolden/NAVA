/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.structurevis.data;

import java.io.Serializable;
import javax.swing.Icon;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public abstract class Overlay implements Serializable {
    private static final long serialVersionUID = -5012755663397917899L;

    public String title;

    public enum OverlayState implements Serializable {

        UNSELECTED, PRIMARY_SELECTED, SECONDARY_SELECTED
    };
    private OverlayState state = OverlayState.UNSELECTED;
   
    // only applies to 1D and 2D data overlays
    public boolean useLowerThreshold = true;
    public boolean useUpperThreshold = true;
    public double thresholdMin = 0;
    public double thresholdMax = 1;

    public OverlayState getState() {
        return state;
    }

    public void setState(OverlayState state) {
        this.state = state;
    }

    public abstract Icon getIcon();
}
