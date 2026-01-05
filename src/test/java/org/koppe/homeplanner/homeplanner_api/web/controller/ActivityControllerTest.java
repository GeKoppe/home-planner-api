package org.koppe.homeplanner.homeplanner_api.web.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
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
@SuppressWarnings({ "null" })
public class ActivityControllerTest {
	private final ActivityService srv = mock(ActivityService.class);
	private WebTestClient client;

	private final LocalDateTime start = LocalDateTime.now();
	private final LocalDateTime end = LocalDateTime.now().plusMinutes(10L);

	private Activity created;
	private ActivityDto dto;

	@BeforeEach
	public void setup() {
		client = WebTestClient.bindToController(new ActivityController(srv)).build();

		created = new Activity(1L,
				new ActivityType(1L, "Test", new HashSet<>(), false, new HashSet<>()),
				start, end, new HashSet<>(), "");
		dto = new ActivityDto(1L, 1L, start, end, new HashSet<>(), "");
	}

	@Test
	public void testActivityCreation() {
		when(srv.createActivity(any(Activity.class), eq(1L))).thenReturn(created);

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

		client.post().uri("/activities").contentType(MediaType.APPLICATION_JSON)
				.body(Mono.just(new ActivityDto(null, 1L, null, null, new HashSet<>(), "")),
						ActivityDto.class)
				.exchange()
				.expectStatus().isOk()
				.expectBody(ActivityDto.class);
	}

	@Test
	public void testGetActivity() {
		when(srv.findById(1L, true)).thenReturn(created);
		when(srv.findById(1L, false)).thenReturn(created);
		when(srv.findById(2L, false)).thenReturn(null);

		client.get().uri("/activities/1?props=false").accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk()
				.expectBody(ActivityDto.class).isEqualTo(dto);

		client.get().uri("/activities/2?props=false").accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isNotFound()
				.expectBody(ProblemDetail.class);
	}

	@Test
	public void testDeleteActivity() {
		when(srv.activityExistsById(1L)).thenReturn(true);
		when(srv.activityExistsById(2L)).thenReturn(false);
		when(srv.deleteById(1L)).thenReturn(created);

		client.delete().uri("/activities/1").accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk()
				.expectBody(ActivityDto.class).isEqualTo(dto);

		client.delete().uri("/activities/2").accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isNotFound()
				.expectBody(ProblemDetail.class);
	}

	@Test
	public void testUpdateActivity() {
		when(srv.updateActivity(any(ActivityDto.class))).thenReturn(created);
		when(srv.activityExistsById(1L)).thenReturn(true);
		when(srv.activityExistsById(5L)).thenReturn(false);

		client.put().uri("/activities/5").contentType(MediaType.APPLICATION_JSON)
				.body(Mono.just(dto), ActivityDto.class)
				.exchange()
				.expectStatus().isNotFound()
				.expectBody(ProblemDetail.class);

		dto.setId(5L);
		client.put().uri("/activities/1").contentType(MediaType.APPLICATION_JSON)
				.body(Mono.just(dto), ActivityDto.class)
				.exchange()
				.expectStatus().isBadRequest()
				.expectBody(ProblemDetail.class);

		dto.setId(1L);
		client.put().uri("/activities/1").contentType(MediaType.APPLICATION_JSON)
				.body(Mono.just(dto), ActivityDto.class)
				.exchange()
				.expectStatus().isOk()
				.expectBody(ActivityDto.class);
	}

}
