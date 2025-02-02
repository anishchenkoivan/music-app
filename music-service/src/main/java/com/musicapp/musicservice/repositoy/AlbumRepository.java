package com.musicapp.musicservice.repositoy;

import com.musicapp.musicservice.entity.Album;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AlbumRepository extends JpaRepository<Album, UUID> {
    List<Album> findByTitleContainingIgnoreCase(String title);
}
