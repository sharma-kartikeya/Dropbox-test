package com.dropbox.DropboxTest.services.fileservice;

import com.dropbox.DropboxTest.models.FileModel;
import lombok.NonNull;

import java.util.List;

public interface FileService {
    @NonNull
    String getFileUrl(String fileId);

    @NonNull
    FileModel addFile(@NonNull String name, @NonNull String url, @NonNull String ownerId, String description);

    FileModel removeFile(@NonNull String fileId);

    @NonNull
    FileModel updateFile(@NonNull String fileId, String name, String description);

    boolean shareFile(@NonNull String fileId, @NonNull List<String> userIds);

    boolean unShareFile(@NonNull String fileId, @NonNull String userId);

}
