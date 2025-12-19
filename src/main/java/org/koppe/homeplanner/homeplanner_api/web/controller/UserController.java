package org.koppe.homeplanner.homeplanner_api.web.controller;

import java.util.Optional;

import org.koppe.homeplanner.homeplanner_api.jpa.entitiy.User;
import org.koppe.homeplanner.homeplanner_api.jpa.service.UserService;
import org.koppe.homeplanner.homeplanner_api.web.dto.UserDto;
import org.koppe.homeplanner.homeplanner_api.web.dto.UserResponseDto;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService dbService;

    @GetMapping(path = "/{id}", produces = "application/json")
    public Mono<ResponseEntity<Optional<UserResponseDto>>> getUser(@PathVariable Long id) {
        return Mono.fromCallable(() -> {
            Optional<User> u = dbService.findUserByid(id);
            if (u.isPresent()) {
                return ResponseEntity.ok(Optional.of(new UserResponseDto(u.get().getName(), u.get().getId())));
            }
            return ResponseEntity.of(Optional.empty());
        });
    }

    @PostMapping(produces = "application/json")
    public Mono<ResponseEntity<UserResponseDto>> addUser(@RequestBody Mono<UserDto> user) {
        return user.flatMap((u) -> {
            return Mono.fromCallable(() -> {

                if (dbService.findByName(u.getName()).size() > 0) {
                    return ResponseEntity.of(
                            ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(400), "Username already exists"))
                            .build();
                }
                User newUser = dbService.createUser(u.getName(), u.getPassword());
                UserResponseDto dto = new UserResponseDto(newUser.getName(), newUser.getId());
                return ResponseEntity.ok(dto);
            });
        });
    }

}
