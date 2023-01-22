package com.softserve.itacademy.todolist.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.softserve.itacademy.todolist.model.ToDo;
import com.softserve.itacademy.todolist.model.User;
import com.softserve.itacademy.todolist.service.UserService;
import lombok.Value;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

@Value
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TodoResponse {
    Long id;
    String title;
    LocalDateTime createdAt;
    Long ownerId;

    public TodoResponse(Long id, String title, LocalDateTime createdAt, Long ownerId) {
        this.id = id;
        this.title = title;
        this.createdAt = createdAt;
        this.ownerId = ownerId;
    }

    public TodoResponse(ToDo todo) {
        this.id = todo.getId();
        this.title = todo.getTitle();
        this.createdAt = todo.getCreatedAt();
        this.ownerId = todo.getOwner().getId();
    }

    public static ToDo transformToEntity(TodoResponse todoResponse, User owner){
        ToDo newTodo = new ToDo();
        newTodo.setId(todoResponse.getId());
        newTodo.setTitle(todoResponse.getTitle());
        newTodo.setOwner(owner);
        newTodo.setCreatedAt(todoResponse.getCreatedAt());
        return newTodo;
    }

}
