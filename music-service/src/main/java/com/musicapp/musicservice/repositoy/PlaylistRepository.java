package com.musicapp.musicservice.repositoy;

import com.musicapp.musicservice.entity.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PlaylistRepository extends JpaRepository<Playlist, UUID> {
    List<Playlist> findByOwnerId(UUID ownerId);
    List<Playlist> findByNameContainingIgnoreCase(String name);
}
