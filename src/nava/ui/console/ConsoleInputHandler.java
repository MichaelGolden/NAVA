/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.ui.console;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class ConsoleInputHandler extends Thread {

    ConsoleBuffer consoleBuffer;
    InputStream inputStream;
    String className;
    String typeName;

    public ConsoleInputHandler(ConsoleBuffer consoleBuffer, InputStream inputStream) {
        this.consoleBuffer = consoleBuffer;
        this.inputStream = inputStream;
        this.className = consoleBuffer.className;
        this.typeName = consoleBuffer.typeName;
        start();
    }

    public ConsoleInputHandler(ConsoleBuffer consoleBuffer, String className, String typeName, InputStream inputStream) {
        this.consoleBuffer = consoleBuffer;
        this.inputStream = inputStream;
        this.className = className;
        this.typeName = typeName;
        start();
    }

    @Override
    public void run() {
        try {

            BufferedReader buffer = new BufferedReader(new InputStreamReader(inputStream));
            String textline = null;
            while ((textline = buffer.readLine()) != null) {
                synchronized(consoleBuffer)
                {
                    consoleBuffer.bufferedWrite(textline, className, typeName);
                    System.out.println(className+"\t"+typeName+"\t"+textline);
                }
            }
            consoleBuffer.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
