package com.softserve.itacademy.todolist.repository;

import com.softserve.itacademy.todolist.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);

}
