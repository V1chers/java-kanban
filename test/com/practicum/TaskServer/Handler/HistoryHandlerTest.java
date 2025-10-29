package com.practicum.TaskServer.Handler;

import com.google.gson.Gson;
import com.practicum.TaskManager.model.Epic;
import com.practicum.TaskManager.model.Status;
import com.practicum.TaskManager.model.Subtask;
import com.practicum.TaskManager.model.Task;
import com.practicum.TaskManager.service.Managers;
import com.practicum.TaskManager.service.TaskManager;
import com.practicum.TaskServer.AdaptersAndTokens.TaskListToken;
import com.practicum.TaskServer.HttpTaskServer;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.UncheckedIOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HistoryHandlerTest {
    TaskManager taskManager;
    Task strollTask;
    Task trashTask;
    Epic productsEpic;
    Subtask milkSubtask;
    Subtask breadSubtask;
    Epic pigeonsEpic;
    Subtask feedSubtask;
    DateTimeFormatter dateTimeFormatter;
    String date;
    HttpServer httpServer;
    HttpClient client;
    Gson gson;

    @BeforeEach
    void createTaskManager() {
        dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        date = "12.12.2024 ";
        taskManager = Managers.getDefault();

        strollTask = new Task("прогулка", "сходить погулять", Status.NEW, Duration.ofMinutes(60)
                , LocalDateTime.parse(date + "08:00", dateTimeFormatter));
        taskManager.createTask(strollTask);

        trashTask = new Task("мусор", "выкинуть мусор", Status.NEW, Duration.ofMinutes(60)
                , LocalDateTime.parse(date + "10:00", dateTimeFormatter));
        taskManager.createTask(trashTask);

        productsEpic = new Epic("купить продукты", "сходить в ближайший магазин во время прогулки");
        taskManager.createEpic(productsEpic);
        milkSubtask = new Subtask("купить молоко", "", Status.NEW, productsEpic.getId(),
                Duration.ofMinutes(60), LocalDateTime.parse(date + "11:00", dateTimeFormatter));
        taskManager.createSubtask(milkSubtask);
        breadSubtask = new Subtask("купить хлеб", "", Status.NEW, productsEpic.getId(),
                Duration.ofMinutes(60), LocalDateTime.parse(date + "12:00", dateTimeFormatter));
        taskManager.createSubtask(breadSubtask);

        pigeonsEpic = new Epic("покормить голубей", "покормить голубей купленным в магазине хлебом");
        taskManager.createEpic(pigeonsEpic);
        feedSubtask = new Subtask("раскрошить хлеб", "что бы голуби смогли его съесть",
                Status.NEW, pigeonsEpic.getId(), Duration.ofMinutes(60), LocalDateTime.parse(date + "13:00",
                dateTimeFormatter));
        taskManager.createSubtask(feedSubtask);

        httpServer = HttpTaskServer.start(taskManager);

        gson = BaseHttpHandler.buildGson();

        try {
            client = HttpClient.newHttpClient();
        } catch (UncheckedIOException e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    void closeServer() {
        httpServer.stop(0);
    }

    @Test
    void getHistory() throws java.io.IOException, java.lang.InterruptedException {
        taskManager.getEpicById(pigeonsEpic.getId());
        taskManager.getTaskById(strollTask.getId());
        taskManager.getSubtaskById(milkSubtask.getId());

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8080/history"))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Task> history = gson.fromJson(response.body(), new TaskListToken().getType());

        assertEquals(200, response.statusCode());
        assertEquals(taskManager.getHistory().size(), history.size());
    }
}
