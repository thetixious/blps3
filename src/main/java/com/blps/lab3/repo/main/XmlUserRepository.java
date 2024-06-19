package com.blps.lab3.repo.main;

import com.blps.lab3.model.mainDB.User;

import java.util.List;
import java.util.Optional;

public class XmlUserRepository implements UserRepository {

    private final XmlUserMarshaller xmlUserMarshaller;
    private long currentId;

    public XmlUserRepository(XmlUserMarshaller xmlUserMarshaller)  {
        this.xmlUserMarshaller = xmlUserMarshaller;
        this.currentId = initializeCurrentId();
    }

    private long initializeCurrentId()  {
        List<User> users = getAllUsers();
        return users.stream()
                .map(User::getId)
                .max(Long::compare)
                .orElse(0L) + 1;
    }

    public Optional<User> findById(Long id)  {
        return getAllUsers().stream().filter(user -> user.getId().equals(id)).findFirst();
    }
    @Override
    public User save(User user) {
        if (user.getId() == null) {
            user.setId(currentId++);
        }
        List<User> users = getAllUsers();
        users.removeIf(existingUser -> existingUser.getId().equals(user.getId()));
        users.add(user);
        xmlUserMarshaller.writeUsers(users);
        return user;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return getAllUsers().stream().filter(user -> user.getUsername().equals(username)).findFirst();
    }

    @Override
    public boolean existsByUsername(String username) {
        return getAllUsers().stream().anyMatch(user -> user.getUsername().equals(username));
    }

    @Override
    public boolean existsByEmail(String email)  {
        return getAllUsers().stream().anyMatch(user -> user.getEmail().equals(email));
    }

    @Override
    public List<User> getAllUsers() {
        return xmlUserMarshaller.readUsers();
    }
}
