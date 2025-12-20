package org.koppe.homeplanner.homeplanner_api.web.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SessionDto {
    private String guid;
    private String userName;
    private LocalDateTime expiration;
}
