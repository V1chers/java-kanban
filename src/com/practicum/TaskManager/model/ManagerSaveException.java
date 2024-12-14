package com.practicum.TaskManager.model;

import java.io.IOException;

public class ManagerSaveException extends IOException {
    public ManagerSaveException(String text, IOException exception) {
        super(text, exception);
    }
}
