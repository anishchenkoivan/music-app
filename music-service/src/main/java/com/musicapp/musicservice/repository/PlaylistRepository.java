package com.musicapp.musicservice.repository;

import com.musicapp.musicservice.entity.Playlist;
import com.musicapp.musicservice.entity.PlaylistSpecialType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PlaylistRepository extends JpaRepository<Playlist, UUID> {
    Optional<Playlist> findByUserIdAndSpecialType(UUID userId, PlaylistSpecialType specialType);
    Optional<List<Playlist>> findByUserId(UUID userId);
}
