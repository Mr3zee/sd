package ru.akirakozov.sd.refactoring.servlet;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.akirakozov.sd.refactoring.dto.ProductDTO;
import ru.akirakozov.sd.refactoring.service.QueryService;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author akirakozov
 */
public class QueryServlet extends BaseHttpServlet {
    private final QueryService service;

    public QueryServlet(final QueryService service) {
        this.service = service;
    }

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        final String command = request.getParameter("command");
        if (command == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Expected command parameter");
            return;
        }

        final Object result;
        try {
            result = service.executeQuery(command).unpackUnchecked();
        } catch (final QueryService.UnknownQueryException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            return;
        }

        prepareResponse(response, writer -> writeQueryResult(writer, command, result));
    }

    private void writeQueryResult(final PrintWriter writer, final String command, final Object result) {
        switch (command) {
            case "max":
                writer.println("<h1>Product with max price: </h1>");

                final ProductDTO maxProduct = (ProductDTO) result;
                if (maxProduct != null) {
                    writer.println(productToHtml(maxProduct));
                }
                break;
            case "min":
                writer.println("<h1>Product with min price: </h1>");

                final ProductDTO minProduct = (ProductDTO) result;
                if (minProduct != null) {
                    writer.println(productToHtml(minProduct));
                }
                break;
            case "sum":
                writer.println("Summary price: ");

                writer.println(result);
                break;
            case "count":
                writer.println("Number of products: ");

                writer.println(result);
                break;
            default:
                writer.println("Unknown command: " + command);
        }
    }
}
