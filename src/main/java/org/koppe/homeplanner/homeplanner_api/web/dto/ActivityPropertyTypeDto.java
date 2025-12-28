package org.koppe.homeplanner.homeplanner_api.web.dto;

import org.koppe.homeplanner.homeplanner_api.jpa.entitiy.PropertyTypeC;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class ActivityPropertyTypeDto {
    private Long id;
    private String name;
    private PropertyTypeC type;
    private Long activityTypeId;
}
