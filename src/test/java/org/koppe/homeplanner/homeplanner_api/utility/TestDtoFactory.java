package org.koppe.homeplanner.homeplanner_api.utility;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.koppe.homeplanner.homeplanner_api.jpa.entitiy.User;
import org.koppe.homeplanner.homeplanner_api.web.dto.UserResponseDto;

public class TestDtoFactory {


    @BeforeEach
    public void setup() {
        
    }

    @Test
    public void testCreateUserResponseDtos() {
        User u1 = new User(1L, "Test1", "");
        User u2 = new User(2L, "Test2", "");

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


}
