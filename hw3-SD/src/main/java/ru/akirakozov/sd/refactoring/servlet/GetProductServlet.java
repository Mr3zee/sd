package ru.akirakozov.sd.refactoring.servlet;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.akirakozov.sd.refactoring.dto.ProductDTO;
import ru.akirakozov.sd.refactoring.service.GetProductService;

import java.io.IOException;
import java.util.List;

/**
 * @author akirakozov
 */
public class GetProductServlet extends BaseHttpServlet {
    private final GetProductService service;

    public GetProductServlet(final GetProductService service) {
        this.service = service;
    }

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        final List<ProductDTO> products = service.getProducts().unpackUnchecked();

        prepareResponse(response, writer -> {
            for (final ProductDTO product : products) {
                writer.println(productToHtml(product));
            }
        });
    }
}
