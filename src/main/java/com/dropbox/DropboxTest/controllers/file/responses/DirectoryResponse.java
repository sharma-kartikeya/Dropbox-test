package com.dropbox.DropboxTest.controllers.file.responses;

import com.dropbox.DropboxTest.models.directory.Directory;
import lombok.Data;
import lombok.NonNull;

@Data
public class DirectoryResponse {
    @NonNull
    private String id;

    @NonNull
    private String name;

    @NonNull
    private Boolean isFile;

//    private String key;

    public DirectoryResponse(@NonNull Directory directory) {
        this.id = directory.getId();
        this.name = directory.getName();
        this.isFile = directory.getIsFile();
//        this.key = directory.getKey();
    }
}
