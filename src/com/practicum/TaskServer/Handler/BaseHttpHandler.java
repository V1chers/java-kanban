package com.practicum.TaskServer.Handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.practicum.TaskServer.AdaptersAndTokens.DurationAdapter;
import com.practicum.TaskServer.AdaptersAndTokens.LocalDateTimeAdapter;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

public abstract class BaseHttpHandler implements HttpHandler {
    public abstract void handle(HttpExchange exchange) throws IOException;

    protected void sendText(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(200, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }

    protected void sendSuccess(HttpExchange h) throws IOException {
        h.sendResponseHeaders(201, 0);
        h.close();
    }

    protected void sendNotFound(HttpExchange h) throws IOException {
        h.sendResponseHeaders(404, 0);
        h.close();
    }

    protected void sendHasInteractions(HttpExchange h) throws IOException {
        h.sendResponseHeaders(409, 0);
        h.close();
    }

    protected void sendBadRequest(HttpExchange h) throws IOException {
        h.sendResponseHeaders(400, 0);
        h.close();
    }

    protected Optional<Integer> getId(HttpExchange exchange) {
        Optional<Integer> id = Optional.empty();
        URI uri = exchange.getRequestURI();
        String[] splittedURI = uri.toString().split("/");
        if (splittedURI.length > 2) {
            id = Optional.of(Integer.parseInt(splittedURI[2]));
        }
        return id;
    }

    public static Gson buildGson() {
        return new GsonBuilder()
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }

    protected String getStringFromBody(HttpExchange exchange) throws java.io.IOException {
        try (InputStream is = exchange.getRequestBody()) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}