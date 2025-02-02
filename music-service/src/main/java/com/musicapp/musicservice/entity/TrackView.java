package com.musicapp.musicservice.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name="trackviews")
@Data
@NoArgsConstructor
public class TrackView {
    @Id
    private UUID id;

    @Column(nullable=false)
    private String title;

    private String cover;

    @ManyToOne(optional=false)
    @JoinColumn(name = "track_data_id", nullable = false)
    private TrackData trackData;

    public TrackView(String title, String cover, TrackData trackData) {
        this.title = title;
        this.cover = cover;
        this.trackData = trackData;
    }
}
