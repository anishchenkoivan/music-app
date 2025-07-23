package com.musicapp.musicservice.util;

import com.musicapp.musicservice.dto.AlbumDto;
import com.musicapp.musicservice.entity.Album;
import com.musicapp.musicservice.entity.Artist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AlbumFactory {
    private final TrackFactory trackFactory;

    @Autowired
    public AlbumFactory(TrackFactory trackFactory) {
        this.trackFactory = trackFactory;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public AlbumDto toAlbumDto(Album album) {
        return new AlbumDto(
                album.getId(),
                album.getTitle(),
                ArtistFactory.toArtistDto(album.getArtist()),
                album.getDuration(),
                album.getLength(),
                album.getReleaseDate(),
                album.getTracks()
                        .stream().map(trackFactory::toTrackDto).toList()
        );
    }

    public Album album(String title, Artist artist) {
        Album album = new Album();
        album.setTitle(title);
        album.setArtist(artist);
        return album;
    }
}
