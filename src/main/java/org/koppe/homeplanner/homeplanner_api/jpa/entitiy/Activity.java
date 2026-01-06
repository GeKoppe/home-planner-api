package org.koppe.homeplanner.homeplanner_api.jpa.entitiy;

import java.time.LocalDateTime;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.EqualsAndHashCode.Include;

/**
 * Represents a single activity within the system
 */
@Entity
@Table(name = "activities")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Activity {
    /**
     * Id of the activity
     */
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Include
    private Long id;

    /**
     * Type of the activity
     */
    @ManyToOne
    @JoinColumn(name = "activity_type_id", nullable = false)
    @Include
    private ActivityType type;

    /**
     * Starting date of the activity
     */
    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    /**
     * Ending date of the activity
     */
    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    /**
     * All properties of the activity
     */
    @OneToMany(mappedBy = "activity", fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.REMOVE)
    private Set<ActivityProperty> properties;

    /**
     * Additional info for the activity
     */
    @Column(name = "info", nullable = true)
    @Include
    private String info;
}
