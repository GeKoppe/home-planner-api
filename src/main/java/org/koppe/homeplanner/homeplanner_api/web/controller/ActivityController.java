package org.koppe.homeplanner.homeplanner_api.web.controller;

import java.time.LocalDateTime;
import java.util.HashSet;

import org.koppe.homeplanner.homeplanner_api.jpa.entitiy.Activity;
import org.koppe.homeplanner.homeplanner_api.jpa.service.ActivityService;
import org.koppe.homeplanner.homeplanner_api.web.dto.ActivityDto;
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
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(path = "/activities")
@Tag(name = "Activity management", description = "Provides functionality to work with activities")
@RequiredArgsConstructor
public class ActivityController {
    /**
     * Logger
     */
    private final Logger logger = LoggerFactory.getLogger(ActivityConfigController.class);
    /**
     * DB Service for working with activitiy tables
     */
    private final ActivityService activities;

    @Operation(summary = "Creates an activity", description = "Creates a new activity with the given type")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully created the activity", content = @Content(schema = @Schema(implementation = ActivityDto.class))),
            @ApiResponse(responseCode = "400", description = "Activity type id is missing", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "No activity type with provided id exists", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping(produces = "application/json")
    public Mono<ResponseEntity<ActivityDto>> addActivity(@RequestBody Mono<ActivityDto> activity) {
        // activity = activity.timeout(Duration.ofSeconds(30));
        return activity.flatMap(a -> {
            return Mono.fromCallable(() -> {
                if (a.getActivityTypeId() == null) {
                    logger.info("No activity type given");
                    return ResponseEntity
                            .of(ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(400), "No activity type given"))
                            .build();
                }

                Activity act = new Activity();
                act.setStartDate(a.getStartDate() != null ? a.getStartDate() : LocalDateTime.now());
                act.setEndDate(a.getEndDate() != null ? a.getEndDate() : LocalDateTime.now());
                Activity created = null;
                try {
                    created = activities.createActivity(act, a.getActivityTypeId());
                    logger.debug("Created new activity");
                } catch (IllegalArgumentException ex) {
                    logger.info("No valid activity type given");
                    return ResponseEntity
                            .of(ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(400),
                                    "No valid activity type given"))
                            .build();
                }

                ActivityDto dto = new ActivityDto();
                dto.setActivityTypeId(created.getType().getId());
                dto.setStartDate(created.getStartDate());
                dto.setEndDate(created.getEndDate());
                dto.setId(created.getId());
                dto.setProperties(new HashSet<>());

                return ResponseEntity.ok(dto);
            });
        });
    }
}
