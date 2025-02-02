package com.musicapp.musicservice.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name="playlists")
@Data
@NoArgsConstructor
public class Playlist {
    @Id
    private UUID id;

    @Column(nullable=false)
    private String name;

    private String description;

    private String image;

    private UUID ownerId;

    private boolean isPublic;

    @ManyToMany
    private List<TrackView> tracks;

    public Playlist(String name, String description, String image, UUID ownerId, boolean isPublic, List<TrackView> tracks) {
        this.name = name;
        this.description = description;
        this.image = image;
        this.ownerId = ownerId;
        this.isPublic = isPublic;
        this.tracks = tracks;
    }
}
