package ru.akirakozov.sd.refactoring.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBProviderImpl extends BaseDBProvider {
    private static final String DB_URL = "jdbc:sqlite:test.db";

    private static BaseDBProvider instance;

    public static synchronized BaseDBProvider getInstance() throws SQLException {
        if (instance == null) {
            instance = createInstance();
        }
        return instance;
    }

    private static BaseDBProvider createInstance() throws SQLException {
        final Connection connection = DriverManager.getConnection(DB_URL);
        return new DBProviderImpl(connection);
    }

    protected DBProviderImpl(Connection connection) throws SQLException {
        super(connection);
    }
}
