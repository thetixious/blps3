package com.blps.lab2.repo.main;

import com.blps.lab2.model.mainDB.User;
import jakarta.xml.bind.JAXBException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

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
