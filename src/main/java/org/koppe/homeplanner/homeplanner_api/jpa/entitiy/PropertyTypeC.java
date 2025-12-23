package org.koppe.homeplanner.homeplanner_api.jpa.entitiy;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public enum PropertyTypeC {
    STRING("string"),
    INTEGER("int"),
    DATE("date"),
    BOOLEAN("boolean");

    String value;
}