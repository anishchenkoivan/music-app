package com.musicapp.musicservice.entity;

import jakarta.persistence.*;

import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name="tracks")
public class Track {
    @Id
    private UUID id;
    private String title;
//    @OneToMany(mappedBy = )
    private Artist[] artist;
    private ZonedDateTime creationDateTime;
    private String cover;
}
