/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.structurevis;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import nava.utils.GraphicsUtils;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class StructurePanel extends javax.swing.JPanel {

    int[] pairedSites;
    int start;
    int end;
    
    Point2D.Double[] nucleotidePositions;
    
    double zoomScale = 1;
    
    double nucleotideDiameter = 40;
    
    boolean showBonds = true;
    Color bondColor = Color.lightGray;
    int bondThickness = 4;

    /**
     * Creates new form StructurePanel
     */
    public StructurePanel() {
        initComponents();
    }

    public void setStructure(int[] pairedSites, int start, int end) {
        this.pairedSites = pairedSites;
        this.start = start;
        this.end = end;
        computeStructureLayout(pairedSites);
    }
    
    double xoffset;
    double minx;
    double miny;
    double maxx;
    double maxy;
    double horizontalScale = 2.6;
    double verticalScale = 2.6;    
    
    public void computeStructureLayout(int [] pairedSites) {
        if (pairedSites == null) {
            return;
        }

        //np = mainapp.getStructureCoordinates(structure.getDotBracketString());
        ArrayList<Point2D.Double> np = NAView.naview_xy_coordinates(pairedSites);

        minx = Double.MAX_VALUE;
        miny = Double.MAX_VALUE;
        maxx = Double.MIN_VALUE;
        maxy = Double.MIN_VALUE;

        for (int i = 0; i < np.size(); i++) {
            Point2D.Double pos = np.get(i);
            minx = Math.min(minx, pos.x);
            miny = Math.min(miny, pos.y);
            maxx = Math.max(maxx, pos.x);
            maxy = Math.max(maxy, pos.y);
        }
        nucleotidePositions = new Point2D.Double[np.size()];
        for (int i = 0; i < nucleotidePositions.length; i++) {
            nucleotidePositions[i] = new Point2D.Double();
            nucleotidePositions[i].x = xoffset + (np.get(i).x - minx) * horizontalScale;
            nucleotidePositions[i].y = 50 + (np.get(i).y - miny) * verticalScale;
        }

    }

    BufferedImage bufferedImage = null;
    Graphics2D g = null;
    public void drawStructureNative() {
        if (pairedSites == null || nucleotidePositions == null) {
            return;
        }
        int panelWidth = (int) ((maxx - minx) * horizontalScale + xoffset * 2);
        int panelHeight = (int) ((maxy - miny) * verticalScale + 50);
        Dimension d = new Dimension((int) (panelWidth * zoomScale), (int) (panelHeight * zoomScale));

        try {
            if (d.width > 0 && d.height > 0) {
                if ((bufferedImage == null || d.width != bufferedImage.getWidth() || d.height != bufferedImage.getHeight())) {
                    bufferedImage = (BufferedImage) (this.createImage(d.width, d.height));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        }

        if (bufferedImage == null) {
            return;
        }

        g = (Graphics2D) bufferedImage.getGraphics();
        //g = (Graphics2D) graphics;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.scale(zoomScale, zoomScale);

        g.setColor(Color.white);
        g.fillRect(0, 0, panelWidth, panelHeight);


        // draw the base pair interactions
        /*
        if (showBonds) {
            for (int i = 0; i < nucleotidePositions.length; i++) {
                int a = structure.pairedSites[0][i] - structure.pairedSites[0][0];
                int b = structure.pairedSites[1][i] - structure.pairedSites[0][0];
                if (structure.pairedSites[0][i] < structure.pairedSites[1][i]) {
                    Line2D bond = new Line2D.Double(nucleotidePositions[a], nucleotidePositions[b]);
                    g.setStroke(new BasicStroke(bondThickness));
                    g.setColor(bondColor);
                    g.draw(bond);
                }
            }
        }
        */

        /*
        g.setColor(Color.black);
        int length = structure.length;
        covariationInteractions.clear();
        if (show2DData && mainapp.data2D != null) {
            for (int i = structure.getStartPosition(); i <= structure.getEndPosition(); i++) {
                for (int j = structure.getStartPosition(); j <= structure.getEndPosition(); j++) {
                    int k = i - structure.getStartPosition();
                    int l = j - structure.getStartPosition();
                    if (mainapp.maxDistance == -1 || (structureDistanceMatrix != null && structureDistanceMatrix.getDistance(k, l) <= mainapp.maxDistance) || (structureDistanceMatrix == null && mainapp.distanceMatrix.getDistance(i - 1, j - 1) <= mainapp.maxDistance)) {
                        Color c = null;
                        double p = mainapp.data2D.matrix.get(i - 1, j - 1);
                        if (p == mainapp.data2D.matrix.emptyValue) {
                            c = null;
                        } else if (((!mainapp.useLowerThreshold2D || p >= mainapp.thresholdMin2D) && (!mainapp.useUpperThreshold2D || p <= mainapp.thresholdMax2D))) {
                            //  Sy
                            if (mainapp.data2D != null) {
                                //System.out.println(p);
                                c = mainapp.data2D.colorGradientSecondary.getColor((float) mainapp.data2D.dataTransform.transform(p));
                            }
                        }

                        if (c != null) {
                            double x1 = nucleotidePositions[k].getX();
                            double y1 = nucleotidePositions[k].getY();
                            double x2 = nucleotidePositions[l].getX();
                            double y2 = nucleotidePositions[l].getY();

                            Shape shape = null;
                            int structureMidpoint = structure.getStartPosition() + (structure.length / 2);

                            if (i <= structureMidpoint && j <= structureMidpoint) { // both on left side
                                double x1p = Math.max(x1 - Math.abs((y1 - y2) / 2), 0);
                                shape = new QuadCurve2D.Double(x1, y1, x1p, (y1 + y2) / 2, x2, y2);
                            } else if (i > structureMidpoint && j > structureMidpoint) { // both on right side
                                double x2p = Math.min(x2 + Math.abs((y1 - y2) / 2), panelWidth);
                                shape = new QuadCurve2D.Double(x1, y1, x2p, (y1 + y2) / 2, x2, y2);
                            } else {
                                shape = new Line2D.Double(nucleotidePositions[k], nucleotidePositions[l]);
                            }
                            covariationInteractions.add(new Interaction(shape, i, j));

                            g.setColor(c);
                            g.setStroke(normalStroke);
                            g.draw(shape);

                            g.setColor(Color.black);
                            g.setStroke(new BasicStroke());
                        }
                    }
                }
            }
        }
        */

    
        // draw the nucleotides
        for (int i = 0; i < nucleotidePositions.length; i++) {
            //int pos = (structure.startPosition + i - 1) % mainapp.structureCollection.genomeLength;
            Ellipse2D stemNucleotide = GraphicsUtils.getCircleCenteredAt(nucleotidePositions[i].getX(), nucleotidePositions[i].getY(), nucleotideDiameter);
            g.setColor(Color.white);
            /*
            Color nucleotideBackgroundColor = mainapp.missingDataColor;
            if (oneDimensionalData == SHOW && mainapp.data1D != null && mainapp.data1D.used[pos]) {
                double p = mainapp.data1D.data[pos];
                if (mainapp.data1D.used[pos] && ((!mainapp.useLowerThreshold1D || p >= mainapp.thresholdMin1D) && (!mainapp.useUpperThreshold1D || p <= mainapp.thresholdMax1D))) {
                    nucleotideBackgroundColor = mainapp.data1D.colorGradientSecondary.getColor(mainapp.data1D.dataTransform.transform((float) p));
                } else if (!((!mainapp.useLowerThreshold1D || p >= mainapp.thresholdMin1D) && (!mainapp.useUpperThreshold1D || p <= mainapp.thresholdMax1D))) {
                    nucleotideBackgroundColor = mainapp.filteredDataColor;
                }
                g.setColor(nucleotideBackgroundColor);
                g.fill(stemNucleotide);
                g.setColor(Color.black);
                // drawStringCentred(g, nucleotidePositions[i].getX(), nucleotidePositions[i].getY()+10, val.toString());
            } else {
                g.setColor(nucleotideBackgroundColor);
                g.fill(stemNucleotide);
            }*/

            g.setColor(Color.black);
            g.draw(stemNucleotide);
            g.setStroke(new BasicStroke());

            /*
            // draw the information
            g.setColor(ColorTools.selectBestForegroundColor(nucleotideBackgroundColor, Color.white, Color.black));
            if (mainapp.nucleotideComposition != null) {
                if (mainapp.nucleotideCompositionType == NucleotideCompositionType.SHANNON) {
                    if (pos < mainapp.nucleotideComposition.mappedShannonComposition.length) {
                        double[] fa = new double[4];
                        for (int k = 0; k < fa.length; k++) // java Arrays.copy causes fatal error
                        {
                            fa[k] = mainapp.nucleotideComposition.mappedShannonComposition[pos][k];
                            if (Double.isNaN(fa[k])) {
                                fa[k] = 0; // java crashes fatally if this is not done
                            }
                        }
                        for (int k = 0; k < 4; k++) {
                            fa[k] = fa[k] / 2;
                        }
                        drawSequenceLogo(g, nucleotidePositions[i].getX(), nucleotidePositions[i].getY() - (nucleotideDiameter / 2) + 0, nucleotideDiameter, nucleotideDiameter - 5, fa);
                        g.setFont(f2);
                    }
                } else if (mainapp.nucleotideCompositionType == NucleotideCompositionType.FREQUENCY) {
                    int index = (structure.startPosition + i - 1) % mainapp.structureCollection.genomeLength;
                    if (index < mainapp.nucleotideComposition.mappedFrequencyComposition.length) {
                        double[] fa = mainapp.nucleotideComposition.mappedFrequencyComposition[index];
                        drawSequenceLogo(g, nucleotidePositions[i].getX(), nucleotidePositions[i].getY() - (nucleotideDiameter / 2) + 0, nucleotideDiameter, nucleotideDiameter - 5, fa);
                        g.setFont(f2);
                    }
                }
            }
            */
        }

        /*
        // draw position lines
        for (int i = 0; i < nucleotidePositions.length; i++) {
            int offsetx = 0;
            double side = 1;
            if (i < length / 2) {
                offsetx = -(int) (nucleotideDiameter - 3);

                side = -1;
            } else {
                offsetx = (int) (nucleotideDiameter - 3);
            }

            if (nucleotidePositions[i] != null) {
                g.setColor(Color.black);
                g.setFont(f2);
                int pos = (structure.getStartPosition() + i - 1) % mainapp.structureCollection.genomeLength + 1;
                if (mainapp.numbering != 0 && pos % mainapp.numbering == 0) {
                    drawStringCentred(g, offsetx + nucleotidePositions[i].getX(), nucleotidePositions[i].getY() - 2, "" + pos);
                    g.setColor(Color.black);
                    g.draw(new Line2D.Double(nucleotidePositions[i].getX() + (side * nucleotideDiameter / 2) - 2, nucleotidePositions[i].getY(), nucleotidePositions[i].getX() + (side * nucleotideDiameter / 2) + 2, nucleotidePositions[i].getY()));
                }
            }
        }
        */
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
