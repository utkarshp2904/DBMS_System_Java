package sql.processor;

import logging.events.CrashListener;
import logging.events.DatabaseListener;
import logging.events.GeneralLogListener;
import logging.events.QueryListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sql.sql.InternalQuery;
import dataFiles.db.databaseStructures;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;

public class DeleteProcessor implements IProcessor {

    static DeleteProcessor instance = null;
    static final CrashListener crashListener = new CrashListener();
    static final DatabaseListener databaseListener = new DatabaseListener();
    static final QueryListener queryListener = new QueryListener();
    static final Logger logger = LogManager.getLogger(CreateProcessor.class.getName());
    static final GeneralLogListener generalLogListener = new GeneralLogListener();

    private String username = null;
    private String database = null;

    public static DeleteProcessor instance(databaseStructures dbsinjectintomethod){
        if(instance == null){
            instance = new DeleteProcessor();
        }
        return instance;
    }

    public String getDatabase()
    {
        return database;
    }
    HashMap<String, HashMap<String, String>> all_rows;
    @Override
    public databaseStructures process(InternalQuery query, String q, String username, String database,databaseStructures dbs)
    {
        Instant start = Instant.now();
        Duration timeElapsed;
        Instant end;

        queryListener.recordEvent();
        String table = (String) query.get("table");

        if(!dbs.database_list.contains(table)) {
            crashListener.recordEvent();
            logger.info("Table does not exist");
        }

        this.database = database;
        String condition = (String) query.get("condition");
        table = table.trim();
        all_rows = dbs.databasedata.get(table);
        String conditions[] = condition.split(" ");

        String to_delete[] = new String[dbs.databasedata.get(table).size()];
        int i = 0;
        int successCount = 0;
        Integer numberOfrowsVisited = 0;
        for(String key : dbs.databasedata.get(table).keySet()){
            numberOfrowsVisited++;
            for(String key2 : dbs.databasedata.get(table).get(key).keySet()){
                numberOfrowsVisited++;
                if(conditions[1].equals("=")){
                   if(key2.equals(conditions[0]) && dbs.databasedata.get(table).get(key).get(key2).equals(conditions[2])){
                       to_delete[i] = key;
                       databaseListener.recordEvent();
                       logger.info("Successful deleted entry");
                       successCount++;
                   }
                }
                if(conditions[1].equals("!=")){
                    if(key2.equals(conditions[0]) && !dbs.databasedata.get(table).get(key).get(key2).equals(conditions[2])){
                        to_delete[i] = key;
                        databaseListener.recordEvent();
                        logger.info("Successful deleted entry");
                        successCount++;
                    }
                }
                if(conditions[1].equals(">")){
                    if(key2.equals(conditions[0]) && Integer.parseInt(dbs.databasedata.get(table).get(key).get(key2)) > Integer.parseInt(conditions[2])){
                        to_delete[i] = key;
                        databaseListener.recordEvent();
                        logger.info("Successful deleted entry");
                        successCount++;
                    }
                }
                if(conditions[1].equals("<")){
                    if(key2.equals(conditions[0]) && Integer.parseInt(dbs.databasedata.get(table).get(key).get(key2)) < Integer.parseInt(conditions[2])){
                        to_delete[i] = key;
                        databaseListener.recordEvent();
                        logger.info("Successful deleted entry");
                        successCount++;
                    }
                }
                if(conditions[1].equals(">=")){
                    if(key2.equals(conditions[0]) && Integer.parseInt(dbs.databasedata.get(table).get(key).get(key2)) >= Integer.parseInt(conditions[2])){
                        to_delete[i] = key;
                        databaseListener.recordEvent();
                        logger.info("Successful deleted entry");
                        successCount++;
                    }
                }
                if(conditions[1].equals("<=")){
                    if(key2.equals(conditions[0]) && Integer.parseInt(dbs.databasedata.get(table).get(key).get(key2)) <= Integer.parseInt(conditions[2])){
                        to_delete[i] = key;
                        databaseListener.recordEvent();
                        logger.info("Successful deleted entry");
                        successCount++;
                    }
                }
            }
            i = i + 1;
        }
        if(successCount == 0){
            crashListener.recordEvent();
            logger.info("Unsuccessful deletion operation");
        }

        for(int j = 0; j < to_delete.length; j++) {
            dbs.databasedata.get(table).remove(to_delete[j]);
        }
        end = Instant.now();
        timeElapsed = Duration.between(start, end);
        generalLogListener.generalLog(timeElapsed.toString(),numberOfrowsVisited.toString());
        return dbs;
    }
}