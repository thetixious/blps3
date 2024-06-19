package com.blps.lab3.repo.main;

import com.blps.lab3.model.mainDB.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    Optional<User> findByUsername(String username) ;
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Optional<User> findById(Long id) ;
    List<User> getAllUsers();
    User save(User user);
}
