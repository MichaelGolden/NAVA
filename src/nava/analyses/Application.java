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

/**
 *
 * @author Michael
 */
public abstract class Application {

    public static int instanceCount = 0;
    int appInstanceId;
    //BufferedWriter standardBuffer;
    BufferedWriter errorBuffer;

    public Application() {
        instanceCount++;
        appInstanceId = instanceCount;

        /*
        try {
            standardBuffer = new BufferedWriter(new FileWriter(appInstanceId + ".app.out"));
            errorBuffer = new BufferedWriter(new FileWriter(appInstanceId + ".app.err"));
        } catch (IOException ex) {
            Logger.getLogger(Application.class.getName()).log(Level.SEVERE, null, ex);
        }
        * 
        */

    }

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
