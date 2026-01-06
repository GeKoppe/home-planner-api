package org.koppe.homeplanner.homeplanner_api.jpa.repository;

import java.util.List;

import org.koppe.homeplanner.homeplanner_api.jpa.entitiy.ActivityType;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * JPA Repository for activity types
 */
public interface ActivityTypeRepository extends JpaRepository<ActivityType, Long> {
    /**
     * Finds all activity types with the given name
     * 
     * @param name Name of the activity type
     * @return All found activity types
     */
    List<ActivityType> findAllByName(String name);
}
