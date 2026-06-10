package com.example.task_manager;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Integer> {
    List<Task> findByUserUserId(Integer userId);
}
