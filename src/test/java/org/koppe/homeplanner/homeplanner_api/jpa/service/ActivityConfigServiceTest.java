package org.koppe.homeplanner.homeplanner_api.jpa.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.koppe.homeplanner.homeplanner_api.jpa.entitiy.ActivityPropertyType;
import org.koppe.homeplanner.homeplanner_api.jpa.entitiy.ActivityType;
import org.koppe.homeplanner.homeplanner_api.jpa.entitiy.PropertyTypeC;
import org.koppe.homeplanner.homeplanner_api.jpa.repository.ActivityPropertyTypeRespository;
import org.koppe.homeplanner.homeplanner_api.jpa.repository.ActivityTypeRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings({ "null" })
public class ActivityConfigServiceTest {
    @Mock
    private ActivityTypeRepository actTypes;
    @Mock
    private ActivityPropertyTypeRespository actProps;

    @InjectMocks
    private ActivityConfigService srv;

    private ActivityType a1;
    private ActivityType a2;

    private ActivityPropertyType p1;
    private ActivityPropertyType p2;
    private ActivityPropertyType p3;

    @BeforeEach
    public void setup() {
        p1 = new ActivityPropertyType(1L, "P1", a1, PropertyTypeC.STRING, new HashSet<>());
        p2 = new ActivityPropertyType(2L, "P2", a1, PropertyTypeC.BOOLEAN, new HashSet<>());
        p3 = new ActivityPropertyType(3L, "P3", a2, PropertyTypeC.STRING, new HashSet<>());

        a1 = new ActivityType(1L, "A1", Set.of(p1, p2), true, new HashSet<>());
        a2 = new ActivityType(2L, "A2", Set.of(p3), false, new HashSet<>());
    }

    @Test
    public void testFindAllActivityTypes() {
        when(actTypes.findAll()).thenReturn(List.of(a1, a2));
        // Load without properties
        List<ActivityType> types = srv.findAllActivityTypes(false);
        assertEquals(2, types.size());

        ActivityType a = types.get(0);
        assertEquals(0, a.getProperties().size());
        assertEquals(a1, a);

        // Load with properties
        a1 = new ActivityType(1L, "A1", Set.of(p1, p2), true, new HashSet<>());
        when(actTypes.findAll()).thenReturn(List.of(a1, a2));
        List<ActivityType> types2 = srv.findAllActivityTypes(true);
        assertEquals(2, types2.size());

        for (var x : types2) {
            if (x.equals(a1)) {
                a = x;
                break;
            }
        }

        assertEquals(2, a.getProperties().size());
        assertEquals(a1, a);
    }

    @Test
    public void testActivityTypeExistsByName() {
        // Test argument checking
        assertThrows(IllegalArgumentException.class, () -> srv.activityTypeExistsByName(null));
        assertThrows(IllegalArgumentException.class, () -> srv.activityTypeExistsByName(""));
        assertThrows(IllegalArgumentException.class, () -> srv.activityTypeExistsByName("   "));

        // Test actual logic
        when(actTypes.findAllByName("A1")).thenReturn(List.of(a1));
        when(actTypes.findAllByName("A3")).thenReturn(List.of());

        assertTrue(srv.activityTypeExistsByName("A1"));
        assertFalse(srv.activityTypeExistsByName("A3"));
    }

    @Test
    public void testCreateActivityType() {
        // Test argument checking
        assertThrows(IllegalArgumentException.class, () -> srv.createActivityType((String) null));
        assertThrows(IllegalArgumentException.class, () -> srv.createActivityType(""));
        assertThrows(IllegalArgumentException.class, () -> srv.createActivityType("     "));

        // Test actual logic
        when(actTypes.findAllByName("A1")).thenReturn(List.of(a1));
        when(actTypes.findAllByName("A3")).thenReturn(List.of());
        when(actTypes.save(any(ActivityType.class))).thenReturn(a1);

        assertEquals(a1, srv.createActivityType("A3"));
        assertThrows(IllegalArgumentException.class, () -> srv.createActivityType("A1"));

        ActivityType a = new ActivityType(3L, null, null, null, null);
        assertThrows(IllegalArgumentException.class, () -> srv.createActivityType((ActivityType) null));
        assertThrows(IllegalArgumentException.class, () -> srv.createActivityType(a));

        a.setName("");
        assertThrows(IllegalArgumentException.class, () -> srv.createActivityType(a));
        a.setName("     ");
        assertThrows(IllegalArgumentException.class, () -> srv.createActivityType(a));
    }

    @Test
    public void testFindActivityTypeById() {
        // Test argument checking
        assertThrows(IllegalArgumentException.class, () -> srv.findActivityTypeById(null));

        when(actTypes.findById(5L)).thenReturn(Optional.empty());
        when(actTypes.findById(1L)).thenReturn(Optional.of(a1));

        ActivityType a = srv.findActivityTypeById(1L);
        assertEquals(a1, a);
        assertNull(srv.findActivityTypeById(5L));
    }

    @Test
    public void testDeleteActivityType() {
        // Test argument checking
        assertThrows(IllegalArgumentException.class, () -> srv.deleteActivityType(null));

        when(actTypes.findById(5L)).thenReturn(Optional.empty());
        when(actTypes.findById(1L)).thenReturn(Optional.of(a1));

        assertNull(srv.deleteActivityType(5L));
        doNothing().when(actTypes).delete(any(ActivityType.class));
        
        assertEquals(a1, srv.deleteActivityType(1L));
    }

    @Test
    public void testActivityTypeExistsById() {
        // test argument checking
        assertThrows(IllegalArgumentException.class, () -> srv.activityExistsById(null));

        when(actTypes.existsById(1L)).thenReturn(true);
        when(actTypes.existsById(5L)).thenReturn(false);

        assertTrue(srv.activityExistsById(1L));
        assertFalse(srv.activityExistsById(5L));
    }

    

}
