package com.practicum.TaskServer.Handler;

import com.google.gson.Gson;
import com.practicum.TaskManager.model.NotFoundException;
import com.practicum.TaskManager.model.Status;
import com.practicum.TaskManager.model.Task;
import com.practicum.TaskManager.service.Managers;
import com.practicum.TaskManager.service.TaskManager;
import com.practicum.TaskServer.AdaptersAndTokens.TaskListToken;
import com.practicum.TaskServer.HttpTaskServer;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.*;

import java.io.UncheckedIOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TaskHandlerTest {
    TaskManager taskManager;
    Task strollTask;
    Task trashTask;
    Task anotherRandomTask;
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

        anotherRandomTask = new Task("random", "...", Status.NEW);
        taskManager.createTask(anotherRandomTask);

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
    void getTasks() throws java.io.IOException, java.lang.InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8080/tasks"))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Task> tasks = gson.fromJson(response.body(), new TaskListToken().getType());

        assertEquals(200, response.statusCode());
        assertEquals(taskManager.getTasks(), tasks);
    }

    @Test
    void getTask() throws java.io.IOException, java.lang.InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8080/tasks/977955276"))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Task task = gson.fromJson(response.body(), Task.class);
        assertEquals(taskManager.getTaskById(977955276), task);

        request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8080/tasks/1059876834"))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        task = gson.fromJson(response.body(), Task.class);
        assertEquals(taskManager.getTaskById(1059876834), task);

        request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8080/tasks/123"))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        task = gson.fromJson(response.body(), Task.class);
        assertEquals(404, response.statusCode());
        assertNull(task);
    }

    @Test
    void postTask() throws java.io.IOException, java.lang.InterruptedException {
        String gsonTask = gson.toJson(taskManager.getTaskById(1059876834));
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gsonTask))
                .uri(URI.create("http://localhost:8080/tasks"))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(409, response.statusCode());

        gsonTask = "{\n" +
                "\t\"name\": \"прогулка\",\n" +
                "\t\"description\": \"сходить погулять\",\n" +
                "\t\"status\": \"NEW\",\n" +
                "\t\"duration\": 60,\n" +
                "\t\"startTime\": \"2024.12.12 08:00\"\n" +
                "}";
        request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gsonTask))
                .uri(URI.create("http://localhost:8080/tasks"))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(409, response.statusCode());

        Task task = new Task("мусор", "выкинуть мусор", Status.NEW, Duration.ofMinutes(60)
                , LocalDateTime.parse(date + "18:00", dateTimeFormatter));
        gsonTask = gson.toJson(task);
        request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gsonTask))
                .uri(URI.create("http://localhost:8080/tasks"))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertEquals(taskManager.getTaskById(-341379032), task);

        task = new Task("TaskWithoutId", "123", Status.NEW, Duration.ofMinutes(60)
                , LocalDateTime.parse(date + "20:00", dateTimeFormatter));
        gsonTask = "{\n" +
                "\t\"name\": \"TaskWithoutId\",\n" +
                "\t\"description\": \"123\",\n" +
                "\t\"status\": \"NEW\",\n" +
                "\t\"duration\": 60,\n" +
                "\t\"startTime\": \"2024.12.12 20:00\"\n" +
                "}";
        request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gsonTask))
                .uri(URI.create("http://localhost:8080/tasks"))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertEquals(taskManager.getTaskById(1433945557), task);
    }

    @Test
    void deleteTask() throws java.io.IOException, java.lang.InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(URI.create("http://localhost:8080/tasks/1059876834"))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        assertThrows(NotFoundException.class, () -> taskManager.getTaskById(1059876834));
    }
}
