package com.softserve.itacademy.todolist.dto;


import com.softserve.itacademy.todolist.model.User;
import com.softserve.itacademy.todolist.service.UserService;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

@Component
public class UserTransformer {

    private final UserService userService;

    public UserTransformer(UserService userService) {
        this.userService = userService;
    }

    public User convertToUpdateEntity(UserDto userDto, BindingResult bindingResult) {
        User user = userService.readById(userDto.getId());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setEmail(userDto.getEmail());
        user.setRole(userDto.getRole());
        user.setPassword(userDto.getNewPassword());
        user.setRole(userDto.getRole());
        return user;
    }

    public User convertToInsertEntity(UserDto userDto) {
        User user = new User();
        user.setId(userDto.getId());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setEmail(userDto.getEmail());
        user.setPassword(userDto.getPassword());
        user.setRole(userDto.getRole());
        return user;
    }

    public UserDto convertToDto(User user) {
        return new UserDto(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPassword(),
                null,
                user.getRole()
        );
    }
}
