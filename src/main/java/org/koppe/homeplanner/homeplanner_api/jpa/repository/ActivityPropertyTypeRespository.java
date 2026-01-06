package org.koppe.homeplanner.homeplanner_api.jpa.repository;

import java.util.List;

import org.koppe.homeplanner.homeplanner_api.jpa.entitiy.ActivityPropertyType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * JPA repository for retrieving activity property types from the database
 */
public interface ActivityPropertyTypeRespository extends JpaRepository<ActivityPropertyType, Long> {
    /**
     * Retrieves all activity property types by their name
     * 
     * @param name Name of the property types to retrieve
     * @return List of all activity property types with the given name
     */
    List<ActivityPropertyType> findAllByName(String name);

    /**
     * Retrieves all properties with a given name and a given Activity type id
     * 
     * @param name           Name of the property type
     * @param activityTypeId Id of the activity type
     * @return List of all found property types
     */
    @Query("""
            select p from ActivityPropertyType p where p.name = :name and p.activity.id = :activityTypeId
            """)
    List<ActivityPropertyType> findAllByNameAndActivity_Id(@Param("name") String name,
            @Param("activityTypeId") Long activityTypeId);
}
