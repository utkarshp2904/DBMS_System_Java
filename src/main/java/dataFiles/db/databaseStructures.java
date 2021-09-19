package dataFiles.db;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class databaseStructures
{
    public HashMap<String,HashMap<String,HashMap<String,String>>> databasedata = new HashMap<>();
    public HashMap<String,HashMap<String ,String>> tableStructure = new HashMap<>();
    public HashMap <String,String> primaryKey_Hashtable = new HashMap<>();
    public HashMap <String,String> foreignKey_Hashtable = new HashMap<>();
    public String[] in_remote;
    public String[] in_local;
    public List<String> inlocal = new ArrayList<>();
    public List<String> inremote = new ArrayList<>();
    public String selectedDb = null;

    public ArrayList<String> database_list = new ArrayList<String>();
    public databaseStructures populateDatabaseData(String DatabaseName){

        try {

        File structurefile = new File("src/main/java/dataFiles/db/"+DatabaseName+"Structure.txt");
        FileReader fr = new FileReader(structurefile);
        BufferedReader br = new BufferedReader(fr);

        String PROJECT_ID = "csci-5408-w21-305009";
        String BUCKET_NAME = "csci5408_group-project";
        String OBJECT_NAME = DatabaseName+"Structure.txt";

        StorageOptions options = null;
        options = StorageOptions.newBuilder()
                .setProjectId(PROJECT_ID)
                .build();

        Storage storage = options.getService();
        Blob blob = storage.get(BUCKET_NAME, OBJECT_NAME);
        String fileContent = new String(blob.getContent());
        String[] content = fileContent.split("\n");

        String line ;
        int count_local = 0;
        while((line=br.readLine())!=null) {
            String[] splitted_part = line.split("=");
            if (splitted_part[0].trim().equals("tablename"))
            {

                String tablenametemp = splitted_part[1].trim();
                count_local++;
                HashMap<String, String> tempcolumnHashmap = new HashMap<>();
                String[] tablemetadata = splitted_part[2].replaceAll("[{}]", " ").trim().split(",");
                for (int j = 0; j < tablemetadata.length; j++)
                {
                    String[] columnattributes = tablemetadata[j].trim().split(":");
                    tempcolumnHashmap.put(columnattributes[0].trim(), columnattributes[1].trim());
                }
                this.tableStructure.put(tablenametemp, tempcolumnHashmap);

            }
        }
        int count_remote = 0;
        for(String line12 : content){
            String[] splitted_part = line12.split("=");
            if(splitted_part[0].trim().equals("tablename"))
            {
                String tablenametemp = splitted_part[1].trim();
                count_remote++;
                HashMap<String,String> tempcolumnHashmap = new HashMap<>();;
                String[] tablemetadata = splitted_part[2].replaceAll("[{}]"," ").trim().split(",");
                for (int j= 0; j<tablemetadata.length; j++)
                {
                    String[] columnattributes = tablemetadata[j].trim().split(":");
                    tempcolumnHashmap.put(columnattributes[0].trim(),columnattributes[1].trim());
                }
                this.tableStructure.put(tablenametemp,tempcolumnHashmap);
            }
        }

        File tempfile = new File("src/main/java/dataFiles/db/"+DatabaseName+"Structure.txt");
        FileReader fileReader = new FileReader(tempfile);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        in_remote = new String[count_remote];
        in_local = new String[count_local];
        int x = 0;
        int y = 0;

        while((line=bufferedReader.readLine())!=null)
        {
            String[] splitted_part = line.split("=");
            if (splitted_part[0].trim().equals("tablename"))
            {
                String tablenametemp = splitted_part[1].trim();
                in_local[x] = tablenametemp;
                inlocal.add(tablenametemp);
                x++;
            }
        }
        for(String line12 : content)
        {
            String[] splitted_part = line12.split("=");
            if(splitted_part[0].trim().equals("tablename")){
                String tablenametemp = splitted_part[1].trim();
                in_remote[y] = tablenametemp;
                inremote.add(tablenametemp);
                y++;
            }
        }

        File structurefile2 = new File("src/main/java/dataFiles/db/"+DatabaseName+"Data.txt");
        FileReader fr2 = new FileReader(structurefile2);
        BufferedReader br2 = new BufferedReader(fr2);
        String line2 ;

        String PROJECT_ID_1 = "csci-5408-w21-305009";
        String BUCKET_NAME_1 = "csci5408_group-project";
        String OBJECT_NAME_1 = DatabaseName+"Data.txt";

        StorageOptions options_1 = null;
        options_1 = StorageOptions.newBuilder()
                .setProjectId(PROJECT_ID_1)
                .build();

        Storage storage_1 = options_1.getService();
        Blob blob_1 = storage_1.get(BUCKET_NAME_1, OBJECT_NAME_1);
        String fileContent_1 = new String(blob_1.getContent());
        String[] content_1 = fileContent_1.split("\n");
        HashMap<String,String> rowdata;
        HashMap<String,HashMap<String,String>> allrows;

        while((line2=br2.readLine())!=null){
            allrows = new HashMap<>();
            String[] spliting_line = line2.split("=",2);
            String tempTablename = spliting_line[0].trim();
            String[] spliting_data = spliting_line[1].split("} ,");
            int rownum = 1;
            for(int i=0;i<spliting_data.length;i++){

                rowdata = new HashMap<String,String>();
                String[] splitting_rows = spliting_data[i].replaceAll("[{}]","").trim().split(",");

                String rowdetails = "row"+rownum;
                for(int j=0;j<splitting_rows.length;j++)
                {
                    String[] splitting_columns = splitting_rows[j].trim().split("=");
                    String column_name = splitting_columns[0].trim();
                    String column_value = splitting_columns[1].replaceAll("\"","").trim();
                    rowdata.put(column_name,column_value);
                }
                allrows.put(rowdetails,rowdata);
                rownum=rownum+1;
            }
            this.databasedata.put(tempTablename,allrows);
        }

        for(String line22 : content_1){
            allrows = new HashMap<>();
            String[] spliting_line = line22.split("=",2);
            String tempTablename = spliting_line[0].trim();
            String[] spliting_data = spliting_line[1].split("} ,");
            int rownum = 1;
            for(int i=0;i<spliting_data.length;i++){

                rowdata = new HashMap<String,String>();
                String[] splitting_rows = spliting_data[i].replaceAll("[{}]","").trim().split(",");

                String rowdetails = "row"+rownum;
                for(int j=0;j<splitting_rows.length;j++)
                {
                    String[] splitting_columns = splitting_rows[j].trim().split("=");
                    String column_name = splitting_columns[0].trim();
                    String column_value = splitting_columns[1].replaceAll("\"","").trim();
                    rowdata.put(column_name,column_value);
                }
                allrows.put(rowdetails,rowdata);
                rownum=rownum+1;
            }
            this.databasedata.put(tempTablename,allrows);
        }
        File structurefile3 = new File("src/main/java/dataFiles/db/databases.txt");
        FileReader fr3 = new FileReader(structurefile3);
        BufferedReader br3 = new BufferedReader(fr3);
        String line3 ;

        while((line3=br3.readLine())!=null){
            database_list.add(line3);
        }

        File keyFile = new File("src/main/java/dataFiles/db/"+DatabaseName+"Keys.txt");
        FileReader fileReaderkey = new FileReader(keyFile);
        bufferedReader = new BufferedReader(fileReaderkey);

        while((line=bufferedReader.readLine())!=null) {
            String[] splitted_part = line.split("=");
            for (String s: splitted_part){
            }
            if(splitted_part[0].contains("Primary Key"))
            {
                this.primaryKey_Hashtable.put(splitted_part[1].trim(), splitted_part[2].trim());
            }
            else if (splitted_part[0].contains("Foreign key"))
            {
                this.foreignKey_Hashtable.put(splitted_part[0].trim(), splitted_part[1].trim());
            }
            else {

            }
        }
        }
        catch (Exception e){
        e.printStackTrace();
    }
        return this;
    }

    public void storePermanatly(String DatabaseName)
    {
        try {

            File structurefile = new File("src/main/java/dataFiles/db/"+DatabaseName+"Structure.txt");
            File structurefile2 = new File("src/main/java/dataFiles/db/"+DatabaseName+"Data.txt");
            File Keyfile = new File("src/main/java/dataFiles/db/"+DatabaseName+"keys.txt");

            String PROJECT_ID = "csci-5408-w21-305009";
            String BUCKET_NAME = "csci5408_group-project";
            String OBJECT_NAME = DatabaseName+"Structure.txt";
            String OBJECT_NAME2 = DatabaseName+"Data.txt";

            StorageOptions options = null;
            options = StorageOptions.newBuilder()
                    .setProjectId(PROJECT_ID)
                    .build();

            Storage storage = options.getService();
            Blob blob = storage.get(BUCKET_NAME, OBJECT_NAME);
            Blob blob2 = storage.get(BUCKET_NAME, OBJECT_NAME2);

            FileWriter fw = new FileWriter(structurefile, false);
            fw.close();
            FileWriter fr = new FileWriter(structurefile,true);

            String dbString = "databasename = "+DatabaseName+"\n\n";
            fr.write(dbString);

            String to_write = "";
            String final_output = "";
            for(String key :  tableStructure.keySet()){

                if(inlocal.contains(key)){
                String tableStruct = "tablename = ";
                String tablename = "";

                tablename = key+" = { ";
                String allColumns = "" ;
                for(String key2 : tableStructure.get(key).keySet())
                {
                    String columnName = key2+" : "+tableStructure.get(key).get(key2)+", ";
                    allColumns = allColumns + columnName;
                }
                StringBuffer sb = new StringBuffer(allColumns);
                sb.deleteCharAt(sb.length()-2);
                String enddelimiter = "}\n";
                tablename = tablename + sb + enddelimiter ;
                tableStruct = tableStruct + tablename;
                fr.write(tableStruct);
                }
                else if(inremote.contains(key)){
                    String tableStruct = "tablename = ";
                    String tablename = "";

                    tablename = key+" = { ";
                    String allColumns = "" ;
                    for(String key2 : tableStructure.get(key).keySet()){
                        String columnName = key2+" : "+tableStructure.get(key).get(key2)+", ";
                        allColumns = allColumns + columnName;
                    }
                    StringBuffer sb = new StringBuffer(allColumns);
                    sb.deleteCharAt(sb.length()-2);
                    String enddelimiter = "}\n";
                    tablename = tablename + sb + enddelimiter ;
                    tableStruct = tableStruct + tablename;
                    final_output = final_output+tableStruct;
                    to_write = dbString+final_output;
                }
            }

           WritableByteChannel channel = blob.writer();
           channel.write(ByteBuffer.wrap(to_write.getBytes(StandardCharsets.UTF_8)));
           channel.close();

            fr.close();


            FileWriter fileWriter = new FileWriter(structurefile2,false);
            fileWriter.close();
            FileWriter fileWriter1 = new FileWriter(structurefile2,true);
            String data_to_write = "";
            for(String key : databasedata.keySet()) {
                if (inlocal.contains(key)) {
                    String table = "";
                    table = key + " = { ";
                    String table_data = "";
                    String all_data = "";
                    for (String key2 : databasedata.get(key).keySet()) {
                        String column = "";
                        String data = "";
                        String final_data = "";

                        for (String key3 : databasedata.get(key).get(key2).keySet()) {
                            column = key3 + " = \"";
                            data = column + databasedata.get(key).get(key2).get(key3) + "\" , ";
                            final_data = final_data + data;

                        }
                        StringBuffer sb = new StringBuffer(final_data);
                        sb.deleteCharAt(sb.length() - 2);
                        String sb1 = sb + "";
                        sb1 = sb1.trim();
                        all_data = all_data + sb1 + " } , { ";
                    }
                    table_data = table + all_data;
                    table_data = table_data.substring(0, table_data.length() - 5);
                    fileWriter1.write(table_data);
                    fileWriter1.write("\n");
                }
                else if(inremote.contains(key)){
                    String table = "";
                    table = key + " = { ";
                    String table_data = "";
                    String all_data = "";
                    for (String key2 : databasedata.get(key).keySet()) {
                        String column = "";
                        String data = "";
                        String final_data = "";

                        for (String key3 : databasedata.get(key).get(key2).keySet()) {
                            column = key3 + " = \"";
                            data = column + databasedata.get(key).get(key2).get(key3) + "\" , ";
                            final_data = final_data + data;

                        }
                        StringBuffer sb = new StringBuffer(final_data);
                        sb.deleteCharAt(sb.length() - 2);
                        String sb1 = sb + "";
                        sb1 = sb1.trim();
                        all_data = all_data + sb1 + " } , { ";
                    }
                    table_data = table + all_data;
                    table_data = table_data.substring(0, table_data.length() - 5);
                    data_to_write = data_to_write+table_data+"\n";
                }
            }
            WritableByteChannel channel2 = blob2.writer();
            channel2.write(ByteBuffer.wrap(data_to_write.getBytes(StandardCharsets.UTF_8)));
            channel2.close();
            fileWriter1.close();


            FileWriter fwOb = new FileWriter(Keyfile, false);
            PrintWriter pwOb = new PrintWriter(fwOb, false);
            pwOb.flush();
            pwOb.close();
            fwOb.close();

            FileWriter fk = new FileWriter(Keyfile,true);
            fk.flush();

            String contentFile = "";
            for(String key: primaryKey_Hashtable.keySet())
            {
                contentFile += "Primary Key For Table = " + key + " = ";
                contentFile += primaryKey_Hashtable.get(key);
                contentFile += "\n";
            }
            for(String key: foreignKey_Hashtable.keySet())
            {
                contentFile += key + " = " ;
                contentFile += foreignKey_Hashtable.get(key);
                contentFile += "\n";
            }
            fk.write(contentFile);
            fk.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}