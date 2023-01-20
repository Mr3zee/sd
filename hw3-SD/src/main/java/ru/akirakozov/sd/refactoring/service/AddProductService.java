package ru.akirakozov.sd.refactoring.service;

import ru.akirakozov.sd.refactoring.db.BaseDBProvider;
import ru.akirakozov.sd.refactoring.dto.ProductDTO;

import java.sql.SQLException;

public class AddProductService {
    private final BaseDBProvider provider;

    public AddProductService(final BaseDBProvider provider) {
        this.provider = provider;
    }

    public CallResult<Object, SQLException> addProduct(final String name, final int price) {
        final ProductDTO product = new ProductDTO(name, price);

        try {
            provider.addProduct(product);
        } catch (final SQLException e) {
            return new CallResult<>(e);
        }

        return new CallResult<>(new Object());
    }
}
