/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.data.types;

import java.io.File;
import java.io.Serializable;
import java.nio.file.Path;
import java.util.ArrayList;
import javax.swing.Icon;
import nava.ui.FileSize;
import nava.ui.ProjectInfo;

/**
 *
 * @author Michael
 */
public abstract class DataSource implements Serializable
{
    // identifier
    protected static long count = 0;
    private long id = 0;
    private long importId = 0;
    
    ProjectInfo projectInfo;
    
    public File originalFile;
    public String originalDataSourcePath;
    public String importedDataSourcePath;
    
    // title and metadata
    public String title = "";
    public DataType dataType;
    public FileSize fileSize;
    
    public DataSource parentSource;
    
    public DataSource()
    {
        id = count;
        count++;
    }
    
    public long getId()
    {
        return id;
    }
    
    public static void setCount(long count)
    {
        DataSource.count = count;
    }
        
    public static long getCount()
    {
        return count;
    }    
    
    public long getImportId()
    {
        return importId;
    }    
    
    public void setImportId(long importId)
    {
        this.importId = importId;
    }
    
    public DataSource getParentDataSource()
    {
       return parentSource;
    }
    
    public void setParentDataSource(DataSource parentSource)
    {
        this.parentSource = parentSource;
    }
    
    public String getTitle()
    {
        return title;
    }
    
    
    @Override
    public String toString()
    {
        return getTitle();
    }
    
    public abstract Icon getIcon();
    public abstract<T extends DataSource> ArrayList<T> getChildren();
    public abstract Object getObject();
    public abstract Object getObject(DataSourceCache cache);
    public abstract void persistObject(Object object);
    public abstract String getTypeName();
}
