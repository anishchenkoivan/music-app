package com.musicapp.musicservice.repository;

import com.musicapp.musicservice.entity.Album;
import com.musicapp.musicservice.entity.Artist;
import com.musicapp.musicservice.entity.TrackData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AlbumRepository extends JpaRepository<Album, UUID> {
    List<Album> findDistinctByTracks_TrackData(TrackData trackData);
    List<Album> findByArtist(Artist artist);
}
