//import SQLDump.SQLDump;
//import datadictionarygenerator.DataDictionaryGenerator;
//import erdgenerator.ERDGenerator;

import DataDictionary.DataDictionary;
import SQLDump.SQLDump;
import dataFiles.db.databaseStructures;
import erdgenerator.ERDGenerator;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {
    private static Scanner sc;

    public static void main(String[] args) {

        databaseStructures dbs = new databaseStructures();

        System.out.println("-----------------------------------------");
        System.out.println ("*** WELCOME TO DATABASE MANAGEMENT SYSTEM ***");
        System.out.println ("----------------------------------------");

        DatabaseSystem databaseSystem = new DatabaseSystem (dbs);
        sc = new Scanner (System.in);
        String username = databaseSystem.authenticate ();
        if (username != null) {
            System.out.println("Choose from one of the operations");
            System.out.println("1. Enter Query");
            System.out.println("2. SQL Dump");
            System.out.println("3. Generate ERD");
            System.out.println("4. Generate data dictionary");
            System.out.println("5. Exit");
            String userInput = sc.nextLine();
            switch (userInput) {
                case "1":
                    dbs = databaseSystem.init(dbs);
                    break;
                case "2":
                    SQLDump dump = new SQLDump();
                    System.out.println("Enter database name");
                    String databaseName = sc.nextLine();
                    dbs.populateDatabaseData(databaseName);
                    dump.generateSQLDump(username, databaseName, dbs);
                    break;
                case "3":
                    ERDGenerator erdObj = new ERDGenerator();
                    System.out.println("Enter a database name");
                    databaseName = sc.nextLine();
                    dbs.populateDatabaseData(databaseName);
                    erdObj.generateERD(username, databaseName, dbs);
                    break;
                case "4":
                    DataDictionary dataDictionary = new DataDictionary();
                    System.out.println("Enter a database name");
                    String database2 = sc.nextLine();
                    dataDictionary.generateDataDictionary(database2);
                    break;
                default:
                    System.out.println("Invalid input!");
            }
        }
    }
}