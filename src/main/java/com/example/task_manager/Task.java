package com.example.task_manager;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import java.util.Objects;

@Entity
public class Task{

	private @Id
	@GeneratedValue Long id;
	private String title;
	private String description;

	public Task(){}

        public Task(String title, String description){

                this.title = title;
                this.description = description;
        }

        public Long getId(){return id;}
        public String getTitle(){return title;}
        public String getDescription(){return description;}

        public void setId(Long id){this.id = id;}
        public void setTitle(String title){this.title = title;}
        public void setDescription(String description){this.description = description;}
}

