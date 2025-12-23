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

@Entity
@Table(name = "activity_property_types")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ActivityPropertyType {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @ManyToOne
    @JoinColumn(name = "activity_type_id", nullable = false)
    private ActivityType activity;

    @Column(name = "type", nullable = false)
    private TypeC type;

    @Column(name = "timeable", nullable = false)
    private Boolean timeable;

    @AllArgsConstructor
    @Getter
    public static enum TypeC {
        STRING("string"),
        INTEGER("int"),
        DATE("date"),
        BOOLEAN("boolean");

        String value;
    }
}
