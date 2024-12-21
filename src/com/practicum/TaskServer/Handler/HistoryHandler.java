package com.practicum.TaskServer.Handler;

import com.google.gson.Gson;
import com.practicum.TaskManager.service.TaskManager;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {
    TaskManager taskManager;

    public HistoryHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();

        switch (method) {
            case "GET":
                getHistory(exchange);
                break;
            default:
                sendNotFound(exchange);
        }
    }

    public void getHistory(HttpExchange exchange) throws IOException {
        Gson gson = buildGson();
        String gsonHistory = gson.toJson(taskManager.getHistory());
        sendText(exchange, gsonHistory);
        exchange.close();
    }
}
