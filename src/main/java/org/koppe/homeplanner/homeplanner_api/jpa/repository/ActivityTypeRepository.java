package org.koppe.homeplanner.homeplanner_api.jpa.repository;

import java.util.List;

import org.koppe.homeplanner.homeplanner_api.jpa.entitiy.ActivityType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityTypeRepository extends JpaRepository<ActivityType, Long> {
    List<ActivityType> findAllByName(String name);
}
