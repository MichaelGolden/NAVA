/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.utils;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import javax.swing.JDialog;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

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
    
    public static void centerWindowOnWindow(Window d, Window w) {
        final int x = w.getX() + ((w.getWidth() - d.getWidth())/ 2);
        final int y = w.getY() + ((w.getHeight() - d.getHeight())/ 2);
        d.setLocation(x, y);
    }
    
    
    

    public static String getHexString(Color color) {
        return Integer.toHexString((color.getRGB() & 0xffffff) | 0x1000000).substring(1);
    }
    
    

    public static String getRGBAString(Color color) {
        //return "rgba("+color.getRed()+","+color.getGreen()+","+color.getBlue()+","+(1f - ((float)color.getAlpha()/255f))+")";
        return "rgb("+color.getRed()+","+color.getGreen()+","+color.getBlue()+");opacity:"+(((float)color.getAlpha()/255f));
        
        //return "rgb("+color.getRed()+","+color.getGreen()+","+color.getBlue()+")";
    }
    
    public static void resizeColumnWidth(JTable table) {
       // table.setAutoResizeMode(JTable.);
        final TableColumnModel columnModel = table.getColumnModel();
        for (int column = 0; column < table.getColumnCount(); column++) {
            int width = 50; // Min width
            for (int row = 0; row < table.getRowCount(); row++) {
                TableCellRenderer renderer = table.getCellRenderer(row, column);
                Component comp = table.prepareRenderer(renderer, row, column);
                width = Math.max(comp.getPreferredSize().width, width);
            }
            columnModel.getColumn(column).setPreferredWidth(width);
        }
    }
}
