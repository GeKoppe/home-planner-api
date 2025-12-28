package org.koppe.homeplanner.homeplanner_api.jpa.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.koppe.homeplanner.homeplanner_api.jpa.entitiy.User;
import org.koppe.homeplanner.homeplanner_api.jpa.repository.UserRepository;
import org.koppe.homeplanner.homeplanner_api.web.dto.UserDto;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    public void testFindUserById() {
        User user = new User(1L, "Test", "");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        assertThrows(IllegalArgumentException.class, () -> userService.findUserByid(null));
        
        Optional<User> found = userService.findUserByid(1L);
        assertTrue(found.isPresent());
        assertEquals("Test", found.get().getName());

        Optional<User> notFound = userService.findUserByid(2L);
        assertFalse(notFound.isPresent());
    }

    @Test
    public void testCreateUser() {
        // Test that no blank values are permitted in any combination
        assertThrows(IllegalArgumentException.class, () -> userService.createUser("", "test") );
        assertThrows(IllegalArgumentException.class, () -> userService.createUser("  ", "test") );
        assertThrows(IllegalArgumentException.class, () -> userService.createUser("test", "") );
        assertThrows(IllegalArgumentException.class, () -> userService.createUser("test", "   ") );
        assertThrows(IllegalArgumentException.class, () -> userService.createUser("", "") );
        assertThrows(IllegalArgumentException.class, () -> userService.createUser("    ", "") );
        assertThrows(IllegalArgumentException.class, () -> userService.createUser("", "     ") );
        assertThrows(IllegalArgumentException.class, () -> userService.createUser("    ", "    ") );

        User user = new User();
        user.setName("Test");
        user.setPwHash(new BCryptPasswordEncoder(12).encode("test"));

        User userReturn = new User(2L, "Test", new BCryptPasswordEncoder(12).encode("test"));
        when(userRepository.save(user)).thenReturn(userReturn);

        User created = userService.createUser("Test", "test");
        assertEquals("Test", created.getName());
        assertEquals(2L, created.getId());
    }

    @Test
    public void testFindByName() {
        User found = new User(1L, "Test", "");
        when(userRepository.findByName("Test")).thenReturn(List.of(found));
        when(userRepository.findByName(null)).thenReturn(new ArrayList<>());

        List<User> user = userService.findByName("Test");
        assertEquals(1, user.size());
        assertEquals("Test", user.get(0).getName());
        assertEquals(1L, user.get(0).getId());

        List<User> userNull = userService.findByName(null);
        assertEquals(0, userNull.size());
    }

    @Test
    public void testPasswordMatches() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setName("Test");
        dto.setPassword("elo");

        User u = new User(1L, "Test", encoder.encode("elo"));
        when(userRepository.findByName("Test")).thenReturn(List.of(u));

        assertTrue(userService.passwordMatches(dto));
        assertThrows(IllegalArgumentException.class, () -> userService.passwordMatches(null));

        dto.setName(null);
        assertThrows(IllegalArgumentException.class, () -> userService.passwordMatches(dto));

        dto.setPassword(null);
        assertThrows(IllegalArgumentException.class, () -> userService.passwordMatches(dto));

        dto.setName("   ");
        assertThrows(IllegalArgumentException.class, () -> userService.passwordMatches(dto));

        dto.setPassword("   ");
        assertThrows(IllegalArgumentException.class, () -> userService.passwordMatches(dto));
    }

    @Test
    public void testUserExists() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.existsById(2L)).thenReturn(false);

        assertTrue(userService.userExists(1L));
        assertFalse(userService.userExists(2L));
        assertThrows(IllegalArgumentException.class, () -> userService.userExists(null));
    }

    @Test
    public void testDeleteuser() {
        User user = new User(1L, "Test", "");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Mockito.doNothing().when(userRepository).delete(user);

        assertThrows(IllegalArgumentException.class, () -> userService.deleteUser(null));
        assertEquals(user, userService.deleteUser(1L));

        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> userService.deleteUser(1L));
    }
}
