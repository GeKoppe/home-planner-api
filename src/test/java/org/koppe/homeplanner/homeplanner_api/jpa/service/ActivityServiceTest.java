package org.koppe.homeplanner.homeplanner_api.jpa.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.koppe.homeplanner.homeplanner_api.jpa.entitiy.Activity;
import org.koppe.homeplanner.homeplanner_api.jpa.entitiy.ActivityProperty;
import org.koppe.homeplanner.homeplanner_api.jpa.entitiy.ActivityPropertyType;
import org.koppe.homeplanner.homeplanner_api.jpa.entitiy.ActivityType;
import org.koppe.homeplanner.homeplanner_api.jpa.entitiy.PropertyTypeC;
import org.koppe.homeplanner.homeplanner_api.jpa.repository.ActivityPropertyRepository;
import org.koppe.homeplanner.homeplanner_api.jpa.repository.ActivityRepository;
import org.koppe.homeplanner.homeplanner_api.web.dto.ActivityDto;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings({ "null" })
public class ActivityServiceTest {
    @Mock
    private ActivityRepository repo;
    @Mock
    private ActivityPropertyRepository propRepo;
    @Mock
    private ActivityConfigService configService;

    @InjectMocks
    private ActivityService srv;

    private ActivityType t1;
    private ActivityType t2;

    private ActivityPropertyType pt1;
    private ActivityPropertyType pt2;

    private Activity a1;
    private Activity a2;

    private ActivityProperty p1;
    private ActivityProperty p2;

    private ActivityDto dto1;

    @BeforeEach
    public void setup() {
        t1 = new ActivityType(1L, "T1", Set.of(), true, Set.of());
        t2 = new ActivityType(2L, "T2", Set.of(), true, Set.of());

        pt1 = new ActivityPropertyType(1L, "PT1", t1, PropertyTypeC.STRING, Set.of());
        pt2 = new ActivityPropertyType(2L, "PT2", t2, PropertyTypeC.STRING, Set.of());

        a1 = new Activity(1L, t1, LocalDateTime.now(), LocalDateTime.now(), Set.of(), "");
        a2 = new Activity(2L, t1, LocalDateTime.now(), LocalDateTime.now(), Set.of(), "");

        p1 = new ActivityProperty(1L, a1, pt1, "Test1", "");
        p2 = new ActivityProperty(2L, a2, pt2, "Test2", "");

        t1.setProperties(Set.of(pt1));
        t2.setProperties(Set.of(pt2));

        a1.setProperties(Set.of(p1));
        a2.setProperties(Set.of(p2));

        dto1 = new ActivityDto(1L, 1L, LocalDateTime.now(), LocalDateTime.now().plusMinutes(10), Set.of(), "Test");
    }

    @Test
    public void testCreateActivity() {
        // Test null assertion
        assertThrows(IllegalArgumentException.class, () -> srv.createActivity(null, 1L));
        assertThrows(IllegalArgumentException.class, () -> srv.createActivity(a1, null));
        assertThrows(IllegalArgumentException.class, () -> srv.createActivity(null, null));

        // Test type does not exist
        // when(configService.activityExistsById(5L)).thenReturn(false);
        assertThrows(IllegalArgumentException.class, () -> srv.createActivity(a1, 5L));

        when(configService.findActivityTypeById(1L)).thenReturn(t1);
        when(repo.save(a1)).thenReturn(a1);

        Activity act = srv.createActivity(a1, 1L);

        assertNotNull(act);
        assertEquals(a1, act);
    }

    @Test
    public void testFindById() {
        // Test empty optional assertion
        when(repo.findById(5L)).thenReturn(Optional.empty());
        assertNull(srv.findById(5L, false));

        when(repo.findById(1L)).thenReturn(Optional.of(a1));
        Activity a = srv.findById(1L, true);
        assertEquals(a1, a);
        assertNotEquals(0, a.getProperties().size());

        a = srv.findById(1L, false);
        assertEquals(a1, a);
        assertEquals(0, a.getProperties().size());
    }

    @Test
    public void testActivityExistsById() {
        assertThrows(IllegalArgumentException.class, () -> srv.activityExistsById(null));

        when(repo.existsById(5L)).thenReturn(false);
        when(repo.existsById(1L)).thenReturn(true);

        assertTrue(srv.activityExistsById(1L));
        assertFalse(srv.activityExistsById(5L));
    }

    @Test
    public void testDeleteActivity() {
        when(repo.existsById(5L)).thenReturn(false);
        when(repo.existsById(1L)).thenReturn(true);
        when(repo.findById(1L)).thenReturn(Optional.of(a1));

        doNothing().when(repo).delete(a1);
        assertThrows(IllegalArgumentException.class, () -> srv.deleteById(5L));

        Activity a = srv.deleteById(1L);
        assertEquals(a1, a);
    }

    @Test
    public void testUpdateActivity() {
        // Test non null assertions
        assertThrows(IllegalArgumentException.class, () -> srv.updateActivity(null));
        dto1.setId(null);
        assertThrows(IllegalArgumentException.class, () -> srv.updateActivity(dto1));

        when(repo.findById(5L)).thenReturn(Optional.empty());
        dto1.setId(5L);
        assertThrows(IllegalArgumentException.class, () -> srv.updateActivity(dto1));

        when(repo.findById(1L)).thenReturn(Optional.of(a1));
        dto1.setId(1L);
        Activity a = srv.updateActivity(dto1);
        assertEquals(dto1.getInfo(), a.getInfo());
        assertEquals(dto1.getEndDate(), a.getEndDate());
        assertEquals(dto1.getStartDate(), a.getStartDate());
        assertEquals(dto1.getId(), a.getId());
    }

    @Test
    public void testFindAll() {
        when(repo.findAll()).thenReturn(List.of(a1, a2));

        assertEquals(2, srv.findAll(null, null, null, null).size());
    }
}
