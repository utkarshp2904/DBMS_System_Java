package sql.processor;
import dataFiles.db.databaseStructures;
import logging.events.DatabaseListener;
import logging.events.GeneralLogListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import logging.events.QueryListener;
import sql.sql.InternalQuery;

import java.time.Duration;
import java.time.Instant;

public class UpdateProcessor implements IProcessor
{
    static final Logger logger = LogManager.getLogger(InsertProcessor.class.getName());
    static final DatabaseListener databaseListener = new DatabaseListener();
    static final QueryListener queryListener = new QueryListener();
    static final GeneralLogListener generalLogListener = new GeneralLogListener();

    static UpdateProcessor instance = null;

    private String username = null;
    private String database = null;

    public static UpdateProcessor instance(databaseStructures dbsinjectintomethod) {
        if (instance == null) {
            instance = new UpdateProcessor ();
        }
        return instance;
    }

    public String getDatabase(){
        return database;
    }

    public databaseStructures process(InternalQuery query, String q, String username, String database, databaseStructures dbs)
    {
        queryListener.recordEvent();
        Instant start = Instant.now();
        Duration timeElapsed;
        Instant end;

        this.username = username;
        this.database = database;
        String name = query.getTableName();
        String option = query.getOption();
        String condition = query.getCondition();

        String conditions[] = condition.split(" ");
        String options[] = option.split(" ");

        name = name.trim();
        logger.info("Identifying requested columns");
        int successfulcount = 0;
        int i = 0;
        Integer numberOfrowsVisited = 0;

        for(String key : dbs.databasedata.get(name).keySet()){
            numberOfrowsVisited++;
            for(String key2 : dbs.databasedata.get(name).get(key).keySet()){
                numberOfrowsVisited++;
                if(conditions[1].equals("=")){
                    if(key2.equals(conditions[0]) && dbs.databasedata.get(name).get(key).get(key2).equals(conditions[2])){
                        System.out.println("update:" +conditions[0]);
                        dbs.databasedata.get(name).get(key).put(options[0], options[2]);
                        databaseListener.recordEvent();
                        logger.info("updated entry successfully");
                        successfulcount++;
                    }
                }
                else if(conditions[1].equals(">")){
                    if(key2.equals(conditions[0]) && Integer.parseInt(dbs.databasedata.get(name).get(key).get(key2)) > Integer.parseInt(conditions[2])){
                        dbs.databasedata.get(name).get(key).put(options[0], options[2]);
                        databaseListener.recordEvent();
                        logger.info("updated entry successfully");
                        successfulcount++;
                    }
                }
                else if(conditions[1].equals("<")){
                    if(key2.equals(conditions[0]) && Integer.parseInt(dbs.databasedata.get(name).get(key).get(key2)) < Integer.parseInt(conditions[2])){
                        dbs.databasedata.get(name).get(key).put(options[0], options[2]);
                        databaseListener.recordEvent();
                        logger.info("updated entry successfully");
                        successfulcount++;
                    }
                }
                else if(conditions[1].equals("!=")){
                    if(key2.equals(conditions[0]) && !dbs.databasedata.get(name).get(key).get(key2).equals(conditions[2])){
                        dbs.databasedata.get(name).get(key).put(options[0], options[2]);
                        databaseListener.recordEvent();
                        logger.info("updated entry successfully");
                        successfulcount++;
                    }
                }
                else if(conditions[1].equals(">=")){
                    if(key2.equals(conditions[0]) && Integer.parseInt(dbs.databasedata.get(name).get(key).get(key2)) >= Integer.parseInt(conditions[2])){
                        dbs.databasedata.get(name).get(key).put(options[0], options[2]);
                        databaseListener.recordEvent();
                        logger.info("updated entry successfully");
                        successfulcount++;
                    }
                }
                else if(conditions[1].equals("<=")){
                    if(key2.equals(conditions[0]) && Integer.parseInt(dbs.databasedata.get(name).get(key).get(key2)) <= Integer.parseInt(conditions[2])){
                        dbs.databasedata.get(name).get(key).put(options[0], options[2]);
                        databaseListener.recordEvent();
                        logger.info("updated entry successfully");
                        successfulcount++;
                    }
                }
            }
            i = i + 1;
        }
        if(successfulcount==0){
            logger.info("update query performed unsuccessfully");
        }
        end = Instant.now();
        timeElapsed = Duration.between(start, end);

        generalLogListener.generalLog(timeElapsed.toString(),numberOfrowsVisited.toString());
        return dbs;
    }
}