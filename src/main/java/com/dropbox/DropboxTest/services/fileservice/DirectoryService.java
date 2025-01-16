package com.dropbox.DropboxTest.services.fileservice;

import com.dropbox.DropboxTest.models.Directory;

import java.util.List;

public interface DirectoryService {
    Directory createDirectory(String name, String parentId);

    Directory createFile(String name, String parentId, String key);

    Directory getDirectory(String id);

    boolean checkOwner(String fileId, String ownerId);

    boolean checkRelated(String directoryId, String parentId);

    void moveDirectory(String id, String targetParentId);

    void deleteDirectory(String id, boolean cascade);

    List<Directory> getChildren(String parentId);

    List<Directory> getAllChildren(String id);

    List<Directory> getPathDirectories(String id);

    List<Directory> getAllDescendantsFiles(String id);

    List<Directory> getAllDirectories();
}
