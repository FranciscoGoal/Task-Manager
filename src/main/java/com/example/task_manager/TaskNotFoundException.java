package com.example.task_manager;

public class TaskNotFoundException extends RuntimeException {

    public TaskNotFoundException(Integer id) {
        super("Could not find task " + id);
    }
}
