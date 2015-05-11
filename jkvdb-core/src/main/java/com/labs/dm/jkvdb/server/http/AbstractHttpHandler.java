package com.labs.dm.jkvdb.server.http;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;

import static com.labs.dm.jkvdb.server.http.AdminHandler.logger;

/**
 *
 * @author daniel
 */
public abstract class AbstractHttpHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange he) throws IOException {
        String requestMethod = he.getRequestMethod();
        if (requestMethod != null) {
            switch (requestMethod.toLowerCase()) {
                case "get":
                    onGet(he);
                    break;

                case "post":
                    onPost(he);
                    break;

                case "put":
                    onPut(he);
                    break;

                case "delete":
                    onDelete(he);
                    break;

                default:
                    throw new IllegalArgumentException("Unsupported request type");

            }
        }
    }

    void onGet(HttpExchange he) throws IOException {
        throw new UnsupportedOperationException();
    }

    void onPost(HttpExchange he) throws IOException {
        throw new UnsupportedOperationException();
    }

    void onPut(HttpExchange he) throws IOException {
        throw new UnsupportedOperationException();
    }

    void onDelete(HttpExchange he) {
        throw new UnsupportedOperationException();
    }

    void renderPage(HttpExchange exchange, String resource) {
        Headers responseHeaders = exchange.getResponseHeaders();
        responseHeaders.set("Content-Type", "text/html");

        try (OutputStream responseBody = exchange.getResponseBody()) {

            URL url = this.getClass().getResource(getPrefix() + resource + getSuffix());
            logger.info(url.toString());
            Path resPath = Paths.get(url.toURI());
            String html = new String(java.nio.file.Files.readAllBytes(resPath), "UTF8");
            logger.info(html);
            responseBody.write(html.getBytes());
        } catch (Exception e) {
            logger.log(Level.SEVERE, null, e);
        }
    }

    private String getPrefix() {
        return "/web/";
    }

    private String getSuffix() {
        return ".html";
    }

}
