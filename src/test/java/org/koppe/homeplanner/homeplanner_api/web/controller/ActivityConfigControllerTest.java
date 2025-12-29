package org.koppe.homeplanner.homeplanner_api.web.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.koppe.homeplanner.homeplanner_api.jpa.entitiy.ActivityPropertyType;
import org.koppe.homeplanner.homeplanner_api.jpa.entitiy.ActivityType;
import org.koppe.homeplanner.homeplanner_api.jpa.entitiy.PropertyTypeC;
import org.koppe.homeplanner.homeplanner_api.jpa.service.ActivityConfigService;
import org.koppe.homeplanner.homeplanner_api.utility.DtoFactory;
import org.koppe.homeplanner.homeplanner_api.web.dto.ActivityPropertyTypeDto;
import org.koppe.homeplanner.homeplanner_api.web.dto.ActivityTypeDto;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.test.web.reactive.server.WebTestClient;

import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("null")
public class ActivityConfigControllerTest {
    private final ActivityConfigService srv = mock(ActivityConfigService.class);
    private WebTestClient client;

    private ActivityTypeDto complete;
    private ActivityTypeDto completeWithProps;
    private ActivityTypeDto nameMissing;
    private ActivityTypeDto duplicate;
    private ActivityTypeDto timeableNull;

    private ActivityPropertyTypeDto prop1;

    private ActivityType created;
    private ActivityType createdNoProperties;

    private ActivityPropertyType createdProperty;

    @BeforeEach
    public void setup() {
        client = WebTestClient.bindToController(new ActivityConfigController(srv)).build();

        // Setup property type dtos
        prop1 = new ActivityPropertyTypeDto(1L, "Property1", PropertyTypeC.STRING, 1L);

        // Setup activity type dtos
        complete = new ActivityTypeDto(1L, "Test", new HashSet<>(), true);
        completeWithProps = new ActivityTypeDto(1L, "Test", Set.of(prop1), true);
        nameMissing = new ActivityTypeDto(2L, "", new HashSet<>(), true);
        duplicate = new ActivityTypeDto(3L, "Duplicate", new HashSet<>(), true);
        timeableNull = new ActivityTypeDto(4L, "TimeableNull", new HashSet<>(), null);

        // Setup property types
        createdProperty = new ActivityPropertyType(1L, "Property1", created, PropertyTypeC.STRING, Set.of());

        // Setup activity types
        created = new ActivityType(1L, "Test", Set.of(createdProperty), true, Set.of());
        createdProperty = new ActivityPropertyType(1L, "Property1", created, PropertyTypeC.STRING, Set.of());
        createdNoProperties = new ActivityType(2L, "TestNoProperties", Set.of(), true, Set.of());

        // Setup standard mocks
        when(srv.activityTypeExistsByName("Test")).thenReturn(false);
        when(srv.activityTypeExistsByName("Duplicate")).thenReturn(true);
        when(srv.activityExistsById(1L)).thenReturn(true);
        when(srv.activityExistsById(2L)).thenReturn(true);
        when(srv.activityExistsById(5L)).thenReturn(false);
        when(srv.createActivityType(any(ActivityType.class))).thenReturn(created);
        when(srv.findAllActivityTypes(true)).thenReturn(List.of(created));
        when(srv.findAllActivityTypes(false)).thenReturn(List.of(createdNoProperties));
        when(srv.findActivityTypeById(1L)).thenReturn(created);
        when(srv.findActivityTypeById(2L)).thenReturn(created);
        when(srv.findActivityTypeById(5L)).thenReturn(null);
        when(srv.deleteActivityType(1L)).thenReturn(created);
        when(srv.updateActivityType(any(ActivityType.class))).thenReturn(created);
        when(srv.activityTypePropertyExistsByNameAndActivityTypeId("Prop1", 2L)).thenReturn(true);
        when(srv.createActivityPropertyType(any(ActivityPropertyType.class))).thenReturn(createdProperty);
    }

    @Test
    public void testCreateActivityType() {
        // Test valid
        client.post().uri("/activity-config/types").contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(complete), ActivityTypeDto.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ActivityTypeDto.class).isEqualTo(complete);

        // Test name is null
        client.post().uri("/activity-config/types").contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(nameMissing), ActivityTypeDto.class)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ProblemDetail.class);

        // Test name is blank
        nameMissing.setName(" ");
        client.post().uri("/activity-config/types").contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(nameMissing), ActivityTypeDto.class)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ProblemDetail.class);

        // Test duplicate
        client.post().uri("/activity-config/types").contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(duplicate), ActivityTypeDto.class)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ProblemDetail.class);

        // Test timeable null
        client.post().uri("/activity-config/types").contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(timeableNull), ActivityTypeDto.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ActivityTypeDto.class).isEqualTo(complete);
    }

    @Test
    public void testGetAllActivityTypes() {
        client.get().uri("/activity-config/types?props=true").accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<ActivityTypeDto>>() {
                }).isEqualTo(DtoFactory.createActivityTypeDtosFromJpa(List.of(created), true));

        client.get().uri("/activity-config/types?props=false").accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<ActivityTypeDto>>() {
                })
                .isEqualTo(DtoFactory.createActivityTypeDtosFromJpa(List.of(createdNoProperties), false));
    }

    @Test
    public void testGetActivityTypeById() {
        // Test valid with and without props
        client.get().uri("/activity-config/types/1?props=true").accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ActivityTypeDto.class).isEqualTo(completeWithProps);

        client.get().uri("/activity-config/types/1?props=false").accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ActivityTypeDto.class).isEqualTo(complete);

        // Test id null
        client.get().uri("/activity-config/types/null").accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ProblemDetail.class);

        // Test not found
        client.get().uri("/activity-config/types/5").accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ProblemDetail.class);
    }

    @Test
    public void testDeleteActivityType() {
        // Test id null
        client.delete().uri("/activity-config/types/null").accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ProblemDetail.class);

        // Test valid with and without props
        client.delete().uri("/activity-config/types/1?props=true").accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ActivityTypeDto.class).isEqualTo(completeWithProps);

        client.delete().uri("/activity-config/types/1?props=false").accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ActivityTypeDto.class).isEqualTo(complete);

        // Test not found
        client.delete().uri("/activity-config/types/5").accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ProblemDetail.class);
    }

    @Test
    public void testUpdateActivityType() {
        // Test id null
        client.put().uri("/activity-config/types/null").contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(complete), ActivityTypeDto.class)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ProblemDetail.class);

        // Test activity does not exist
        client.put().uri("/activity-config/types/5").contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(complete), ActivityTypeDto.class)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ProblemDetail.class);

        // Test activity id in path and object don't match
        client.put().uri("/activity-config/types/1").contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(nameMissing), ActivityTypeDto.class)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ProblemDetail.class);

        // Test name missing
        client.put().uri("/activity-config/types/2").contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(nameMissing), ActivityTypeDto.class)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ProblemDetail.class);

        nameMissing.setName(null);
        client.put().uri("/activity-config/types/2").contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(nameMissing), ActivityTypeDto.class)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ProblemDetail.class);

        // Test valid
        client.put().uri("/activity-config/types/1").contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(complete), ActivityTypeDto.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ActivityTypeDto.class).isEqualTo(complete);
    }

    @Test
    public void testCreateActivityPropertyType() {
        // Test activity null
        client.post().uri("/activity-config/types/null/properties").contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(prop1), ActivityPropertyTypeDto.class)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ProblemDetail.class);

        // Test activity not found
        client.post().uri("/activity-config/types/5/properties").contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(prop1), ActivityPropertyTypeDto.class)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ProblemDetail.class);

        // Test invalid bodies
        client.post().uri("/activity-config/types/1/properties").contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(mock(ActivityPropertyTypeDto.class)), ActivityPropertyTypeDto.class)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ProblemDetail.class);

        prop1.setName(null);
        client.post().uri("/activity-config/types/1/properties").contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(prop1), ActivityPropertyTypeDto.class)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ProblemDetail.class);

        prop1.setName("   ");
        client.post().uri("/activity-config/types/1/properties").contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(prop1), ActivityPropertyTypeDto.class)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ProblemDetail.class);

        prop1.setName("Prop1");
        prop1.setType(null);
        client.post().uri("/activity-config/types/1/properties").contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(prop1), ActivityPropertyTypeDto.class)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ProblemDetail.class);

        // Test activity property already exists
        client.post().uri("/activity-config/types/2/properties").contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(prop1), ActivityPropertyTypeDto.class)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ProblemDetail.class);

        // Test valid
        prop1.setType(PropertyTypeC.STRING);
        client.post().uri("/activity-config/types/1/properties").contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(prop1), ActivityPropertyTypeDto.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ActivityTypeDto.class);
    }
}
