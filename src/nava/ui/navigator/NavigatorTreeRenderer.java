/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.ui.navigator;

import java.awt.Color;
import java.awt.Component;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;
import nava.data.types.DataSource;

/**
 *
 * @author Michael
 */
public class NavigatorTreeRenderer implements TreeCellRenderer {

    JLabel label = new JLabel("Test");
    //JPanel panel = new JPanel();
    DefaultTreeCellRenderer defaultRenderer = new DefaultTreeCellRenderer();
    Color backgroundSelectionColor;
    Color backgroundNonSelectionColor;

    public NavigatorTreeRenderer() {
        //label.setForeground(Color.BLUE);

        backgroundSelectionColor = defaultRenderer.getBackgroundSelectionColor();
        backgroundNonSelectionColor = defaultRenderer.getBackgroundNonSelectionColor();
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        Component returnValue = null;
        if ((value != null) && (value instanceof NavigatorTreeNode)) {
            NavigatorTreeNode node = (NavigatorTreeNode) value;
            if (node.isFolder) {
                if (expanded) {
                    label.setIcon(new ImageIcon(ClassLoader.getSystemResource("resources/icons/gnome-folder-open-16x16.png")));
                    label.setText(node.title);
                } else {
                    label.setIcon(new ImageIcon(ClassLoader.getSystemResource("resources/icons/gnome-folder-16x16.png")));
                    label.setText(node.title);
                }
            } else {
                DataSource userObject = node.dataSource;
                
                label.setIcon(node.getIcon());
                label.setText(userObject.title);
            }
            
            label.setBorder(BorderFactory.createEmptyBorder(2, 0, 1, 0));
            if (selected) {
                label.setBackground(backgroundSelectionColor);
            } else {
                label.setBackground(backgroundNonSelectionColor);
            }
            label.setEnabled(tree.isEnabled());
            returnValue = label;
        }
        if (returnValue == null) {
            returnValue = defaultRenderer.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
        }
        return returnValue;
    }
}
