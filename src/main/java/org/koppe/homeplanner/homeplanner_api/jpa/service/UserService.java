package org.koppe.homeplanner.homeplanner_api.jpa.service;

import java.util.List;
import java.util.Optional;

import org.koppe.homeplanner.homeplanner_api.jpa.entitiy.User;
import org.koppe.homeplanner.homeplanner_api.jpa.repository.UserRepository;
import org.koppe.homeplanner.homeplanner_api.security.PasswordEncryption;
import org.koppe.homeplanner.homeplanner_api.web.dto.UserDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserService {
    /**
     * Logger
     */
    private final Logger logger = LoggerFactory.getLogger(UserService.class);
    /**
     * Database repository for interacting with users
     */
    private UserRepository users;

    private final String userCache = "users";

    /**
     * Queries database for the user with given id
     * 
     * @param id Id to be queried
     * @return Optional containing that user or null
     */
    @Cacheable(value = userCache, key = "#id")
    public Optional<User> findUserByid(Long id) throws IllegalArgumentException {
        if (id == null) {
            logger.debug("No id given");
            throw new IllegalArgumentException();
        }
        return users.findById(id);
    }

    /**
     * Creates a user with given username and password: Password will only be saved
     * hashed
     * 
     * @param name     Name of the new user
     * @param password Password of the new user
     * @return Created user
     */
    @Transactional
    public User createUser(@NotNull String name, @NotNull String password) {
        User user = new User();
        user.setName(name);
        user.setPwHash(new BCryptPasswordEncoder(12).encode(password));
        return createUser(user);
    }

    /**
     * Creates new user in the database
     * 
     * @param user User to create
     * @return Created user
     * @throws IllegalArgumentException if no User is given
     */
    @Transactional
    public User createUser(User user) throws IllegalArgumentException {
        if (user == null) {
            logger.info("No user argument given");
            throw new IllegalArgumentException();
        }
        return users.save(user);
    }

    public List<User> findByName(String name) {
        return users.findByName(name);
    }

    public boolean passwordMatches(UserDto u) throws IllegalArgumentException {
        List<User> userList = users.findByName(u.getName());
        if (userList.size() == 0) {
            logger.info("User with name {} does not exist", u.getName());
            throw new IllegalArgumentException();
        }
        return PasswordEncryption.matches(u.getPassword(), userList.get(0).getPwHash());
    }

    public boolean userExists(Long id) throws IllegalArgumentException {
        if (id == null) {
            logger.info("No id given");
            throw new IllegalArgumentException();
        }
        return users.existsById(id);
    }

    @Transactional
    @CacheEvict(value = userCache, key = "#id")
    public User deleteUser(Long id) throws IllegalArgumentException {
        if (id == null) {
            logger.info("No id given");
            throw new IllegalArgumentException();
        }

        Optional<User> u = users.findById(id);
        if (u.isEmpty()) {
            logger.info("User with id {} does not exist", id);
            throw new IllegalArgumentException();
        }

        User user = u.get();
        if (user == null) {
            throw new IllegalArgumentException();
        }

        users.delete(user);
        return user;
    }
}
