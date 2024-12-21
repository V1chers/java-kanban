package com.practicum.TaskServer.Handler;

import com.google.gson.Gson;
import com.practicum.TaskManager.model.Epic;
import com.practicum.TaskManager.model.NotFoundException;
import com.practicum.TaskManager.model.Status;
import com.practicum.TaskManager.model.Subtask;
import com.practicum.TaskManager.service.Managers;
import com.practicum.TaskManager.service.TaskManager;
import com.practicum.TaskServer.AdaptersAndTokens.SubtaskListToken;
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
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SubtaskHandlerTest {
    TaskManager taskManager;
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

        productsEpic = new Epic("купить продукты", "сходить в ближайший магазин во время прогулки");
        taskManager.createEpic(productsEpic);
        milkSubtask = new Subtask("купить молоко", "", Status.NEW, productsEpic.getId());
        taskManager.createSubtask(milkSubtask);
        breadSubtask = new Subtask("купить хлеб", "", Status.NEW, productsEpic.getId());
        taskManager.createSubtask(breadSubtask);

        pigeonsEpic = new Epic("покормить голубей", "покормить голубей купленным в магазине хлебом");
        taskManager.createEpic(pigeonsEpic);
        feedSubtask = new Subtask("раскрошить хлеб", "что бы голуби смогли его съесть",
                Status.NEW, pigeonsEpic.getId());
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
    void getSubtasks() throws java.io.IOException, java.lang.InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Subtask> subtasks = gson.fromJson(response.body(), new SubtaskListToken().getType());

        assertEquals(200, response.statusCode());
        assertEquals(taskManager.getSubtasks(), subtasks);
    }

    @Test
    void getSubtask() throws java.io.IOException, java.lang.InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8080/subtasks/1999792966"))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Subtask subtask = gson.fromJson(response.body(), Subtask.class);
        assertEquals(taskManager.getSubtaskById(1999792966), subtask);

        request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8080/subtasks/123"))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        subtask = gson.fromJson(response.body(), Subtask.class);
        assertEquals(404, response.statusCode());
        assertNull(subtask);
    }

    @Test
    void postSubtask() throws java.io.IOException, java.lang.InterruptedException {
        String gsonSubtask = gson.toJson(taskManager.getSubtaskById(1999792966));
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gsonSubtask))
                .uri(URI.create("http://localhost:8080/subtasks"))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        gsonSubtask = "{\n" +
                "\t\"epicId\": 1007592185,\n" +
                "\t\"name\": \"купить хлеб\",\n" +
                "\t\"description\": \"\",\n" +
                "\t\"status\": \"NEW\"\n" +
                "}";
        request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gsonSubtask))
                .uri(URI.create("http://localhost:8080/subtasks"))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode());

        Subtask subtaskWithoutEpic = new Subtask("subtaskWithoutEpic", "", Status.NEW, 1234);
        gsonSubtask = gson.toJson(subtaskWithoutEpic);
        request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gsonSubtask))
                .uri(URI.create("http://localhost:8080/subtasks"))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
        assertThrows(NotFoundException.class, () -> taskManager.getSubtaskById(subtaskWithoutEpic.getId()));

        Subtask chipsSubtask = new Subtask("Купить чипсы", "", Status.NEW, productsEpic.getId());
        gsonSubtask = gson.toJson(chipsSubtask);
        request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gsonSubtask))
                .uri(URI.create("http://localhost:8080/subtasks"))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertEquals(chipsSubtask, taskManager.getSubtaskById(chipsSubtask.getId()));

        Subtask potatoSubtask = new Subtask("Купить картошку", "", Status.NEW, productsEpic.getId());
        gsonSubtask = "{\n" +
                "\t\"epicId\": 1007592185,\n" +
                "\t\"name\": \"Купить картошку\",\n" +
                "\t\"description\": \"\",\n" +
                "\t\"status\": \"NEW\"\n" +
                "}";
        request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gsonSubtask))
                .uri(URI.create("http://localhost:8080/subtasks"))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertEquals(potatoSubtask, taskManager.getSubtaskById(potatoSubtask.getId()));
    }

    @Test
    void deleteSubtask() throws java.io.IOException, java.lang.InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(URI.create("http://localhost:8080/subtasks/1999792966"))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        assertThrows(NotFoundException.class, () -> taskManager.getSubtaskById(1999792966));
    }
}
