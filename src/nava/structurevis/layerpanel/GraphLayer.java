/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.structurevis.layerpanel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import nava.structurevis.StructureVisController;
import nava.structurevis.data.DataOverlay1D;
import nava.ui.MainFrame;
import nava.ui.ProjectController;
import nava.utils.GraphicsUtils;
import nava.utils.Mapping;

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
    public Mapping data1Dmapping;
    int currentWindowIndex = 4;
    public Layer parent;
    StructureVisController structureVisController;
    ProjectController projectController;
    JMenuItem saveAsSVGItem = new JMenuItem("Save as SVG");

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

        saveAsSVGItem.addActionListener(this);
        popupMenu.add(saveAsSVGItem);
    }

    public void setDataOverlay1D(DataOverlay1D dataOverlay1D) {
        setData(dataOverlay1D, windowSizes[currentWindowIndex]);
        redraw();
    }

    public void setData(DataOverlay1D dataOverlay1D, int windowSize) {
        this.dataOverlay1D = dataOverlay1D;
        setSlidingWindow(windowSize);
    }

    public void setSlidingWindow(int windowSize) {
        if (dataOverlay1D != null && structureVisController.structureVisModel.substructureModel.structureOverlay != null) {
            data1Dmapping = structureVisController.getMapping(structureVisController.structureVisModel.substructureModel.structureOverlay.mappingSource, dataOverlay1D.mappingSource);

            slidingWindowData = new double[data1Dmapping.getALength()];
            for (int i = 0; i < slidingWindowData.length; i++) {
                double sum = 0;
                double count = 0;
                for (int j = 0; j < windowSize; j++) {
                    // int x = i - (windowSize / 2) + j;
                    int x = data1Dmapping.aToB(i - (windowSize / 2) + j);
                   // System.out.println((i - (windowSize / 2) + j) + " -> " + x +"  "+data1Dmapping.getBLength()+"  "+dataOverlay1D.used.length);
                    if (x >= 0 && x < dataOverlay1D.used.length) {
                        if (dataOverlay1D.used[x]) {
                            sum += dataOverlay1D.data[x];
                            count++;
                        }
                    }
                }
                int x = data1Dmapping.aToB(i);
                if (x >= 0 && x < dataOverlay1D.used.length && dataOverlay1D.used[x] && count > 0) {
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
            if (dataOverlay1D != null && slidingWindowData != null) {
                for (int i = 0; i < getWidth(); i++) {

                    int coordinate = (int) (((double) i / (double) getWidth()) *  slidingWindowData.length);
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
        if (e.getSource().equals(saveAsSVGItem)) {
            MainFrame.saveDialog.setDialogTitle("Save as SVG");
            String name = "layer";
            MainFrame.saveDialog.setSelectedFile(new File(MainFrame.saveDialog.getCurrentDirectory().getPath() + "/" + name + ".svg"));
            int returnVal = MainFrame.saveDialog.showSaveDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                try {
                    saveAsSVG(MainFrame.saveDialog.getSelectedFile());
                } catch (IOException ex) {
                    Logger.getLogger(GraphLayer.class.getName()).log(Level.SEVERE, null, ex);
                }
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }
        } else {

            for (int i = 0; i < windowSizes.length; i++) {
                if (e.getSource().equals(slidingWindowItems[i])) {
                    currentWindowIndex = i;
                    setData(dataOverlay1D, windowSizes[i]);
                    redraw();
                    break;
                }
            }
        }

    }

    public void saveAsSVG(File file) throws IOException {
        BufferedWriter buffer = new BufferedWriter(new FileWriter(file));
        buffer.write(getSVG());
        buffer.close();
    }

    public String getSVG() {

        int panelWidth = 1000;
        //int panelWidth = this.getWidth();
        int panelHeight = this.getHeight();


        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        pw.println("<?xml version=\"1.0\" standalone=\"no\"?>");
        pw.println("<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\" \n\"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">");
        pw.println("<svg xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\" width=\"" + panelWidth + "\" height=\"" + panelHeight + "\" style=\"fill:none;stroke-width:16\">");

        pw.println("<g>");
        pw.println("<rect x=\"" + (0) + "\" y=\"" + (0) + "\" width=\"" + (panelWidth) + "\" height=\"" + (panelHeight) + "\"  style=\"fill:#" + GraphicsUtils.getHexString(Color.white) + ";\"/>");
        
        
        if (dataOverlay1D != null) {
            for (int i = 0; i < panelWidth; i++) {

                int coordinate = (int) (((double) i / (double) panelWidth) * slidingWindowData.length);
                //System.out.println("i="+i+"\t"+coordinate);
                if (dataOverlay1D != null && coordinate < slidingWindowData.length) {
                    float x = (float) slidingWindowData[coordinate];
                    if (x != -1) {
                        Color c = dataOverlay1D.colorGradient.getColor(dataOverlay1D.dataTransform.transform(x));
                        g.setColor(c);

                        g.fillRect(i, 0, 1, panelHeight);
                        pw.println("<rect x=\"" + (i) + "\" y=\"" + (0) + "\" width=\"" + (2) + "\" height=\"" + (panelHeight) + "\"  style=\"fill:#" + GraphicsUtils.getHexString(c) + ";\"/>");

                    }
                }
            }
        }
         pw.println("</g>");

        pw.println("</svg>");
        pw.close();
        //System.out.println(sw.toString());
        return sw.toString();
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
