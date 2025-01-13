package com.dropbox.DropboxTest.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name = "users")
public class User implements UserDetails {

    @NonNull
    @Id
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    private String id;

    @NonNull
    @Column(nullable = false)
    private String name;

    @NonNull
    @Column(unique = true, nullable = false)
    private String email;

    @NonNull
    @Column(nullable = false)
    private String password;

    private String phone;

    @OneToOne
    private Directory rootDirectory;

    @NonNull
    @Column(nullable = false)
    private UserRole role;

    public User() {
        this.id = UUID.randomUUID().toString();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}
