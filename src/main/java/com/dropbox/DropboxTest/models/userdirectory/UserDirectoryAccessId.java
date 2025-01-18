package com.dropbox.DropboxTest.models.userdirectory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class UserDirectoryAccessId implements Serializable {
    private String userId;
    private String directoryId;

    public UserDirectoryAccessId(String userId, String directoryId) {
        this.userId = userId;
        this.directoryId = directoryId;
    }
}
