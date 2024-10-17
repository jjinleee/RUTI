package com.hyejin.ruti.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
@Table(name="routine_status_table")
public class RoutineStateEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "routine_id", nullable = false)
    @JsonBackReference
    private RoutineEntity routine;

    @Column(nullable = false)
    private String date;  // The specific date (e.g., "2024-10-16")

    @Column(nullable = false)
    private String state = "pending";  // Status for the specific date

}