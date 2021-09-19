package sql.processor;

import dataFiles.db.databaseStructures;
import logging.events.CrashListener;
import logging.events.DatabaseListener;
import logging.events.QueryListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sql.sql.InternalQuery;

public class DropProcessor implements IProcessor{

    static DropProcessor instance = null;
    static final CrashListener crashListener = new CrashListener();
    static final DatabaseListener databaseListener = new DatabaseListener();
    static final QueryListener queryListener = new QueryListener();
    static final Logger logger = LogManager.getLogger(CreateProcessor.class.getName());

    private String username = null;
    private String database = null;

    public static DropProcessor instance(databaseStructures dbsinjectintomethod)
    {
        if(instance == null)
        {
            instance = new DropProcessor();
        }
        return instance;
    }

    public String getDatabase()
    {
        return database;
    }

    @Override
    public databaseStructures process(InternalQuery query,  String q,String username, String database, databaseStructures dbs)
    {
        queryListener.recordEvent();

        this.username = username;
        this.database = database;

        String table = (String) query.get("table");
        table = table.trim();
        String to_remove = "";
        for(String key : dbs.databasedata.keySet())
        {
            if (key.equals(table)){
                to_remove = key;
            }
        }
        for (String key : dbs.tableStructure.keySet())
        {
            if(key.equals(table)){
                to_remove = key;
            }
        }
        dbs.databasedata.remove(to_remove);
        dbs.tableStructure.remove(to_remove);
        if(to_remove.equals(""))
        {
            crashListener.recordEvent();
            logger.info("No table found");
        }
        else {
            databaseListener.recordEvent();
            logger.info("Deleted table");
        }
        return dbs;
    }
}
