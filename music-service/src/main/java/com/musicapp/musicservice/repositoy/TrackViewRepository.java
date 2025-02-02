package com.musicapp.musicservice.repositoy;

import com.musicapp.musicservice.entity.TrackView;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TrackViewRepository extends JpaRepository<TrackView, UUID> {
    List<TrackView> findByTitleIgnoreCase(String title);
}
