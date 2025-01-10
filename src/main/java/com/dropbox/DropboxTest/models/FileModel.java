package com.dropbox.DropboxTest.models;

import lombok.Data;
import lombok.NonNull;

import java.util.List;
import java.util.UUID;

@Data
public class FileModel {
    @NonNull
    private String id = UUID.randomUUID().toString();

    @NonNull
    private String url;

    @NonNull
    private String name;

    @NonNull
    private String ownerId;

    private String description;

    private List<String> accessUsersId;
}
