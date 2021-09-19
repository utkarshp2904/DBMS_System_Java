package sql.processor;

import dataFiles.db.databaseStructures;
import sql.sql.InternalQuery;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

@WebService
@SOAPBinding(style = Style.RPC)

public interface IProcessor
{
    @WebMethod databaseStructures process(InternalQuery internalquery, String query, String username, String database,databaseStructures dbs);
}
