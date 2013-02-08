/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.alignment;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import javax.swing.JPanel;
import javax.swing.Scrollable;
import nava.utils.GraphicsUtils;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class AlignmentChartPanel extends JPanel implements Scrollable {
    
    ArrayList<AlignmentChartData> chartDataList = null;
    Rectangle visibleRect;
    
    public void setAlignmentChartData(ArrayList<AlignmentChartData> chartDataList) {
        this.chartDataList = chartDataList;
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
            int length = chartDataList.get(0).normalised.length;
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
                
                int startNuc = (int) (visibleRect.x / AlignmentPanel.blockWidth);
                int endNuc = Math.min(length, (int) ((visibleRect.x + visibleRect.width) / AlignmentPanel.blockWidth) + 1);
                
                for (int c = 0; c < chartDataList.size(); c++) {
                    AlignmentChartData chartData = chartDataList.get(c);
                    if (chartData.chartType == AlignmentChartData.ChartType.DASHED_LINE) {
                        float[] d1 = {9.0f, 9.0f};
                        BasicStroke s1 = new BasicStroke(1.5f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10.0f, d1, 0f);
                        BasicStroke s2 = new BasicStroke(1.5f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10.0f, d1, 9f);
                        
                        GeneralPath path = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 10);
                        int mod = 50;
                        int startPath = (startNuc / mod) * mod;
                        int endPath = Math.min(chartData.normalised.length, ((int) Math.ceil((double) endNuc / (double) mod)) * mod);
                        boolean start = false;
                        for (int i = startPath; i < endPath; i++) {
                            if (chartData.normalised[i] != Double.MIN_VALUE) {                                
                                double x1 = i * AlignmentPanel.blockWidth - visibleRect.x;
                                double y1 = yoffset - (chartData.normalised[i] * height);
                                
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
                        
                        if (chartData.lineLabel != null) {
                            g2.setColor(Color.black);
                            for (int i = startNuc; i < endNuc; i++) {
                                if ((i + c * 2) % 25 == 0) {
                                    double x1 = i * AlignmentPanel.blockWidth - visibleRect.x;
                                    double y1 = yoffset - (chartData.normalised[i] * height);
                                    GraphicsUtils.drawStringCentred(g2, x1, y1, chartData.lineLabel);
                                }
                            }
                        }
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
