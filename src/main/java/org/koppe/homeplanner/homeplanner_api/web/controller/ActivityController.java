package org.koppe.homeplanner.homeplanner_api.web.controller;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.koppe.homeplanner.homeplanner_api.jpa.entitiy.Activity;
import org.koppe.homeplanner.homeplanner_api.jpa.service.ActivityService;
import org.koppe.homeplanner.homeplanner_api.utility.DtoFactory;
import org.koppe.homeplanner.homeplanner_api.web.dto.ActivityDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

    // #region Get all activities
    @Operation(summary = "Get all activities", description = "Returns all activities. If from and to are specified, only activities in that time period will be returned", parameters = {
            @Parameter(name = "from", required = false, description = "Oldest activity to be returned"),
            @Parameter(name = "to", required = false, description = "Youngest activity to be returned"),
            @Parameter(name = "top", required = false, description = "Only the given number of elements will be returned"),
            @Parameter(name = "props", required = false, description = "If true, all properties of the activities will be returned as well")
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully queried the activities")
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<ActivityDto>>> getAllActivities(
            @RequestParam(name = "from", required = false) Optional<LocalDateTime> from,
            @RequestParam(name = "to", required = false) Optional<LocalDateTime> to,
            @RequestParam(name = "top", required = false) Optional<Long> top,
            @RequestParam(name = "props", required = false) Optional<Boolean> props) {
        return Mono.fromCallable(() -> {

            return ResponseEntity.ok(null);
        });
    }

    // #region Create activity
    /**
     * Used to create a single activity
     * 
     * @param activity Activity to create
     * @return Created activity
     */
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

    // #region Get activity
    @Operation(summary = "Get single activity", description = "Returns a single activity with the given id", parameters = {
            @Parameter(name = "props", required = false, description = "If set to true, all activity properties will be returned as well. If false, only the activity with an empty properties block will be returned.") })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully queried the activity", content = @Content(schema = @Schema(implementation = ActivityDto.class))),
            @ApiResponse(responseCode = "404", description = "No activity with given id was found", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/{id}")
    public Mono<ResponseEntity<ActivityDto>> getActivity(@PathVariable(name = "id", required = true) Long id,
            @RequestParam(name = "props", required = false) Optional<Boolean> props) {
        return Mono.fromCallable(() -> {
            Activity a = activities.findById(id, props.orElse(false));
            if (a == null) {
                logger.info("No activity with id {} exists", id);
                return ResponseEntity.of(ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(404),
                        "No activity with given id exists")).build();
            }

            ActivityDto dto = DtoFactory.createSingleActivityDtoFromJpa(a, props.orElse(false), a.getType().getId());

            return ResponseEntity.ok(dto);
        });
    }

    // #region Delete Activity
    @Operation(summary = "Delete single activity", description = "Deletes the activity with the given id", parameters = {
            @Parameter(name = "id", required = true, description = "Id of the activity that is to be deleted")
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Succesfully deleted the activity", content = @Content(schema = @Schema(implementation = ActivityDto.class))),
            @ApiResponse(responseCode = "404", description = "Could not find an activity with the given id", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @DeleteMapping("/{id")
    public Mono<ResponseEntity<ActivityDto>> deleteActivity(@PathVariable(name = "id", required = true) Long id) {
        return Mono.fromCallable(() -> {
            if (!activities.activityExistsById(id)) {
                logger.info("No activity with id {} exists", id);
                return ResponseEntity.of(ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(404),
                        "No activity with given id found")).build();
            }

            Activity act = activities.deleteById(id);
            return ResponseEntity.ok(DtoFactory.createSingleActivityDtoFromJpa(act, true, act.getType().getId()));
        });
    }
}
