package org.koppe.homeplanner.homeplanner_api.jpa.specification;

import java.util.ArrayList;
import java.util.List;

import org.koppe.homeplanner.homeplanner_api.jpa.entitiy.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class UserSpecificationBuilder {
    /**
     * Logger
     */
    private final Logger logger = LoggerFactory.getLogger(UserSpecificationBuilder.class);

    public Specification<User> build() {
        logger.debug("Creating specification for user query");
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }

}
