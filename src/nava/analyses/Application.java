/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.analyses;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import nava.data.types.DataSource;
import nava.ui.console.ConsoleBuffer;
import nava.ui.console.ConsoleDatabase;
import nava.ui.console.ConsoleInputHandler;

/**
 *
 * @author Michael
 */
public abstract class Application {

    public static long runtimeId = System.currentTimeMillis();
    public static long instanceCount = 0;
    public static ConsoleDatabase consoleDatabase = new ConsoleDatabase();
    String appInstanceId;
    
    public ConsoleBuffer consoleInputBuffer;
    public ConsoleBuffer consoleErrorBuffer;
    private ConsoleInputHandler consoleInputHandler;
    private ConsoleInputHandler consoleErrorHandler;
    //BufferedWriter standardBuffer;
    //BufferedWriter errorBuffer;

    public Application() {
        instanceCount++;
        appInstanceId = runtimeId+"_"+instanceCount + "";
        
        consoleInputBuffer = new ConsoleBuffer(consoleDatabase, appInstanceId, "standard_out");
        consoleErrorBuffer = new ConsoleBuffer(consoleDatabase, appInstanceId, "standard_err");
    }
    
    public void startConsoleInputBuffer(Process process)
    {
        consoleInputHandler = new ConsoleInputHandler(consoleInputBuffer, process.getInputStream());
    }
    
    public void startConsoleErrorBuffer(Process process)
    {
        consoleErrorHandler = new ConsoleInputHandler(consoleErrorBuffer, process.getErrorStream());
    }
    
    /*
    public void closeBuffers ()
    {
        consoleInputBuffer.close();
        consoleErrorBuffer.close();
    }*/

    public abstract void start();

    public abstract void pause();

    public abstract void cancel();

    public abstract boolean canProcessDataSource(DataSource dataSource);

    public abstract void setDataSource(DataSource dataSource);

    public abstract boolean isStarted();

    public abstract boolean isRunning();

    public abstract boolean isCanceled();

    public abstract boolean isFinished();

    public abstract List<ApplicationOutput> getOutputFiles();

    public abstract String getName();

    public abstract String getDescription();

    /*
    public void writeStandardOutput(String s) {
        try {
            standardBuffer.write(s + "\n");
        } catch (IOException ex) {
            Logger.getLogger(Application.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void writeErrorOutput(String s) {
        try {
            errorBuffer.write(s + "\n");
        } catch (IOException ex) {
            Logger.getLogger(Application.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void close() {
        try {
            standardBuffer.close();
            errorBuffer.close();
        } catch (IOException ex) {
            Logger.getLogger(Application.class.getName()).log(Level.SEVERE, null, ex);
        }
    }*/
}
