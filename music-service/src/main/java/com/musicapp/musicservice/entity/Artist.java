package com.musicapp.musicservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name="artists")
public class Artist {
    @Id
    private UUID id;
    private String name;
    private String description;
}
