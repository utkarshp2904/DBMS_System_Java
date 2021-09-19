package sql.sql;

import dataFiles.db.databaseStructures;
import logging.events.GeneralLogListener;
import sql.parser.*;
import sql.processor.*;
import sql.parser.UseParser;
import sql.processor.CreateProcessor;

import java.time.Duration;
import java.time.Instant;

public class QueryEngine {
    private String database = null;
    private databaseStructures databaseStructures;
    static final GeneralLogListener generalLogListener = new GeneralLogListener();
    Duration timeElapsed;
    Instant end;
    Instant start;

    public QueryEngine(databaseStructures dbs){
        this.databaseStructures =dbs;
    }

    public databaseStructures run(String query, String username,databaseStructures dbs)
    {

        this.databaseStructures = dbs;
        InternalQuery internalQuery = null;
        String action = query.replaceAll (" .*", "");
        action = action.toLowerCase ();
        boolean success = false;
        switch (action) {
            case "use":
                start = Instant.now();

                internalQuery = UseParser.instance ().parse (query);
                UseProcessor useProcessor = UseProcessor.instance(databaseStructures);
                this.databaseStructures = useProcessor.process (internalQuery, query, username, database, this.databaseStructures);
                this.database = useProcessor.getDatabase ();
                end = Instant.now();
                timeElapsed = Duration.between(start, end);
                generalLogListener.generalLog(timeElapsed.toString(),"1");
                break;

            case "create":
                start = Instant.now();

                internalQuery = CreateParser.instance ().parse (query);
                if (((String) internalQuery.get ("type")).equalsIgnoreCase ("database")) {
                    this.databaseStructures = CreateProcessor.instance ().process (internalQuery, query, username, this.database,this.databaseStructures);
                } else {
                    if (checkDbSelected ()) {
                        this.databaseStructures = CreateProcessor.instance ().process (internalQuery, query, username, this.database, this.databaseStructures);
                    }
                }
                end = Instant.now();
                timeElapsed = Duration.between(start, end);
                generalLogListener.generalLog(timeElapsed.toString(),"1");

                break;
            case "insert":
                start = Instant.now();

                if (checkDbSelected ()) {
                    internalQuery = InsertParser.instance ().parse (query);
                    InsertProcessor insertProcessor = InsertProcessor.instance(databaseStructures);
                    this.databaseStructures = insertProcessor.process(internalQuery, query, username, database, this.databaseStructures);
                    this.database = insertProcessor.getDatabase();
                }
                end = Instant.now();
                timeElapsed = Duration.between(start, end);
                generalLogListener.generalLog(timeElapsed.toString(),"1");

                break;
            case "delete":
                if (checkDbSelected ()) {
                    internalQuery = DeleteParser.instance ().parse (query);
                    DeleteProcessor deleteProcessor = DeleteProcessor.instance(databaseStructures);
                    this.databaseStructures = deleteProcessor.process(internalQuery, query, username, database, this.databaseStructures);
                    this.database = deleteProcessor.getDatabase();
                }

                break;

            case "update":

                if (checkDbSelected ()) {
                    internalQuery = UpdateParser.instance ().parse (query);
                    UpdateProcessor updateProcessor = UpdateProcessor.instance(databaseStructures);
                    if (internalQuery != null) {
                        this.databaseStructures = updateProcessor.process(internalQuery, query, username, this.database,this.databaseStructures);
                    }
                }

                break;

            case "drop":
                start = Instant.now();

                Instant start = Instant.now();
                if(checkDbSelected()) {
                    internalQuery = DropParser.instance().parse(query);
                    DropProcessor dropProcessor = DropProcessor.instance(databaseStructures);
                    this.databaseStructures = dropProcessor.process(internalQuery, query, username, database, this.databaseStructures);
                    this.database = dropProcessor.getDatabase();
                }

                end = Instant.now();
                timeElapsed = Duration.between(start, end);
                generalLogListener.generalLog(timeElapsed.toString(),"1");

                break;
            case "select":

                if (checkDbSelected ()) {
                    internalQuery = SelectParser.instance ().parse (query);
                    this.databaseStructures = SelectProcessor.instance ().process (internalQuery,query,username, database,this.databaseStructures);
                }

                break;
            default:
                System.out.println ("invalid query!");
        }
        return this.databaseStructures;
    }

    private boolean checkDbSelected() {
        database = "db";
        if (database == null) {
            System.out.println ("Please select a Database:");
            return false;
        } else {
            return true;
        }
    }
}
