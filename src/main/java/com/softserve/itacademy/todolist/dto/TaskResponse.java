package com.softserve.itacademy.todolist.dto;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.softserve.itacademy.todolist.model.*;
import com.softserve.itacademy.todolist.service.UserService;
import lombok.Value;
import org.springframework.beans.factory.annotation.Autowired;

@Value
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TaskResponse {
    Long id;
    String name;
    Priority priority;
    ToDo toDo;
    State state;

    public TaskResponse(Long id, String name, Priority priority, ToDo toDo, State state) {
        this.id = id;
        this.name = name;
        this.priority = priority;
        this.toDo = toDo;
        this.state = state;
    }

   public TaskResponse(Task task){
        this.id = task.getId();
        this.name = task.getName();
        this.priority = task.getPriority();
        this.toDo = task.getTodo();
        this.state = task.getState();
   }


    public static Task transformToEntity(TaskResponse taskResponse, ToDo toDo){
        Task newTask = new Task();
        newTask.setId(taskResponse.getId());
        newTask.setName(taskResponse.getName());
        newTask.setPriority(taskResponse.getPriority());
        newTask.setTodo(toDo);
        return newTask;

   }
}
