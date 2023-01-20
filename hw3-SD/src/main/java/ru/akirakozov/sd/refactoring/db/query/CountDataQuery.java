
package ru.akirakozov.sd.refactoring.db.query;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CountDataQuery implements DataQuery<Long> {
    @Override
    public String getSql() {
        return "SELECT COUNT(NAME) FROM PRODUCT";
    }

    @Override
    public Long processResult(final ResultSet result) throws SQLException {
        if (result.next()) {
            return result.getLong(1);
        }
        return 0L;
    }
}
