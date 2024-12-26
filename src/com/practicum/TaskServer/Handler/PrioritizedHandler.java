package com.practicum.TaskServer.Handler;

import com.google.gson.Gson;
import com.practicum.TaskManager.service.TaskManager;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {
    TaskManager taskManager;

    public PrioritizedHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();

        switch (method) {
            case "GET":
                getPrioritizedTasks(exchange);
                break;
            default:
                sendBadRequest(exchange);
        }
    }

    public void getPrioritizedTasks(HttpExchange exchange) throws IOException {
        Gson gson = buildGson();
        String gsonPrioritizedList = gson.toJson(taskManager.getPrioritizedTasks());
        sendText(exchange, gsonPrioritizedList);
        exchange.close();
    }
}
