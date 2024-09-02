package com.musicapp.musicservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name="albums")
public class Album {
    @Id
    private UUID id;
    private String title;
    private String artist;
    private String cover;
}
