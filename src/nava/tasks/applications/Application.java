/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.tasks.applications;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JDialog;
import javax.swing.JPanel;
import nava.data.types.DataSource;
import nava.tasks.Task;
import nava.ui.MainFrame;
import nava.ui.console.ConsoleBuffer;
import nava.ui.console.ConsoleDatabase;
import nava.ui.console.ConsoleInputHandler;
import nava.utils.GraphicsUtils;

/**
 *
 * @author Michael
 */
public abstract class Application extends Task {

    public static long appRuntimeID = System.currentTimeMillis();
    public static long appInstanceCount = 0;
    public static ConsoleDatabase consoleDatabase = new ConsoleDatabase();
    String appInstanceId;
    private ConsoleInputHandler consoleInputHandler;
    private ConsoleInputHandler consoleErrorHandler;
    private JPanel applicationPanel;
    private ApplicationPanelInterface applicationPanelInterface;

    //BufferedWriter standardBuffer;
    //BufferedWriter errorBuffer;
    public Application() {
        super();

        appInstanceCount++;
        appInstanceId = appRuntimeID + "_" + appInstanceCount + "";


        combinedBuffer = new ConsoleBuffer(consoleDatabase, taskInstanceId, null);
        //consoleInputBuffer = new ConsoleBuffer(consoleDatabase, appInstanceId, "standard_out");
        //consoleErrorBuffer = new ConsoleBuffer(consoleDatabase, appInstanceId, "standard_err");
    }

    public void setApplicationPanel(JPanel applicationPanel) {
        if (applicationPanel instanceof ApplicationPanelInterface) {
            this.applicationPanel = applicationPanel;
            this.applicationPanelInterface = (ApplicationPanelInterface) applicationPanel;
        }
    }

    public void startConsoleInputBuffer(Process process) {
        consoleInputHandler = new ConsoleInputHandler(combinedBuffer, taskInstanceId, "standard_out", process.getInputStream());
        //consoleInputHandler = new ConsoleInputHandler(consoleInputBuffer, process.getInputStream());
    }

    public void startConsoleErrorBuffer(Process process) {
        consoleErrorHandler = new ConsoleInputHandler(combinedBuffer, taskInstanceId, "standard_err", process.getErrorStream());
        //consoleErrorHandler = new ConsoleInputHandler(consoleInputBuffer, process.getErrorStream());        
    }

    public static void nullOutput(final InputStream inputStream) {
        new Thread() {

            @Override
            public void run() {
                try {

                    BufferedReader buffer = new BufferedReader(new InputStreamReader(inputStream));
                    String textline = null;
                    while ((textline = buffer.readLine()) != null) {
                    }
                    buffer.close();
                } catch (IOException ex) {
                }
            }
        }.start();
    }

    protected abstract void start();

    //public abstract boolean canProcessDataSource(DataSource dataSource);
    public boolean canProcessDataSource(DataSource dataSource) {
        ArrayList<DataSource> dataSources = new ArrayList<>();
        dataSources.add(dataSource);
        return canProcessDataSources(dataSources);
    }

    public abstract boolean canProcessDataSources(List<DataSource> dataSources);

    public abstract void setDataSource(DataSource dataSource);

    /*
     * public abstract boolean isStarted();
     *
     * public abstract boolean isRunning();
     *
     * public abstract boolean isCanceled();
     *
     * public abstract boolean isFinished();
     */
    public abstract List<ApplicationOutput> getOutputFiles();
    boolean shouldRun = true;

    @Override
    public void before() {
        if (applicationPanel != null && applicationPanel instanceof ApplicationPanelInterface) {
            ApplicationPanelInterface appPanel = (ApplicationPanelInterface) applicationPanel;
            ApplicationDialog d = new ApplicationDialog(MainFrame.self, true, applicationPanel);
            GraphicsUtils.centerWindowOnWindow(d, MainFrame.self);
            d.setVisible(true);
            this.shouldRun = d.shouldRunOnDialogClose;            
            appPanel.setupApplication(this);
        }

        if (combinedBuffer != null) {
            if (this.shouldRun) {
                combinedBuffer.bufferedWrite("Started.", taskInstanceId, "console");
            } else {
                combinedBuffer.bufferedWrite("Cancelled.", taskInstanceId, "console");
                setStatus(Status.STOPPED);
            }
        }
    }

    @Override
    public void task() {
        if (shouldRun) {
            start();
        }
    }

    @Override
    public void after() {
        if (combinedBuffer != null) {
            combinedBuffer.bufferedWrite("Finished.", taskInstanceId, "console");
            combinedBuffer.close();
        }
    }

    @Override
    public List<ApplicationOutput> get() {
        return getOutputFiles();
    }

    public File createTemporaryDirectory() {
        File tempDir = new File(System.getProperty("java.io.tmpdir") + "/" + taskInstanceId + "/");
        tempDir.mkdirs();
        return tempDir;
    }
}
