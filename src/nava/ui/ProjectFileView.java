/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.ui;

import java.awt.Image;
import java.io.File;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.filechooser.FileView;

/**
 *
 * @author Michael Golden
 */
public class ProjectFileView extends FileView {

    Icon appIcon = new ImageIcon(getClass().getResource("/resources/icons/icon-32x32.png"));
   
    @Override
    public Icon getIcon(File file) {
        if (ProjectFileFilter.isProjectFolder(file)) {
            return appIcon;
        }
        return super.getIcon(file);
    }
}
