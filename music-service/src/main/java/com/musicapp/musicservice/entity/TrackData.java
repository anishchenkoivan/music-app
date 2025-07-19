package com.musicapp.musicservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "track_data")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrackData {
    @Id
    private UUID id;
    private long likesCount;
    private long playsCount;
    private int duration;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "track_artists",
            joinColumns = @JoinColumn(name = "track_id"),
            inverseJoinColumns = @JoinColumn(name = "artist_id")
    )
    private Set<Artist> artists;
}
