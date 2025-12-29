package org.koppe.homeplanner.homeplanner_api.jpa.service;

import org.koppe.homeplanner.homeplanner_api.jpa.entitiy.Activity;
import org.koppe.homeplanner.homeplanner_api.jpa.entitiy.ActivityType;
import org.koppe.homeplanner.homeplanner_api.jpa.repository.ActivityPropertyRepository;
import org.koppe.homeplanner.homeplanner_api.jpa.repository.ActivityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
@SuppressWarnings({"unused", "null"})
public class ActivityService {
    private final Logger logger = LoggerFactory.getLogger(ActivityService.class);
    /**
     * Repository for working with activities
     */
    private ActivityRepository activities;

    /**
     * Activity properties
     */
    private ActivityPropertyRepository properties;

    private final ActivityConfigService config;

    @Transactional
    public Activity createActivity(Activity act, Long typeId) {
        if (act == null || typeId == null) {
            logger.info("No activity given");
            throw new IllegalArgumentException();
        }

        ActivityType type = config.findActivityTypeById(typeId);
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
