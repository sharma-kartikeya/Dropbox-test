package com.dropbox.DropboxTest.models.userdirectory;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@IdClass(UserDirectoryAccessId.class)
@Table(name = "user_directory_meta")
public class UserDirectoryAccess {
    @Id
    @NonNull
    private String userId;

    @Id
    @NonNull
    private String directoryId;

    @NonNull
    private Boolean readAccess;

    @NonNull
    private Boolean writeAccess;

    @NonNull
    private Boolean deleteAccess;

    @NonNull
    private Boolean shareAccess;

    @NonNull
    private Boolean downloadAccess;

    @NonNull
    private Boolean uploadAccess;

    @NonNull
    private Boolean isOwner;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public UserDirectoryAccess(@NonNull String userId, @NonNull String directoryId) {
        this.userId = userId;
        this.directoryId = directoryId;
    }
}