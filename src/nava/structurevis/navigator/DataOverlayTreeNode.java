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

/**
 *
 * @author Michael
 */
public class DataOverlayTreeNode extends DefaultMutableTreeNode implements Serializable {

    public boolean isFolder = false;
    public boolean isNew = true;
    public String title;
    public DataSource dataSource;

    private DataOverlayTreeNode() {
    }

    public DataOverlayTreeNode(DataSource dataSource) {
        super();
        this.dataSource = dataSource;

        ArrayList<DataSource> children = this.dataSource.getChildren();
        for (int i = 0; i < children.size(); i++) {
           DataOverlayTreeNode childNode = new DataOverlayTreeNode(children.get(i));
           childNode.isNew = false;
           this.add(childNode);
        }
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
            if (isNew) {
                ImageIcon dataSourceIcon = (ImageIcon) dataSource.getIcon();
                BufferedImage newIcon = new BufferedImage(dataSourceIcon.getIconWidth()+3, dataSourceIcon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
                Graphics2D ng = (Graphics2D) newIcon.getGraphics();
                ng.setColor(new Color(128, 255, 160, 255));
                ng.fillRect(0, 0,2,dataSourceIcon.getIconHeight());
                ng.drawImage(dataSourceIcon.getImage(), 3, 0, null);
                 //ng.drawImage(new ImageIcon(ClassLoader.getSystemResource("resources/icons/star-7x7.png")).getImage(), dataSourceIcon.getIconWidth()-7, dataSourceIcon.getIconHeight()-7, null);
              //  ng.setColor(new Color(240, 200, 14, 220));
               // ng.drawString("*",dataSourceIcon.getIconWidth() - 5, 10);
                return new ImageIcon(newIcon);
            } else {
                return (ImageIcon) dataSource.getIcon();
            }
        }
    }

    public String toString() {
        return "title=" + title + ", isfolder=" + this.isFolder;
    }
}
