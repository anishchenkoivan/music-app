package com.musicapp.musicservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name="playlists")
public class Playlist {
    @Id
    private UUID id;
    private String name;
    private String description;
    private String image;
    private UUID ownerId;
    private boolean isPublic;
}
