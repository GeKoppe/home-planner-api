package org.koppe.homeplanner.homeplanner_api.web.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ActivityTypeDto {
    private Long id;
    private String name;
    private List<ActivityPropertyTypeDto> properties;
}
