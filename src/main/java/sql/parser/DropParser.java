package sql.parser;
import sql.sql.InternalQuery;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DropParser implements IParser {
    static DropParser instance = null;

    public static DropParser instance() {
        if (instance == null) {
            instance = new DropParser();
        }
        return instance;
    }

    @Override
    public InternalQuery parse(String query) {
        Pattern pattern = Pattern.compile("drop\\s(.*?)table\\s(.*?)?;");
        Matcher matcher = pattern.matcher(query);
        boolean match = matcher.matches();

        if (match == true) {
            String tableName = matcher.group(2);

            InternalQuery internalQuery = new InternalQuery();
            internalQuery.set("table", tableName);
            return internalQuery;
        }
        System.out.println("Invalid query");
        return null;
    }
}
