/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.tasks.applications;

import java.io.File;
import nava.data.types.DataSource;

/**
 *
 * @author Michael
 */
public class ApplicationOutput {
    
    // output can either be a file or an object
    public File file;    
    public Object object;
    
    public String description;
    public Application source;
    public DataSource dataSource;
}
