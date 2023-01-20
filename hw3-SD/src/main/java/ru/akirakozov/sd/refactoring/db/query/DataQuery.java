package ru.akirakozov.sd.refactoring.db.query;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface DataQuery<T> {
    String getSql();

    T processResult(final ResultSet result) throws SQLException;
}
