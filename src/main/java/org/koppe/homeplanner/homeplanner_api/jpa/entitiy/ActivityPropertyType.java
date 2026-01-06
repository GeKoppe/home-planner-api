package org.koppe.homeplanner.homeplanner_api.jpa.entitiy;

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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Represents an Activity Property Type. Those are definitions for actual
 * Activity Properties
 */
@Entity
@Table(name = "activity_property_types")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ActivityPropertyType {
    /**
     * ID
     */
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Name
     */
    @Column(name = "name")
    private String name;

    /**
     * Type of the activity this property type was created for
     */
    @ManyToOne
    @JoinColumn(name = "activity_type_id", nullable = false)
    private ActivityType activity;

    /**
     * Type of the property (e.g. string)
     */
    @Column(name = "type", nullable = false)
    private PropertyTypeC type;

    /**
     * Activity properties that were created with this property type as template
     */
    @OneToMany(mappedBy = "propertyType", orphanRemoval = true, fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private Set<ActivityProperty> activityProperties;
}
