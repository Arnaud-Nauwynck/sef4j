package org.sef4j.jdbc.util;

import java.sql.Connection;

public class ConnectionUtils {


    public static String transactionLevelToString(int level) {
        switch(level) {
        case Connection.TRANSACTION_NONE: return "NONE";
        case Connection.TRANSACTION_READ_UNCOMMITTED: return "READ_COMMITTED";
        case Connection.TRANSACTION_REPEATABLE_READ: return "REPEATABLE_READ";
        case Connection.TRANSACTION_SERIALIZABLE: return "SERIALIZABLE";
        default: return "UNKOWN";
        }
    }
        
    
}
