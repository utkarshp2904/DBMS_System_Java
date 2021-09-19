package sql.processor;

import dataFiles.db.databaseStructures;
import logging.events.CrashListener;
import logging.events.DatabaseListener;
import logging.events.QueryListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sql.sql.InternalQuery;

import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class UseProcessor implements IProcessor {
    static final Logger logger = LogManager.getLogger(UseProcessor.class.getName());
    static final QueryListener queryListener = new QueryListener();


    static UseProcessor instance = null;

    private String username = null;
    private String database = null;

    public static UseProcessor instance(databaseStructures dbsinjectintomethod){
        if(instance == null){
            instance = new UseProcessor();
        }
        return instance;
    }

    public String getDatabase() {
        return database;
    }

    @Override
    public databaseStructures process(InternalQuery query, String q, String username, String database,databaseStructures dbs)
    {
        queryListener.recordEvent();
        this.username = username;
        this.database = database;
        String newDatabase = (String) query.get("database");
        dbs.populateDatabaseData(newDatabase);
        if(dbs.selectedDb == null){

            logger.info("Selecting database '"+ newDatabase+"'");
        }
        else{

            logger.info("Changing database from '"+ dbs.selectedDb +"' to '"+ newDatabase+"'" );
        }
        dbs.selectedDb = newDatabase;
        return dbs;
    }
}