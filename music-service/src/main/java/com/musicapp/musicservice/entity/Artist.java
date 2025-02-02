package com.musicapp.musicservice.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name="artists")
@Data
@NoArgsConstructor
public class Artist {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique=true)
    private UUID userId;

    @Column(nullable=false)
    private String name;

    private String bio;

    public Artist(UUID userId, String name, String bio) {
        this.userId = userId;
        this.name = name;
        this.bio = bio;
    }
}
