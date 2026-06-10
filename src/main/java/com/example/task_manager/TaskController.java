package com.example.task_manager;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TaskController {

    private final TaskRepository repository;
    private final UserRepository userRepository;

    public TaskController(TaskRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    @GetMapping("/task")
    List<Task> all() {
        return repository.findAll();
    }

    @GetMapping("/task/my")
    List<Task> myTasks(Authentication auth) {
        Integer userId = (Integer) auth.getPrincipal();
        return repository.findByUserUserId(userId);
    }

    @PostMapping("/task")
    Task newTask(@RequestBody Map<String, String> body, Authentication auth) {
        Integer userId = (Integer) auth.getPrincipal();
        User user = userRepository.findById(userId).orElseThrow();
        Task task = new Task(body.get("title"), body.get("content"), user);
        return repository.save(task);
    }

    @GetMapping("/task/{id}")
    Task one(@PathVariable Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
    }

    @PutMapping("/task/{id}")
    Task replaceTask(@RequestBody Task newTask, @PathVariable Integer id) {
        return repository.findById(id)
                .map(task -> {
                    task.setTitle(newTask.getTitle());
                    task.setContent(newTask.getContent());
                    return repository.save(task);
                })
                .orElseGet(() -> repository.save(newTask));
    }

    @DeleteMapping("/task/{id}")
    void deleteTask(@PathVariable Integer id) {
        repository.deleteById(id);
    }
}
