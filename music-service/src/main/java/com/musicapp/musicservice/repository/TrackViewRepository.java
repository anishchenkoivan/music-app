package com.musicapp.musicservice.repository;

import com.musicapp.musicservice.entity.TrackView;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TrackViewRepository extends JpaRepository<TrackView, UUID> {
}
