/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.alignment;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Line2D;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class SortPanel extends JPanel implements MouseListener {

    ImageIcon sortAscIcon = new ImageIcon(ClassLoader.getSystemResource("resources/icons/sort_asc.png"));
    ImageIcon sortDescIcon = new ImageIcon(ClassLoader.getSystemResource("resources/icons/sort_desc.png"));
    ImageIcon sortOrigIcon = new ImageIcon(ClassLoader.getSystemResource("resources/icons/sort_orig.png"));
    AlignmentModel alignmentModel;

    public SortPanel() {
        addMouseListener(this);
    }

    public void setModel(AlignmentModel alignmentModel) {
        this.alignmentModel = alignmentModel;
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        //g2.setFont(fontLiberationSans);
        g2.setColor(Color.white);
        g2.fill(new Rectangle.Double(0, 0, getWidth(), AlignmentPanel.rulerHeight));


        switch (AlignmentModel.sortOrder) {
            case AlignmentModel.ASCENDING:
                //g2.draw(new Line2D.Double(0, visibleRect.height, getWidth(), visibleRect.height));
                g2.drawImage(sortAscIcon.getImage(), 1, 1, this);
                break;
            case AlignmentModel.DESCENDING:
                //g2.draw(new Line2D.Double(0, visibleRect.height, getWidth(), visibleRect.height));
                g2.drawImage(sortDescIcon.getImage(), 1, 1, this);
                break;
            case AlignmentModel.NOT_SORTED:
                //g2.draw(new Line2D.Double(0, visibleRect.height, getWidth(), visibleRect.height));
                g2.drawImage(sortOrigIcon.getImage(), 1, 1, this);
                break;
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (alignmentModel != null) {
            switch (AlignmentModel.sortOrder) {
                case AlignmentModel.ASCENDING:
                    alignmentModel.sort(AlignmentModel.DESCENDING);
                    repaint();
                    break;
                case AlignmentModel.DESCENDING:
                    alignmentModel.sort(AlignmentModel.NOT_SORTED);
                    repaint();
                    break;
                case AlignmentModel.NOT_SORTED:
                    alignmentModel.sort(AlignmentModel.ASCENDING);
                    repaint();
                    break;
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
}
