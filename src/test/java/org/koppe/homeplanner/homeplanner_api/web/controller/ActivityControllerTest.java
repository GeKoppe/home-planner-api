package org.koppe.homeplanner.homeplanner_api.web.controller;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.HashSet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.koppe.homeplanner.homeplanner_api.jpa.entitiy.Activity;
import org.koppe.homeplanner.homeplanner_api.jpa.entitiy.ActivityType;
import org.koppe.homeplanner.homeplanner_api.jpa.service.ActivityService;
import org.koppe.homeplanner.homeplanner_api.web.dto.ActivityDto;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.test.web.reactive.server.WebTestClient;

import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
public class ActivityControllerTest {
    private final ActivityService srv = mock(ActivityService.class);
    private WebTestClient client;

    private final LocalDateTime start = LocalDateTime.now();
    private final LocalDateTime end = LocalDateTime.now().plusMinutes(10L);

    @BeforeEach
    public void setup() {
        client = WebTestClient.bindToController(new ActivityController(srv)).build();
    }

    @SuppressWarnings("null")
    @Test
    public void testActivityCreation() {
        Activity a = new Activity();
        a.setStartDate(start);
        a.setEndDate(end);

        Activity created = new Activity(1L, new ActivityType(1L, "Test", new HashSet<>(), false, new HashSet<>()),
                start, end, new HashSet<>());
        ActivityDto dto = new ActivityDto(1L, 1L, start, end, new HashSet<>());

        when(srv.createActivity(a, dto.getActivityTypeId())).thenReturn(created);

        client.post().uri("/activities").contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(dto), ActivityDto.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ActivityDto.class).isEqualTo(dto);

        dto.setActivityTypeId(null);
        client.post().uri("/activities").contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(dto), ActivityDto.class)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ProblemDetail.class);
    }
}
