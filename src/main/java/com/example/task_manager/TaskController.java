package com.example.task_manager;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TaskController {

	private final TaskRepository repository;

	public TaskController(TaskRepository repository){
		this.repository = repository;
	}

	@GetMapping("/task")
	List<Task> all() { return repository.findAll(); }

	@PostMapping("/task")
	Task newTask(@RequestBody Task newTask){ 
		return repository.save(newTask);
	}

	@GetMapping("/task/{id}")
	Task one(@PathVariable Long id){
		return repository.findById(id)
			.orElseThrow(() -> new TaskNotFoundException(id));
	}

	@PutMapping("/task/{id}")
	Task replaceTask(@RequestBody Task newTask, @PathVariable Long id){
	return repository.findById(id)
		.map(task -> {
			task.setTitle(newTask.getTitle());
			task.setDescription(newTask.getDescription());
			return repository.save(task);
		})
		.orElseGet(() -> {
		return repository.save(newTask);
		});
	}

	@DeleteMapping("/task/{id}")
	public void deleteTask(@PathVariable Long id){
		repository.deleteById(id);
	}

}
