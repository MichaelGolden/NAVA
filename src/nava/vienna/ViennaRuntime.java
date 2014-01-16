/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.vienna;

import java.io.File;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class ViennaRuntime {
    public enum OS {WINDOWS, LINUX};
    
    OS currentOS;
    File binaryDirectory;
    public static final double defaultTempCelsius = 37;
    
    public ViennaRuntime(File binaryDirectory, OS currentOS)
    {
        this.binaryDirectory = binaryDirectory;
        this.currentOS = currentOS;        
    }
    
    public String getExecutablePath(String executableName)
    {
        switch(currentOS)
        {
            case WINDOWS:
                return "\""+binaryDirectory.getAbsolutePath()+File.separatorChar+executableName+".exe"+"\"";
            case LINUX:
                return "\""+"./"+binaryDirectory.getAbsolutePath()+File.separatorChar+executableName+"\"";                        
        }
        
        return null;
    }
    
    public void setBinaryDirectory(File binaryDirectory)
    {
        this.binaryDirectory = binaryDirectory;
    }
}
