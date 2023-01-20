package ru.akirakozov.sd.refactoring.service;

import ru.akirakozov.sd.refactoring.db.BaseDBProvider;
import ru.akirakozov.sd.refactoring.db.query.*;

import java.sql.SQLException;

public class QueryService {
    private final BaseDBProvider provider;

    public QueryService(final BaseDBProvider provider) {
        this.provider = provider;
    }

    // we return an Object because the idea of executing queries
    // with the different return types from one handle
    // is bad and there is no good solution how to overcome it
    public CallResult<Object, SQLException> executeQuery(final String command) {
        final DataQuery<?> query;
        switch (command) {
            case "max":
                query = new MaxDataQuery();
                break;
            case "min":
                query = new MinDataQuery();
                break;
            case "sum":
                query = new SumDataQuery();
                break;
            case "count":
                query = new CountDataQuery();
                break;
            default:
                throw new UnknownQueryException();
        }

        try {
            return new CallResult<>(provider.executeQuery(query));
        } catch (final SQLException e) {
            return new CallResult<>(e);
        }
    }

    public static class UnknownQueryException extends RuntimeException {
        @Override
        public String getMessage() {
            return "Illegal query name. Valid are: min, max, sum and count";
        }
    }
}
