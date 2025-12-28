package org.koppe.homeplanner.homeplanner_api.web.controller;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.koppe.homeplanner.homeplanner_api.jpa.entitiy.Session;
import org.koppe.homeplanner.homeplanner_api.jpa.entitiy.User;
import org.koppe.homeplanner.homeplanner_api.jpa.service.SessionService;
import org.koppe.homeplanner.homeplanner_api.jpa.service.UserService;
import org.koppe.homeplanner.homeplanner_api.web.dto.SessionDto;
import org.koppe.homeplanner.homeplanner_api.web.dto.UserDto;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.reactive.server.WebTestClient;

import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("null")
public class SessionControllerTest {
    private final SessionService srv = mock(SessionService.class);
    private final UserService userSrv = mock(UserService.class);
    private WebTestClient client;
    private final User u1 = new User(1L, "Test1", new BCryptPasswordEncoder(12).encode("test1"));
    private final User u2 = new User(2L, "Test2", new BCryptPasswordEncoder(12).encode("test2"));
    private final UserDto u1Dto = new UserDto(1L, "Test1", "test1");
    private final UserDto u2Dto = new UserDto(2L, "Test2", "test2");

    private final LocalDateTime exp = LocalDateTime.now();

    @BeforeEach
    public void beforeEach() {
        client = WebTestClient.bindToController(new SessionController(srv, userSrv)).build();
        when(userSrv.passwordMatches(u1Dto)).thenReturn(true);
        when(userSrv.passwordMatches(u2Dto)).thenReturn(false);
    }

    @Test
    public void testLogin() {
        when(srv.createSession(u1.getName())).thenReturn(new Session("1", u1, exp));

        SessionDto dto = new SessionDto();
        dto.setExpiration(exp);
        dto.setGuid("1");
        dto.setUserName(u1.getName());

        client.post().uri("/session/login")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(u1Dto), UserDto.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(SessionDto.class).isEqualTo(dto);

        client.post().uri("/session/login")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(u2Dto), UserDto.class)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody(ProblemDetail.class);
    }

    @Test
    public void testRefresh() {
        Session session = new Session("1", u1, exp);
        Session invalSession = new Session("3", u2, exp);
        when(srv.findById("1")).thenReturn(Optional.of(session));
        when(srv.findById("2")).thenReturn(Optional.empty());
        when(srv.findById("3")).thenReturn(Optional.of(invalSession));
        when(srv.refreshSession(session)).thenReturn(session);

        // Valid session test
        SessionDto s = new SessionDto("1", u1.getName(), exp);
        client.post().uri("/session/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(s), SessionDto.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(SessionDto.class)
                .isEqualTo(s);

        // Non existing session test
        s = new SessionDto("2", "", exp);
        client.post().uri("/session/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(s), SessionDto.class)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ProblemDetail.class);

        // Invalid session test
        s = new SessionDto("3", u1.getName(), exp);
        client.post().uri("/session/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(s), SessionDto.class)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ProblemDetail.class);
    }
}
