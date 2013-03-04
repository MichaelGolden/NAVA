/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.alignment;

import java.awt.*;
import java.awt.geom.Line2D;
import javax.swing.JPanel;
import nava.utils.GraphicsUtils;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class RulerPanel extends JPanel {

    Rectangle visibleRect = null;

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        if (visibleRect != null) {
            int startNuc = (int) (visibleRect.x / AlignmentPanel.blockWidth);
            int endNuc = startNuc + (int) (visibleRect.width / AlignmentPanel.blockWidth) + 1;

            double xoffset = -visibleRect.x;
            int rulerTickMarkInterval = 10;
            g2.setColor(Color.white);
            g2.fill(new Rectangle.Double(0, 0, getWidth(), getHeight()));
            g2.setColor(Color.gray);
            for (int nuc = startNuc; nuc < endNuc; nuc++) {
                if (nuc % rulerTickMarkInterval == 0) {
                    GraphicsUtils.drawStringCentred(g2, xoffset + (nuc * AlignmentPanel.blockWidth + (AlignmentPanel.blockWidth / 2)), 0 + (AlignmentPanel.blockHeight / 2), (nuc + 1) + "");
                    Line2D.Double tickMark = new Line2D.Double(xoffset + nuc * AlignmentPanel.blockWidth + (AlignmentPanel.blockWidth / 2), 0 + AlignmentPanel.rulerHeight - 2, xoffset + nuc * AlignmentPanel.blockWidth + (AlignmentPanel.blockWidth / 2), 0 + AlignmentPanel.rulerHeight);
                    g2.draw(tickMark);
                }
            }
            g2.setColor(Color.darkGray);
            g2.draw(new Line2D.Double(visibleRect.x, visibleRect.y + AlignmentPanel.rulerHeight, visibleRect.x + visibleRect.width, visibleRect.y + AlignmentPanel.rulerHeight));
        }
        else
        {
            g2.setColor(Color.white);
            Rectangle visibleRect = this.getVisibleRect();
            System.out.println("HEREA"+visibleRect);
            g2.fillRect(visibleRect.x, visibleRect.y, visibleRect.width, visibleRect.height);
        }
    }

    public void setVisibleRect(Rectangle visibleRect) {
        this.visibleRect = visibleRect;
        repaint();
    }
}
