package com.dropbox.DropboxTest.controllers.user.responses;

import com.dropbox.DropboxTest.models.user.User;
import lombok.Data;
import lombok.NonNull;

@Data
public class UserResponse {
    @NonNull
    private String name;

    @NonNull
    private String email;

    @NonNull
    private String phone;

    @NonNull
    private String rootDirectoryId;

    public UserResponse(@NonNull User user) {
        this.name = user.getName();
        this.email = user.getEmail();
        this.phone = user.getPhone();
        this.rootDirectoryId = user.getRootDirectoryId();
    }
}
