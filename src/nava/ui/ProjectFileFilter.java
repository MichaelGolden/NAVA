/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package nava.ui;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author Michael Golden
 */
public class ProjectFileFilter extends FileFilter {

    public boolean accept(File pathname) {
        return isProjectFolder(pathname);
    }

    @Override
    public String getDescription() {
        return "NAVA project";
    }

    public static boolean isProjectFolder(File file)
    {
        return new File(file.getAbsoluteFile()+"//project.data").exists();
    }
}
