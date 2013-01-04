/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.ui.console;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.EventListenerList;
import nava.tasks.Task;
import nava.tasks.TaskListener;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class ConsoleBuffer extends Thread {

    ConsoleDatabase db;
    String className;
    String typeName;
    HashMap<Long, ConsoleRecord> rows = new HashMap<>();
    int lower = 0;
    int upper = 0;
    int bufferSize = 1000;
    double ratio = 0.5; // ratio above and below to load
    int upperBufferSize = (int) ((double) bufferSize * 0.25);
    int lowerBufferSize = bufferSize - upper;
    int n = 0;
    int maxLineChars = 0;

    public ConsoleBuffer(ConsoleDatabase db, String className, String typeName) {
        this.db = db;
        this.className = className;
        this.typeName = typeName;
        this.n = (int) db.getRowCount(className, typeName);
        System.out.println("N=" + this.n);
        start();
    }

    public void addRecords(ArrayList<ConsoleRecord> records) throws Exception {
        for (ConsoleRecord record : records) {
            if (Objects.equals(record.className, this.className) && Objects.equals(record.typeName, this.typeName)) {
                record.lineNumber = n;
                rows.put(new Long(n), record);
                n++;
            }
        }
        fireLineAdded(n);
        db.insertRecords(records);
    }

    /*
     * public void addLines(ArrayList<String> lines) throws Exception {
     * ArrayList<ConsoleRecord> records = new ArrayList<>();
     *
     * for (String line : lines) { records.add(new ConsoleRecord(className,
     * typeName, line, System.currentTimeMillis())); }
     *
     * db.insertRecords(records); n += records.size(); }
     */
    public ConsoleRecord getRecord(int index) {
        Long key = new Long(index);
        if (rows.containsKey(key)) {
        } else if (key < n) {

            int length = Math.min(lowerBufferSize + upperBufferSize, n);
            lower = Math.max(0, index - lowerBufferSize);
            upper = lower + length;
            ArrayList<ConsoleRecord> records = db.getConsoleRecordsFromDB(className, typeName, lower, length);
            for (int i = lower; i < upper && i - lower < records.size(); i++) {
                System.out.println("Inserting " + i);
                rows.put(new Long(i), records.get(i - lower));
            }

            Set<Long> keys = rows.keySet();
            Iterator<Long> keyIter = keys.iterator();
            ArrayList<Long> removeKeys = new ArrayList<>();
            Long k = null;
            while (keyIter.hasNext()) {
                k = keyIter.next();
                if (k < lower || k >= upper) {
                    removeKeys.add(k);
                }
            }
            for (Long removeKey : removeKeys) {
                System.out.println("Removing " + removeKey);
                rows.remove(removeKey);
            }
        }

        ConsoleRecord record = rows.get(key);
        if (record != null) {
            maxLineChars = Math.max(record.text.length(), maxLineChars);
        }

        return rows.get(key);
    }

    public ArrayList<ConsoleRecord> getRecords(int index, int length) {
        ArrayList<ConsoleRecord> records = new ArrayList<>();
        for (int i = index; i < index + length; i++) {
            ConsoleRecord record = getRecord(i);
            if (record != null) {
                records.add(record);
            } else {
                System.err.println("Record is null "+i);
            }
        }
        return records;
    }
    ArrayList<ConsoleRecord> writeBuffer = new ArrayList<>();
    int bufferMaxLines = 25;
    int bufferMaxTime = 10000; // 10 seconds
    int sleepTime = 200;
    int mod = bufferMaxTime / sleepTime;
    final Integer lock = new Integer(0);

    public void bufferedWrite(String line, String className, String typeName) {
        synchronized (lock) {
            writeBuffer.add(new ConsoleRecord(className, typeName, line, System.currentTimeMillis()));
        }
    }
    boolean running = true;

    @Override
    public void run() {
        for (int i = 1; running; i++) {
            if (i % mod == 0 || writeBuffer.size() >= bufferMaxLines) {

                System.out.println("ADDING RECORDS");
                if (!writeBuffer.isEmpty()) {
                    synchronized (lock) {
                        try {
                            addRecords(writeBuffer);
                            writeBuffer.clear();
                        } catch (Exception ex) {
                            Logger.getLogger(ConsoleBuffer.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        i = 1;
                    }
                }
                this.n = (int) db.getRowCount(this.className, this.typeName);
            }

            try {
                Thread.sleep(200);
            } catch (InterruptedException ex) {
                Logger.getLogger(ConsoleBuffer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void close() {
        synchronized (lock) {
            try {
                addRecords(writeBuffer);
                writeBuffer.clear();
                this.n = (int) db.getRowCount(this.className, this.typeName);
                running = false;
            } catch (Exception ex) {
                Logger.getLogger(ConsoleBuffer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    protected EventListenerList listeners = new EventListenerList();

    public void addConsoleListener(ConsoleListener listener) {
        listeners.add(ConsoleListener.class, listener);
    }

    public void removeConsoleListener(ConsoleListener listener) {
        listeners.remove(ConsoleListener.class, listener);
    }

    public void fireLineAdded(int totalLines) {
        Object[] listeners = this.listeners.getListenerList();
        // Each listener occupies two elements - the first is the listener class
        // and the second is the listener instance
        for (int i = 0; i < listeners.length; i += 2) {
            if (listeners[i] == ConsoleListener.class) {
                ((ConsoleListener) listeners[i + 1]).lineAddedEvent(totalLines);
            }
        }
    }

    public static void main(String[] args) {
        ConsoleBuffer consoleBuffer = new ConsoleBuffer(new ConsoleDatabase(), "app", "standard_out");
        consoleBuffer.bufferedWrite("1A", "app", "standard_out");
        System.out.println(consoleBuffer.getRecords(0, 5));

    }
}
