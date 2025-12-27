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
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;

/**
 * Service for dealing with sessions in the database
 */
@Service
@AllArgsConstructor
@SuppressWarnings("unused")
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

    /**
     * Entity manager for hibernate context
     */
    private EntityManager em;

    /**
     * Creates a session for user with given id, if the user id exists.
     * 
     * @param userId User id for which to create the session
     * @return Created session
     * @throws IllegalArgumentException If the user id does not exist
     */
    @Transactional
    public Session createSession(@NotNull Long userId) throws IllegalArgumentException {
        if (userId == null) {
            logger.debug("User id not given");
            throw new IllegalArgumentException();
        }
        Optional<User> u = users.findById(userId);
        if (u.isEmpty()) {
            logger.info("User with id {} does not exist", userId);
            throw new IllegalArgumentException();
        }

        User user = u.get();
        return create(user);
    }

    @Transactional
    public Session createSession(@NotNull String userName) throws IllegalArgumentException {
        List<User> u = users.findByName(userName);
        if (u.size() == 0) {
            logger.info("No user for name {} exists", userName);
            throw new IllegalArgumentException();
        }

        return create(u.get(0));
    }

    /**
     * Creates session for given user
     * 
     * @param user User for which to create a new session
     * @return Created session
     */
    @Transactional
    private Session create(@NotNull User user) {
        logger.debug("Creating session for user {}", user.getName());

        String guid = UUID.randomUUID().toString();
        LocalDateTime expiration = LocalDateTime.now().plusMinutes(10L);
        Session session = new Session(guid, user, expiration);

        return sessions.save(session);
    }

    /**
     * Finds session by guid
     * 
     * @param guid Guid of the session
     * @return Found session
     */
    public Optional<Session> findById(String guid) throws IllegalArgumentException {
        if (guid == null || guid.isBlank()) {
            logger.debug("No guid given");
            throw new IllegalArgumentException();
        }
        return sessions.findById(guid);
    }

    @Transactional
    public Session refreshSession(Session session) {
        LocalDateTime expiration = LocalDateTime.now().plusMinutes(10L);
        session.setExpiration(expiration);
        return sessions.save(session);
    }
}
