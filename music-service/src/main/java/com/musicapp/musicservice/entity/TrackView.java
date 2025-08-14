package com.musicapp.musicservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "track_views")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrackView {
    @Id
    @GeneratedValue
    private UUID id;
    private String title;
    @ManyToOne
    @JoinColumn(name = "track_data_id")
    private TrackData trackData;
    @ManyToOne
    @JoinColumn(name = "album_id")
    private Album album;
}
