package tfms.utils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class DataSource {

    private static HikariConfig config = new HikariConfig();
    private static HikariDataSource ds;

    static {
        config.setJdbcUrl( "jdbc:postgresql://localhost:5432/task_flow_management_system" );
        config.setUsername( "postgres" );
        config.setPassword( "admin" );
        config.addDataSourceProperty( "cachePrepStmts" , "true" );
        config.addDataSourceProperty( "prepStmtCacheSize" , "250" );
        config.addDataSourceProperty( "prepStmtCacheSqlLimit" , "2048" );
       // config.setAutoCommit(false);
        ds = new HikariDataSource( config );
    }

    private DataSource() {}
    public static Connection getConnection() throws SQLException, SQLException {
        return ds.getConnection();
    }
}
