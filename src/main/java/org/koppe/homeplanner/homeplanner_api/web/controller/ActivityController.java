package org.koppe.homeplanner.homeplanner_api.web.controller;

import org.koppe.homeplanner.homeplanner_api.jpa.entitiy.ActivityType;
import org.koppe.homeplanner.homeplanner_api.jpa.service.ActivityService;
import org.koppe.homeplanner.homeplanner_api.web.dto.ActivityTypeDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/activity")
@Tag(name = "Activity management", description = "Provides functionality for working with activities")
@AllArgsConstructor
public class ActivityController {
    /**
     * Logger
     */
    private final Logger logger = LoggerFactory.getLogger(ActivityController.class);
    /**
     * DB Service for working with activitiy tables
     */
    private final ActivityService activities;

    @Operation(summary = "Activity type creation", description = "Creates a new activity type with the given parameters")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Created activity successfully", content = @Content(schema = @Schema(implementation = ActivityTypeDto.class))),
            @ApiResponse(responseCode = "400", description = "Parameters in body are missing", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping(path = "/type", produces = "application/json")
    public Mono<ResponseEntity<ActivityTypeDto>> createActivityType(@RequestBody Mono<ActivityTypeDto> activityType) {
        return activityType.flatMap(t -> {
            return Mono.fromCallable(() -> {
                if (t.getName() == null || t.getName().isBlank()) {
                    logger.info("No name for new activity type given");
                    return ResponseEntity.of(ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(400),
                            "No name given for new activity type")).build();
                }

                if (activities.activityTypeExistsByName(t.getName())) {
                    logger.info("Activity with name {} already exists", t.getName());
                    return ResponseEntity.of(ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(400),
                            "Activity type with given name already exists")).build();
                }
                ActivityType type = new ActivityType();
                type.setName(t.getName());

                ActivityType created = activities.createActivityType(type);
                ActivityTypeDto dto = new ActivityTypeDto();
                dto.setId(created.getId());
                dto.setName(created.getName());
                return ResponseEntity.ok(dto);
            });
        });
    }
}
