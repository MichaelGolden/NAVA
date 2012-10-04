/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.ui;

import java.awt.FontMetrics;
import java.awt.Graphics2D;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class GraphicsTools {
     public static void drawStringCentred(Graphics2D g, double x, double y, String s) {
        FontMetrics fm = g.getFontMetrics();
        java.awt.geom.Rectangle2D rect = fm.getStringBounds(s, g);

        int textHeight = (int) (rect.getHeight());
        int textWidth = (int) (rect.getWidth());
        double x1 = x + (-textWidth / 2);
        double y1 = y + (-textHeight / 2 + fm.getAscent());

        g.drawString(s, (float) x1, (float) y1);  // Draw the string.
    }
}
