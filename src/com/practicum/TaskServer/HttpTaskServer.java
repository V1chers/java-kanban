package com.practicum.TaskServer;

import com.practicum.TaskManager.service.Managers;
import com.practicum.TaskManager.service.TaskManager;
import com.practicum.TaskServer.Handler.*;
import com.sun.net.httpserver.HttpServer;

import java.net.InetSocketAddress;
import java.nio.file.Paths;

public class HttpTaskServer {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getFileBacked(Paths.get("D:\\TaskManager.txt"));

        start(taskManager);
    }

    public static HttpServer start(TaskManager taskManager) {
        try {
            HttpServer httpServer = HttpServer.create(new InetSocketAddress(8080), 0);
            httpServer.createContext("/tasks", new TaskHandler(taskManager));
            httpServer.createContext("/epics", new EpicHandler(taskManager));
            httpServer.createContext("/subtasks", new SubtaskHandler(taskManager));
            httpServer.createContext("/history", new HistoryHandler(taskManager));
            httpServer.createContext("/prioritized", new PrioritizedHandler(taskManager));
            httpServer.start();
            return httpServer;
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
