package org.koppe.homeplanner.homeplanner_api.jpa.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.koppe.homeplanner.homeplanner_api.jpa.entitiy.Activity;
import org.koppe.homeplanner.homeplanner_api.jpa.entitiy.ActivityType;
import org.koppe.homeplanner.homeplanner_api.jpa.repository.ActivityPropertyRepository;
import org.koppe.homeplanner.homeplanner_api.jpa.repository.ActivityRepository;
import org.koppe.homeplanner.homeplanner_api.web.dto.ActivityDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
@SuppressWarnings({ "unused", "null" })
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

    @Transactional(readOnly = true)
    public Activity findById(Long id, boolean withProps) {
        Optional<Activity> a = activities.findById(id);
        if (a.isEmpty()) {
            logger.info("No activity with given id exists");
            return null;
        }

        Activity act = a.get();

        if (withProps) {
            act.getProperties().size();
            act.getProperties().forEach(p -> {
                p.getPropertyType().getId();
            });
        } else
            act.setProperties(new HashSet<>());

        act.getType().getId();
        return act;
    }

    /**
     * TODO implement correctly and with specification
     * 
     * @param from
     * @param to
     * @param withProps
     * @param top
     * @return
     */
    public List<Activity> findAll(Optional<LocalDateTime> from, Optional<LocalDateTime> to, Optional<Boolean> withProps,
            Optional<Long> top) {
        return activities.findAll();
    }

    public boolean activityExistsById(Long id) throws IllegalArgumentException {
        if (id == null) {
            logger.info("No id given");
            throw new IllegalArgumentException();
        }
        return activities.existsById(id);
    }

    @Transactional
    public Activity updateActivity(ActivityDto act) throws IllegalArgumentException {
        if (act == null || act.getId() == null) {
            logger.warn("No activity given");
            throw new IllegalArgumentException();
        }

        Activity activity = findById(act.getId(), true);
        if (activity == null) {
            logger.warn("Activity with id {} does not exist", act.getId());
            throw new IllegalArgumentException();
        }

        activity.setEndDate(act.getEndDate());
        activity.setStartDate(act.getStartDate());
        activity.setInfo(act.getInfo());
        activities.save(activity);

        return activity;
    }

    @Transactional
    public Activity deleteById(Long id) throws IllegalArgumentException {
        if (!activityExistsById(id)) {
            logger.info("No activity with id {} exists", id);
            throw new IllegalArgumentException();
        }

        Optional<Activity> actOp = activities.findById(id);
        Activity act = actOp.get();
        act.getProperties().size();
        act.getType().getId();

        activities.delete(act);
        return act;
    }
}
