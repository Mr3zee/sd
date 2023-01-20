package ru.akirakozov.sd.refactoring.db;

import ru.akirakozov.sd.refactoring.db.query.DataQuery;
import ru.akirakozov.sd.refactoring.dto.ProductDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseDBProvider implements AutoCloseable {
    private final Connection connection;

    protected BaseDBProvider(final Connection connection) throws SQLException {
        this.connection = connection;

        initializeDB();
    }

    @Override
    public void close() throws Exception {
        connection.close();
    }

    private void initializeDB() throws SQLException {
        final String sql = "CREATE TABLE IF NOT EXISTS PRODUCT" +
                "(ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                " NAME           TEXT    NOT NULL, " +
                " PRICE          INT     NOT NULL)";

        try (final Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(sql);
        }
    }

    @FunctionalInterface
    private interface ThrowingProcessor<R> {
        R apply(final ResultSet result) throws SQLException;
    }

    private <R> R executeAndProcessResult(final String sql, final ThrowingProcessor<R> processor) throws SQLException {
        try (final PreparedStatement statement = connection.prepareStatement(sql)) {
            try (final ResultSet result = statement.executeQuery()) {
                return processor.apply(result);
            }
        }
    }

    public List<ProductDTO> getProducts() throws SQLException {
        return executeAndProcessResult("SELECT NAME, PRICE FROM PRODUCT", BaseDBProvider::collectProducts);
    }

    public static List<ProductDTO> collectProducts(final ResultSet result) throws SQLException {
        final List<ProductDTO> products = new ArrayList<>();
        while (result.next()) {
            final String name = result.getString("NAME");
            final int price = result.getInt("PRICE");
            final ProductDTO product = new ProductDTO(name, price);
            products.add(product);
        }

        return products;
    }

    public static ProductDTO collectSingleProduct(final ResultSet result) throws SQLException {
        final List<ProductDTO> products = BaseDBProvider.collectProducts(result);
        if (products.isEmpty()) {
            return null;
        }
        return products.get(0);
    }

    public void addProduct(final ProductDTO product) throws SQLException {
        try (final PreparedStatement stmt = connection.prepareStatement("INSERT INTO PRODUCT (NAME, PRICE) VALUES (?, ?)")) {
            stmt.setString(1, product.getName());
            stmt.setInt(2, product.getPrice());
            stmt.executeUpdate();
        }
    }

    public <T> T executeQuery(final DataQuery<T> query) throws SQLException {
        return executeAndProcessResult(query.getSql(), query::processResult);
    }
}

