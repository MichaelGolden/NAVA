/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.analyses;

import java.io.*;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class InputStreamHandler extends Thread {

    private BufferedReader bufferedInputStream;
    private BufferedWriter bufferedOutputStream;
    
    public InputStreamHandler(InputStream stream, OutputStream outputStream) {
        bufferedInputStream = new BufferedReader(new InputStreamReader(stream));
        bufferedOutputStream = new BufferedWriter(new OutputStreamWriter(outputStream));
    }

    /**
     * Stream the data.
     */
    public void run() {
        try {        
            String textline = null;
            while ((textline = bufferedInputStream.readLine()) != null) {
                bufferedOutputStream.write(textline, 0, textline.length());
                bufferedOutputStream.write('\n');
            }
            bufferedOutputStream.close();
            bufferedInputStream.close();
        } catch (IOException ioe) {
        }
    }
}
