package com.musicapp.musicservice.entity;

import jakarta.persistence.*;

import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name="trackdata")
public class TrackData {
    @Id
    private UUID id;
    private ZonedDateTime creationDateTime;
    private Byte[] source;
}
