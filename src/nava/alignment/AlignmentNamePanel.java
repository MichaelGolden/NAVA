/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.alignment;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import javax.swing.*;
import nava.utils.GraphicsUtils;
import nava.utils.Utils;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class AlignmentNamePanel extends javax.swing.JPanel implements ActionListener, MouseListener {

    AlignmentModel alignmentModel;
    Color fontNumberingColor = Color.gray;
    Color fontColor = Color.black;
    Rectangle visibleRect = null;
    ImageIcon tickSelected = new ImageIcon(ClassLoader.getSystemResource("resources/icons/tick_on_12x12.png"));
    ImageIcon tickUnselected = new ImageIcon(ClassLoader.getSystemResource("resources/icons/tick_off_12x12.png"));
    ArrayList<ItemAndRectangle<AlignmentItem>> tickMarks = new ArrayList<>();
    ArrayList<ItemAndRectangle<AlignmentItem>> itemAreas = new ArrayList<>();
    JPopupMenu popupMenu = new JPopupMenu();
    JMenuItem nameItem = new JMenuItem("Edit name");
    JMenuItem removeItem = new JMenuItem("Remove");
    JMenuItem removeSelectedItem = new JMenuItem("Remove selected");
    JMenuItem removeUnselectedItem = new JMenuItem("Remove unselected");

    class ItemAndRectangle<T> {

        T item;
        Rectangle2D rect;

        public ItemAndRectangle(T item, Rectangle2D rect) {
            this.item = item;
            this.rect = rect;
        }
    }

    /**
     * Creates new form SequenceNamePanel
     */
    public AlignmentNamePanel(AlignmentModel alignmentModel) {
        initComponents();
        this.alignmentModel = alignmentModel;

        addMouseListener(this);

        nameItem.addActionListener(this);
        popupMenu.add(nameItem);

        removeItem.addActionListener(this);
        popupMenu.add(removeItem);

        removeSelectedItem.addActionListener(this);
        popupMenu.add(removeSelectedItem);

        removeUnselectedItem.addActionListener(this);
        popupMenu.add(removeUnselectedItem);
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setFont(AlignmentPanel.fontDroidSansMono);

        g2.setColor(Color.white);
        g2.fill(new Rectangle2D.Double(0, 0, getWidth(), getHeight()));

        if (visibleRect != null) {
            int startSeq = (int) (visibleRect.y / AlignmentPanel.blockHeight);
            int endSeq = Math.min(startSeq + (int) (visibleRect.height / AlignmentPanel.blockHeight) + 2, alignmentModel.getSize());
            double yoffset = (int) (startSeq * AlignmentPanel.blockHeight) - visibleRect.y;

            int padding = (int) Math.log10(alignmentModel.getSize()) + 2;
            int j = 0;
            int n = 0;
            tickMarks = new ArrayList<>();
            itemAreas = new ArrayList<>();
            for (int seq = startSeq; seq < endSeq; seq++) {
                AlignmentModel.ItemRange itemRange = alignmentModel.getItemRange(seq);
                double y = yoffset + j * AlignmentPanel.blockHeight;
                double xoffset = 16;
                if (itemRange.mod == 0) {

                    AlignmentItem item = alignmentModel.getItemAt(seq);
                    if (item.selected) {
                        g2.drawImage(tickSelected.getImage(), 2, (int) (y + AlignmentPanel.blockHeight / 2 - 7) + 2, this);
                    } else {
                        g2.drawImage(tickUnselected.getImage(), 2, (int) (y + AlignmentPanel.blockHeight / 2 - 7) + 2, this);
                    }
                    tickMarks.add(new ItemAndRectangle(alignmentModel.getItemAt(seq), new Rectangle2D.Double(2, (int) (y + AlignmentPanel.blockHeight / 2 - 7) + 2, 12, 12)));
                    itemAreas.add(new ItemAndRectangle(alignmentModel.getItemAt(seq), new Rectangle2D.Double(0, y, getWidth(), itemRange.length * AlignmentPanel.blockHeight)));
                    // draw horizontal line
                    g2.setColor(new Color(255, 130, 130));
                    Line2D.Double hr = new Line2D.Double(0, y, getWidth(), y);
                    g2.draw(hr);
                    // draw seq no
                    g2.setColor(fontNumberingColor);
                    GraphicsUtils.drawStringVerticallyCentred(g2, xoffset + 4, y + (AlignmentPanel.blockHeight / 2), Utils.padStringRight((alignmentModel.itemCount[seq] + 1) + "", padding, ' '));

                    g2.setColor(fontColor);
                    GraphicsUtils.drawStringVerticallyCentred(g2, xoffset + 4, y + (AlignmentPanel.blockHeight / 2), Utils.padStringRight("", padding, ' ') + alignmentModel.getElementNameAt(seq) + "");

                    double legendKeyWidth = 12;
                    double legendKeyHeight = 12;
                    Rectangle2D.Double legendKeyRect = new Rectangle2D.Double(getWidth() - 2 - legendKeyWidth, y + (AlignmentPanel.blockHeight / 2) - (legendKeyWidth / 2), legendKeyWidth, legendKeyHeight);
                    g2.setColor(alignmentModel.getItemAt(seq).color);
                    g2.fill(legendKeyRect);
                    g2.setColor(Color.black);
                    g2.draw(legendKeyRect);
                }



                j++;
            }

            g2.setColor(this.getBackground());
            g2.fill(new Rectangle.Double(0, visibleRect.height, getWidth(), 100));
        }
    }

    /**
     * Set the range of sequences to display.
     *
     * @param startSeq
     * @param endSeq
     */
    public void setVisibleRect(Rectangle visibleRect) {
        this.visibleRect = visibleRect;
        repaint();
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
            .addGap(0, 130, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    AlignmentItem popupItem = null;

    @Override
    public void mouseClicked(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            for (ItemAndRectangle<AlignmentItem> tickMark : tickMarks) {
                if (tickMark.rect.contains(e.getX(), e.getY())) {
                    tickMark.item.selected = !tickMark.item.selected;
                    alignmentModel.fireAlignmentStateDataChanged(tickMark.item);
                }
            }
        } else if (SwingUtilities.isRightMouseButton(e)) {
            for (ItemAndRectangle<AlignmentItem> itemArea : itemAreas) {
                if (itemArea.rect.contains(e.getX(), e.getY())) {
                    popupItem = itemArea.item;
                    popupMenu.show(this, e.getX(), e.getY());
                }
            }
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
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(removeItem)) {
            alignmentModel.alignment.items.remove(popupItem);
            alignmentModel.setAlignment(alignmentModel.alignment);
        }
        else
        if (e.getSource().equals(removeSelectedItem)) {
            for (int i = 0; i < alignmentModel.alignment.items.size(); i++) {
                AlignmentItem item = (AlignmentItem) alignmentModel.alignment.items.get(i);
                if(item.selected)
                {
                     alignmentModel.alignment.items.remove(item);
                     i--;
                }
            }
            alignmentModel.setAlignment(alignmentModel.alignment);
        }
        else
        if (e.getSource().equals(removeUnselectedItem)) {
             for (int i = 0; i < alignmentModel.alignment.items.size(); i++) {
                AlignmentItem item = (AlignmentItem) alignmentModel.alignment.items.get(i);
                if(!item.selected)
                {
                     alignmentModel.alignment.items.remove(item);
                     i--;
                     System.out.println(">>>>"+i+"\t"+alignmentModel.alignment.items.size());
                }
            }
            alignmentModel.setAlignment(alignmentModel.alignment);
        } else if (e.getSource().equals(nameItem)) {

            String s = (String) JOptionPane.showInputDialog(
                    this,
                    "Enter a new name:",
                    "Edit name",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    null,
                    popupItem.name);
            if (s != null) {
                popupItem.name = s;
                alignmentModel.setAlignment(alignmentModel.alignment);
            }

        }
    }
}
