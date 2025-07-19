package com.musicapp.musicservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "artists")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Artist {
    @Id
    private UUID id;
    private String name;
    private UUID userId;
    @ManyToMany(mappedBy = "artists", fetch = FetchType.LAZY)
    private Set<TrackData> tracks;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "artist")
    private Set<Album> albums;
}
