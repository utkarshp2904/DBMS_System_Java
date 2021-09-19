package sql.processor;

import com.google.gson.internal.bind.SqlDateTypeAdapter;
import dataFiles.db.databaseStructures;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import logging.events.CrashListener;
import logging.events.DatabaseListener;
import logging.events.QueryListener;
import sql.sql.InternalQuery;

import java.io.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class CreateProcessor implements IProcessor {
    static final Logger logger = LogManager.getLogger(CreateProcessor.class.getName());
    static final CrashListener crashListener = new CrashListener();
    static final DatabaseListener databaseListener = new DatabaseListener();
    static final QueryListener queryListener = new QueryListener();
    private HashMap<String, String> primaryKey_Hashtable = new HashMap<String, String>();
    private HashMap<String, String> foreignKey_Hashtable = new HashMap<String, String>();
    static CreateProcessor instance = null;

    private boolean databaseExists = false;
    private String username = null;
    private String database = null;

    public static CreateProcessor instance(){
        if(instance == null){
            instance = new CreateProcessor();
        }
        return instance;
    }

    private databaseStructures createDB(InternalQuery internalQuery, String query, String username, String database, databaseStructures dbs)
    {
        queryListener.recordEvent();
        String name = (String) internalQuery.get("name");
        logger.info("Checking if database exists!");

        if(!dbs.database_list.contains(name)) {
            dbs.database_list.add(name);
            try{
                File structurefile = new File("src/main/java/dataFiles/db/"+name+"Data.txt");
                File structurefile2 = new File("src/main/java/dataFiles/db/"+name+"Structure.txt");
                structurefile.createNewFile();
                structurefile2.createNewFile();
            }
            catch(Exception e){
                e.printStackTrace();
                crashListener.recordEvent();
            }

            logger.info("DB "+name+" created successfully!");

            databaseListener.recordEvent();
        }
        else{
            logger.info("Sorry couldn’t create DB");
            logger.info("Duplicate database found!");
            crashListener.recordEvent();
        }
        return dbs;
    }

    private databaseStructures createTable(InternalQuery internalQuery, String query, String username, String database, databaseStructures dbs)
    {
        HashMap<String,String> datatable = new HashMap<String,String>(); // Create an ArrayList object
        queryListener.recordEvent();

        String name = (String) internalQuery.get("name");

        String location = (String) internalQuery.get("location");

        if(location.equals("local")){
            dbs.inlocal.add(name);
            logger.info("Generating data on local machine");
        }

        else if(location.contains("remote")){
            dbs.inremote.add(name);
            logger.info("Generating data on remote machine");
        }

        query = query.replaceAll(";", "");
        query = query.replaceAll(",", " ");
        query = query.replaceAll("\\(", " ").replaceAll("\\)"," ");
        query = query.replaceAll("[^a-zA-Z ]", "");
        query = query.replaceAll("  ", " ");

        String[] sqlWords = query.split(" ");

        int foreignIndex = 1;
        int foreignkeylocation = 0;
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        if(query.toLowerCase().contains("primary key")) {
            for(int i = 0; i< sqlWords.length; i++)
            {
                if(sqlWords[i].equalsIgnoreCase("primary")) {
                    dbs.primaryKey_Hashtable.put(name, sqlWords[i-2]);
                    i++;
                    logger.info("Successful primaryKey generation");
                }
            }
            List<String> list = new ArrayList<String>(Arrays.asList(sqlWords));
            list.remove("primary");
            list.remove("key");
            sqlWords = list.toArray(new String[0]);
        }

        if(query.toLowerCase().contains("foreign key")) {
            for(int i = 0; i< sqlWords.length; i++) {
                if(sqlWords[i].equalsIgnoreCase("foreign"))
                {
                    if(foreignkeylocation==0)
                        foreignkeylocation = i;

                    if(dbs.tableStructure.keySet().contains(sqlWords[i+4]))
                    {
                        String value = name + "(" + sqlWords[i+2] + ")" +" Reference To " + sqlWords[i+4] + "(" +sqlWords[i+5] + ")";
                        dbs.foreignKey_Hashtable.put("foreign key " + foreignIndex, value);
                        logger.info("Successful ForeignKey generation");
                    }
                    else {
                        logger.info("Unsuccessful foreignKey generation");
                    }
                    foreignIndex++;
                    i=i+7;
                }
            }
            String[] foreignKeys = Arrays.copyOfRange(sqlWords, 0, foreignkeylocation);
            sqlWords = foreignKeys;
        }

        for(int i = 4; i< sqlWords.length; i+=2)
        {
            datatable.put(sqlWords[i], sqlWords[i+1]);
        }

        logger.info("Adding indexes to table!");
        String tableName = (String) internalQuery.get("name");

        if(datatable!=null)
        {
            dbs.tableStructure.put(tableName, datatable);
            databaseListener.recordEvent();
            logger.info("Successful creation of data table");
        }
        else{
            logger.info("Unsuccessful creation of data table");
        }
        return dbs;
    }

    public databaseStructures process(InternalQuery internalQuery, String query, String username, String database, databaseStructures dbs) {

        this.username = username;
        this.database = database;

        if(internalQuery.get("type").equals("database"))
        {
            return createDB(internalQuery, query, username, database, dbs);
        }
        else {
            return createTable(internalQuery,query, username, database, dbs);
        }
    }
}