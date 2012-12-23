/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.analyses;

import java.util.ArrayList;
import nava.tasks.Task;
import nava.tasks.Task.Status;
import nava.tasks.TaskListener;
import nava.tasks.TaskManager;

/**
 *
 * @author Michael
 */
public class ApplicationController implements TaskListener {
    
    TaskManager taskManager;
    
    public ApplicationController(TaskManager taskManager)
    {
        this.taskManager = taskManager;
        taskManager.addTaskListener(this);
    }

    private ArrayList<Application> applications = new ArrayList<Application>();

    public void registerApplication(Application application) {
        if (!applications.contains(application)) {
            applications.add(application);
        }
    }
    
    public ArrayList<Application> getApplications()
    {
        return applications;
    }

    @Override
    public void taskProgressChanged(Task task, double progress) {
    }

    @Override
    public void taskStatusChanged(Task task, Status oldStatus, Status newStatus) {
        if(newStatus == Status.FINISHED)
        {
            if(task instanceof ApplicationTask)
            {
                ApplicationTask appTask = (ApplicationTask)task;
                System.out.println(appTask.getApplication().getName()+"Finished");
            }
        }
    }
    
    
}
