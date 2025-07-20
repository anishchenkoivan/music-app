package com.musicapp.musicservice.repository;

import com.musicapp.musicservice.entity.Album;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AlbumRepository extends JpaRepository<Album, UUID> {
}
