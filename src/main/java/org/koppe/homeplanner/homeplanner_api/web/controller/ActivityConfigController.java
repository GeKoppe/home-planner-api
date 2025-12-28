package org.koppe.homeplanner.homeplanner_api.web.controller;

import java.util.List;
import java.util.Optional;

import org.koppe.homeplanner.homeplanner_api.jpa.entitiy.ActivityPropertyType;
import org.koppe.homeplanner.homeplanner_api.jpa.entitiy.ActivityType;
import org.koppe.homeplanner.homeplanner_api.jpa.service.ActivityConfigService;
import org.koppe.homeplanner.homeplanner_api.utility.DtoFactory;
import org.koppe.homeplanner.homeplanner_api.web.dto.ActivityPropertyTypeDto;
import org.koppe.homeplanner.homeplanner_api.web.dto.ActivityTypeDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/activity-config")
@Tag(name = "Activity configuration management", description = "Provides functionality for configuring activity types")
@AllArgsConstructor
public class ActivityConfigController {
    /**
     * Logger
     */
    private final Logger logger = LoggerFactory.getLogger(ActivityConfigController.class);
    /**
     * DB Service for working with activitiy tables
     */
    private final ActivityConfigService activities;

    // #region Activity Types
    /**
     * Creates new activity type
     * 
     * @param activityType Activity type to create
     * @return Created activity type
     */
    @Operation(summary = "Create activity types", description = "Creates a new activity type with the given parameters")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Created activity successfully", content = @Content(schema = @Schema(implementation = ActivityTypeDto.class))),
            @ApiResponse(responseCode = "400", description = "Parameters in body are missing", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping(path = "/types", produces = "application/json")
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
                type.setTimeable(t.getTimable() != null ? t.getTimable() : false);

                ActivityType created = activities.createActivityType(type);
                ActivityTypeDto dto = DtoFactory.createSingleActivityTypeDtoFromJpa(created, false);
                return ResponseEntity.ok(dto);
            });
        });
    }

    @Operation(summary = "Retrieve activity types", description = "Retrieves all existing activity types.", parameters = {
            @Parameter(name = "props", required = false, description = "If set to true, all properties for the activity types will be returned as well") })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All activity types returned successfully", content = @Content(schema = @Schema(implementation = List.class)))
    })
    @GetMapping(path = "/types", produces = "application/json")
    public Mono<ResponseEntity<List<ActivityTypeDto>>> getAllActivityTypes(
            @RequestParam(name = "props") Optional<Boolean> props) {
        return Mono.fromCallable(() -> {
            List<ActivityType> types = activities.findAllActivityTypes(props.orElse(false));
            List<ActivityTypeDto> dtos = DtoFactory.createActivityTypeDtosFromJpa(types, props.orElse(false));
            return ResponseEntity.ok(dtos);
        });
    }

    /**
     * Returns the activitiy with given id
     * 
     * @param id Id of the activity to return
     * @return Activity with given id
     */
    @Operation(summary = "Gets activity type with given id", description = "Returns the activity associated with given id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved activity type", content = @Content(schema = @Schema(implementation = ActivityTypeDto.class))),
            @ApiResponse(responseCode = "400", description = "No activity id provided", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Activity with provided id does not exist", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping(path = "/types/{id}", produces = "application/json")
    public Mono<ResponseEntity<ActivityTypeDto>> getActivityTypeById(@PathVariable Long id,
            @RequestParam(required = false, name = "props") Optional<Boolean> props) {
        return Mono.fromCallable(() -> {
            if (id == null) {
                return ResponseEntity
                        .of(ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(400), "No activity id given"))
                        .build();
            }

            ActivityType type = activities.findActivityTypeById(id);
            if (type == null) {
                return ResponseEntity.of(ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(404),
                        "Activity type with given id does not exist")).build();
            }

            ActivityTypeDto dto = DtoFactory.createSingleActivityTypeDtoFromJpa(type, props.orElse(false));
            return ResponseEntity.ok(dto);
        });
    }

    @Operation(summary = "Delete activity types", description = "Deletes activity with provided id and all it's properties")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted activity type", content = @Content(schema = @Schema(implementation = ActivityTypeDto.class))),
            @ApiResponse(responseCode = "404", description = "Activity type with provided id not found", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @DeleteMapping(path = "/types/{id}", produces = "application/json")
    public Mono<ResponseEntity<ActivityTypeDto>> deleteActivityType(@PathVariable Long id,
            @RequestParam(required = false, name = "props") Optional<Boolean> props) {
        return Mono.fromCallable(() -> {
            if (id == null) {
                return ResponseEntity
                        .of(ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(400), "No activity id given"))
                        .build();
            }

            ActivityType type = activities.findActivityTypeById(id);
            if (type == null) {
                return ResponseEntity.of(ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(404),
                        "Activity type with given id does not exist")).build();
            }

            ActivityType deleted = activities.deleteActivityType(id);
            if (deleted == null) {
                return ResponseEntity.of(
                        ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(404), "No activity found to delete"))
                        .build();
            }

            ActivityTypeDto dto = DtoFactory.createSingleActivityTypeDtoFromJpa(deleted, props.orElse(false));
            return ResponseEntity.ok(dto);
        });
    }

    @Operation(summary = "Updates an activity", description = "Updates the activity with given id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated activity type", content = @Content(schema = @Schema(implementation = ActivityTypeDto.class))),
            @ApiResponse(responseCode = "400", description = "Id or name not correctly given", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Activity type with given id does not exist", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PutMapping(path = "/types/{id}")
    public Mono<ResponseEntity<ActivityTypeDto>> updateActivityType(@RequestBody Mono<ActivityTypeDto> activity,
            @PathVariable(name = "id") Long activityId) {

        return activity.flatMap(a -> {
            return Mono.fromCallable(() -> {
                if (!activities.activityExistsById(activityId)) {
                    logger.warn("No activity with id {} exists", activityId);
                    return ResponseEntity.of(ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(404),
                            "No activity with given id exists")).build();
                }

                if (a.getId() != null && !a.getId().equals(activityId)) {
                    logger.warn("Trying to update activity with id {} on resource with id {}", a.getId(), activityId);
                    return ResponseEntity.of(ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(400),
                            "Resource id and id in body don't match")).build();
                }

                if (a.getName() == null || a.getName().isBlank()) {
                    logger.warn("Cannot update an activity with blank name");
                    return ResponseEntity.of(ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(400),
                            "Cannot update activity with blank name")).build();
                }

                ActivityType type = new ActivityType();
                type.setId(activityId);
                type.setName(a.getName());
                type.setTimeable(a.getTimable() != null ? a.getTimable() : false);

                ActivityType t = activities.updateActivityType(type);
                ActivityTypeDto dto = DtoFactory.createSingleActivityTypeDtoFromJpa(t, false);

                return ResponseEntity.ok(dto);
            });
        });
    }

    // #region Activity Type Properties
    @Operation(summary = "Creates new activity property", description = """
            If an activity with the given id exists, the property in the body is created and linked with the activity
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully created the property", content = @Content(schema = @Schema(implementation = ActivityPropertyTypeDto.class))),
            @ApiResponse(responseCode = "400", description = "Returned, if no valid property definition is given", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Returned, if no activity type with given id exists", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping(path = "/types/{activityTypeId}/properties")
    public Mono<ResponseEntity<ActivityPropertyTypeDto>> createActivityTypeProperty(
            @RequestBody Mono<ActivityPropertyTypeDto> propertyType, @PathVariable Long activityTypeId) {
        return propertyType.flatMap(t -> {
            return Mono.fromCallable(() -> {
                // Check if all necessary properties are given
                if (t == null || t.getName() == null || t.getName().isBlank() || t.getType() == null) {
                    logger.info("No activity property to create given");
                    return ResponseEntity
                            .of(ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(400), "No property name given"))
                            .build();
                }

                // Check if the activity property already exists
                if (activities.activityTypePropertyExistsByNameAndActivityTypeId(t.getName(), activityTypeId)) {
                    logger.info("Property with given name for given activity already exists");
                    return ResponseEntity
                            .of(ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(400),
                                    "Property with given name for given activity already exists"))
                            .build();
                }

                // Check if the activity id exists
                ActivityType act = activities.findActivityTypeById(activityTypeId);
                if (act == null) {
                    logger.info("No activity type with id {} found", activityTypeId);
                    return ResponseEntity.of(ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(404),
                            "No activity with given id exists")).build();
                }
                logger.debug("Creating new property for activity {}", act);

                // Create the property to save in the database
                ActivityPropertyType type = new ActivityPropertyType();
                type.setActivity(act);
                type.setName(t.getName());
                type.setType(t.getType());
                ActivityPropertyType created = activities.createActivityPropertyType(type);
                logger.debug("Created new property type {} for activity {}", created, act);

                ActivityPropertyTypeDto dto = new ActivityPropertyTypeDto(created.getId(), created.getName(),
                        created.getType(), created.getActivity().getId());

                return ResponseEntity.ok(dto);
            });
        });
    }
}
