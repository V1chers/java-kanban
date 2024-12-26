package com.practicum.TaskServer.Handler;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.practicum.TaskManager.model.ConflictException;
import com.practicum.TaskManager.model.NotFoundException;
import com.practicum.TaskManager.model.Subtask;
import com.practicum.TaskManager.service.TaskManager;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.time.format.DateTimeParseException;
import java.util.Optional;

public class SubtaskHandler extends BaseHttpHandler implements HttpHandler {
    TaskManager taskManager;

    public SubtaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            Optional<Integer> subtaskId = getId(exchange);

            switch (method) {
                case "GET":
                    if (subtaskId.isPresent()) {
                        getSubtask(exchange, subtaskId.get());
                    } else {
                        getSubtasks(exchange);
                    }
                    break;
                case "POST":
                    postSubtask(exchange);
                    break;
                case "DELETE":
                    if (subtaskId.isPresent()) {
                        deleteSubtask(exchange, subtaskId.get());
                    } else {
                        sendBadRequest(exchange);
                    }
                    break;
                default:
                    sendBadRequest(exchange);
            }
        } catch (NotFoundException e) {
            sendNotFound(exchange);
        } catch (ConflictException e) {
            sendHasInteractions(exchange);
        } catch (DateTimeParseException | NumberFormatException e) {
            sendBadRequest(exchange);
        }
    }

    private void getSubtasks(HttpExchange exchange) throws java.io.IOException {
        Gson gson = buildGson();
        String gsonSubtasksList = gson.toJson(taskManager.getSubtasks());
        sendText(exchange, gsonSubtasksList);
        exchange.close();
    }

    private void getSubtask(HttpExchange exchange, int id) throws java.io.IOException {
        Gson gson = buildGson();
        String gsonEpic = gson.toJson(taskManager.getSubtaskById(id));
        sendText(exchange, gsonEpic);
        exchange.close();
    }

    private void postSubtask(HttpExchange exchange) throws java.io.IOException {
        try {
            String jsonSubtask = getStringFromBody(exchange);
            Gson gson = buildGson();
            Subtask subtask = gson.fromJson(jsonSubtask, Subtask.class);
            if (subtask.getName().isBlank() || subtask.getStatus() == null) {
                sendBadRequest(exchange);
                return;
            }
            if (subtask.getId() == 0) {
                taskManager.createSubtask(new Subtask(subtask));
            } else {
                taskManager.updateSubTask(new Subtask(subtask));
            }
            sendSuccess(exchange);
        } catch (JsonSyntaxException e) {
            sendBadRequest(exchange);
        }
    }

    private void deleteSubtask(HttpExchange exchange, int id) throws java.io.IOException {
        taskManager.removeSubtaskById(id);
        sendSuccess(exchange);
    }
}
