package com.practicum.TaskServer.Handler;

import com.google.gson.Gson;
import com.practicum.TaskManager.model.*;
import com.practicum.TaskManager.service.Managers;
import com.practicum.TaskManager.service.TaskManager;
import com.practicum.TaskServer.AdaptersAndTokens.EpicListToken;
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

public class EpicHandlerTest {
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
    void getEpics() throws java.io.IOException, java.lang.InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8080/epics"))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Epic> epics = gson.fromJson(response.body(), new EpicListToken().getType());

        assertEquals(200, response.statusCode());
        assertEquals(taskManager.getEpics(), epics);
    }

    @Test
    void getEpicSubtasks() throws java.io.IOException, java.lang.InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8080/epics/1007592185/subtasks"))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Subtask> subtasks = gson.fromJson(response.body(), new SubtaskListToken().getType());
        assertEquals(taskManager.getSubtasksOfEpic(1007592185), subtasks);

        request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8080/epics/123/subtasks"))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        subtasks = gson.fromJson(response.body(), new SubtaskListToken().getType());
        assertEquals(404, response.statusCode());
        assertNull(subtasks);
    }

    @Test
    void getEpic() throws java.io.IOException, java.lang.InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8080/epics/1007592185"))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Epic epic = gson.fromJson(response.body(), Epic.class);
        assertEquals(taskManager.getEpicById(1007592185), epic);

        request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8080/epics/123"))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        epic = gson.fromJson(response.body(), Epic.class);
        assertEquals(404, response.statusCode());
        assertNull(epic);
    }

    @Test
    void postEpic() throws java.io.IOException, java.lang.InterruptedException {
        String gsonEpic = gson.toJson(taskManager.getEpicById(1007592185));
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gsonEpic))
                .uri(URI.create("http://localhost:8080/epics"))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        gsonEpic = "{\n" +
                "\t\"subtasks\": {\n" +
                "\t\t\"1999792966\": {\n" +
                "\t\t\t\"epicId\": 1007592185,\n" +
                "\t\t\t\"name\": \"купить хлеб\",\n" +
                "\t\t\t\"description\": \"\",\n" +
                "\t\t\t\"id\": 1999792966,\n" +
                "\t\t\t\"status\": \"NEW\"\n" +
                "\t\t},\n" +
                "\t\t\"2000315197\": {\n" +
                "\t\t\t\"epicId\": 1007592185,\n" +
                "\t\t\t\"name\": \"купить молоко\",\n" +
                "\t\t\t\"description\": \"\",\n" +
                "\t\t\t\"id\": 2000315197,\n" +
                "\t\t\t\"status\": \"NEW\"\n" +
                "\t\t}\n" +
                "\t},\n" +
                "\t\"name\": \"купить продукты\",\n" +
                "\t\"description\": \"сходить в ближайший магазин во время прогулки\",\n" +
                "\t\"status\": \"NEW\"\n" +
                "}";
        request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gsonEpic))
                .uri(URI.create("http://localhost:8080/epics"))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode());

        Epic epicWithId = new Epic("покормить голубей", "покормить голубей купленным в магазине хлебом");
        Subtask subtask = new Subtask("раскрошить хлеб", "что бы голуби смогли его съесть",
                Status.NEW, pigeonsEpic.getId());
        epicWithId.addSubtask(subtask);
        gsonEpic = gson.toJson(epicWithId);
        request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gsonEpic))
                .uri(URI.create("http://localhost:8080/epics"))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertEquals(epicWithId, taskManager.getEpicById(epicWithId.getId()));

        Epic epicWithoutId = new Epic("epicWithoutId", "123");
        subtask = new Subtask("RandomName", "123", Status.NEW, epicWithoutId.getId());
        epicWithoutId.addSubtask(subtask);
        gsonEpic = "{\n" +
                "\t\"subtasks\": {\n" +
                "\t\t\"529943644\": {\n" +
                "\t\t\t\"epicId\": 420705434,\n" +
                "\t\t\t\"name\": \"RandomName\",\n" +
                "\t\t\t\"description\": \"123\",\n" +
                "\t\t\t\"status\": \"NEW\"\n" +
                "\t\t}\n" +
                "\t},\n" +
                "\t\"name\": \"epicWithoutId\",\n" +
                "\t\"description\": \"123\",\n" +
                "\t\"status\": \"NEW\"\n" +
                "}";
        request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gsonEpic))
                .uri(URI.create("http://localhost:8080/epics"))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertEquals(epicWithoutId, taskManager.getEpicById(epicWithoutId.getId()));
    }

    @Test
    void deleteEpic() throws java.io.IOException, java.lang.InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(URI.create("http://localhost:8080/epics/1007592185"))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        assertThrows(NotFoundException.class, () -> taskManager.getEpicById(1059876834));
    }
}
