/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.alignment;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import javax.swing.event.EventListenerList;
import nava.alignment.AlignmentModel.ItemRange;
import nava.data.types.Sequence;
import nava.ui.MainFrame;
import nava.ui.navigator.NavigationEvent;
import nava.ui.navigator.NavigationListener;
import nava.utils.GraphicsUtils;
import nava.utils.Utils;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class AlignmentPanel extends javax.swing.JPanel implements KeyListener, MouseListener, MouseMotionListener, AlignmentModelListener {

    public static final double rulerHeight = 17;
    public static final double blockHeight = 17;
    public static final double blockWidth = 15;
    public static final Color gridColor = Color.lightGray;
    public static final Color fontColor = Color.black;
    public static Font fontLiberationSans = new Font("Sans", Font.PLAIN, 12);
    public static Font fontDroidSansMono = new Font("Sans", Font.PLAIN, 12);
    AlignmentModel alignmentModel;
    AlignmentNamePanel namePanel;
    AlignmentChartPanel chartPanel;
    RulerPanel rulerPanel;
    int selectionStartSeq = -1;
    int selectionEndSeq = -1;
    int selectionStartPos = -1;
    int selectionEndPos = -1;
    boolean allowEditing = false;
    Color structureSelectionCol = new Color(150, 150, 255);
    int structureSelectionPos1 = -1;
    int structureSelectionPos2 = -1;

    /**
     * Creates new form AlignmentPanel
     */
    public AlignmentPanel(AlignmentModel alignmentModel, AlignmentNamePanel namePanel, AlignmentChartPanel chartPanel, RulerPanel rulerPanel) {
        initComponents();
        this.alignmentModel = alignmentModel;
        this.namePanel = namePanel;
        this.chartPanel = chartPanel;
        this.rulerPanel = rulerPanel;
        this.setPreferredSize(new Dimension(50000, 50000));
        try {
            fontLiberationSans = Font.createFont(Font.PLAIN, ClassLoader.getSystemResourceAsStream("resources/fonts/LiberationSans-Regular.ttf")).deriveFont(12.0f);
            fontDroidSansMono = Font.createFont(Font.PLAIN, ClassLoader.getSystemResourceAsStream("resources/fonts/DroidSansMono.ttf")).deriveFont(12.0f);
        } catch (FontFormatException ex) {
            Logger.getLogger(AlignmentPanel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(AlignmentPanel.class.getName()).log(Level.SEVERE, null, ex);
        }

        addMouseListener(this);
        addMouseMotionListener(this);
        addKeyListener(this);
        alignmentModel.addAlignmentModelListener(this);
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);


        if (alignmentModel != null && alignmentModel.getSize() > 0) {
            //int length = alignmentModel.item.get(0).getSequence().length();
            int length = alignmentModel.maxSequenceLength;
            int numSequences = alignmentModel.getSize();
            setPreferredSize(new Dimension((int) (length * blockWidth), (int) (AlignmentPanel.rulerHeight + numSequences * blockHeight)));

            g2.setFont(fontLiberationSans);
            //Rectangle viewableRect = this.getVisibleRect();
            Rectangle viewableRect = g2.getClipBounds();

            g2.setColor(Color.white);
            g2.fill(new Rectangle2D.Double(viewableRect.x, viewableRect.y, viewableRect.width, viewableRect.height));

            double yoffset = 0;

            int startNuc = (int) (viewableRect.x / blockWidth);
            int endNuc = Math.min(length, (int) ((viewableRect.x + viewableRect.width) / blockWidth) + 1);
            int startSeq = Math.max(0, (int) ((viewableRect.y - AlignmentPanel.rulerHeight) / blockHeight));
            int endSeq = Math.max(0, Math.min(numSequences, (int) (((viewableRect.y - AlignmentPanel.rulerHeight) + viewableRect.height) / blockHeight) + 1) + 2);

            System.out.println(startSeq + "\t" + endSeq);

            for (int seq = startSeq; seq < endSeq; seq++) {
                //String sequence = alignmentModel.getElementAt(seq).getSequence();
                String sequence = alignmentModel.getElementAt(seq);
                ItemRange itemRange = alignmentModel.getItemRange(seq);
                if (sequence != null && itemRange != null) {
                    for (int nuc = startNuc; nuc < endNuc; nuc++) {
                        Rectangle2D.Double block = new Rectangle2D.Double(nuc * blockWidth, yoffset + seq * blockHeight, blockWidth, blockHeight);
                        char c = nuc < sequence.length() ? sequence.charAt(nuc) : ' ';

                        Color blockColor = Color.white;
                        if (alignmentModel.getElementTypeAt(seq) == 0) {
                            blockColor = getNucleotideColor(c);
                        } else if (alignmentModel.getElementTypeAt(seq) == 1) {
                            if (nuc == structureSelectionPos1 || nuc == structureSelectionPos2) {
                                blockColor = structureSelectionCol;
                            }
                        }

                        if (selectionStartPos <= nuc && nuc <= selectionEndPos && selectionStartSeq <= seq && seq <= selectionEndSeq) {
                            blockColor = blockColor.darker();
                        }
                        g2.setColor(blockColor);
                        g2.fill(block);

                        //if (alignmentModel.getElementTypeAt(seq) == 0) {
                        g2.setColor(gridColor);
                        g2.draw(block);
                        // }
                        g2.setColor(fontColor);
                        GraphicsUtils.drawStringCentred(g2, nuc * blockWidth + (blockWidth / 2), yoffset + seq * blockHeight + (blockHeight / 2), c + "");
                        if (itemRange.mod == 0) {
                            g2.setColor(new Color(255, 130, 130));
                            Line2D.Double hr = new Line2D.Double(viewableRect.x, yoffset + seq * blockHeight, viewableRect.x + viewableRect.width, yoffset + seq * blockHeight);
                            g2.draw(hr);
                        }
                    }
                }
            }
        } else {
            Rectangle visibleRect = this.getVisibleRect();
            setPreferredSize(new Dimension(visibleRect.width, visibleRect.height));
            g2.setColor(Color.white);
            visibleRect = this.getVisibleRect();
            g2.fillRect(visibleRect.x, visibleRect.y, visibleRect.width, visibleRect.height);
        }

        Rectangle visibleRect = getVisibleRect();
        namePanel.setVisibleRect(visibleRect);
        chartPanel.setVisibleRect(visibleRect);
        rulerPanel.setVisibleRect(visibleRect);
    }

    public static Color getNucleotideColor(char c) {
        char n = Character.toUpperCase(c);
        switch (n) {
            case 'A':
                return Color.red;
            case 'C':
                return Color.cyan;
            case 'G':
                return Color.yellow;
            case 'T':
                return Color.green;
            case 'U':
                return Color.green;
            default:
                return Color.white;
        }
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
    int mouseClickedStartPos = -1;
    int mouseClickedEndPos = -1;
    int mouseClickedStartSeq = -1;
    int mouseClickedEndSeq = -1;

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        this.requestFocusInWindow();
        if (SwingUtilities.isLeftMouseButton(e)) {
            mouseClickedStartPos = (int) (e.getX() / blockWidth);
            mouseClickedEndPos = (int) (e.getX() / blockWidth);
            mouseClickedStartSeq = (int) ((e.getY()) / blockHeight);
            mouseClickedEndSeq = (int) ((e.getY()) / blockHeight);

            selectionStartPos = mouseClickedStartPos;
            selectionEndPos = mouseClickedEndPos;
            selectionStartSeq = mouseClickedStartSeq;
            selectionEndSeq = mouseClickedEndSeq;



            repaint();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (selectionStartPos == selectionEndPos && selectionStartSeq == selectionEndSeq && alignmentModel.getElementTypeAt(selectionStartSeq) == 1) {
            this.structureSelectionPos1 = selectionStartPos;
            int [] pairedSites = ((SecondaryStructureItem) alignmentModel.alignment.items.get(selectionStartSeq/2)).getPairedSites();
            
            structureSelectionPos2 = selectionStartPos < pairedSites.length ? pairedSites[selectionStartPos] - 1: -1;
            //System.out.println(selectionStartSeq+"\tXX"+structureSelectionPos1+"\t"+structureSelectionPos2);
            if(structureSelectionPos2 >= 0 && e.getClickCount() >= 2)
            {
                selectionStartPos = structureSelectionPos1 < structureSelectionPos2 ? structureSelectionPos1 : structureSelectionPos2;
                selectionEndPos = structureSelectionPos1 < structureSelectionPos2  ? structureSelectionPos2 : structureSelectionPos1;
                if(e.getClickCount() >= 3)
                {
                    selectionStartSeq = 0;
                    selectionEndSeq = alignmentModel.alignment.items.size()*2;
                }
            }
        } else {
            structureSelectionPos1 = -1;
            structureSelectionPos2 = -1;

        }
        repaint();
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        int gridX = (int) (e.getX() / blockWidth);
        int gridY = (int) ((e.getY()) / blockHeight);
        selectionStartPos = mouseClickedStartPos;
        selectionStartSeq = mouseClickedStartSeq;
        if (gridX < mouseClickedStartPos) {
            int temp = selectionStartPos;
            selectionStartPos = gridX;
            selectionEndPos = temp;
        } else {
            selectionEndPos = gridX;
        }

        if (gridY < mouseClickedStartSeq) {
            int temp = selectionStartSeq;
            selectionStartSeq = gridY;
            selectionEndSeq = temp;
        } else {
            selectionEndSeq = gridY;
        }

        Rectangle rect = getVisibleRect();
        double xamount = 0;
        if (e.getX() > rect.x + rect.getWidth()) {
            xamount = e.getX() - (rect.x + rect.getWidth());
        } else if (e.getX() < rect.x) {
            xamount = e.getX() - rect.x;
        }

        double yamount = 0;
        if (e.getY() > rect.y + rect.getHeight()) {
            yamount = e.getY() - (rect.y + rect.getHeight());
        } else if (e.getY() < rect.y) {
            yamount = e.getY() - rect.y;
        }

        if (xamount != 0 || yamount != 0) {
            fireMouseDraggedOffVisibleRegion((int) xamount, (int) yamount);
        }
        
        repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }
    protected EventListenerList listeners = new EventListenerList();

    public void addAlignmentPanelListener(AlignmentPanelListener listener) {
        listeners.add(AlignmentPanelListener.class, listener);
    }

    public void removeAlignmentPanelListener(AlignmentPanelListener listener) {
        listeners.remove(AlignmentPanelListener.class, listener);
    }

    public void fireMouseDraggedOffVisibleRegion(int x, int y) {
        Object[] listeners = this.listeners.getListenerList();
        // Each listener occupies two elements - the first is the listener class
        // and the second is the listener instance
        for (int i = 0; i < listeners.length; i += 2) {
            if (listeners[i] == AlignmentPanelListener.class) {
                ((AlignmentPanelListener) listeners[i + 1]).mouseDraggedOffVisibleRegion(x, y);
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        if (allowEditing) {
            for (int i = selectionStartSeq; i <= selectionEndSeq; i++) {
                StringBuffer sb = new StringBuffer(alignmentModel.getElementAt(i));
                alignmentModel.setSubItemAt(i, sb.insert(selectionStartPos, Utils.nChars('-', Math.abs(selectionStartPos - selectionEndPos) + 1)).toString());
            }
            repaint();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void alignmentSortOrderChanged(int oldOrder, int newOrder) {
        repaint();
    }

    @Override
    public void alignmentChanged(Alignment alignment) {
        repaint();
    }

    @Override
    public void itemStateDataChanged(AlignmentItem item) {
        repaint();
    }
}
