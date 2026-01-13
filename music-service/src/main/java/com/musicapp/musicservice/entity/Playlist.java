package com.musicapp.musicservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "playlists")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Playlist {
    @Id
    @GeneratedValue
    private UUID id;
    private UUID userId;
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
    private List<TrackView> tracks = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "special_type")
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private PlaylistSpecialType specialType;

    public boolean addTrack(TrackView track) {
        if (tracks.contains(track)) {
            return false;
        }
        tracks.add(track);
        length++;
        duration += track.getTrackData().getDuration();
        return true;
    }

    public boolean removeTrack(TrackView track) {
        boolean removed = tracks.remove(track);
        if (removed) {
            length--;
            duration -= track.getTrackData().getDuration();
        }
        return removed;
    }
}
