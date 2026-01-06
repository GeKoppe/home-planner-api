package org.koppe.homeplanner.homeplanner_api.jpa.entitiy;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Represents a single property of an activity
 */
@Entity
@Table(name = "activity_properties")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ActivityProperty {

    /**
     * ID of the activity property
     */
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Activity this property belongs to
     */
    @ManyToOne
    @JoinColumn(name = "activity_id", nullable = false)
    private Activity activity;

    /**
     * Type of the property
     */
    @ManyToOne
    @JoinColumn(name = "property_type_id", nullable = false)
    private ActivityPropertyType propertyType;

    /**
     * Value this property holds
     */
    @Column(name = "value", nullable = false)
    private String value;

    /**
     * Contains additional information for this property
     */
    @Column(name = "additional_info", nullable = true)
    private String additionalInfo;
}
