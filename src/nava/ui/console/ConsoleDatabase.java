/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.ui.console;

import java.io.File;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.tmatesoft.sqljet.core.SqlJetException;
import org.tmatesoft.sqljet.core.SqlJetTransactionMode;
import org.tmatesoft.sqljet.core.table.ISqlJetCursor;
import org.tmatesoft.sqljet.core.table.ISqlJetTable;
import org.tmatesoft.sqljet.core.table.ISqlJetTransaction;
import org.tmatesoft.sqljet.core.table.SqlJetDb;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class ConsoleDatabase {

    String DB_NAME = "db.sqlite";
    private static final String CONSOLE_TABLE_NAME = "console";
    private static final String CLASS_FIELD = "class";
    private static final String TYPE_FIELD = "type";
    private static final String TEXT_FIELD = "text";
    private static final String TIMESTAMP_FIELD = "timestamp";
    private static final String CLASS_TYPE_INDEX = "classtypeindex";

    public static void main(String[] args) {
        try {
            ConsoleDatabase db = new ConsoleDatabase();
            ArrayList<ConsoleRecord> records = new ArrayList<>();
            records.add(new ConsoleRecord("app_123", "standard_out", "1", System.currentTimeMillis()));
            records.add(new ConsoleRecord("app_123", "standard_out", "2", System.currentTimeMillis()));
            records.add(new ConsoleRecord("app_123", "standard_out", "3", System.currentTimeMillis()));
            records.add(new ConsoleRecord("app_123", "standard_out", "4", System.currentTimeMillis()));
            records.add(new ConsoleRecord("app_123", "standard_err", "1", System.currentTimeMillis()));
            records.add(new ConsoleRecord("app_123", "standard_err", "2", System.currentTimeMillis()));
            records.add(new ConsoleRecord("app_123", "standard_err", "3", System.currentTimeMillis()));
            records.add(new ConsoleRecord("app_123", "standard_err", "4", System.currentTimeMillis()));
            records.add(new ConsoleRecord("app_123", "standard_err", "4", System.currentTimeMillis()));

            db.insertRecords(records);

            System.out.println(db.getConsoleRecordsFromDB("app_123", "standard_err", 2, 3));
        } catch (SqlJetException ex) {
            Logger.getLogger(ConsoleDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public ConsoleDatabase() {

        File dbFile = new File(DB_NAME);

        if (!dbFile.exists()) {
            try {
                //dbFile.delete();

                // create database, table and two indices:
                SqlJetDb db = SqlJetDb.open(dbFile, true);
                // set DB option that have to be set before running any transactions: 
                db.getOptions().setAutovacuum(true);
                // set DB option that have to be set in a transaction: 
                db.runTransaction(new ISqlJetTransaction() {

                    public Object run(SqlJetDb db) throws SqlJetException {
                        db.getOptions().setUserVersion(1);
                        return true;
                    }
                }, SqlJetTransactionMode.WRITE);


                db.beginTransaction(SqlJetTransactionMode.WRITE);
                try {
                    String createTableQuery = "CREATE TABLE " + CONSOLE_TABLE_NAME + " ("
                            //+ "id" + " INTEGER NOT NULL PRIMARY KEY, "
                            + CLASS_FIELD + " TEXT, "
                            + TYPE_FIELD + " TEXT, "
                            + TEXT_FIELD + " TEXT, "
                            + TIMESTAMP_FIELD + " INTEGER"
                            //+ "PRIMARY KEY (" + CLASS_FIELD + "," + TYPE_FIELD + "," + LINE_FIELD + ")" 
                            + ")";
                    String classTypeIndex = "CREATE INDEX " + CLASS_TYPE_INDEX + " ON " + CONSOLE_TABLE_NAME + "(" + CLASS_FIELD + "," + TYPE_FIELD + ")";

                    System.out.println(createTableQuery);
                    System.out.println(classTypeIndex);
                    //System.out.println(createFirstNameIndexQuery);
                    //System.out.println(createDateIndexQuery);

                    db.createTable(createTableQuery);
                    //System.out.println("CREATE INDEX " + "LINE_INDEX" + " ON " + TABLE_NAME + "(" + CLASS_FIELD + "," + TYPE_FIELD +","+LINE_FIELD+")");
                    db.createIndex("CREATE INDEX " + CLASS_TYPE_INDEX + " ON " + CONSOLE_TABLE_NAME + "(" + CLASS_FIELD + "," + TYPE_FIELD + ")");
                    //db.createIndex("CREATE INDEX " + "LINE_INDEX" + " ON " + CONSOLE_TABLE_NAME + "(" + CLASS_FIELD + "," + TYPE_FIELD + "," + LINE_FIELD + ")");
                    //db.createIndex(createDateIndexQuery);
                } finally {
                    db.commit();
                }
                // close DB and open it again (as part of example code)

                db.close();
            } catch (SqlJetException ex) {
                ex.printStackTrace();
            }
        }
    }

    public ArrayList<ConsoleRecord> getConsoleRecordsFromDB(String className, String typeName, int index, int len) {
        File dbFile = new File(DB_NAME);

        ArrayList<ConsoleRecord> records = new ArrayList<>();

        try {

            SqlJetDb db = SqlJetDb.open(dbFile, true);
            ISqlJetTable table = db.getTable(CONSOLE_TABLE_NAME);
            db.beginTransaction(SqlJetTransactionMode.READ_ONLY);
            
            ISqlJetCursor cursor = table.lookup(CLASS_TYPE_INDEX, className, typeName);
            //System.out.println
            cursor.goToRow(index + 1);
            if (!cursor.eof()) {
                for (int i = 0; i < len; i++) {
                    Object[] values = cursor.getRowValues();
                    ConsoleRecord record = new ConsoleRecord((String) values[0], (String) values[1], i + index, (String) values[2], (Long) values[3]);
                    records.add(record);
                    if (!cursor.next()) {
                        break;
                    }
                }
            }
            cursor.close();
            db.close();
        } catch (SqlJetException ex) {
            Logger.getLogger(ConsoleDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }

        return records;
    }

    public long getRowCount(String className, String typeName) {
        File dbFile = new File(DB_NAME);
        long rowCount = 0;
        try {
            SqlJetDb db = SqlJetDb.open(dbFile, true);
            ISqlJetTable table = db.getTable(CONSOLE_TABLE_NAME);
            db.beginTransaction(SqlJetTransactionMode.READ_ONLY);
            ISqlJetCursor cursor = table.lookup(CLASS_TYPE_INDEX, className, typeName);
            rowCount = cursor.getRowCount();
            cursor.close();
            db.close();
        } catch (SqlJetException ex) {
            Logger.getLogger(ConsoleDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }

        return rowCount;
    }

    public void insertRecords(ArrayList<ConsoleRecord> records) throws SqlJetException {        
        File dbFile = new File(DB_NAME);

        SqlJetDb db = SqlJetDb.open(dbFile, true);

        db.beginTransaction(SqlJetTransactionMode.WRITE);
        ISqlJetTable table = db.getTable(CONSOLE_TABLE_NAME);
        try {
            for (ConsoleRecord record : records) {
                table.insert(record.className, record.typeName, record.text, record.time);
            }
        } finally {
            db.commit();
        }
        db.close();
    }
    
    
    private static void printRecords(ISqlJetCursor cursor) throws SqlJetException {
        try {
            if (!cursor.eof()) {
                do {
                    System.out.println(cursor.getRowId() + " : " + 
                            cursor.getString(0) + " " + 
                            cursor.getString(1)+ " " + cursor.getString(2));
                } while(cursor.next());
            }
        } finally {
            cursor.close();
        }
    }
}
