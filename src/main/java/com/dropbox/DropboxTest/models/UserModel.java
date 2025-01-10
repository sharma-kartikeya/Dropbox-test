package com.dropbox.DropboxTest.models;

import lombok.Data;
import lombok.NonNull;

import java.util.List;
import java.util.UUID;

@Data
public class UserModel {
    @NonNull
    private String id = UUID.randomUUID().toString();

    @NonNull
    private String name;

    @NonNull
    private String email;

    private String phone;

    private List<String> ownedFiles;

    private List<String> sharedFiles;
}
