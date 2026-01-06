package org.koppe.homeplanner.homeplanner_api.jpa.repository;

import java.util.List;

import org.koppe.homeplanner.homeplanner_api.jpa.entitiy.ActivityProperty;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * JPA repository for getting Activity Properties from the database
 */
public interface ActivityPropertyRepository extends JpaRepository<ActivityProperty, Long> {
    /**
     * Get all activity properties belonging to a single activity, designated by the
     * activity id
     * 
     * @param id Id of the activity for which to retrieve all properties
     * @return List of all properties of the given activity
     */
    List<ActivityProperty> findAllByActivity_Id(Long id);
}
