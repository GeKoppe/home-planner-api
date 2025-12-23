package org.koppe.homeplanner.homeplanner_api.web.controller;

import java.util.Optional;

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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/session")
@AllArgsConstructor
@Tag(name = "Session management", description = "Manages sessions within the application. Allows login and logout.")
public class SessionController {
    /**
     * Logger
     */
    private final Logger logger = LoggerFactory.getLogger(SessionController.class);

    /**
     * Db service for working with sessions in the database
     */
    private SessionService sessions;
    /**
     * DB Service for working with users in the database
     */
    private UserService users;

    /**
     * Creates a session for given user information
     * 
     * @param loginInfo Login information for the user
     * @return The created session
     */
    @PostMapping(path = "/login", produces = "application/json")
    @Operation(summary = "Session creation", description = "Creates a session, if the given login information is correct")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Session created", content = @Content(schema = @Schema(implementation = SessionDto.class))),
            @ApiResponse(responseCode = "401", description = "Login information incorrect", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public Mono<ResponseEntity<SessionDto>> login(@RequestBody Mono<UserDto> loginInfo) {
        return loginInfo.flatMap(login -> {
            return Mono.fromCallable(() -> {
                if (!users.passwordMatches(login)) {
                    logger.info("User name and password don't match");
                    return ResponseEntity.of(ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(401),
                            "Username and password don't match")).build();
                }
                Session session = sessions.createSession(login.getName());
                return ResponseEntity
                        .ok(new SessionDto(session.getGuid(), session.getUser().getName(), session.getExpiration()));
            });
        });
    }

    @PostMapping(path = "/refresh", produces = "application/json")
    @Operation(summary = "Refresh session", description = "Sets the session expiration to 10 minutes from now")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Session refreshed, expiration prolonged by 10 minutes", content = @Content(schema = @Schema(implementation = SessionDto.class))),
            @ApiResponse(responseCode = "400", description = "Session with given id does not belong to given user", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Session not found", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public Mono<ResponseEntity<SessionDto>> refresh(@RequestBody Mono<SessionDto> sessionInfo) {
        return sessionInfo.flatMap(session -> {
            return Mono.fromCallable(() -> {
                // Get the actual session that is represented by the dto
                Optional<Session> sOpt = sessions.findById(session.getGuid());
                if (sOpt.isEmpty()) {
                    logger.info("No session with id {} exists", session.getGuid());
                    return ResponseEntity.of(ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(404),
                            "Session with given id does not exist for given user")).build();
                }

                Session s = sOpt.get();
                if (!s.getUser().getName().equals(session.getUserName())) {
                    logger.info("No session with id {} exists", session.getGuid());
                    return ResponseEntity.of(ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(400),
                            "Session with given id does not exist for given user")).build();
                }
                logger.debug("Refreshing session {}", s.getGuid());

                // Refresh the session
                Session refreshed = sessions.refreshSession(s);
                logger.info("Refreshed session for user {}", s.getUser().getName());

                SessionDto dto = new SessionDto(refreshed.getGuid(), refreshed.getUser().getName(),
                        refreshed.getExpiration());

                return ResponseEntity.ok(dto);
            });
        });
    }
}
