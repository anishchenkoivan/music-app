package com.musicapp.userservice.entity;

import com.musicapp.userservice.dto.PublicUserDetailsDto;
import com.musicapp.userservice.dto.UserDetailsDto;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "users")
public class User {
    @Id
    private UUID id;

    private String firstName;
    private String lastName;
    private String username;
    private String bio;
    private String country;
    private String email;
    private String profilePicture;

    public User() {

    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public User(UUID id, String firstName, String lastName, String username, String bio, String country, String email, String profilePicture) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.bio = bio;
        this.country = country;
        this.email = email;
        this.profilePicture = profilePicture;
    }

    public User(String firstName, String lastName, String username, String bio, String country, String email, String profilePicture) {
        this.id = UUID.randomUUID();
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.bio = bio;
        this.country = country;
        this.email = email;
        this.profilePicture = profilePicture;
    }

    public PublicUserDetailsDto getPublicUserDetails() {
        return new PublicUserDetailsDto(
                this.username,
                this.profilePicture,
                this.firstName,
                this.lastName
        );
    }

    public UserDetailsDto toDto() {
        return new UserDetailsDto(
                this.firstName,
                this.lastName,
                this.username,
                this.bio,
                this.country,
                this.email,
                this.profilePicture
        );
    }
}
