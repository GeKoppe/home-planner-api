package org.koppe.homeplanner.homeplanner_api.jpa.service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.koppe.homeplanner.homeplanner_api.config.CacheNames;
import org.koppe.homeplanner.homeplanner_api.jpa.entitiy.Activity;
import org.koppe.homeplanner.homeplanner_api.jpa.entitiy.ActivityPropertyType;
import org.koppe.homeplanner.homeplanner_api.jpa.entitiy.ActivityType;
import org.koppe.homeplanner.homeplanner_api.jpa.repository.ActivityPropertyRepository;
import org.koppe.homeplanner.homeplanner_api.jpa.repository.ActivityPropertyTypeRespository;
import org.koppe.homeplanner.homeplanner_api.jpa.repository.ActivityRepository;
import org.koppe.homeplanner.homeplanner_api.jpa.repository.ActivityTypeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
@SuppressWarnings({ "unused", "null" })
public class ActivityConfigService {
    /**
     * Logger
     */
    private final Logger logger = LoggerFactory.getLogger(ActivityConfigService.class);

    /**
     * Activity types
     */
    private ActivityTypeRepository actTypes;

    /**
     * Activity property types
     */
    private ActivityPropertyTypeRespository propTypes;

    // #region Types

    /**
     * Returns a list of all activity types
     * 
     * @param loadProperties If set to true, the activity type properties will be
     *                       loaded into the entitiies. If false, the type
     *                       properties will be set to an empty HashSet
     * @return List of all activity types
     */
    @Transactional(readOnly = true)
    public List<ActivityType> findAllActivityTypes(boolean loadProperties) {
        List<ActivityType> types = actTypes.findAll();
        if (loadProperties)
            types.forEach(t -> t.getProperties().size());
        else
            types.forEach(t -> t.setProperties(new HashSet<>()));
        return types;
    }

    /**
     * Checks if an {@link ActivityType} entity with the given name exists
     * 
     * @param activityName Name of the ActivityType to be checked
     * @return True, if an activity with given name exists
     * @throws IllegalArgumentException If no name is given
     */
    public boolean activityTypeExistsByName(String activityName) throws IllegalArgumentException {
        if (activityName == null || activityName.isBlank()) {
            logger.info("No activity name given");
            throw new IllegalArgumentException();
        }
        return actTypes.findAllByName(activityName).size() > 0;
    }

    /**
     * Creates a new ActivityType with the given name. Calls
     * {@link ActivityConfigService#createActivityType(ActivityType)}
     * 
     * @param name Name of the activity to be created
     * @return The newly created activity type
     * @throws IllegalArgumentException If no name is given
     */
    public ActivityType createActivityType(String name) throws IllegalArgumentException {
        if (name == null || name.isBlank()) {
            logger.info("No activity name given");
            throw new IllegalArgumentException();
        }

        ActivityType type = new ActivityType();
        type.setName(name);

        return createActivityType(type);
    }

    /**
     * Saves the new activity type in the database
     * 
     * @param type Type to be saved
     * @return The newly created activity type
     * @throws IllegalArgumentException If no name is given or an activity type with
     *                                  the name already exists
     */
    @Transactional
    public ActivityType createActivityType(ActivityType type) throws IllegalArgumentException {
        if (type == null || type.getName() == null || type.getName().isBlank()) {
            logger.info("No activity name given");
            throw new IllegalArgumentException();
        }

        if (activityTypeExistsByName(type.getName())) {
            logger.info("Activity type with name {} already exists", type.getName());
            throw new IllegalArgumentException();
        }

        return actTypes.save(type);
    }

    /**
     * Finds activity type with the given id in the database
     * 
     * @param id Id of the activity type to be returned
     * @return The found activity type or null, if no type with given id exists
     * @throws IllegalArgumentException If id is null
     */
    @Transactional
    public ActivityType findActivityTypeById(@NotNull Long id) throws IllegalArgumentException {
        // Check, if id is null
        if (id == null) {
            logger.info("No activity id given");
            throw new IllegalArgumentException();
        }

        // Retrieve the activity type
        Optional<ActivityType> typeOpt = actTypes.findById(id);
        if (typeOpt.isEmpty())
            return null;

        // Load activity type and properties
        ActivityType type = typeOpt.get();
        type.getProperties().size();
        return type;
    }

    /**
     * Deletes activity type with given id from the database
     * 
     * @param id Id of the activity type to be deleted
     * @return The deleted activity type or null, if no activity type with given id
     *         exists
     * @throws IllegalArgumentException If id is null
     */
    @Transactional
    public ActivityType deleteActivityType(Long id) throws IllegalArgumentException {
        if (id == null) {
            logger.info("No activity id given");
            throw new IllegalArgumentException();
        }

        Optional<ActivityType> typeOpt = actTypes.findById(id);
        if (typeOpt.isEmpty())
            return null;

        ActivityType type = typeOpt.get();
        type.getProperties().size();
        actTypes.delete(type);
        return type;
    }

    /**
     * Checks, if an activity type with the given id exists
     * 
     * @param id Id of the activity type to be checked
     * @return True, if such an activity type exists, false otherwise
     * @throws IllegalArgumentException if id is null
     */
    public boolean activityExistsById(Long id) throws IllegalArgumentException {
        if (id == null) {
            logger.info("No activity type id given");
            throw new IllegalArgumentException();
        }

        return actTypes.existsById(id);
    }

    // #region Type Properties
    /**
     * Checks, if an ActivityPropertyType with the given name for the given activity
     * id exists
     * 
     * @param name       Name of the activity property type
     * @param activityId ID of the activity type
     * @return True, if such a property type exists, false otherwise
     * @throws IllegalArgumentException If name is null or blank or activityId is
     *                                  null
     */
    public boolean activityTypePropertyExistsByNameAndActivityTypeId(String name, Long activityId)
            throws IllegalArgumentException {
        if (name == null || name.isBlank() || activityId == null || activityId < 0) {
            logger.info("No activity property name given");
            throw new IllegalArgumentException();
        }

        return propTypes.findAllByNameAndActivity_Id(name, activityId).size() > 0;
    }

    /**
     * Creates an activity property type in the database
     * 
     * @param prop Type to be created
     * @return The created type
     * @throws IllegalArgumentException If prop is null
     */
    @Transactional
    public ActivityPropertyType createActivityPropertyType(ActivityPropertyType prop) throws IllegalArgumentException {
        if (prop == null) {
            throw new IllegalArgumentException();
        }
        return propTypes.save(prop);
    }

    /**
     * Updates an activity type in the database
     * 
     * @param type Type to be updated
     * @return The updated type
     * @throws IllegalArgumentException If type is null or type.id is missing or
     *                                  invalid
     */
    @Transactional
    public ActivityType updateActivityType(ActivityType type) throws IllegalArgumentException {
        if (type == null) {
            logger.info("No activity type given");
            throw new IllegalArgumentException();
        }

        if (type.getId() == null || type.getId() < 0 || !activityExistsById(type.getId())) {
            logger.info("Invalid activity type id given");
            throw new IllegalArgumentException();
        }

        ActivityType t = actTypes.findById(type.getId()).get();
        t.setName(type.getName());
        t.setTimeable(type.getTimeable());
        return actTypes.save(t);
    }
}
