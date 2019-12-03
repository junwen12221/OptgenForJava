package cn.lightfish.optgen;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Test {
    public static void main(String[] args) throws SQLException {
        Connection connection = null;
        Properties properties = new Properties();
        properties.put("user", "root");
        properties.put("password","123456");
        properties.put("useBatchMultiSend", "false");
        properties.put("usePipelineAuth", "false");
        connection = DriverManager
                .getConnection("jdbc:mysql://localhost:8066/TESTDB?useServerPrepStmts=false&useCursorFetch=true&serverTimezone=UTC&allowMultiQueries=false&useBatchMultiSend=false&characterEncoding=utf8", properties);


        System.out.println();
    }
}