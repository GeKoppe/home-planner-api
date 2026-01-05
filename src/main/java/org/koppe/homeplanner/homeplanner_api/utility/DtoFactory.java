package org.koppe.homeplanner.homeplanner_api.utility;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.koppe.homeplanner.homeplanner_api.jpa.entitiy.Activity;
import org.koppe.homeplanner.homeplanner_api.jpa.entitiy.ActivityProperty;
import org.koppe.homeplanner.homeplanner_api.jpa.entitiy.ActivityPropertyType;
import org.koppe.homeplanner.homeplanner_api.jpa.entitiy.ActivityType;
import org.koppe.homeplanner.homeplanner_api.jpa.entitiy.User;
import org.koppe.homeplanner.homeplanner_api.web.dto.ActivityDto;
import org.koppe.homeplanner.homeplanner_api.web.dto.ActivityPropertyDto;
import org.koppe.homeplanner.homeplanner_api.web.dto.ActivityPropertyTypeDto;
import org.koppe.homeplanner.homeplanner_api.web.dto.ActivityTypeDto;
import org.koppe.homeplanner.homeplanner_api.web.dto.UserResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.persistence.FetchType;

/**
 * Abstract utility class for creating dtos, mostly from JPA entitiies.
 */
public abstract class DtoFactory {
    /**
     * Logger
     */
    private final static Logger logger = LoggerFactory.getLogger(DtoFactory.class);

    public static final ActivityTypeDto createSingleActivityTypeDtoFromJpa(ActivityType a, boolean withProperties) {
        return createActivityTypeDtosFromJpa(List.of(a), withProperties).get(0);
    }

    /**
     * Converts all given AcitivityType JPA entities to dtos
     * 
     * @param a              List of all JPA entities to be converted
     * @param withProperties If true, properties will be converted as well by
     *                       calling
     *                       {@link DtoFactory#createActivityPropertyTypeDtosFromJpa(Set, Long)}.
     *                       JPA property types MUST be preloaded by the jpa, as the
     *                       properties are set to {@link FetchType#LAZY}. If set to
     *                       false, properties will be set to an empty HashSet
     * @return List of all converted dtos
     */
    public static final List<ActivityTypeDto> createActivityTypeDtosFromJpa(List<ActivityType> a,
            boolean withProperties) {
        logger.debug("Creating dtos from {} ActivityType JPA entities", a.size());
        List<ActivityTypeDto> dtos = new ArrayList<>();

        a.forEach(t -> {
            ActivityTypeDto dto = new ActivityTypeDto();
            dto.setId(t.getId());
            dto.setName(t.getName());
            dto.setTimable(t.getTimeable());

            if (withProperties) {
                dto.setProperties(createActivityPropertyTypeDtosFromJpa(t.getProperties(), t.getId()));
            } else
                dto.setProperties(new HashSet<>());

            dtos.add(dto);
        });

        return dtos;
    }

    /**
     * Converts all given ActivityPropertyType objects into ActivityPropertyTypeDto
     * objects and returns them as a HashSet
     * 
     * @param p JPA entities to convert
     * @return Set of all converted jpa entities
     * @throws IllegalArgumentException When p or activityTypeId are null
     */
    public static final Set<ActivityPropertyTypeDto> createActivityPropertyTypeDtosFromJpa(
            Set<ActivityPropertyType> p, Long activityTypeId) throws IllegalArgumentException {
        if (p == null || activityTypeId == null) {
            logger.warn("Given arguments are null");
            throw new IllegalArgumentException();
        }
        logger.debug("Creating dtos from {} ActivityPropertyType JPA entities", p.size());

        Set<ActivityPropertyTypeDto> props = new HashSet<>();

        p.forEach(prop -> {
            ActivityPropertyTypeDto propDto = new ActivityPropertyTypeDto();
            propDto.setActivityTypeId(activityTypeId);
            propDto.setId(prop.getId());
            propDto.setName(prop.getName());
            propDto.setType(prop.getType());

            props.add(propDto);
        });

        return props;
    }

    /**
     * Creates activity dtos from a list of activities
     * 
     * @param act            Activities to be converted to dtos
     * @param withProperties If true, properties will be loaded into the dtos. The
     *                       properties in the activities MUST be loaded beforehand
     *                       by the jpa.
     * @param activityTypeId Id of the activity type the activities belong to
     * @return List of all activity dtos
     * @throws IllegalArgumentException If the list of activities is empty
     */
    public static final List<ActivityDto> createActivityDtosFromJpa(List<Activity> act, boolean withProperties,
            Long activityTypeId)
            throws IllegalArgumentException {

        if (act == null) {
            logger.info("No activities to convert given");
            throw new IllegalArgumentException();
        }
        logger.debug("Creating dtos from {} Activity JPA entities", act.size());

        List<ActivityDto> dtos = new ArrayList<>();
        act.forEach(a -> {
            ActivityDto dto = new ActivityDto();
            dto.setActivityTypeId(activityTypeId);
            dto.setId(a.getId());
            dto.setStartDate(a.getStartDate());
            dto.setEndDate(a.getEndDate());
            dto.setInfo(a.getInfo() == null ? "" : a.getInfo());

            if (withProperties) {
                dto.setProperties(createActivityPropertyDtosFromJpa(a.getProperties(), a.getId()));
            } else
                dto.setProperties(new HashSet<>());

            dtos.add(dto);
        });

        return dtos;
    }

    /**
     * Creates a single activity dto from the given activity.
     * 
     * @param act            Activity to convert to activity dto
     * @param withProperties If true, properties will be added to the dto.
     *                       Properties MUST be preloaded by JPA.
     * @param activityTypeId Id of the activity type
     * @return The converted dto
     */
    public static final ActivityDto createSingleActivityDtoFromJpa(Activity act, boolean withProperties,
            Long activityTypeId) throws IllegalArgumentException {
        return createActivityDtosFromJpa(List.of(act), withProperties, activityTypeId).get(0);
    }

    // #region Activity Properties
    public static final Set<ActivityPropertyDto> createActivityPropertyDtosFromJpa(Set<ActivityProperty> props,
            Long activityId) {
        Set<ActivityPropertyDto> dtos = new HashSet<>();
        props.forEach(p -> {
            ActivityPropertyDto dto = new ActivityPropertyDto();
            dto.setId(p.getId());
            dto.setPropertyTypeId(p.getPropertyType().getId());
            dto.setActivityId(activityId);
            dto.setType(p.getPropertyType().getType());
            dto.setValue(p.getValue());

            dtos.add(dto);
        });
        return dtos;
    }

    public static final ActivityPropertyDto createSingleActivityPropertyDtoFromJpa(ActivityProperty prop,
            Long activityId) {
        return createActivityPropertyDtosFromJpa(Set.of(prop), activityId).toArray(ActivityPropertyDto[]::new)[0];
    }

    // #region Users
    public static final List<UserResponseDto> createUserResponseDtosFromJpas(List<User> u) {
        logger.debug("Creating dtos from {} User JPA entities", u.size());
        List<UserResponseDto> dtos = new ArrayList<>();

        u.forEach(user -> {
            UserResponseDto dto = new UserResponseDto();

            dto.setId(user.getId());
            dto.setName(user.getName());

            dtos.add(dto);
        });
        return dtos;
    }

    public static final UserResponseDto createSingleUserResponseDtoFromJpa(User u) {
        return createUserResponseDtosFromJpas(List.of(u)).get(0);
    }
}
