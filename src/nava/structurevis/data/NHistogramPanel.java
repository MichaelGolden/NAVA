/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.structurevis.data;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.JPanel;
import javax.swing.ToolTipManager;
import nava.ui.MainFrame;
import nava.utils.GraphicsUtils;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class NHistogramPanel extends JPanel implements MouseMotionListener {

    NHistogram histogram;
    double horizontalSpacing = 4;
    double rightBorder = 70;
    double leftBorder = rightBorder;
    double bottomBorder = 15;
    double topBorder = bottomBorder;
    public static Font titleFont = new Font("Arial", Font.BOLD, 12);
    public static Font normalFont = new Font("Arial", Font.PLAIN, 11);
    public static Font smallFont = new Font("Arial", Font.PLAIN, 10);
    public static DecimalFormat df = new DecimalFormat("0.00");

    public NHistogramPanel() {
        normalFont = MainFrame.fontLiberationSans.deriveFont(11f);
        smallFont = MainFrame.fontLiberationSans.deriveFont(10f);

        addMouseMotionListener(this);

        ToolTipManager.sharedInstance().registerComponent(this);
    }

    public void setNHistogram(NHistogram histogram) {
        this.histogram = histogram;
        repaint();
    }
    ArrayList<ColumnRectangle> columns = new ArrayList<>();

    @Override
    public void paintComponent(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        g.setColor(Color.white);
        g.fillRect(0, 0, width, height);

        if (histogram == null) {
            g.setColor(Color.black);
            GraphicsUtils.drawStringCentred(g, width / 2, height / 2, "Click on a substructure to compare it's distribution to that of the full sequence.");
        } else {
            columns.clear();
            g.setFont(normalFont);

            if (histogram.title != null) {
                topBorder = bottomBorder + 20;
                g.setColor(Color.black);
                g.setFont(titleFont);
                GraphicsUtils.drawStringCentred(g, width / 2, topBorder / 2 - 5, histogram.title);
            }

            double legendItemHeight = 25;
            double legendItemLeftBorder = 10;
            double startY = height / 2 - (histogram.classes.size() * (legendItemHeight)) / 2;
            double legendBlockHeight = 15;
            double legendBlockWidth = legendBlockHeight;

            double legendWidth = 50;
            for (int j = 0; j < histogram.classes.size(); j++) {
                NHistogramClass hist = histogram.classes.get(j);
                legendWidth = Math.max(legendWidth, legendItemLeftBorder + legendBlockWidth + 5 + g.getFontMetrics().stringWidth(hist.name) + 25);
            }

            double graphWidth = width - leftBorder - legendWidth;
            double barWidth = (graphWidth - histogram.nbins * horizontalSpacing) / histogram.nbins;
            double graphHeight = height - bottomBorder - topBorder;
            double individualBarWidth = barWidth / histogram.classes.size();
            for (int j = 0; j < histogram.classes.size(); j++) {
                NHistogramClass hist = histogram.classes.get(j);
                for (int i = 0; i < histogram.nbins; i++) {
                    double x = leftBorder + i * (barWidth + horizontalSpacing);
                    double barHeight = (hist.percs[i] / histogram.maxBinPerc) * graphHeight;

                    g.setColor(hist.transparentColor);
                    Rectangle2D.Double rect = new Rectangle2D.Double(x + j * individualBarWidth, height - bottomBorder - barHeight, individualBarWidth, barHeight);
                    double min = (double) i / ((double) (histogram.nbins));
                    double max = (double) (i + 1) / ((double) (histogram.nbins));
                    String mouseOverString = "";
                    for (int k = 0; k < histogram.classes.size(); k++) {
                        mouseOverString += histogram.classes.get(k).bins[i] + " (" + df.format(histogram.classes.get(k).percs[i] * 100) + "%)";
                        if (k != histogram.classes.size() - 1) {
                            mouseOverString += ", ";
                        }
                    }
                    if (histogram.transform != null) {
                        min = histogram.transform.inverseTransform(min);
                        max = histogram.transform.inverseTransform(max);
                        mouseOverString += " in range [" + histogram.transform.getFormattedString(min, 2) + ", " + histogram.transform.getFormattedString(max, 2) + ")";
                    } else {
                        mouseOverString += " in range [" + df.format(min) + ", " + df.format(max) + ")";
                    }
                    ColumnRectangle columnRectangle = new ColumnRectangle(rect, mouseOverString);
                    columns.add(columnRectangle);
                    g.fill(rect);
                }
            }
            for (int j = 0; j < histogram.classes.size(); j++) {
                NHistogramClass hist = histogram.classes.get(j);
                g.setColor(hist.transparentColor);
                double y = startY + j * (legendItemHeight);
                g.fill(new Rectangle2D.Double(leftBorder + graphWidth + legendItemLeftBorder, y + (legendItemHeight / 2) - (legendBlockHeight / 2), legendBlockWidth, legendBlockHeight));

                g.setColor(Color.black);
                g.setFont(normalFont);
                GraphicsUtils.drawStringVerticallyCentred(g, leftBorder + graphWidth + legendItemLeftBorder + legendBlockWidth + 5, y + (legendItemHeight / 2), hist.name);
            }

            g.setColor(Color.black);
            // g.draw(new Line2D.Double(leftBorder, height - bottomBorder / 2, endy, height - bottomBorder / 2));
            // draw x-axis

            g.setFont(smallFont);
            if (histogram.transform != null) {
                double swidth = g.getFontMetrics().stringWidth("0.0000");

                int points = Math.max(2, (int) (graphWidth / swidth / 1.7));
                for (int i = 0; i < points; i++) {
                    double val = 0;
                    double y = 0;
                    if (i == 0) {
                        val = histogram.min;
                        y = 0;
                    } else if (i == points - 1) {
                        val = histogram.max;
                        y = 1;
                    } else {
                        y = (double) i / (double) (points - 1);
                        val = histogram.transform.inverseTransform(y);
                    }
                    g.draw(new Line2D.Double(leftBorder + (y * graphWidth), height - bottomBorder - 1, leftBorder + (y * graphWidth), height - bottomBorder + 1));
                    GraphicsUtils.drawStringCentred(g, leftBorder + (y * graphWidth), height - bottomBorder / 2, histogram.transform.getFormattedString(val, 2));
                }

            } else {
                double endy = leftBorder + histogram.nbins * (barWidth + horizontalSpacing);
                GraphicsUtils.drawStringCentred(g, leftBorder, height - bottomBorder / 2, histogram.min + "");
                GraphicsUtils.drawStringCentred(g, endy, height - bottomBorder / 2, histogram.max + "");
            }

            g.setFont(normalFont);
            // draw y-axis
            double maxPerc = histogram.maxBinPerc;
            GraphicsUtils.drawStringVerticallyCentred(g, leftBorder - 20 - g.getFontMetrics().stringWidth(df.format(maxPerc * 100) + "%"), topBorder, df.format(maxPerc * 100) + "%");
            GraphicsUtils.drawStringVerticallyCentred(g, leftBorder - 20 - g.getFontMetrics().stringWidth(df.format(maxPerc / 2 * 100) + "%"), topBorder + graphHeight / 2, df.format(maxPerc / 2 * 100) + "%");
            GraphicsUtils.drawStringVerticallyCentred(g, leftBorder - 20 - g.getFontMetrics().stringWidth("0%"), topBorder + graphHeight, "0%");

            g.draw(new Line2D.Double(leftBorder, height - bottomBorder, leftBorder + graphWidth, height - bottomBorder));
            g.draw(new Line2D.Double(leftBorder, topBorder, leftBorder, height - bottomBorder));

            float dash[] = {2.0f};
            for (int j = 0; j < histogram.classes.size(); j++) {
                g.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, (float) j / (float) histogram.classes.size() * 2.0f));
                NHistogramClass hist = histogram.classes.get(j);
                g.setColor(hist.color.darker());
                if (histogram.transform != null) {
                    double medianx = histogram.transform.transform(hist.median);
                    g.draw(new Line2D.Double(leftBorder + medianx * graphWidth, height - bottomBorder, leftBorder + medianx * graphWidth, topBorder));
                }
            }
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        for (ColumnRectangle col : columns) {
            if (col.rectangle.contains(e.getX(), e.getY())) {
            }
        }
    }

    @Override
    public String getToolTipText(MouseEvent event) {
        Point p = new Point(event.getX(), event.getY());
        for (ColumnRectangle col : columns) {
            if (col.rectangle.contains(event.getX(), event.getY())) {
                return col.mouseOverString;
            }
        }
        return super.getToolTipText(event);
    }
}

class ColumnRectangle {

    Rectangle2D.Double rectangle;
    String mouseOverString;

    public ColumnRectangle(Rectangle2D.Double rectangle, String mouseOverString) {
        this.rectangle = rectangle;
        this.mouseOverString = mouseOverString;
    }
}
