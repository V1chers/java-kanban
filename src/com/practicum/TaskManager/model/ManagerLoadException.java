package com.practicum.TaskManager.model;

import java.io.IOException;

public class ManagerLoadException extends IOException {
    public ManagerLoadException() {
        super("Ошибка при загрузке файла");
    }
}
