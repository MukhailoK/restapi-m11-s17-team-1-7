package com.softserve.itacademy.todolist.controller;

import com.softserve.itacademy.todolist.dto.UserDto;
import com.softserve.itacademy.todolist.dto.UserResponse;
import com.softserve.itacademy.todolist.dto.UserTransformer;
import com.softserve.itacademy.todolist.model.User;
import com.softserve.itacademy.todolist.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final UserTransformer userTransformer;

    public UserController(UserService userService, UserTransformer userTransformer) {
        this.userService = userService;
        this.userTransformer = userTransformer;
    }

    @PostMapping("/user/create/")
    public ResponseEntity<Long> create(@RequestBody UserDto userDto, BindingResult bindingResult) {
        User newUser = userTransformer.convertToInsertEntity(userDto);
        if (userTransformer.convertToDto(userService.create(newUser)) != null) {
            return new ResponseEntity<>(HttpStatus.CREATED);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping(value = "/user/{id}/")
    public UserDto read(@PathVariable long id) {
        return userTransformer.convertToDto(userService.readById(id));
    }

    @PutMapping("/user/{id}/")
    public UserDto updateUserById(@PathVariable long id, @RequestBody UserDto userDto, BindingResult bindingResult) {
        User oldUser = userTransformer.convertToUpdateEntity(userDto, bindingResult);
        return userTransformer.convertToDto(userService.update(oldUser));
    }
    @Secured("ROLE_ADMIN")
    @DeleteMapping("/user/{id}/")
    public ResponseEntity<Long> deletePost(@PathVariable Long id) {
        if (userService.delete(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(id, HttpStatus.OK);
    }
    @Secured("ROLE_ADMIN")
    @GetMapping(value = "/all/")
    List<UserResponse> getAll() {
        return userService.getAll().stream()
                .map(UserResponse::new)
                .collect(Collectors.toList());
    }
}
