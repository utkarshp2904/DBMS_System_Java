package erdgenerator;

import java.io.FileWriter;
import java.io.IOException;
import dataFiles.db.databaseStructures;

public class ERDGenerator {
	String ERD_PATH = "src/main/java/erdgenerator/";

	public void generateERD(String username, String databaseName, databaseStructures dbs)
	{
		String foreignkey_table = null;
		String content = "";
		dbs.populateDatabaseData(databaseName);

		for(String tableName: dbs.tableStructure.keySet())
		{
			int count = 0;
			content += "\n";
			content += "******************* Entity Name :" +tableName +" *******************";

			for(String attributesName: dbs.tableStructure.get(tableName).keySet())
			{
				content += "\n";
				content += "Attribute name : " +attributesName;
				content += "\n";
				content += "Datatype name : " +dbs.tableStructure.get(tableName).get(attributesName);
				content += "\n";
			}
			content += "\n";
			if((dbs.primaryKey_Hashtable.keySet()).contains(tableName))
			{
				content += "Primary Key : " +dbs.primaryKey_Hashtable.get(tableName);
				content += "\n";
			}
			for(String key: dbs.foreignKey_Hashtable.keySet()){
				String foreign_key = dbs.foreignKey_Hashtable.get(key);
				String tables[] = foreign_key.split(" ");
				if(tables[0].contains(tableName)) {
					foreignkey_table = tables[3];
					content += "Foreign key reference table:" + foreignkey_table;
					content += "\n";
					count++;
				}
			}
			if(foreignkey_table!=null) {
				String cardinality[] = foreignkey_table.split("\\(");
				if (count > 1 && cardinality[0] != null) {
					content += "Cardinality for table " + tableName + " with table  '" + cardinality[0] + "' 1:Many";
					content += "\n";
				}
				if (count == 1 && cardinality[0] != null) {
					content += "Cardinality for table " + tableName + " with table '" + cardinality[0] + "' 1:1";
					content += "\n";
				}
			}
		}
		content += "\n";
		content += "\n";

		writeERDFile(content,databaseName);
	}
	
	private void writeERDFile(String contentWritter, String databaseName) {

		String content = "";
		content += "-------------------- ERD DIAGRAM ----------------------";
		contentWritter = content + contentWritter;
		try (FileWriter file = new FileWriter(ERD_PATH+databaseName+"_ERD.txt")) {
            file.write(contentWritter);
            file.flush();
            System.out.println("ERD generated successfully!");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to Generate ERD!");
        }
    }
}