package com.dropbox.DropboxTest.models.directory;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "directories")
public class Directory {
    @Id
    @NonNull
    @Column(nullable = false, updatable = false, unique = true)
    private String id;

    @NonNull
    @Column(nullable = false)
    private String name;

    private String parentId;

    private String ownerId;

    @NonNull
    @Column(nullable = false)
    private Boolean isFile = false;

    private String key;

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column()
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Directory() {
        this.id = UUID.randomUUID().toString();
    }
}

