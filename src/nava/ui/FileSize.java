/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.ui;

import java.io.Serializable;
import java.text.DecimalFormat;

/**
 *
 * @author Michael
 */
public class FileSize implements Serializable {
    public long bytes;
    
    public FileSize (long bytes)
    {
        this.bytes = bytes;
    }
    
    @Override
    public String toString()
    {
        return getFileSizeString(this.bytes);
    }
    
    public static DecimalFormat df = new DecimalFormat("0.0");
    public static String getFileSizeString(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        } else if (bytes < 1024 * 1024) {
            return df.format(((double) bytes) / 1024.0) + " KB";
        } else if (bytes < 1024 * 1024 * 1024){
            return df.format(((double) bytes) / 1024.0 / 1024.0) + " MB";
        }
        else {
            return df.format(((double) bytes) / 1024.0 / 1024.0 / 1024) + " GB";
        }
    }
}
