package com.softserve.itacademy.todolist.dto;

import com.softserve.itacademy.todolist.model.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    @Id
    private Long id;
    @Pattern(regexp = "[A-Z][a-z]+", message = "Must start with a capital letter followed by one or more lowercase letters")
    private String firstName;
    @Pattern(regexp = "[A-Z][a-z]+", message = "Must start with a capital letter followed by one or more lowercase letters")
    private String lastName;
    @Pattern(regexp = "[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}", message = "Must be a valid e-mail address")
    private String email;
    @Pattern(regexp = "(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,}", message = "Must be minimum 6 characters, at least one letter and one number")
    @NotBlank(message = "Field cant`t be blank")
    private String password;
    @Pattern(regexp = "(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,}", message = "Must be minimum 6 characters, at least one letter and one number")
    private String newPassword;

    private Role role;

    public boolean isNew() {
        return this.id == null;
    }

}
