package sql.processor;

import logging.events.CrashListener;
import logging.events.DatabaseListener;
import logging.events.QueryListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sql.sql.InternalQuery;
import dataFiles.db.databaseStructures;
import java.util.ArrayList;
import java.util.HashMap;

public class InsertProcessor implements IProcessor
{
    static final Logger logger = LogManager.getLogger(InsertProcessor.class.getName());
    static final CrashListener crashListener = new CrashListener();
    static final DatabaseListener databaseListener = new DatabaseListener();
    static final QueryListener queryListener = new QueryListener();
    static InsertProcessor instance = null;

    private String username = null;
    private String database = null;

    public static InsertProcessor instance(databaseStructures dbsinjectintomethod)
    {
        if (instance == null) {
            instance = new InsertProcessor();
        }
        return instance;
    }

    public String getDatabase() {
        return database;
    }

    HashMap<String, String> rowdata;
    HashMap<String, HashMap<String, String>> all_rows;
    HashMap<String, HashMap<String, String>> first_entry;

    @Override
    public databaseStructures process(InternalQuery query, String q, String username, String database, databaseStructures dbs) {
        queryListener.recordEvent();
        first_entry = new HashMap<>();
        rowdata = new HashMap<>();
        this.username = username;
        this.database = database;
        String table = (String) query.get("table");
        String primaryKey = null;
        String foreignkeyValue = null;
        String[] foreignKeytable = new String[2];
        String[] foreignKeyReferencetable = new String[2];
        String[] columns = (String[]) query.get("columns");
        String[] values = (String[]) query.get("values");
        int temp = 0;

        if (dbs.foreignKey_Hashtable.keySet() != null)
        {
            for(String key : dbs.foreignKey_Hashtable.keySet())
            {
                foreignkeyValue = dbs.foreignKey_Hashtable.get(key);
                String[] foreignKey = foreignkeyValue.split(" ");
                foreignKeytable = foreignKey[0].split("\\(");
                foreignKeytable[1] = foreignKeytable[1].substring(0,foreignKeytable[1].length()-1);

                foreignKeyReferencetable = foreignKey[3].split("\\(");
                foreignKeyReferencetable[1] = foreignKeyReferencetable[1].substring(0,foreignKeyReferencetable[1].length()-1);

                if(foreignKeytable[0].equals(table))
                {
                    for(String row : dbs.databasedata.get(foreignKeyReferencetable[0]).keySet()) {
                        for(String col : dbs.databasedata.get(foreignKeyReferencetable[0]).get(row).keySet())
                        {
                            if(col.equals(foreignKeyReferencetable[1])) {
                                for(int i=0; i<values.length;i++) {
                                    if (dbs.databasedata.get(foreignKeyReferencetable[0]).get(row).get(col).equals(values[i])) {
                                        temp = 1;
                                    }
                                }
                            }}
                    }
                }}
        }

        if (dbs.primaryKey_Hashtable.containsKey(table)) {
            primaryKey = dbs.primaryKey_Hashtable.get(table);
        }

        ArrayList<String> uniqueItem = new ArrayList<>();
        if(dbs.databasedata.get(table) != null) {
            for (String row : dbs.databasedata.get(table).keySet())
            {
                for (String col : dbs.databasedata.get(table).get(row).keySet())
                {
                    if (col.equals(primaryKey))
                    {
                        uniqueItem.add(dbs.databasedata.get(table).get(row).get(col));
                    }
                }
            }
        }
        all_rows = dbs.databasedata.get(table);

        int flag = 0;
        if (all_rows == null) {
            flag = 1;
        }

        for (int i = 0; i < columns.length; i++) {
            if (columns[i].equals(primaryKey) && uniqueItem.contains(values[i])) {
                rowdata.clear();
                logger.info("Can't insert due to primary key constraints");
                break;
            }
            if(temp==1) {
                rowdata.put(columns[i], values[i]);
            }
            else{
                crashListener.recordEvent();
                logger.info("foreign key constraints failed");
            }
        }

        int hashmap_size = 0;
        if (flag == 1) {
            first_entry.put("row1", rowdata);
            databaseListener.recordEvent();
            dbs.databasedata.put(table, first_entry);
        } else {
            hashmap_size = all_rows.size();
            int next_row_to_enter = hashmap_size + 1;
            if(!rowdata.isEmpty()) {
                all_rows.put("row" + next_row_to_enter, rowdata);
            }
            databaseListener.recordEvent();
            dbs.databasedata.put(table, all_rows);
        }
        return dbs;
    }
}