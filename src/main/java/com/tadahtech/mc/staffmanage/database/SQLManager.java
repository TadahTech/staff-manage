package com.tadahtech.mc.staffmanage.database;

import com.tadahtech.mc.staffmanage.StaffManage;
import com.tadahtech.mc.staffmanage.util.UtilConcurrency;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * An abstract SQLManager class used to avoid having to constantly create the same methods and constructors.
 */
public abstract class SQLManager {

    private final HikariDataSource dataSource;

    public SQLManager() {
        this(true);
    }

    public SQLManager(boolean createTable) {
        SQLConfig sqlConfig = StaffManage.getInstance().getSqlConfig();
        String host = sqlConfig.getHost();
        int port = sqlConfig.getPort();
        String database = sqlConfig.getDatabase();
        String user = sqlConfig.getUser();
        String password = sqlConfig.getPassword();

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database + "?&serverTimezone=UTC");
        hikariConfig.setDriverClassName("com.mysql.jdbc.Driver");
        hikariConfig.setUsername(user);
        hikariConfig.setPassword(password);
        hikariConfig.setMinimumIdle(1);
        hikariConfig.setMaximumPoolSize(10000);
        hikariConfig.setConnectionTimeout(10000);

        this.dataSource = new HikariDataSource(hikariConfig);

        if (!createTable) {
            return;
        }

        runAsync(this::runCreateTableCommand);
    }

    public abstract String createTable();

    public void runCreateTableCommand() {
        String table = createTable();
        if (table == null) {
            return;
        }

        try (Connection connection = getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(table);
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void getResultSet(SQLStatement query, SQLCallback<ResultSet> callback) {
        try (Connection connection = getConnection()) {
            PreparedStatement preparedStatement = query.prepare(connection);
            preparedStatement.execute();
            ResultSet resultSet = preparedStatement.getResultSet();
            callback.call(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ResultSet getResultSet(SQLStatement query, Connection connection) {
        try {
            PreparedStatement preparedStatement = query.prepare(connection);
            preparedStatement.execute();
            return preparedStatement.getResultSet();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void runAsync(Runnable runnable) {
        UtilConcurrency.runAsync(runnable);
    }

    public void runSync(Runnable runnable) {
        UtilConcurrency.runSync(runnable);
    }
}
