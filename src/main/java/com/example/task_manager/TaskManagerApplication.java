package com.example.task_manager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TaskManagerApplication {

	public static void main(String[] args) {
		// TODO: integrate persistent database — currently using in-memory H2
		SpringApplication.run(TaskManagerApplication.class, args);
	}

}
