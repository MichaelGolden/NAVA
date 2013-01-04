/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.analyses;

import java.util.List;
import nava.tasks.Task;
import nava.ui.ProjectController;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class ApplicationTask extends Task {

    Application application;
    ProjectController projectController;

    public ApplicationTask(Application application, ProjectController projectController) {
        this.application = application;
        this.projectController = projectController;
    }

    @Override
    public void before() {
        if (application.combinedBuffer != null) {
            application.combinedBuffer.bufferedWrite("Started.", application.appInstanceId, "console");
        }
    }

    @Override
    public void task() {
        application.start();
    }

    @Override
    public void after() {
        List<ApplicationOutput> output = application.getOutputFiles();
        for (int i = 0; i < output.size(); i++) {
            projectController.importDataSourceFromOutputFile(output.get(i));
        }

        if (application.combinedBuffer != null) {
            application.combinedBuffer.bufferedWrite("Finished.", application.appInstanceId, "console");
            application.combinedBuffer.close();
        }
    }

    @Override
    public List<ApplicationOutput> get() {
        return application.getOutputFiles();
    }

    public Application getApplication() {
        return application;
    }

    @Override
    public String getName() {
        return application.getName();
    }

    @Override
    public String getDescription() {
        return application.getDescription();
    }
}
