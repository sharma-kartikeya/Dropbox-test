package com.dropbox.DropboxTest.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "directories")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Directory {
    @Id
    @NonNull
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    private String id;

    @NonNull
    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    @JsonIgnore
    private Directory parent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    @JsonIgnore
    private User owner;

    @NonNull
    @Column(nullable = false)
    private Boolean isFile = false;

    private String key;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Directory() {
        this.id = UUID.randomUUID().toString();
    }
}

