/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.structurevis.navigator;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.tree.DefaultMutableTreeNode;
import nava.data.types.DataSource;
import nava.structurevis.data.Overlay;

/**
 *
 * @author Michael
 */
public class DataOverlayTreeNode extends DefaultMutableTreeNode implements Serializable {

    public boolean isFolder = false;
    public String title;
    public Overlay overlay;

    private DataOverlayTreeNode() {
    }

    public DataOverlayTreeNode(Overlay overlay) {
        super();
        this.overlay = overlay;
        this.title = overlay.title;
    }

    public static DataOverlayTreeNode createFolderNode(String title) {

        DataOverlayTreeNode node = new DataOverlayTreeNode();
        node.isFolder = true;
        node.title = title;
        return node;
    }

    public ImageIcon getIcon() {
        if (isFolder) {
            return new ImageIcon(ClassLoader.getSystemResource("resources/icons/gnome-folder-open-16x16.png"));
        } else {
            switch (overlay.getState()) {
                case PRIMARY_SELECTED:
                    /*
                     * ImageIcon dataSourceIcon = (ImageIcon) overlay.getIcon();
                     * BufferedImage newIcon = new
                     * BufferedImage(dataSourceIcon.getIconWidth()+3,
                     * dataSourceIcon.getIconHeight(),
                     * BufferedImage.TYPE_INT_ARGB); Graphics2D ng =
                     * (Graphics2D) newIcon.getGraphics(); ng.setColor(new
                     * Color(128, 255, 160, 255)); ng.fillRect(0,
                     * 0,2,dataSourceIcon.getIconHeight());
                     * ng.drawImage(dataSourceIcon.getImage(), 3, 0, null);
                     * return new ImageIcon(newIcon);
                     */

                    ImageIcon dataSourceIcon = (ImageIcon) overlay.getIcon();
                    ImageIcon eyeIcon = new ImageIcon(ClassLoader.getSystemResource("resources/icons/eye-13x7.png"));
                    BufferedImage icon = new BufferedImage(dataSourceIcon.getIconWidth(), dataSourceIcon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
                    Graphics2D ng = (Graphics2D) icon.getGraphics();
                    ng.drawImage(dataSourceIcon.getImage(), 0, 0, null);
                    ng.drawImage(eyeIcon.getImage(), icon.getWidth()/2-6, icon.getHeight() - 8, null);
                    return new ImageIcon(icon);

                case SECONDARY_SELECTED:
                    return (ImageIcon) overlay.getIcon();
                case UNSELECTED:
                    return (ImageIcon) overlay.getIcon();
                default:
                    return (ImageIcon) overlay.getIcon();
            }
        }
    }

    public String toString() {
        return "title=" + title + ", isfolder=" + this.isFolder;
    }
}
