//import login.Login;
import dataFiles.db.databaseStructures;
import sql.sql.QueryEngine;

import java.util.Scanner;

public class DatabaseSystem {
    private String user = null;
    private Scanner sc;
    private QueryEngine queryEngine;
    public databaseStructures dbs;

    public DatabaseSystem(databaseStructures dbsinject)
    {
        sc = new Scanner(System.in);

        this.dbs = dbsinject;

        queryEngine = new QueryEngine(dbs);
    }

    public String authenticate()
    {
        if(user == null){
            login login = new login();
            System.out.print("Please enter UserName:");
            String userName = sc.nextLine();
            System.out.print("Please enter Password:");
            String password = sc.nextLine();
            user = login.verification(userName, password);
        }
        return user;
    }

    public databaseStructures init(databaseStructures dbsinject)
    {
        this.dbs = dbsinject;
        while(true){
            System.out.println("Enter a SQL query to proceed or exit; to end!");
            String sqlQuery = sc.nextLine();
            if(sqlQuery.equals("exit;")){
                dbs.storePermanatly(dbs.selectedDb);
                break;
            }
            this.dbs = queryEngine.run(sqlQuery, user, this.dbs);
        }
        sc.close();
        return this.dbs;
    }
}
