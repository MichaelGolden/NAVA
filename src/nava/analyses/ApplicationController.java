/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.analyses;

import java.util.ArrayList;

/**
 *
 * @author Michael
 */
public class ApplicationController {

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
}
