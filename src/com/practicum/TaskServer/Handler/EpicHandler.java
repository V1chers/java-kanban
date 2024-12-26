package com.practicum.TaskServer.Handler;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.practicum.TaskManager.model.Epic;
import com.practicum.TaskManager.model.ConflictException;
import com.practicum.TaskManager.model.NotFoundException;
import com.practicum.TaskManager.service.TaskManager;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.time.format.DateTimeParseException;
import java.util.Optional;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {
    TaskManager taskManager;

    public EpicHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            Optional<Integer> epicId = getId(exchange);
            boolean isSubtasks = false;

            String[] splittedURI = exchange.getRequestURI().toString().split("/");
            if (splittedURI.length > 3 && splittedURI[3].equals("subtasks")) {
                isSubtasks = true;
            }

            switch (method) {
                case "GET":
                    if (epicId.isPresent() && isSubtasks) {
                        getEpicSubtasks(exchange, epicId.get());
                    } else if (epicId.isPresent()) {
                        getEpic(exchange, epicId.get());
                    } else {
                        getEpics(exchange);
                    }
                    break;
                case "POST":
                    postEpic(exchange);
                    break;
                case "DELETE":
                    if (epicId.isPresent()) {
                        deleteEpic(exchange, epicId.get());
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

    private void getEpicSubtasks(HttpExchange exchange, int id) throws java.io.IOException {
        Gson gson = buildGson();
        String gsonSubtasks = gson.toJson(taskManager.getSubtasksOfEpic(id));
        sendText(exchange, gsonSubtasks);
        exchange.close();
    }

    private void getEpics(HttpExchange exchange) throws java.io.IOException {
        Gson gson = buildGson();
        String gsonEpicsList = gson.toJson(taskManager.getEpics());
        sendText(exchange, gsonEpicsList);
        exchange.close();
    }

    private void getEpic(HttpExchange exchange, int id) throws java.io.IOException {
        Gson gson = buildGson();
        String gsonEpic = gson.toJson(taskManager.getEpicById(id));
        sendText(exchange, gsonEpic);
        exchange.close();
    }

    private void postEpic(HttpExchange exchange) throws java.io.IOException {
        try {
            String jsonEpic = getStringFromBody(exchange);
            Gson gson = buildGson();
            Epic epic = gson.fromJson(jsonEpic, Epic.class);
            if (epic.getName().isBlank() || epic.getStatus() == null) {
                sendBadRequest(exchange);
                return;
            }
            if (epic.getId() == 0) {
                taskManager.createEpic(new Epic(epic));
            } else {
                taskManager.updateEpic(new Epic(epic));
            }
            sendSuccess(exchange);
        } catch (JsonSyntaxException e) {
            sendBadRequest(exchange);
        }
    }

    private void deleteEpic(HttpExchange exchange, int id) throws java.io.IOException {
        taskManager.removeEpicById(id);
        sendSuccess(exchange);
    }
}
