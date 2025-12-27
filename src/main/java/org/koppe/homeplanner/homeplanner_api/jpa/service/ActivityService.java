package org.koppe.homeplanner.homeplanner_api.jpa.service;

import java.util.List;
import java.util.Optional;

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

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
@SuppressWarnings("unused")
public class ActivityService {
    /**
     * Logger
     */
    private final Logger logger = LoggerFactory.getLogger(ActivityService.class);

    /**
     * Repository for working with activities
     */
    private ActivityRepository activities;

    /**
     * Activity properties
     */
    private ActivityPropertyRepository properties;

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
     * Returns all defined activity types
     * 
     * @return
     */
    @Transactional(readOnly = true)
    public List<ActivityType> findAllActivityTypes(boolean loadProperties) {
        List<ActivityType> types = actTypes.findAll();
        if (loadProperties)
            types.forEach(t -> t.getProperties().size());
        return types;
    }

    /**
     * Returns true if an activity with given name already exists in the database
     * 
     * @param activityName Name of the activity
     * @return
     * @throws IllegalArgumentException If no name is given
     */
    public boolean activityTypeExistsByName(String activityName) throws IllegalArgumentException {
        if (activityName == null || activityName.isBlank()) {
            logger.info("No activity name given");
            throw new IllegalArgumentException();
        }
        return actTypes.findAllByName(activityName).size() > 0;
    }

    @Transactional(readOnly = true)
    public ActivityType createActivityType(String name) throws IllegalArgumentException {
        if (name == null || name.isBlank()) {
            logger.info("No activity name given");
            throw new IllegalArgumentException();
        }

        ActivityType type = new ActivityType();
        type.setName(name);

        return createActivityType(type);
    }

    @Transactional
    public ActivityType createActivityType(ActivityType type) throws IllegalArgumentException {
        if (type == null || type.getName() == null || type.getName().isBlank()) {
            logger.info("No activity name given");
            throw new IllegalArgumentException();
        }

        return actTypes.save(type);
    }

    @Transactional
    public ActivityType findActivityTypeById(Long id) throws IllegalArgumentException {
        if (id == null) {
            logger.info("No activity id given");
            throw new IllegalArgumentException();
        }
        Optional<ActivityType> typeOpt = actTypes.findById(id);
        if (typeOpt.isEmpty())
            return null;
        ActivityType type = typeOpt.get();
        type.getProperties().size();
        return type;
    }

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

    public boolean activityExistsById(Long id) {
        if (id == null) {
            logger.info("No activity type id given");
            throw new IllegalArgumentException();
        }

        return actTypes.existsById(id);
    }

    // #region Type Properties
    public boolean activityTypePropertyExistsByNameAndActivityTypeId(String name, Long activityId)
            throws IllegalArgumentException {
        if (name == null || name.isBlank() || activityId == null || activityId < 0) {
            logger.info("No activity property name given");
            throw new IllegalArgumentException();
        }

        return propTypes.findAllByNameAndActivity_Id(name, activityId).size() > 0;
    }

    @Transactional
    public ActivityPropertyType createActivityPropertyType(ActivityPropertyType prop) throws IllegalArgumentException {
        if (prop == null) {
            throw new IllegalArgumentException();
        }
        return propTypes.save(prop);
    }

    // #region Activities
    @Transactional
    public Activity createActivity(Activity act, Long typeId) {
        if (act == null || typeId == null) {
            logger.info("No activity given");
            throw new IllegalArgumentException();
        }

        ActivityType type = findActivityTypeById(typeId);
        if (type == null) {
            logger.info("No valid activity id given");
            throw new IllegalArgumentException();
        }

        act.setType(type);
        Activity created = activities.save(act);
        created.getType().getId();
        return created;
    }
}
