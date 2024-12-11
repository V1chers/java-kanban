package com.practicum.TaskManager.model;

import java.io.IOException;

public class ManagerSaveException extends IOException {
    public ManagerSaveException() {
        super("Ошибка при сохранении файла");
    }
}
