/*package com.gasparbarancelli.rinhabackend;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class HttpConnectionPool {

    private final String baseUrl;
    private final BlockingQueue<HttpURLConnection> connectionPool;

    public HttpConnectionPool(String baseUrl, int poolSize) {
        this.baseUrl = baseUrl;
        this.connectionPool = new LinkedBlockingQueue<>(poolSize);
        for (int i = 0; i < poolSize; i++) {
            this.connectionPool.offer(createConnection());
        }
    }

    private HttpURLConnection createConnection() {
        try {
            return (HttpURLConnection) new URL(baseUrl).openConnection();
        } catch (Exception e) {
            throw new RuntimeException("Error creating HTTP connection", e);
        }
    }

    public HttpURLConnection getConnection() {
        try {
            return connectionPool.take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while waiting for connection", e);
        }
    }

    public void releaseConnection(HttpURLConnection connection) {
        connectionPool.offer(connection);
    }

    public String sendRequest(String method) throws Exception {
        return sendRequest(method, null);
    }

    public String sendRequest(String method, String requestData) throws Exception {
        HttpURLConnection connection = getConnection();
        try {
            connection.setRequestMethod(method);
            connection.setRequestProperty("Content-Type", "application/json");

            if (requestData != null) {
                connection.setRequestProperty("Content-Length", String.valueOf(requestData.length()));
                connection.setDoOutput(true);

                try (OutputStream outputStream = connection.getOutputStream()) {
                    outputStream.write(requestData.getBytes());
                }
            }

            try (InputStream inputStream = connection.getInputStream()) {
                byte[] responseBytes = inputStream.readAllBytes();
                return new String(responseBytes);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        } finally {
            releaseConnection(connection);
        }
    }

}*/