package com.musicapp.musicservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "albums")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Album {
    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue
    private UUID id;
    private String title;
    @ManyToOne
    @JoinColumn(name = "artist_id")
    private Artist artist;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "album",  cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderColumn(name = "album_index")
    private List<TrackView> tracks = new ArrayList<>();
    private int duration;
    private int length;
    private LocalDate releaseDate;

    public void addTrack(TrackView track) {
        track.setAlbum(this);
        tracks.add(track);
        length++;
        duration += track.getTrackData().getDuration();
    }
}
