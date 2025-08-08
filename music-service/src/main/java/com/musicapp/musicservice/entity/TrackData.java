package com.musicapp.musicservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "track_data")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class TrackData {
    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue
    private UUID id;
    private String title;
    private long likesCount;
    private long playsCount;
    // Duration in seconds
    private int duration;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "track_artists",
            joinColumns = @JoinColumn(name = "track_id"),
            inverseJoinColumns = @JoinColumn(name = "artist_id")
    )
    private Set<Artist> artists;
    // Underlying source file exists
    private boolean isValid;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        TrackData trackData = (TrackData) o;
        return getId() != null && Objects.equals(getId(), trackData.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
