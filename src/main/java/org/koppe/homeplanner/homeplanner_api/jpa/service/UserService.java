package org.koppe.homeplanner.homeplanner_api.jpa.service;

import java.util.List;
import java.util.Optional;

import org.koppe.homeplanner.homeplanner_api.jpa.entitiy.User;
import org.koppe.homeplanner.homeplanner_api.jpa.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserService {
    private UserRepository users;

    /**
     * Queries database for the user with given id
     * 
     * @param id Id to be queried
     * @return Optional containing that user or null
     */
    public Optional<User> findUserByid(Long id) {
        return users.findById(id);
    }

    public User createUser(@NotNull String name, @NotNull String password) {
        User user = new User();
        user.setName(name);
        user.setPwHash(new BCryptPasswordEncoder(12).encode(password));
        return createUser(user);
    }
    public User createUser(User user) {
        return users.save(user);
    }

    public List<User> findByName(String name) {
        return users.findByName(name);
    }
}
