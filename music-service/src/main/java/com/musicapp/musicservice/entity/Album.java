package com.musicapp.musicservice.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name="albums")
@Data
@NoArgsConstructor
public class Album {
    @Id
    private UUID id;

    @Column(nullable=false)
    private String title;

    @Column(nullable=false)
    private String artist;

    private String cover;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "track_id")
    private List<TrackView> tracks;

    public Album(String title, String artist, String cover, List<TrackView> tracks) {
        this.title = title;
        this.artist = artist;
        this.cover = cover;
        this.tracks = tracks;
    }
}
