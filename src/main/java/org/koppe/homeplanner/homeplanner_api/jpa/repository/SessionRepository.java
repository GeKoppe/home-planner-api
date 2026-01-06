package org.koppe.homeplanner.homeplanner_api.jpa.repository;

import org.koppe.homeplanner.homeplanner_api.jpa.entitiy.Session;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * JPA Repository for all sessions in the system
 */
public interface SessionRepository extends JpaRepository<Session, String> {
}
