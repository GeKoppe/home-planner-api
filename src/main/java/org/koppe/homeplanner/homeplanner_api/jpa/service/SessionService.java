package org.koppe.homeplanner.homeplanner_api.jpa.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.koppe.homeplanner.homeplanner_api.jpa.entitiy.Session;
import org.koppe.homeplanner.homeplanner_api.jpa.entitiy.User;
import org.koppe.homeplanner.homeplanner_api.jpa.repository.SessionRepository;
import org.koppe.homeplanner.homeplanner_api.jpa.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class SessionService {
    private final Logger logger = LoggerFactory.getLogger(SessionService.class);

    /**
     * Session respository
     */
    private SessionRepository sessions;
    /**
     * User repository
     */
    private UserRepository users;

    @Transactional
    public Session createSession(@NotNull Long userId) throws IllegalArgumentException {
        Optional<User> u = users.findById(userId);
        if (u.isEmpty()) {
            logger.info("User with id {} does not exist", userId);
            throw new IllegalArgumentException();
        }

        User user = u.get();
        return create(user);
    }

    public Session createSession(@NotNull String userName) throws IllegalArgumentException {
        List<User> u = users.findByName(userName);
        if (u.size() == 0) {
            logger.info("No user for name {} exists", userName);
            throw new IllegalArgumentException();
        }

        return create(u.get(0));
    }

    private Session create(@NotNull User user) {
        logger.debug("Creating session for user {}", user.getName());

        String guid = UUID.randomUUID().toString();
        LocalDateTime expiration = LocalDateTime.now().plusMinutes(10L);
        Session session = new Session(guid, user, expiration);

        return sessions.save(session);
    }
}
