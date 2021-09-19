package sql.parser;

import sql.sql.InternalQuery;

public interface IParser {
    InternalQuery parse(String query);
}
