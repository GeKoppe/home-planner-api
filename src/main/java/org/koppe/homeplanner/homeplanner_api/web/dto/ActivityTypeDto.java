package org.koppe.homeplanner.homeplanner_api.web.dto;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ActivityTypeDto {
    private Long id;
    private String name;
    private Set<ActivityPropertyTypeDto> properties;
    private Boolean timable;
}
