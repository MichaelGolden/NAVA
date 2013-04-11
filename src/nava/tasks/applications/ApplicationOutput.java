/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.tasks.applications;

import java.io.File;
import nava.data.types.DataSource;
import nava.data.types.DataType;
import nava.data.types.DataType.FileFormat;

/**
 *
 * @author Michael
 */
public class ApplicationOutput {
    
    // output can either be a file or an object
    public File file;    
    public FileFormat fileFormat;
    public Object object;
    
    public String description;
    public Application source;
    public DataSource dataSource;
}
