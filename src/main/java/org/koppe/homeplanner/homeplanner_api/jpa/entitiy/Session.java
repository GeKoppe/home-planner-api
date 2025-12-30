package org.koppe.homeplanner.homeplanner_api.jpa.entitiy;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.EqualsAndHashCode.Include;

@Entity
@Table(name = "sessions")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Session {

    @Id
    @Column(name = "guid", nullable = false, unique = true)
    @Include
    private String guid;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @Include
    private User user;

    @Column(name = "expiration", nullable = false)
    private LocalDateTime expiration;
}
