package com.musicapp.musicservice.repository;

import com.musicapp.musicservice.entity.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PlaylistRepository extends JpaRepository<Playlist, UUID> {
}
