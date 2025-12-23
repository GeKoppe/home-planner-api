package org.koppe.homeplanner.homeplanner_api.jpa.repository;

import org.koppe.homeplanner.homeplanner_api.jpa.entitiy.Activity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityRepository extends JpaRepository<Activity, Long> {

}
