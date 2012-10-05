/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.analyses;

import java.util.List;
import nava.data.types.DataSource;

/**
 *
 * @author Michael
 */
public interface Application 
{    
    public void start();
    public void pause();
    public void cancel();
    
    public boolean canProcessDataSource(DataSource dataSource);
    public void setDataSource(DataSource dataSource);
    
    public boolean isStarted();
    public boolean isRunning();
    public boolean isCanceled();
    public boolean isFinished();
    
    public List<ApplicationOutput> getOutputFiles();
    
    public String getName();
    public String getDescription();
}
