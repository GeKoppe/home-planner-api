package org.koppe.homeplanner.homeplanner_api.web.dto;

import java.time.LocalDateTime;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ActivityDto {
    private Long id;
    private Long activityTypeId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Set<ActivityPropertyDto> properties;
}
