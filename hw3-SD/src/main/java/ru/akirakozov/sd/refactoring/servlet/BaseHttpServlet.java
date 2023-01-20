package ru.akirakozov.sd.refactoring.servlet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletResponse;
import ru.akirakozov.sd.refactoring.dto.ProductDTO;

import java.io.IOException;
import java.io.PrintWriter;


public abstract class BaseHttpServlet extends HttpServlet {
    protected void prepareResponse(final HttpServletResponse response, final ResponseWriter responseWriter) throws IOException {
        final PrintWriter writer = response.getWriter();

        writer.println("<html><body>");
        responseWriter.apply(writer);
        writer.println("</body></html>");

        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
    }

    @FunctionalInterface
    protected interface ResponseWriter {
        void apply(final PrintWriter responseWriter);
    }

    protected String productToHtml(final ProductDTO product) {
        return product.getName() + "\t" + product.getPrice() + "</br>";
    }
}
