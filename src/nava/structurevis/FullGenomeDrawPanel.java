/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.structurevis;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import nava.structurevis.data.*;
import nava.structurevis.data.PersistentSparseMatrix.Element;
import nava.utils.ColorUtils;
import nava.utils.GraphicsUtils;
import nava.utils.RNAFoldingTools;

/**
 *
 * @author Michael Golden
 */
public class FullGenomeDrawPanel extends JPanel implements ActionListener, MouseListener, MouseMotionListener, StructureVisView {

    Point2D.Double[] fullCoordinates;
    ArrayList<CoordinatesAndNucleotidePosition> coordinatesAndPosMinList; // left-most x-coordinates in ascending order of index, for optimal binary search
    ArrayList<Double> sortedXPositionMinList;
    ArrayList<CoordinatesAndNucleotidePosition> coordinatesAndPosMaxList; // right-most x-coordinates in ascending order of index, for optimal binary search
    ArrayList<Double> sortedXPositionMaxList;
    int[] substructures;
    double width = 0;
    double basePosY = 0;
    double lowestPosY = Double.MIN_VALUE;
    boolean forceRepaint = true;
    final static BasicStroke normalStroke = new BasicStroke(2.5f);
    double nucleotideRadius = 6;
    double nucleotideDiameter = nucleotideRadius * 2;
    SubstructureModel substructureModel;
    StructureOverlay structureOverlay;
    JPopupMenu popupMenu = new JPopupMenu();
    JMenuItem gotoStructureItem = new JMenuItem();
    int gotoStructure = -1;
    StructureVisController structureVisController;
    int maxSubstructureSize = 150;

    public FullGenomeDrawPanel(StructureVisController structureVisController) {
        this.substructureModel = structureVisController.structureVisModel.substructureModel;

        initialise(substructureModel.structureOverlay, maxSubstructureSize);
        structureVisController.addView(this);

        addMouseListener(this);
        addMouseMotionListener(this);

        gotoStructureItem.addActionListener(this);
        popupMenu.add(gotoStructureItem);
    }

    public int getMaxEndPosition(ArrayList<Substructure> substructures) {
        int max = 0;
        for (int i = 0; i < substructures.size(); i++) {
            max = Math.max(substructures.get(i).getEndPosition(), max);
        }
        return max;
    }

    public void initialise(StructureOverlay structureOverlay, int maxSubstructureSize) {
        if (this.structureOverlay == structureOverlay && this.maxSubstructureSize == maxSubstructureSize) {
            return;
        }

        this.structureOverlay = structureOverlay;
        this.maxSubstructureSize = maxSubstructureSize;

        if (structureOverlay != null) {
            structureOverlay.loadData();

            ArrayList<Substructure> substructures = StructureOverlay.enumerateAdjacentSubstructures(structureOverlay.pairedSites, 0, maxSubstructureSize, structureOverlay.circular);
            fullCoordinates = new Point2D.Double[Math.max(structureOverlay.pairedSites.length, getMaxEndPosition(substructures))];
            this.substructures = new int[fullCoordinates.length];

            //fullCoordinates = new Point2D.Double[consensusStructure.length()];
            //structures = new int[consensusStructure.length()];
            coordinatesAndPosMinList = new ArrayList<>();
            coordinatesAndPosMaxList = new ArrayList<>();
            sortedXPositionMinList = new ArrayList<>();
            sortedXPositionMaxList = new ArrayList<>();
            Arrays.fill(this.substructures, -1);
            double offsetX = 0;
            int lastEndIndex = 0;
            double pairedDistance = 15;
            double unpairedLength = 100;
            double maxXcoordinate = 0;

            // determine full genome structure coordinates

            for (int i = 0; i < substructures.size(); i++) {
                int[] subpairedsites = RNAFoldingTools.getPairedSitesFromDotBracketString(RNAFoldingTools.getDotBracketStringFromPairedSites(substructures.get(i).pairedSites));

                ArrayList<Point2D.Double> coordinates = NAView.naview_xy_coordinates(subpairedsites);
                //ArrayList<Point2D.Double> coordinates = mainapp.getStructureCoordinates(substructures.get(i).getDotBracketString());
                Point2D.Double[] normalisedCoordinates = normaliseStructureCoordinates(coordinates);
                int startIndex = substructures.get(i).startPosition;

                Arrays.fill(this.substructures, startIndex, startIndex + substructures.get(i).length, i);
                offsetX = maxXcoordinate + unpairedLength; // offset for next substructure
                double maxY = normalisedCoordinates[0].y;


                // get substructure coordinates
                for (int j = 0; j < normalisedCoordinates.length; j++) {

                    fullCoordinates[startIndex + j] = new Point2D.Double();
                    fullCoordinates[startIndex + j].x = offsetX + normalisedCoordinates[j].x;
                    fullCoordinates[startIndex + j].y = normalisedCoordinates[j].y - maxY;
                    maxXcoordinate = Math.max(maxXcoordinate, fullCoordinates[startIndex + j].x);
                    width = Math.max(width, fullCoordinates[startIndex + j].x);
                    basePosY = Math.max(basePosY, normalisedCoordinates[j].y);
                    lowestPosY = Math.max(fullCoordinates[startIndex + j].y, lowestPosY);

                    coordinatesAndPosMinList.add(new CoordinatesAndNucleotidePosition(fullCoordinates[startIndex + j], startIndex + j));
                    coordinatesAndPosMaxList.add(new CoordinatesAndNucleotidePosition(fullCoordinates[startIndex + j], startIndex + j));
                    lastEndIndex = startIndex + j;
                }
                lastEndIndex += 1;

                int c = 1;
                for (int j = lastEndIndex; (i + 1) < substructures.size() && j < substructures.get(i + 1).startPosition; j++) {
                    fullCoordinates[j] = new Point2D.Double();
                    fullCoordinates[j].x = fullCoordinates[startIndex].x + (c * pairedDistance) + 30;
                    maxXcoordinate = Math.max(maxXcoordinate, fullCoordinates[j].x);
                    fullCoordinates[j].y = 0;
                    c++;
                    coordinatesAndPosMinList.add(new CoordinatesAndNucleotidePosition(fullCoordinates[j], j));
                    coordinatesAndPosMaxList.add(new CoordinatesAndNucleotidePosition(fullCoordinates[j], j));
                }
            }


            basePosY += pairedDistance;
            lowestPosY += pairedDistance;

            for (int i = 0; i < coordinatesAndPosMinList.size(); i++) {
                for (int j = i + 1; j < coordinatesAndPosMinList.size(); j++) {
                    if (coordinatesAndPosMinList.get(i).p.x >= coordinatesAndPosMinList.get(j).p.x) {
                        coordinatesAndPosMinList.remove(j);
                        j--;
                    }
                }
            }


            for (int i = 0; i < coordinatesAndPosMaxList.size(); i++) {
                for (int j = i + 1; j < coordinatesAndPosMaxList.size(); j++) {
                    if (coordinatesAndPosMaxList.get(coordinatesAndPosMaxList.size() - i - 1).p.x <= coordinatesAndPosMaxList.get(coordinatesAndPosMaxList.size() - j - 1).p.x) {
                        coordinatesAndPosMaxList.remove(coordinatesAndPosMaxList.size() - j - 1);
                        j--;
                    }
                }
            }

            for (int i = 0; i < coordinatesAndPosMinList.size(); i++) {
                sortedXPositionMinList.add(coordinatesAndPosMinList.get(i).p.x);
            }

            for (int i = 0; i < coordinatesAndPosMaxList.size(); i++) {
                sortedXPositionMaxList.add(coordinatesAndPosMaxList.get(i).p.x);
            }


            setPreferredSize(new Dimension((int) (width + 1), (int) (basePosY + lowestPosY + 1)));
        }
    }

    public void redraw() {
        forceRepaint = true;
        repaint();
    }

    @Override
    public void paintComponent(Graphics graphics) {

        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        Rectangle viewableArea = this.getVisibleRect();

        setPreferredSize(new Dimension((int) (width + 1), (int) (basePosY + lowestPosY + 1)));

        g.setColor(Color.white);
        g.fillRect(viewableArea.x, viewableArea.y, viewableArea.width, viewableArea.height);
        
        if(substructureModel.structureOverlay == null || substructureModel.structureOverlay.pairedSites == null)
        {
            return;
        }

        int lessThanVisibleX = Collections.binarySearch(sortedXPositionMinList, (double) viewableArea.x);


        if (lessThanVisibleX < 0) {
            lessThanVisibleX = -lessThanVisibleX - 1;
        }
        lessThanVisibleX = Math.min(lessThanVisibleX, coordinatesAndPosMinList.size() - 1);
        int startDrawingFromNucleotide = Math.max(coordinatesAndPosMinList.get(Math.max(lessThanVisibleX, 0)).nucleotidePosition - 1, 0);

        int moreThanVisibleX = Collections.binarySearch(sortedXPositionMaxList, (double) (viewableArea.x + viewableArea.width));
        if (moreThanVisibleX < 0) {
            moreThanVisibleX = -moreThanVisibleX - 1;
        }
        int endDrawingAtNucleotide = Math.min(coordinatesAndPosMaxList.get(Math.min(moreThanVisibleX, coordinatesAndPosMaxList.size() - 1)).nucleotidePosition + 1, fullCoordinates.length);


        if (substructureModel.data2D != null && substructureModel.mapping2D != null) {
            int start = substructureModel.mapping2D.aToBNearestLowerBound(startDrawingFromNucleotide);
            int end = substructureModel.mapping2D.aToBNearestUpperBound(endDrawingAtNucleotide);

            start = start < 0 ? 0 : start;
            end = end < 0 ? substructureModel.data2D.dataMatrix.n : end;

            Iterator<Element> matrixIterator = substructureModel.data2D.dataMatrix.iterator(start, end, start, end);

            g.setStroke(normalStroke);
            while (matrixIterator.hasNext()) {
                Element element = matrixIterator.next();
                int posi = substructureModel.mapping2D.bToA(element.i);
                int posj = substructureModel.mapping2D.bToA(element.j);
                double val = element.value;

                if (posi != -1 && posj != -1) {
                    if (((!substructureModel.data2D.useLowerThreshold || val >= substructureModel.data2D.thresholdMin) && (!substructureModel.data2D.useUpperThreshold || val <= substructureModel.data2D.thresholdMax))) {
                        if (fullCoordinates[posi] != null && fullCoordinates[posj] != null) {
                            Line2D.Double line = new Line2D.Double(fullCoordinates[posi].x, fullCoordinates[posi].y + basePosY, fullCoordinates[posj].x, fullCoordinates[posj].y + basePosY);
                            Color c = substructureModel.data2D.colorGradient.getColor((float) substructureModel.data2D.dataTransform.transform(val));
                            g.setColor(c);
                            g.draw(line);
                        }
                    }
                }

            }
        }
        g.setStroke(new BasicStroke());


        // 1D and nucleotide data
        for (int pos = startDrawingFromNucleotide; pos < endDrawingAtNucleotide; pos++) {
            int posWrap = pos % substructureModel.structureOverlay.pairedSites.length;


            // draw nucleotide circle                
            if (fullCoordinates[pos] != null && pos + 1 < fullCoordinates.length && fullCoordinates[pos + 1] != null) {
                if (pos + 1 < substructures.length && substructures[pos] != substructures[pos + 1]) {
                    Line2D.Double line = new Line2D.Double(fullCoordinates[pos].x, fullCoordinates[pos].y + basePosY, fullCoordinates[pos + 1].x, fullCoordinates[pos + 1].y + basePosY);
                    g.setColor(Color.black);
                    g.draw(line);
                }

                Color nucleotideBackgroundColor = Color.pink;

                if (substructureModel.data1D != null && substructureModel.mapping1D != null) {
                    int dataPos = substructureModel.mapping1D.aToB(posWrap);
                    if (dataPos != -1) {
                        double p = substructureModel.data1D.data[dataPos];
                        if (substructureModel.data1D.used[posWrap] && ((!substructureModel.data1D.useLowerThreshold || p >= substructureModel.data1D.thresholdMin) && (!substructureModel.data1D.useUpperThreshold || p <= substructureModel.data1D.thresholdMax))) {
                            nucleotideBackgroundColor = substructureModel.data1D.colorGradient.getColor(substructureModel.data1D.dataTransform.transform((float) p));
                        } else if (!((!substructureModel.data1D.useLowerThreshold || p >= substructureModel.data1D.thresholdMin) && (!substructureModel.data1D.useUpperThreshold || p <= substructureModel.data1D.thresholdMax))) {
                            nucleotideBackgroundColor = Color.MAGENTA; // if filtered date
                        }
                    }
                }
                g.setColor(nucleotideBackgroundColor);

                Ellipse2D.Double nucleotide = new Ellipse2D.Double(fullCoordinates[pos].x - nucleotideRadius, (fullCoordinates[pos].y + basePosY - nucleotideRadius), nucleotideDiameter, nucleotideDiameter);
                g.fill(nucleotide);
                g.setColor(Color.black);
                g.draw(nucleotide);

                // draw nucleotide position
                if (fullCoordinates[pos] != null && fullCoordinates[pos].y == 0 && (pos + 1) % 10 == 0) {

                    Line2D.Double line = new Line2D.Double(fullCoordinates[pos].x, fullCoordinates[pos].y + basePosY + nucleotideRadius - 1, fullCoordinates[pos].x, fullCoordinates[pos].y + basePosY + nucleotideRadius + 1);
                    g.setColor(Color.black);
                    g.draw(line);
                    GraphicsUtils.drawStringCentred(g, fullCoordinates[pos].x, fullCoordinates[pos].y + basePosY + nucleotideDiameter, (posWrap + 1) + "");
                }

                if (substructureModel.nucleotideSource != null && substructureModel.nucleotideMapping != null) {
                    if (pos < fullCoordinates.length && posWrap < substructureModel.nucleotideSource.consensus.length()) {
                        g.setColor(ColorUtils.selectBestForegroundColor(nucleotideBackgroundColor, Color.white, Color.black));
                        GraphicsUtils.drawStringCentred(g, fullCoordinates[pos].x, fullCoordinates[pos].y + basePosY - 1, substructureModel.nucleotideSource.getMappedCharAtNucleotide(substructureModel.nucleotideMapping, posWrap) + "");
                    }
                }
            }
        }
    }

    public void mouseClicked(MouseEvent e) {
        int lessThanVisibleX = Collections.binarySearch(sortedXPositionMinList, (double) e.getX() - nucleotideDiameter);

        if (lessThanVisibleX < 0) {
            lessThanVisibleX = -lessThanVisibleX - 1;
        }
        int startDrawingFromNucleotide = Math.max(coordinatesAndPosMinList.get(Math.max(lessThanVisibleX, 0)).nucleotidePosition - 1, 0);

        int moreThanVisibleX = Collections.binarySearch(sortedXPositionMaxList, (double) e.getX() + nucleotideDiameter);
        if (moreThanVisibleX < 0) {
            moreThanVisibleX = -moreThanVisibleX - 1;
        }
        int endDrawingAtNucleotide = Math.min(coordinatesAndPosMaxList.get(Math.min(moreThanVisibleX, coordinatesAndPosMaxList.size() - 1)).nucleotidePosition + 1, fullCoordinates.length);


        Point2D.Double mousePosition = new Point2D.Double(e.getX(), e.getY());

        for (int posi = startDrawingFromNucleotide; posi < endDrawingAtNucleotide; posi++) {
            Point2D.Double nucleotide = new Point2D.Double(fullCoordinates[posi].x, fullCoordinates[posi].y + basePosY);
            if (mousePosition.distance(nucleotide) <= nucleotideRadius) {
                System.out.println("User clicked at " + (posi % substructureModel.structureOverlay.pairedSites.length + 1));
            }
        }
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
            int nucleotide = getNucleotideAtPosition(e.getX(), e.getY());
            //gotoStructure = mainapp.getStructureIndexAtPosition(nucleotide);
            if (gotoStructure != -1) {
                this.gotoStructureItem.setText("Open structure " + gotoStructure);
                this.popupMenu.show(this, e.getX(), e.getY());
            }
        }
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseDragged(MouseEvent e) {
    }

    public void mouseMoved(MouseEvent e) {
    }

    public int getNucleotideAtPosition(double x, double y) {
        int lessThanVisibleX = Collections.binarySearch(sortedXPositionMinList, x - nucleotideDiameter);

        if (lessThanVisibleX < 0) {
            lessThanVisibleX = -lessThanVisibleX - 1;
        }
        int startDrawingFromNucleotide = Math.max(coordinatesAndPosMinList.get(Math.max(lessThanVisibleX, 0)).nucleotidePosition - 1, 0);

        int moreThanVisibleX = Collections.binarySearch(sortedXPositionMaxList, x + nucleotideDiameter);
        if (moreThanVisibleX < 0) {
            moreThanVisibleX = -moreThanVisibleX - 1;
        }
        int endDrawingAtNucleotide = Math.min(coordinatesAndPosMaxList.get(Math.min(moreThanVisibleX, coordinatesAndPosMaxList.size() - 1)).nucleotidePosition + 1, fullCoordinates.length);


        Point2D.Double mousePosition = new Point2D.Double(x, y);

        for (int posi = startDrawingFromNucleotide; posi < endDrawingAtNucleotide; posi++) {
            Point2D.Double nucleotide = new Point2D.Double(fullCoordinates[posi].x, fullCoordinates[posi].y + basePosY);
            if (mousePosition.distance(nucleotide) <= nucleotideRadius) {
                return (posi + 1);
            }
        }

        return -1;
    }

    public void actionPerformed(ActionEvent e) {

        if (e.getSource().equals(gotoStructureItem)) {
            if (gotoStructure != -1) {
                //mainapp.openStructure(gotoStructure);
            }
        }
    }

    @Override
    public void structureVisModelChanged(StructureVisModel newStructureVisModel) {
        this.substructureModel = newStructureVisModel.substructureModel;
        initialise(newStructureVisModel.substructureModel.structureOverlay, maxSubstructureSize);
        redraw();
    }

    @Override
    public void dataOverlayAdded(Overlay overlay) {
    }

    @Override
    public void dataOverlayRemoved(Overlay overlay) {
    }

    @Override
    public void dataOverlayChanged(Overlay oldOverlay, Overlay newOverlay) {
    }

    class CoordinatesAndNucleotidePosition implements Comparable {

        Point2D.Double p;
        int nucleotidePosition;

        public CoordinatesAndNucleotidePosition(Point2D.Double p, int nucleotidePos) {
            this.p = p;
            this.nucleotidePosition = nucleotidePos;
        }

        // compare p.x values
        public int compareTo(Object o) {
            CoordinatesAndNucleotidePosition other = (CoordinatesAndNucleotidePosition) o;

            if (p.x < other.p.x) {
                return -1;
            } else if (p.x > other.p.x) {
                return 1;
            }

            return 0;
        }
    }

    public static Point2D.Double[] normaliseStructureCoordinates(ArrayList<Point2D.Double> coordinates) {
        double minx = Double.MAX_VALUE;
        double miny = Double.MAX_VALUE;
        double maxx = Double.MIN_VALUE;
        double maxy = Double.MIN_VALUE;

        for (int i = 0; i < coordinates.size(); i++) {
            Point2D.Double pos = coordinates.get(i);
            minx = Math.min(minx, pos.x);
            miny = Math.min(miny, pos.y);
            maxx = Math.max(maxx, pos.x);
            maxy = Math.max(maxy, pos.y);
        }
        Point2D.Double normalisedPositions[] = new Point2D.Double[coordinates.size()];
        for (int i = 0; i < normalisedPositions.length; i++) {
            normalisedPositions[i] = new Point2D.Double();
            normalisedPositions[i].x = 0 + (coordinates.get(i).x - minx);
            normalisedPositions[i].y = 0 + (coordinates.get(i).y - miny);
        }
        return normalisedPositions;
    }
}
