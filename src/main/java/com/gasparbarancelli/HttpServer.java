package com.gasparbarancelli;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class HttpServer {

    private static final Logger LOGGER = Logger.getLogger(HttpServer.class.getName());

    public static void main(String[] args) throws IOException {
        LOGGER.info("Iniciando aplicacao");
        new HttpServer().startServer();
    }

    public void startServer() throws IOException {
        var socketAddress = getSocketAddress();
        if (socketAddress.isEmpty()) {
            LOGGER.info("Defina a variavel de ambiente HTTP_PORT para iniciar o servidor web");
            return;
        }

        var httpServer = com.sun.net.httpserver.HttpServer.create(socketAddress.get(), 0);
        httpServer.createContext("/", new HttpHandler());
        httpServer.setExecutor(Executors.newVirtualThreadPerTaskExecutor());
        httpServer.start();
    }

    private Optional<InetSocketAddress> getSocketAddress() {
        var port = System.getenv("HTTP_PORT");
        if (Objects.isNull(port)) {
            return Optional.empty();
        }

        LOGGER.info("Servidor http respondendo na porta " + port);

        var socketAddress = new InetSocketAddress(Integer.parseInt(port));
        return Optional.of(socketAddress);
    }

}