/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.tasks;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class TestTask extends Task{

    @Override
    public void before() {
        
    }

    @Override
    public void task() {
        
    }

    @Override
    public void after() {
        
    }

    @Override
    public String getName() {
        return "Test task";
    }

    @Override
    public String getDescription() {
        return "A test task.";
    }

    @Override
    public Object get() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected void pause() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected void cancel() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected void resume() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
