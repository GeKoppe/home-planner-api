package org.koppe.homeplanner.homeplanner_api.web.controller;

import org.koppe.homeplanner.homeplanner_api.jpa.entitiy.Session;
import org.koppe.homeplanner.homeplanner_api.jpa.service.SessionService;
import org.koppe.homeplanner.homeplanner_api.jpa.service.UserService;
import org.koppe.homeplanner.homeplanner_api.web.dto.SessionDto;
import org.koppe.homeplanner.homeplanner_api.web.dto.UserDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/session")
@AllArgsConstructor
public class SessionController {
    /**
     * Logger
     */
    private final Logger logger = LoggerFactory.getLogger(SessionController.class);

    /**
     * Db service for working with sessions in the database
     */
    private SessionService sessions;
    private UserService users;

    @PostMapping(path = "/login", produces = "application/json")
    public Mono<ResponseEntity<SessionDto>> login(@RequestBody Mono<UserDto> loginInfo) {
        return loginInfo.flatMap(login -> {
            return Mono.fromCallable(() -> {
                if (!users.passwordMatches(login)) {
                    return ResponseEntity.of(ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(401),
                            "Username and password don't match")).build();
                }
                Session session = sessions.createSession(login.getName());
                return ResponseEntity.ok(new SessionDto(session.getGuid(), session.getUser().getName(), session.getExpiration()));
            });
        });
    }
}
