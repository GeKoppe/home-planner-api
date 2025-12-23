package org.koppe.homeplanner.homeplanner_api.jpa.repository;

import java.util.List;

import org.koppe.homeplanner.homeplanner_api.jpa.entitiy.ActivityProperty;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityPropertyRepository extends JpaRepository<ActivityProperty, Long> {
    List<ActivityProperty> findAllByActivity_Id(Long id);
}
