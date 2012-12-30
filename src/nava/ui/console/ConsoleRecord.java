/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.ui.console;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class ConsoleRecord {

    String className;
    String typeName;
    long lineNumber;
    String text;
    long time;

    public ConsoleRecord(String className, String typeName, String text, long time) {
        this.className = className;
        this.typeName = typeName;
        this.text = text;
        this.time = time;
    }

    public ConsoleRecord(String className, String typeName, long lineNumber, String text, long time) {
        this.className = className;
        this.typeName = typeName;
        this.lineNumber = lineNumber;
        this.text = text;
        this.time = time;
    }

    @Override
    public String toString() {
        return "ConsoleRecord{" + "className=" + className + ", typeName=" + typeName + ", lineNumber=" + lineNumber + ", text=" + text + ", time=" + time + '}';
    }
}
