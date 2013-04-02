/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.structurevis.data;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.JPanel;
import nava.ui.MainFrame;
import nava.utils.GraphicsUtils;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class NHistogramPanel extends JPanel {

    NHistogram histogram;
    double horizontalSpacing = 4;
    double rightBorder = 60;
    double leftBorder = rightBorder;
    double bottomBorder = 15;
    double topBorder = bottomBorder;
    public static Font normalFont = new Font("Arial", Font.PLAIN, 11);
    public static Font smallFont = new Font("Arial", Font.PLAIN, 10);

    public NHistogramPanel() {
        normalFont = MainFrame.fontLiberationSans.deriveFont(11f);
        smallFont = MainFrame.fontLiberationSans.deriveFont(10f);

        Random random = new Random();
        ArrayList<Double> values1 = new ArrayList<>();
        ArrayList<Double> values2 = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            values1.add(random.nextDouble() * 0.5);
            values2.add(random.nextDouble() * 1);
        }

        histogram = new NHistogram(0, 1, 15, null);
        histogram.addClass("class 1", Color.blue, values1);
        histogram.addClass("class 2", Color.red, values2);
        histogram.addClass("class 3", Color.green, values2);
        histogram.calculate();
    }

    public void setNHistogram(NHistogram histogram) {
        this.histogram = histogram;
        repaint();
    }

    @Override
    public void paintComponent(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        g.setColor(Color.white);
        g.fillRect(0, 0, width, height);

        if (histogram != null) {
            g.setFont(normalFont);

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
                    g.fill(new Rectangle2D.Double(x + j * individualBarWidth, height - bottomBorder - barHeight, individualBarWidth, barHeight));
                }
            }
            for (int j = 0; j < histogram.classes.size(); j++) {
                NHistogramClass hist = histogram.classes.get(j);
                g.setColor(hist.color);
                double y = startY + j * (legendItemHeight);
                g.fill(new Rectangle2D.Double(leftBorder + graphWidth + legendItemLeftBorder, y + (legendItemHeight / 2) - (legendBlockHeight / 2), legendBlockWidth, legendBlockHeight));

                g.setColor(Color.black);
                GraphicsUtils.drawStringVerticallyCentred(g, leftBorder + graphWidth + legendItemLeftBorder + legendBlockWidth + 5, y + (legendItemHeight / 2), hist.name);
            }

            g.setColor(Color.black);
            // g.draw(new Line2D.Double(leftBorder, height - bottomBorder / 2, endy, height - bottomBorder / 2));
            // draw x-axis

            g.setFont(smallFont);
            if (histogram.transform != null) {
                double swidth = g.getFontMetrics().stringWidth("0.0000");

                int points = Math.max(2, (int) (graphWidth / swidth / 2));
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
            GraphicsUtils.drawStringVerticallyCentred(g, leftBorder - 20 - g.getFontMetrics().stringWidth("100%"), topBorder, "100%");
            GraphicsUtils.drawStringVerticallyCentred(g, leftBorder - 20 - g.getFontMetrics().stringWidth("50%"), topBorder + graphHeight / 2, "50%");
            GraphicsUtils.drawStringVerticallyCentred(g, leftBorder - 20 - g.getFontMetrics().stringWidth("0%"), topBorder + graphHeight, "0%");

            g.draw(new Line2D.Double(leftBorder, height - bottomBorder, leftBorder + graphWidth, height - bottomBorder));
            g.draw(new Line2D.Double(leftBorder, topBorder, leftBorder, height - bottomBorder));

            float dash[] = { 2.0f };
            g.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f));
            for (int j = 0; j < histogram.classes.size(); j++) {
                NHistogramClass hist = histogram.classes.get(j);;
                g.setColor(hist.color.darker());
                if (histogram.transform != null) {
                    double medianx = histogram.transform.transform(hist.median);
                    System.out.println(j+" median "+medianx);
                    g.draw(new Line2D.Double(leftBorder+medianx*graphWidth, height - bottomBorder, leftBorder+medianx*graphWidth, topBorder));
                }
            }
        }
    }
}
