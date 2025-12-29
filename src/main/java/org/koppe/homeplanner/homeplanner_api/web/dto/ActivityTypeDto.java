package org.koppe.homeplanner.homeplanner_api.web.dto;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.EqualsAndHashCode.Include;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ActivityTypeDto {
    @Include
    private Long id;
    @Include
    private String name;
    private Set<ActivityPropertyTypeDto> properties;
    @Include
    private Boolean timable;
}
