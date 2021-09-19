package SQLDump;

import dataFiles.db.databaseStructures;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class SQLDump
{
    public void generateSQLDump(String username, String database, databaseStructures dbs)
    {
        File dumpfile = new File("src/main/java/SQLDumps/"+database+"DUMP.txt");

        try
        {
            FileWriter fw = new FileWriter(dumpfile, false);
            fw.close();
            FileWriter fr = new FileWriter(dumpfile,true);
            String query="";
            String table_query="";

            for(String key : dbs.tableStructure.keySet()){
               table_query ="CREATE TABLE '"+ key + "' (\n";
               String column_query="";
               for(String key2 : dbs.tableStructure.get(key).keySet()){
                   column_query = "'"+key2+"'";
                   switch (dbs.tableStructure.get(key).get(key2)){
                       case "int": column_query=column_query+" INT(),\n";
                           break;
                       case "String":column_query = column_query +" VARCHAR(),\n";
                           break;
                   }
                   table_query = table_query + column_query;
               }
               table_query = table_query + ")\n\n";
               fr.write(table_query);
            }
            fr.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}