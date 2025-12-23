package org.koppe.homeplanner.homeplanner_api.jpa.repository;

import java.util.List;

import org.koppe.homeplanner.homeplanner_api.jpa.entitiy.ActivityPropertyType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ActivityPropertyTypeRespository extends JpaRepository<ActivityPropertyType, Long> {
    List<ActivityPropertyType> findAllByName(String name);

    @Query("""
            select p from ActivityPropertyType p where p.name = :name and p.activity.id = :activityTypeId
            """)
    List<ActivityPropertyType> findAllByNameAndActivity_Id(@Param("name") String name,
            @Param("activityTypeId") Long activityTypeId);
}
