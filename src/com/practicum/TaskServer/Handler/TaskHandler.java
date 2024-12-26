package com.practicum.TaskServer.Handler;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.practicum.TaskManager.model.ConflictException;
import com.practicum.TaskManager.model.NotFoundException;
import com.practicum.TaskManager.model.Task;
import com.practicum.TaskManager.service.TaskManager;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.time.format.DateTimeParseException;
import java.util.Optional;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {
    TaskManager taskManager;

    public TaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            Optional<Integer> taskId = getId(exchange);

            switch (method) {
                case "GET":
                    if (taskId.isPresent()) {
                        getTask(exchange, taskId.get());
                    } else {
                        getTasks(exchange);
                    }
                    break;
                case "POST":
                    postTask(exchange);
                    break;
                case "DELETE":
                    if (taskId.isPresent()) {
                        deleteTask(exchange, taskId.get());
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

    private void getTasks(HttpExchange exchange) throws java.io.IOException {
        Gson gson = buildGson();
        String gsonTasksList = gson.toJson(taskManager.getTasks());
        sendText(exchange, gsonTasksList);
        exchange.close();
    }

    private void getTask(HttpExchange exchange, int id) throws java.io.IOException {
        Gson gson = buildGson();
        String gsonTask = gson.toJson(taskManager.getTaskById(id));
        sendText(exchange, gsonTask);
        exchange.close();
    }

    private void postTask(HttpExchange exchange) throws java.io.IOException {
        try {
            String jsonTask = getStringFromBody(exchange);
            Gson gson = buildGson();
            Task task = gson.fromJson(jsonTask, Task.class);
            if (task.getName().isBlank() || task.getStatus() == null) {
                sendBadRequest(exchange);
                return;
            }
            if (task.getId() == 0) {
                taskManager.createTask(new Task(task));
            } else {
                taskManager.updateTask(new Task(task));
            }
            sendSuccess(exchange);
        } catch (JsonSyntaxException e) {
            sendBadRequest(exchange);
        }
    }

    private void deleteTask(HttpExchange exchange, int id) throws java.io.IOException {
        taskManager.removeTaskById(id);
        sendSuccess(exchange);
    }
}
