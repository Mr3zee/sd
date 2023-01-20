package ru.akirakozov.sd.refactoring.db.query;

import ru.akirakozov.sd.refactoring.db.BaseDBProvider;
import ru.akirakozov.sd.refactoring.dto.ProductDTO;

import java.sql.ResultSet;
import java.sql.SQLException;


public class MinDataQuery implements DataQuery<ProductDTO> {
    @Override
    public String getSql() {
        return "SELECT NAME, PRICE FROM PRODUCT ORDER BY PRICE LIMIT 1";
    }

    @Override
    public ProductDTO processResult(final ResultSet result) throws SQLException {
        return BaseDBProvider.collectSingleProduct(result);
    }
}
