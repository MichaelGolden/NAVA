/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.structurevis;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import javax.swing.*;
import nava.structurevis.data.AnnotationSource;
import nava.structurevis.data.Block;
import nava.structurevis.data.Feature;
import nava.tasks.AnnotationMappingTask;
import nava.ui.MainFrame;
import nava.ui.ProjectController;
import nava.ui.ProjectModel;
import nava.utils.CustomItem;
import nava.utils.CustomJMenuItem;
import nava.utils.GraphicsUtils;
import nava.utils.Pair;

/**
 *
 * @author Michael Golden
 */
public class AnnotationsLayer extends JPanel implements ActionListener, MouseListener, MouseMotionListener {

    //Graphics2D g = null;
    int xoffset = 5;
    private AnnotationSource annotationData;
    ArrayList<Pair<Shape, Feature>> featurePositions;
    public int rulerHeight = 20;
    public int blockHeight = 22;
    int maxLevel = 0;
    int mouseoverStart = -1;
    int mouseoverEnd = -1;
    int selectedStart = -1;
    int selectedEnd = -1;
    JPopupMenu popupMenu = new JPopupMenu();
    JMenuItem removeAnnotationItem = new JMenuItem("Remove annotation");
    JMenuItem setColorItem = new JMenuItem("Set color");
    JMenu removeItem = new JMenu("Remove");
    JMenuItem removeAllItem = new JMenuItem("Remove all");
    JMenuItem addAnnotationItem = new JMenuItem("Add annotation...");
    JMenuItem addAnnotationFromSourceItem = new JMenuItem("Add from source...");
    JMenuItem stackAnnotationsItem = new JMenuItem("Stack annotations");
    JMenuItem autofitItem = new JMenuItem("Autofit width");
    JMenuItem zoomInItem = new JMenuItem("Zoom in");
    JMenuItem zoomOutItem = new JMenuItem("Zoom out");
    int minorTickMark = 500;
    int majorTickMark = 1000;
    int[] tickMarkPossibilities = {1, 5, 10, 15, 20, 25, 50, 75, 100, 200, 250, 500, 750, 1000, 1500, 2000, 2500, 5000};
    Font annotationsFont = MainFrame.fontLiberationSans.deriveFont(12);
    StructureVisController structureVisController;
    ProjectController projectController;
    /*
     * Structure selected = null; ArrayList<Structure> structures = null;
     * ArrayList<StructureAndMouseoverRegion> structurePositions = null;
     */
    Layer parent;

    public AnnotationsLayer(Layer parent, StructureVisController structureVisController, ProjectController projectController) {

        this.parent = parent;
        this.structureVisController = structureVisController;
        this.projectController = projectController;

        addMouseListener(this);
        addMouseMotionListener(this);

        removeAnnotationItem.addActionListener(this);
        popupMenu.add(removeAnnotationItem);

        popupMenu.add(removeItem);

        popupMenu.add(removeAllItem);
        removeAllItem.addActionListener(this);

        // addAnnotationItem.addActionListener(this);
        // popupMenu.add(addAnnotationItem);

        addAnnotationFromSourceItem.addActionListener(this);
        popupMenu.add(addAnnotationFromSourceItem);


        setColorItem.addActionListener(this);
        popupMenu.add(setColorItem);


        stackAnnotationsItem.addActionListener(this);
        popupMenu.add(stackAnnotationsItem);

        popupMenu.add(new JPopupMenu.Separator());

        autofitItem.addActionListener(this);
        popupMenu.add(autofitItem);

        zoomInItem.addActionListener(this);
        popupMenu.add(zoomInItem);

        popupMenu.add(zoomOutItem).addActionListener(this);
        popupMenu.add(zoomOutItem);

        ToolTipManager.sharedInstance().registerComponent(this);
    }
    boolean forceRepaint = true;

    public int chooseBestTickMarkSize(int genomeLength) {
        for (int i = tickMarkPossibilities.length - 1; i >= 0; i--) {
            double distanceBetweenTicks = (double) getWidth() / (double) genomeLength * (double) tickMarkPossibilities[i];
            if (distanceBetweenTicks < 40) {
                if (i < tickMarkPossibilities.length - 1) {
                    return tickMarkPossibilities[i + 1];
                } else {
                    return tickMarkPossibilities[i];
                }
            }
        }
        return 1;
    }
    public static Comparator<CustomJMenuItem<Feature>> removeFeatureComparator = new Comparator<CustomJMenuItem<Feature>>() {

        public int compare(CustomJMenuItem<Feature> item1, CustomJMenuItem<Feature> item2) {

            //ascending order
            int compare = new Integer(item1.getObject().row).compareTo(item2.getObject().row);
            if (compare == 0) {
                compare = new Integer(item1.getObject().min).compareTo(item2.getObject().min);
            }
            if (compare == 0) {
                compare = new Integer(item1.getObject().max).compareTo(item2.getObject().max);
            }
            return compare;

        }
    };

    public void setAnnotationData(AnnotationSource annotationData, boolean map) {
        if (annotationData != null) {
            if (map) {
                MainFrame.taskManager.queueUITask(new AnnotationMappingTask(annotationData, structureVisController.structureVisModel.substructureModel.structureSource, structureVisController, this));
            } else {
                setAnnotationData(annotationData);
            }
        }
    }
    boolean showLoading = true;

    public void showLoading() {
        this.showLoading = true;
        repaint();
    }

    public void showAnnotations() {
        this.showLoading = false;
        repaint();
    }

    public void setAnnotationData(AnnotationSource annotationData) {
        this.annotationData = annotationData;
        ArrayList<Feature> features = annotationData.mappedFeatures;
        removeItem.removeAll();
        ArrayList<CustomJMenuItem<Feature>> items = new ArrayList<>();
        for (Feature feature : features) {
            CustomJMenuItem<Feature> item = new CustomJMenuItem(feature.getName());
            item.setActionCommand("REMOVE_FEATURE");
            item.setObject(feature);
            item.addActionListener(this);
            items.add(item);
        }

        Collections.sort(items, removeFeatureComparator);
        for (CustomJMenuItem item : items) {
            removeItem.add(item);
        }

        updatePreferredHeight();
        if (this.parent != null) {
            this.parent.refresh();
            this.parent.parent.updatePanel();
        }
        revalidate();
        repaint();
    }

    public void updatePreferredHeight() {
        int numRows = 0;
        for (int i = 0; i < annotationData.mappedFeatures.size(); i++) {
            numRows = Math.max(numRows, annotationData.mappedFeatures.get(i).row);
        }
        setPreferredSize(new Dimension(10000, rulerHeight + numRows * blockHeight + blockHeight + 5));
        this.parent.preferredHeight = rulerHeight + numRows * blockHeight + blockHeight + 5;
    }

    /*
     * @Override public void redraw() { forceRepaint = true; repaint(); }
     *
     *
     * public void setGenomeOrganization(GenomeOrganization g) {
     * this.genomeOrganization = g; if (g != null) { for (int i = 0; i <
     * g.genome.size(); i++) { maxLevel = Math.max(maxLevel,
     * g.genome.get(i).level); } setPreferredSize(new Dimension(10000,
     * rulerHeight + (maxLevel + 1) * blockHeight)); redraw(); } }
     */
    @Override
    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g2 = (Graphics2D) graphics;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int panelWidth = this.getWidth();
        int panelHeight = this.getHeight();

        g2.setColor(Color.white);
        g2.fillRect(0, 0, panelWidth, panelHeight);

        if (annotationData == null || annotationData.features.isEmpty()) {
            g2.setColor(Color.black);
            GraphicsUtils.drawStringCentred(g2, panelWidth / 2, panelHeight / 2, "Right click to add annotations.");
        } else {
            if (showLoading) {
                g2.setColor(Color.black);
                GraphicsUtils.drawStringCentred(g2, panelWidth / 2, panelHeight / 2, "Mapping annotations...");
                return;
            }


            minorTickMark = chooseBestTickMarkSize(annotationData.mappedSequenceLength);
            majorTickMark = minorTickMark * 2;

            System.out.println("Length: " + annotationData.mappedSequenceLength);
            // draw ruler
            g2.setFont(annotationsFont.deriveFont(12.0f));
            for (int i = 0; i < annotationData.mappedSequenceLength; i++) {
                if (i % majorTickMark == 0) {
                    double x = ((double) i / (double) annotationData.mappedSequenceLength) * (getWidth() - xoffset);
                    g2.setColor(Color.black);
                    Line2D.Double tick = new Line2D.Double(x + xoffset, rulerHeight - 1, x + xoffset, rulerHeight + 1);
                    g2.draw(tick);
                    GraphicsUtils.drawStringCentred(g2, x + xoffset, rulerHeight / 2, i + "");
                } else if (i % minorTickMark == 0) {
                    double x = ((double) i / (double) annotationData.mappedSequenceLength) * (getWidth() - xoffset);
                    g2.setColor(Color.black);
                    Line2D.Double tick = new Line2D.Double(x + xoffset, rulerHeight - 1, x + xoffset, rulerHeight + 1);
                    g2.draw(tick);
                }
            }


            // draw blocks
            this.featurePositions = new ArrayList<>();
            for (int i = 0; i < annotationData.mappedFeatures.size(); i++) {
                Feature feature = annotationData.mappedFeatures.get(i);
                for (int j = 0; j < feature.blocks.size(); j++) {
                    double regionLength = feature.blocks.get(j).max - feature.blocks.get(j).min;
                    double regionWidth = (regionLength / (double) annotationData.mappedSequenceLength) * (getWidth() - xoffset);
                    double x = ((double) feature.min / (double) annotationData.mappedSequenceLength) * (getWidth() - xoffset);
                    g2.setColor(feature.blocks.get(j).color);
                    RoundRectangle2D.Double rect = new RoundRectangle2D.Double(x + xoffset, rulerHeight + feature.row * blockHeight, regionWidth, blockHeight, 10, 10);
                    featurePositions.add(new Pair(rect, feature));
                    g2.fill(rect);
                    g2.setColor(Color.black);
                    // scale text to block size

                    float fontSize = 13;
                    for (; fontSize >= 6; fontSize -= 0.25) {
                        if (g2.getFontMetrics(annotationsFont.deriveFont(Font.PLAIN, fontSize)).stringWidth(feature.name) < regionWidth * 0.95) {
                            break;
                        }
                    }
                    if (fontSize >= 7) {
                        g2.setFont(annotationsFont.deriveFont(Font.PLAIN, fontSize));
                        GraphicsUtils.drawStringCentred(g2, x + xoffset + regionWidth / 2, rulerHeight + feature.row * blockHeight + blockHeight / 2, feature.name);
                    } else {
                        g2.setFont(annotationsFont.deriveFont(Font.PLAIN, 10));
                        GraphicsUtils.drawStringCentred(g2, x + xoffset + regionWidth / 2, rulerHeight + feature.row * blockHeight + blockHeight / 2, "..");
                    }
                }
            }
        }

        /*
         * g2.drawImage(bufferedImage, 0, 0, this);
         *
         * if (structures != null && structurePositions != null) { for (int i =
         * 0; i < structurePositions.size(); i++) {
         *
         * Color blockColor = new Color(150, 150, 150, 100); if
         * (structurePositions.get(i).structure.equals(selected)) { blockColor =
         * new Color(255, 10, 10, 100); //selectedRect =
         * structurePositions.get(i); } g2.setColor(blockColor);
         * g2.fill(structurePositions.get(i).rectangle);
         *
         * g2.setColor(Color.GRAY);
         *
         * }
         * }
         */


        /*
         * double rulerHeight = 0; if (mouseoverStart != -1 || mouseoverEnd !=
         * -1) { double mouseoverLength = mouseoverEnd - mouseoverStart; double
         * regionWidth = (mouseoverLength / (double) layerPanel.genomeLength) *
         * getWidth(); double x = (mouseoverStart / (double)
         * layerPanel.genomeLength) * getWidth(); g2.setColor(new Color(0, 0, 0,
         * 125)); Rectangle2D rect = new Rectangle2D.Double(x + xoffset,
         * rulerHeight + 0, regionWidth, getHeight() - rulerHeight - 1);
         * g2.draw(rect);
         *
         * // wrap around if (layerPanel.genomeLength < mouseoverEnd) {
         * mouseoverLength = mouseoverEnd - layerPanel.genomeLength; regionWidth
         * = (mouseoverLength / (double) layerPanel.genomeLength) * getWidth();
         * x = 0; g2.setColor(new Color(125, 125, 125, 125)); rect = new
         * Rectangle2D.Double(x + xoffset, rulerHeight + 0, regionWidth,
         * getHeight() - rulerHeight - 1); g2.draw(rect); } } //rulerHeight =
         * this.rulerHeight;
         *
         * if (selectedStart != -1 || selectedEnd != -1) { double
         * mouseoverLength = selectedEnd - selectedStart; double regionWidth =
         * (mouseoverLength / (double) layerPanel.genomeLength) * getWidth();
         * double x = (selectedStart / (double) layerPanel.genomeLength) *
         * getWidth(); g2.setColor(Color.RED); Rectangle2D rect = new
         * Rectangle2D.Double(x + xoffset, rulerHeight + 0, regionWidth,
         * getHeight() - rulerHeight - 1); g2.draw(rect);
         *
         * // wrap around if (layerPanel.genomeLength < selectedEnd) {
         * mouseoverLength = selectedEnd - layerPanel.genomeLength; regionWidth
         * = (mouseoverLength / (double) layerPanel.genomeLength) * getWidth();
         * x = 0; g2.setColor(Color.RED); rect = new Rectangle2D.Double(x +
         * xoffset, rulerHeight + 0, regionWidth, getHeight() - rulerHeight -
         * 1); g2.draw(rect); } }
         *
         * // }
         */
    }

    @Override
    public String getToolTipText(MouseEvent event) {
        Point p = new Point(event.getX(), event.getY());
        if (featurePositions != null) {
            for (int i = 0; i < featurePositions.size(); i++) {
                if (featurePositions.get(i).getLeft().contains(p)) {
                    Feature f = featurePositions.get(i).getRight();
                    return f.name + " (" + f.min + "-" + f.max + ")";
                }
            }
        }
        return super.getToolTipText(event);
    }

    /*
     * public void drawMouseOverSelection(int start, int end) { mouseoverStart =
     * start; mouseoverEnd = end; repaint(); }
     *
     * public void drawSelected(int start, int end) { selectedStart = start;
     * selectedStart = end; repaint(); }
     *
     * public void mouseClicked(MouseEvent e) { if (this.isEnabled()) { if
     * (selected != null) { layerPanel.mainapp.openStructure(selected); } else {
     * int x = e.getX(); if (x >= 0 && x < getWidth()) { int position = (int)
     * (((double) x / (double) getWidth()) * layerPanel.genomeLength); int
     * structureIndex =
     * layerPanel.mainapp.getStructureIndexAtPosition(position); if
     * (structureIndex != -1) { //System.o
     * layerPanel.mainapp.openStructure(structureIndex); } } else { } } } }
     *
     * public void selectStructureAtPosition(int position) { if
     * (layerPanel.mainapp != null) { Structure s =
     * layerPanel.mainapp.getStructureAtPosition(position); selectStructure(s);
     * } }
     *
     * public void selectStructure(Structure s) { if (s == null) { selectedStart
     * = -1; selectedEnd = -1; } else { selectedStart = s.getStartPosition();
     * selectedEnd = s.getEndPosition(); } repaint(); }
     *
     * public void mousePressed(MouseEvent e) { // throw new
     * UnsupportedOperationException("Not supported yet."); }
     *
     * public void mouseReleased(MouseEvent e) { if
     * (SwingUtilities.isRightMouseButton(e)) { this.popupMenu.show(this,
     * e.getX(), e.getY()); } }
     *
     * public void mouseEntered(MouseEvent e) { //throw new
     * UnsupportedOperationException("Not supported yet."); }
     *
     * public void mouseExited(MouseEvent e) { structures = null; mouseoverStart
     * = -1; mouseoverEnd = -1; repaint(); }
     *
     * public void mouseDragged(MouseEvent e) { //throw new
     * UnsupportedOperationException("Not supported yet."); }
     *
     * public void mouseMoved(MouseEvent e) { if (this.isEnabled()) { int x =
     * e.getX(); int y = e.getY(); if (x >= 0 && x < getWidth()) { int position
     * = (int) (((double) x / (double) getWidth()) * layerPanel.genomeLength);
     * if (layerPanel.mainapp != null) { Structure s =
     * layerPanel.mainapp.getStructureAtPosition(position); Structure largest =
     * layerPanel.mainapp.getLargestStructureAtPosition(position, 500); if
     * (largest != null) { selected = null; structures =
     * layerPanel.mainapp.getStructuresInRegion(largest.startPosition,
     * largest.getEndPosition()); structurePositions =
     * getStructurePositions(structures); for (int i = 0; i <
     * structurePositions.size(); i++) { if
     * (structurePositions.get(i).rectangle.contains(x, y)) { selected =
     * structurePositions.get(i).structure; } } if (selected == null) { selected
     * = s; } } else { structures = null; } if (selected == null) {
     * mouseoverStart = -1; mouseoverEnd = -1; } else { mouseoverStart =
     * selected.getStartPosition(); mouseoverEnd = selected.getEndPosition(); }
     * repaint(); } } else { } } }
     *
     * public void actionPerformed(ActionEvent e) { if
     * (e.getSource().equals(this.autofitItem)) { layerPanel.autofitWidth(); }
     * else if (e.getSource().equals(this.zoomInItem)) { layerPanel.zoomIn(); }
     * else if (e.getSource().equals(this.zoomOutItem)) { layerPanel.zoomOut();
     * } }
     *
     * public ArrayList<StructureAndMouseoverRegion>
     * getStructurePositions(ArrayList<Structure> structures) { double
     * rulerHeight = 0; ArrayList<StructureAndMouseoverRegion> rectangles = new
     * ArrayList<StructureAndMouseoverRegion>(); double minDistance = 3; int
     * level = 0; System.out.println("start"); for (int i = 0; i <
     * structures.size(); i++) { double h = (getHeight() - rulerHeight - 1) /
     * (double) (structures.size()); double y = rulerHeight + i * h; double
     * mouseoverLength = (double) structures.get(i).getEndPosition() - (double)
     * structures.get(i).startPosition; double regionWidth = (mouseoverLength /
     * (double) layerPanel.genomeLength) * getWidth(); double x = ((double)
     * structures.get(i).startPosition / (double) layerPanel.genomeLength) *
     * getWidth(); Rectangle2D rect = new Rectangle2D.Double(x + xoffset, y,
     * regionWidth, h);
     *
     * int rectLevel = 0; for (rectLevel = 0; rectLevel <= level + 1;
     * rectLevel++) { double dist = minHorizontalDistance(rectangles, rect,
     * rectLevel); System.out.println(i+"\t"+dist+"\t"+level+"\t"+rect); if
     * (dist < minDistance) { } else { System.out.println("break"); break; } }
     * System.out.println(rectLevel); level = Math.max(level, rectLevel);
     * rect.setRect(x + xoffset, rulerHeight + rectLevel * h, regionWidth, h);
     * System.out.println("**"+rectLevel+"\t"+rect); rectangles.add(new
     * StructureAndMouseoverRegion(structures.get(i), rect, rectLevel));
     *
     * }
     *
     * for (int i = 0; i < rectangles.size(); i++) { Rectangle2D r =
     * rectangles.get(i).rectangle; double h = (getHeight() - rulerHeight - 1) /
     * (double) (level + 1); double y = rulerHeight + rectangles.get(i).level *
     * h; rectangles.get(i).rectangle.setRect(r.getX(), y, r.getWidth(), h); }
     *
     * return rectangles; }
     *
     * public double
     * minHorizontalDistance(ArrayList<StructureAndMouseoverRegion> rectangles,
     * Rectangle2D rect, int level) { double x = rect.getX(); double width =
     * rect.getWidth(); double distance = Double.MAX_VALUE; for (int i = 0; i <
     * rectangles.size(); i++) { StructureAndMouseoverRegion other =
     * rectangles.get(i); if (other.level == level) { double dist1 =
     * other.rectangle.getX() - (x + width); if (dist1 >= 0) { distance =
     * Math.min(distance, dist1); } double dist2 = x - (other.rectangle.getX() +
     * other.rectangle.getWidth()); if (dist2 >= 0) { distance =
     * Math.min(distance, dist2); } if (x >= other.rectangle.getX() && x + width
     * <= other.rectangle.getX() + other.rectangle.getWidth()) { distance = 0; }
     * if (x <= other.rectangle.getX() && x + width >= other.rectangle.getX() +
     * other.rectangle.getWidth()) { distance = 0; } } } return distance; }
     *
     * public class StructureAndMouseoverRegion {
     *
     * Structure structure; Rectangle2D rectangle; int level;
     *
     * public StructureAndMouseoverRegion(Structure structure, Rectangle2D
     * rectangle, int level) { this.structure = structure; this.rectangle =
     * rectangle; this.level = level; } }
     */
    int popupMenuX = 0;
    int popupMenuY = 0;

    @Override
    public void mouseClicked(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
            popupMenuX = e.getX();
            popupMenuY = e.getY();

            boolean mouseoverFeature = false;
            if (featurePositions != null) {
                for (Pair<Shape, Feature> featurePosition : featurePositions) {
                    if (featurePosition.getLeft().contains(popupMenuX, popupMenuY)) {
                        mouseoverFeature = true;
                        removeAnnotationItem.setText("Remove annotation: " + featurePosition.getRight().getName());
                        setColorItem.setText("Set color: " + featurePosition.getRight().getName());
                        break;
                    }
                }
            }
            removeAnnotationItem.setEnabled(mouseoverFeature);
            setColorItem.setEnabled(mouseoverFeature);
            if (!mouseoverFeature) {
                removeAnnotationItem.setText("Remove annotation");
                setColorItem.setText("Set color");
            }


            popupMenu.show(this, popupMenuX, popupMenuY);
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

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("ACTION:" + e.getActionCommand());
        if (e.getSource().equals(removeAnnotationItem)) {
            for (Pair<Shape, Feature> featurePosition : featurePositions) {
                if (featurePosition.getLeft().contains(popupMenuX, popupMenuY)) {
                    int index = annotationData.mappedFeatures.indexOf(featurePosition.getRight());
                    if (index >= 0) {
                        annotationData.features.remove(index);
                        annotationData.mappedFeatures.remove(index);
                    }
                    setAnnotationData(annotationData, false);
                    break;
                }
            }
        } else if (e.getSource().equals(setColorItem)) {

            for (Pair<Shape, Feature> featurePosition : featurePositions) {
                if (featurePosition.getLeft().contains(popupMenuX, popupMenuY)) {
                    int index = annotationData.mappedFeatures.indexOf(featurePosition.getRight());
                    if (index >= 0) {
                        Color retColor = JColorChooser.showDialog(this, "Select a color", annotationData.features.get(index).blocks.get(0).color);
                        for (Block block : annotationData.features.get(index).blocks) {
                            block.color = retColor;
                        }
                        for (Block block : annotationData.mappedFeatures.get(index).blocks) {
                            block.color = retColor;
                        }
                        break;
                    }
                }
            }
            setAnnotationData(annotationData, false);
        } else if (e.getSource().equals(removeAllItem)) {
            annotationData.features.clear();
            annotationData.mappedFeatures.clear();
            setAnnotationData(annotationData, false);
        } else if (e.getSource().equals(stackAnnotationsItem)) {
            AnnotationSource.stackFeatures(annotationData);
            setAnnotationData(annotationData, false);
            /*
             * updatePreferredHeight(); repaint(); if (parent != null) {
             * parent.updatePanel(); }
             */
        } else if (e.getSource().equals(this.addAnnotationFromSourceItem)) {
            AnnotationsDialog d = new AnnotationsDialog(null, true, projectController.projectModel, structureVisController);
            //d.setSize(640, 480);
            // d.editMode = false;
            d.setVisible(true);
        } else if (e.getActionCommand().equals("REMOVE_FEATURE")) {
            CustomJMenuItem<Feature> removeItem = (CustomJMenuItem<Feature>) e.getSource();
            int index = annotationData.mappedFeatures.indexOf(removeItem.getObject());
            if (index >= 0) {
                annotationData.features.remove(index);
                annotationData.mappedFeatures.remove(index);
            }
            setAnnotationData(annotationData, false);
        } else if (e.getSource().equals(this.autofitItem)) {
            this.parent.parent.autofitWidth();
        } else if (e.getSource().equals(this.zoomInItem)) {
            this.parent.parent.zoomIn();
        } else if (e.getSource().equals(this.zoomOutItem)) {
            this.parent.parent.zoomOut();
        }
    }
}
