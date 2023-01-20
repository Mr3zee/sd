package ru.akirakozov.sd.refactoring;

import ru.akirakozov.sd.refactoring.db.BaseDBProvider;
import ru.akirakozov.sd.refactoring.db.DBProviderImpl;

/**
 * @author akirakozov
 */
public class Main {
    private static final int PORT = 8081;

    public static void main(final String[] args) throws Exception {
        try (final BaseDBProvider provider = DBProviderImpl.getInstance()) {
            ProductServer.withServer(provider, PORT, server -> {
                server.start();
                server.join();
            });
        }
    }
}
