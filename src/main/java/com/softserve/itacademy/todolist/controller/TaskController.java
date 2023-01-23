package com.softserve.itacademy.todolist.controller;
import com.softserve.itacademy.todolist.dto.TaskResponse;
import com.softserve.itacademy.todolist.model.Task;
import com.softserve.itacademy.todolist.model.ToDo;
import com.softserve.itacademy.todolist.service.TaskService;
import com.softserve.itacademy.todolist.service.ToDoService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class TaskController {
    private final TaskService taskService;
    private final ToDoService toDoService;

    public TaskController(TaskService taskService, ToDoService toDoService){
        this.taskService = taskService;
        this.toDoService = toDoService;
    }

    @GetMapping("/tasks")
    List<TaskResponse> getAll() {
        return taskService.getAll().stream()
                .map(TaskResponse::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/{u_id}/todos/{t_id}/tasks")
    public ResponseEntity<TaskResponse> read(@PathVariable("u_id") long uid, @PathVariable("t_id") long tid ) {
        HttpHeaders headers = new HttpHeaders();
        ToDo toDo = toDoService.readById(uid);
        Task task = taskService.readById(tid);

        if ( toDo == null || task == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(new TaskResponse(task), headers, HttpStatus.OK);
    }

    @PutMapping("/{t_id}/tasks")
    public ResponseEntity<TaskResponse> update(@PathVariable("u_id") long uid, @PathVariable("t_id") long tid, @RequestBody @Valid TaskResponse newTask) {
        HttpHeaders headers = new HttpHeaders();
        ToDo toDo;

        if (newTask == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try{
            toDo = toDoService.readById(uid);
        }catch (Exception e){
            return  new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

       Task task = taskService.readById(newTask.getId());

        if(task.getTodo().getId() != uid){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        taskService.update(TaskResponse.transformToEntity(newTask, toDo));
        return new ResponseEntity<>(newTask, headers, HttpStatus.CREATED);
    }

    @DeleteMapping("{t_id}")
    public ResponseEntity<TaskResponse> update(@PathVariable("t_id") long tid) {
        Task deleteTask;

        try{
            deleteTask = taskService.readById(tid);
        } catch (Exception e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        taskService.delete(tid);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
