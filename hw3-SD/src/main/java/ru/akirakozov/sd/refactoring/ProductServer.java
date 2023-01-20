package ru.akirakozov.sd.refactoring;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import ru.akirakozov.sd.refactoring.db.BaseDBProvider;
import ru.akirakozov.sd.refactoring.service.AddProductService;
import ru.akirakozov.sd.refactoring.service.GetProductService;
import ru.akirakozov.sd.refactoring.service.QueryService;
import ru.akirakozov.sd.refactoring.servlet.AddProductServlet;
import ru.akirakozov.sd.refactoring.servlet.GetProductServlet;
import ru.akirakozov.sd.refactoring.servlet.QueryServlet;


public class ProductServer {
    public static void withServer(final BaseDBProvider provider, final int port, final ThrowingServerConsumer execute) throws Exception {
        final Server server = new Server(port);

        final ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        final AddProductServlet addProductServlet = new AddProductServlet(new AddProductService(provider));
        final GetProductServlet getProductServlet = new GetProductServlet(new GetProductService(provider));
        final QueryServlet queryServlet = new QueryServlet(new QueryService(provider));

        context.addServlet(new ServletHolder(addProductServlet), "/add-product");
        context.addServlet(new ServletHolder(getProductServlet), "/get-products");
        context.addServlet(new ServletHolder(queryServlet), "/query");

        execute.accept(server);
    }

    @FunctionalInterface
    public interface ThrowingServerConsumer {
        void accept(final Server server) throws Exception;
    }
}
