package com.musicapp.musicservice.repositoy;

import com.musicapp.musicservice.entity.Artist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ArtistRepository extends JpaRepository<Artist, UUID> {
    List<Artist> findByNameContainingIgnoreCase(String name);
}
