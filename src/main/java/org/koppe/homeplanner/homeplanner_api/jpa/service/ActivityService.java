package org.koppe.homeplanner.homeplanner_api.jpa.service;

import org.koppe.homeplanner.homeplanner_api.jpa.entitiy.ActivityType;
import org.koppe.homeplanner.homeplanner_api.jpa.repository.ActivityPropertyRepository;
import org.koppe.homeplanner.homeplanner_api.jpa.repository.ActivityPropertyTypeRespository;
import org.koppe.homeplanner.homeplanner_api.jpa.repository.ActivityRepository;
import org.koppe.homeplanner.homeplanner_api.jpa.repository.ActivityTypeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
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

    @Transactional
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
}
