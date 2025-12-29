package org.koppe.homeplanner.homeplanner_api.web.dto;

import org.koppe.homeplanner.homeplanner_api.jpa.entitiy.PropertyTypeC;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class ActivityPropertyDto {
    private Long id;
    private Long activityId;
    private Long propertyTypeId;
    private PropertyTypeC type;
    private String value;
}
