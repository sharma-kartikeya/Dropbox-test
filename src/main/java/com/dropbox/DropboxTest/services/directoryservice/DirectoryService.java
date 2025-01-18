package com.dropbox.DropboxTest.services.directoryservice;

import com.dropbox.DropboxTest.models.directory.Directory;
import lombok.NonNull;

import java.util.List;

public interface DirectoryService {
    Directory createRootDirectory();

    Directory createDirectory(@NonNull String name, @NonNull String parentId);

    Directory createFile(@NonNull String name, @NonNull String parentId, @NonNull String key);

    Directory getDirectory(String id);

    void moveDirectory(String id, String targetParentId);

    void deleteDirectoryByCascade(String id);

    void deleteDirectories(List<String> id);

    List<Directory> getChildren(String parentId);

    List<Directory> getPathDirectories(String id);

    List<Directory> getAllDescendantsDirectories(String id);

    List<Directory> getAllDirectories();
}
