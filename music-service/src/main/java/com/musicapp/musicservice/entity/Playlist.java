package com.musicapp.musicservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "playlists")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Playlist {
    @Id
    private UUID id;
    private String title;
    private int length;
    private int duration;
    private boolean isPublic;
    private boolean isSpecial;
    @ManyToMany
    @JoinTable(
            name = "playlist_tracks",
            joinColumns = @JoinColumn(name = "playlist_id"),
            inverseJoinColumns = @JoinColumn(name = "track_id")
    )
    @OrderColumn(name = "playlist_index")
    private List<TrackView> tracks;

    public void addTrack(TrackView track) {
        tracks.add(track);
    }
}
