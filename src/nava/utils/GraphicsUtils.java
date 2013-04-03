/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.utils;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import javax.swing.JDialog;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class GraphicsUtils {

    public static void drawStringCentred(Graphics2D g, double x, double y, String s) {
        FontMetrics fm = g.getFontMetrics();
        java.awt.geom.Rectangle2D rect = fm.getStringBounds(s, g);

        double textHeight = rect.getHeight();
        double textWidth = rect.getWidth();
        double x1 = x + (-textWidth / 2);
        double y1 = y + (-textHeight / 2 + fm.getAscent());

        //System.out.println(s+"\t"+x1 +"\t"+y1);
        g.drawString(s, (float) x1, (float) y1);  // Draw the string.
    }

    public static void drawStringVerticallyCentred(Graphics2D g, double x, double y, String s) {
        FontMetrics fm = g.getFontMetrics();
        java.awt.geom.Rectangle2D rect = fm.getStringBounds(s, g);

        double textHeight = rect.getHeight();
        double x1 = x;
        double y1 = y + (-textHeight / 2 + fm.getAscent());

        //System.out.println(s+"\t"+x1 +"\t"+y1);
        g.drawString(s, (float) x1, (float) y1);  // Draw the string.
    }

    public static Ellipse2D getCircleCenteredAt(double x, double y, double diameter) {
        return new Ellipse2D.Double(x - (diameter / 2), y - (diameter / 2), diameter, diameter);
    }

    public static void centerWindowOnScreen(Window d) {
        final Toolkit toolkit = Toolkit.getDefaultToolkit();
        final Dimension screenSize = toolkit.getScreenSize();
        final int x = (screenSize.width - d.getWidth()) / 2;
        final int y = (screenSize.height - d.getHeight()) / 2;
        d.setLocation(x, y);
    }
}
