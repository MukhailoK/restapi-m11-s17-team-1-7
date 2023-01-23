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
    long id;
    String name;
    String priority;
    long toDoId;
    String state;

    public TaskResponse(long id, String name, String priority, long toDoId, String state) {
        this.id = id;
        this.name = name;
        this.priority = priority;
        this.toDoId = toDoId;
        this.state = state;
    }

    public TaskResponse(Task task){
        this.id = task.getId();
        this.name = task.getName();
        this.priority = task.getPriority().name();
        this.toDoId = task.getTodo().getId();
        this.state = task.getState().getName();
   }


    public static Task transformToEntity(TaskResponse taskResponse, ToDo toDo, State state){
        Task newTask = new Task();
        newTask.setId(taskResponse.getId());
        newTask.setName(taskResponse.getName());
        newTask.setPriority(Priority.valueOf(taskResponse.getPriority()));
        newTask.setTodo(toDo);
        newTask.setState(state);

        return newTask;

   }
}
