package nava.structurevis.layerpanel;

/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
import nava.structurevis.layerpanel.Layer;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import nava.structurevis.AnnotationsDialog;
import nava.structurevis.StructureVisController;
import nava.structurevis.data.*;
import nava.ui.MainFrame;
import nava.ui.ProjectController;
import nava.utils.CustomJMenuItem;
import nava.utils.GraphicsUtils;
import nava.utils.Pair;

/**
 *
 * @author Michael Golden
 */
public class AnnotationsLayer extends JPanel implements ActionListener, MouseListener, MouseMotionListener
{

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
    JMenuItem saveAsSVGItem = new JMenuItem("Save as SVG");
    JMenuItem moveUpItem = new JMenuItem("Move up");
    JMenuItem moveDownItem = new JMenuItem("Move down");
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
    public Layer parent;

    public AnnotationsLayer(Layer parent, StructureVisController structureVisController, ProjectController projectController) {

        this.parent = parent;
        this.structureVisController = structureVisController;
        this.projectController = projectController;

        addMouseListener(this);
        addMouseMotionListener(this);

        removeAnnotationItem.addActionListener(this);
        popupMenu.add(removeAnnotationItem);

        moveUpItem.addActionListener(this);
        popupMenu.add(moveUpItem);

        moveDownItem.addActionListener(this);
        popupMenu.add(moveDownItem);

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

        saveAsSVGItem.addActionListener(this);
        popupMenu.add(saveAsSVGItem);

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
    
    public void updateSubstructures()
    {
        try
        {
            structureList = this.structureVisController.structureVisModel.substructureModel.structureOverlay.substructureList.substructures;
            structures =  this.structureVisController.structureVisModel.substructureModel.structureOverlay.substructureList.substructures;   
            //structurePositions = getSubstructurePositions(structureVisController.structureVisModel.substructureModel.getSubstructures(), structureVisController.structureVisModel.substructureModel.structureOverlay.pairedSites.length);
            genomeLength = this.structureVisController.structureVisModel.substructureModel.structureOverlay.pairedSites.length;
//            this.selected2 = this.structureVisController.structureVisModel.substructureModel.structureOverlay.s
            this.selectedSubstructure = this.structureVisController.structureVisModel.substructureModel.substructure;
        }
        catch(Exception ex)
        {
            structures =  this.structureVisController.structureVisModel.substructureModel.structureOverlay.substructureList.substructures;
           // structurePositions = new ArrayList<>();
            genomeLength = 0;
            ex.printStackTrace();
        }
    }

    public void setAnnotationData(AnnotationSource annotationData, boolean map) {
        
        if (annotationData != null) {
            if (map) {
                AnnotationSource mappedAnnotations = AnnotationSource.getMappedAnnotations(annotationData, structureVisController.structureVisModel.substructureModel.structureOverlay, structureVisController);
                setAnnotationData(mappedAnnotations);
                showAnnotations();
                // MainFrame.taskManager.queueTask(new AnnotationMappingTask(annotationData, structureVisController.structureVisModel.substructureModel.structureOverlay, structureVisController, this), true);
            } else {
                setAnnotationData(annotationData);
            }
        }
        updateSubstructures();
      
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

    public synchronized void setAnnotationData(AnnotationSource annotationData) {
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
        showAnnotations();
        updateSubstructures();
        
        revalidate();
        repaint();
        
          
       
        
    }

    public void updatePreferredHeight() {
        int numRows = 0;
        for (int i = 0; i < annotationData.mappedFeatures.size(); i++) {
            numRows = Math.max(numRows, annotationData.mappedFeatures.get(i).row);
        }
        setPreferredSize(new Dimension(10000, rulerHeight + numRows * blockHeight + blockHeight + 5));
        if (this.parent != null) {
            this.parent.preferredHeight = rulerHeight + numRows * blockHeight + blockHeight + 5;
        }
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
    public void saveAsSVG(File file) throws IOException {
        BufferedWriter buffer = new BufferedWriter(new FileWriter(file));
        buffer.write(getSVG());
        buffer.close();
    }

    public String getSVG() {

        int panelWidth = 1000;
        // int panelWidth = this.getWidth();
        int panelHeight = this.getHeight();


        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        pw.println("<?xml version=\"1.0\" standalone=\"no\"?>");
        pw.println("<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\" \n\"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">");
        pw.println("<svg xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\" width=\"" + panelWidth + "\" height=\"" + panelHeight + "\" style=\"fill:none;stroke-width:16\">");

        minorTickMark = chooseBestTickMarkSize(annotationData.mappedSequenceLength);
        majorTickMark = minorTickMark * 2;

        pw.println("<g>");
        pw.println("<rect x=\"" + (0) + "\" y=\"" + (0) + "\" width=\"" + (panelWidth) + "\" height=\"" + (panelHeight) + "\"  style=\"fill:#" + GraphicsUtils.getHexString(Color.white) + ";\"/>");
        // draw ruler
        // g2.setFont(annotationsFont.deriveFont(12.0f));
        for (int i = 0; i < annotationData.mappedSequenceLength; i++) {
            if (i % majorTickMark == 0) {
                double x = ((double) i / (double) annotationData.mappedSequenceLength) * (panelWidth - xoffset);
                // g2.setColor(Color.black);
                // Line2D.Double tick = new Line2D.Double(x + xoffset, rulerHeight - 1, x + xoffset, rulerHeight + 1);
                //g2.draw(tick);
                pw.println("<line x1=\"" + (x + xoffset) + "\" y1=\"" + (rulerHeight - 1) + "\" x2=\"" + (x + xoffset) + "\" y2=\"" + (rulerHeight + 1) + "\" style=\"stroke:rgb(0,0,0);stroke-width:1\"/>");

                // GraphicsUtils.drawStringCentred(g2, x + xoffset, rulerHeight / 2, i + "");
                pw.println("<text x=\"" + (x + xoffset) + "\" y=\"" + (rulerHeight / 2 + 5) + "\" style=\"font-size:" + 10 + "px;stroke:none;fill:black\" text-anchor=\"" + "middle" + "\" >");
                pw.println("<tspan>" + i + "" + "</tspan>");
                pw.println("</text>");
            } else if (i % minorTickMark == 0) {
                double x = ((double) i / (double) annotationData.mappedSequenceLength) * (panelWidth - xoffset);
                // g2.setColor(Color.black);
                Line2D.Double tick = new Line2D.Double(x + xoffset, rulerHeight - 1, x + xoffset, rulerHeight + 1);
                //g2.draw(tick);
                pw.println("<line x1=\"" + (x + xoffset) + "\" y1=\"" + (rulerHeight - 1) + "\" x2=\"" + (x + xoffset) + "\" y2=\"" + (rulerHeight + 1) + "\" style=\"stroke:rgb(0,0,0);stroke-width:1\"/>");
            }
        }

        for (int i = 0; i < annotationData.mappedFeatures.size(); i++) {
            Feature feature = annotationData.mappedFeatures.get(i);
            for (int j = 0; j < feature.blocks.size(); j++) {
                double regionLength = feature.blocks.get(j).max - feature.blocks.get(j).min;
                double regionWidth = (regionLength / (double) annotationData.mappedSequenceLength) * (panelWidth - xoffset);
                double x = ((double) feature.blocks.get(j).min / (double) annotationData.mappedSequenceLength) * (panelWidth - xoffset);
                //g2.setColor(feature.blocks.get(j).color);
                // RoundRectangle2D.Double rect = new RoundRectangle2D.Double(x + xoffset, rulerHeight + feature.row * blockHeight, regionWidth, blockHeight, 10, 10);
                pw.println("<rect x=\"" + (x + xoffset) + "\" y=\"" + (rulerHeight + feature.row * blockHeight) + "\" rx=\"" + (5) + "\" ry=\"" + (5) + "\" width=\"" + (regionWidth) + "\" height=\"" + (blockHeight) + "\"  style=\"fill:#" + GraphicsUtils.getHexString(feature.blocks.get(j).color) + ";\"/>");
                //featurePositions.add(new Pair(rect, feature));
                //g2.fill(rect);
                // g2.setColor(Color.black);
                // scale text to block size

                /*
                 * float fontSize = 13; for (; fontSize >= 6; fontSize -= 0.25)
                 * { if
                 * (g2.getFontMetrics(annotationsFont.deriveFont(Font.PLAIN,
                 * fontSize)).stringWidth(feature.name) < regionWidth * 0.95) {
                 * break; } } if (fontSize >= 7) {
                 * g2.setFont(annotationsFont.deriveFont(Font.PLAIN, fontSize));
                 * GraphicsUtils.drawStringCentred(g2, x + xoffset + regionWidth
                 * / 2, rulerHeight + feature.row * blockHeight + blockHeight /
                 * 2, feature.name); } else {
                 * g2.setFont(annotationsFont.deriveFont(Font.PLAIN, 10));
                 * GraphicsUtils.drawStringCentred(g2, x + xoffset + regionWidth
                 * / 2, rulerHeight + feature.row * blockHeight + blockHeight /
                 * 2, ".."); }
                 */
            }
        }

        for (int i = 0; i < annotationData.mappedFeatures.size(); i++) {
            Feature feature = annotationData.mappedFeatures.get(i);
            for (int j = 0; j < feature.blocks.size(); j++) {
                double regionLength = feature.blocks.get(j).max - feature.blocks.get(j).min;
                double regionWidth = (regionLength / (double) annotationData.mappedSequenceLength) * (panelWidth - xoffset);
                double x = ((double) feature.blocks.get(j).min/ (double) annotationData.mappedSequenceLength) * (panelWidth - xoffset);

                int fontSize = 12;
                pw.println("<text x=\"" + (x + xoffset + regionWidth / 2) + "\" y=\"" + (rulerHeight + feature.row * blockHeight + blockHeight / 2 + (fontSize / 2)) + "\" style=\"font-size:" + fontSize + "px;stroke:none;fill:black\" text-anchor=\"" + "middle" + "\" >");
                pw.println("<tspan>" + feature.name + "</tspan>");
                pw.println("</text>");

            }
        }
        pw.println("</g>");
        pw.println("</svg>");
        pw.close();
        //System.out.println(sw.toString());
        return sw.toString();
    }

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
        
        int rulerLength = genomeLength;
        if(rulerLength > 0)
        {
            if(annotationData != null)
            {
                rulerLength = annotationData.mappedSequenceLength;
            }
            
             // draw ruler
            g2.setFont(annotationsFont.deriveFont(12.0f));
            for (int i = 0; i < rulerLength; i++) {
                if (i % majorTickMark == 0) {
                    double x = ((double) i / (double) rulerLength) * (getWidth() - xoffset);
                    g2.setColor(Color.black);
                    Line2D.Double tick = new Line2D.Double(x + xoffset, rulerHeight - 1, x + xoffset, rulerHeight + 1);
                    g2.draw(tick);
                    GraphicsUtils.drawStringCentred(g2, x + xoffset, rulerHeight / 2, i + "");
                } else if (i % minorTickMark == 0) {
                    double x = ((double) i / (double) rulerLength) * (getWidth() - xoffset);
                    g2.setColor(Color.black);
                    Line2D.Double tick = new Line2D.Double(x + xoffset, rulerHeight - 1, x + xoffset, rulerHeight + 1);
                    g2.draw(tick);
                }
            }
        }

        g2.setFont(annotationsFont.deriveFont(13.0f));
        if (annotationData == null || annotationData.features.isEmpty()) {
            g2.setColor(Color.black);
            GraphicsUtils.drawStringCentred(g2, panelWidth / 2, rulerHeight + ((panelHeight-rulerHeight) / 2), "Right click to add annotations.");
        } else {
            if (showLoading) {
                g2.setColor(Color.black);
                GraphicsUtils.drawStringCentred(g2, panelWidth / 2, rulerHeight+ ((panelHeight-rulerHeight) / 2), "Mapping annotations...");
                return;
            }


            minorTickMark = chooseBestTickMarkSize(annotationData.mappedSequenceLength);
            majorTickMark = minorTickMark * 2;

           

            // draw blocks
            this.featurePositions = new ArrayList<>();
            for (int i = 0; i < annotationData.mappedFeatures.size(); i++) {
                Feature feature = annotationData.mappedFeatures.get(i);
                for (int j = 0; j < feature.blocks.size(); j++) {
                    double regionLength = feature.blocks.get(j).max - feature.blocks.get(j).min;
                    double regionWidth = (regionLength / (double) annotationData.mappedSequenceLength) * (getWidth() - xoffset);
                    double x = ((double) feature.blocks.get(j).min / (double) annotationData.mappedSequenceLength) * (getWidth() - xoffset);
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
        
        if(selectedSubstructures != null)
        {
            for (int i = 0; i < selectedSubstructures.size(); i++) {
                Color blockColor = new Color(150, 150, 150, 100);
                if (selectedSubstructures.get(i).structure.equals(selectedSubstructure)) {
                    blockColor = new Color(10, 255, 10, 100);
                }
                g2.setColor(blockColor);
                g2.fill(selectedSubstructures.get(i).rectangle);
                
                g2.setColor(Color.GRAY);
                
            }
        }

        if (structures != null && highlightedSubstructures != null) {
            for (int i = 0; i < highlightedSubstructures.size(); i++) {
                Color blockColor = new Color(150, 150, 150, 100);
                if (highlightedSubstructures.get(i).structure.equals(highlightedSubstructure)) {
                    blockColor = new Color(10, 10, 255, 100);
                }
                g2.setColor(blockColor);
                g2.fill(highlightedSubstructures.get(i).rectangle);
                
                g2.setColor(Color.GRAY);                
            }
        }


        double rulerHeight = 0;
        if (mouseoverStart != -1 || mouseoverEnd != -1) {
            double mouseoverLength = mouseoverEnd - mouseoverStart;
            double regionWidth = (mouseoverLength / (double) genomeLength) * getWidth();
            double x = (mouseoverStart / (double) genomeLength) * getWidth();
            g2.setColor(new Color(0, 0, 0, 125));
            Rectangle2D rect = new Rectangle2D.Double(x + xoffset, rulerHeight + 0  - 3, regionWidth, 3 + getHeight() - rulerHeight - 1);
            g2.draw(rect);

            // wrap around
            if (genomeLength < mouseoverEnd) {
                mouseoverLength = mouseoverEnd - genomeLength;
                regionWidth = (mouseoverLength / (double) genomeLength) * getWidth();
                x = 0;
                g2.setColor(new Color(125, 125, 125, 125));
                rect = new Rectangle2D.Double(x + xoffset, rulerHeight + 0 - 3, regionWidth, 3 + getHeight() - rulerHeight - 1);
                g2.draw(rect);
            }
        }        

        if (selectedStart != -1 || selectedEnd != -1) {
            double mouseoverLength = selectedEnd - selectedStart;
            double regionWidth = (mouseoverLength / (double) genomeLength) * getWidth();
            double x = (selectedStart / (double) genomeLength) * getWidth();
            g2.setColor(Color.RED);
            Rectangle2D rect = new Rectangle2D.Double(x + xoffset, rulerHeight + 0  - 3, regionWidth, 3 + getHeight() - rulerHeight - 1);
            g2.draw(rect);

            // wrap around
            if (genomeLength < selectedEnd) {
                mouseoverLength = selectedEnd - genomeLength;
                regionWidth = (mouseoverLength / (double) genomeLength) * getWidth();
                x = 0;
                g2.setColor(Color.RED);
                rect = new Rectangle2D.Double(x + xoffset, rulerHeight + 0  - 3, regionWidth, 3 + getHeight() - rulerHeight - 1);
                g2.draw(rect);
            }
        }
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

    
    
     ArrayList<SubstructureMouseoverRegion> highlightedSubstructures = new ArrayList<>();     
     ArrayList<SubstructureMouseoverRegion> selectedSubstructures = new ArrayList<>();
    
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
        
        if (this.isEnabled()) {
            selectedSubstructures = (ArrayList<SubstructureMouseoverRegion>) highlightedSubstructures.clone();
            selectedSubstructure = highlightedSubstructure;
            if (highlightedSubstructure != null) {
                this.structureVisController.structureVisModel.substructureModel.openSubstructure(highlightedSubstructure);
            } else {
                int x = e.getX();
                if (x >= 0 && x < getWidth()) {
                    int position = (int) (((double) x / (double) getWidth()) * genomeLength);
                    int structureIndex = getStructureIndexAtPosition(position);
                    if (structureIndex != -1) {                        
                        this.structureVisController.structureVisModel.substructureModel.openSubstructure(getStructureAtIndex(structureIndex));
                    }
                }
            }
        }
        
        repaint();
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
        structures = null;
        mouseoverStart = -1;
        mouseoverEnd = -1;
        repaint();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        structures = null;
        mouseoverStart = -1;
        mouseoverEnd = -1;
        repaint();
    }
    
    Substructure highlightedSubstructure = null;    
    Substructure selectedSubstructure = null;
    ArrayList<Substructure> structures = null;
    ArrayList<Substructure> structureList = null;
    int genomeLength = 0;

    @Override
    public void mouseMoved(MouseEvent e) {
        if (this.isEnabled()) {
            int x = e.getX();
            int y = e.getY();
            if (x >= 0 && x < getWidth()) {
                int position = (int) (((double) x / (double) getWidth()) * genomeLength);
                    Substructure s = getStructureAtIndex(position);
                    Substructure largest = getLargestStructureAtPosition(position, 500);
                    if (largest != null) {
                        highlightedSubstructure = null;
                        structures = getStructuresInRegion(largest.startPosition, largest.getEndPosition());
                        highlightedSubstructures = getStructurePositions(structures);
                        for (int i = 0; i < highlightedSubstructures.size(); i++) {
                            if (highlightedSubstructures.get(i).rectangle.contains(x, y)) {
                                highlightedSubstructure = highlightedSubstructures.get(i).structure;
                            }
                        }
                        if (highlightedSubstructure == null) {
                            highlightedSubstructure = s;
                        }
                    } else {
                        structures = null;
                    }
                    if (highlightedSubstructure == null) {
                        mouseoverStart = -1;
                        mouseoverEnd = -1;
                    } else {
                        mouseoverStart = highlightedSubstructure.getStartPosition();
                        mouseoverEnd = highlightedSubstructure.getEndPosition();
                    }
                    repaint();
                }
            } else {
            }
        }
   
    public Substructure getLargestStructureAtPosition(int position, int lessThanLength) {
        Substructure s = null;
        if(structureList != null)
        {
            for (int i = 0; i < structureList.size(); i++) {
                if (structureList.get(i).length < lessThanLength && structureList.get(i).getStartPosition() <= position && structureList.get(i).getEndPosition() >= position) {
                    if (s != null) {
                        int ilen = structureList.get(i).length;
                        int slen = s.length;
                        if (ilen > slen) {
                            s = structureList.get(i);
                        }
                    } else {
                       s = structureList.get(i);
                    }
                }
            }
        }
        return s;
    }
    
    public void selectedSubstructures(Substructure s)
    {
        if(s != null)
        {
            int position = s.startPosition;
            Substructure largest = getLargestStructureAtPosition(position, 500);
            if (largest != null) {
                selectedSubstructure = s;
                structures = getStructuresInRegion(largest.startPosition, largest.getEndPosition());
                selectedSubstructures = getStructurePositions(structures);
            } else {
                structures = null;
            }
            highlightedSubstructure = null;
            mouseoverStart = -1;
            mouseoverEnd = -1;
        }
        repaint();
    }
    
    public void highlightSubstructure(Substructure s)
    {
        //int position = (int) (((double) x / (double) getWidth()) * genomeLength);
        //  getStructureIndexAtPosition(substructure)
        int position = s.startPosition;
        Substructure largest = getLargestStructureAtPosition(position, 500);
        if (largest != null) {
            highlightedSubstructure = s;
            structures = getStructuresInRegion(largest.startPosition, largest.getEndPosition());
            highlightedSubstructures = getStructurePositions(structures);
            /*for (int i = 0; i < highlightedSubstructures.size(); i++) {
                if (highlightedSubstructures.get(i).rectangle.contains(x, y)) {
                    highlightedSubstructure = highlightedSubstructures.get(i).structure;
                }
            }
            if (highlightedSubstructure == null) {
                highlightedSubstructure = s;
            }*/
        } else {
            structures = null;
        }
        if (highlightedSubstructure == null) {
            mouseoverStart = -1;
            mouseoverEnd = -1;
        } else {
            mouseoverStart = highlightedSubstructure.getStartPosition();
            mouseoverEnd = highlightedSubstructure.getEndPosition();
        }
        repaint();
    }
    
    public ArrayList<Substructure> getStructuresInRegion(int start, int end) {
        ArrayList<Substructure> substructures = new ArrayList<>();
        if (structureList != null) 
        {
            for (int i = 0; i < structureList.size(); i++) {
                if (structureList.get(i).getStartPosition() >= start && structureList.get(i).getEndPosition() <= end) {
                    substructures.add(structureList.get(i));
                }
            }
            
        }
        return substructures;
    }
    
    public Substructure getStructureAtIndex(int index)
    {
        for(Substructure s : structureList)
        {
            if(s.index == index)
            {
                return s;
            }
        }
        return null;
    }
    
    public ArrayList<SubstructureMouseoverRegion> getStructurePositions(ArrayList<Substructure> structures) {
        double rulerHeight = 0;
        ArrayList<SubstructureMouseoverRegion> rectangles = new ArrayList<SubstructureMouseoverRegion>();
        double minDistance = 3;
        int level = 0;
        for (int i = 0; i < structures.size(); i++) {
            double h = (getHeight() - rulerHeight - 1) / (double) (structures.size());
            double y = rulerHeight + i * h;
            double mouseoverLength = (double) structures.get(i).getEndPosition() - (double) structures.get(i).startPosition;
            double regionWidth = (mouseoverLength / (double) genomeLength) * getWidth();
            double x = ((double) structures.get(i).startPosition / (double) genomeLength) * getWidth();
            Rectangle2D rect = new Rectangle2D.Double(x + xoffset, y, regionWidth, h);

            int rectLevel = 0;
            for (rectLevel = 0; rectLevel <= level + 1; rectLevel++) {
                double dist = minHorizontalDistance(rectangles, rect, rectLevel);
                if (dist < minDistance) {
                } else {
                    break;
                }
            }
            System.out.println(rectLevel);
            level = Math.max(level, rectLevel);
            rect.setRect(x + xoffset, rulerHeight + rectLevel * h, regionWidth, h);
            rectangles.add(new SubstructureMouseoverRegion(structures.get(i), rect, rectLevel));

            /*
            // wrap around
            if (layerPanel.genomeLength < (double) structures.get(i).getEndPosition()) {
                Rectangle2D rect2 = new Rectangle2D.Double(x + xoffset, y, regionWidth, h);
                rectLevel = 0;
                for (rectLevel = 0; rectLevel <= level + 1; rectLevel++) {
                    if (minHorizontalDistance(rectangles, rect2, rectLevel) < minDistance) {
                    } else {
                        break;
                    }
                }
                level = Math.max(level, rectLevel);
                rect2.setRect(x + xoffset, rulerHeight + rectLevel * h, regionWidth, h);
                rectangles.add(new StructureAndMouseoverRegion(structures.get(i), rect2, rectLevel));
            }*/
        }

        for (int i = 0; i < rectangles.size(); i++) {
            Rectangle2D r = rectangles.get(i).rectangle;
            double h = (getHeight() - rulerHeight - 1) / (double) (level + 1);
            double y = rulerHeight + rectangles.get(i).level * h;
            rectangles.get(i).rectangle.setRect(r.getX(), y, r.getWidth(), h);
        }

        return rectangles;
    }
    
    
    public void selectStructureAtIndex(int position) {
            Substructure s = getStructureAtIndex(position);
            selectStructure(s);
    }

    public void selectStructure(Substructure s) {
        if (s == null) {
            selectedStart = -1;
            selectedEnd = -1;
        } else {
            selectedStart = s.getStartPosition();
            selectedEnd = s.getEndPosition();
        }
        repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
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
        } else if (e.getSource().equals(moveUpItem)) {
            for (Pair<Shape, Feature> featurePosition : featurePositions) {
                if (featurePosition.getLeft().contains(popupMenuX, popupMenuY)) {
                    int index = annotationData.mappedFeatures.indexOf(featurePosition.getRight());
                    if (index >= 0) {
                        annotationData.features.get(index).row = Math.max(0, annotationData.features.get(index).row - 1);
                        annotationData.mappedFeatures.get(index).row = Math.max(0, annotationData.mappedFeatures.get(index).row - 1);
                    }
                    setAnnotationData(annotationData, false);
                    break;
                }
            }
        } else if (e.getSource().equals(moveDownItem)) {
            for (Pair<Shape, Feature> featurePosition : featurePositions) {
                if (featurePosition.getLeft().contains(popupMenuX, popupMenuY)) {
                    int index = annotationData.mappedFeatures.indexOf(featurePosition.getRight());
                    if (index >= 0) {
                        annotationData.features.get(index).row++;
                        annotationData.mappedFeatures.get(index).row++;
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
                        if (retColor != null) {
                            for (Block block : annotationData.features.get(index).blocks) {
                                block.color = retColor;
                            }
                            for (Block block : annotationData.mappedFeatures.get(index).blocks) {
                                block.color = retColor;
                            }
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
            d.setVisible(true);

            if (structureVisController.structureVisModel.substructureModel.getAnnotationSource() == null) {
                structureVisController.addAnnotationsSource(d.annotationSource);
                structureVisController.structureVisModel.substructureModel.setAnnotationSource(d.annotationSource);
                this.setAnnotationData(structureVisController.structureVisModel.substructureModel.getAnnotationSource());
            } else {
                structureVisController.structureVisModel.substructureModel.getAnnotationSource().addAnnotations(d.annotationSource);
                structureVisController.structureVisModel.substructureModel.setAnnotationSource(structureVisController.structureVisModel.substructureModel.getAnnotationSource());
                this.setAnnotationData(structureVisController.structureVisModel.substructureModel.getAnnotationSource());
            }
            // this.setAnnotationData(structureVisController.structureVisModel.substructureModel.getAnnotationSource());

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
        } else if (e.getSource().equals(this.saveAsSVGItem)) {
            MainFrame.saveDialog.setDialogTitle("Save as SVG");
            String name = "annotations";
            MainFrame.saveDialog.setSelectedFile(new File(MainFrame.saveDialog.getCurrentDirectory().getPath() + "/" + name + ".svg"));
            int returnVal = MainFrame.saveDialog.showSaveDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                try {
                    saveAsSVG(MainFrame.saveDialog.getSelectedFile());
                } catch (IOException ex) {
                    Logger.getLogger(AnnotationsLayer.class.getName()).log(Level.SEVERE, null, ex);
                }
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }
        }
    }
    
    public ArrayList<SubstructureMouseoverRegion> getSubstructurePositions(ArrayList<Substructure> structures, int genomeLength) {
        double rulerHeight = 0;
        ArrayList<SubstructureMouseoverRegion> rectangles = new ArrayList<>();
        double minDistance = 3;
        int level = 0;
        for (int i = 0; i < structures.size(); i++) {
            double h = (getHeight() - rulerHeight - 1) / (double) (structures.size());
            double y = rulerHeight + i * h;
            double mouseoverLength = (double) structures.get(i).getEndPosition() - (double) structures.get(i).startPosition;
            double regionWidth = (mouseoverLength / (double) genomeLength) * getWidth();
            double x = ((double) structures.get(i).startPosition / (double) genomeLength) * getWidth();
            Rectangle2D rect = new Rectangle2D.Double(x + xoffset, y, regionWidth, h);

            int rectLevel = 0;
            for (rectLevel = 0; rectLevel <= level + 1; rectLevel++) {
                double dist = minHorizontalDistance(rectangles, rect, rectLevel);
                if (dist < minDistance) {
                } else {
                    break;
                }
            }
            System.out.println(rectLevel);
            level = Math.max(level, rectLevel);
            rect.setRect(x + xoffset, rulerHeight + rectLevel * h, regionWidth, h);
            rectangles.add(new SubstructureMouseoverRegion(structures.get(i), rect, rectLevel));

            /*
            // wrap around
            if (layerPanel.genomeLength < (double) structures.get(i).getEndPosition()) {
                Rectangle2D rect2 = new Rectangle2D.Double(x + xoffset, y, regionWidth, h);
                rectLevel = 0;
                for (rectLevel = 0; rectLevel <= level + 1; rectLevel++) {
                    if (minHorizontalDistance(rectangles, rect2, rectLevel) < minDistance) {
                    } else {
                        break;
                    }
                }
                level = Math.max(level, rectLevel);
                rect2.setRect(x + xoffset, rulerHeight + rectLevel * h, regionWidth, h);
                rectangles.add(new StructureAndMouseoverRegion(structures.get(i), rect2, rectLevel));
            }*/
        }

        for (int i = 0; i < rectangles.size(); i++) {
            Rectangle2D r = rectangles.get(i).rectangle;
            double h = (getHeight() - rulerHeight - 1) / (double) (level + 1);
            double y = rulerHeight + rectangles.get(i).level * h;
            rectangles.get(i).rectangle.setRect(r.getX(), y, r.getWidth(), h);
        }

        return rectangles;
    }
    
    public int getStructureIndexAtPosition(int position) {
        int s = -1;
        if (structureList != null) {
            for (int i = 0;i < structureList.size(); i++) {
                if (structureList.get(i).getStartPosition() <= position && structureList.get(i).getEndPosition() >= position) {
                    if (s != -1) {
                        int ilen = structureList.get(i).length;
                        int slen = structureList.get(s).length;
                        if (ilen < slen) {
                            s = i;
                        }
                    } else {
                        s = i;
                    }
                }
            }
        }
        return s;
    }
    
    public double minHorizontalDistance(ArrayList<SubstructureMouseoverRegion> rectangles, Rectangle2D rect, int level) {
        double x = rect.getX();
        double width = rect.getWidth();
        double distance = Double.MAX_VALUE;
        for (int i = 0; i < rectangles.size(); i++) {
            SubstructureMouseoverRegion other = rectangles.get(i);
            if (other.level == level) {
                double dist1 = other.rectangle.getX() - (x + width);
                if (dist1 >= 0) {
                    distance = Math.min(distance, dist1);
                }
                double dist2 = x - (other.rectangle.getX() + other.rectangle.getWidth());
                if (dist2 >= 0) {
                    distance = Math.min(distance, dist2);
                }
                if (x >= other.rectangle.getX() && x + width <= other.rectangle.getX() + other.rectangle.getWidth()) {
                    distance = 0;
                }
                if (x <= other.rectangle.getX() && x + width >= other.rectangle.getX() + other.rectangle.getWidth()) {
                    distance = 0;
                }
            }
        }
        return distance;
    }
    
    public class SubstructureMouseoverRegion {

        Substructure structure;
        Rectangle2D rectangle;
        int level;

        public SubstructureMouseoverRegion(Substructure structure, Rectangle2D rectangle, int level) {
            this.structure = structure;
            this.rectangle = rectangle;
            this.level = level;
        }

        @Override
        public String toString() {
            return "SubstructureMouseoverRegion{" + "structure=" + structure + ", rectangle=" + rectangle + ", level=" + level + '}';
        }
    }
    
     public void drawMouseOverSelection(int start, int end) {
        mouseoverStart = start;
        mouseoverEnd = end;
        repaint();
    }

    public void drawSelected(int start, int end) {
        selectedStart = start;
        selectedStart = end;
        repaint();
    }
}
