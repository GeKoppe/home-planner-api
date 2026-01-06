package org.koppe.homeplanner.homeplanner_api.jpa.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.koppe.homeplanner.homeplanner_api.jpa.entitiy.Session;
import org.koppe.homeplanner.homeplanner_api.jpa.entitiy.User;
import org.koppe.homeplanner.homeplanner_api.jpa.repository.SessionRepository;
import org.koppe.homeplanner.homeplanner_api.jpa.repository.UserRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings({ "null" })
public class SessionServiceTest {
    @Mock
    private SessionRepository repo;

    @Mock
    private UserRepository userRepo;

    @InjectMocks
    private SessionService srv;

    private User u1;
    private Session s1;

    @BeforeEach
    public void setup() {
        u1 = new User(1L, "Test", "", Set.of());
        s1 = new Session("random-uuid", u1, LocalDateTime.now().plusMinutes(10L));
    }

    @Test
    public void testCreateSessionById() {
        when(userRepo.findById(1L)).thenReturn(Optional.of(u1));
        when(userRepo.findById(5L)).thenReturn(Optional.empty());
        when(repo.save(any(Session.class))).thenReturn(s1);
        
        assertThrows(IllegalArgumentException.class, () -> srv.createSession((Long) null));
        assertThrows(IllegalArgumentException.class, () -> srv.createSession(5L));

        Session session = srv.createSession(1L);
        assertEquals("random-uuid", session.getGuid());
        assertEquals("Test", session.getUser().getName());
    }

    @Test
    public void testCreateSessionByName() {
        when(userRepo.findByName("Test")).thenReturn(List.of(u1));
        when(userRepo.findByName("Test2")).thenReturn(new ArrayList<>());
        when(repo.save(any(Session.class))).thenReturn(s1);

        assertThrows(IllegalArgumentException.class, () -> srv.createSession((String) null));
        assertThrows(IllegalArgumentException.class, () -> srv.createSession("Test2"));
        assertThrows(IllegalArgumentException.class, () -> srv.createSession("    "));

        Session session = srv.createSession("Test");
        assertEquals("random-uuid", session.getGuid());
        assertEquals("Test", session.getUser().getName());
    }

    @Test
    public void testFindByGuid() {
        when(repo.findById("random-uuid")).thenReturn(Optional.of(s1));

        assertThrows(IllegalArgumentException.class, () -> srv.findById(null));
        assertThrows(IllegalArgumentException.class, () -> srv.findById(""));
        assertThrows(IllegalArgumentException.class, () -> srv.findById("      "));

        Optional<Session> s = srv.findById("random-uuid");
        assertTrue(s.isPresent());
        
        Session sess = s.get();
        assertEquals("random-uuid", sess.getGuid());
        assertEquals(1L, sess.getUser().getId());
    }

    @Test
    public void testRefreshSession() {
        Session s = new Session(s1.getGuid(), s1.getUser(), s1.getExpiration().plusMinutes(10L));
        when(repo.save(s1)).thenReturn(s);

        Session refreshed = srv.refreshSession(s1);
        assertEquals(s1.getGuid(), refreshed.getGuid());
        assertEquals(s1.getUser(), refreshed.getUser());
        assertEquals(s.getExpiration(), refreshed.getExpiration());
    }
}
