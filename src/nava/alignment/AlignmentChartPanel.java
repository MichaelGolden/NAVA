/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.alignment;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.util.ArrayList;
import javax.swing.JPanel;
import javax.swing.Scrollable;
import nava.alignment.AlignmentChartData.Marker;
import nava.structure.StructureAlign.Region;
import nava.utils.GraphicsUtils;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class AlignmentChartPanel extends JPanel implements Scrollable {

    ArrayList<AlignmentChartData> chartDataList = null;
    ArrayList<Region> highlightRegions = null;
    DecimalFormat df = new DecimalFormat("0.00");
    Rectangle visibleRect;

    public void setAlignmentChartData(ArrayList<AlignmentChartData> chartDataList) {
        this.chartDataList = chartDataList;
        repaint();
    }

    public void setHighlightRegions(ArrayList<Region> highlightRegions) {
        this.highlightRegions = highlightRegions;
        repaint();
    }

    public void setVisibleRect(Rectangle visibleRect) {
        this.visibleRect = visibleRect;
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setColor(Color.white);
        g2.fillRect(0, 0, getWidth(), getHeight());

        if (chartDataList != null && chartDataList.size() > 0) {
            int length = chartDataList.get(0).data.length;
            setPreferredSize(new Dimension((int) (length * AlignmentPanel.blockWidth), 200));

            double yoffset = getHeight() - 5;
            double height = getHeight() - 10;

            if (visibleRect != null) {
                g2.setColor(Color.white);
                g2.fill(new Rectangle2D.Double(0, 0, getWidth(), visibleRect.height));


                int n = 5;
                for (int i = 0; i < n + 1; i++) {
                    if (i == 0 || i == n) {
                        g2.setColor(Color.lightGray);
                        //g2.setColor(Color.darkGray);
                    } else {
                        g2.setColor(Color.lightGray);
                    }
                    g2.draw(new Line2D.Double(0.0, 5.0 + (i * (height / n)), visibleRect.width, 5.0 + (i * (height / n))));
                }

                double min = Double.MAX_VALUE;
                double max = Double.MIN_VALUE;
                for (AlignmentChartData chart : chartDataList) {
                    min = Math.min(min, chart.min);
                    max = Math.max(max, chart.max);
                }
                min = 0;
                max = 1;

                double x = 10;
                double width = 0;
                for (int i = 0; i < n + 1; i++) {
                    width = g2.getFontMetrics().stringWidth(df.format((min + (i * ((max - min) / ((double) n))))));
                }
                g2.setColor(Color.white);
                g2.fill(new Rectangle2D.Double(x, 0, width, getHeight()));
                for (int i = 0; i < n + 1; i++) {
                    if (i == 0 || i == n) {
                        g2.setColor(Color.lightGray);
                    } else {
                        g2.setColor(Color.lightGray);
                    }
                    double y = 5.0 + ((n - i) * (height / ((double) n)));
                    String text = df.format((min + (i * ((max - min) / ((double) n)))));
                    g2.setColor(Color.darkGray);
                    GraphicsUtils.drawStringVerticallyCentred(g2, x, y, text);
                }



                int startNuc = (int) (visibleRect.x / AlignmentPanel.blockWidth);
                int endNuc = Math.min(length, (int) ((visibleRect.x + visibleRect.width) / AlignmentPanel.blockWidth) + 1);

                if (highlightRegions != null) {
                    for (Region highlightRegion : highlightRegions) {
                        g2.setColor(new Color(255, 0, 0, 70));
                        Rectangle2D.Double rect = new Rectangle2D.Double((highlightRegion.startPos * AlignmentPanel.blockWidth) - visibleRect.x, 0, (highlightRegion.length * AlignmentPanel.blockWidth), getHeight());
                        g2.fill(rect);
                    }
                }

                for (int c = 0; c < chartDataList.size(); c++) {
                    AlignmentChartData chartData = chartDataList.get(c);
                    if (chartData.chartType == AlignmentChartData.ChartType.DASHED_LINE) {
                        float[] d1 = {9.0f, 9.0f};
                        BasicStroke s1 = new BasicStroke(1.5f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10.0f, d1, 0f);
                        BasicStroke s2 = new BasicStroke(1.5f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10.0f, d1, 9f);

                        GeneralPath path = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 10);
                        int mod = 50;
                        int startPath = (startNuc / mod) * mod;
                        int endPath = Math.min(chartData.data.length, ((int) Math.ceil((double) endNuc / (double) mod)) * mod);
                        boolean start = false;
                        for (int i = startPath; i < endPath; i++) {
                            if (chartData.data[i] != Double.MIN_VALUE) {
                                double x1 = i * AlignmentPanel.blockWidth - visibleRect.x;
                                double val = (chartData.data[i] - min) / (max - min);
                                double y1 = yoffset - (val * height);

                                if (!start) {
                                    path.moveTo(x1, y1);
                                    start = true;
                                } else {
                                    path.lineTo(x1, y1);
                                }
                            }
                        }
                        g2.setColor(chartData.lineColor1);
                        g2.setStroke(s1);
                        g2.draw(path);
                        g2.setColor(chartData.lineColor2);
                        g2.setStroke(s2);
                        g2.draw(path);

                        // draw marker
                        g2.setColor(chartData.lineColor1);
                        g2.setStroke(new BasicStroke(1));
                        for (int i = startNuc; i < endNuc; i++) {
                            if (i % 10 == 0 && i < chartData.data.length && chartData.data[i] != Double.MIN_VALUE) {
                                if (chartData.marker == Marker.CIRCLE) {
                                    double x1 = i * AlignmentPanel.blockWidth - visibleRect.x;
                                    double val = (chartData.data[i] - min) / (max - min);
                                    double y1 = yoffset - (val * height);
                                    g2.fill(new Ellipse2D.Double(x1 - 2, y1 - 2, 4, 4));
                                } else if (chartData.marker == Marker.SQUARE) {
                                    double x1 = i * AlignmentPanel.blockWidth - visibleRect.x;
                                    double val = (chartData.data[i] - min) / (max - min);
                                    double y1 = yoffset - (val * height);
                                    g2.fill(new Rectangle2D.Double(x1 - 2, y1 - 2, 4, 4));
                                }
                            }
                        }


                        /*
                         * if (chartData.lineLabel != null) {
                         * g2.setColor(Color.black); for (int i = startNuc; i <
                         * endNuc; i++) { if ((i + c * 2) % 25 == 0) { double x1
                         * = i * AlignmentPanel.blockWidth - visibleRect.x;
                         * double y1 = yoffset - (chartData.normalised[i] *
                         * height); GraphicsUtils.drawStringCentred(g2, x1, y1,
                         * chartData.lineLabel); } } }
                         */
                        /*
                         * for (int i = startNuc; i <
                         * Math.min(chartData.normalised.length - 1, endNuc);
                         * i++) { double x1 = i * AlignmentPanel.blockWidth -
                         * visibleRect.x; double y1 = yoffset -
                         * (chartData.normalised[i] * height); double x2 = (i +
                         * 1) * AlignmentPanel.blockWidth - visibleRect.x;
                         * double y2 = yoffset - (chartData.normalised[i + 1] *
                         * height); g2.setColor(chartData.lineColor1);
                         * g2.setStroke(s1); g2.draw(new Line2D.Double(x1, y1,
                         * x2, y2)); g2.setColor(chartData.lineColor2);
                         * g2.setStroke(s2); g2.draw(new Line2D.Double(x1, y1,
                         * x2, y2)); }
                         */
                    }
                }
            }
        }
    }

    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return this.getPreferredSize();
    }

    @Override
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        return -1;
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        return -1;
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        return true;
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
        return true;
    }
}
