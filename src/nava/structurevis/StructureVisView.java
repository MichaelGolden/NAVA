/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.structurevis;

import javax.swing.event.ListDataEvent;
import nava.structurevis.data.Overlay;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public interface StructureVisView {
    public void dataOverlayAdded(Overlay overlay);
    public void dataOverlayRemoved(Overlay overlay);
    public void dataOverlayChanged(Overlay oldOverlay, Overlay newOverlay);
}
