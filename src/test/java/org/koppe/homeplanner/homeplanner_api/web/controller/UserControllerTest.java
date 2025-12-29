package org.koppe.homeplanner.homeplanner_api.web.controller;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.koppe.homeplanner.homeplanner_api.jpa.entitiy.User;
import org.koppe.homeplanner.homeplanner_api.jpa.service.UserService;
import org.koppe.homeplanner.homeplanner_api.web.dto.UserDto;
import org.koppe.homeplanner.homeplanner_api.web.dto.UserResponseDto;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.test.web.reactive.server.WebTestClient;

import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    private WebTestClient client;

    @Mock
    private UserService srv = mock(UserService.class);

    @BeforeEach
    public void setup() {
        client = WebTestClient.bindToController(new UserController(srv)).build();
    }

    @Test
    public void getSingleUser() {
        User u = new User(1L, "Test", "");
        when(srv.findUserByid(Long.valueOf(1))).thenReturn(Optional.of(u));

        UserResponseDto dto = new UserResponseDto("Test", 1L);
        client.get().uri("/users/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserResponseDto.class).isEqualTo(dto);

        client.get().uri("/users/2")
                .exchange()
                .expectStatus()
                .isNotFound()
                .expectBody(ProblemDetail.class);
    }

    @SuppressWarnings("null")
    @Test
    public void testAddUser() {
        User u1 = new User(1L, "Test", "");
        User u2 = new User(2L, "Test2", "");

        when(srv.findByName("Test")).thenReturn(List.of(u1));
        when(srv.findByName("Test2")).thenReturn(new ArrayList<>());
        when(srv.createUser("Test2", "")).thenReturn(u2);

        UserResponseDto dto = new UserResponseDto("Test2", 2L);
        UserDto toPost = new UserDto();
        toPost.setName("Test2");
        toPost.setPassword("");

        client.post().uri("/users").contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(toPost), UserDto.class)
                .exchange()
                .expectStatus().isOk().expectBody(UserResponseDto.class).isEqualTo(dto);

        client.post().uri("/users").contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(new UserDto(null, "Test", "")), UserDto.class)
                .exchange()
                .expectStatus().isBadRequest().expectBody(ProblemDetail.class);
    }

    @Test
    public void testDeleteUser() {
        User u1 = new User(1L, "Test", "");
        when(srv.userExists(1L)).thenReturn(true);
        when(srv.userExists(2L)).thenReturn(false);

        when(srv.deleteUser(1L)).thenReturn(u1);

        UserResponseDto dto = new UserResponseDto("Test", 1L);

        client.delete().uri("/users/1").exchange().expectStatus().isOk().expectBody(UserResponseDto.class)
                .isEqualTo(dto);

        client.delete().uri("/users/2").exchange().expectStatus().isNotFound().expectBody(ProblemDetail.class);
    }
}
