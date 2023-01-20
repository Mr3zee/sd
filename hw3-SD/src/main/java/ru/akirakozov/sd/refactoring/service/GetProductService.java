package ru.akirakozov.sd.refactoring.service;

import ru.akirakozov.sd.refactoring.db.BaseDBProvider;
import ru.akirakozov.sd.refactoring.dto.ProductDTO;

import java.sql.SQLException;
import java.util.List;

public class GetProductService {
    private final BaseDBProvider provider;

    public GetProductService(final BaseDBProvider provider) {
        this.provider = provider;
    }

    public CallResult<List<ProductDTO>, SQLException> getProducts() {
        try {
            return new CallResult<>(provider.getProducts());
        } catch (final SQLException e) {
            return new CallResult<>(e);
        }
    }
}
