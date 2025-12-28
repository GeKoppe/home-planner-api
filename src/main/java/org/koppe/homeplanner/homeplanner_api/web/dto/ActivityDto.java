package org.koppe.homeplanner.homeplanner_api.web.dto;

import java.time.LocalDateTime;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.EqualsAndHashCode.Include;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ActivityDto {
    @Include
    private Long id;
    @Include
    private Long activityTypeId;
    @Include
    private LocalDateTime startDate;
    @Include
    private LocalDateTime endDate;
    private Set<ActivityPropertyDto> properties;
}
