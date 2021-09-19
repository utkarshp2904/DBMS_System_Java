package DataDictionary;

import dataFiles.db.databaseStructures;
import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.List;

public class DataDictionary {
    databaseStructures dbs = new databaseStructures();
    public void generateDataDictionary(String database){
        dbs.populateDatabaseData(database);
        List<String> in_local = Arrays.asList(dbs.in_local);
        List<String> in_remote = Arrays.asList(dbs.in_remote);
        System.out.println(dbs.tableStructure);
       try {
           File file = new File("src/main/java/Datadictionary/" + database + ".txt");
           FileWriter fr = new FileWriter(file,false);
           fr.close();

           FileWriter fw = new FileWriter(file,true);
           String databasename = "Database = "+database+"\n\n";
           fw.write(databasename);

           for(String key : dbs.tableStructure.keySet())
           {
              fw.write("Table : "+key+" {\n");
              for (String key2 : dbs.tableStructure.get(key).keySet()){
                  fw.write("\t"+ key2 + " : " + dbs.tableStructure.get(key).get(key2)+"\n");

              }
              if(in_local.contains(key)) {
                  fw.write("} Location : Local site\n\n");
              }
              else if(in_remote.contains(key)){
                  fw.write("} Location : Remote site\n\n");
              }
           }
           fw.close();

       } catch (IOException e) {
           e.printStackTrace();
       }

    }

}
