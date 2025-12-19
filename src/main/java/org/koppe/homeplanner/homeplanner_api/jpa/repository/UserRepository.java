package org.koppe.homeplanner.homeplanner_api.jpa.repository;

import java.util.List;

import org.koppe.homeplanner.homeplanner_api.jpa.entitiy.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByName(String name);
}
