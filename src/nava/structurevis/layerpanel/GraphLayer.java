/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.structurevis.layerpanel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import javax.swing.*;
import nava.structurevis.StructureVisController;
import nava.structurevis.data.DataOverlay1D;
import nava.ui.ProjectController;

/**
 *
 * @author Michael Golden
 */
public class GraphLayer extends JPanel implements ActionListener, MouseListener {

    Graphics2D g = null;
    double[] slidingWindowData;
    JPopupMenu popupMenu = new JPopupMenu();
    JMenu slidingWindowMenu = new JMenu("Sliding window size");
    private static final int[] windowSizes = {5, 10, 20, 30, 50, 75, 100, 150, 200};
    ButtonGroup slidingWindowGroup = new ButtonGroup();
    JRadioButtonMenuItem[] slidingWindowItems = new JRadioButtonMenuItem[windowSizes.length];
    public DataOverlay1D dataOverlay1D;
    int currentWindowIndex = 4;
    public Layer parent;
    StructureVisController structureVisController;
    ProjectController projectController;

    public GraphLayer(Layer parent, StructureVisController structureVisController, ProjectController projectController) {

        this.parent = parent;
        this.structureVisController = structureVisController;
        this.projectController = projectController;

        this.setPreferredSize(new Dimension(1000, 20));
        addMouseListener(this);
        for (int i = 0; i < slidingWindowItems.length; i++) {
            slidingWindowItems[i] = new JRadioButtonMenuItem(windowSizes[i] + "");
            slidingWindowItems[i].addActionListener(this);
            slidingWindowGroup.add(slidingWindowItems[i]);
            slidingWindowMenu.add(slidingWindowItems[i]);
        }
        slidingWindowItems[currentWindowIndex].setSelected(true);
        popupMenu.add(slidingWindowMenu);
    }

    public void setDataOverlay1D(DataOverlay1D dataOverlay1D) {
        setData(dataOverlay1D, windowSizes[currentWindowIndex]);
        redraw();
        System.out.println("setDataOverlay1D 2" + dataOverlay1D);
    }

    public void setData(DataOverlay1D dataOverlay1D, int windowSize) {
        this.dataOverlay1D = dataOverlay1D;
        setSlidingWindow(windowSize);
    }

    public void setSlidingWindow(int windowSize) {
        if (dataOverlay1D != null) {
            slidingWindowData = new double[dataOverlay1D.data.length];
            for (int i = 0; i < slidingWindowData.length; i++) {
                double sum = 0;
                double count = 0;
                for (int j = 0; j < windowSize; j++) {
                    int x = i - (windowSize / 2) + j;
                    if (x >= 0 && x < slidingWindowData.length) {
                        if (dataOverlay1D.used[x]) {
                            sum += dataOverlay1D.data[x];
                            count++;
                        }
                    }
                }
                if (dataOverlay1D.used[i] && count > 0) {
                    slidingWindowData[i] = sum / count;
                } else {
                    slidingWindowData[i] = -1;
                }
            }
        }
    }
    boolean forceRepaint = true;

    public void redraw() {
        forceRepaint = true;
        repaint();
        System.out.println("redrawing");
    }
    BufferedImage bufferedImage = null;

    @Override
    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Graphics2D g2 = (Graphics2D) graphics;

        int panelWidth = this.getWidth();
        int panelHeight = this.getHeight();

        if (bufferedImage == null || bufferedImage.getWidth() != panelWidth || bufferedImage.getHeight() != panelHeight) {
            bufferedImage = (BufferedImage) (this.createImage(panelWidth, panelHeight));
            g = (Graphics2D) bufferedImage.getGraphics();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            forceRepaint = true;
        }

        if (forceRepaint) {

            forceRepaint = false;

            g.setColor(Color.white);
            g.fillRect(0, 0, panelWidth, panelHeight);

            // draw 1D data
            if (dataOverlay1D != null) {
                for (int i = 0; i < getWidth(); i++) {

                    int coordinate = (int) (((double) i / (double) getWidth()) * dataOverlay1D.data.length);
                    //System.out.println("i="+i+"\t"+coordinate);
                    if (dataOverlay1D != null && coordinate < slidingWindowData.length) {
                        float x = (float) slidingWindowData[coordinate];
                        if (x != -1) {
                            Color c = dataOverlay1D.colorGradient.getColor(dataOverlay1D.dataTransform.transform(x));
                            g.setColor(c);

                            //System.out.println(c);
                            g.fillRect(i, 0, 1, getHeight());
                            //g.fillRect(i + xoffset, 0, 1, getHeight());
                        }
                    }
                }
            }
        }

        g2.drawImage(bufferedImage, 0, 0, this);
    }

    public void mouseClicked(MouseEvent e) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    public void mousePressed(MouseEvent e) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    public void mouseReleased(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
            this.popupMenu.show(this, e.getX(), e.getY());
        }
    }

    public void mouseEntered(MouseEvent e) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    public void mouseExited(MouseEvent e) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    public void actionPerformed(ActionEvent e) {
        for (int i = 0; i < windowSizes.length; i++) {
            if (e.getSource().equals(slidingWindowItems[i])) {
                currentWindowIndex = i;
                setData(dataOverlay1D, windowSizes[i]);
                redraw();
                break;
            }
        }
    }
    /*
     * @Override public Object clone() throws CloneNotSupportedException {
     * GraphLayer layer = new GraphLayer(layerPanel, data1D, name); layer.canPin
     * = canPin; layer.isPinned = isPinned; return layer; }
     */

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj)) {
            final GraphLayer other = (GraphLayer) obj;
            if (this.dataOverlay1D == null && other.dataOverlay1D == null) {
                return true;
            }
            if (!this.dataOverlay1D.equals(other.dataOverlay1D)) {
                return false;
            }
            if (this.currentWindowIndex != other.currentWindowIndex) {
                return false;
            }
            return true;
        }
        return false;
    }
}
