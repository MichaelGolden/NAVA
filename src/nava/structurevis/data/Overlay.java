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
    public String title;    
    
    public enum OverlayState {UNSELECTED, PRIMARY_SELECTED, SECONDARY_SELECTED};    
    private OverlayState state = OverlayState.UNSELECTED;
    
    public OverlayState getState()
    {
        return state;
    }
    
    
    public void setState(OverlayState state)
    {
        this.state = state;
    }
    
    public abstract Icon getIcon();
}
