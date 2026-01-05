package org.koppe.homeplanner.homeplanner_api.utility;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.koppe.homeplanner.homeplanner_api.jpa.entitiy.ActivityPropertyType;
import org.koppe.homeplanner.homeplanner_api.jpa.entitiy.ActivityType;
import org.koppe.homeplanner.homeplanner_api.jpa.entitiy.PropertyTypeC;
import org.koppe.homeplanner.homeplanner_api.jpa.entitiy.User;
import org.koppe.homeplanner.homeplanner_api.web.dto.ActivityPropertyTypeDto;
import org.koppe.homeplanner.homeplanner_api.web.dto.UserResponseDto;

// TODO finish testing
@SuppressWarnings({ "null", "unused" })
public class TestDtoFactory {
    private Set<ActivityPropertyType> typeSet1;
    private Set<ActivityPropertyType> typeSet2;
    private ActivityType type1;
    private ActivityType type2;

    @BeforeEach
    public void setup() {
        typeSet1 = Set.of(
                new ActivityPropertyType(1L, "Prop1", type1, PropertyTypeC.STRING, null),
                new ActivityPropertyType(2L, "Prop2", type1, PropertyTypeC.STRING, null));

        typeSet2 = Set.of(
                new ActivityPropertyType(3L, "Prop3", type2, PropertyTypeC.STRING, null));

        type1 = new ActivityType(1L, "Act1", typeSet1, true, new HashSet<>());
        type2 = new ActivityType(2L, "Act2", typeSet1, false, new HashSet<>());

    }

    @Test
    public void testCreateUserResponseDtos() {
        User u1 = new User(1L, "Test1", "", Set.of());
        User u2 = new User(2L, "Test2", "", Set.of());

        List<UserResponseDto> dtos = DtoFactory.createUserResponseDtosFromJpas(List.of(u1, u2));
        assertEquals(2, dtos.size());
        assertInstanceOf(UserResponseDto.class, dtos.get(0));
        assertEquals("Test1", dtos.get(0).getName());
        assertEquals(1L, dtos.get(0).getId());

        UserResponseDto dto = DtoFactory.createSingleUserResponseDtoFromJpa(u1);
        assertInstanceOf(UserResponseDto.class, dto);
        assertEquals("Test1", dto.getName());
        assertEquals(1L, dto.getId());
    }

    @Test
    public void testCreateActivityPropertyTypeDtosFromJpa() {
        // Test null checks
        assertThrows(IllegalArgumentException.class,
                () -> DtoFactory.createActivityPropertyTypeDtosFromJpa(null, null));
        assertThrows(IllegalArgumentException.class,
                () -> DtoFactory.createActivityPropertyTypeDtosFromJpa(new HashSet<>(), null));
        assertThrows(IllegalArgumentException.class, () -> DtoFactory.createActivityPropertyTypeDtosFromJpa(null, 7L));

        Set<ActivityPropertyTypeDto> dtos = DtoFactory.createActivityPropertyTypeDtosFromJpa(typeSet1, 1L);
        assertFalse(dtos.isEmpty());
        assertEquals(2, dtos.size());
    }
}
