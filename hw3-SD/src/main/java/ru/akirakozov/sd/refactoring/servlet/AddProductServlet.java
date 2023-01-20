package ru.akirakozov.sd.refactoring.servlet;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.akirakozov.sd.refactoring.service.AddProductService;

import java.io.IOException;


/**
 * @author akirakozov
 */
public class AddProductServlet extends BaseHttpServlet {
    private final AddProductService service;

    public AddProductServlet(final AddProductService service) {
        this.service = service;
    }

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        final String name = request.getParameter("name");
        final String price = request.getParameter("price");

        if (name == null || price == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Expected name and price parameters");
            return;
        }

        service.addProduct(name, Integer.parseInt(price)).unpackUnchecked();

        prepareResponse(response, writer -> writer.println("OK"));
    }
}
