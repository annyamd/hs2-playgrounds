package com.example.hs2playgrounds.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "playground_availability")
public class PlaygroundAvailability {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable;

    @Column(name = "available_from")
    private LocalTime availableFrom;

    @Column(name = "available_to")
    private LocalTime availableTo;

    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    @OneToOne(mappedBy = "playgroundAvailability")
    private Playground playground;
}
