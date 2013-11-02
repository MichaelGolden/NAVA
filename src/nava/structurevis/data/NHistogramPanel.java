/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.structurevis.data;

import com.kitfox.svg.SVGException;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.*;
import nava.structurevis.SubstructureDrawPanel;
import nava.ui.MainFrame;
import nava.utils.ColorUtils;
import nava.utils.GraphicsUtils;
import net.hanjava.svg.SVG2EMF;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class NHistogramPanel extends JPanel implements ActionListener, MouseMotionListener, MouseListener {

    NHistogram histogram;
    double horizontalSpacing = 4;
    //double rightBorder = 0;
    double leftBorder = 70;
    double bottomBorder = 15;
    double topBorder = bottomBorder;
    public static Font titleFont = new Font("Arial", Font.BOLD, 12);
    public static Font normalFont = new Font("Arial", Font.PLAIN, 11);
    public static Font smallFont = new Font("Arial", Font.PLAIN, 10);
    public static DecimalFormat df = new DecimalFormat("0.00");
    public String nullText = "";
    JPopupMenu popupMenu = new JPopupMenu();
    JMenuItem saveAsPNGItem = new JMenuItem("Save as PNG (at current resolution)");
    JMenuItem saveAsSVGItem = new JMenuItem("Save as SVG");
    JMenuItem saveAsEMFItem = new JMenuItem("Save as EMF");

    public NHistogramPanel() {
        normalFont = MainFrame.fontLiberationSans.deriveFont(11f);
        smallFont = MainFrame.fontLiberationSans.deriveFont(10f);

        addMouseMotionListener(this);
        addMouseListener(this);

        ToolTipManager.sharedInstance().registerComponent(this);

        saveAsPNGItem.addActionListener(this);
        popupMenu.add(saveAsPNGItem);
        saveAsSVGItem.addActionListener(this);
        popupMenu.add(saveAsSVGItem);
        saveAsEMFItem.addActionListener(this);
        popupMenu.add(saveAsEMFItem);

    }

    public void setNHistogram(NHistogram histogram) {
        this.histogram = histogram;
        repaint();
    }

    public void setNullText(String nullText) {
        this.nullText = nullText;
    }
    ArrayList<ColumnRectangle> columns = new ArrayList<>();

    public String getSVGString() {
        Graphics g = getGraphics();
  

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        int width = 1000;
        int height = 600;
        if(g == null)
        {
            BufferedImage bimage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            g = bimage.getGraphics();
        }

        // initialise svg
        pw.println("<?xml version=\"1.0\" standalone=\"no\"?>");
        pw.println("<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\" \n\"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">");
        pw.println("<svg xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\" width=\"" + width + "\" height=\"" + height + "\" style=\"fill:none;stroke-width:16\">");

        pw.println("<g>");

        pw.println("</g>");

        if (histogram == null) {
            //
        } else {
            columns.clear();
            if (histogram.title != null) {
                topBorder = bottomBorder + 20;
                pw.println("<text x=\"" + (width / 2) + "\" y=\"" + (topBorder / 2 - 5) + "\" id=\"title\"  style=\"font-size:12;stroke:none;fill:#000000;text-anchor:middle;\"><tspan id=\"title\">" + histogram.title + "</tspan></text>");
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

                    //g.setColor(hist.transparentColor);
                    //Rectangle2D.Double rect = new Rectangle2D.Double(x + j * individualBarWidth, height - bottomBorder - barHeight, individualBarWidth, barHeight);
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
                    // ColumnRectangle columnRectangle = new ColumnRectangle(rect, mouseOverString);
                    //columns.add(columnRectangle);
                    pw.println("<rect  x=\"" + (x + j * individualBarWidth) + "\" y=\"" + (height - bottomBorder - barHeight) + "\" width=\"" + individualBarWidth + "\" height=\"" + barHeight + "\" id=\"bar\"  style=\"stroke:none;fill:" + GraphicsUtils.getRGBAString(hist.transparentColor) + ";\"/>");
                    //g.fill(rect);
                }
            }

            // draw legends
            for (int j = 0; j < histogram.classes.size(); j++) {
                NHistogramClass hist = histogram.classes.get(j);
                //g.setColor(hist.transparentColor);
                double y = startY + j * (legendItemHeight);
                //g.fill(new Rectangle2D.Double(leftBorder + graphWidth + legendItemLeftBorder, y + (legendItemHeight / 2) - (legendBlockHeight / 2), legendBlockWidth, legendBlockHeight));
                pw.println("<rect  x=\"" + (leftBorder + graphWidth + legendItemLeftBorder) + "\" y=\"" + (y + (legendItemHeight / 2) - (legendBlockHeight / 2)) + "\" width=\"" + legendBlockWidth + "\" height=\"" + legendBlockHeight + "\" id=\"legendblock\"  style=\"stroke:none;fill:" + GraphicsUtils.getRGBAString(hist.transparentColor) + ";\"/>");
                // g.setColor(Color.black);
                //g.setFont(normalFont);
                //GraphicsUtils.drawStringVerticallyCentred(g, leftBorder + graphWidth + legendItemLeftBorder + legendBlockWidth + 5, y + (legendItemHeight / 2), hist.name);
                pw.println("<text x=\"" + (leftBorder + graphWidth + legendItemLeftBorder + legendBlockWidth + 5) + "\" y=\"" + (y + (legendItemHeight / 2) + 5.5) + "\" id=\"legendtext\"  style=\"font-size:11;stroke:none;fill:#000000;text-anchor:left\"><tspan id=\"title\">" + hist.name + "</tspan></text>");

            }

            //g.setColor(Color.black);
            //g.draw(new Line2D.Double(leftBorder, height - bottomBorder / 2, endy, height - bottomBorder / 2));
            // draw x-axis


            // g.setFont(smallFont);
            if (histogram.transform != null) {
                double swidth = g.getFontMetrics().stringWidth("0.0000");
                
                
                //int points = Math.max(2, (int) (graphWidth / swidth / 1.7));
                int points = histogram.nbins+1;
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
                    //g.draw(new Line2D.Double(leftBorder + (y * graphWidth), height - bottomBorder - 1, leftBorder + (y * graphWidth), height - bottomBorder + 1));
                    pw.println("<rect  x1=\"" + (leftBorder + (y * graphWidth)) + "\" y1=\"" + (height - bottomBorder - 1) + "\" x2=\"" + (leftBorder + (y * graphWidth)) + "\" y2=\"" + (height - bottomBorder + 1) + "\" id=\"bar\"  style=\"stroke:none;fill:" + GraphicsUtils.getRGBAString(Color.black) + ";\"/>");
                    // GraphicsUtils.drawStringCentred(g, leftBorder + (y * graphWidth), height - bottomBorder / 2, histogram.transform.getFormattedString(val, 2));
                    pw.println("<text x=\"" + (leftBorder + (y * graphWidth)) + "\" y=\"" + (height - bottomBorder / 2 + 3) + "\" id=\"legendtext\"  style=\"font-size:11;stroke:none;fill:#000000;text-anchor:middle;vertical-align:top;\"><tspan id=\"title\">" + histogram.transform.getFormattedString(val, 2) + "</tspan></text>");
                }

            } else {
                double endy = leftBorder + histogram.nbins * (barWidth + horizontalSpacing);
                // GraphicsUtils.drawStringCentred(g, leftBorder, height - bottomBorder / 2, histogram.min + "");
                // GraphicsUtils.drawStringCentred(g, endy, height - bottomBorder / 2, histogram.max + "");
            }

            // g.setFont(normalFont);
            // draw y-axis
            double maxPerc = histogram.maxBinPerc;
            //GraphicsUtils.drawStringVerticallyCentred(g, leftBorder - 20 - g.getFontMetrics().stringWidth(df.format(maxPerc * 100) + "%"), topBorder, df.format(maxPerc * 100) + "%");
            // GraphicsUtils.drawStringVerticallyCentred(g, leftBorder - 20 - g.getFontMetrics().stringWidth(df.format(maxPerc / 2 * 100) + "%"), topBorder + graphHeight / 2, df.format(maxPerc / 2 * 100) + "%");
            // GraphicsUtils.drawStringVerticallyCentred(g, leftBorder - 20 - g.getFontMetrics().stringWidth("0%"), topBorder + graphHeight, "0%");
            pw.println("<text x=\"" + (leftBorder - 20 - g.getFontMetrics().stringWidth(df.format(maxPerc * 100) + "%")) + "\" y=\"" + topBorder + "\" id=\"legendtext\"  style=\"font-size:10;stroke:none;fill:#000000;text-anchor:right;\"><tspan id=\"title\">" + df.format(maxPerc * 100) + "%" + "</tspan></text>");
            pw.println("<text x=\"" + (leftBorder - 20 - g.getFontMetrics().stringWidth(df.format(maxPerc / 2 * 100) + "%")) + "\" y=\"" + (topBorder + graphHeight / 2) + "\" id=\"legendtext\"  style=\"font-size:10;stroke:none;fill:#000000;text-anchor:right;\"><tspan id=\"title\">" + df.format(maxPerc / 2 * 100) + "%" + "</tspan></text>");
            pw.println("<text x=\"" + (leftBorder - 20 - g.getFontMetrics().stringWidth("0%")) + "\" y=\"" + (topBorder + graphHeight) + "\" id=\"legendtext\"  style=\"font-size:10;stroke:none;fill:#000000;text-anchor:right;\"><tspan id=\"title\">" + "0%" + "</tspan></text>");



            // draw graph borders
            // g.draw(new Line2D.Double(leftBorder, height - bottomBorder, leftBorder + graphWidth, height - bottomBorder));
            //g.draw(new Line2D.Double(leftBorder, topBorder, leftBorder, height - bottomBorder));
            pw.println("<line  x1=\"" + (leftBorder) + "\" y1=\"" + (height - bottomBorder) + "\" x2=\"" + (leftBorder + graphWidth) + "\" y2=\"" + (height - bottomBorder) + "\" id=\"bar\"  style=\"stroke:" + GraphicsUtils.getRGBAString(Color.black) + ";stroke-width:1;\"/>");
            pw.println("<line  x1=\"" + leftBorder + "\" y1=\"" + topBorder + "\" x2=\"" + leftBorder + "\" y2=\"" + (height - bottomBorder) + "\" id=\"bar\"  style=\"stroke:" + GraphicsUtils.getRGBAString(Color.black) + ";stroke-width:1;\"/>");

            // draw medians
            float dash[] = {2.0f};
            for (int j = 0; j < histogram.classes.size(); j++) {
                // g.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, (float) j / (float) histogram.classes.size() * 2.0f));
                NHistogramClass hist = histogram.classes.get(j);
                // g.setColor(hist.color.darker());
                if (histogram.transform != null) {
                    double medianx = histogram.transform.transform(hist.median);
                    // g.draw(new Line2D.Double(leftBorder + medianx * graphWidth, height - bottomBorder, leftBorder + medianx * graphWidth, topBorder));
                    pw.println("<line  x1=\"" + (leftBorder + medianx * graphWidth) + "\" y1=\"" + (height - bottomBorder) + "\" x2=\"" + (leftBorder + medianx * graphWidth) + "\" y2=\"" + topBorder + "\" id=\"medianline\"  style=\"stroke:" + GraphicsUtils.getRGBAString(hist.color.darker()) + ";stroke-width:1;\" stroke-dasharray=\"" + dash[0] + "," + dash[0] + "\"/>");
                }
            }
        }

        pw.println("</svg>");
        pw.close();
        //System.out.println(sw.toString());
        return sw.toString();
    }

    public void drawHistogram(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        g.setColor(Color.white);
        g.fillRect(0, 0, width, height);

        if (histogram == null) {
            g.setColor(Color.black);
            GraphicsUtils.drawStringCentred(g, width / 2, height / 2, nullText);
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

            // draw legends
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

            // draw graph borders
            g.draw(new Line2D.Double(leftBorder, height - bottomBorder, leftBorder + graphWidth, height - bottomBorder));
            g.draw(new Line2D.Double(leftBorder, topBorder, leftBorder, height - bottomBorder));

            // draw medians
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
    public void paintComponent(Graphics graphics) {
        drawHistogram(graphics);
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

    public void saveAsSVG(File file) throws IOException {
        BufferedWriter buffer = new BufferedWriter(new FileWriter(file));
        buffer.write(getSVGString());
        buffer.close();
    }

    public void saveAsEMF(File file) throws IOException {
        File tempFile = new File("temp.svg");
        saveAsSVG(tempFile);
        String svgUrl = "file:///" + tempFile.getAbsolutePath();
        SVG2EMF.convert(svgUrl, file);
    }

    public void saveAsPNG(File file) {
        int panelWidth = getWidth();
        int panelHeight = getHeight();
        double zoomScale = 1;
        Dimension d = new Dimension((int) Math.ceil(panelWidth * zoomScale), (int) Math.ceil(panelHeight * zoomScale));

        BufferedImage tempImage = null;
        tempImage = (BufferedImage) (this.createImage((int) (d.width), (int) (d.height)));
        Graphics2D g = (Graphics2D) tempImage.getGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        //g.scale(zoomScale, zoomScale);
        g.setColor(Color.white);
        g.fillRect(0, 0, (int) (panelWidth * zoomScale), (int) (panelHeight * zoomScale));

        this.drawHistogram(tempImage.getGraphics());

        if (tempImage != null) {
            try {
                ImageIO.write(tempImage, "png", file);
            } catch (IOException ex) {
                Logger.getLogger(NHistogramPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(saveAsPNGItem)) {
            String name = "histogram";
            MainFrame.saveDialog.setDialogTitle("Save as PNG");
            MainFrame.saveDialog.setSelectedFile(new File(MainFrame.saveDialog.getCurrentDirectory().getPath() + "/" + name + ".png"));
            int returnVal = MainFrame.saveDialog.showSaveDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                saveAsPNG(MainFrame.saveDialog.getSelectedFile());
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }
        } else if (e.getSource().equals(saveAsSVGItem)) {
            MainFrame.saveDialog.setDialogTitle("Save as SVG");
            String name = "histogram";
            MainFrame.saveDialog.setSelectedFile(new File(MainFrame.saveDialog.getCurrentDirectory().getPath() + "/" + name + ".svg"));
            int returnVal = MainFrame.saveDialog.showSaveDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                try {
                    saveAsSVG(MainFrame.saveDialog.getSelectedFile());
                } catch (IOException ex) {
                    Logger.getLogger(NHistogramPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }
        } else if (e.getSource().equals(saveAsEMFItem)) {
            MainFrame.saveDialog.setDialogTitle("Save as EMF");
            String name = "histogram";
            MainFrame.saveDialog.setSelectedFile(new File(MainFrame.saveDialog.getCurrentDirectory().getPath() + "/" + name + ".emf"));
            int returnVal = MainFrame.saveDialog.showSaveDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                try {
                    saveAsEMF(MainFrame.saveDialog.getSelectedFile());
                } catch (IOException ex) {
                    Logger.getLogger(NHistogramPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
            this.popupMenu.show(this, e.getX(), e.getY());
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
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
