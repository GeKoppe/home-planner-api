package org.koppe.homeplanner.homeplanner_api.web.controller;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.koppe.homeplanner.homeplanner_api.jpa.entitiy.ActivityPropertyType;
import org.koppe.homeplanner.homeplanner_api.jpa.entitiy.ActivityType;
import org.koppe.homeplanner.homeplanner_api.jpa.service.ActivityService;
import org.koppe.homeplanner.homeplanner_api.web.dto.ActivityPropertyTypeDto;
import org.koppe.homeplanner.homeplanner_api.web.dto.ActivityTypeDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
@RequestMapping("/activity")
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
                type.setTimeable(t.getTimable() != null ? t.getTimable() : false);

                ActivityType created = activities.createActivityType(type);
                ActivityTypeDto dto = new ActivityTypeDto();
                dto.setId(created.getId());
                dto.setName(created.getName());
                return ResponseEntity.ok(dto);
            });
        });
    }

    @Operation(summary = "Gets activity type with given id", description = "Returns the activity associated with given id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved activity type", content = @Content(schema = @Schema(implementation = ActivityTypeDto.class))),
            @ApiResponse(responseCode = "400", description = "No activity id provided", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Activity with provided id does not exist", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping(path = "/type/{id}", produces = "application/json")
    public Mono<ResponseEntity<ActivityTypeDto>> getActivityTypeById(@PathVariable Long id) {
        return Mono.fromCallable(() -> {
            if (id == null) {
                return ResponseEntity
                        .of(ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(400), "No activity id given"))
                        .build();
            }

            Optional<ActivityType> actOp = activities.findActivityTypeById(id);
            if (actOp.isEmpty()) {
                return ResponseEntity.of(ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(404),
                        "Activity type with given id does not exist")).build();
            }

            ActivityType type = actOp.get();
            ActivityTypeDto dto = new ActivityTypeDto();
            dto.setId(type.getId());
            dto.setName(type.getName());
            dto.setTimable(type.getTimeable());
            Set<ActivityPropertyTypeDto> props = new HashSet<>();

            type.getProperties().forEach(
                    p -> props.add(new ActivityPropertyTypeDto(p.getId(), p.getName(), p.getType(), type.getId())));

            dto.setProperties(props);
            return ResponseEntity.ok(dto);
        });
    }

    @Operation(summary = "Creates new activity property", description = """
            If an activity with the given id exists, the property in the body is created and linked with the activity
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully created the property", content = @Content(schema = @Schema(implementation = ActivityPropertyTypeDto.class))),
            @ApiResponse(responseCode = "400", description = "Returned, if no valid property definition is given", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Returned, if no activity type with given id exists", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping(path = "/type/{activityTypeId}/property")
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
                Optional<ActivityType> actOp = activities.findActivityTypeById(activityTypeId);
                if (actOp.isEmpty()) {
                    logger.info("No activity type with id {} found", activityTypeId);
                    return ResponseEntity.of(ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(404),
                            "No activity with given id exists")).build();
                }

                ActivityType act = actOp.get();
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
