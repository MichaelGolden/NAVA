/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.alignment;

import java.util.EventListener;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public interface AlignmentPanelListener extends EventListener {
    public void mouseDraggedOffVisibleRegion(int x, int y);
}
