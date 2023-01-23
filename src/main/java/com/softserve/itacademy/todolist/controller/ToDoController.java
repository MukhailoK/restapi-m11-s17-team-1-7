package com.softserve.itacademy.todolist.controller;

import com.softserve.itacademy.todolist.dto.CollaboratorRequest;
import com.softserve.itacademy.todolist.dto.TodoResponse;
import com.softserve.itacademy.todolist.dto.UserResponse;
import com.softserve.itacademy.todolist.model.ToDo;
import com.softserve.itacademy.todolist.model.User;
import com.softserve.itacademy.todolist.service.ToDoService;
import com.softserve.itacademy.todolist.service.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class ToDoController {
    private final ToDoService todoService;

    private final UserService userService;

    public ToDoController(ToDoService todoService, UserService userService) {
        this.todoService = todoService;
        this.userService = userService;
    }

    @GetMapping("/todos")
    List<TodoResponse> getAll() {
        return todoService.getAll().stream()
                .map(TodoResponse::new)
                .collect(Collectors.toList());
    }


    @PreAuthorize("(authentication.principal.id == #id) || hasRole('ROLE_ADMIN')")
    @GetMapping("/{u_id}/todos")
    public ResponseEntity<TodoResponse> read(@PathVariable("u_id") long id) {
        HttpHeaders headers = new HttpHeaders();

        ToDo toDo = todoService.readById(id);

        if (toDo == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }


        return new ResponseEntity<>(new TodoResponse(toDo), headers, HttpStatus.OK);
    }

    @PostMapping("/todos")
    public ResponseEntity<TodoResponse> create(@RequestBody @Valid TodoResponse newTodo) {
        HttpHeaders headers = new HttpHeaders();
        if (newTodo == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        User owner;
        try{
            owner = userService.readById(newTodo.getOwnerId());
        } catch (Exception e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        todoService.create(TodoResponse.transformToEntity(newTodo, owner));
        return new ResponseEntity<>(newTodo, headers, HttpStatus.CREATED);
    }


    @PreAuthorize("(authentication.principal.id == #ownerId) || hasRole('ROLE_ADMIN')")
    @PutMapping("/{u_id}/todos")
    public ResponseEntity<TodoResponse> update(@PathVariable("u_id") long ownerId, @RequestBody @Valid TodoResponse updateTodo) {
        HttpHeaders headers = new HttpHeaders();
        if (updateTodo == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        User owner;
        try{
            owner = userService.readById(ownerId);
        } catch (Exception e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        ToDo toDo = todoService.readById(updateTodo.getId());

        if(toDo.getOwner().getId() != ownerId){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        todoService.update(TodoResponse.transformToEntity(updateTodo, owner));
        return new ResponseEntity<>(updateTodo, headers, HttpStatus.CREATED);
    }

    @DeleteMapping("{todo_id}")
    public ResponseEntity<TodoResponse> update(@PathVariable("todo_id") long todoId) {
        ToDo deleteTodo;
        try{
            deleteTodo = todoService.readById(todoId);
        } catch (Exception e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        todoService.delete(todoId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/todos/{t_id}/collaborators")
    List<UserResponse> getAllCollaborators(@PathVariable("t_id") long todoId) {
        return todoService.readById(todoId).getCollaborators().stream()
                .map(UserResponse::new)
                .collect(Collectors.toList());
    }

    @PreAuthorize("(authentication.principal.id == #userId) || hasRole('ROLE_ADMIN')")
    @PutMapping("/{user_id}/todos/{t_id}/collaborators")
    ResponseEntity<?> addCollaborator(@PathVariable("t_id") long todoId, @PathVariable("user_id") long userId, @RequestBody CollaboratorRequest collaboratorRequest) {
        ToDo toDo = todoService.readById(todoId);
        List<User> collaborators = toDo.getCollaborators();
        User collaborator = userService.readById(collaboratorRequest.getCollaboratorId());
        if(collaborators.contains(collaborator) || toDo.getOwner().getId() == collaborator.getId()){
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        collaborators.add(collaborator);
        toDo.setCollaborators(collaborators);
        todoService.update(toDo);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PreAuthorize("(authentication.principal.id == #userId) || hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{user_id}/todos/{t_id}/collaborators")
    ResponseEntity<UserResponse> removeCollaborator(@PathVariable("t_id") long todoId, @PathVariable("user_id") long userId, @RequestBody CollaboratorRequest collaboratorRequest) {
        ToDo toDo = todoService.readById(todoId);
        List<User> collaborators = toDo.getCollaborators();


        User collaborator = userService.readById(collaboratorRequest.getCollaboratorId());
        if(!collaborators.contains(collaborator) || toDo.getOwner().getId() == collaborator.getId()){
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        collaborators.remove(collaborator);
        toDo.setCollaborators(collaborators);
        todoService.update(toDo);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
