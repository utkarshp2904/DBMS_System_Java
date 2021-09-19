package sql.processor;

import dataFiles.db.databaseStructures;
import logging.events.CrashListener;
import logging.events.DatabaseListener;
import logging.events.GeneralLogListener;
import logging.events.QueryListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sql.sql.InternalQuery;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;

public class SelectProcessor implements IProcessor
{
    static final Logger logger = LogManager.getLogger(InsertProcessor.class.getName());
    static final CrashListener crashListener = new CrashListener();
    static final DatabaseListener databaseListener = new DatabaseListener();
    static final QueryListener queryListener = new QueryListener();
    static final GeneralLogListener generalLogListener = new GeneralLogListener();

    static SelectProcessor instance = null;

    private String username = null;
    private String database = null;

    public static SelectProcessor instance(){
        if(instance == null){
            instance = new SelectProcessor();
        }
        return instance;
    }

    @Override
    public databaseStructures process(InternalQuery query, String query1, String username, String database, databaseStructures dbs) {


        this.username = username;
        this.database = database;

        queryListener.recordEvent();
        Instant start = Instant.now();
        Duration timeElapsed;
        Instant end;
        String table = (String) query.get("table");
        String[] columns = (String[]) query.get("columns");
        String conditions = (String) query.get("conditions");

        logger.info("Identifying requested columns");
        Integer numberOfrowsVisited = 0;

        if(columns.length ==1 && columns[0].equals("*")) {
            ArrayList<String> allcolumns = new ArrayList<>();
            for(String key : dbs.databasedata.get(table).get("row1").keySet()){
                allcolumns.add(key);
            }
            displayColumns(allcolumns);


            for(String key : dbs.databasedata.get(table).keySet()){
                numberOfrowsVisited++;
                String row_Values = "";

                if(conditions.equals("")){
                    for(String key2 : dbs.databasedata.get(table).get(key).keySet()){
                        numberOfrowsVisited++;
                            row_Values = row_Values +"|"+ dbs.databasedata.get(table).get(key).get(key2);
                    }
                }else{
                    int conditionsSatisfied =0;
                    String[] andConditions = conditions.split(" and ");
                    for(int j=0; j<andConditions.length; j++) {
                        numberOfrowsVisited++;
                        String[] orConditions = andConditions[j].split(" or ");
                        boolean satisfied = false;
                        for (int k = 0; k < orConditions.length; k++) {
                            numberOfrowsVisited++;
                            String condition = orConditions[k];
                            if (condition.contains("!=")) {
                                String[] conditionParts = condition.split("!=");
                                String column = conditionParts[0];
                                String value = conditionParts[1].replace("'", "").replace("\"", "");
                                if (!dbs.databasedata.get(table).get(key).get(column).equals(value)) {
                                    satisfied = true;
                                }
                            }
                            else if (condition.contains("=")) {
                                String[] conditionParts = condition.split("=");
                                String column = conditionParts[0];
                                String value = conditionParts[1].replace("'", "").replace("\"", "");
                                if (dbs.databasedata.get(table).get(key).get(column).equals(value)) {
                                    satisfied = true;
                                }
                            } else if (condition.contains(" like ")) {
                                String[] conditionParts = condition.split(" like ");
                                String column = conditionParts[0];
                                String regex = conditionParts[1].replace("'", "").replace("\"", "");
                                ;
                                String value = dbs.databasedata.get(table).get(key).get(column);
                                if (value.matches(regex)) {
                                    satisfied = true;
                                }
                            }
                        }
                        if (satisfied) {
                            conditionsSatisfied++;
                        }
                    }
                    if(conditionsSatisfied == andConditions.length){
                        for(String key2 : dbs.databasedata.get(table).get(key).keySet()){
                                row_Values = row_Values +"|"+ dbs.databasedata.get(table).get(key).get(key2);
                        }
                    }
                }
                if(row_Values.equals("")){
                    crashListener.recordEvent();
                    logger.info("Unsuccessful performance of select statement");
                    continue;
                }else{
                    databaseListener.recordEvent();
                    logger.info("Successfully performance the select statement");
                    System.out.println(row_Values+"|");
                }
            }
        }

        end = Instant.now();
        timeElapsed = Duration.between(start, end);

        generalLogListener.generalLog(timeElapsed.toString(),numberOfrowsVisited.toString());
        return dbs;
    }

    public void displayColumns(ArrayList<String> columns_name){
        String all_names = "|";
        for(String name : columns_name){
            all_names = all_names + name+"|";
        }
        System.out.print(all_names+"\n");
    }
}

