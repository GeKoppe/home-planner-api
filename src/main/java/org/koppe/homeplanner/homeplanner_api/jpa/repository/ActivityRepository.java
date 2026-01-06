package org.koppe.homeplanner.homeplanner_api.jpa.repository;

import org.koppe.homeplanner.homeplanner_api.jpa.entitiy.Activity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for getting activities from the database
 */
public interface ActivityRepository extends JpaRepository<Activity, Long> {

}
