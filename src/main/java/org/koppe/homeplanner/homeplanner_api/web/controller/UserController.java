package org.koppe.homeplanner.homeplanner_api.web.controller;

import java.util.NoSuchElementException;
import java.util.Optional;

import org.koppe.homeplanner.homeplanner_api.jpa.entitiy.User;
import org.koppe.homeplanner.homeplanner_api.jpa.service.UserService;
import org.koppe.homeplanner.homeplanner_api.web.dto.UserDto;
import org.koppe.homeplanner.homeplanner_api.web.dto.UserResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

/**
 * Controller for user management
 */
@AllArgsConstructor
@RestController
@RequestMapping("/users")
@Tag(name = "User Management", description = "Provides functionality for managing users within the application")
public class UserController {
    /**
     * Database service for interacting with users
     */
    private final UserService userService;

    /**
     * Logger
     */
    private final Logger logger = LoggerFactory.getLogger(UserController.class);

    /**
     * API operation for retrieving users
     * 
     * @param id ID of the user
     * @return User object
     */
    @Operation(summary = "Get user", description = "Returns information for user with given id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found", content = @Content(schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping(path = "/{id}", produces = "application/json")
    public Mono<ResponseEntity<Optional<UserResponseDto>>> getUser(@PathVariable Long id) {
        return Mono.fromCallable(() -> {
            Optional<User> u = userService.findUserByid(id);
            if (u.isPresent()) {
                return ResponseEntity.ok(Optional.of(new UserResponseDto(u.get().getName(), u.get().getId())));
            }
            return ResponseEntity.of(ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(404), "User not found"))
                    .build();
        });
    }

    /**
     * API operation for creating a new user
     * 
     * @param user User to create
     * @return Created user
     */
    @Operation(summary = "Create new user", description = "Creates user with given information within the application")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User successfully created", content = @Content(schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "User already exists in the system", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping(produces = "application/json")
    public Mono<ResponseEntity<UserResponseDto>> addUser(@RequestBody Mono<UserDto> user) {
        return user.flatMap((u) -> {
            return Mono.fromCallable(() -> {

                if (userService.findByName(u.getName()).size() > 0) {
                    return ResponseEntity.of(
                            ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(400), "Username already exists"))
                            .build();
                }
                User newUser = userService.createUser(u.getName(), u.getPassword());
                UserResponseDto dto = new UserResponseDto(newUser.getName(), newUser.getId());
                return ResponseEntity.ok(dto);
            });
        });
    }

    /**
     * API operation for deleting a user
     * 
     * @param id ID of the user to be deleted
     * @return Deleted user
     */
    @Operation(summary = "Delete a user", description = "Deletes the user with given id irreversibly")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User deleted successfully", content = @Content(schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "User with given id not found", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @DeleteMapping(path = "/{id}", produces = "applicaion/json")
    public Mono<ResponseEntity<UserResponseDto>> deleteUser(@PathVariable Long id) {
        return Mono.fromCallable(() -> {
            if (!userService.userExists(id)) {
                logger.info("User with id {} does not exist", id);
                return ResponseEntity.of(ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(404),
                        "User with given id does not exist")).build();
            }

            User u = null;
            try {
                u = userService.deleteUser(id);
            } catch (IllegalArgumentException ex) {
                logger.info("User with id {} does not exist", id);
                return ResponseEntity.of(ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(404),
                        "User with given id does not exist")).build();
            } catch (NoSuchElementException ex) {
                logger.info("User with id {} does not exist", id);
                return ResponseEntity.of(ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(404),
                        "User with given id does not exist")).build();
            }

            UserResponseDto dto = new UserResponseDto(u.getName(), u.getId());
            return ResponseEntity.ok(dto);
        });
    }
}
