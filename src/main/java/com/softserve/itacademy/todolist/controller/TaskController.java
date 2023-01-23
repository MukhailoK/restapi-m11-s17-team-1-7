package com.softserve.itacademy.todolist.controller;
import com.softserve.itacademy.todolist.dto.TaskResponse;
import com.softserve.itacademy.todolist.model.State;
import com.softserve.itacademy.todolist.model.Task;
import com.softserve.itacademy.todolist.model.ToDo;
import com.softserve.itacademy.todolist.service.StateService;
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
    private final StateService stateService;

    public TaskController(TaskService taskService, ToDoService toDoService, StateService stateService){
        this.taskService = taskService;
        this.toDoService = toDoService;
        this.stateService = stateService;
    }


    @GetMapping("/{u_id}/todos/{t_id}/tasks")
    public ResponseEntity<List<TaskResponse>> readAll( @PathVariable("u_id") long uid, @PathVariable("t_id") long tid) {
        HttpHeaders headers = new HttpHeaders();
        List<TaskResponse> task = taskService.getByTodoId(tid).stream()
                .map(TaskResponse::new).collect(Collectors.toList());
        if (  task == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(task, headers, HttpStatus.OK);
    }

    @GetMapping("/{u_id}/todos/{t_id}/tasks/{task_id}")
    public ResponseEntity<TaskResponse> read( @PathVariable("u_id") long uid, @PathVariable("t_id") long tid, @PathVariable("task_id") long taskid) {
        HttpHeaders headers = new HttpHeaders();
        Task task = taskService.readById(taskid);
        if (  task == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(new TaskResponse((Task) task), headers, HttpStatus.OK);
    }

    @PostMapping("/{u_id}/todos/{t_id}/tasks")
    public ResponseEntity<TaskResponse> create( @PathVariable("t_id") long tid, @RequestBody @Valid TaskResponse newTask) {
        HttpHeaders headers = new HttpHeaders();
        ToDo toDo;

        if (newTask == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }


        try{
            toDo = toDoService.readById(tid);
        }catch (Exception e){
            return  new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        State state = stateService.getByName(newTask.getState());

        Task createdTask = TaskResponse.transformToEntity(newTask, toDo, state);

        taskService.create(createdTask);
        return new ResponseEntity<>(newTask, headers, HttpStatus.CREATED);
    }

    @PutMapping("/{u_id}/todos/{t_id}/tasks")
    public ResponseEntity<TaskResponse> update( @PathVariable("t_id") long tid, @RequestBody @Valid TaskResponse newTask) {
        HttpHeaders headers = new HttpHeaders();
        ToDo toDo;

        if (newTask == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try{
            toDo = toDoService.readById(tid);
        }catch (Exception e){
            return  new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

       Task task = taskService.readById(newTask.getId());

        if(task.getTodo().getId() != tid){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }


        State state = stateService.getByName(newTask.getState());

        taskService.update(TaskResponse.transformToEntity(newTask, toDo, state));
        return new ResponseEntity<>(newTask, headers, HttpStatus.CREATED);
    }

    @DeleteMapping("/{u_id}/todos/{t_id}/tasks/{task_id}")
    public ResponseEntity<TaskResponse> update(@PathVariable("task_id") long tid) {
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
